package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.checkout.pages.GetMsisdnPage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.consumer.pages.signup.SignupCancelPage;
import com.sybase365.mobiliser.web.consumer.pages.signup.SignupFinishedPage;
import com.sybase365.mobiliser.web.consumer.pages.signup.SignupStartPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.StandingDataPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@SuppressWarnings("all")
public class ConfirmOtpPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ConfirmOtpPanel.class);

    protected CustomerBean customer;
    protected MobiliserBasePage mobBasePage;
    protected WebPage backPage;
    protected int otpSentCount = 1;
    protected int otpEnteredCount = 0;
    protected String otp;
    protected String sentOtp;
    protected Button btnResend;
    protected Button btnContinue;
    protected Label text2;
    protected Label text3;

    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic clientLogic;

    public ConfirmOtpPanel(String id, CustomerBean customer,
	    MobiliserBasePage mobBasePage, String sentOtp) {
	super(id);
	this.customer = customer;
	this.mobBasePage = mobBasePage;
	this.backPage = backPage;
	this.sentOtp = sentOtp;
	constructPanel();
    }

    protected void constructPanel() {
	add(new FeedbackPanel("errorMessages"));
	Form<?> form = new Form("confirmOtpForm",
		new CompoundPropertyModel<ConfirmOtpPanel>(this));
	final RequiredTextField otp = new RequiredTextField<String>("otp");
	otp.add(Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier);
	btnContinue = new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		next(otp);
	    };
	};
	btnResend = new Button("resend") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		otp.clearInput();
		resend();
	    };
	}.setDefaultFormProcessing(false);
	form.add(otp.setRequired(true)).add(new ErrorIndicator());

	// adding the submit buttons
	// the back button should not validate the form's input
	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleCancel();
	    };
	}.setDefaultFormProcessing(false).setVisible(
		!mobBasePage.getMobiliserWebSession().getRoles().hasRole(
			Constants.PRIV_CST_LOGIN)));

	form.add(btnContinue);

	form.add(btnResend);

	text2 = new Label("text2", getLocalizer().getString(
		"authenticate.text2", this));
	text3 = new Label("text3", getLocalizer().getString(
		"authenticate.text3", this));
	form.add(text2);
	form.add(text3.setVisible(!mobBasePage.getMobiliserWebSession()
		.getRoles().hasRole(Constants.PRIV_CST_LOGIN)));

	add(form);
    }

    protected void handleCancel() {
	LOG.debug("#ConfirmOtpPage.handleBack()");
	mobBasePage.cleanupSession();
	setResponsePage(new SignupStartPage());
    }

    protected void next(RequiredTextField otp) {
	LOG.debug("#ConfirmOtpPage.next()");
	try {
	    mobBasePage.setCreateStatus(true);
	    if (otpEnteredCount >= 3) {
		LOG.debug("# OTP enetr counter increased the limited count");
		if (mobBasePage.getMobiliserWebSession().getRoles().hasRole(
			Constants.PRIV_CST_LOGIN)) {
		    error(getLocalizer().getString(
			    "customer.create.failed.otp.retry", this));
		    if (mobBasePage.deleteCustomer(this.customer.getId())) {
			LOG
				.debug("maximum limit for entering wrong OTP has been increased.Customer["
					+ customer.getId()
					+ "] has been deactivated");
			otp.add(new SimpleAttributeModifier("disabled",
				"disabled"));
			btnContinue.setVisible(false);
			btnResend.setVisible(false);
			text2.setVisible(false);
			text3.setVisible(false);
			return;
		    }
		} else {
		    getSession().error(
			    getLocalizer().getString(
				    "otp.input.count.exceeded", this));
		    setResponsePage(SignupCancelPage.class);
		}

		return;
	    }
	    if (!sentOtp.equals(getOtp())) {
		this.otpEnteredCount++;
		LOG.debug("# OTP mismatch");
		error("'"
			+ getOtp()
			+ "'"
			+ getLocalizer().getString("otp.verification.error",
				this));
		setOtp(null);
		return;
	    }

	    LOG.debug("# OTP Verified");

	    if (mobBasePage.getMobiliserWebSession().getRoles().hasRole(
		    Constants.PRIV_CST_LOGIN)) {
		customer.setBlackListReason(0);
		if (mobBasePage.updateCustomerDetail(customer)) {
		    setResponsePage(new StandingDataPage(customer));
		}
		return;
	    }

	    customer.setKvCountry(customer.getAddress().getKvCountry());
	    mobBasePage.setCreateStatus(false);
	    customer.setRiskCategoryId(mobBasePage.getConfiguration()
		    .getDefaultRiskCatForNewCustomer());
	    if (customer.getCustomerTypeId() != null
		    && customer.getCustomerTypeId().intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
		clientLogic.createFullCustomer(customer, null, mobBasePage);
	    } else {

		customer = mobBasePage.createFullCustomer(customer, null);
	    }

	    if (PortalUtils.exists(customer)) {
		if (customer.isPendingApproval())
		    setResponsePage(new SignupFinishedPage(false));
		else
		    LOG.debug("# Successfully created Customer data");
	    }

	    // if (mobBasePage.isCreateStatus()) {
	    // mobBasePage.createCustomerIdentification(customer);
	    // }
	    //
	    // if (mobBasePage.isCreateStatus()) {
	    // mobBasePage.createCustomerMsisdn(customer);
	    // }
	    //
	    // if (mobBasePage.isCreateStatus()) {
	    // mobBasePage.createCustomerUserName(customer);
	    // }
	    //
	    // if (mobBasePage.isCreateStatus()) {
	    // mobBasePage.createCustomerCredential(customer,
	    // Constants.CREDENTIAL_TYPE_PASSWORD);
	    // }
	    //
	    // if (mobBasePage.isCreateStatus()) {
	    // mobBasePage.createCustomerCredential(customer,
	    // Constants.CREDENTIAL_TYPE_PIN);
	    // }
	    //
	    // if (mobBasePage.isCreateStatus()) {
	    // mobBasePage.createCustomerAddress(customer);
	    // }

	    if (mobBasePage.isCreateStatus()) {
		mobBasePage.createSvaWalletWithPI(customer);
	    }

	    if (!mobBasePage.isCreateStatus()) {
		LOG.error("Failed to register a new customer");
		if (PortalUtils.exists(customer))
		    error(getLocalizer().getString("register.customer.error",
			    this));
	    } else {
		LOG.info("successfully registered a new customer");
		if (mobBasePage.getMobiliserWebSession().isContinueToCheckout()) {
		    setResponsePage(new GetMsisdnPage(customer.getMsisdn()));
		    mobBasePage.getMobiliserWebSession().setContinueToCheckout(
			    false);
		} else {
		    setResponsePage(new SignupFinishedPage(true));
		}
	    }

	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while registering a new customer",
			    e);
	    error(getLocalizer().getString("register.customer.error", this));
	    mobBasePage.setCreateStatus(false);
	    if (PortalUtils.exists(customer)
		    && PortalUtils.exists(customer.getId())) {
		try {
		    mobBasePage.deleteCustomer(this.customer.getId());
		} catch (Exception e1) {
		    LOG.error("# An error occured while deleting the customer["
			    + customer.getId() + "]", e1);

		}
	    }
	} finally {
	    if (!mobBasePage.isCreateStatus()) {
		if (PortalUtils.exists(customer)) {

		    if (PortalUtils.exists(customer.getId())) {
			try {
			    mobBasePage.deleteCustomer(this.customer.getId());
			} catch (Exception e1) {
			    LOG.error(
				    "# An error occured while deleting the customer["
					    + customer.getId() + "]", e1);

			}
		    }
		    customer.setId(null);
		    customer.getAddress().setId(null);

		}
	    }
	}
    }

    protected void resend() {
	LOG.debug("#ConfirmOtpPage.resend()");
	try {
	    if (this.otpSentCount > 2) {
		error(getLocalizer().getString("resend.otp.limit.exceeded",
			this));
	    } else {
		this.sentOtp = mobBasePage.generateOTP();
		if (this.sentOtp == null)
		    return;
		List<KeyValue<String, String>> paramsList = new ArrayList<KeyValue<String, String>>();
		paramsList
			.add(new KeyValue<String, String>("otp", this.sentOtp));

		// this.sentOtp = sendOTP(customer.getMsisdn());
		if (mobBasePage.sendOTP(customer.getMsisdn(), mobBasePage
			.getConfiguration().getSmsOtpTemplate(), "sms",
			customer.getId(), paramsList)) {
		    info(getLocalizer().getString("otp.resend.success", this));
		    this.otpSentCount++;
		    this.otpEnteredCount = 0;
		}
	    }
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while sending OTP for changing msisdn for customer["
			    + customer.getId() + "[", e);
	    error(getLocalizer().getString("otp.send.error", this));
	}
    }

    public String getOtp() {
	return otp;
    }

    public void setOtp(String otp) {
	this.otp = otp;
    }

}
