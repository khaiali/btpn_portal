package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices.CommonParam2;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices.FundTransfer;
import id.co.btpn.corp.dev.appmdwdev02.com_btpn_emoney_ws.wowtransferservices.FundTransferResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceOperations;
import org.springframework.ws.soap.SoapMessage;

import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAccountType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAmountAndCurrencyType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAttributeType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.FundTransferSuccessPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class FundTransferConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(FundTransferConfirmPanel.class);

	private static String  COMMON_PARAM_PAN = "6035150700000069";  // 16 dgt
    private static String  COMMON_PARAM_CHANNEL_ID = "6018";  //6017
    private static String  COMMON_PARAM_CHANNEL_TYPE = "WOW"; //WOW
    private static String  COMMON_PARAM_NODE = "PB"; // PB
    private static String  COMMON_PARAM_CURRENCY_AMOUNT = "IDR";
    private static String  COMMON_PARAM_CURRENCY_FEE = "IDR";
    private static String  COMMON_PARAM_ACQ_ID = "213"; // 6
    private static String  COMMON_PARAM_TERMINAL_ID = "MSISDN"; // MSISDN
    private static String  COMMON_PARAM_TERMINAL_NAME = "MSISDN"; // MSISDN
    private static String  COMMON_PARAM_ORIGINAL = "MDW"; // MDW
    
    private static final String ORIGIN = "CONSUMER_PORTAL";
	private static final String PROCESSING_CODE	= "100801";
	private static final String MERCHANT_TYPE = "6012";
	private static final String MERCHANT_ID = "CONSUMER_PORTAL";
	private static final String TERMINAL_ID = "CONSUMER_PORTAL";
	private static final String ACQUIRER_ID = "BTPN";
	private static final String DEBIT_ACCT_TYPE = "MSISDN";
	private static final String ORG_UNIT_ID = "0901";
	private static final int DEBIT_ACCT_FLAGS = 0;
	private static final int CREDIT_ACCT_FLAGS = 0;
	private static final String CREDIT_ACCT_TYPE = "MSISDN";
	private static final String CURRENCY = "IDR";
	private static final String DESC = "FT INQUIRY";
	
	
	protected BtpnMobiliserBasePage basePage;

	protected FundTransferBean fundTransfer;

	String selectedFundTransferType;
	
	@SpringBean(name = "wsFTransTemplate")
	private WebServiceOperations wsFTransTemplate;
	
	@SpringBean(name = "systemAuthSystemClient")
	private ISystemEndpoint systemEndpoint;
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitFacade;
	
	public FundTransferConfirmPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		FundTransferBean fundTransferBean) {
		super(id);
		this.basePage = basePage;
		this.fundTransfer = fundTransferBean;
		selectedFundTransferType = fundTransfer.getSelectedFundTransferType();
		constructPanel();
	}

	protected void constructPanel() {
		
		log.info(" ### (FundTransferConfirmPanel::constructPanel) ### ");
		
		final Form<FundTransferConfirmPanel> form = new Form<FundTransferConfirmPanel>("fundTransferMobilePinForm",
			new CompoundPropertyModel<FundTransferConfirmPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		WebMarkupContainer payeeMsisdnContainer = new WebMarkupContainer("payeeMsisdnContainer");
		payeeMsisdnContainer.setOutputMarkupId(true);
		payeeMsisdnContainer.setOutputMarkupPlaceholderTag(true);
		payeeMsisdnContainer.setVisible(false);
		payeeMsisdnContainer.add(new Label("fundTransfer.payeeMsisdn"));
		form.add(payeeMsisdnContainer);

		WebMarkupContainer accNumContainer = new WebMarkupContainer("accNumContainer");
		accNumContainer.setOutputMarkupId(true);
		accNumContainer.setOutputMarkupPlaceholderTag(true);
		accNumContainer.setVisible(false);

		accNumContainer.add(new Label("fundTransfer.accountNo"));
		form.add(accNumContainer);

		final String messageKey = "header." + selectedFundTransferType;
		String headerMessage = getLocalizer().getString(messageKey, this);
		form.add(new Label("fundTransferHeaderMessage", headerMessage));

		if (fundTransfer.isMobileSelected()) {
			payeeMsisdnContainer.setVisible(true);
			accNumContainer.setVisible(false);
		} else {
			payeeMsisdnContainer.setVisible(false);
			accNumContainer.setVisible(true);
		}
		
		form.add(new AmountLabel("fundTransfer.amount"));
		form.add(new AmountLabel("fundTransfer.feeAmount"));

		form.add(new PasswordTextField("fundTransfer.pin").setRequired(true).add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier).add(new ErrorIndicator()));
		
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				
				try {
					
					if (basePage.checkCredential(fundTransfer.getPin())) {
						if (fundTransfer.getFlag().equals("REG_MOBILE")) {
							log.info(" ### (FundTransferConfirmPanel::createFundTransferToRegMobileRequest ### ");
							createFundTransferToMobileRequest(fundTransfer);
						} else if (fundTransfer.getFlag().equals("UNREG_MOBILE")) {
							createFundTransferToMobileRequest(fundTransfer);
						} else if (selectedFundTransferType.equals(BtpnConstants.FT_ACCOUNT_TYPE_BTPN_ACCOUNT)) {
							createFundTransferToBTPNAccRequest(fundTransfer);
						} else if (selectedFundTransferType.equals(BtpnConstants.FT_ACCOUNT_TYPE_OTHER_BANK_ACCOUNT)) {
							createFundTransferToOtherAccRequest(fundTransfer);
						} else {
							error(getLocalizer().getString("error.exception", this));
						}
					} else {
						error("Invalid Pin");
					}
				
				} catch (Exception ex) {
					log.error("#An error occurred while calling FundTransfer service", ex);
					error(getLocalizer().getString("error.exception", this));
				}
			};
		});
		
		add(form);
	}
	

	/**
	 * calling fundTransferToRegMobile service from fund transfer end point
	 */
	private void createFundTransferToMobileRequest(FundTransferBean bean) {
		
		try {
			
			String payerAccountNo = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (FundTransferConfirmPanel::createFundTransferToRegMobileRequest ### "); 
			
			UUID uuid = UUID.randomUUID();
			String traceNo = uuid.toString();
			final DebitPostingRequest req = basePage.getNewMobiliserRequest(DebitPostingRequest.class); 
			req.setOrigin(ORIGIN);
			req.setTraceNo(traceNo);
			req.setRepeat(false);
			UUID uuid2 = UUID.randomUUID();
			String convId = uuid2.toString();
			req.setConversationId(convId);
			req.setFinal(false);
			
			if(fundTransfer.getFlag().equals("REG_MOBILE"))
				req.setProcessingCode(PROCESSING_CODE);
			
			if(fundTransfer.getFlag().equals("UNREG_MOBILE"))
				req.setProcessingCode("100701");
			
			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			req.setTransactionDateTime(transDate);
			
			req.setMerchantType(MERCHANT_TYPE);
			req.setMerchantId(MERCHANT_ID);
			req.setTerminalId(TERMINAL_ID);
			req.setAcquirerId(ACQUIRER_ID);
			
			req.setDescription(DESC);
			
			DebitAmountAndCurrencyType dbAmtAndCurType = new DebitAmountAndCurrencyType();
			dbAmtAndCurType.setCurrency(CURRENCY);
			log.info(" ### (FundTransferConfirmPanel::createFundTransferToRegMobileRequest) FT AMOUNT ### " + fundTransfer.getAmount());
			dbAmtAndCurType.setValue(fundTransfer.getAmount());
			
			/* Request For Debit Account */
			DebitAccountType dbAcct = new DebitAccountType();
			dbAcct.setNumber(payerAccountNo);
			dbAcct.setType(DEBIT_ACCT_TYPE);
			dbAcct.setOrgUnitId(ORG_UNIT_ID);
			dbAcct.setFlags(DEBIT_ACCT_FLAGS);
			
			/* Request For Credit Account */
			DebitAccountType crAcct = new DebitAccountType();
			crAcct.setNumber(PortalUtils.exists(fundTransfer.getPayeeMsisdn()) ? fundTransfer.getPayeeMsisdn() : fundTransfer.getFavoriteNum().getId());
			
			if(fundTransfer.getFlag().equals("REG_MOBILE"))
				crAcct.setType(CREDIT_ACCT_TYPE);
			else 
				crAcct.setType("3");   
			
			crAcct.setFlags(CREDIT_ACCT_FLAGS);
			
			DebitTransactionType obj = new DebitTransactionType();
			obj.setDebitAccount(dbAcct);
			obj.setCreditAccount(crAcct);
			obj.setAmount(dbAmtAndCurType);
			
			DebitAttributeType dbAttr = new DebitAttributeType();
			dbAttr.setKey(53);
			dbAttr.setValue(fundTransfer.getPayeeMsisdn());
			
			req.getTransaction().add(obj);
			req.getAttribute().add(dbAttr);
			
			final DebitPostingResponse response = debitFacade.posting(req);
			Status status = response.getStatus();
			log.info(" ### (FundTransferConfirmPanel::createFundTransferToRegMobileRequest) RESPONSE POSTING STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				setResponsePage(new FundTransferSuccessPage(fundTransfer));
			} else {
				error(handleSpecificErrorMessage(response.getStatus().getCode()));
			}
		
		} catch (Exception e) {
			log.error(" ### An Error occured while calling callDebitPostingService ### " +e);
			error(getLocalizer().getString("posting.failure.exception", FundTransferConfirmPanel.this));
		}
	}

	/**
	 * calling FundTransferToBTPNAcc service from fund transfer end point
	 */
	private void createFundTransferToBTPNAccRequest(FundTransferBean bean) {
		
		log.info("### FundTransferConfirmPanel::createFundTransferToBTPNAccRequest ###");
		
		try {
			
			String payerAccountNo = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (FundTransferConfirmPanel::createFundTransferToBTPNAccRequest) PAYEER ACCT NO ### " +payerAccountNo);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			XMLGregorianCalendar transmissionDateTime = PortalUtils.getSaveXMLGregorianCalendar(cal);
			
			FundTransfer request = new FundTransfer();
			CommonParam2 param = new CommonParam2();
			
			String pan = "";
			if (PortalUtils.exists(basePage.getMobiliserWebSession().getCustomerRegistrationBean())){
				pan = basePage.getMobiliserWebSession().getCustomerRegistrationBean().getAtmCardNo();
			}else{
				pan = COMMON_PARAM_PAN;
			}
			
			param.setPan(pan);
			param.setProcessingCode("100901");
			param.setChannelId("6011");
			param.setChannelType("PB");
			param.setNode("WOW_CHANNEL");
			param.setCurrencyAmount(COMMON_PARAM_CURRENCY_AMOUNT);

			log.info(" ### (FundTransferConfirmPanel::createFundTransferToBTPNAccRequest) AMOUNT ## "+ fundTransfer.getAmount());
			Long amount = fundTransfer.getAmount();
			Long realAmount = amount / 100;
			param.setAmount(String.valueOf(realAmount));
			param.setCurrencyfee(COMMON_PARAM_CURRENCY_FEE);
			log.info(" ### (FundTransferConfirmPanel::createFundTransferToBTPNAccRequest) FEE AMOUNT ## "+ fundTransfer.getFeeAmount());

			param.setFee(String.valueOf(fundTransfer.getFeeAmount()));
			param.setTransmissionDateTime(transmissionDateTime.toXMLFormat());
			String refNo = MobiliserUtils.getExternalReferenceNo(systemEndpoint);
			log.info(" ### (FundTransferConfirmPanel::createFundTransferToBTPNAccRequest) REF NO ## "+ refNo);
			param.setRequestId(refNo);
			param.setAcqId(COMMON_PARAM_ACQ_ID);
			param.setReferenceNo(refNo);
			param.setTerminalId(formatedNationalMsisdn(payerAccountNo));
			param.setTerminalName(formatedNationalMsisdn(payerAccountNo));
			param.setOriginal(COMMON_PARAM_ORIGINAL);
			
			request.setCommonParam(param);
			request.setAccountNo(formatedNationalMsisdn(payerAccountNo));
			request.setBankIssuer("213");
			request.setBankBeneficiary("213");
			
			request.setAccountBeneficiary(PortalUtils.exists(fundTransfer.getAccountNo()) ? fundTransfer.getAccountNo() : fundTransfer.getFavoriteNum().getId());
			
			request.setDebitType("MSISDN");
//			request.setUnitId(URG_UNIT_ID);
			
			String Att1 = "Transfer Uang ";
			String Att2 = "Dari Wow ";
			String Att3 = "Ke BTPN Account ";

			request.setAttribute1(Att1);
			request.setAttribute2(Att2);
			request.setAttribute3(Att3);
			
			log.info("### FundTransferConfirmPanel::createFundTransferToBTPNAccRequest BEFORE SERVICE ###");
			
			// calling fundTransferToOtherAcc service
			FundTransferResponse response = new FundTransferResponse(); 
			
			response = (FundTransferResponse) wsFTransTemplate
						.marshalSendAndReceive(request,
								new WebServiceMessageCallback() {
		
									public void doWithMessage(
											WebServiceMessage message) {
										((SoapMessage) message)
												.setSoapAction("com_btpn_emoney_ws_wowTransferServices_Binder_fundTransfer");
									}
								});
			final int statusCode = Integer.valueOf(response.getResponseCode());
		    log.info("### FundTransferConfirmPanel::createFundTransferToBTPNAccRequest RESPONSE CODE ###" +statusCode);
			if (basePage.evaluateConsumerPortalMobResponse(response)) {
				setResponsePage(new FundTransferSuccessPage(fundTransfer));
			} else {
				error(response.getResponseCode());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling createFundTransferToBTPNAccRequest service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}
	
	
	/**
	 * calling fundTransferToOtherAcc service from fund transfer end point
	 */
	private void createFundTransferToOtherAccRequest(FundTransferBean bean) {
		
		log.info("### FundTransferConfirmPanel::createPreFundTransferToOtherAccRequest ###");
		
		try {
			
			String payerAccountNo = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (FundTransferConfirmPanel::createFundTransferToOtherAccRequest) ACCT NO ### " +payerAccountNo);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			XMLGregorianCalendar transmissionDateTime = PortalUtils.getSaveXMLGregorianCalendar(cal);
			
			FundTransfer request = new FundTransfer();
			CommonParam2 param = new CommonParam2();
			
			String pan = "";
			if (PortalUtils.exists(basePage.getMobiliserWebSession().getCustomerRegistrationBean())){
				pan = basePage.getMobiliserWebSession().getCustomerRegistrationBean().getAtmCardNo();
			}else{
				pan = COMMON_PARAM_PAN;
			}
			param.setPan(pan);
			param.setProcessingCode(fundTransfer.getProc_code());
			param.setChannelId("6011");
			param.setChannelType(COMMON_PARAM_CHANNEL_TYPE);
			param.setNode(COMMON_PARAM_NODE);
			param.setCurrencyAmount(COMMON_PARAM_CURRENCY_AMOUNT);

			log.info(" ### (FundTransferConfirmPanel::createPreFTtoOtherAcctRequest) AMOUNT ## "+ fundTransfer.getAmount());
			Long amount = fundTransfer.getAmount();
			Long realAmount = amount / 100;
			param.setAmount(String.valueOf(realAmount));
			param.setCurrencyfee(COMMON_PARAM_CURRENCY_FEE);
			log.info(" ### (FundTransferConfirmPanel::createPreFTtoOtherAcctRequest) FEE AMOUNT ## "+ fundTransfer.getFeeAmount());
			Long realFee = fundTransfer.getFeeAmount() / 100;
			log.info(" ### (FundTransferConfirmPanel::createPreFTtoOtherAcctRequest) REAL FEE AMOUNT ## "+ realFee);
			param.setFee(String.valueOf(realFee));
			param.setTransmissionDateTime(transmissionDateTime.toXMLFormat());
			String refNo = MobiliserUtils.getExternalReferenceNo(systemEndpoint);
			log.info(" ### (FundTransferConfirmPanel::createPreFTtoOtherAcctRequest) REF NO ## "+ refNo);
			param.setRequestId(refNo);
			param.setAcqId(COMMON_PARAM_ACQ_ID);
			param.setReferenceNo(refNo);
			param.setTerminalId(COMMON_PARAM_TERMINAL_ID);
			param.setTerminalName(COMMON_PARAM_TERMINAL_NAME);
			param.setOriginal(COMMON_PARAM_ORIGINAL);

			String Att1 = " Transfer Uang ";
			String Att2 = " Dari Wow ";
			String Att3 = " Ke Other Account ";
			String Att4 = " ";

			request.setCommonParam(param);
			request.setAccountNo(formatedNationalMsisdn(payerAccountNo));
			request.setBankIssuer("213");
			request.setDebitType("MSISDN");
			request.setUnitId("0901");
			log.info(" ### (FundTransferConfirmPanel::createPreFTtoOtherAcctRequest) BENEF BANK CODE ### "+ fundTransfer.getBenefBankCode());
			request.setBankBeneficiary(fundTransfer.getBenefBankCode());
			request.setAccountBeneficiary(PortalUtils.exists(fundTransfer.getAccountNo()) ? fundTransfer.getAccountNo() : fundTransfer.getFavoriteNum().getId());
			
			request.setAttribute1(Att1);
			request.setAttribute2(Att2);
			request.setAttribute3(Att3);
			request.setAttribute3(Att4);
			
			log.info("### FundTransferConfirmPanel::createPreFTtoOtherAcctRequest BEFORE SERVICE ###");
			FundTransferResponse response = new FundTransferResponse(); 
			
			response = (FundTransferResponse) wsFTransTemplate
						.marshalSendAndReceive(request,
								new WebServiceMessageCallback() {
		
									public void doWithMessage(
											WebServiceMessage message) {
										((SoapMessage) message)
												.setSoapAction("com_btpn_emoney_ws_wowTransferServices_Binder_fundTransfer");
									}
								});
			final int statusCode = Integer.valueOf(response.getResponseCode());
		    log.info("### FundTransferConfirmPanel::createPreFTtoOtherAcctRequest RESPONSE CODE ###" +statusCode);
			if (basePage.evaluateConsumerPortalMobResponse(response)) {
				setResponsePage(new FundTransferSuccessPage(fundTransfer));
			} else {
				error(response.getResponseCode());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling fundTransferToOtherAcc service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private String formatedNationalMsisdn(String msisdn) {
		return new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig().getDefaultCountryCode()).getNationalFormat();
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("posting.fail", this);
		}
		return message;
	}
	
}
