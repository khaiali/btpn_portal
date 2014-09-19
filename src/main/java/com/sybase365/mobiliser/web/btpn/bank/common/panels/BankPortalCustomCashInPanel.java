package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAccountType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAmountAndCurrencyType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAttributeType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCustomCashInConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AccountDropDownChoice;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the BankPortalCustomCashInPanel page for bank portals.
 * 
 * @author Andi Samallangi W
 * @modified Feny Yanti
 */
public class BankPortalCustomCashInPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BankPortalCustomCashInPanel.class);

	private static final String ORIGIN = "BANK_PORTAL";
	private static String PROCESSING_CODE; 
    
	private static final String MERCHANT_TYPE = "6012"; 
	private static final String MERCHANT_ID = "BANK_PORTAL";
	private static final String TERMINAL_ID = "BANK_PORTAL";
	private static final String ACQUIRER_ID = "BTPN";
	private static final String DEBIT_ACCT_TYPE = "WALLET"; 
	private static final String ORG_UNIT_ID = "0901";
	private static final int DEBIT_ACCT_FLAGS = 0;
	private static final int CREDIT_ACCT_FLAGS = 0;
	private static final String CREDIT_ACCT_TYPE = "MSISDN";
	private static final String CURRENCY = "IDR";
	private static final String DESC = "CASH IN INQUIRY";
	
	/* DEBIT ATTRIBUTE TYPE */
	private static final int DB_ATTR_TYPE_KEY = 110;
	private static final String DB_ATTR_TYPE_VALUE = "1";
	
	private FeedbackPanel feedBack;
	protected BtpnMobiliserBasePage basePage;
	protected BankCustomCashInBean cashInBean;
	
	private Component glComponent;
	private Component msisdnComponent;
	private Component amountComponent;
	private CodeValue glTemp;
	/**
	 * Constructor for cash-in panel.
	 * 
	 * @param id
	 * @param basePage
	 */
	public BankPortalCustomCashInPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashInBean cashInBean, String processing_code) {
		super(id);
		this.basePage = basePage;
		this.cashInBean =  cashInBean;
		PROCESSING_CODE = processing_code;
		constructPanel();
	}

	protected void constructPanel() {
		
		log.info(" ##### constructPanel ##### ");
		final Form<BankPortalCustomCashInPanel> form = new Form<BankPortalCustomCashInPanel>("customCashInForm",
			new CompoundPropertyModel<BankPortalCustomCashInPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);
	
		form.add(glComponent = new AccountDropDownChoice(
								"cashInBean.glAccount", false, true,
								Long.toString(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()),
								1, Collections.singleton(Integer.valueOf(999)), null)
				.setNullValid(false)
				.setRequired(true)
				.add(new ErrorIndicator()));
		glComponent.setOutputMarkupId(true);
		
		form.add(msisdnComponent = new TextField<String>("cashInBean.msisdn").setRequired(true)
			.add(new PatternValidator(this.basePage.getBankPortalPrefsConfig().getMobileRegex()))
			.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
			.add(new ErrorIndicator()));
		msisdnComponent.setOutputMarkupId(true);
		
		form.add(amountComponent = new AmountTextField<Long>("cashInBean.cashinAmount", Long.class, false).setRequired(true)
				.add(new ErrorIndicator()));
		amountComponent.setOutputMarkupId(true);
		
		
		form.add(new AjaxButton("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (!PortalUtils.exists(cashInBean)) {
					cashInBean = new BankCustomCashInBean();
				}
				performCashInInquiry();
			};

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBack);
				target.addComponent(glComponent);
				target.addComponent(msisdnComponent);
				target.addComponent(amountComponent);
				
				super.onError(target, form);
			}
		});

		add(form);
	}
	
	
	/**
	 * This method handles the cash in inquiry.
	 */
	private void performCashInInquiry() {
		
		try {
			
			log.info(" ##### performCashInInquiry ##### ");
			log.info(" ##### GL Account ##### " + cashInBean.getGlAccount().getIdAndValue());
			log.info(" ##### MSISDN ##### " + cashInBean.getMsisdn());
			
			glTemp = cashInBean.getGlAccount();
			UUID uuid = UUID.randomUUID();
			String traceNo = uuid.toString();
			log.info(" ##### TRACE NO ##### " + traceNo);
			final DebitInquiryRequest req = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			req.setOrigin(ORIGIN);
			req.setTraceNo(traceNo);
			req.setRepeat(false);
			UUID uuid2 = UUID.randomUUID();
			String convId = uuid2.toString();
			log.info(" ##### CONVERSATION ID ##### " + convId);
			req.setConversationId(convId);
			req.setFinal(false);
			req.setProcessingCode(PROCESSING_CODE);
			
			/* Get Current Calendar */
			Calendar cal = Calendar.getInstance();
			XMLGregorianCalendar transDate = PortalUtils.getSaveXMLGregorianCalendar(cal);
			log.info(" ### XML GREGORIAN CURRENT DATE ### " + transDate);
			req.setTransactionDateTime(transDate);
			
			req.setMerchantType(MERCHANT_TYPE);
			req.setMerchantId(MERCHANT_ID);
			req.setTerminalId(TERMINAL_ID);
			req.setAcquirerId(ACQUIRER_ID);
			
			req.setDescription(DESC);
			
			DebitAmountAndCurrencyType dbAmtAndCurType = new DebitAmountAndCurrencyType();
			dbAmtAndCurType.setCurrency(CURRENCY);
			dbAmtAndCurType.setValue(cashInBean.getCashinAmount());
			
			/* Request For Debit Account */
			DebitAccountType dbAcct = new DebitAccountType();
			dbAcct.setNumber(cashInBean.getGlAccount().getId());//1051050
			dbAcct.setType(DEBIT_ACCT_TYPE);
			dbAcct.setOrgUnitId(ORG_UNIT_ID);
			dbAcct.setFlags(DEBIT_ACCT_FLAGS);
			
			/* Request For Credit Account */
			DebitAccountType crAcct = new DebitAccountType();
			crAcct.setNumber(cashInBean.getMsisdn());
			crAcct.setType(CREDIT_ACCT_TYPE);
			crAcct.setFlags(CREDIT_ACCT_FLAGS);
			crAcct.setOrgUnitId(ORG_UNIT_ID);
			
			DebitTransactionType obj = new DebitTransactionType();
			obj.setDebitAccount(dbAcct);
			obj.setCreditAccount(crAcct);
			obj.setAmount(dbAmtAndCurType);
			
			DebitAttributeType dbAttr = new DebitAttributeType();
			dbAttr.setKey(DB_ATTR_TYPE_KEY);
			dbAttr.setValue(DB_ATTR_TYPE_VALUE);
			
			req.getTransaction().add(obj);
			req.getAttribute().add(dbAttr);
			
			final DebitInquiryResponse response = basePage.getDebitClient().inquiry(req);
			Status status = response.getStatus();
			log.info(" ### RESPONSE INQUIRY STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				cashInBean = ConverterUtils.convertToBankCustomCashInBean(response.getTransaction());
				cashInBean.setGlAccount(glTemp);
				
				cashInBean.setMsisdn(crAcct.getNumber());
				cashInBean.setCashinAmount(dbAmtAndCurType.getValue());
				
				/* Display Data On Page */
				setResponsePage(new BankPortalCustomCashInConfirmPage(cashInBean, PROCESSING_CODE));
			} else {  
				log.info(" ##### ELSE COMING ##### ");
				error(handleSpecificErrorMessage(response.getStatus().getCode()));
			}
		
		} catch (Exception e) {
			log.error(" ### An error occurred while calling service ### ", e);
			error(getLocalizer().getString("inquiry.failure.exception", BankPortalCustomCashInPanel.this));
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
			message = getLocalizer().getString("inquiry.fail", this);
		}
		return message;
	}

}
