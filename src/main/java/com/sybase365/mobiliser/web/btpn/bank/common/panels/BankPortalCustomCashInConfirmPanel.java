package com.sybase365.mobiliser.web.btpn.bank.common.panels;


import java.util.Calendar;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.debit.services.contract.v1_0.DebitAccountType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAmountAndCurrencyType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAttributeType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCustomCashInPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCustomCashInSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;



public class BankPortalCustomCashInConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BankPortalCustomCashInConfirmPanel.class);

	private static final String POST_ORIGIN = "BANK_PORTAL";
	private static String POST_PROCESSING_CODE;
	private static final String POST_MERCHANT_TYPE = "6012";
	private static final String POST_MERCHANT_ID = "BANK_PORTAL";
	private static final String POST_TERMINAL_ID = "BANK_PORTAL";
	private static final String POST_ACQUIRER_ID = "BTPN";
	private static final String POST_DEBIT_ACCT_TYPE = "WALLET";
	private static final String POST_ORG_UNIT_ID = "0901";
	private static final int POST_DEBIT_ACCT_FLAGS = 0;
	private static final int POST_CREDIT_ACCT_FLAGS = 0;
	private static final String POST_CREDIT_ACCT_TYPE = "MSISDN";
	private static final String POST_CURRENCY = "IDR";
	private static final String POST_DESC = "CASH IN DEBIT POSTING";
	
	/* DEBIT ATTRIBUTE TYPE */
	private static final int POST_DB_ATTR_TYPE_KEY = 110;
	private static final String POST_DB_ATTR_TYPE_VALUE = "1";
	
	protected BtpnMobiliserBasePage basePage;
	protected BankCustomCashInBean cashInBean;

	public BankPortalCustomCashInConfirmPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashInBean cashInBean, String PROCESSING_CODE) {
		super(id);
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		POST_PROCESSING_CODE = PROCESSING_CODE;
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<BankPortalCustomCashInConfirmPanel> form = new Form<BankPortalCustomCashInConfirmPanel>("customCashInConfirmForm",
			new CompoundPropertyModel<BankPortalCustomCashInConfirmPanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		form.add(new Label("cashInBean.glAccount", cashInBean.getGlAccount().getIdAndValue()));
		form.add(new Label("cashInBean.msisdn"));
		form.add(new Label("cashInBean.displayName"));
		form.add(new AmountLabel("cashInBean.cashinAmount", true, true));
		
		// Add Confirm button
		Button confirmButton = new Button("submitConfirm") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onSubmit() {
				try {
					if (!PortalUtils.exists(cashInBean)) {
						cashInBean = new BankCustomCashInBean();
					}
					performCashInPosting();
				} catch (Exception e) {
					log.error("#An error occurred while calling performCashInPosting", e);
				}
			}
		};
		confirmButton.add(new AttributePrepender("onclick", Model.of("loading(submitConfirm)"), ";"));
		form.add(confirmButton);

		// Add Back button
		form.add(new Button("submitBack") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new BankPortalCustomCashInPage()); 
			};
		}.setDefaultFormProcessing(false));

		// Add Cancel button
		form.add(new Button("submitCancle") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			};
		}.setDefaultFormProcessing(false));

		add(form);
	}
	
	
	/**
	 * This method handles the cash in posting.
	 */
	protected void performCashInPosting(){
		
		try {
			
			log.info(" ### (BankPortalCustomCashInConfirmPanel) performDebitPosting ### "); 
			
			UUID uuid = UUID.randomUUID();
			String traceNo = uuid.toString();
			log.info(" ##### POST_TRACE NO ##### " + traceNo);
			final DebitPostingRequest req = basePage.getNewMobiliserRequest(DebitPostingRequest.class); 
			req.setOrigin(POST_ORIGIN);
			req.setTraceNo(traceNo);
			req.setRepeat(false);
			UUID uuid2 = UUID.randomUUID();
			String convId = uuid2.toString();
			log.info(" ##### POST_CONVERSATION ID ##### " + convId);
			req.setConversationId(convId);
			req.setFinal(false);
			req.setProcessingCode(POST_PROCESSING_CODE);
			
			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			req.setTransactionDateTime(transDate);
			
			req.setMerchantType(POST_MERCHANT_TYPE);
			req.setMerchantId(POST_MERCHANT_ID);
			req.setTerminalId(POST_TERMINAL_ID);
			req.setAcquirerId(POST_ACQUIRER_ID);
			
			req.setDescription(POST_DESC);
			
			DebitAmountAndCurrencyType dbAmtAndCurType = new DebitAmountAndCurrencyType();
			dbAmtAndCurType.setCurrency(POST_CURRENCY);
//			log.info(" ### (performCashInPosting) CASH IN AMOUNT ### " + cashInBean.getCashinAmount());
//			dbAmtAndCurType.setValue(cashInBean.getCashinAmount());
			
			/* Request For Debit Account */
			DebitAccountType dbAcct = new DebitAccountType();
			dbAcct.setNumber(cashInBean.getGlAccount().getId()); //old 1051050
			dbAcct.setType(POST_DEBIT_ACCT_TYPE);
			dbAcct.setOrgUnitId(POST_ORG_UNIT_ID);
			dbAcct.setFlags(POST_DEBIT_ACCT_FLAGS);
			
			/* Request For Credit Account */
			DebitAccountType crAcct = new DebitAccountType();
			log.info(" ### (performCashInPosting) MSISDN ### " + cashInBean.getMsisdn());
			crAcct.setNumber(cashInBean.getMsisdn());
			crAcct.setType(POST_CREDIT_ACCT_TYPE);
			dbAcct.setOrgUnitId(POST_ORG_UNIT_ID);
			crAcct.setFlags(POST_CREDIT_ACCT_FLAGS);
			
			DebitTransactionType obj = new DebitTransactionType();
			obj.setDebitAccount(dbAcct);
			obj.setCreditAccount(crAcct);
			obj.setAmount(dbAmtAndCurType);

			DebitAttributeType dbAttr = new DebitAttributeType();
			dbAttr.setKey(POST_DB_ATTR_TYPE_KEY);
			dbAttr.setValue(POST_DB_ATTR_TYPE_VALUE);
			
			req.getTransaction().add(obj);
			req.getAttribute().add(dbAttr);
			
			final DebitPostingResponse response = basePage.getDebitClient().posting(req);
			Status status = response.getStatus();
			log.info(" ### RESPONSE POSTING STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				if (cashInBean != null)
				cashInBean = new BankCustomCashInBean();
				cashInBean = ConverterUtils.convertToBankCustomCashInBean(response.getTransaction());
				log.info(" ### (BankPortalCustomCashInConfirmPanel) performCashInPosting ACCT NAME ### " + cashInBean.getAccountName());
			
				setResponsePage(new BankPortalCustomCashInSuccessPage(cashInBean));
			} else {
				error(handleSpecificErrorMessage(response.getStatus().getCode()));
			}
		
		} catch (Exception e) {
			log.error(" ### An Error occured while calling callDebitPostingService ### ",e);
			error(getLocalizer().getString("posting.failure.exception", BankPortalCustomCashInConfirmPanel.this));
		}
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
