package com.sybase365.mobiliser.web.common.panels;

import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.security.ChangeCredentialRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.ChangeCredentialResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.CheckCredentialStrengthRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.CheckCredentialStrengthResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.ChangePasswordPage;
import com.sybase365.mobiliser.web.util.Constants;

public class ChangePasswordPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChangePasswordPanel.class);

    private long customerId;
    private MobiliserBasePage mobBasePage;
    private Class responsePageClazz;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String customerUserName;

    /**
     * Contructor where Customer Id is passed
     * 
     * @param id
     * @param customerId
     * @param mobBasePage
     * @param responsePageClazz
     */
    public ChangePasswordPanel(String id, long customerId,
	    MobiliserBasePage mobBasePage,
	    Class<? extends WebPage> responsePageClazz) {

	super(id);

	this.customerId = customerId;
	this.mobBasePage = mobBasePage;
	this.responsePageClazz = responsePageClazz;

	LOG.info("Created new ChangePasswordPanel");

	constructPanel();
    }

    /**
     * Contructor where Customer object is passed
     * 
     * @param id
     * @param customer
     * @param mobBasePage
     * @param responsePageClazz
     */
    public ChangePasswordPanel(String id, Customer customer,
	    MobiliserBasePage mobBasePage,
	    Class<? extends WebPage> responsePageClazz) {

	super(id);
	this.customerUserName = customer.getUsername();
	this.customerId = customer.getCustomerId();
	this.mobBasePage = mobBasePage;
	this.responsePageClazz = responsePageClazz;

	LOG.info("Created new ChangePasswordPanel");
	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

	Form<?> form = new Form("changepasswordform",
		new CompoundPropertyModel<ChangePasswordPage>(this));

	form.add(new PasswordTextField("oldPassword").setRequired(true)
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(new ErrorIndicator()));

	PasswordTextField pswd = new PasswordTextField("newPassword");

	// NOTE: you must ensure that there is a customised validation error
	// message supplied for password fields - otherwise the default message
	// will put the entered value in cleartext into the message.
	// Use the validator message key;
	// [myformfieldname>].StringValidator.range
	pswd.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(new ErrorIndicator());

	PasswordTextField cnfrmPswd = new PasswordTextField("confirmPassword");
	// don't need a required validator or a length between validator
	// for confirm password, because there is an equal check on the
	// new password field, which does have a required and length between
	// validator
	cnfrmPswd.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator());

	form.add(pswd);

	form.add(cnfrmPswd);

	form.add(new EqualPasswordInputValidator(pswd, cnfrmPswd));

	form.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));

	add(form);
    }

    private void handleSubmit() {

	try {
	    CheckCredentialStrengthRequest credentialStrengthRequest = mobBasePage
		    .getNewMobiliserRequest(CheckCredentialStrengthRequest.class);

	    credentialStrengthRequest.setCredential(getNewPassword());
	    credentialStrengthRequest
		    .setCredentialType(Constants.CREDENTIAL_TYPE_PASSWORD);
	    credentialStrengthRequest
		    .setCustomerTypeId(Constants.CONSUMER_IDTYPE);

	    CheckCredentialStrengthResponse strengthResponse = mobBasePage.wsSecurityClient
		    .checkCredentialStrength(credentialStrengthRequest);

	    if (!mobBasePage.evaluateMobiliserResponse(strengthResponse)) {
		return;
	    }
	} catch (Exception e) {
	    LOG.error("# Failed to check credential strength", e);
	    error(getLocalizer().getString("change.password.error", this));
	}

	try {
	    ChangeCredentialRequest chCreReq = mobBasePage
		    .getNewMobiliserRequest(ChangeCredentialRequest.class);

	    chCreReq.setOldCredential(getOldPassword());
	    chCreReq.setNewCredential(getNewPassword());
	    chCreReq.setCustomerId(customerId);
	    chCreReq.setCredentialType(Constants.CREDENTIAL_TYPE_PASSWORD);

	    ChangeCredentialResponse chCreResp = mobBasePage.wsSecurityClient
		    .changeCredential(chCreReq);

	    if (!mobBasePage.evaluateMobiliserResponse(chCreResp)) {
		return;
	    } else {
		// Replacing the session with new credentials
		AuthenticatedWebSession session = AuthenticatedWebSession.get();
		if (session.signIn(customerUserName, getNewPassword())) {
		    // replacing the session used to login with a new one to
		    // avoid sessionFixation exploit
		    session.replaceSession();
		}

		getSession().info(
			getLocalizer().getString("change.password.success",
				this));
		LOG.debug("# Successfully changed password");
		setResponsePage(responsePageClazz);
	    }

	} catch (Exception e) {
	    LOG.error("# Failed to update password", e);
	    error(getLocalizer().getString("change.password.error", this));
	}
    }

    public String getOldPassword() {
	return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
	this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
	return newPassword;
    }

    public void setNewPassword(String newPassword) {
	this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
	return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
    }

}
