package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.consumer.pages.signup.CaptchaImage;
import com.sybase365.mobiliser.web.consumer.pages.signup.CaptchaValidator;
import com.sybase365.mobiliser.web.consumer.pages.signup.ConfirmDataPage;
import com.sybase365.mobiliser.web.consumer.pages.signup.SignupCancelPage;
import com.sybase365.mobiliser.web.consumer.pages.signup.SignupStartPage;
import com.sybase365.mobiliser.web.consumer.pages.signup.TermsAndConditions_en;
import com.sybase365.mobiliser.web.cst.pages.customercare.StandingDataPage;
import com.sybase365.mobiliser.web.distributor.pages.customerservices.CustomerDetailsPage;
import com.sybase365.mobiliser.web.distributor.pages.customerservices.RegisterCustomerPage;
import com.sybase365.mobiliser.web.distributor.pages.selfcare.SelfCareHomePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class RegisterCustomerPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(RegisterCustomerPanel.class);

    protected MobiliserBasePage basePage;
    protected CustomerBean customer;
    protected String pinConfirmed;
    protected String passwordConfirmed;
    protected Boolean isAgree;
    protected String captcha;
    protected AttachmentsPanel attachmentsPanel;
    protected NonCachingImage captchaImage;
    protected RequiredTextField captchTextField;

    protected String challengeId;
    @SpringBean(name = "systemAuthMBankingClientLogic")
    protected MBankingClientLogic clientLogic;

    public RegisterCustomerPanel(String id, MobiliserBasePage basePage,
	    CustomerBean customer) {
	this(id, basePage, null, customer);
    }

    public RegisterCustomerPanel(String id, MobiliserBasePage basePage,
	    AttachmentsPanel attachmentsPanel, CustomerBean customer) {
	super(id);
	this.basePage = basePage;
	this.attachmentsPanel = attachmentsPanel;
	this.customer = customer;

	LOG.debug("New customer type -> {}", customer.getCustomerTypeId());

	final String chooseDtTxt = this.basePage.getLocalizer().getString(
		"datepicker.chooseDate", basePage);

	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {
		captcha = "";

		// localize the jquery datepicker based on users locale setting
		// locale specific js includes for datepicker are available at
		// http://jquery-ui.googlecode.com/svn/trunk/ui/i18n/
		String localeLang = getLocale().getLanguage().toLowerCase();

		LOG.debug("Using DatePicker for locale language: {}",
			localeLang);

		if (PortalUtils.exists(localeLang)) {
		    response
			    .renderJavascriptReference("scripts/jquery/i18n/jquery.ui.datepicker-"
				    + localeLang + ".js");
		}

		response.renderJavascript("\n"
			+ "jQuery(document).ready(function($) { \n"
			+ "  $('#birthDate').datepicker( { \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'changeMonth' : true, \n"
			+ "	'changeYear' : true, \n"
			+ "     'yearRange' : '-100:+0', \n"
			+ "	'showOn': 'both', \n" + "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	constructPanel();
    }

    protected void constructPanel() {
	@SuppressWarnings("rawtypes")
	final Form<?> form = new Form("registerCustomerForm",
		new CompoundPropertyModel<RegisterCustomerPanel>(this));
	form.add(new FeedbackPanel("errorMessages").setVisible(!PortalUtils
		.exists(attachmentsPanel)));
	form.add(new RequiredTextField<String>("customer.address.firstName")
		.setRequired(true).add(
			new PatternValidator(Constants.REGEX_FIRSTNAME)).add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier).add(
			new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"customer.address.kvGender", Integer.class,
		Constants.RESOURCE_BUNDLE_GENDER, this, true, true)
		.setRequired(true).add(new ErrorIndicator()));

	form.add(new DateTextField("birthDateField", new PropertyModel<Date>(
		this, "customer.birthDateString"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)).setRequired(true)
		.add(new ErrorIndicator()));

	WebMarkupContainer customerTypeDiv = new WebMarkupContainer(
		"customerTypeDiv");
	customerTypeDiv.setVisible(true);
	List<Integer> typeList = null;
	try {
	    typeList = basePage.getKeysFromPreferences(
		    "cstRegisterCustomerTypes", Integer.class);
	} catch (Exception ex) {
	    LOG
		    .warn(
			    "Error in loading cutomer type list from preferences.",
			    ex);
	}
	LocalizableLookupDropDownChoice<Integer> customertypes = (LocalizableLookupDropDownChoice<Integer>) new LocalizableLookupDropDownChoice<Integer>(
		"customer.customerTypeId", Integer.class,
		Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE, this, false, true,
		typeList).setNullValid(false).setRequired(true).add(
		new ErrorIndicator());
	customerTypeDiv.add(customertypes);

	form.add(customerTypeDiv);

	form.add(new RequiredTextField<String>("customer.address.lastName")
		.setRequired(true).add(
			new PatternValidator(Constants.REGEX_FIRSTNAME)).add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier).add(
			new ErrorIndicator()));

	WebMarkupContainer titleDiv = new WebMarkupContainer("titleDiv");
	titleDiv.add(new TextField<String>("customer.address.title").add(
		new PatternValidator(Constants.REGEX_TITLE)).add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier).add(
		new ErrorIndicator()));
	titleDiv.setVisible(true);
	form.add(titleDiv);

	WebMarkupContainer languageDiv = new WebMarkupContainer("languageDiv");
	languageDiv.add(new LocalizableLookupDropDownChoice<String>(
		"customer.language", String.class,
		Constants.RESOURCE_BUNDLE_LANGUAGES, this, false, true)
		.add(new ErrorIndicator()));
	languageDiv.setVisible(true);
	form.add(languageDiv);

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.timeZone", String.class,
		Constants.RESOURCE_BUNDLE_TIMEZONES, this, false, true)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.street1").add(
		new PatternValidator(Constants.REGEX_STREET1)).add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier).add(
		new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.houseNo").add(
		new PatternValidator(Constants.REGEX_HOUSENO)).add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier).add(
		new ErrorIndicator()));
	form.add(new TextField<String>("customer.address.state").add(
		new PatternValidator(Constants.REGEX_STATE)).add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier).add(
		new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.address.kvCountry", String.class, "countries", this,
		false, true).setNullValid(false).setRequired(true).add(
		new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.street2").add(
		new PatternValidator(Constants.REGEX_STREET1)).add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier).add(
		new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.city").setRequired(
		false).add(new PatternValidator(Constants.REGEX_CITY)).add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier).add(
		new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.zip").add(
		new PatternValidator(Constants.REGEX_ZIP)).add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier).add(
		new ErrorIndicator()));

	form.add(new TextField<String>("customer.msisdn").setRequired(true)
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBER)).add(
			Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier).add(
			new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.email").setRequired(
		true).add(EmailAddressValidator.getInstance()).add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier).add(
		new ErrorIndicator()));
	WebMarkupContainer infoModeDiv = new WebMarkupContainer("infoModeDiv");
	infoModeDiv.setVisible(true);

	infoModeDiv.add(new LocalizableLookupDropDownChoice<Integer>(
		"customer.kvInfoMode", Integer.class,
		Constants.RESOURCE_BUNDLE_INFO_MODE, this, false, true)
		.setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	final WebMarkupContainer networkProviderDiv = new WebMarkupContainer(
		"networkProviderDiv");

	networkProviderDiv.add(new LocalizableLookupDropDownChoice<String>(
		"customer.networkProvider", String.class, "networkproviders",
		this, false, true).setNullValid(false).setRequired(true).add(
		new ErrorIndicator()));

	// network provider selection to be made only for mbanking customer
	// types
	if (customer.getCustomerTypeId() != null
		&& customer.getCustomerTypeId().intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
	    networkProviderDiv.setVisible(true);
	} else {
	    networkProviderDiv.setVisible(false);
	}

	infoModeDiv.add(networkProviderDiv);
	networkProviderDiv.setOutputMarkupPlaceholderTag(true);
	form.add(infoModeDiv);
	customertypes.add(new AjaxFormComponentUpdatingBehavior("onchange") {

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		if (customer.getCustomerTypeId() != null
			&& customer.getCustomerTypeId().intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
		    networkProviderDiv.setVisible(true);
		} else {
		    networkProviderDiv.setVisible(false);
		}
		target.addComponent(networkProviderDiv);
	    }
	});
	WebMarkupContainer identityInfoDiv = new WebMarkupContainer(
		"identityInfoDiv");

	final LocalizableLookupDropDownChoice<Integer> identityTypeDropDown = new LocalizableLookupDropDownChoice<Integer>(
		"customer.kvIdentityType", Integer.class,
		Constants.RESOURCE_BUNDLE_IDENTITYTYPES, this, false, true);

	identityInfoDiv.add(identityTypeDropDown.setNullValid(true)
		.setRequired(false).setEnabled(true).add(new ErrorIndicator()));
	identityInfoDiv.setVisible(true);

	final TextField<String> identityValueField = new TextField<String>(
		"customer.identityValue");

	identityInfoDiv.add(identityValueField.add(
		new PatternValidator(Constants.REGEX_ID_NO)).setRequired(false)
		.add(Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier).add(
			new ErrorIndicator()));
	form.add(identityInfoDiv);

	WebMarkupContainer securityInfoDiv = new WebMarkupContainer(
		"securityInfoDiv");
	securityInfoDiv.setVisible(true);

	securityInfoDiv.add(new LocalizableLookupDropDownChoice<String>(
		"customer.securityQuestion", String.class,
		Constants.RESOURCE_BUNDLE_SEC_QUESTIONS, basePage)
		.setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	securityInfoDiv
		.add(new RequiredTextField<String>("customer.SecQuesAns").add(
			new PatternValidator(Constants.REGEX_SECURITY_ANSWER))
			.add(Constants.mediumStringValidator).add(
				Constants.mediumSimpleAttributeModifier).add(
				new ErrorIndicator()));

	WebMarkupContainer credentialDiv = new WebMarkupContainer(
		"credentialDiv");
	credentialDiv.setVisible(true);
	credentialDiv.add(new RequiredTextField<String>("customer.userName")
		.add(new PatternValidator(Constants.REGEX_USERNAME))
		.setRequired(true).add(Constants.mediumStringValidator).add(
			Constants.mediumSimpleAttributeModifier).add(
			new ErrorIndicator()));
	PasswordTextField pin = new PasswordTextField("customer.pin");
	pin.add(new PatternValidator(Constants.REGEX_PIN)).add(
		Constants.largeStringValidator).add(
		new SimpleAttributeModifier("autocomplete", "off")).add(
		Constants.largeSimpleAttributeModifier);
	credentialDiv.add(pin.setRequired(true).add(new ErrorIndicator()));
	PasswordTextField password = new PasswordTextField("customer.password");
	credentialDiv.add(password.setRequired(true).add(
		Constants.largeStringValidator).add(
		new SimpleAttributeModifier("autocomplete", "off")).add(
		Constants.largeSimpleAttributeModifier).add(
		new ErrorIndicator()));

	WebMarkupContainer credentialConfirmDiv = new WebMarkupContainer(
		"credentialConfirmDiv");
	credentialConfirmDiv.setVisible(true);
	PasswordTextField pinConfirmed = new PasswordTextField("pinConfirmed");
	pinConfirmed.add(Constants.largeStringValidator).add(
		new SimpleAttributeModifier("autocomplete", "off")).add(
		Constants.largeSimpleAttributeModifier);
	credentialConfirmDiv.add(pinConfirmed.setRequired(true).add(
		new ErrorIndicator()));
	PasswordTextField passwordConfirmed = new PasswordTextField(
		"passwordConfirmed");
	credentialConfirmDiv.add(passwordConfirmed.setRequired(true).add(
		Constants.largeStringValidator).add(
		new SimpleAttributeModifier("autocomplete", "off")).add(
		Constants.largeSimpleAttributeModifier).add(
		new ErrorIndicator()));
	securityInfoDiv.add(credentialDiv);
	securityInfoDiv.add(credentialConfirmDiv);
	form.add(securityInfoDiv);

	// adding captcha and terms and conditions
	WebMarkupContainer signUpDiv = new WebMarkupContainer("signUpDiv");
	final CaptchaImage captchaImage = new CaptchaImage("captchaImage");
	captchaImage.setOutputMarkupId(true);

	captchTextField = new RequiredTextField("captcha") {
	    @Override
	    protected void onComponentTag(final ComponentTag tag) {
		super.onComponentTag(tag);
		if (form.hasError() && !this.hasErrorMessage())
		    tag.put("value", "");

	    }
	};
	captchTextField.add(new CaptchaValidator());
	captchTextField.add(new ErrorIndicator());
	signUpDiv.add(captchTextField);

	signUpDiv.add(new AjaxFallbackLink("captchLink") {
	    /**
	     * 
	     */
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		captchaImage.detach();

		if (target != null) {
		    target.addComponent(captchaImage);
		} else {
		    // javascript is disabled
		}
	    }

	}.add(captchaImage));

	RadioGroup termAgree = new RadioGroup("group", new PropertyModel(this,
		"isAgree"));
	signUpDiv.add(termAgree);
	termAgree.add(new Radio("agree", new Model(true)));
	termAgree.add(new Radio("disAgree", new Model(false)));
	WebPage wp = getTnCPage();
	signUpDiv.add(new InlineFrame("iframe", wp));
	form.add(signUpDiv);
	form.add(new EqualPasswordInputValidator(pin, pinConfirmed));
	form.add(new EqualPasswordInputValidator(password, passwordConfirmed));

	WebMarkupContainer signupButtons = new WebMarkupContainer(
		"signupButtons");
	signupButtons.setVisible(true);
	signupButtons.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (isEligible()) {
		    continueSignUp();
		} else {
		    return;
		}

	    };
	});

	signupButtons.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	signupButtons.add(new Button("cancelSignup") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleCancel();
	    };
	}.setDefaultFormProcessing(false));

	form.add(signupButtons);

	WebMarkupContainer cstButtons = new WebMarkupContainer("cstButtons");
	cstButtons.setVisible(true);
	cstButtons.add(new Button("register") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (isEligible()) {
		    continueRegistration();
		} else {
		    return;
		}
	    };
	});
	form.add(cstButtons);

	WebMarkupContainer dppButtons = new WebMarkupContainer("dppButtons");
	dppButtons.setVisible(true);
	dppButtons.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(SelfCareHomePage.class);
	    };
	}.setDefaultFormProcessing(false));

	dppButtons.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		try {
		    if (!isEligible()) {
			return;
		    }
		    if (getAttachments().isEmpty()) {
			error(getLocalizer().getString("required.attachment",
				this));
		    } else if (getBasePage().uniqueIdentificationCheck(
			    getCustomer().getMsisdn(),
			    Constants.IDENT_TYPE_MSISDN, getCustomer().getId())) {
			saveCustomer();
		    }
		} catch (Exception e) {
		    LOG.error(
			    "#An error occurred while registering a customer",
			    e);
		    error(getLocalizer().getString("register.customer.error",
			    this));
		}
	    };
	});

	form.add(dppButtons);

	// field toggling for CST
	if (getBasePage().getMobiliserWebSession().getRoles().hasRole(
		Constants.PRIV_CST_LOGIN)) {
	    securityInfoDiv.setVisible(true);
	    credentialDiv.setVisible(false);
	    credentialConfirmDiv.setVisible(false);
	    titleDiv.setVisible(false);
	    languageDiv.setVisible(true);
	    identityInfoDiv.setVisible(false);
	    infoModeDiv.setVisible(true);
	    dppButtons.setVisible(false);
	    signupButtons.setVisible(false);
	    signUpDiv.setVisible(false);
	    identityTypeDropDown.setRequired(false);
	    identityInfoDiv.addOrReplace(new Label("requiredIdentityType", ""));
	    identityValueField.setRequired(false);
	    identityInfoDiv
		    .addOrReplace(new Label("requiredIdentityValue", ""));
	} else if (getBasePage().getMobiliserWebSession().getRoles().hasRole(
		Constants.PRIV_MERCHANT_LOGIN)) {
	    securityInfoDiv.setVisible(false);
	    credentialDiv.setVisible(false);
	    credentialConfirmDiv.setVisible(false);
	    titleDiv.setVisible(false);
	    languageDiv.setVisible(false);
	    identityInfoDiv.setVisible(true);
	    infoModeDiv.setVisible(true);
	    cstButtons.setVisible(false);
	    customerTypeDiv.setVisible(false);
	    signupButtons.setVisible(false);
	    signUpDiv.setVisible(false);
	    identityTypeDropDown.setRequired(true);
	    identityInfoDiv
		    .addOrReplace(new Label("requiredIdentityType", "*"));
	    identityValueField.setRequired(true);
	    identityInfoDiv
		    .addOrReplace(new Label("requiredIdentityValue", "*"));
	} else {
	    cstButtons.setVisible(false);
	    dppButtons.setVisible(false);
	    languageDiv.setVisible(false);
	    identityInfoDiv.addOrReplace(new Label("requiredIdentityType", ""));
	    identityInfoDiv
		    .addOrReplace(new Label("requiredIdentityValue", ""));
	    customerTypeDiv.setVisible(false);
	}

	add(form);
    }

    protected void saveCustomer() {
	LOG.debug("#RegisterCustomerPanel.next()");

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

		customer = getBasePage().createFullCustomer(customer,
			getAttachments());
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
	    // if (getBasePage().isCreateStatus() && paymentInstrumentId !=
	    // null) {
	    // getBasePage().createWalletEntry(customer.getId(),
	    // paymentInstrumentId,
	    // getBasePage().getConfiguration().getSVAAlias());
	    // }
	    // if (getBasePage().isCreateStatus()) {
	    // customer = getBasePage().createCustomerIdentity(customer);
	    // }

	    // if (getBasePage().isCreateStatus() && attachmentsPanel != null
	    // && !getAttachments().isEmpty()) {
	    // for (Attachment attachment : getAttachments()) {
	    // getBasePage().createAttachmentService(attachment,
	    // customer.getId());
	    // }
	    // }
	    if (getBasePage().isCreateStatus()) {
		info("Customer created successfully");
		getBasePage().getMobiliserWebSession().setCustomer(customer);
		customer.setMsisdn(new PhoneNumber(customer.getMsisdn())
			.getInternationalFormat());
		customer.setDisplayName(basePage.createDisplayName(customer
			.getAddress().getFirstName(), customer.getAddress()
			.getLastName()));
		if (getBasePage().getMobiliserWebSession().getRoles().hasRole(
			Constants.PRIV_MERCHANT_LOGIN))
		    setResponsePage(CustomerDetailsPage.class);
		if (getBasePage().getMobiliserWebSession().getRoles().hasRole(
			Constants.PRIV_CST_LOGIN))
		    setResponsePage(new StandingDataPage(getCustomer()));
	    }
	} catch (Exception e) {
	    LOG.error("# An error has occurred for register customer ", e);
	    error(getLocalizer().getString("customerRegistration.error", this));
	    getBasePage().setCreateStatus(false);
	} finally {

	    if (!getBasePage().isCreateStatus()) {
		if (PortalUtils.exists(customer)
			&& customer.isPendingApproval()
			&& this.basePage instanceof RegisterCustomerPage) {
		    customer = null;
		    info(getLocalizer().getString("pendingApproval.msg", this));

		    setResponsePage(basePage);
		} else {

		    try {
			if (PortalUtils.exists(customer)
				&& PortalUtils.exists(customer.getId()))
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

    public MobiliserBasePage getBasePage() {
	return basePage;
    }

    public void setBasePage(MobiliserBasePage basePage) {
	this.basePage = basePage;
    }

    public CustomerBean getCustomer() {
	return customer;
    }

    public void setCustomer(CustomerBean customer) {
	this.customer = customer;
    }

    public List<Attachment> getAttachments() {
	List<Attachment> attachments = attachmentsPanel.getAttachments();
	if (attachments == null) {
	    attachments = new ArrayList<Attachment>();
	}
	return attachments;
    }

    protected WebPage getTnCPage() {
	LOG.debug("#RegisterCustomerPanel.getTnCPage()");
	String className = "com.sybase365.mobiliser.web.consumer.pages.signup."
		+ "TermsAndConditions_"
		+ getBasePage().getMobiliserWebSession().getLocale()
			.getLanguage();
	WebPage wp = new TermsAndConditions_en();
	try {
	    Class classObj = Class.forName(className);
	    wp = (WebPage) classObj.newInstance();
	} catch (ClassNotFoundException e) {
	    LOG.error("# Class not Found Exception", e);
	} catch (IllegalAccessException e) {
	    LOG.error("# IllegalAccessException", e);
	} catch (InstantiationException e) {
	    LOG.error("# InstantiationException", e);
	}
	return wp;
    }

    protected void handleBack() {
	LOG.debug("#RegisterCustomerPanel.handleBack()");
	setResponsePage(SignupStartPage.class);
    }

    protected void handleCancel() {
	LOG.debug("#RegisterCustomerPanel.handleCancel()");
	setResponsePage(SignupCancelPage.class);
    }

    protected void continueSignUp() {
	try {
	    if (!getBasePage().uniqueIdentificationCheck(
		    getCustomer().getMsisdn(), Constants.IDENT_TYPE_MSISDN,
		    getCustomer().getId()))
		return;
	    // if (getBasePage().duplicateUsernameCheck(customer))
	    // return;
	    if (!getBasePage().uniqueIdentificationCheck(
		    getCustomer().getUserName(), Constants.IDENT_TYPE_USERNAME,
		    getCustomer().getId()))
		return;
	    if (isAgree == null || !isAgree) {
		error(getLocalizer().getString("terms.disAgree", this));
		return;
	    }
	    if (!getBasePage().checkPasswordStrength(customer))
		return;

	    if (!getBasePage().checkPinStrength(customer))
		return;
	    setResponsePage(new ConfirmDataPage(customer, this.getWebPage()));
	} catch (Exception e) {
	    LOG.error("#An error occurred while registering a customer", e);
	    error(getLocalizer().getString("register.customer.error", this));
	}

    }

    protected void continueRegistration() {
	try {
	    if (!getBasePage().uniqueIdentificationCheck(
		    getCustomer().getMsisdn(), Constants.IDENT_TYPE_MSISDN,
		    getCustomer().getId()))
		return;
	    setResponsePage(new com.sybase365.mobiliser.web.cst.pages.customercare.ConfirmDataPage(
		    customer, this.getWebPage()));
	} catch (Exception e) {
	    LOG.error("#An error occurred while registering a customer", e);
	    error(getLocalizer().getString("register.customer.error", this));
	}
    }

    protected boolean isEligible() {
	if (PortalUtils.exists(customer.getBirthDateString())) {
	    Calendar cc = Calendar.getInstance();
	    cc.setTimeInMillis(System.currentTimeMillis());
	    cc.set(Calendar.YEAR, cc.get(Calendar.YEAR) - 18);
	    Calendar c = customer.getBirthDateAsXml().toGregorianCalendar();

	    if (c.after(cc)) {
		error(getLocalizer().getString("customer.age.error", this));
		captcha = "";
		return false;
	    }
	    return true;
	}
	return false;
    }

    public String getPinConfirmed() {
	return pinConfirmed;
    }

    public void setPinConfirmed(String pinConfirmed) {
	this.pinConfirmed = pinConfirmed;
    }

    public String getPasswordConfirmed() {
	return passwordConfirmed;
    }

    public void setPasswordConfirmed(String passwordConfirmed) {
	this.passwordConfirmed = passwordConfirmed;
    }

    public Boolean getIsAgree() {
	return isAgree;
    }

    public void setIsAgree(Boolean isAgree) {
	this.isAgree = isAgree;
    }

    public String getCaptcha() {
	return captcha;
    }

    public void setCaptcha(String captcha) {
	this.captcha = captcha;
    }

}
