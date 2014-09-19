package com.sybase365.mobiliser.web.btpn.agent.pages.portal.profile;

import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.DateValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.api.CustomerWrkFacade;
import com.btpnwow.core.customer.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.customer.facade.contract.GetCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.GetCustomerExResponse;
import com.btpnwow.core.customer.facade.contract.UpdateCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.UpdateCustomerExWrkResponse;
import com.btpnwow.portal.bank.converter.CustomerRegistrationBeanConverter;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentProfilePanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentPortalHomePage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.AgentPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.ConsumerPortalConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class AgentProfilePage extends BtpnBaseAgentPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(AgentProfilePanel.class);

	private FeedbackPanel feedBack;

	protected CustomerRegistrationBean customer;
	
	@SpringBean(name = "customerClient")
	private CustomerFacade customerClient;
	
	@SpringBean(name = "customerWrkFacade")
	protected CustomerWrkFacade customerWrkFacade;
	
	
	public AgentProfilePage() {
		addDateHeaderContributor();
		this.customer = getCustomerDetailsByCustomerId();
		constructPanel();
	}
	

	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.getLocalizer().getString("datepicker.chooseDate", this);
		final String locale = this.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER,
			chooseDtTxt)));
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

	/**
	 * Constructs the required components for the panel
	 */
	@SuppressWarnings("unchecked")
	protected void constructPanel() {

		final Form<AgentProfilePanel> form = new Form<AgentProfilePanel>("manageProfileForm",
			new CompoundPropertyModel<AgentProfilePanel>(this));
		
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		form.add(feedBack);

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		form.add(new RequiredTextField<String>("customer.name").setRequired(true)
			.add(new PatternValidator(BtpnConstants.REGISTRATION_DISPLAY_NAME_REGEX))
			.add(BtpnConstants.REGISTRATION_DISPLAY_NAME_MAX_LENGTH).add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.mothersMaidenName").setRequired(true)
			.add(new PatternValidator(BtpnConstants.REGISTRATION_MOTHER_MAIDEN_NAME_REGEX))
			.add(BtpnConstants.REGISTRATION_MOTHER_MAIDEN_NAME_MAX_LENGTH).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.nationality", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_NATIONALITY, this, Boolean.FALSE, true)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.language", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.receiptMode", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, this, Boolean.FALSE, true)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.job", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_JOB, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.purposeOfAccount", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.income", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_INCOME, this, Boolean.TRUE, false).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.highRiskCustomer", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_HIGH_RISK_CUSTOMER, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).setEnabled(false).add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.atmCardNo")
			.add(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_VALIDATOR)
			.add(new PatternValidator(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_REGEX))
			.add(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_LENGTH).add(new ErrorIndicator()));

		form.add(new TextField<String>("customer.emailId").setRequired(true)
			.add(new PatternValidator(BtpnConstants.EMAIL_ID_REGEX)).add(BtpnConstants.EMAIL_ID_MAX_LENGTH)
			.add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.gender", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_GENDERS, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.highRiskBusiness", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_HIGH_RISK_BUSINESS, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).setEnabled(false).add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.customerNumber"));

		form.add(new RequiredTextField<String>("customer.shortName").setRequired(true)
			.add(new PatternValidator(BtpnConstants.REGISTRATION_SHORT_NAME_REGEX))
			.add(BtpnConstants.REGISTRATION_SHORT_NAME_MAX_LENGTH).add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.employeeId")
			.add(new PatternValidator(BtpnConstants.REGISTRATION_EMPLOYEE_ID_REGEX))
			.add(BtpnConstants.REGISTRATION_EMPLOYEE_ID_MAX_LENGTH).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.maritalStatus", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.placeOfBirth").setRequired(true)
			.add(new PatternValidator(BtpnConstants.REGISTRATION_PLACE_OF_BIRTH_REGEX))
			.add(BtpnConstants.REGISTRATION_PLACE_OF_BIRTH_MAX_LENGTH).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.occupation", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_OCCUPATION, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.industryOfEmployee", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.sourceofFound", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.idCardNo").add(
			BtpnConstants.REGISTRATION_ID_CARD_NUMBER_LENGTH).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.idType", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_ID_TYPE, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(DateTextField.forDatePattern("customer.expireDateString", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
			.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN)).setRequired(true)
			.add(new ErrorIndicator()));
   
		form.add(DateTextField.forDatePattern("customer.birthDateString", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
			.add(DateValidator.maximum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN))
			.setRequired(true).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.taxExempted", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_TAX_EXEMPTED, this, Boolean.FALSE, true)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).setEnabled(false).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.optimaActivated", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_IS_OPTIMA_ACTIVATED, this, Boolean.FALSE, true)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).setEnabled(false).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.religion", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_RELIGION, this, Boolean.FALSE, true).setNullValid(true)
			.setChoiceRenderer(codeValueChoiceRender).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.lastEducation", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, this, Boolean.FALSE, true).setNullValid(true)
			.setChoiceRenderer(codeValueChoiceRender).add(new ErrorIndicator()));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.foreCastTransaction", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS, this, Boolean.TRUE, false).setNullValid(true)
			.setChoiceRenderer(codeValueChoiceRender).add(new ErrorIndicator()));

		form.add(new TextField<String>("customer.taxCardNumber").add(new PatternValidator("^[0-9]*$"))
			.add(new ErrorIndicator()).add(new SimpleAttributeModifier("maxlength", "30")));

		form.add(new TextField<String>("customer.mobileNumber").setRequired(true));

		form.add(new RequiredTextField<String>("customer.street1").setRequired(true)
			.add(new PatternValidator(BtpnConstants.REGISTRATION_STREET1_REGEX))
			.add(BtpnConstants.REGISTRATION_STREET1_MAX_LENGTH).add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.street2").setRequired(false)
			.add(new PatternValidator(BtpnConstants.REGISTRATION_STREET2_REGEX))
			.add(BtpnConstants.REGISTRATION_STREET2_MAX_LENGTH).add(new ErrorIndicator()));

		final BtpnLocalizableLookupDropDownChoice<CodeValue> cityDropdown;
		form.add(cityDropdown = (BtpnLocalizableLookupDropDownChoice<CodeValue>) new BtpnLocalizableLookupDropDownChoice<CodeValue>(
			"customer.city", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_CITIES, this, Boolean.FALSE, true)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		cityDropdown.setLookupName(customer.getProvince() != null ? customer.getProvince().getId() : null);
		cityDropdown.setOutputMarkupId(true);

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.province", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, this, Boolean.FALSE, true)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator())
			.add(new AjaxFormComponentUpdatingBehavior("onchange") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					cityDropdown.setLookupName(customer.getProvince().getId());
					target.addComponent(cityDropdown);
				}
			}));
		
		form.add(new TextField<String>("customer.zipCode").add(new ErrorIndicator()).add(
			new SimpleAttributeModifier("maxlength", "6")));

		form.add(new RequiredTextField<String>("customer.territoryCode", Model.of(customer.getTerritoryCode()))
			.setEnabled(false));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customer.customerStatus", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_CUSTOMER_STATUS, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setEnabled(false).add(new ErrorIndicator()));
		
		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		
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
		
		
		
		form.add(new TextField<String>("customer.marketingSourceCode")
				.setRequired(true)
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));
		
		form.add(new TextField<String>("customer.referralNumber").add(new ErrorIndicator()).add(
			new SimpleAttributeModifier("maxlength", "30")));
		
		form.add(new RequiredTextField<String>("customer.agentCode").add(new ErrorIndicator()).add(
			new SimpleAttributeModifier("maxlength", "30")));
		
		form.add(new TextField<String>("customer.productType.idAndValue").add(new ErrorIndicator()));

		
		// Submit Button
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				updateAgentProfile();
			}
		});

		// Cancel Button
		form.add(new Button("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleCancelButtonRedirectToHomePage(AgentPortalHomePage.class);
			};
		}.setDefaultFormProcessing(false));

		add(form);
	}

	
	private void updateAgentProfile() {
		
		BtpnCustomer loggedIn = this.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		try {
			
			String countryCode = this.getBankPortalPrefsConfig().getDefaultCountryCode();
			UpdateCustomerExWrkRequest request = MobiliserUtils.fill(new UpdateCustomerExWrkRequest(), this);
			request.setCallerId(Long.valueOf(loggedIn.getCustomerId()));
			request.setInformation(CustomerRegistrationBeanConverter.toContractWrk(
					customer,
					countryCode,
					loggedIn.getOrgUnitId(),
					customer.getParentId() >= 0 ? Long.valueOf(customer.getParentId()) : null));
			
			UpdateCustomerExWrkResponse response = customerWrkFacade.updateWrk(request);
			log.info(" ### (AgentProfilePage::updateAgentProfile) RESPONSE ### " +response.getStatus().getCode());
			if (MobiliserUtils.success(response)) {
				this.getMobiliserWebSession().info(getLocalizer().getString("customer.update.success.message", this));
			} else {
				this.getMobiliserWebSession().error(MobiliserUtils.errorMessage(response, this));
			}
			
		} catch (Exception ex) {
			log.error("Error occured while calling customerEditProfile service from Edit Profile Endpoint");
		}
	}
	
	
	public CustomerRegistrationBean getCustomerDetailsByCustomerId() {
		
		CustomerRegistrationBean custRegBean = new CustomerRegistrationBean();
		
		try {
			
			log.info(" ### (AgentProfilePage::getCustomerDetailsByCustomerId) ### ");
			
			String userName = getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (AgentProfilePage::getCustomerDetailsByCustomerId) USER NAME ### "+userName);
			
			final GetCustomerExRequest request = getNewMobiliserRequest(GetCustomerExRequest.class);
			CustomerIdentificationType obj = new CustomerIdentificationType();
			obj.setType(0);
			obj.setValue(userName);
			
			request.setFlags(0);
			request.setIdentification(obj);
			
			final GetCustomerExResponse response = customerClient.get(request);
			log.info(" ### (AgentProfilePage::getCustomerDetailsByCustomerId) RESPONSE ### " +response.getStatus().getCode());
			
			if (evaluateMobiliserResponse(response, AgentPortalApplicationLoginPage.class)) {
				custRegBean = ConsumerPortalConverterUtils.convertViewCustomertoCustomer(response,
					this.getLookupMapUtility(), this);
				custRegBean.setCustomerId(String.valueOf(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
				
			} else {
				error(getLocalizer().getString("error.customer.details", this));
			}

		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Error occured while calling viewCustomerProfile Service", e);
		}
		
		return custRegBean;
	}


	@SuppressWarnings("unchecked")
	public <Resp extends MobiliserResponseType> boolean evaluateMobiliserResponse(Resp response,
			Class<? extends Page> loginClass) {

		log.debug("# Response returned status: {}-{}", response.getStatus().getCode(), response.getStatus().getValue());
	
		if (response.getStatus().getCode() == 0) {
			return true;
		}
	
		// check for mobiliser session closed or expired
		if (response.getStatus().getCode() == 352 || response.getStatus().getCode() == 353) {
			log.debug("# Mobiliser session closed/expired, redirect to sign in page");
			// if mobiliser session gone, then can't continue with web session
			// so invalidate session and redirect to home, which will go to
			getMobiliserWebSession().invalidate();
			getRequestCycle().setRedirect(true);
			if (null != loginClass) {
	
				setResponsePage(getComponent(loginClass));
	
				// Set the mobiliser session expired and redirect it to login
				// page
				String errorMessage = null;
	
				errorMessage = getDisplayValue(String.valueOf(response.getStatus().getCode()),
					Constants.RESOURCE_BUNDLE_ERROR_CODES);
	
				if (PortalUtils.exists(errorMessage)) {
					getMobiliserWebSession().error(errorMessage);
				} else {
					getMobiliserWebSession().error(getLocalizer().getString("portal.genericError", this));
				}
			}
		}
	
		return false;
	}
}
