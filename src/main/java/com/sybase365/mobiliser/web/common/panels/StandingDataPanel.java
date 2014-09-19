package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.ContinuePendingCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.ContinuePendingCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteIdentificationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.cst.pages.customercare.ApproveCustomersPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.ChangeMsisdnPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.ContactNotePage;
import com.sybase365.mobiliser.web.cst.pages.customercare.IndividualFeeSetConfig;
import com.sybase365.mobiliser.web.cst.pages.customercare.IndividualLimitSetConfig;
import com.sybase365.mobiliser.web.cst.pages.customercare.ResetCredentialPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.StandingDataPage;
import com.sybase365.mobiliser.web.cst.pages.selfcare.CstHomePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class StandingDataPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected MobiliserBasePage mobBasePage;
    protected String msisdn;
    protected CustomerBean customer;

    private static final Logger LOG = LoggerFactory
	    .getLogger(StandingDataPanel.class);

    public StandingDataPanel(String id, MobiliserBasePage mobBasePage) {
	super(id);
	this.mobBasePage = mobBasePage;
	this.customer = getCustomer();
	this.msisdn = getCustomer().getMsisdn();
	this.constructPanel();

    }

    public StandingDataPanel(String id, MobiliserBasePage mobBasePage,
	    CustomerBean customer) {
	super(id);
	this.mobBasePage = mobBasePage;
	this.customer = customer;
	this.msisdn = customer.getMsisdn();
	this.constructPanel();
    }

    protected void constructPanel() {

	final String chooseDtTxt = this.getLocalizer().getString(
		"datepicker.chooseDate", mobBasePage);

	add(new HeaderContributor(new IHeaderContributor() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void renderHead(IHeaderResponse response) {

		// localize the jquery datepicker based on users locale setting
		// locale specific js includes for datepicker are available at
		// http://jquery-ui.googlecode.com/svn/trunk/ui/i18n/
		String localeLang = getLocale().getLanguage().toLowerCase();

		LOG.debug("Using DatePicker for locale language: {}",
			localeLang);

		if (PortalUtils.exists(localeLang)) {
		    response.renderJavascriptReference("scripts/jquery/i18n/jquery.ui.datepicker-"
			    + localeLang + ".js");
		}

		response.renderJavascript("\n"
			+ "jQuery(document).ready(function($) { \n"
			+ "  $('#birthDate').datepicker( { \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'changeMonth' : true, \n"
			+ "	'changeYear' : true, \n"
			+ "       'yearRange' : '-100:+0', \n"
			+ "	'showOn': 'both', \n" + "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	final Form<?> form = new Form("standingDataForm",
		new CompoundPropertyModel<StandingDataPanel>(this));

	if (!PortalUtils.exists(getCustomer().getTaskId()))
	    mobBasePage.getMobiliserWebSession().setShowContact(true);
	form.add(new FeedbackPanel("errorMessages"));
	form.add(
		new RequiredTextField<String>("customer.address.firstName")
			.setRequired(true)
			.add(new PatternValidator(Constants.REGEX_FIRSTNAME))
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add(new DateTextField("birthDateField", new PropertyModel<Date>(
		this, "customer.birthDateString"), new PatternDateConverter(
		Constants.DATE_FORMAT_PATTERN_PARSE, false)).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"customer.customerTypeId", Integer.class,
		Constants.RESOURCE_BUNDLE_CUSTOMER_TYPE, this, Boolean.FALSE,
		true).setNullValid(false).setRequired(true));

	form.add(
		new RequiredTextField<String>("customer.address.lastName")
			.setRequired(true)
			.add(new PatternValidator(Constants.REGEX_FIRSTNAME))
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.language", String.class,
		Constants.RESOURCE_BUNDLE_LANGUAGES, this, false, true)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.timeZone", String.class,
		Constants.RESOURCE_BUNDLE_TIMEZONES, this, false, true)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.street1")
		.add(new PatternValidator(Constants.REGEX_STREET1))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.houseNo")
		.add(StringValidator.lengthBetween(1, 20))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("customer.address.state")
		.add(new PatternValidator(Constants.REGEX_STATE))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.address.kvCountry", String.class, "countries", this,
		false, true).setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.street2")
		.add(new PatternValidator(Constants.REGEX_STREET1))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.city")
		.setRequired(false)
		.add(new PatternValidator(Constants.REGEX_CITY))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.zip")
		.add(new PatternValidator(Constants.REGEX_ZIP))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	TextField<String> msisdn = new TextField<String>("customer.msisdn");
	if (!mobBasePage.getConfiguration().isMsisdnOtpConfirmed()) {
	    msisdn.add(new SimpleAttributeModifier("readonly", "readonly"));
	    msisdn.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));
	}
	form.add(msisdn.add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.email")
		.setRequired(true).add(EmailAddressValidator.getInstance())
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"customer.kvInfoMode", Integer.class, "sendModes", this,
		Boolean.FALSE, true).setNullValid(false).setRequired(true));

	WebMarkupContainer networkProviderDiv = new WebMarkupContainer(
		"networkProviderDiv");

	networkProviderDiv.add(new LocalizableLookupDropDownChoice<String>(
		"customer.networkProvider", String.class, "networkproviders",
		this, false, true).setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	// network provider selection to be made only for mbanking customer
	// types
	if (customer.getCustomerTypeId() != null
		&& customer.getCustomerTypeId().intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
	    networkProviderDiv.setVisible(true);
	} else {
	    networkProviderDiv.setVisible(false);
	}

	form.add(networkProviderDiv);

	form.add(new KeyValueDropDownChoice<Long, String>("customer.feeSetId",
		mobBasePage.getFeeSets(getCustomer().getFeeSetId())) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected CharSequence getDefaultChoice(Object selected) {
		return null;
	    };
	}.setNullValid(false));

	form.add(new KeyValueDropDownChoice<Long, String>("customer.limitId",
		getLimitSets(getCustomer().getLimitId())) {

	    private static final long serialVersionUID = 1L;

	}.setNullValid(true));

	Button feeSetConfButton = new Button("feeSetConf") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new IndividualFeeSetConfig(getCustomer()));
	    };
	}.setDefaultFormProcessing(false);
	feeSetConfButton.add(new PrivilegedBehavior(mobBasePage,
		Constants.PRIV_CUST_WRITE));
	form.add(feeSetConfButton);

	Button limitSetConfButton = new Button("limitSetConf") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(new IndividualLimitSetConfig(getCustomer()));
	    };
	}.setDefaultFormProcessing(false);
	limitSetConfButton.add(new PrivilegedBehavior(mobBasePage,
		Constants.PRIV_CUST_WRITE));
	form.add(limitSetConfButton);

	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.securityQuestion", String.class,
		Constants.RESOURCE_BUNDLE_SEC_QUESTIONS, this, false, true)
		.setNullValid(false).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.userName")
		.add(new PatternValidator(Constants.REGEX_USERNAME))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("customer.SecQuesAns")
		.add(new PatternValidator(Constants.REGEX_SECURITY_ANSWER))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	PrivilegedBehavior cancelReasonBehavior = new PrivilegedBehavior(
		mobBasePage, Constants.PRIV_CUST_CANCEL);
	cancelReasonBehavior.setMissingPrivilegeHidesComponent(false);
	KeyValueDropDownChoice<Integer, String> custStatus = new KeyValueDropDownChoice<Integer, String>(
		"customer.active", mobBasePage.getCustomerStatus());
	custStatus.add(new SimpleAttributeModifier("onchange",
		"confirmDeactivation('"
			+ getLocalizer().getString(
				"customer.deactivate.warning", mobBasePage)
			+ "')"));
	form.add(custStatus.setNullValid(false).setRequired(true)
		.add(cancelReasonBehavior));

	WebMarkupContainer blackListReasonDiv = new WebMarkupContainer(
		"blackListReasonDiv");
	PrivilegedBehavior blackListBehavior = new PrivilegedBehavior(
		mobBasePage, Constants.PRIV_CUST_BLACKLIST);
	blackListBehavior.setMissingPrivilegeHidesComponent(false);
	blackListReasonDiv.add(new LocalizableLookupDropDownChoice<Integer>(
		"customer.blackListReason", Integer.class, "blackListReasons",
		this, Boolean.FALSE, true).setNullValid(false)
		.setRequired(true).add(blackListBehavior));

	form.add(blackListReasonDiv);

	WebMarkupContainer cancelDivContainer = new WebMarkupContainer(
		"cancelDivContainer");
	LocalizableLookupDropDownChoice<Integer> cancelationreason = new LocalizableLookupDropDownChoice<Integer>(
		"customer.cancelationReason", Integer.class,
		"cancellationReasons", this, false, true);
	cancelationreason.add(new SimpleAttributeModifier("onchange",
		"confirmCancellation('"
			+ getLocalizer().getString("customer.cancel.warning",
				mobBasePage) + "')"));
	cancelDivContainer.add(cancelationreason.setNullValid(false)
		.setRequired(true).add(cancelReasonBehavior));
	// cancelDivContainer
	// .setVisible(getCustomer().getCustomerTypeId() ==
	// Constants.CONSUMER_IDTYPE
	// || getCustomer().getCustomerTypeId() ==
	// Constants.CUSTOMER_ROLE_MONEY_MERCHANT);
	form.add(cancelDivContainer);
	Button changenMsisdnButton = new Button("changeMsisdn") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		Customer customer = mobBasePage.getCustomerByIdentification(
			Constants.IDENT_TYPE_CUST_ID,
			String.valueOf(getCustomer().getId()));
		if (customer.getCancellationReasonId() != 0
			|| !customer.isActive()) {
		    error(getLocalizer().getString(
			    "customer.msisdn.change.error.customerinactive",
			    mobBasePage));
		    return;
		}
		mobBasePage.getMobiliserWebSession().setCustomerOtp(null);
		mobBasePage.getMobiliserWebSession().setCustomerOtpCount(0);
		mobBasePage.getMobiliserWebSession().setCustomerOtpLimitHit(
			false);
		setResponsePage(new ChangeMsisdnPage(getCustomer()));
	    };
	}.setDefaultFormProcessing(false);
	changenMsisdnButton.setVisible(!mobBasePage.getConfiguration()
		.isMsisdnOtpConfirmed());
	changenMsisdnButton.add(new PrivilegedBehavior(mobBasePage,
		Constants.PRIV_CUST_WRITE));

	form.add(changenMsisdnButton);
	changenMsisdnButton.setVisible(!mobBasePage.getConfiguration()
		.isMsisdnOtpConfirmed());

	form.add(new Button("resetPin") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		Customer customer = mobBasePage.getCustomerByIdentification(
			Constants.IDENT_TYPE_MSISDN, getCustomer().getMsisdn());
		if (!PortalUtils.exists(customer)) {
		    error(getLocalizer().getString(
			    "customer.reset.password.noMsisdn", mobBasePage));
		    return;
		}
		setResponsePage(new ResetCredentialPage(getCustomer(), this
			.getWebPage(), "pin"));
	    };
	}.setDefaultFormProcessing(false)
		.add(new PrivilegedBehavior(mobBasePage,
			Constants.PRIV_CUST_PINCALL)));

	form.add(new Button("resetPassword") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		Address address = mobBasePage
			.getAddressByCustomer(getCustomer().getId());
		if (address == null || !PortalUtils.exists(address.getEmail())) {
		    error(getLocalizer().getString(
			    "customer.reset.password.noEmail", mobBasePage));
		    return;
		}
		setResponsePage(new ResetCredentialPage(getCustomer(), this
			.getWebPage(), "password"));
	    };
	}.setDefaultFormProcessing(false).add(
		new PrivilegedBehavior(mobBasePage,
			Constants.PRIV_CUST_PASSWORD)));

	form.add(new Button("update") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (updateCustomer()) {
		    LOG.info("Data updated successfully for customer["
			    + mobBasePage.getMobiliserWebSession()
				    .getCustomer().getId() + "]");
		    getSession().info(
			    getLocalizer().getString("data.update.successful",
				    mobBasePage));
		    setResponsePage(new StandingDataPage(getCustomer()));

		}
	    };
	}.add(new PrivilegedBehavior(mobBasePage, Constants.PRIV_CUST_WRITE))
		.setVisible(!PortalUtils.exists(getCustomer().getTaskId())));

	form.add(new Button("approve") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		approveCustomer(true);
	    };
	}.setDefaultFormProcessing(false).setVisible(
		PortalUtils.exists(getCustomer().getTaskId())));

	form.add(new Button("reject") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		approveCustomer(false);
	    };
	}.setDefaultFormProcessing(false).setVisible(
		PortalUtils.exists(getCustomer().getTaskId())));

	if (PortalUtils.exists(getCustomer().getTaskId())) {
	    Iterator iter = form.iterator();
	    Component component;
	    for (int i = 0; iter.hasNext(); i++) {
		component = (Component) iter.next();

		if (component.getId().equals("approve")
			|| component.getId().equals("reject")
			|| component instanceof FeedbackPanel) {
		    continue;
		} else if (component instanceof Button) {
		    component.setVisible(false);
		} else {

		    if (component.getId().equals("blackListReasonDiv")
			    || component.getId().equals("cancelDivContainer")) {
			Iterator iter1 = ((WebMarkupContainer) component)
				.iterator();
			Component comp;
			for (int j = 0; iter1.hasNext(); j++) {
			    comp = (Component) iter1.next();
			    comp.setEnabled(false);
			    comp.add(new SimpleAttributeModifier("readonly",
				    "readonly"));
			    comp.add(new SimpleAttributeModifier("style",
				    "background-color: #E6E6E6;"));

			}

		    } else {
			component.setEnabled(false);
			component.add(new SimpleAttributeModifier("readonly",
				"readonly"));
			component.add(new SimpleAttributeModifier("style",
				"background-color: #E6E6E6;"));
		    }

		}
	    }
	}

	add(form);

	LOG.debug("PatternDateConverter format: "
		+ Constants.DATE_FORMAT_PATTERN_PARSE + " DatePicker format: "
		+ Constants.DATE_FORMAT_PATTERN_PICKER);

    }

    protected boolean updateCustomer() {
	try {
	    Identification existingMsisdn = mobBasePage
		    .getIdentificationByCustomer(getCustomer().getId(),
			    Constants.IDENT_TYPE_MSISDN);
	    Identification existingUserName = mobBasePage
		    .getIdentificationByCustomer(getCustomer().getId(),
			    Constants.IDENT_TYPE_USERNAME);
	    String existingMsisdnStr = null;
	    if (PortalUtils.exists(existingMsisdn)) {
		existingMsisdnStr = existingMsisdn.getIdentification();
	    }
	    String existingUserNameStr = null;
	    if (PortalUtils.exists(existingUserName)) {
		existingUserNameStr = existingUserName.getIdentification();
	    }
	    String existingNetworkProviderStr = null;
	    if (PortalUtils.exists(existingMsisdn)) {
		existingNetworkProviderStr = existingMsisdn.getProvider();
	    }
	    // do the unique identification check only when msisdn is modified
	    // OR when a new msisdn is given
	    if ((PortalUtils.exists(existingMsisdnStr) && !existingMsisdnStr
		    .equals(getCustomer().getMsisdn()))
		    || (!PortalUtils.exists(existingMsisdnStr) && PortalUtils
			    .exists(getCustomer().getMsisdn()))) {
		if (PortalUtils.exists(getCustomer().getMsisdn())
			&& !mobBasePage.uniqueIdentificationCheck(getCustomer()
				.getMsisdn(), Constants.IDENT_TYPE_MSISDN,
				getCustomer().getId())) {
		    getCustomer().setMsisdn(existingMsisdnStr);
		    return false;
		}
	    }
	    // do the unique identification check when user name is modified
	    // OR when a new user name is given
	    if ((PortalUtils.exists(existingUserNameStr) && !existingUserNameStr
		    .equals(getCustomer().getUserName()))
		    || (!PortalUtils.exists(existingUserNameStr) && PortalUtils
			    .exists(getCustomer().getUserName()))) {
		if (PortalUtils.exists(getCustomer().getUserName())
			&& !mobBasePage.uniqueIdentificationCheck(getCustomer()
				.getUserName(), Constants.IDENT_TYPE_USERNAME,
				getCustomer().getId())) {
		    getCustomer().setUserName(existingUserNameStr);
		    return false;
		}
	    }
	    // check if customer status has been changed
	    boolean isStatusChanged = false;
	    if (!getCustomer().isActive()) {
		isStatusChanged = true;
		getCustomer().setActive(true);
	    }

	    if (PortalUtils.exists(getCustomer().getOriginalFeeSetId())) {
		if (getCustomer().getOriginalFeeSetId().longValue() != getCustomer()
			.getFeeSetId().longValue()) {
		    detachFeeSetFromCustomer(getCustomer()
			    .getOriginalFeeSetId(), getCustomer().getFeeSetId());
		}
	    }
	    getCustomer().setOriginalFeeSetId(getCustomer().getFeeSetId());
	    // Check whether individual limit configuration changed

	    if (PortalUtils.exists(getCustomer().getOriginalLimitSetId())) {
		if (!PortalUtils.exists(getCustomer().getLimitId())
			|| getCustomer().getOriginalLimitSetId().longValue() != getCustomer()
				.getLimitId().longValue()) {
		    if (getCustomer().getIsIndividualLimitSet()) {
			List<LimitSetClass> limitSetClassList = mobBasePage
				.getLimitSetClassList(getCustomer()
					.getOriginalLimitSetId().longValue(),
					Constants.LIMIT_LIMITSET_TYPE);
			if (PortalUtils.exists(limitSetClassList)) {
			    for (LimitSetClass limitSetClass : limitSetClassList) {
				if (getCustomer().getOriginalLimitSetId()
					.longValue() == limitSetClass
					.getLimitSet().getId().longValue()) {
				    mobBasePage
					    .removeLimitClass(
						    limitSetClass
							    .getLimitClass()
							    .getId(),
						    limitSetClass.getLimitSet()
							    .getId(),
						    Integer.valueOf(Constants.LIMIT_LIMITSET_TYPE));

				    mobBasePage.removeLimitSet(getCustomer()
					    .getOriginalLimitSetId()
					    .longValue());

				    getCustomer().setIsIndividualLimitSet(
					    Boolean.FALSE);
				}
			    }
			}

		    }

		    if (PortalUtils.exists(getCustomer().getLimitId())
			    && getCustomer().getLimitId().longValue() == 0) {
			getCustomer().setLimitId(null);
		    }
		    getCustomer().setOriginalLimitSetId(
			    getCustomer().getLimitId());
		}

	    }

	    if (!mobBasePage.updateCustomerDetail(getCustomer()))
		return false;

	    if (PortalUtils.exists(getCustomer().getAddress().getId())) {
		if (!mobBasePage.updateCustomerAddress(getCustomer()))
		    return false;
	    } else {
		if (mobBasePage.createCustomerAddress(getCustomer()) == null)
		    return false;
	    }
	    if (isStatusChanged) {
		if (!mobBasePage.deleteCustomer(getCustomer().getId()))
		    return false;
		getCustomer().setActive(false);
		setResponsePage(new ContactNotePage(new CstHomePage()));
		info(getLocalizer().getString("customer.deactivated.success",
			mobBasePage));
		LOG.info("Customer["
			+ mobBasePage.getMobiliserWebSession().getCustomer()
				.getId() + "] has been succesfully deactivated");
		return false;
	    }

	    if (getCustomer().isActive()
		    && (getCustomer().getBlackListReason() == null || getCustomer()
			    .getCancelationReason() == Constants.CANCELATION_REASON_OK)) {
		if (PortalUtils.exists(getCustomer().getMsisdn())) {
		    if ((PortalUtils.exists(existingMsisdnStr) && !existingMsisdnStr
			    .equals(getInternationalMsisdnFormat(getCustomer()
				    .getMsisdn())))
			    || (PortalUtils.exists(existingNetworkProviderStr) && !existingNetworkProviderStr
				    .equals(getCustomer().getNetworkProvider()))) {
			if (mobBasePage.updateIdentificationByCustomer(
				getCustomer().getId(),
				Constants.IDENT_TYPE_MSISDN, existingMsisdn
					.getId(),
				getInternationalMsisdnFormat(getCustomer()
					.getMsisdn()), getCustomer()
					.getNetworkProvider())) {
			    getCustomer().setMsisdn(
				    getInternationalMsisdnFormat(getCustomer()
					    .getMsisdn()));
			} else {
			    getCustomer().setMsisdn(
				    existingMsisdn.getIdentification());
			    return false;
			}
		    } else if (!PortalUtils.exists(existingMsisdnStr)) {
			mobBasePage.createCustomerMsisdn(getCustomer());
			if (mobBasePage.isCreateStatus())
			    getCustomer().setMsisdn(
				    getInternationalMsisdnFormat(getCustomer()
					    .getMsisdn()));
			else {
			    getCustomer().setMsisdn(null);
			    return false;
			}
		    }
		} else if (PortalUtils.exists(existingMsisdn)
			&& PortalUtils.exists(existingMsisdn.getId())) {
		    // remove identification
		    DeleteIdentificationRequest req = mobBasePage
			    .getNewMobiliserRequest(DeleteIdentificationRequest.class);
		    req.setIdentificationId(existingMsisdn.getId());
		    DeleteIdentificationResponse res = mobBasePage.wsIdentClient
			    .deleteIdentification(req);
		    if (!mobBasePage.evaluateMobiliserResponse(res)) {
			return false;
		    }
		}
		// create or update user name
		if (PortalUtils.exists(getCustomer().getUserName())) {
		    if (PortalUtils.exists(existingUserNameStr)
			    && !existingUserNameStr.equals(getCustomer()
				    .getUserName())) {
			if (!mobBasePage.updateIdentificationByCustomer(
				getCustomer().getId(),
				Constants.IDENT_TYPE_USERNAME, existingUserName
					.getId(), getCustomer().getUserName(),
				null)) {
			    getCustomer().setUserName(
				    existingUserName.getIdentification());
			    return false;
			}
		    } else if (!PortalUtils.exists(existingUserNameStr)) {
			mobBasePage.createCustomerUserName(getCustomer());
			if (!mobBasePage.isCreateStatus()) {
			    getCustomer().setUserName(null);
			    return false;
			}
		    }
		} else if (PortalUtils.exists(existingUserName)
			&& PortalUtils.exists(existingUserName.getId())) {
		    // remove identification
		    DeleteIdentificationRequest req = mobBasePage
			    .getNewMobiliserRequest(DeleteIdentificationRequest.class);
		    req.setIdentificationId(existingUserName.getId());
		    DeleteIdentificationResponse res = mobBasePage.wsIdentClient
			    .deleteIdentification(req);
		    if (!mobBasePage.evaluateMobiliserResponse(res)) {
			return false;
		    }
		}
	    }
	    return true;

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while updating details for customer["
			    + getCustomer().getId() + "]", e);
	    error(getLocalizer()
		    .getString("customer.update.error", mobBasePage));
	    return false;
	}

    }

    public CustomerBean getCustomer() {
	return mobBasePage.getMobiliserWebSession().getCustomer();
    }

    public String getMsisdn() {
	return msisdn;
    }

    protected String getInternationalMsisdnFormat(String msisdn) {
	PhoneNumber pn = new PhoneNumber(msisdn);
	return pn.getInternationalFormat();
    }

    protected void detachFeeSetFromCustomer(Long oldFeeSetId, Long newFeeSetId) {
	List<FeeSet> feeSetsList = null;
	try {
	    feeSetsList = mobBasePage.getFeeSetsList(null);
	    for (FeeSet feeSet : feeSetsList) {
		if (feeSet.getId().longValue() == oldFeeSetId.longValue()) {
		    if (feeSet.isIndividual()) {
			try {
			    mobBasePage.removeFeeSet(feeSet);
			} catch (Exception e) {
			    error(getLocalizer().getString(
				    "feeSets.remove.error", mobBasePage));
			    LOG.error("# An error occurred while deleting the fee set");
			}
			// break;
		    }
		} else if (feeSet.getId().longValue() == newFeeSetId
			.longValue()) {
		    if (feeSet.isIndividual()) {
			mobBasePage.getMobiliserWebSession().getCustomer()
				.setIsIndividualFeeSet(Boolean.TRUE);
		    } else {
			mobBasePage.getMobiliserWebSession().getCustomer()
				.setIsIndividualFeeSet(Boolean.FALSE);
		    }
		}
	    }
	    if (PortalUtils.exists(newFeeSetId) && newFeeSetId.longValue() == 0) {
		mobBasePage.getMobiliserWebSession().getCustomer()
			.setIsIndividualFeeSet(Boolean.FALSE);

	    }
	} catch (DataProviderLoadException dpe) {
	    LOG.error("#An error occurred while fetching fee sets");
	}
	((StandingDataPage) (mobBasePage)).buildLeftMenu();
	setResponsePage(new StandingDataPage(getCustomer()));
    }

    protected List<KeyValue<Long, String>> getLimitSets(Long limitSetId) {
	List<KeyValue<Long, String>> limitSetKeyValueList = new ArrayList<KeyValue<Long, String>>();
	List<LimitSet> limitSetList;
	try {
	    limitSetList = mobBasePage.findLimitSet(null);
	    KeyValue<Long, String> keyValue;
	    boolean isGlobalLimitSet = false;
	    if (PortalUtils.exists(limitSetList)) {
		for (LimitSet limitSet : limitSetList) {
		    keyValue = new KeyValue<Long, String>(new Long(limitSet
			    .getId().intValue()), limitSet.getName());
		    limitSetKeyValueList.add(keyValue);
		    if (!PortalUtils.exists(limitSetId)
			    || limitSet.getId().longValue() == limitSetId
				    .longValue())
			isGlobalLimitSet = true;
		}
	    }

	    if (PortalUtils.exists(limitSetId) && limitSetId.longValue() != 0
		    && !isGlobalLimitSet) {
		keyValue = new KeyValue<Long, String>(new Long(
			limitSetId.longValue()), getLocalizer().getString(
			"cst.customercare.limitset.individual", mobBasePage));
		limitSetKeyValueList.add(keyValue);
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while fetching limit sets" + e);
	}
	return limitSetKeyValueList;
    }

    protected void approveCustomer(boolean bApprove) {
	ContinuePendingCustomerResponse response = null;

	try {
	    ContinuePendingCustomerRequest request = mobBasePage
		    .getNewMobiliserRequest(ContinuePendingCustomerRequest.class);
	    request.setTaskId(getCustomer().getTaskId());
	    request.setApprove(bApprove);
	    response = mobBasePage.wsCustomerClient
		    .continuePendingCustomer(request);
	    if (!mobBasePage.evaluateMobiliserResponse(response)) {
		if (bApprove) {
		    LOG.warn("# An error occurred while approving customer");
		} else {
		    LOG.warn("# An error occurred while rejecting customer");
		}
		return;
	    }

	} catch (Exception e) {
	    if (bApprove) {

		LOG.error("# An error occurred while approving customer");
		error(getLocalizer().getString("customer.approval.error",
			mobBasePage));
	    } else {
		LOG.error("# An error occurred while rejecting customer");
		error(getLocalizer().getString("customer.rejection.error",
			mobBasePage));

	    }
	}

	// Create SVA for customer after approval
	if (bApprove) {

	    boolean isSvaCreated = false;
	    try {
		// the customer object at this point does not contain
		// customer id value from DB so set the id obtained from
		// response
		customer.setId(response.getCustomerId());
		isSvaCreated = mobBasePage.createSvaWalletWithPI(customer);
	    } catch (Exception e1) {
		LOG.error("# An error occured while creating SVA for customer["
			+ customer.getId() + "]", e1);

	    } finally {
		if (!isSvaCreated && PortalUtils.exists(response)
			&& PortalUtils.exists(response.getCustomerId())) {
		    try {
			mobBasePage.deleteCustomer(customer.getId());
		    } catch (Exception e1) {
			LOG.error(
				"# An error occured while deleting the customer["
					+ customer.getId() + "]", e1);

		    }

		}
	    }

	    // DPP agent related processing
	    Integer custType = customer.getCustomerTypeId();
	    if (custType == Constants.CUSTOMER_ROLE_MONEY_MERCHANT
		    || custType == Constants.CUSTOMER_ROLE_MONEY_MERCHANT_AGENT
		    || custType == Constants.CUSTOMER_ROLE_MONEY_MERCHANT_DEALER) {
		inheritParentPI();
		// createBalanceAlerts();
		// saveAgentLimitSettings();
	    }

	}// End of SVA Creation and DPP agent processing

	if (bApprove) {
	    LOG.info("Customer approved successfully");
	    mobBasePage.getMobiliserWebSession().info(
		    getLocalizer().getString("customer.approved.successfull",
			    mobBasePage));

	} else {
	    LOG.info("Customer rejected successfully");
	    mobBasePage.getMobiliserWebSession().info(
		    getLocalizer().getString("customer.rejected.successfull",
			    mobBasePage));

	}

	setResponsePage(ApproveCustomersPage.class);

    }

    private void inheritParentPI() {
	// inherit parent PI's if customer has privilege
	try {
	    List<String> privileges = mobBasePage.getDefaultPrivileges(
		    customer.getCustomerTypeId(), customer.getRiskCategoryId());
	    if (PortalUtils.exists(privileges)
		    && privileges.contains(Constants.PRIV_AUTO_INHERIT_PIS)) {
		List<WalletEntry> weList = mobBasePage.getWalletEntryList(
			customer.getParentId(), null, null);
		if (PortalUtils.exists(weList)) {
		    for (WalletEntry we : weList) {
			LOG.debug("# add sva[" + we.getPaymentInstrumentId()
				+ "] to the wallet of customer["
				+ customer.getId() + "]");

			mobBasePage.createAgentWalletEntry(customer.getId(),
				we.getPaymentInstrumentId(), we.getAlias());
		    }
		}
	    }
	} catch (Exception e) {
	    error(getLocalizer().getString("ERROR.CREATE_AGENT_FAILURE", this));

	    LOG.error(
		    "# An error occurred while creating new customer's wallet Entry",
		    e);
	}

    }

    // private void createBalanceAlerts() {
    // try {
    //
    // // TODO
    // // Check with Andreas whether it is correct to set parent's
    // // PI's PI ID
    // WalletEntry svaWallet = mobBasePage.getSvaPI(customer.getId());
    // Long paymentInstrumentId = null;
    // if (PortalUtils.exists(svaWallet))
    // paymentInstrumentId = svaWallet.getPaymentInstrumentId();
    // if (PortalUtils.exists(paymentInstrumentId)) {
    // List<BalanceAlert> balanceAlertList = getMobiliserWebSession()
    // .getBalanceAlertList();
    // for (BalanceAlert balAlert : balanceAlertList) {
    // if (!PortalUtils.exists(balAlert.getCountry())) {
    // balAlert.setCountry(null);
    // }
    // if (!PortalUtils.exists(balAlert.getLanguage())) {
    // balAlert.setLanguage(null);
    // }
    //
    // CreateBalanceAlertRequest balAlertReq = mobBasePage
    // .getNewMobiliserRequest(CreateBalanceAlertRequest.class);
    // balAlertReq.setBalanceAlert(balAlert);
    // balAlert.setPaymentInstrumentId(paymentInstrumentId);
    // CreateBalanceAlertResponse balAlertResp =
    // mobBasePage.wsBalanceAlertClient
    // .createBalanceAlert(balAlertReq);
    // if (!mobBasePage.evaluateMobiliserResponse(balAlertResp)) {
    // return;
    // }
    // LOG.info("# Successfully created Balance Alert data");
    // }
    // }
    // } catch (Exception e1) {
    // error(getLocalizer().getString("ERROR.CREATE_AGENT_FAILURE", this));
    // LOG.error("# An error occurred while creating balance alert data",
    // e1);
    // }
    //
    // }

    private void saveAgentLimitSettings() {
	try {

	    if (customer.getLimitClass() != null) {
		customer = mobBasePage.createAgentLimitSetting(customer);

	    }
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred whilte updating Limit setting data, with agent Id {}",
		    customer.getId(), e);
	    error(getLocalizer().getString("ERROR.CHANGE_AGENT_SETTINGS", this));
	}

    }
}
