package com.sybase365.mobiliser.web.application.pages;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAddressByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAddressByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GenerateTemporaryCredentialRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GenerateTemporaryCredentialResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GetAllPrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GetAllPrivilegesResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ForgotPasswordPage extends BaseLoginPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ForgotPasswordPage.class);

    private String msisdn;
    private String email;

    public ForgotPasswordPage() {
	super();
	LOG.info("Created new ForgotPasswordPage");
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public ForgotPasswordPage(final PageParameters parameters) {
	super(parameters);
	LOG.info("Created new ForgotPasswordPage");
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	PageParameters param = super.getPageParameters();
	if (param != null) {
	    String action = param.getString("action");
	    if ("finish".equals(action)) {
		addForgotPasswordFinishDiv(true);
		addForgotPasswordDiv(false);
	    }
	} else {
	    addForgotPasswordFinishDiv(false);
	    addForgotPasswordDiv(true);
	}
    }

    private void addForgotPasswordFinishDiv(boolean isRender) {

	WebMarkupContainer forgotPasswordFinishDiv = new WebMarkupContainer(
		"forgotPasswordFinishDiv");

	final Class loginPageClass = getComponent(ApplicationLoginPage.class);

	if (isRender) {
	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    final Form<?> form = new Form("forgotPasswordFinishForm",
		    new CompoundPropertyModel<ForgotPasswordPage>(this));
	    form.add(new FeedbackPanel("messages"));
	    form.add(new Button("finish") {
		@Override
		public void onSubmit() {
		    setResponsePage(loginPageClass);
		}
	    });
	    forgotPasswordFinishDiv.add(form);
	    forgotPasswordFinishDiv.setVisible(true);
	} else {
	    forgotPasswordFinishDiv.setVisible(false);
	}
	add(forgotPasswordFinishDiv);
    }

    private void addForgotPasswordDiv(boolean isRender) {

	WebMarkupContainer forgotPasswordDiv = new WebMarkupContainer(
		"forgotPasswordDiv");

	if (isRender) {
	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    final Form<?> form = new Form("forgotPasswordForm",
		    new CompoundPropertyModel<ForgotPasswordPage>(this));
	    form.add(new RequiredTextField<String>("msisdn").setRequired(true)
		    .add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		    .add(Constants.mediumStringValidator)
		    .add(Constants.mediumSimpleAttributeModifier)
		    .add(new ErrorIndicator()));

	    form.add(new RequiredTextField<String>("email").setRequired(true)
		    .add(EmailAddressValidator.getInstance())
		    .add(Constants.mediumStringValidator)
		    .add(Constants.mediumSimpleAttributeModifier)
		    .add(new ErrorIndicator()));

	    final Class loginPageClass = getComponent(ApplicationLoginPage.class);

	    // since we want error messages to be displayed, we have to include
	    // a feedback panel
	    form.add(new FeedbackPanel("errorMessages"));

	    form.add(new Button("cancel") {
		@Override
		public void onSubmit() {
		    setResponsePage(loginPageClass);
		}
	    }.setDefaultFormProcessing(false));

	    form.add(new Button("submit") {
		private static final long serialVersionUID = 1L;

		@Override
		public void onSubmit() {
		    sendNewPassword();
		};
	    });
	    forgotPasswordDiv.add(form);
	    forgotPasswordDiv.setVisible(true);
	} else {
	    forgotPasswordDiv.setVisible(false);
	}
	add(forgotPasswordDiv);
    }

    private void sendNewPassword() {

	LOG.debug("# ForgotPasswordPage.sendNewPassword()");

	Customer customer = null;

	if (PortalUtils.exists(getMsisdn())) {
	    try {
		// getting the customer
		GetCustomerByIdentificationRequest getCustByIdentReq = getNewMobiliserRequest(GetCustomerByIdentificationRequest.class);
		getCustByIdentReq.setIdentification(getMsisdn());
		getCustByIdentReq
			.setIdentificationType(Constants.IDENT_TYPE_MSISDN);
		GetCustomerByIdentificationResponse getCustByIdentRes = wsCustomerClient
			.getCustomerByIdentification(getCustByIdentReq);
		if (!evaluateMobiliserResponse(getCustByIdentRes)) {
		    LOG.warn("Failed to get customer by identification(MSISDN)");
		    return;
		} else if (!PortalUtils.exists(getCustByIdentRes.getCustomer())) {
		    error(getLocalizer().getString(
			    "customer.not.found.msisdn.error", this));
		    return;
		}
		customer = getCustByIdentRes.getCustomer();

		// getting the address of customer
		GetAddressByCustomerRequest getAddByCustReq = getNewMobiliserRequest(GetAddressByCustomerRequest.class);
		getAddByCustReq.setCustomerId(customer.getId());
		getAddByCustReq
			.setAddressType(Constants.ADDRESS_TYPE_POSTAL_DELIVERY);
		GetAddressByCustomerResponse getAddByCustRes = wsAddressClient
			.getAddressByCustomer(getAddByCustReq);
		Address address = getAddByCustRes.getAddress();

		if (!evaluateMobiliserResponse(getAddByCustRes)) {
		    LOG.warn("Failed to get customer's address");
		    return;
		}

		if (!PortalUtils.exists(address)
			|| !PortalUtils.exists(address.getEmail())) {
		    error(getLocalizer().getString(
			    "customer.not.found.email.error", this));
		    return;
		}

		GetAllPrivilegesRequest customerPrivsReq = getNewMobiliserRequest(GetAllPrivilegesRequest.class);
		customerPrivsReq.setCustomerId(customer.getId());
		GetAllPrivilegesResponse customerPrivsRes = wsSecurityClient
			.getPrivileges(customerPrivsReq);

		if (!evaluateMobiliserResponse(customerPrivsRes)) {
		    LOG.warn("Failed to get customer's privileges");
		    return;
		}

		List<String> privList = customerPrivsRes.getPrivileges();

		LOG.debug("The user [{}], has privileges: [{}]",
			customer.getDisplayName(), privList);

		boolean isAuthorized = false;
		if (PortalUtils.exists(privList)) {
		    for (String priv : privList) {
			if (priv.equals(Constants.PASSWORD_RECOVERY_PRIV)) {
			    isAuthorized = true;
			}
		    }
		}

		if (!isAuthorized) {
		    LOG.info(
			    "The user[{}] has not the required Privilege: [{}]",
			    customer.getId(), Constants.PASSWORD_RECOVERY_PRIV);
		    error(getLocalizer().getString(
			    "no.privilege.password.recovery.error", this));
		    return;
		}

		GenerateTemporaryCredentialRequest genTempCredReq = getNewMobiliserRequest(GenerateTemporaryCredentialRequest.class);
		genTempCredReq
			.setCredentialType(Constants.CREDENTIAL_TYPE_PASSWORD);
		genTempCredReq.setCustomerId(customer.getId());
		genTempCredReq
			.setNotificationType(Constants.NOTIFICATION_CHANNEL_EMAIL);
		GenerateTemporaryCredentialResponse gentempCredRes = wsSecurityClient
			.generateTemporaryCredential(genTempCredReq);
		if (!evaluateMobiliserResponse(gentempCredRes)) {
		    LOG.warn(
			    "An error occurred while generating temporary credential for customer [{}]",
			    customer.getId());
		    return;
		} else {
		    gentempCredRes.getUnstructuredData();
		    LOG.info(
			    "Temporary credential generated and sent to the the customer [{}]",
			    customer.getId());
		    getSession().info(
			    getLocalizer().getString(
				    "temp.credential.sent.success", this));
		}
		PageParameters param = new PageParameters();
		param.add("action", "finish");
		setResponsePage(ForgotPasswordPage.class, param);

	    } catch (Exception e) {
		LOG.error("# Error while sending the new password", e);
		error(getLocalizer().getString(
			"customer.temp.password.generate.error", this));
	    }

	}

    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

}
