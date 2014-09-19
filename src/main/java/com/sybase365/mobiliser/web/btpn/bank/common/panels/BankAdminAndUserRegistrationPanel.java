package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.UserWrkFacade;
import com.btpnwow.core.customer.facade.contract.CreateInquiryUserWrkRequest;
import com.btpnwow.core.customer.facade.contract.CreateInquiryUserWrkResponse;
import com.btpnwow.portal.bank.converter.BankStaffBeanConverter;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankAdminRegistrationConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Registration Panel for registering Bank Admins and Users.
 * 
 * @author Andi Samallangi W
 */
public class BankAdminAndUserRegistrationPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(BankAdminAndUserRegistrationPanel.class);

	@SpringBean(name = "btpnLookupMapUtilitiesImpl")
	public ILookupMapUtility lookupMapUtility;

	@SpringBean(name = "userWrkFacade")
	private UserWrkFacade userWrkFacade;

	private FeedbackPanel feedBack;

	private WebMarkupContainer adminUserContainer;

	private BankStaffBean bankStaffBean;

	private BtpnMobiliserBasePage page;
	
	private boolean bankAdmin;
	
	public BankAdminAndUserRegistrationPanel(String id, BtpnMobiliserBasePage page, boolean bankAdmin) {
		super(id);
		
		this.page = page;
		
		this.bankAdmin = bankAdmin;
		
		// constructs the panel
		constructPanel();
	}

	/**
	 * This method constructs the panel components
	 */
	private void constructPanel() {
		// Add the wicket form
		final Form<BankAdminAndUserRegistrationPanel> form = new Form<BankAdminAndUserRegistrationPanel>(
				"registrationForm",
				new CompoundPropertyModel<BankAdminAndUserRegistrationPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		
		form.add(feedBack);

		// Choice Renderer
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);

		if (bankAdmin) {
			form.add(new Label("registration.legend.label", getLocalizer().getString("bankAdminRole.registration.legend.label", this)));
		} else {
			form.add(new Label("registration.legend.label", getLocalizer().getString("bankOfficerRole.registration.legend.label", this)));
		}

		// Roles
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"bankStaffBean.customerType", CodeValue.class,
				bankAdmin ? "bankAdminCustomerTypes" : "bankStaffCustomerTypes", this, Boolean.FALSE, true)
				.setNullValid(false)
				.setChoiceRenderer(codeValueChoiceRender)
				.setRequired(true)
				.setOutputMarkupId(true)
				.add(new ErrorIndicator()));

		// User id
		form.add(new TextField<String>("bankStaffBean.userId")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.BANK_STAFF_USER_ID_REGEX))
				.add(BtpnConstants.BANK_STAFF_USER_ID_MAX_LENGTH)
				.setOutputMarkupId(true)
				.add(new ErrorIndicator()));

		// E-mail
		form.add(new TextField<String>("bankStaffBean.email")
				.setRequired(false)
				.add(new PatternValidator(BtpnConstants.EMAIL_ID_REGEX))
				.add(BtpnConstants.EMAIL_ID_MAX_LENGTH)
				.setOutputMarkupId(true)
				.add(new ErrorIndicator()));

		// Designation
		form.add(new TextField<String>("bankStaffBean.designation")
				.setRequired(false)
				.add(BtpnConstants.BANK_STAFF_DESIGNATION_MAX_LENGTH)
				.setOutputMarkupId(true)
				.add(new ErrorIndicator()));

		// Language
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>(
					"bankStaffBean.language", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_BTPN_LANGUAGES, page)
				.setChoiceRenderer(codeValueChoiceRender)
				.setRequired(true)
				.setOutputMarkupId(true)
				.add(new ErrorIndicator()));

		// GL Code
		form.add(new Label("registration.glCode.label", getLocalizer().getString("registration.glCode.label", this))
				.setEscapeModelStrings(false)
				.setVisible(!bankAdmin));
		
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>(
					"bankStaffBean.glCode", CodeValue.class, "allGlCodes", page,
					Boolean.TRUE, true)
				.setChoiceRenderer(codeValueChoiceRender)
				.setRequired(false)
				.setOutputMarkupId(true)
				.add(new ErrorIndicator())
				.setVisible(!bankAdmin));

		// Territory code
		String territoryCode = page.getMobiliserWebSession().getBtpnLoggedInCustomer().getTerritoryCode();
		
		if (territoryCode == null) {
			form.add(new TextField<String>("bankStaffBean.territoryCode")
					.add(new PatternValidator(BtpnConstants.BANK_STAFF_TERRITORY_CODE_REGEX))
					.setRequired(false)
					.add(BtpnConstants.BANK_STAFF_TERRITORY_CODE_MAX_LENGTH)
					.setOutputMarkupId(true)
					.add(new ErrorIndicator()));
		} else {
			form.add(new TextField<String>("bankStaffBean.territoryCode", Model.of(territoryCode))
					.setRequired(false)
					.setEnabled(false)
					.setOutputMarkupId(true)
					.add(new ErrorIndicator()));
		}
		
		// Org Unit
		String orgUnitId = page.getMobiliserWebSession().getBtpnLoggedInCustomer().getOrgUnitId();
		
		form.add(new Label("registration.orgUnit.label", getLocalizer().getString("registration.orgUnit.label", this) + BtpnConstants.REQUIRED_SYMBOL)
				.setEscapeModelStrings(false)
				.setVisible(bankAdmin && (orgUnitId == null)));

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>(
					"bankStaffBean.orgUnit", CodeValue.class, "orgunits", page)
				.setChoiceRenderer(codeValueChoiceRender)
				.setRequired(true)
				.setOutputMarkupId(true)
				.add(new ErrorIndicator())
				.setVisible(bankAdmin && (orgUnitId == null)));

		adminUserContainer = new WebMarkupContainer("adminUserContainer");
		adminUserContainer.setOutputMarkupId(true);
		adminUserContainer.setOutputMarkupPlaceholderTag(true);
		adminUserContainer.setVisible(false);
		
		form.add(adminUserContainer);

		form.add(new Button("register") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (bankAdmin) {
					onRegisterBankAdminClick();
				} else {
					onRegisterBankStaffClick();
				}
			}
		});

		// Add Cancel button
		form.add(new Button("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				page.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			}
		}.setDefaultFormProcessing(false));

		add(form);
	}
	
	void onRegisterBankAdminClick() {
		if (!PortalUtils.exists(bankStaffBean)) {
			bankStaffBean = new BankStaffBean();
		}
		
		String territoryCode = page.getMobiliserWebSession().getBtpnLoggedInCustomer().getTerritoryCode();
		if (territoryCode != null) {
			bankStaffBean.setTerritoryCode(territoryCode);
		}
		
		String orgUnitId = page.getMobiliserWebSession().getBtpnLoggedInCustomer().getOrgUnitId();
		if (orgUnitId != null) {
			bankStaffBean.setOrgUnit(MobiliserUtils.getCodeValue("orgunits", orgUnitId, lookupMapUtility, this));
		}
		
		CreateInquiryUserWrkRequest request = new CreateInquiryUserWrkRequest();
		request.setInformation(BankStaffBeanConverter.toContractWrk(bankStaffBean, true));
		request.setNote(null);
		request.setCallerId(Long.valueOf(page.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
		
		CreateInquiryUserWrkResponse response;
		
		try {
			response = userWrkFacade.createInquiryWrk(request);
		} catch (Throwable ex) {
			LOG.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("error.exception", this));
			return;
		}
		
		if (MobiliserUtils.success(response)) {
			BankStaffBeanConverter.fromContractWrk(response.getInformation(), bankStaffBean);
			
			setResponsePage(new BankAdminRegistrationConfirmPage(bankStaffBean, bankAdmin));
		} else {
			error(MobiliserUtils.errorMessage(response, this));
		}
	}
 
	void onRegisterBankStaffClick() {
		if (!PortalUtils.exists(bankStaffBean)) {
			bankStaffBean = new BankStaffBean();
		}
		
		String territoryCode = page.getMobiliserWebSession().getBtpnLoggedInCustomer().getTerritoryCode();
		if (territoryCode != null) {
			bankStaffBean.setTerritoryCode(territoryCode);
		}
		
		String orgUnitId = page.getMobiliserWebSession().getBtpnLoggedInCustomer().getOrgUnitId();
		if (orgUnitId != null) {
			bankStaffBean.setOrgUnit(MobiliserUtils.getCodeValue("orgunits", orgUnitId, lookupMapUtility, this));
		}
		
		CreateInquiryUserWrkRequest request = new CreateInquiryUserWrkRequest();
		request.setInformation(BankStaffBeanConverter.toContractWrk(bankStaffBean, true));
		request.setNote(null);
		request.setCallerId(Long.valueOf(page.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
		
		CreateInquiryUserWrkResponse response;
		
		try {
			response = userWrkFacade.createInquiryWrk(request);
		} catch (Throwable ex) {
			LOG.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("error.exception", this));
			return;
		}
		
		if (MobiliserUtils.success(response)) {
			BankStaffBeanConverter.fromContractWrk(response.getInformation(), bankStaffBean);
			
			setResponsePage(new BankAdminRegistrationConfirmPage(bankStaffBean, bankAdmin));
		} else {
			error(MobiliserUtils.errorMessage(response, this));
		}
	}
}
