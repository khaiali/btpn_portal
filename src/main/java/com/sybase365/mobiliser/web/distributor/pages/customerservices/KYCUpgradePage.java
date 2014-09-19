package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateAddressRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateAddressResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateIdentityRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateIdentityResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateAddressRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateAddressResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateIdentityRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateIdentityResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identity;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class KYCUpgradePage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;
    private FeedbackPanel kycFeedBackPanel;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(KYCUpgradePage.class);

    public KYCUpgradePage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public KYCUpgradePage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	final String chooseDtTxt = this.getLocalizer().getString(
		"datepicker.chooseDate", this);

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
			+ "  $('#expirationDate').datepicker( { \n"
			+ "	'buttonText' : '" + chooseDtTxt + "', \n"
			+ "	'changeMonth' : true, \n"
			+ "	'changeYear' : true, \n" + "	'showOn': 'both', \n"
			+ "	'dateFormat' : '"
			+ Constants.DATE_FORMAT_PATTERN_PICKER + "', \n"
			+ "	'buttonImage': 'images/calendar.gif', \n"
			+ "	'buttonOnlyImage': true} ); \n" + "});\n",
			"datePicker");
	    }
	}));

	final Form<?> form = new Form("kyUpgradeForm",
		new CompoundPropertyModel<KYCUpgradePage>(this));
	kycFeedBackPanel = new FeedbackPanel("errorMessages");
	form.add(kycFeedBackPanel);

	form.add(new Label("customer.msisdn"));

	form.add(
		new RequiredTextField<String>("customer.address.firstName")
			.setRequired(false)
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new RequiredTextField<String>("customer.address.lastName")
			.setRequired(false)
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(new RequiredTextField<String>("customer.address.street1")
		.setRequired(false).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new TextField<String>("customer.address.street2")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new RequiredTextField<String>("customer.address.zip")
		.setRequired(false).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new RequiredTextField<String>("customer.address.city")
		.setRequired(false).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new RequiredTextField<String>("customer.address.state")
		.setRequired(false).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier));
	form.add(new LocalizableLookupDropDownChoice<String>(
		"customer.address.kvCountry", String.class, "countries", this,
		false, true).setNullValid(true).setRequired(false)
		.setEnabled(true));

	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"customer.kvIdentityType", Integer.class, "identityTypes",
		this, false, true).setNullValid(true).setRequired(false)
		.setEnabled(true));

	form.add(new TextField<String>("customer.identityValue")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("customer.issuer")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	DateTextField dateTextField = new DateTextField("expirationDate",
		new PropertyModel<Date>(this, "customer.expirationDate"),
		new PatternDateConverter(Constants.DATE_FORMAT_PATTERN_PARSE,
			false));
	form.add(dateTextField.add(new ErrorIndicator()));
	List<Integer> keyList = null;
	try {
	    keyList = getKeysFromPreferences(
		    Constants.RESOURCE_BUNDLE_RISK_CATEGORIES, Integer.class);
	} catch (Exception e) {
	    LOG.error(
		    "An error occurred in loading the key lisy of kyc levels from preferences",
		    e);
	}
	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"customer.kvKycLevel", Integer.class,
		Constants.RESOURCE_BUNDLE_RISK_CATEGORIES, this, Boolean.TRUE,
		true, keyList).setRequired(true).setEnabled(true));
	form.add(new CheckBox("customer.verified"));
	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		// no-op = redirect to own page
		// NOT NO-OP - what is the point of a cancel button (or
		// any button if it doesn't do anything!!!) - so redirect to
		// customer details view page
		setResponsePage(CustomerDetailsPage.class);
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		validateOnSave();
		if (Session.get().getFeedbackMessages()
			.hasErrorMessageFor(getPage())) {
		    return;
		} else {
		    handleUpdate();
		}
	    };
	}.setMarkupId("save"));

	add(form);

	add(new AttachmentsPanel("attachments.panel", getCustomer().getId(),
		this, kycFeedBackPanel));
    }

    public void handleUpdate() {
	// updating customer details
	getCustomer().setRiskCategoryId(getCustomer().getKvKycLevel());
	getCustomer().setDisplayName(
		createDisplayName(getCustomer().getAddress().getFirstName(),
			getCustomer().getAddress().getLastName()));
	Customer customer = Converter.getInstance(getConfiguration())
		.getCustomerFromCustomerBean(getCustomer());
	customer.setId(getMobiliserWebSession().getCustomer().getId());
	UpdateCustomerRequest updCustReq;
	try {
	    updCustReq = getNewMobiliserRequest(UpdateCustomerRequest.class);
	    updCustReq.setCustomer(customer);
	    UpdateCustomerResponse updCustRes = wsCustomerClient
		    .updateCustomer(updCustReq);
	    if (!evaluateMobiliserResponse(updCustRes)) {
		return;
	    } else {
		getCustomer().setRiskCategoryId(getCustomer().getKvKycLevel());
		LOG.info("The customer details have been updated");
	    }
	} catch (Exception e) {
	    LOG.error("# The customer details update encountered a problem", e);
	    error(getLocalizer().getString("update.customer.error", this));
	    return;
	}

	// Updating customer address
	Address address;
	if (getCustomer().getAddress() == null) {
	    address = new Address();
	    address.setId(getMobiliserWebSession().getCustomer().getAddress()
		    .getId());
	} else {
	    address = Converter.getInstance().getAddressFromAddressBean(
		    getCustomer().getAddress());
	    address.setCustomerId(getMobiliserWebSession().getCustomer()
		    .getId());
	}
	try {

	    if (address.getId() != null) {
		UpdateAddressRequest updAddReq = getNewMobiliserRequest(UpdateAddressRequest.class);
		updAddReq.setAddress(address);
		UpdateAddressResponse updAddRes = wsAddressClient
			.updateAddress(updAddReq);
		if (!evaluateMobiliserResponse(updAddRes))
		    return;
		LOG.info("The customer address details have been updated");
	    } else {
		CreateAddressRequest req = getNewMobiliserRequest(CreateAddressRequest.class);
		req.setAddress(address);
		CreateAddressResponse res = wsAddressClient.createAddress(req);
		if (!evaluateMobiliserResponse(res))
		    return;
		getMobiliserWebSession().getCustomer().getAddress()
			.setId(res.getAddressId());
	    }
	} catch (Exception e) {
	    LOG.error(
		    "# The customer address details update encountered a problem",
		    e);
	    error(getLocalizer().getString("update.customer.error", this));
	    return;
	}

	// Updating customer identity
	Identity identity = new Identity();
	if (getCustomer().getKvIdentityType() != null) {
	    identity.setIdentityType(getCustomer().getKvIdentityType());
	    identity.setCustomerId(getMobiliserWebSession().getCustomer()
		    .getId());
	    identity.setIdentity(getCustomer().getIdentityValue());
	    identity.setIssuer(getCustomer().getIssuer());
	    identity.setActive(true);
	    // Have to add expiration date in identity
	    if (getCustomer().getExpirationDate() != null) {
		try {
		    XMLGregorianCalendar expiryDate = PortalUtils
			    .getSaveXMLGregorianCalendarFromDate(getCustomer()
				    .getExpirationDate(),
				    getMobiliserWebSession().getTimeZone());
		    identity.setDateExpires(expiryDate);
		} catch (Exception e) {
		    LOG.error(
			    "# An error occurred while evaluating expiry date for the customer's[{}] identity",
			    getMobiliserWebSession().getCustomer().getId(), e);
		    error(getLocalizer().getString("update.customer.error",
			    this));
		    return;
		}

	    }
	    if (getMobiliserWebSession().getCustomer().getIdentityId() != null) {
		identity.setId(Long.valueOf(getMobiliserWebSession()
			.getCustomer().getIdentityId()));
		UpdateIdentityRequest updIdenReq;
		try {
		    updIdenReq = getNewMobiliserRequest(UpdateIdentityRequest.class);
		    updIdenReq.setIdentity(identity);
		    UpdateIdentityResponse updIdenRes = wsIdentityClient
			    .updateIdentity(updIdenReq);
		    if (!evaluateMobiliserResponse(updIdenRes))
			return;
		    LOG.info("The customer identity details have been updated");
		} catch (Exception e) {
		    LOG.error(
			    "# An error occurred while uploading the customer's[{}] identity",
			    getMobiliserWebSession().getCustomer().getId(), e);
		    error(getLocalizer().getString("update.customer.error",
			    this));
		    return;
		}
	    } else {
		try {
		    CreateIdentityRequest createIdentityReq = getNewMobiliserRequest(CreateIdentityRequest.class);
		    createIdentityReq.setIdentity(identity);
		    CreateIdentityResponse createIdentityRes = wsIdentityClient
			    .createIdenity(createIdentityReq);
		    if (!evaluateMobiliserResponse(createIdentityRes))
			return;
		    LOG.info("The customer identity details have been updated");

		} catch (Exception e) {
		    LOG.error("# Error occurred while creating customer's identity details.");
		    error(getLocalizer().getString("update.customer.error",
			    this));
		    return;
		}
	    }

	}
	getCustomer().setVerified(false);
	info(getLocalizer().getString("kyc.details.updated", this));

    }

    public void validateOnSave() {

	int kycLevel = getCustomer().getKvKycLevel();
	if (kycLevel < getCustomer().getRiskCategoryId()) {
	    error(getLocalizer().getString("error.kyc.decreas", this));
	} else if (kycLevel > 0
		&& !getMobiliserWebSession().hasPrivilege(
			Constants.PRIV_KYC_LEVEL_UPGRADE_PREFIX + kycLevel)) {
	    error(getLocalizer().getString("error.kyc.privilege", this));
	} else {
	    // first and last name required for all kyc levels except 0 and 1
	    if (kycLevel != Constants.KYC_LEVEL_0
		    && kycLevel != Constants.KYC_LEVEL_1) {

		if (getCustomer().getAddress().getFirstName() == null) {
		    error(getLocalizer().getString(
			    "customer.address.firstName.Required", this));
		}

		if (getCustomer().getAddress().getLastName() == null) {
		    error(getLocalizer().getString(
			    "customer.address.lastName.Required", this));
		}
	    }
	    // Address info required for kyc levels 3,4,6,7
	    if (kycLevel == Constants.KYC_LEVEL_3
		    || kycLevel == Constants.KYC_LEVEL_4
		    || kycLevel == Constants.KYC_LEVEL_6
		    || kycLevel == Constants.KYC_LEVEL_7) {

		if (getCustomer().getAddress().getStreet1() == null) {
		    error(getLocalizer().getString(
			    "customer.address.street1.Required", this));
		}

		if (getCustomer().getAddress().getCity() == null) {
		    error(getLocalizer().getString(
			    "customer.address.city.Required", this));
		}
		if (getCustomer().getAddress().getKvCountry() == null) {
		    error(getLocalizer().getString(
			    "customer.address.kvCountry.Required", this));
		}
		if (getCustomer().getAddress().getZip() == null) {
		    error(getLocalizer().getString(
			    "customer.address.zip.Required", this));
		}
	    }

	    // Identity required for kyc levels 5,6,7
	    if (kycLevel == Constants.KYC_LEVEL_5
		    || kycLevel == Constants.KYC_LEVEL_6
		    || kycLevel == Constants.KYC_LEVEL_7) {
		if (getCustomer().getKvIdentityType() == null) {
		    error(getLocalizer().getString(
			    "customer.kvIdentityType.Required", this));
		}

		if (getCustomer().getIdentityValue() == null) {
		    error(getLocalizer().getString(
			    "customer.identityValue.Required", this));
		}

		if (getCustomer().getIssuer() == null) {
		    error(getLocalizer().getString("customer.issuer.Required",
			    this));
		}
		if (getCustomer().getExpirationDate() == null) {
		    error(getLocalizer().getString(
			    "customer.expirationDate.Required", this));

		}
	    } else {
		if (getCustomer().getExpirationDate() != null
			|| getCustomer().getIssuer() != null) {

		    if (getCustomer().getIdentityValue() == null) {
			error(getLocalizer().getString(
				"customer.identityValue.Required", this));
		    }
		    if (getCustomer().getKvIdentityType() == null) {
			error(getLocalizer().getString(
				"customer.kvIdentityType.Required", this));
		    }
		} else {

		    if (getCustomer().getKvIdentityType() != null
			    && getCustomer().getIdentityValue() == null) {
			error(getLocalizer().getString(
				"customer.identityValue.Required", this));
		    } else if (getCustomer().getIdentityValue() != null
			    && getCustomer().getKvIdentityType() == null) {
			error(getLocalizer().getString(
				"customer.kvIdentityType.Required", this));

		    }
		}

	    }
	    //
	    // Attachment required for kyc levels 7
	    if (kycLevel == Constants.KYC_LEVEL_7) {
	    }
	    // if (!PortalUtils.exists(getCustomer().getAttach)) {
	    // error(getLocalizer().getString("required.attachment", this));
	    // }
	}

    }

    public void setCustomer(CustomerBean customer) {
	getMobiliserWebSession().setCustomer(customer);
    }

    public CustomerBean getCustomer() {
	return getMobiliserWebSession().getCustomer();
    }

}
