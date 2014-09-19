package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices.CommonParam2;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices.InquiryDebitFundTransfer;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices.InquiryDebitFundTransferResponse;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceOperations;
import org.springframework.ws.soap.SoapMessage;

import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.common.components.FavouriteDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.FundTransferConfirmPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class FundTransferToOtherAccountsPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(FundTransferToOtherAccountsPanel.class);

	private static String  COMMON_PARAM_PAN = "6035150700000069";  // 16 dgt
    private static String  COMMON_PARAM_CHANNEL_ID = "6011";  //6017
    private static String  COMMON_PARAM_CHANNEL_TYPE = "WOW"; //WOW
    private static String  COMMON_PARAM_NODE = "WOW_CHANNEL"; // PB
    private static String  COMMON_PARAM_CURRENCY_AMOUNT = "IDR";
    private static String  COMMON_PARAM_CURRENCY_FEE = "IDR";
    private static String  COMMON_PARAM_ACQ_ID = "213"; // 6
    private static String  COMMON_PARAM_ORIGINAL = "MDW"; // MDW
    
    @SpringBean(name = "systemAuthSystemClient")
    private ISystemEndpoint systemEndpoint;
	
	protected BtpnMobiliserBasePage basePage;
	protected FundTransferBean fundTransfer;

	String selectedFundTransferAccount;
	private String filterType = BtpnConstants.FILTERTYPE_MANUAL;
	private int favoriteType = 0;
	private int useCaseId;
	
	@SpringBean(name = "wsFTransTemplate")
	private WebServiceOperations wsFTransTemplate;

	
	public FundTransferToOtherAccountsPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		FundTransferBean fundTransferBean) {
		super(id);
		this.basePage = basePage;
		this.fundTransfer = fundTransferBean;
		selectedFundTransferAccount = fundTransfer.getSelectedFundTransferType();
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<FundTransferToOtherAccountsPanel> form = new Form<FundTransferToOtherAccountsPanel>(
			"fundTransferBtpnBankForm", new CompoundPropertyModel<FundTransferToOtherAccountsPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		final WebMarkupContainer bankCodeDiv = new WebMarkupContainer("bankCodeDiv");
		bankCodeDiv.setOutputMarkupId(true);
		bankCodeDiv.setOutputMarkupPlaceholderTag(true);

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		
		bankCodeDiv.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("fundTransfer.bankCode", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_FT_TO_OTHER_BANK_CODES, this, Boolean.FALSE, Boolean.TRUE).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(bankCodeDiv);

		final String messageKey = "header." + selectedFundTransferAccount;
		String headerMessage = getLocalizer().getString(messageKey, this);
		form.add(new Label("fundTransferHeaderMessage", headerMessage));

		if (selectedFundTransferAccount.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)) {
			bankCodeDiv.setVisible(false);
			favoriteType = BtpnConstants.FAVOURITE_TYPE_BTPN_ACCOUNT;
			useCaseId = BtpnConstants.USECASE_FT_BTPN_BANK;
		} else {
			bankCodeDiv.setVisible(true);
			favoriteType = BtpnConstants.FAVOURITE_TYPE_OTHER_BANK_ACCOUNT;
			useCaseId = BtpnConstants.USECASE_FT_OTHER_BANK;
		}

		RadioGroup<FundTransferToOtherAccountsPanel> radioGroup = new RadioGroup<FundTransferToOtherAccountsPanel>(
			"radioGroup", new PropertyModel<FundTransferToOtherAccountsPanel>(this, "filterType"));

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<FundTransferToOtherAccountsPanel> enterManually = new Radio<FundTransferToOtherAccountsPanel>(
			"EnterManually", new Model(BtpnConstants.FILTERTYPE_MANUAL));
		enterManually.setOutputMarkupId(true).setMarkupId("EnterManually");
		radioGroup.add(enterManually);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		Radio<FundTransferToOtherAccountsPanel> favoriteList = new Radio<FundTransferToOtherAccountsPanel>(
			"FavoriteList", new Model(BtpnConstants.FILTERTYPE_FAVORITE));
		favoriteList.setOutputMarkupId(true).setMarkupId("FavoriteList");
		radioGroup.add(favoriteList);

		form.add(radioGroup);

		final WebMarkupContainer enterManualllyDiv = new WebMarkupContainer("enterManualllyDiv");
		enterManualllyDiv.setOutputMarkupId(true);
		enterManualllyDiv.setOutputMarkupPlaceholderTag(true);

		enterManualllyDiv.add(new TextField<String>("fundTransfer.accountNo").add(new PatternValidator("^[0-9]*$"))
			.setRequired(true).add(new ErrorIndicator()));

		final WebMarkupContainer selectFromFavoriteDiv = new WebMarkupContainer("selectFromFavoriteDiv");
		selectFromFavoriteDiv.setOutputMarkupId(true);
		selectFromFavoriteDiv.setOutputMarkupPlaceholderTag(true);
		selectFromFavoriteDiv.setVisible(false);
		
		if (selectedFundTransferAccount.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)) {
			selectFromFavoriteDiv.add(new FavouriteDropdownChoice("fundTransfer.favoriteNum", false, true, 
				basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId(), 3).setNullValid(false)
				.setRequired(true).add(new ErrorIndicator()));
		} else {
			selectFromFavoriteDiv.add(new FavouriteDropdownChoice("fundTransfer.favoriteNum", false, true, 
				basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId(), 5).setNullValid(false)
				.setRequired(true).add(new ErrorIndicator()));
		}

		form.add(enterManualllyDiv);
		form.add(selectFromFavoriteDiv);

		enterManually.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				log.info("Selecetd Type : " + filterType);
				selectFromFavoriteDiv.setVisible(false);
				target.addComponent(selectFromFavoriteDiv);
				enterManualllyDiv.setVisible(true);
				target.addComponent(enterManualllyDiv);
			}
		});

		favoriteList.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				log.info("Selecetd Type : " + filterType);
				selectFromFavoriteDiv.setVisible(true);
				target.addComponent(selectFromFavoriteDiv);
				enterManualllyDiv.setVisible(false);
				target.addComponent(enterManualllyDiv);
			}
		});

		TextField<String> beneficiaryName = new TextField<String>("fundTransfer.beneficiaryName");
		beneficiaryName.add(Constants.mediumStringValidator).add(Constants.mediumSimpleAttributeModifier)
			.add(new ErrorIndicator());
		beneficiaryName.setOutputMarkupId(true);
		form.add(beneficiaryName);
		Label requiredSymbol = new Label("requiredSymbol", "*");
		requiredSymbol.setOutputMarkupId(true);
		requiredSymbol.setVisible(false);
		form.add(requiredSymbol);

		if (selectedFundTransferAccount.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)) {
			beneficiaryName.setRequired(false);
			requiredSymbol.setVisible(false);
		} else {
			beneficiaryName.setRequired(true);
			requiredSymbol.setVisible(true);
		}

		form.add(new AmountTextField<Long>("fundTransfer.amount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (selectedFundTransferAccount.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)) {
					createPreFundTransferToBTPNAccRequest(fundTransfer);

				} else if (selectedFundTransferAccount.equals(BtpnConstants.FT_ACCOUNT_TYPE_OTHER_BANK_ACCOUNT)) {
					createPreFTtoOtherAcctRequest(fundTransfer);
				} 
			};
		});
		add(form);
	}

	
	private void createPreFundTransferToBTPNAccRequest(FundTransferBean bean) {
		
		log.info("### FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest ###");
		
		try {
			
			String payerAccountNo = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest) ACCT NO ### "+payerAccountNo);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			XMLGregorianCalendar transmissionDateTime = PortalUtils.getSaveXMLGregorianCalendar(cal);
			
			// request n response
		    CommonParam2 param = new CommonParam2();
		    
		    String pan = "";
			if (PortalUtils.exists(basePage.getMobiliserWebSession().getCustomerRegistrationBean())){
				pan = basePage.getMobiliserWebSession().getCustomerRegistrationBean().getAtmCardNo();
			}else{
				pan = COMMON_PARAM_PAN;
			}
		    
		    param.setPan(pan);
		    param.setProcessingCode("100901");
		    param.setChannelId(COMMON_PARAM_CHANNEL_ID);
		    param.setChannelType("PB");
		    param.setNode(COMMON_PARAM_NODE);
		    param.setCurrencyAmount(COMMON_PARAM_CURRENCY_AMOUNT);
		    
		    log.info(" ### (FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest) AMOUNT ## " +fundTransfer.getAmount());
		    Long amount = fundTransfer.getAmount();
		    Long realAmount = amount / 100;
		    log.info(" ### (FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest) REAL AMOUNT ## " +realAmount);
		    param.setAmount(String.valueOf(realAmount));
		    param.setCurrencyfee(COMMON_PARAM_CURRENCY_FEE);
		    param.setTransmissionDateTime(transmissionDateTime.toXMLFormat());
		    String refNo = MobiliserUtils.getExternalReferenceNo(systemEndpoint);
		    log.info(" ### (FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest) REF NO ## " +refNo);
		    param.setRequestId(refNo);
		    param.setAcqId(COMMON_PARAM_ACQ_ID);
		    param.setReferenceNo(refNo);
		    param.setTerminalId(payerAccountNo);
		    param.setTerminalName(payerAccountNo);
		    param.setOriginal(COMMON_PARAM_ORIGINAL);
		    
			InquiryDebitFundTransfer request = new InquiryDebitFundTransfer();
		    request.setCommonParam(param);
		    request.setAccountNo(payerAccountNo);
		    request.setDebitType("MSISDN");
		    request.setUnitId("0901");
		    
		    request.setBeneficiaryBankCode("213");
		    request.setBeneficiaryAccountNo(PortalUtils.exists(fundTransfer.getAccountNo()) ? fundTransfer.getAccountNo() : fundTransfer.getFavoriteNum().getId());
		    
		    String Att1 = "Fund Transfer Inquiry ";
		    String Att2 = "From Wow ";
		    String Att3 = "To BTPN Account ";
		    String Att4 = "";
		    
		    request.setAttribute1(Att1);
		    request.setAttribute2(Att2);
		    request.setAttribute3(Att3); 
		    request.setAttribute3(Att4);
		    
		    log.info("### FundTransferToOtherAccountsPanel::createPreFTtoOtherAcctRequest BEFORE SERVICE ###");
		    InquiryDebitFundTransferResponse response = new InquiryDebitFundTransferResponse();
		    
		    response = (InquiryDebitFundTransferResponse) wsFTransTemplate
					.marshalSendAndReceive(request,
							new WebServiceMessageCallback() {
	
								public void doWithMessage(
										WebServiceMessage message) {
									((SoapMessage) message)
											.setSoapAction("com_btpn_emoney_ws_wowTransferServices_Binder_inquiryDebitFundTransfer");
								}
							});
		    
		    final int statusCode = Integer.valueOf(response.getResponseCode());
		    
		    log.info("### FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest RESPONSE CODE ###" +statusCode);
		    if ( response != null && statusCode == 0){
		    	log.info(" ### (FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest) RESP FEE ## " +response.getFee());
		    	fundTransfer.setFeeAmount(Long.parseLong(response.getFee()));
		    	log.info(" ### (FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest) RESP FEE CURRENCY ## " +response.getFeeCurrency());
		    	fundTransfer.setFeeCurrency(response.getFeeCurrency());
		    	fundTransfer.setSourceHolderName(response.getSourceHolderName());
		    	fundTransfer.setAmount(amount);
		    	fundTransfer.setFlag("FT_TO_BTPN_BANK");
		    	setResponsePage(new FundTransferConfirmPage(fundTransfer));
		    } else {
		    	error(MobiliserUtils.errorMessage(response.getResponseCode(), response.getResponseDesc(), getLocalizer(), this));
		    }
		    
		} catch (Exception ex) {
			log.error("#An error occurred while calling createPreFundTransferToBTPNAccRequest service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	
	
	private void createPreFTtoOtherAcctRequest(FundTransferBean bean) {
		
		log.info("### FundTransferToOtherAccountsPanel::createPreFundTransferToOtherAccRequest ###");
		
		try {
			
		    String PROCESSING_CODE = "";  
		    String benefBankCode = "";
		    
			if (PortalUtils.exists(fundTransfer.getBankCode())){
				String bankCode  = fundTransfer.getBankCode().getId();
				String code = bankCode.substring(0, 1);
				benefBankCode = bankCode.substring(1, 4);
				
				if (code.equalsIgnoreCase("B")){
					PROCESSING_CODE = "101001";
				}
				
				if (code.equalsIgnoreCase("P")){
					PROCESSING_CODE = "101101";
				}
			}
			
			String payerAccountNo = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (FundTransferToOtherAccountsPanel::createPreFundTransferToBTPNAccRequest) PAYEER ACCT NO ### "+payerAccountNo);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			XMLGregorianCalendar transmissionDateTime = PortalUtils.getSaveXMLGregorianCalendar(cal);
			
			// request n response
			InquiryDebitFundTransfer request = new InquiryDebitFundTransfer();
		    CommonParam2 param = new CommonParam2();
		    
			String pan = "";
			if (PortalUtils.exists(basePage.getMobiliserWebSession().getCustomerRegistrationBean())){
				pan = basePage.getMobiliserWebSession().getCustomerRegistrationBean().getAtmCardNo();
			}else{
				pan = COMMON_PARAM_PAN;
			}
		    
			param.setPan(pan);
		    param.setProcessingCode(PROCESSING_CODE);
		    param.setChannelId(COMMON_PARAM_CHANNEL_ID);
		    param.setChannelType(COMMON_PARAM_CHANNEL_TYPE);
		    param.setNode(COMMON_PARAM_NODE);
		    param.setCurrencyAmount(COMMON_PARAM_CURRENCY_AMOUNT);
		    
		    log.info(" ### (FundTransferToOtherAccountsPanel::createPreFTtoOtherAcctRequest) AMOUNT ## " +fundTransfer.getAmount());
		    Long amount = fundTransfer.getAmount();
		    Long realAmount = amount / 100;
		    log.info(" ### (FundTransferToOtherAccountsPanel::createPreFTtoOtherAcctRequest) REAL AMOUNT ## " +realAmount);
		    param.setAmount(String.valueOf(realAmount));
		    param.setCurrencyfee(COMMON_PARAM_CURRENCY_FEE);
		    param.setFee("0");
		    param.setTransmissionDateTime(transmissionDateTime.toXMLFormat());
		    String refNo = MobiliserUtils.getExternalReferenceNo(systemEndpoint);
		    log.info(" ### (FundTransferToOtherAccountsPanel::createPreFTtoOtherAcctRequest) REF NO ## " +refNo);
		    param.setRequestId(refNo);
		    param.setAcqId(COMMON_PARAM_ACQ_ID);
		    param.setReferenceNo(refNo);
		    param.setTerminalId(formatedNationalMsisdn(payerAccountNo));
		    param.setTerminalName(formatedNationalMsisdn(payerAccountNo));
		    param.setOriginal(COMMON_PARAM_ORIGINAL);
		    
		    request.setCommonParam(param);
		    request.setAccountNo(formatedNationalMsisdn(payerAccountNo));
		    request.setDebitType("MSISDN");
		    request.setUnitId("0901");
		    
//		    request.setBeneficiaryBankCode(PortalUtils.exists(fundTransfer.getBankCode()) ? fundTransfer.getBankCode().getId() : null);
		    request.setBeneficiaryBankCode(PortalUtils.exists(fundTransfer.getBankCode()) ? benefBankCode : null);
		    request.setBeneficiaryAccountNo(PortalUtils.exists(fundTransfer.getAccountNo()) ? fundTransfer.getAccountNo() : fundTransfer.getFavoriteNum().getId());
		    
		    String Att1 = "Fund Transfer Inquiry";
		    String Att2 = "From Wow ";
		    String Att3 = "To Other Account ";
		    String Att4 = "";
		    
		    request.setAttribute1(Att1);
		    request.setAttribute2(Att2);
		    request.setAttribute3(Att3); 
		    request.setAttribute3(Att4);
		    
		    log.info("### FundTransferToOtherAccountsPanel::createPreFTtoOtherAcctRequest BEFORE SERVICE ###");
		    InquiryDebitFundTransferResponse response = new InquiryDebitFundTransferResponse();
		    
		    response = (InquiryDebitFundTransferResponse) wsFTransTemplate
					.marshalSendAndReceive(request,
							new WebServiceMessageCallback() {
	
								public void doWithMessage(
										WebServiceMessage message) {
									((SoapMessage) message)
											.setSoapAction("com_btpn_emoney_ws_wowTransferServices_Binder_inquiryDebitFundTransfer");
								}
							});
		    
		    final int statusCode = Integer.valueOf(response.getResponseCode());
		    log.info("### FundTransferToOtherAccountsPanel::createPreFTtoOtherAcctRequest RESPONSE CODE ###" +statusCode);
		    if (evaluateMobResponse(response, ConsumerPortalApplicationLoginPage.class)){
		    	log.info(" ### (FundTransferToOtherAccountsPanel::createPreFTtoOtherAcctRequest) RESP FEE ## " +response.getFee());
		    	fundTransfer.setFeeAmount(Long.parseLong(response.getFee()));
		    	fundTransfer.setFeeCurrency(response.getFeeCurrency());
		    	fundTransfer.setSourceHolderName(response.getSourceHolderName());
		    	fundTransfer.setAmount(amount);
		    	fundTransfer.setProc_code(PROCESSING_CODE);
		    	fundTransfer.setBenefBankCode(benefBankCode);
		    	fundTransfer.setFlag("FT_TO_OTHER_BANK");
		    	setResponsePage(new FundTransferConfirmPage(fundTransfer));
		    } else {
		    	error(response.getResponseCode());  
		    }
		    
		} catch (Exception ex) {
			log.error("#An error occurred while calling createPreFTtoOtherAcctRequest service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public boolean evaluateMobResponse(InquiryDebitFundTransferResponse response,
		Class<? extends Page> loginClass) {

		if (Integer.parseInt(response.getResponseCode()) == 0 && Integer.parseInt(response.getResponseCode()) == 00) {
			return true;
		}

		// check for mobiliser session closed or expired
		if (Integer.parseInt(response.getResponseCode()) == 352 || Integer.parseInt(response.getResponseCode()) == 353) {
			log.debug("# Mobiliser session closed/expired, redirect to sign in page");
			basePage.getMobiliserWebSession().invalidate();
			getRequestCycle().setRedirect(true);
			if (null != loginClass) {
				setResponsePage(basePage.getComponent(loginClass));
				String errorMessage = null;
				errorMessage = basePage.getDisplayValue(String.valueOf(response.getResponseCode()),
					Constants.RESOURCE_BUNDLE_ERROR_CODES);

				if (PortalUtils.exists(errorMessage)) {
					basePage.getMobiliserWebSession().error(errorMessage);
				} else {
					basePage.getMobiliserWebSession().error(getLocalizer().getString("portal.genericError", this));
				}
			}
		}

		return false;
	}
	
	private String formatedNationalMsisdn(String msisdn) {
		return new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig().getDefaultCountryCode()).getNationalFormat();
	}
	
}
