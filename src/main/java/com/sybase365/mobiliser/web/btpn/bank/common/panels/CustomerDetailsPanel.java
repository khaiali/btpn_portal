package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.DateValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerWrkFacade;
import com.btpnwow.core.customer.facade.contract.RemoveCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.RemoveCustomerExWrkResponse;
import com.btpnwow.core.customer.facade.contract.UpdateCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.UpdateCustomerExWrkResponse;
import com.btpnwow.portal.bank.converter.CustomerRegistrationBeanConverter;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.PinResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.ResetPinRequest;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

public class CustomerDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(CustomerDetailsPanel.class);

	@SpringBean(name = "customerWrkFacade")
	protected CustomerWrkFacade customerWrkFacade;

	protected BtpnMobiliserBasePage basePage;

	protected CustomerRegistrationBean customer;

	// CustomerSearchBean searchBean;

	// private String type;

	public CustomerDetailsPanel(String id, BtpnMobiliserBasePage basePage, CustomerRegistrationBean customer, String searchFor, String type) {
		super(id);
		
		this.basePage = basePage;
		this.customer = customer;
		
//		this.searchFor = searchFor;
//		this.type = type;
		
		//this.searchBean = searchBean;
		
		addDateHeaderContributor();
		
		constructPanel();
	}

	protected void addDateHeaderContributor() {
		final String chooseDtTxt = basePage.getLocalizer().getString("datepicker.chooseDate", basePage);
		final String locale = basePage.getLocale().getLanguage();
		
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER, chooseDtTxt)));
	}
	
	protected BtpnLocalizableLookupDropDownChoice<CodeValue> newCodeValueDropDown(String id, String lookupName, boolean required, IChoiceRenderer<CodeValue> renderer) {
		BtpnLocalizableLookupDropDownChoice<CodeValue> c =
				new BtpnLocalizableLookupDropDownChoice<CodeValue>(id, CodeValue.class, lookupName, this, Boolean.FALSE, true);
		
		c.setNullValid(!required);
		c.setChoiceRenderer(renderer);
		c.setRequired(required);
		c.add(new ErrorIndicator());
		
		return c;
	}

	protected void constructPanel() {
		final String customerTypeValue = customer.getCustomerType();
		
		final boolean agent = BtpnConstants.REG_CHILD_AGENT.equals(customerTypeValue) ||
				BtpnConstants.REG_SUB_AGENT.equals(customerTypeValue) ||
				BtpnConstants.REG_TOPUP_AGENT.equals(customerTypeValue);
		
		final Form<CustomerDetailsPanel> form = new Form<CustomerDetailsPanel>(
				"consumerRegistrationForm", new CompoundPropertyModel<CustomerDetailsPanel>(this));
		
		form.add(new FeedbackPanel("errorMessages"));

		String headerMessage = "";
		
		if (!agent) {
			headerMessage = getLocalizer().getString("customerInfo.headLine", this);
		} else {
			headerMessage = getLocalizer().getString("agentInfo.headLine", this);
		}
		
		form.add(new Label("customerInfo.headLine", headerMessage));

		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		form.add(new RequiredTextField<String>("customer.name")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_DISPLAY_NAME_REGEX))
				.add(BtpnConstants.REGISTRATION_DISPLAY_NAME_MAX_LENGTH)
				.add(new ErrorIndicator()));
		
		form.add(new RequiredTextField<String>("customer.shortName")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_SHORT_NAME_REGEX))
				.add(BtpnConstants.REGISTRATION_SHORT_NAME_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new TextField<String>("customer.employeeId")
				.setRequired(false)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_EMPLOYEE_ID_REGEX))
				.add(BtpnConstants.REGISTRATION_EMPLOYEE_ID_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.mothersMaidenName")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_MOTHER_MAIDEN_NAME_REGEX))
				.add(BtpnConstants.REGISTRATION_MOTHER_MAIDEN_NAME_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.maritalStatus", BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS, true, choiceRenderer));
		
		form.add(new RequiredTextField<String>("customer.placeOfBirth")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_PLACE_OF_BIRTH_REGEX))
				.add(BtpnConstants.REGISTRATION_PLACE_OF_BIRTH_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.nationality", BtpnConstants.RESOURCE_BUBDLE_NATIONALITY, true, choiceRenderer));

		form.add(newCodeValueDropDown("customer.language", BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, true, choiceRenderer));

		form.add(newCodeValueDropDown("customer.receiptMode", BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, true, choiceRenderer));

		form.add(newCodeValueDropDown("customer.occupation", BtpnConstants.RESOURCE_BUNDLE_OCCUPATION, true, choiceRenderer));
		
		form.add(newCodeValueDropDown("customer.job", BtpnConstants.RESOURCE_BUBDLE_JOB, true, choiceRenderer));

		form.add(newCodeValueDropDown("customer.industryOfEmployee", BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE, true, choiceRenderer));

		form.add(newCodeValueDropDown("customer.purposeOfAccount", BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT, true, choiceRenderer));
		
		form.add(newCodeValueDropDown("customer.sourceofFound", BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND, true, choiceRenderer));

		form.add(newCodeValueDropDown("customer.income", BtpnConstants.RESOURCE_BUBDLE_INCOME, true, choiceRenderer));

		form.add(new RequiredTextField<String>("customer.idCardNo")
				.add(BtpnConstants.REGISTRATION_ID_CARD_NUMBER_LENGTH)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.idType", BtpnConstants.RESOURCE_BUBDLE_ID_TYPE, true, choiceRenderer));

		form.add(DateTextField.forDatePattern("customer.expireDateString", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN))
				.setRequired(true)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.highRiskCustomer", BtpnConstants.RESOURCE_BUBDLE_HIGH_RISK_CUSTOMER, true, choiceRenderer));
		
		form.add(newCodeValueDropDown("customer.highRiskBusiness", BtpnConstants.RESOURCE_BUNDLE_HIGH_RISK_BUSINESS, true, choiceRenderer));

		form.add(new TextField<String>("customer.atmCardNo")
				.setRequired(false)
				.add(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_VALIDATOR)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_REGEX))
				.add(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_LENGTH)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.gender", BtpnConstants.RESOURCE_BUNDLE_GENDERS, true, choiceRenderer));

		form.add(new RequiredTextField<String>("customer.emailId")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.EMAIL_ID_REGEX))
				.add(BtpnConstants.EMAIL_ID_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new WebMarkupContainer("customerNumberInfo")
				.add(new RequiredTextField<String>("customer.customerNumber")
				.setEnabled(false)));
		
		form.add(createPurposeOfTransactionContainer(agent));

		form.add(DateTextField.forDatePattern("customer.birthDateString", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.add(DateValidator.maximum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN))
				.setRequired(true)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.taxExempted", BtpnConstants.RESOURCE_BUBDLE_TAX_EXEMPTED, true, choiceRenderer));
		
		form.add(new Label("customer.optimaActivated", customer.getOptimaActivated() == null ? "-" : customer.getOptimaActivated().getValue()));

		// Added as part of CR-32 : Start
		form.add(newCodeValueDropDown("customer.religion", BtpnConstants.RESOURCE_BUNDLE_RELIGION, false, choiceRenderer));
		
		form.add(newCodeValueDropDown("customer.lastEducation", BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, false, choiceRenderer));

		form.add(newCodeValueDropDown("customer.foreCastTransaction", BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS, false, choiceRenderer));

		form.add(new TextField<String>("customer.taxCardNumber")
				.add(new PatternValidator("^[0-9]*$"))
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));
		// Added as part of CR-32 : End

		form.add(new TextField<String>("customer.mobileNumber")
				.setRequired(true)
				.setEnabled(false));

		form.add(new RequiredTextField<String>("customer.street1")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_STREET1_REGEX))
				.add(BtpnConstants.REGISTRATION_STREET1_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new TextField<String>("customer.street2")
				.setRequired(false)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_STREET2_REGEX))
				.add(BtpnConstants.REGISTRATION_STREET2_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.territoryCode")
				.setEnabled(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getTerritoryCode() == null));

		final BtpnLocalizableLookupDropDownChoice<CodeValue> cityDropdown =
				newCodeValueDropDown("customer.city", BtpnConstants.RESOURCE_BUNDLE_CITIES, true, choiceRenderer);
		
		cityDropdown.setLookupName(customer.getProvince() != null ? customer.getProvince().getId() : null);
		cityDropdown.setOutputMarkupId(true);

		form.add(cityDropdown);
		
		// Added as part of CR-32 : Start
		form.add(newCodeValueDropDown("customer.province", BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, true, choiceRenderer)
				.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					
					private static final long serialVersionUID = 1L;

					protected void onUpdate(AjaxRequestTarget target) {
						cityDropdown.setLookupName(customer.getProvince().getId());
						
						target.addComponent(cityDropdown);
					}
				}));
		
		form.add(new TextField<String>("customer.zipCode")
				.add(new SimpleAttributeModifier("maxlength", "6"))
				.add(new ErrorIndicator()));
		// Added as part of CR-32 : End

		form.add(new RequiredTextField<String>("customer.marketingSourceCode")
				.setRequired(true)
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));
		
		form.add(new TextField<String>("customer.referralNumber")
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));
		
		form.add(new RequiredTextField<String>("customer.agentCode")
				.setRequired(true)
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));

		form.add(new TextField<String>("customer.productType.idAndValue").add(new ErrorIndicator()));

