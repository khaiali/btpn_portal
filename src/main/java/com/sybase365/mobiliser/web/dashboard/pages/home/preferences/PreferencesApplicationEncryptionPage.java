package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.home.HomeMenuGroup;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeEntry;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNodeViewer;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_PREFS)
public class PreferencesApplicationEncryptionPage extends HomeMenuGroup {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = 
            org.slf4j.LoggerFactory.getLogger(PreferencesApplicationEncryptionPage.class);

    private IPreferencesNodeViewer backPage;

    private IPreferencesTreeEntry selectedPreferencesNode;
    private String encryptionPassword;
    private String encryptionType;

    public PreferencesApplicationEncryptionPage(IPreferencesTreeEntry prefsNode, IPreferencesNodeViewer backPage) {
        super();
        this.selectedPreferencesNode = prefsNode;
        this.backPage = backPage;
        initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	Form prefsForm = new Form("preferencesForm",
		new CompoundPropertyModel<PreferencesApplicationEncryptionPage>(this));

        prefsForm.add(new FeedbackPanel("errorMessages"));

       	prefsForm.add(new Label("name", new Model(selectedPreferencesNode.getApplicationName())));

       	prefsForm.add(new RequiredTextField<String>("encryptionPassword")
                        .add(new ErrorIndicator()));

       	prefsForm.add(new RequiredTextField<String>("encryptionType")
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

                if (setPassword()) {

                    backPage.refreshPreferences();

                    setResponsePage((MobiliserBasePage)backPage);

                    info(getLocalizer()
                            .getString("preferences.application.encryption.success",
                            this));
                }
                else {
                    error(getLocalizer()
                            .getString("preferences.application.encryption.error",
                            this));
                }
            };
        });

	add(prefsForm);
    }

    private boolean setPassword() {
    
        List<String> result = 
                this.invokeRemoteManagedOperation(
                        "com.sybase365.mobiliser.util.prefs:product=Preferences,category=Tree,instance="
                                +selectedPreferencesNode.getApplicationName(),
                        "setEncryptionPassword",
                                new String[] { this.encryptionPassword,
                                        this.encryptionType});

        if (result != null) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public String getEncryptionPassword() {
        return this.encryptionPassword;
    }

    public void setEncrpytionPassword(String value) {
        this.encryptionPassword = value;
    }

    public String getEncryptionType() {
        return this.encryptionType;
    }

    public void setEncrpytionType(String value) {
        this.encryptionType = value;
    }

    @Override
    protected Class getActiveMenu() {
	return PreferencesPage.class;
    }
 
}