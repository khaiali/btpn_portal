package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.PreferenceBean;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNavigator;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeApplicationEntry;
import org.apache.wicket.behavior.SimpleAttributeModifier;

/**
 *
 * @author msw
 */
public class PreferencesApplicationPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(PreferencesApplicationPanel.class);

    private IPreferencesNavigator backPage;
    private MobiliserBasePage mobBasePage;

    private boolean isNew;

    private String name;
    private String description;
    private String readPrivilege;
    private String writePrivilege;

    public PreferencesApplicationPanel(String id, IPreferencesTreeApplicationEntry appNode, 
            IPreferencesNavigator backPage, MobiliserBasePage mobBasePage) {

	super(id);

	if (appNode != null) {
	    this.isNew = Boolean.FALSE;
	    this.name = appNode.getName();
	    this.readPrivilege = appNode.getReadPrivilege();
	    this.writePrivilege = appNode.getWritePrivilege();
	    this.description = appNode.getDescription();
	}
	else {
	    this.isNew = Boolean.TRUE;
	    this.name = "";
	    this.readPrivilege = "";
	    this.writePrivilege = "";
	    this.description = "";
	}

	this.backPage = backPage;
	this.mobBasePage = mobBasePage;

	this.constructPanel();
    }

    Form<?> prefsForm;

    private void constructPanel() {

	Form prefsForm = new Form("preferencesForm",
		new CompoundPropertyModel<PreferenceBean>(this));

        prefsForm.add(new FeedbackPanel("errorMessages"));

       	prefsForm.add(new RequiredTextField<String>("name")
                        .add(new ErrorIndicator()));

       	prefsForm.add(new TextField<String>("description")
                        .add(new ErrorIndicator()));

       	prefsForm.add(new TextField<String>("readPrivilege")
                        .add(new ErrorIndicator()));

       	prefsForm.add(new TextField<String>("writePrivilege")
                        .add(new ErrorIndicator()));

        prefsForm.add(new Button("cancelButton") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage((MobiliserBasePage)backPage);
            };

        }.setDefaultFormProcessing(false));

        prefsForm.add(new Button("removeButton") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
		if (deleteApplication()) {
		    backPage.refresh();
		    setResponsePage((MobiliserBasePage)backPage);
		    info(getLocalizer()
			    .getString("preferences.application.remove.success",
			    this));
		} else {
		    error(getLocalizer()
			    .getString("preferences.application.remove.error",
			    this));
		}
            };
        }.setVisible(!isNew).add(new SimpleAttributeModifier("onclick",
		"return confirm('"
			+ getLocalizer().getString(
				"preferences.application.remove.confirm", this)
			+ "');")));

        prefsForm.add(new Button("saveButton") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {

		if (isNew) {
		    if (addApplication()) {
			backPage.refresh();
			setResponsePage((MobiliserBasePage)backPage);
			info(getLocalizer()
				.getString("preferences.application.add.success",
				this));
		    } else {
			error(getLocalizer()
				.getString("preferences.application.add.error",
				this));
		    }
		} else {
		    if (updateApplication()) {
			backPage.refresh();
			setResponsePage((MobiliserBasePage)backPage);
			info(getLocalizer()
				.getString("preferences.application.update.success",
				this));
		    } else {
			error(getLocalizer()
				.getString("preferences.application.update.error",
				this));
		    }
		}
            };
        });

	add(prefsForm);
    }

    private boolean addApplication() {

        LOG.debug("Adding preferences application: {}", name);

	try {
            return mobBasePage.createPreferencesApplication(name, readPrivilege, 
		    writePrivilege, description);
	}
	catch (Exception e) {
            LOG.error("Exception creating preferences application", e);
	}

	return Boolean.FALSE;
    }

    private boolean updateApplication() {

        LOG.debug("Update preferences application: {}", name);

	try {
            return mobBasePage.updatePreferencesApplication(name, readPrivilege, 
		    writePrivilege, description);
	}
	catch (Exception e) {
            LOG.error("Exception updating preferences application", e);
	}

	return Boolean.FALSE;
    }

    private boolean deleteApplication() {

        LOG.debug("Delete preferences application: {}", name);

	try {
            return mobBasePage.deletePreferencesApplication(name);
	}
	catch (Exception e) {
            LOG.error("Exception deleting preferences application", e);
	}

	return Boolean.FALSE;
    }

}