//		String customerStatus = BtpnConstants.CUSTOMER_STATUS_PENDING.equalsIgnoreCase(customer.getStatus()) ?
//				BtpnConstants.RESOURCE_BUNDLE_PENDING_CUST_STATUS :
//					BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS;
//		
//		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.customerStatus", CodeValue.class,
//			customerStatus, this, Boolean.FALSE, true).setNullValid(false).setChoiceRenderer(codeValueChoiceRender)
//			.add(new ErrorIndicator()));

		Component blacklistReason = newCodeValueDropDown("customer.blackListReason", "blackListReasons", true, choiceRenderer);
		
		WebMarkupContainer blackListReasonDiv = new WebMarkupContainer("blackListReasonDiv");
		blackListReasonDiv.setOutputMarkupId(true);
		blackListReasonDiv.setOutputMarkupPlaceholderTag(true);
		blackListReasonDiv.setVisible(true);
		
		blackListReasonDiv.add(blacklistReason);
		
		boolean updatable;
		
		switch (customer.getBlackListReson()) {
		case 5:
		case 6:
		case 7:
		case 9:
			blacklistReason.setEnabled(updatable = false);
			break;
		case 11:
			updatable = true;
			blacklistReason.setEnabled(false);
			break;
		default:
			blacklistReason.setEnabled(updatable = true);
			break;
		}
		
		form.add(blackListReasonDiv);

		Button updateButton = addSubmitButton();
		Button cancelButton = addCancelButton();
		
		form.add(new SimpleAttributeModifier("onkeydown", "keyEnter(" + updateButton.getMarkupId(true) + ")"));

		form.add(updateButton);
		form.add(cancelButton);

		final boolean showResetPin = basePage.getMobiliserWebSession().getBtpnRoles().hasRole(BtpnConstants.RESET_PIN_PRIV);

		form.add(new Label("label.pin", getLocalizer().getString("reset.pin", this))
				.setVisible(showResetPin));
		
		form.add(new Button("resetPinButton") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				// FIXME 
				try {
					final ResetPinRequest request = basePage.getNewMobiliserRequest(ResetPinRequest.class);
					request.setMsisdn(customer.getMobileNumber());
					PinResponse resetPinResponse = basePage.getSupportClient().resetPin(request);
					if (basePage.evaluateBankPortalMobiliserResponse(resetPinResponse)) {
						info(this.getLocalizer().getString("reset.pin.success", CustomerDetailsPanel.this));
					} else {
						error(this.getLocalizer().getString("reset.pin.fail", CustomerDetailsPanel.this));
					}
				} catch (Exception ex) {
					LOG.error("#An error occurred while calling resetPin service", ex);
				}
			};
		}
		.setDefaultFormProcessing(false)
		.setVisible(showResetPin));
		
		if (!updatable) {
			for (int i = 0, n = form.size(); i < n; ++i) {
				form.get(i).setEnabled(false);
			}
			
			updateButton.setVisible(false);
			cancelButton.setEnabled(true);
		}

		add(form);
	}

	/**
	 * This method adds the Purpose of Transaction Container only for all Agent registrations
	 */
	private WebMarkupContainer createPurposeOfTransactionContainer(boolean agent) {
		WebMarkupContainer purpOfTrans = new WebMarkupContainer("purposeOfTransactionBlock");
		
		purpOfTrans.add(new RequiredTextField<String>("customer.purposeOfTransaction")
				.add(BtpnConstants.REGISTRATION_PURPOSE_OF_TRANSACTION_LENGTH)
				.add(new ErrorIndicator()));
		
		purpOfTrans.setVisible(agent);
		
//		WebMarkupContainer purpOfTrans = new WebMarkupContainer("purposeOfTransactionBlock") {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public boolean isVisible() {
//				return type == null ? false : !type.equals("customer");
//			}
//		};
//		purpOfTrans.add(new RequiredTextField<String>("customer.purposeOfTransaction").add(
//			BtpnConstants.REGISTRATION_PURPOSE_OF_TRANSACTION_LENGTH).add(new ErrorIndicator()));
		
		return purpOfTrans;
	}

	/**
	 * This method adds the submit button for the Registration Panel
	 */
	protected Button addSubmitButton() {
		Button submitButton = new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if ((customer.getBlackListReason() != null) && "9".equals(customer.getBlackListReason().getId())) {
					closeCustomer();
				} else {
					updateCustomerProfile();
				}
			}
		};
		
		return submitButton;
	}

	/**
	 * This method adds the cancel button for the Registration Panel
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			}
		};
		
		cancelButton.setDefaultFormProcessing(false);
		
		return cancelButton;
	}

	public void closeCustomer() {
		BtpnCustomer loggedIn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		try {
			RemoveCustomerExWrkRequest request = MobiliserUtils.fill(new RemoveCustomerExWrkRequest(), basePage);
			request.setCallerId(Long.valueOf(loggedIn.getCustomerId()));
			request.setId(Long.parseLong(customer.getCustomerId()));
			
			RemoveCustomerExWrkResponse response = customerWrkFacade.removeWrk(request);
			
			if (MobiliserUtils.success(response)) {
				basePage.getMobiliserWebSession().info(getLocalizer().getString("customer.remove.success.message", this));
			} else {
				basePage.getMobiliserWebSession().error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Exception ex) {
			LOG.error("An exception was thrown", ex);
			
			basePage.getMobiliserWebSession().error(getLocalizer().getString("customer.remove.failure.message", this));
		}
	}

	public void updateCustomerProfile() {
		BtpnCustomer loggedIn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		try {
			String countryCode = basePage.getBankPortalPrefsConfig().getDefaultCountryCode();
			
			UpdateCustomerExWrkRequest request = MobiliserUtils.fill(new UpdateCustomerExWrkRequest(), basePage);
			request.setCallerId(Long.valueOf(loggedIn.getCustomerId()));
			request.setInformation(CustomerRegistrationBeanConverter.toContractWrk(
					customer,
					countryCode,
					loggedIn.getOrgUnitId(),
					customer.getParentId() >= 0 ? Long.valueOf(customer.getParentId()) : null));
			
			UpdateCustomerExWrkResponse response = customerWrkFacade.updateWrk(request);
			
			if (MobiliserUtils.success(response)) {
				basePage.getMobiliserWebSession().info(getLocalizer().getString("customer.update.success.message", this));
			} else {
				basePage.getMobiliserWebSession().error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Exception ex) {
			LOG.error("An exception was thrown", ex);
			
			basePage.getMobiliserWebSession().error(getLocalizer().getString("customer.update.failure.message", this));
		}
	}

}
