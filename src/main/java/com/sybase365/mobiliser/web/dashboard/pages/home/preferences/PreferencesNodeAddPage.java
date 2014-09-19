package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.PreferencesApplication;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.PreferenceBean;
import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNavigator;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_PREFS)
public class PreferencesNodeAddPage extends HomeMenuGroup {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = 
            org.slf4j.LoggerFactory.getLogger(PreferencesNodeAddPage.class);

    private IPreferencesNavigator backPage;

    private List<String> appNames;

    private String selectedApplication;
    private String nodePath;

    public PreferencesNodeAddPage(IPreferencesNavigator backPage) {
        super();
        this.backPage = backPage;
        initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	Form prefsForm = new Form("preferencesForm",
		new CompoundPropertyModel<PreferenceBean>(this));

        prefsForm.add(new FeedbackPanel("errorMessages"));

	try {
    	    List<PreferencesApplication> prefsApps = 
		    this.getAllPreferencesApplicationNames();

	    appNames = new ArrayList<String>();

	    for (PreferencesApplication prefsApp : prefsApps) {
		appNames.add(prefsApp.getApplicationName());
	    }

	}
	catch (Exception e) {
	    LOG.error("Got exception loading preferences applications", e);
	}

        prefsForm.add(new DropDownChoice<String>("selectedApplication", appNames)
		.setRequired(true)
		.add(new ErrorIndicator()));

       	prefsForm.add(new RequiredTextField<String>("nodePath")
                        .add(new ErrorIndicator()));

        prefsForm.add(new Button("cancelButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage((MobiliserBasePage)backPage);
            };
        }.setDefaultFormProcessing(false));

        prefsForm.add(new Button("saveButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
		if (addPreferenceNode()) {
		    backPage.refresh();
		    setResponsePage((MobiliserBasePage)backPage);
		    info(getLocalizer()
			    .getString("preferences.node.add.success",
			    this));
		} else {
		    error(getLocalizer()
			    .getString("preferences.node.add.error",
			    this));
		}
            };
        });

	add(prefsForm);

    }

    public String getSelectedApplication() {
        return this.selectedApplication;
    }

    public void setSelectedApplication(String value) {
        this.selectedApplication = value;
    }

    public String getNodePath() {
        return this.nodePath;
    }

    public void setNodePath(String value) {
        this.nodePath = value;
    }

    private boolean addPreferenceNode() {

        LOG.debug("Adding preference node: {}/{}", selectedApplication, 
		nodePath);

	try {
            return this.setPreferencesValue(selectedApplication, nodePath, 
		    "node", "", "", "");
	}
	catch (Exception e) {
            LOG.error("Exception creating preferences application", e);
	}

	return Boolean.FALSE;
    }

    @Override
    protected Class getActiveMenu() {
	return PreferencesPage.class;
    }
 
}