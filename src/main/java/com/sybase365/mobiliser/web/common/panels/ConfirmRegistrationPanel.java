package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.consumer.pages.signup.ConfirmDataPage;
import com.sybase365.mobiliser.web.consumer.pages.signup.ConfirmOtpPage;
import com.sybase365.mobiliser.web.consumer.pages.signup.SignupCancelPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerRegistration;
import com.sybase365.mobiliser.web.cst.pages.customercare.OtpConfirmPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.StandingDataPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

@SuppressWarnings("all")
public class ConfirmRegistrationPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ConfirmRegistrationPanel.class);

    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic clientLogic;

    protected CustomerBean customer;
    protected MobiliserBasePage mobBasePage;
    protected WebPage backPage;

    public ConfirmRegistrationPanel(String id, CustomerBean customer,
	    MobiliserBasePage mobBasePage, WebPage backPage) {
	super(id);
	this.customer = customer;
	this.mobBasePage = mobBasePage;
	this.backPage = backPage;
	constructPanel();
    }

    protected void constructPanel() {
	Form<?> form = new Form("confirmDataForm",
		new CompoundPropertyModel<ConfirmRegistrationPanel>(this));

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		next();
	    };
	});

	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleCancel();
	    };
	}.setVisible(!mobBasePage.getMobiliserWebSession().getRoles().hasRole(
		Constants.PRIV_CST_LOGIN)));

	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<ConfirmDataPage>(this));
	labels.add(new Label("customer.msisdn"));
	WebMarkupContainer userNameDiv = new WebMarkupContainer("userNameDiv");
	userNameDiv.add(new Label("customer.userName"));
	labels.add(userNameDiv.setVisible(!mobBasePage.getMobiliserWebSession()
		.getRoles().hasRole(Constants.PRIV_CST_LOGIN)));
	labels.add(new Label("customer.address.firstName"));
	labels.add(new Label("customer.address.lastName"));
	WebMarkupContainer titleDiv = new WebMarkupContainer("titleDiv");
	titleDiv.add(new Label("customer.address.title"));
	labels.add(titleDiv.setVisible(!mobBasePage.getMobiliserWebSession()
		.getRoles().hasRole(Constants.PRIV_CST_LOGIN)));

	WebMarkupContainer languageDiv = new WebMarkupContainer("languageDiv");
	languageDiv.add(new Label("customer.language", mobBasePage
		.getDisplayValue(getCustomer().getLanguage(),
			Constants.RESOURCE_BUNDLE_LANGUAGES)));
	labels.add(languageDiv.setVisible(mobBasePage.getMobiliserWebSession()
		.getRoles().hasRole(Constants.PRIV_CST_LOGIN)));

	labels.add(new Label("customer.timeZone", mobBasePage.getDisplayValue(
		getCustomer().getTimeZone(),
		Constants.RESOURCE_BUNDLE_TIMEZONES)));

	labels.add(new Label("customer.birthDateString"));
	labels.add(new Label("customer.address.kvGender", mobBasePage
		.getDisplayValue(customer.getAddress().getKvGender(),
			Constants.RESOURCE_BUNDLE_GENDER)));
	WebMarkupContainer customerTypeConfirmDiv = new WebMarkupContainer(
		"customerTypeConfirmDiv");
	customerTypeConfirmDiv.add(new Label("customer.customerTypeId",
		mobBasePage.getDisplayValue(String.valueOf(customer
			.getCustomerTypeId()),
			Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE)));
	labels.add(customerTypeConfirmDiv.setVisible(mobBasePage
		.getMobiliserWebSession().getRoles().hasRole(
			Constants.PRIV_CST_LOGIN)));
	WebMarkupContainer street1Div = new WebMarkupContainer("street1Div");
	street1Div.add(new Label("customer.address.street1"));
	labels.add(street1Div.setVisible(!mobBasePage.getMobiliserWebSession()
		.getRoles().hasRole(Constants.PRIV_CST_LOGIN)));

	WebMarkupContainer stateDiv = new WebMarkupContainer("stateDiv");
	stateDiv.add(new Label("customer.address.state"));
	labels.add(stateDiv.setVisible(!mobBasePage.getMobiliserWebSession()
		.getRoles().hasRole(Constants.PRIV_CST_LOGIN)));

	WebMarkupContainer houseNoDiv = new WebMarkupContainer("houseNoDiv");
	houseNoDiv.add(new Label("customer.address.houseNo"));
	labels.add(houseNoDiv.setVisible(!mobBasePage.getMobiliserWebSession()
		.getRoles().hasRole(Constants.PRIV_CST_LOGIN)));

	WebMarkupContainer addressRightDiv = new WebMarkupContainer(
		"addressRightDiv");
	addressRightDiv.add(new Label("customer.address.street2"));
	addressRightDiv.add(new Label("customer.address.zip"));
	addressRightDiv.add(new Label("customer.address.city"));
	labels.add(addressRightDiv.setVisible(!mobBasePage
		.getMobiliserWebSession().getRoles().hasRole(
			Constants.PRIV_CST_LOGIN)));

	labels.add(new Label("customer.address.kvCountry", mobBasePage
		.getDisplayValue(customer.getAddress().getKvCountry(),
			Constants.RESOURCE_BUNDLE_COUNTIRES)));

	labels.add(new Label("customer.address.email"));

	labels.add(new Label("customer.kvInfoMode", mobBasePage
		.getDisplayValue(String.valueOf(customer.getKvInfoMode()),
			Constants.RESOURCE_BUNDLE_INFO_MODE)));

	WebMarkupContainer networkProviderDiv = new WebMarkupContainer(
		"networkProviderDiv");

	if (customer.getNetworkProvider() != null) {
	    networkProviderDiv.add(new Label("customer.networkProvider",
		    mobBasePage.getDisplayValue(customer.getNetworkProvider(),
			    Constants.RESOURCE_BUNDLE_NETWORK_PROVIDERS)));
	} else {
	    networkProviderDiv.add(new Label("customer.networkProvider", ""));
	}

	// network provider confirmation only for mbanking customer types
	if (customer.getCustomerTypeId() != null
		&& customer.getCustomerTypeId().intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
	    networkProviderDiv.setVisible(true);
	} else {
	    networkProviderDiv.setVisible(false);
	}

	labels.add(networkProviderDiv);

	labels.add(new Label("customer.securityQuestion", mobBasePage
		.getDisplayValue(customer.getSecurityQuestion(),
			Constants.RESOURCE_BUNDLE_SEC_QUESTIONS)));

	labels.add(new Label("customer.SecQuesAns"));
	form.add(labels);

	add(new FeedbackPanel("errorMessages"));
	add(form);
    }

    protected void handleBack() {
	LOG.debug("#ConfirmDataPage.handleBack()");
	setResponsePage(backPage);
    }

    protected void handleCancel() {
	LOG.debug("#ConfirmDataPage.handleCancel()");
	mobBasePage.cleanupSession();
	setResponsePage(SignupCancelPage.class);
    }

    protected void next() {
	LOG.debug("#ConfirmDataPage.next()");

	if (mobBasePage.getMobiliserWebSession().getRoles().hasRole(
		Constants.PRIV_CST_LOGIN)) {
	    if (mobBasePage.getConfiguration().getOtpConfirmationRegistration()) {
		customer.setBlackListReason(5);
		saveCustomer();
	    } else {
		saveCustomer();
		return;
	    }

	}

	String otp;
	try {
	    String generatedOtp = mobBasePage.generateOTP();

	    if (generatedOtp == null)
		return;
	    List<KeyValue<String, String>> paramsList = new ArrayList<KeyValue<String, String>>();
	    paramsList.add(new KeyValue<String, String>("otp", generatedOtp));

	    if (mobBasePage.sendOTP(customer.getMsisdn(), mobBasePage
		    .getConfiguration().getSmsOtpTemplate(), "sms", customer
		    .getId(), paramsList)) {
		if (mobBasePage.getMobiliserWebSession().getRoles().hasRole(
			Constants.PRIV_CST_LOGIN)) {
		    setResponsePage(new OtpConfirmPage(customer, generatedOtp));
		} else {
		    setResponsePage(new ConfirmOtpPage(customer, generatedOtp));
		}
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while creating new customer", e);
	    error(getLocalizer().getString("register.customer.error", this));
	}

    }

    protected void saveCustomer() {
	LOG.debug("#ConfirmRegistrationPanel.next()");

	try {
	    if (!getBasePage().uniqueIdentificationCheck(
		    (getCustomer().getMsisdn()), Constants.IDENT_TYPE_MSISDN,
		    getCustomer().getId())) {
		return;
	    }
	    getBasePage().setCreateStatus(false);
	    customer.setRiskCategoryId(getBasePage().getConfiguration()
		    .getDefaultRiskCatForNewCustomer());
	    customer.setKvCountry(customer.getAddress().getKvCountry());
	    if (customer.getCustomerTypeId() != null
		    && customer.getCustomerTypeId().intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
		clientLogic.createFullCustomer(customer, null, getBasePage());
	    } else {

		customer = getBasePage().createFullCustomer(customer, null);
	    }
	    // if (getBasePage().isCreateStatus()) {
	    // getBasePage().createCustomerMsisdn(customer);
	    // }
	    // if (getBasePage().isCreateStatus()) {
	    // customer = getBasePage().createCustomerIdentification(customer);
	    // }
	    // if (getBasePage().isCreateStatus()) {
	    // getBasePage().createCustomerAddress(customer);
	    // }

	    if (getBasePage().isCreateStatus()) {
		getBasePage().createSvaWalletWithPI(customer);
	    }

	    if (getBasePage().isCreateStatus()) {
		getBasePage().getMobiliserWebSession().setCustomer(customer);
		customer.setMsisdn(new PhoneNumber(customer.getMsisdn())
			.getInternationalFormat());
		customer.setDisplayName(mobBasePage.createDisplayName(customer
			.getAddress().getFirstName(), customer.getAddress()
			.getLastName()));

		setResponsePage(new StandingDataPage(getCustomer()));
	    }
	} catch (Exception e) {
	    LOG.error("# An error has occurred for register customer ", e);
	    error(getLocalizer().getString("register.customer.error", this));
	    getBasePage().setCreateStatus(false);
	} finally {
	    if (!getBasePage().isCreateStatus()) {

		if (PortalUtils.exists(customer)
			&& customer.isPendingApproval()) {
		    getBasePage().getMobiliserWebSession().info(
			    getLocalizer().getString("pendingApproval.msg",
				    this));
		    if (backPage instanceof CustomerRegistration)
			setResponsePage(CustomerRegistration.class);

		} else {
		    try {
			if (PortalUtils.exists(customer.getId()))
			    getBasePage().deleteCustomer(customer.getId());

			customer.setId(null);
			customer.getAddress().setId(null);
		    } catch (Exception e) {
			LOG
				.error(
					"# An error occurred whilte inactivating agent",
					e);
		    }
		}
	    }
	}
    }

    protected MobiliserBasePage getBasePage() {
	return mobBasePage;
    }

    public CustomerBean getCustomer() {
	return customer;
    }

}
