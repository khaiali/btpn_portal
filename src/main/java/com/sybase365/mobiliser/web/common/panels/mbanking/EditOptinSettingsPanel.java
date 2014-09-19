package com.sybase365.mobiliser.web.common.panels.mbanking;

/**
 * @author sagraw03
 */

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.OptInSetting;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking.OptinSettingsPage;

public class EditOptinSettingsPanel extends Panel {

    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic mBankingClientLogic;

    AjaxCheckBox termAndConditionsCheck = null;
    CheckBox sendAlertWhenTandCResetCheck = null;
    CheckBox sendAlertWhenAlertDisableCheck = null;
    AjaxCheckBox sendAlertWhenTandCNotAcceptedCheck = null;
    CheckBox tokenRequiredCheck = null;
    private String msg;
    private OptInSetting optInSettings;
    private boolean enableTermAndConditions;
    private boolean sendAlertWhenTandCNotAccepted;
    TextField<String> disableDeviceAlertsDaysTextFeild;

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EditOptinSettingsPanel.class);

    public EditOptinSettingsPanel(String id, MobiliserBasePage mobBasePage,
	    OptInSetting optInSettings, String msg) {
	super(id);
	this.setOptInSettings(optInSettings);
	this.msg = msg;
	this.constructPanel();
    }

    @SuppressWarnings("serial")
    private void constructPanel() {

	final Form<?> form = new Form<EditOptinSettingsPanel>(
		"editOptinSettingsForm",
		new CompoundPropertyModel<EditOptinSettingsPanel>(this));
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new TextField<String>("optInSettings.url").setRequired(true)
		.add(new ErrorIndicator()));
	if (optInSettings.getStatus() == 0) {
	    enableTermAndConditions = true;
	}
	termAndConditionsCheck = new AjaxCheckBox("enableTermAndConditions",
		new PropertyModel<Boolean>(this, "enableTermAndConditions")) {
	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {

		if (enableTermAndConditions) {
		    optInSettings.setStatus(0);
		}
		sendAlertWhenTandCResetCheck
			.setEnabled(enableTermAndConditions);
		sendAlertWhenAlertDisableCheck
			.setEnabled(enableTermAndConditions);
		sendAlertWhenTandCNotAcceptedCheck
			.setEnabled(enableTermAndConditions);
		disableDeviceAlertsDaysTextFeild
			.setEnabled(enableTermAndConditions);
		target.addComponent(sendAlertWhenTandCResetCheck);
		target.addComponent(sendAlertWhenAlertDisableCheck);
		target.addComponent(sendAlertWhenTandCNotAcceptedCheck);
		target.addComponent(disableDeviceAlertsDaysTextFeild);

	    }

	};

	sendAlertWhenTandCResetCheck = new CheckBox("optInSettings.resetNotify");
	sendAlertWhenTandCResetCheck.setOutputMarkupId(true);
	sendAlertWhenAlertDisableCheck = new CheckBox(
		"optInSettings.disableNotify");

	if (optInSettings.getDisableDeviceAlertsDays() != null) {
	    sendAlertWhenTandCNotAccepted = true;
	}
	sendAlertWhenTandCNotAcceptedCheck = new AjaxCheckBox(
		"sendAlertWhenTandCNotAccepted", new PropertyModel<Boolean>(
			this, "sendAlertWhenTandCNotAccepted")) {
	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (sendAlertWhenTandCNotAccepted) {
		    disableDeviceAlertsDaysTextFeild.setRequired(true);
		} else {
		    disableDeviceAlertsDaysTextFeild.setRequired(false);
		}
		target.addComponent(disableDeviceAlertsDaysTextFeild);
	    }

	};

	sendAlertWhenAlertDisableCheck.setOutputMarkupId(true);
	sendAlertWhenTandCResetCheck.setEnabled(enableTermAndConditions);
	sendAlertWhenAlertDisableCheck.setEnabled(enableTermAndConditions);
	sendAlertWhenTandCNotAcceptedCheck.setEnabled(enableTermAndConditions);

	WebMarkupContainer container = new WebMarkupContainer("container");
	tokenRequiredCheck = new CheckBox("optInSettings.tokenRequired");
	container.add(tokenRequiredCheck);
	if (optInSettings.getType() == 1) {
	    container.setVisible(false);
	}

	disableDeviceAlertsDaysTextFeild = new TextField<String>(
		"optInSettings.disableDeviceAlertsDays");
	disableDeviceAlertsDaysTextFeild.add(new ErrorIndicator());
	if (optInSettings.getDisableDeviceAlertsDays() != null) {
	    disableDeviceAlertsDaysTextFeild.setRequired(true);
	}
	form.add(disableDeviceAlertsDaysTextFeild);
	disableDeviceAlertsDaysTextFeild.setOutputMarkupId(true);
	disableDeviceAlertsDaysTextFeild.setEnabled(enableTermAndConditions);
	form.add(new Button("confirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (enableTermAndConditions == false) {
		    optInSettings.setDisableNotify(false);
		    optInSettings.setResetNotify(false);
		    optInSettings.setDisableDeviceAlertsDays(null);
		    optInSettings.setStatus(1);
		}
		if (sendAlertWhenTandCNotAccepted == false) {
		    optInSettings.setDisableDeviceAlertsDays(null);
		}
		if (mBankingClientLogic.updateOptInSettings(optInSettings) != -1) {
		    getSession()
			    .info(
				    optInSettings.getCompanyName()
					    + " "
					    + msg
					    + " "
					    + getLocalizer()
						    .getString(
							    "optin.requirments.action.edit.success",
							    this));
		    setResponsePage(OptinSettingsPage.class);
		}
	    };
	});

	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(OptinSettingsPage.class);
	    }
	});

	form.add(termAndConditionsCheck);
	form.add(sendAlertWhenTandCResetCheck);
	form.add(sendAlertWhenAlertDisableCheck);
	form.add(sendAlertWhenTandCNotAcceptedCheck);
	form.add(container);
	add(form);
    }

    public void setEnableTermAndConditions(boolean enableTermAndConditions) {
	this.enableTermAndConditions = enableTermAndConditions;
    }

    public boolean isEnableTermAndConditions() {
	return enableTermAndConditions;
    }

    public void setSendAlertWhenTandCNotAccepted(
	    boolean sendAlertWhenTandCNotAccepted) {
	this.sendAlertWhenTandCNotAccepted = sendAlertWhenTandCNotAccepted;
    }

    public boolean isSendAlertWhenTandCNotAccepted() {
	return sendAlertWhenTandCNotAccepted;
    }

    public void setOptInSettings(OptInSetting optInSettings) {
	this.optInSettings = optInSettings;
    }

    public OptInSetting getOptInSettings() {
	return optInSettings;
    }

}