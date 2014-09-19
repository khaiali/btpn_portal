package com.sybase365.mobiliser.web.btpn.consumer.common.panels;


import java.util.Date;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.DateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.schedtxn.facade.api.SchedTxnFacade;
import com.btpnwow.core.schedtxn.facade.contract.AddSchedTxnRequest;
import com.btpnwow.core.schedtxn.facade.contract.AddSchedTxnResponse;
import com.btpnwow.core.schedtxn.facade.contract.MapEntryType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.StandingInstructionsPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class ConfirmFundTransferPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ConfirmFundTransferPanel.class);

	protected BtpnMobiliserBasePage basePage;
	protected FundTransferBean fundTransferBean;

	private String filterType;
	private String frequencyType;
	String ftAccountType;
	String ftAccountTypes;
	private boolean isMobileTransfer = false;
	
	@SpringBean(name="siClient")
	private SchedTxnFacade siClient;
	

	public ConfirmFundTransferPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		FundTransferBean fundTransferBean, String ftAccountTypes) {
		super(id);
		this.basePage = basePage;
		this.fundTransferBean = fundTransferBean;
		this.ftAccountType = fundTransferBean.getAccountType().getId();
		this.ftAccountTypes = ftAccountTypes;
		constructPanel();
		addDateHeaderContributor();
	}

	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		
		final String chooseDtTxt = this.basePage.getLocalizer().getString("datepicker.chooseDate", basePage);
		final String locale = this.basePage.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER,
			chooseDtTxt)));
	}

	protected void constructPanel() {
		
		final Form<ConfirmFundTransferPanel> form = new Form<ConfirmFundTransferPanel>("confirmFundTransferForm",
			new CompoundPropertyModel<ConfirmFundTransferPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		String headerMessage = "";
		if (ftAccountType.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)) {
			headerMessage = ConfirmFundTransferPanel.this.getLocalizer().getString(
				"header.fundTransferBtpnBankAccount", ConfirmFundTransferPanel.this);
		} else if (ftAccountType.equals(BtpnConstants.FT_ACCOUNT_TYPE_OTHER_BANK_ACCOUNT)) {
			headerMessage = ConfirmFundTransferPanel.this.getLocalizer().getString(
				"header.fundTransferOtherBankAccount", ConfirmFundTransferPanel.this);
		} else {
			headerMessage = getLocalizer().getString("header.fundTransferMobileToMobile", this);
			isMobileTransfer = true;
		}
		
		form.add(new Label("headerMessage", headerMessage));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		final String labelName = isMobileTransfer ? "label.mobileNum" : "label.beneficiaryAccountNum";
		form.add(new Label("label.beneficiaryAccountNum", getLocalizer().getString(labelName, this)));
		form.add(new Label("fundTransferBean.payeeMsisdn").setVisible(isMobileTransfer));
		form.add(new Label("fundTransferBean.accountNo").setVisible(!isMobileTransfer));

		form.add(new AmountLabel("fundTransferBean.amount"));
		form.add(new TextField<String>("fundTransferBean.siName").setRequired(true).add(new ErrorIndicator()));

		form.add(DateTextField.forDatePattern("fundTransferBean.startDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
			.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN)).setRequired(true)
			.add(new ErrorIndicator()));

		form.add(DateTextField.forDatePattern("fundTransferBean.expiryDate", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
			.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN)).setRequired(true)
			.add(new ErrorIndicator()));

		RadioGroup<ConfirmFundTransferPanel> frequencyRadioGroup = new RadioGroup<ConfirmFundTransferPanel>(
			"frequencyRadioGroup", new PropertyModel<ConfirmFundTransferPanel>(this, "frequencyType"));
		frequencyRadioGroup.setRequired(true).add(new ErrorIndicator());

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<ConfirmFundTransferPanel> frequencyMonth = new Radio<ConfirmFundTransferPanel>("frequencyMonth",
			new Model(BtpnConstants.FREQUENCY_TYPE_MONTH));
		frequencyMonth.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyMonth);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<ConfirmFundTransferPanel> frequencyQuarter = new Radio<ConfirmFundTransferPanel>(
			"frequencyQuarter", new Model(BtpnConstants.FREQUENCY_TYPE_QUARTER));
		frequencyQuarter.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyQuarter);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<ConfirmFundTransferPanel> frequencyWeek = new Radio<ConfirmFundTransferPanel>("frequencyWeek",
			new Model(BtpnConstants.FREQUENCY_TYPE_WEEK));
		frequencyWeek.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyWeek);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Radio<ConfirmFundTransferPanel> frequencyDate = new Radio<ConfirmFundTransferPanel>("frequencyDate",
			new Model(BtpnConstants.FREQUENCY_TYPE_DAILY));
		frequencyDate.setOutputMarkupId(true);
		frequencyRadioGroup.add(frequencyDate);

		form.add(frequencyRadioGroup);

		final WebMarkupContainer selectedDayContainer = new WebMarkupContainer("selectedDayContainer");
		selectedDayContainer.setOutputMarkupId(true);
		selectedDayContainer.setOutputMarkupPlaceholderTag(true);

		selectedDayContainer.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("fundTransferBean.selectedDay",
			CodeValue.class, BtpnConstants.RESOURCE_BUBDLE_SELECTED_DAY, this, Boolean.FALSE, Boolean.TRUE)
			.setNullValid(false).setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		selectedDayContainer.setVisible(true);

		form.add(selectedDayContainer);

		frequencyMonth.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				fundTransferBean.setFrequencyType(frequencyMonth.getDefaultModelObjectAsString());
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyQuarter.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				fundTransferBean.setFrequencyType(frequencyQuarter.getDefaultModelObjectAsString());
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyWeek.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				fundTransferBean.setFrequencyType(frequencyWeek.getDefaultModelObjectAsString());
				selectedDayContainer.setVisible(true);
				target.addComponent(selectedDayContainer);
			}
		});
		
		frequencyDate.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				fundTransferBean.setFrequencyType(frequencyDate.getDefaultModelObjectAsString());
				selectedDayContainer.setVisible(false);
				target.addComponent(selectedDayContainer);
			}
		});

		form.add(new Button("confirmButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				Date startDate = fundTransferBean.getStartDate();
				Date expiryDate = fundTransferBean.getExpiryDate();
				if (expiryDate.before(startDate)) {
					error(getLocalizer().getString("invalid.expiry.date", this));
					return;
				}
				
				if (ftAccountTypes.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)){
					createPreFTtoBTPNBankRequest(fundTransferBean);
				} else if (ftAccountTypes.equals(BtpnConstants.FT_ACCOUNT_TYPE_OTHER_BANK_ACCOUNT)){
					createPreFTtoOtherBTPNBankRequest(fundTransferBean);
				} else {
					createPreFTtoReqMobileRequest(fundTransferBean);
				}

			};
		});

		add(form);
	}

	
	
	private void createPreFTtoBTPNBankRequest(FundTransferBean bean) {
		
		try {
			
			final AddSchedTxnRequest request = basePage.getNewMobiliserRequest(AddSchedTxnRequest.class);
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			
			String bankCode = "";
			if (PortalUtils.exists(bean.getBankCode())) {
				bankCode = bean.getBankCode().getId();
			}
			
			request.setCustomerId(customerId);
			request.setProcessingCode("100901");
			request.setDescription(bean.getSiName());
			request.setBeneficiaryNo(PortalUtils.exists(bean.getAccountNo()) ? bean.getAccountNo() : bean.getFavoriteNum().getId());
			request.setStartDate(PortalUtils.getSaveXMLGregorianCalendarFromDate(bean.getStartDate(), null));
			request.setFrequency(bean.getFrequencyType());
			request.setEndDate(PortalUtils.getSaveXMLGregorianCalendarToDate(bean.getExpiryDate(), null));
			request.setAmount(fundTransferBean.getAmount()/100);
			
			MapEntryType amount = new MapEntryType();
			amount.setKey("amount");
			amount.setValue(String.valueOf(fundTransferBean.getAmount()/100));
			
//			MapEntryType debitIdentifier = new MapEntryType();
//			debitIdentifier.setKey("debitIdentifier");
//			debitIdentifier.setValue("");
			
			MapEntryType description = new MapEntryType();
			description.setKey("description");
			description.setValue("FUND TRANSFER TO BTPN BANK");
			
			MapEntryType beneficiaryBankCode = new MapEntryType();
			beneficiaryBankCode.setKey("beneficiaryBankCode");
			beneficiaryBankCode.setValue(bankCode);
			
			request.getParameter().add(amount);
//			request.getParameter().add(debitIdentifier);
			request.getParameter().add(description);
			request.getParameter().add(beneficiaryBankCode);
			request.setFlags(0);
			
			AddSchedTxnResponse response = new AddSchedTxnResponse();
			response = siClient.add(request);
			
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				basePage.getWebSession().info(getLocalizer().getString("si.ft.add.successMessage", this));
				setResponsePage(new StandingInstructionsPage());
			} else {
				error(response.getStatus().getValue());
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
		}
		
	}
	
	
	private void createPreFTtoOtherBTPNBankRequest(FundTransferBean bean) {

		try {
			
			final AddSchedTxnRequest request = basePage.getNewMobiliserRequest(AddSchedTxnRequest.class);
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			
			String PROCESSING_CODE = "";  
			String benefBankCode = "";
			    
			if (PortalUtils.exists(bean.getBankCode())) {
				String bankCode = bean.getBankCode().getId();
				String code = bankCode.substring(0, 1);
				benefBankCode = bankCode.substring(1, 4);

				if (code.equalsIgnoreCase("B")) {
					PROCESSING_CODE = "101001";
				}

				if (code.equalsIgnoreCase("P")) {
					PROCESSING_CODE = "101101";
				}
			}

			request.setCustomerId(customerId);
			request.setProcessingCode(PROCESSING_CODE);
			request.setDescription(bean.getSiName());
			request.setBeneficiaryNo(PortalUtils.exists(bean.getAccountNo()) ? bean.getAccountNo() : bean.getFavoriteNum().getId());
			request.setStartDate(PortalUtils.getSaveXMLGregorianCalendarFromDate(bean.getStartDate(), null));
			request.setFrequency(bean.getFrequencyType());
			request.setEndDate(PortalUtils.getSaveXMLGregorianCalendarToDate(bean.getExpiryDate(), null));
			request.setAmount(fundTransferBean.getAmount()/100);

			MapEntryType amount = new MapEntryType();
			amount.setKey("amount");
			amount.setValue(String.valueOf(fundTransferBean.getAmount()/100));

//			MapEntryType debitIdentifier = new MapEntryType();
//			debitIdentifier.setKey("debitIdentifier");
//			debitIdentifier.setValue("");

			MapEntryType description = new MapEntryType();
			description.setKey("description");
			description.setValue("FUND TRANSFER TO OTHER BANK");

			MapEntryType beneficiaryBankCode = new MapEntryType();
			beneficiaryBankCode.setKey("beneficiaryBankCode");
			beneficiaryBankCode.setValue(benefBankCode);

			request.getParameter().add(amount);
//			request.getParameter().add(debitIdentifier);
			request.getParameter().add(description);
			request.getParameter().add(beneficiaryBankCode);
			request.setFlags(0);

			AddSchedTxnResponse response = new AddSchedTxnResponse();
			response = siClient.add(request);
			log.info("### (ConfirmFundTransferPanel::createPreFTtoOtherBTPNBankRequest) RESPONSE ### " +response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				basePage.getWebSession().info(getLocalizer().getString("si.ft.add.successMessage", this));
				setResponsePage(new StandingInstructionsPage());
			} else {
				error(response.getStatus().getValue());
			}

		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
		}
	}
	
	
	private void createPreFTtoReqMobileRequest(FundTransferBean bean) {

		try {

			final AddSchedTxnRequest request = basePage.getNewMobiliserRequest(AddSchedTxnRequest.class);
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			String userLogin = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			
			final PhoneNumber phoneNumber = new PhoneNumber(userLogin, basePage
					.getAgentPortalPrefsConfig().getDefaultCountryCode());
			
			request.setCustomerId(customerId);
			request.setProcessingCode("100801");
			request.setDescription(bean.getSiName());
			request.setBeneficiaryNo(PortalUtils.exists(bean.getPayeeMsisdn()) ? bean
					.getPayeeMsisdn() : bean.getFavoriteNum().getId());
			request.setStartDate(PortalUtils.getSaveXMLGregorianCalendarFromDate(bean.getStartDate(), null));
			request.setFrequency(bean.getFrequencyType());
			request.setEndDate(PortalUtils.getSaveXMLGregorianCalendarToDate(bean.getExpiryDate(), null));
			request.setCurrencyCode("IDR");
			request.setAmount(fundTransferBean.getAmount());
			
			MapEntryType acquirerId = new MapEntryType();
			acquirerId.setKey("acquirerId");
			acquirerId.setValue("BTPN");

			MapEntryType merchantId = new MapEntryType();
			merchantId.setKey("merchantId");
			merchantId.setValue("BTPN");

			MapEntryType merchantType = new MapEntryType();
			merchantType.setKey("merchantType");
			merchantType.setValue("6012");
			
			MapEntryType terminalId = new MapEntryType();
			terminalId.setKey("terminalId");
			terminalId.setValue(phoneNumber.getNationalFormat());

			MapEntryType currencyCode = new MapEntryType();
			currencyCode.setKey("currencyCode");
			currencyCode.setValue("IDR");

//			MapEntryType debitIdentifier = new MapEntryType();
//			debitIdentifier.setKey("debitIdentifier");
//			debitIdentifier.setValue("");
			
			MapEntryType debitIdentifierType = new MapEntryType();
			debitIdentifierType.setKey("debitIdentifierType");
			debitIdentifierType.setValue("MSISDN");

			MapEntryType creditIdentifier = new MapEntryType();
			creditIdentifier.setKey("creditIdentifier");
			creditIdentifier.setValue(PortalUtils.exists(bean.getPayeeMsisdn()) ? bean
					.getPayeeMsisdn() : bean.getFavoriteNum().getId());								// no yg mau di transferin

			MapEntryType creditIdentifierType = new MapEntryType();
			creditIdentifierType.setKey("creditIdentifierType");
			creditIdentifierType.setValue("MSISDN");

			request.getParameter().add(acquirerId);
			request.getParameter().add(merchantId);
			request.getParameter().add(merchantType);
			request.getParameter().add(terminalId);
			request.getParameter().add(currencyCode);
//			request.getParameter().add(debitIdentifier);
			request.getParameter().add(debitIdentifierType);
			request.getParameter().add(creditIdentifier);
			request.getParameter().add(creditIdentifierType);
			
			request.setFlags(0);

			AddSchedTxnResponse response = new AddSchedTxnResponse();
			response = siClient.add(request);
			log.info("### (ConfirmFundTransferPanel::createPreFTtoReqMobileRequest) RESPONSE ### " +response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				basePage.getWebSession().info(getLocalizer().getString("si.ft.add.successMessage", this));
				setResponsePage(new StandingInstructionsPage());
			} else if (response.getStatus().getCode()==2501){
				createPreFTtoUnReqMobileRequest(fundTransferBean);
			} else {
				error(response.getStatus().getValue());
			}

		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
		}

	}
	
	
	private void createPreFTtoUnReqMobileRequest(FundTransferBean bean) {

		try {

			final AddSchedTxnRequest request = basePage.getNewMobiliserRequest(AddSchedTxnRequest.class);
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			String userLogin = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			
			final PhoneNumber phoneNumber = new PhoneNumber(userLogin, basePage
					.getAgentPortalPrefsConfig().getDefaultCountryCode());
			
			request.setCustomerId(customerId);
			request.setProcessingCode("100701");
			request.setDescription(bean.getSiName());
			request.setBeneficiaryNo(PortalUtils.exists(bean.getAccountNo()) ? bean
					.getAccountNo() : bean.getFavoriteNum().getId());
			request.setStartDate(PortalUtils.getSaveXMLGregorianCalendarFromDate(bean.getStartDate(), null));
			request.setFrequency(bean.getFrequencyType());
			request.setEndDate(PortalUtils.getSaveXMLGregorianCalendarToDate(bean.getExpiryDate(), null));
			request.setCurrencyCode("IDR");
			request.setAmount(fundTransferBean.getAmount());

			MapEntryType acquirerId = new MapEntryType();
			acquirerId.setKey("acquirerId");
			acquirerId.setValue("BTPN");

			MapEntryType merchantId = new MapEntryType();
			merchantId.setKey("merchantId");
			merchantId.setValue("BTPN");

			MapEntryType merchantType = new MapEntryType();
			merchantType.setKey("merchantType");
			merchantType.setValue("6012");
			
			MapEntryType terminalId = new MapEntryType();
			terminalId.setKey("terminalId");
			terminalId.setValue(phoneNumber.getNationalFormat());

			MapEntryType currencyCode = new MapEntryType();
			currencyCode.setKey("currencyCode");
			currencyCode.setValue("IDR");

//			MapEntryType debitIdentifier = new MapEntryType();
//			debitIdentifier.setKey("debitIdentifier");
//			debitIdentifier.setValue("");
			
			MapEntryType debitIdentifierType = new MapEntryType();
			debitIdentifierType.setKey("debitIdentifierType");
			debitIdentifierType.setValue("MSISDN");

			MapEntryType creditIdentifier = new MapEntryType();
			creditIdentifier.setKey("creditIdentifier");
			creditIdentifier.setValue("");

			MapEntryType creditIdentifierType = new MapEntryType();
			creditIdentifierType.setKey("creditIdentifierType");
			creditIdentifierType.setValue("");

			request.getParameter().add(acquirerId);
			request.getParameter().add(merchantId);
			request.getParameter().add(merchantType);
			request.getParameter().add(terminalId);
			request.getParameter().add(currencyCode);
//			request.getParameter().add(debitIdentifier);
			request.getParameter().add(debitIdentifierType);
			request.getParameter().add(creditIdentifier);
			request.getParameter().add(creditIdentifierType);
			
			request.setFlags(0);

			AddSchedTxnResponse response = new AddSchedTxnResponse();
			response = siClient.add(request);
			log.info("### (ConfirmFundTransferPanel::createPreFTtoUnReqMobileRequest) RESPONSE ### " +response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				basePage.getWebSession().info(getLocalizer().getString("si.ft.add.successMessage", this));
				setResponsePage(new StandingInstructionsPage());
			} else {
				error(response.getStatus().getValue());
			}

		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
		}
	}


	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFrequencyType() {
		return frequencyType;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

}
