package com.sybase365.mobiliser.web.dashboard.pages.home.preferences;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.PreferenceBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesNodeViewer;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.model.IPreferencesTreeEntry;
import com.sybase365.mobiliser.web.util.PortalUtils;
import java.util.Set;

/**
 *
 * @author msw
 */
public class PreferencePanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(PreferencePanel.class);

    private IPreferencesNodeViewer backPage;
    private MobiliserBasePage mobBasePage;

    private IPreferencesTreeEntry selectedPreferencesNode;
    private PreferenceBean selectedPreferenceBean;

    private boolean isNew = Boolean.FALSE; 

    private static String DEFAULT_ENCRYPTION = "default";

    public PreferencePanel(String id, IPreferencesTreeEntry prefsNode, 
            PreferenceBean prefBean, IPreferencesNodeViewer backPage,
	    MobiliserBasePage mobBasePage) {
	super(id);
	this.selectedPreferencesNode = prefsNode;
	this.selectedPreferenceBean = prefBean;
	this.backPage = backPage;
	this.mobBasePage = mobBasePage;
	if (prefBean.getKey() == null) {
	    isNew = Boolean.TRUE;
	}
	this.constructPanel();
    }

    Form<?> prefsForm;

    private void constructPanel() {

	prefsForm = new Form("preferencesForm",
		new CompoundPropertyModel<PreferenceBean>(selectedPreferenceBean));

        prefsForm.add(new FeedbackPanel("errorMessages"));

        prefsForm.add(new Label("selectedApplication", 
                    new Model(selectedPreferencesNode.getApplicationName()))
                            .setOutputMarkupId(true)
                            .setVisible(true));

        prefsForm.add(new Label("selectedNodePath", 
                    new Model(selectedPreferencesNode.getPath() + "/"
			    + selectedPreferencesNode.getName()))
                            .setOutputMarkupId(true)
                            .setVisible(true));

       	prefsForm.add(new RequiredTextField<String>("key")
                        .add(new ErrorIndicator()));

       	prefsForm.add(new RequiredTextField<String>("value")
                        .add(new ErrorIndicator()));

       	prefsForm.add(new TextField<String>("type")
                        .add(new ErrorIndicator()));

       	prefsForm.add(new TextField<String>("description")
                        .add(new ErrorIndicator()));

	List<KeyValue<String, String>> implementationTypeOptions = 
		new ArrayList<KeyValue<String, String>>();

	Set<String> encryptSchemesSet = 
		mobBasePage.preferencesEncryptionManager.getSupportedSchemes();

	// if we have a set of encyption schemes from prefs, then use that
	if (PortalUtils.exists(encryptSchemesSet)) {
	    for (String scheme : encryptSchemesSet) {
		// one of these should be DEFAULT, which we'll map to no encryption
		if (scheme.equals(DEFAULT_ENCRYPTION)) {
		    implementationTypeOptions.add(
			    new KeyValue<String,String>(
				    DEFAULT_ENCRYPTION,
				    mobBasePage.getLocalizer()
				    	.getString("preferences.node.preference.noencryption", 
					    mobBasePage)));
		}
		// others add as an encryption option
		else {
		    implementationTypeOptions.add(
			    new KeyValue<String,String>(
				    scheme.trim(),
				    scheme.toUpperCase().trim()));
		}
	    }
	}
	// otherwise just ensure we have an entry for no encryption only
	else {
	    implementationTypeOptions.add(
		    new KeyValue<String,String>(DEFAULT_ENCRYPTION,
			mobBasePage.getLocalizer()
			    .getString("preferences.node.preference.noencryption", 
				    mobBasePage)));
	}

	// ensure default option is for no encryption
	selectedPreferenceBean.setScheme(DEFAULT_ENCRYPTION);

	final Component encryptScheme = 
		new KeyValueDropDownChoice<String, String>( 
			"scheme", implementationTypeOptions)
			    .setRequired(true)
			    .add(new ErrorIndicator())
			    .setOutputMarkupId(true);

	prefsForm.add(encryptScheme);

	final Component encryptPassphrase = new TextField<String>("passphrase")
                        .add(new ErrorIndicator());

	prefsForm.add(encryptPassphrase);

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

		if (! selectedPreferenceBean.getScheme()
			.equals(DEFAULT_ENCRYPTION) &&
			! PortalUtils.exists(
				selectedPreferenceBean.getPassphrase())) {

		    encryptPassphrase.error(this.getLocalizer()
                            .getString("preferences.node.preference.passphrase.required",
			    	mobBasePage));
		}
		else {
		    if (setPreferenceValue()) {
			info(this.getLocalizer()
				.getString("preferences.node.item.add.success", 
				    this));

			if (isNew) {
			    backPage.refreshPreferences();
			}

			setResponsePage((MobiliserBasePage)backPage);
		    }
		    else {
			error(this.getLocalizer()
				.getString("preferences.node.item.add.error", 
				    this));
		    }
                }
            };
        });

	add(prefsForm);
    }
 
    private boolean setPreferenceValue() {

	// if an encryption scheme other than default has been selected
	if (! selectedPreferenceBean.getScheme().equals(DEFAULT_ENCRYPTION)) {

            LOG.debug("Encrypting preference value using {}...",
		    selectedPreferenceBean.getScheme());

	    // get the prefs encryption manager to do the clever bit...
	    String encryptedValue = 
		    mobBasePage.preferencesEncryptionManager
		    	.encryptValue(selectedPreferenceBean.getValue(),
					selectedPreferenceBean.getScheme(),
					selectedPreferenceBean.getPassphrase());

	    // now ditch the passphrase for security reasons
	    selectedPreferenceBean.setScheme(DEFAULT_ENCRYPTION);
	    selectedPreferenceBean.setPassphrase("");

	    // if the encryption process worked
	    if (null != encryptedValue) {
		selectedPreferenceBean.setValue(encryptedValue);
	    }
	    // otherwise output a warning to the log, but continue with 
	    // the store using the unencrypted value instead
	    else {
        	LOG.warn("Couldn't encrypt the value of {} using scheme of {} and passphrase of {}", 
			new Object[] {
			    selectedPreferenceBean.getValue(),
			    selectedPreferenceBean.getScheme(),
			    selectedPreferenceBean.getPassphrase()});
	    }
	}

        LOG.debug("Set preference value: {}:{} -> {}/{}/{}/{}", 
		new Object[] {
		    	selectedPreferencesNode.getApplicationName(),
			selectedPreferencesNode.getFullPath(),
			selectedPreferenceBean.getKey(),
			selectedPreferenceBean.getValue(),
			selectedPreferenceBean.getType(),
			selectedPreferenceBean.getDescription() } );

	try {
            return mobBasePage.setPreferencesValue(
		    	selectedPreferencesNode.getApplicationName(),
			selectedPreferencesNode.getFullPath(),
			selectedPreferenceBean.getKey(),
			selectedPreferenceBean.getValue(),
			selectedPreferenceBean.getType(),
			selectedPreferenceBean.getDescription());
	}
	catch (Exception e) {
            LOG.error("Exception creating preferences application", e);
	}

	return Boolean.FALSE;
    }

}
