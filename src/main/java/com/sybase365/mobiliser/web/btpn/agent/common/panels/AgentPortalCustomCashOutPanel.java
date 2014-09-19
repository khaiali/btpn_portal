package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.customer.facade.contract.GetCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.GetCustomerExResponse;
import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAccountType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAmountAndCurrencyType;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitInquiryResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout.AgentPortalCustomCashOutConfirmPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class AgentPortalCustomCashOutPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(AgentPortalCustomCashOutPanel.class);

	private static final String PROCODE_TOPAGENT = "102201";
	private static final String PROCODE_CUST = "100401";
	
	private static final String PARENT_ID = "parentId";
	private static final String CUST_TYPE_CATEGORY = "custTypeCategory";
	
	protected BtpnMobiliserBasePage basePage;
	
	protected AgentCustomCashOutBean cashOutBean;
	
	private FeedbackPanel feedBack;
	private Component msisdnComponent;
	private Component amountComponent;
	
	@SpringBean(name = "customerClient")
	private CustomerFacade customerClient;
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitClient; 
	
	public AgentPortalCustomCashOutPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage) {
		super(id);
		
		this.basePage = basePage;
		
		constructPanel();
	}

	protected void constructPanel() {
		final Form<AgentPortalCustomCashOutPanel> form = new Form<AgentPortalCustomCashOutPanel>(
				"agentCustomCashOutForm", new CompoundPropertyModel<AgentPortalCustomCashOutPanel>(this));
		
		feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		
//		form.add(msisdnComponent = new TextField<String>("cashOutBean.payeeMsisdn").setRequired(true)
//				.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
//				.add(new ErrorIndicator()));
//			msisdnComponent.setOutputMarkupId(true);
			
		form.add(msisdnComponent = new TextField<String>("cashOutBean.payerMsisdn")
				.setRequired(true)
				.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
				.add(new ErrorIndicator()));
		msisdnComponent.setOutputMarkupId(true);	
		
		form.add(amountComponent = new AmountTextField<Long>("cashOutBean.cashOutAmount", Long.class, false)
				.setRequired(true)
				.add(new ErrorIndicator()));
		amountComponent.setOutputMarkupId(true);

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (!PortalUtils.exists(cashOutBean)) {
					cashOutBean = new AgentCustomCashOutBean();
				}
				
				beforeInquiry();
			};
		});
		
		add(form);
	}
	
	
	private void beforeInquiry() {
		
		BtpnCustomer loggedIn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		/* Cek Customer Type (TOP AGENT OR AGENT) */
		if (loggedIn.getCustomerTypeCategoryId() == BtpnConstants.VALID_TOP_AGENT_MSISDN_RESPONSE_CODE) {
			
			/* Cash Out Agent by Top Agent */
			Map<String, String> custCashOut = getCustomerEx(formatMsisdn(cashOutBean.getPayerMsisdn()));
			
			if (custCashOut == null) {
				return;
			}
			
			if (Long.toString(loggedIn.getCustomerId()).equals(custCashOut.get(PARENT_ID))) {
				performCashOutInqTopAgent(PROCODE_TOPAGENT);
			} else {
				error(getLocalizer().getString("cashOutBean.payerMsisdn.NotValid", this));
			}
		} else {
			/* Cash Out Customer By Agent */
			performCashOutInquiry(PROCODE_CUST);
		}
	}
	
	
	private Map<String, String> getCustomerEx(String msisdn) {
		GetCustomerExResponse response = null;
		
		Map<String, String> ctcMap = new HashMap<String, String>();
		
		try {
			
			CustomerIdentificationType cit = new CustomerIdentificationType();
			cit.setType(0);
			cit.setValue(formatMsisdn(msisdn));
			
			GetCustomerExRequest request = basePage.getNewMobiliserRequest(GetCustomerExRequest.class);
			request.setFlags(0);
			request.setIdentification(cit);
			
			response = customerClient.get(request);
			
			log.info(" ### (AgentPortalCustomCashInPanel::getCustomerParentId) RESPONSE ### "+response.getStatus().getCode());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				Long parentId = response.getInformation().getParentId();
				Integer custTypeCategory = response.getInformation().getCustomerTypeCategory();
				Long agentId = response.getInformation().getId();
				String agentName = response.getInformation().getName();
				
				ctcMap.put(PARENT_ID, String.valueOf(parentId));
				ctcMap.put(CUST_TYPE_CATEGORY, String.valueOf(custTypeCategory));
				
				cashOutBean.setAgentId(String.valueOf(agentId));
				cashOutBean.setAgentName(agentName);
				
//				ctcMap.put("agentId", String.valueOf(agentId));
//				ctcMap.put("agentName", String.valueOf(agentName));
				
				return ctcMap;
			}
			
			// Specific error message handling
			final String messageKey = "error." + response.getStatus().getCode();
			handleSpecificErrorMessage(messageKey, "error.cashOut.fail");
		} catch (Exception ex) {
			log.error("#An error occurred while calling getCustomerByIdentification service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		
		return null;
	}
	
	
	/**
	 * This method handles the cash out inquiry.
	 */
	private void performCashOutInqTopAgent(String proc_code) {
		
		try {
			
			log.info(" ##### (AgentPortalCustomCashOutPanel::performCashOutInqTopAgent) ##### ");
			String topAgentMsisdn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			
			String conversationId = UUID.randomUUID().toString();
			String terminalId = formatMsisdn(topAgentMsisdn);
			
			cashOutBean.setAccountNumber(topAgentMsisdn);
			
			/* Get Current Calendar */
			XMLGregorianCalendar transactionDate = PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance());
			
			final DebitInquiryRequest request = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			
			request.setRepeat(Boolean.FALSE);
			request.setConversationId(conversationId);
			request.setFinal(false);
			
			request.setProcessingCode(proc_code);
			request.setTransactionDateTime(transactionDate);
			request.setMerchantType("6012");
			request.setMerchantId("BTPN");
			request.setTerminalId(terminalId);
			request.setAcquirerId("213");
			
			request.setDescription("Agent Cash Out via Top Agent");
			
			DebitAmountAndCurrencyType amount = new DebitAmountAndCurrencyType();
			amount.setCurrency("IDR");
			amount.setValue(cashOutBean.getCashOutAmount().longValue());
			
			DebitAccountType debit = new DebitAccountType();
			debit.setNumber(formatMsisdn(cashOutBean.getPayerMsisdn())); 				// AGENT MSISDN
			debit.setType("MSISDN");
			debit.setFlags(0);
			
			DebitAccountType credit = new DebitAccountType();
			credit.setNumber(formatMsisdn(topAgentMsisdn)); 							// TOP AGENT MSISDN
			credit.setType("MSISDN");
			credit.setFlags(0);
			
			DebitTransactionType transaction = new DebitTransactionType();
			transaction.setDebitAccount(debit);
			transaction.setCreditAccount(credit);
			transaction.setAmount(amount);
			
			request.getTransaction().add(transaction);
			
			final DebitInquiryResponse response = debitClient.inquiry(request);
			
			Status status = response.getStatus();
			log.info(" ### RESPONSE TOP AGENT INQUIRY 1 STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {

				cashOutBean.setConvId(conversationId);
				cashOutBean.setProc_code(proc_code);
				cashOutBean.setPayerMsisdn(formatMsisdn(cashOutBean.getPayerMsisdn()));
				cashOutBean.setTransactionDatetime(transactionDate);
				cashOutBean.setTerminalId(terminalId);
				
				List<DebitTransactionType> obj = response.getTransaction();
				
				long realAmount = 0;
				for (DebitTransactionType txnBean : obj) {
					DebitAmountAndCurrencyType deb = txnBean.getDebitAccount().getFee();
					long cashOutAmount = cashOutBean.getCashOutAmount().longValue();
					long feeDebit = deb.getValue();
					realAmount = cashOutAmount + feeDebit;
				}
				
				performCashOutInqTopAgent2(cashOutBean, realAmount);
				
			} else {  
				handleSpecificErrorMessage("error." + response.getStatus().getCode(), "error.cashOut.fail");
			}
		} catch (Exception e) {
			log.error(" ### An error occurred while calling service ### ", e);
			
			error(getLocalizer().getString("inquiry.failure.exception", AgentPortalCustomCashOutPanel.this));
		}
	}
	
	
	
	private void performCashOutInqTopAgent2(AgentCustomCashOutBean bean, long realAmount) {
		
		try {
			
			log.info(" ##### (AgentPortalCustomCashOutPanel::performCashOutInqTopAgent) ##### ");
			String topAgentMsisdn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			
			String conversationId = UUID.randomUUID().toString();
			String terminalId = formatMsisdn(topAgentMsisdn);
			
			cashOutBean.setAccountNumber(topAgentMsisdn);
			
			/* Get Current Calendar */
			XMLGregorianCalendar transactionDate = PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance());
			
			final DebitInquiryRequest request = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			
			request.setRepeat(Boolean.FALSE);
			request.setConversationId(conversationId);
			request.setFinal(false);
			
			request.setProcessingCode(bean.getProc_code());
			request.setTransactionDateTime(transactionDate);
			request.setMerchantType("6012");
			request.setMerchantId("BTPN");
			request.setTerminalId(terminalId);
			request.setAcquirerId("213");
			
			request.setDescription("Agent Cash Out via Top Agent");
			
			DebitAmountAndCurrencyType amount = new DebitAmountAndCurrencyType();
			amount.setCurrency("IDR");
			amount.setValue(realAmount);
			
			DebitAccountType debit = new DebitAccountType();
			debit.setNumber(formatMsisdn(cashOutBean.getPayerMsisdn())); 				// AGENT MSISDN
			debit.setType("MSISDN");
			debit.setFlags(0);
			
			DebitAccountType credit = new DebitAccountType();
			credit.setNumber(formatMsisdn(topAgentMsisdn)); 							// TOP AGENT MSISDN
			credit.setType("MSISDN");
			credit.setFlags(0);
			
			DebitTransactionType transaction = new DebitTransactionType();
			transaction.setDebitAccount(debit);
			transaction.setCreditAccount(credit);
			transaction.setAmount(amount);
			
			request.getTransaction().add(transaction);
			
			final DebitInquiryResponse response = debitClient.inquiry(request);
			
			Status status = response.getStatus();
			log.info(" ### RESPONSE TOP AGENT INQUIRY 2 STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				fillAgentCustomCashOutBean(cashOutBean, response.getTransaction());
				
				cashOutBean.setConvId(conversationId);
				cashOutBean.setProc_code(bean.getProc_code());
				cashOutBean.setPayerMsisdn(formatMsisdn(cashOutBean.getPayerMsisdn()));
				cashOutBean.setTransactionDatetime(transactionDate);
				cashOutBean.setTerminalId(terminalId);
				
				/* Display Data On Page */
				setResponsePage(new AgentPortalCustomCashOutConfirmPage(cashOutBean));
			} else {  
				handleSpecificErrorMessage("error." + response.getStatus().getCode(), "error.cashOut.fail");
			}
			
		} catch (Exception e) {
			log.error(" ### An error occurred while calling service ### ", e);
			error(getLocalizer().getString("inquiry.failure.exception", AgentPortalCustomCashOutPanel.this));
		}
	}
	
	

	/**
	 * This method handles the cash in inquiry.
	 */
	private void performCashOutInquiry(String proc_code) {
		
		try {
			
			log.info(" ##### (AgentPortalCustomCashOutPanel::performCashOutInquiry) ##### ");
			String agentMsisdn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			
			cashOutBean.setAccountNumber(agentMsisdn);
			
			String conversationId = UUID.randomUUID().toString();
			String terminalId = formatMsisdn(agentMsisdn);
			
			long cashOutAmount = cashOutBean.getCashOutAmount().longValue();
			
			/* Get Current Calendar */
			XMLGregorianCalendar transactionDate = PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance());
			
			final DebitInquiryRequest request = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			
			request.setRepeat(Boolean.FALSE);
			request.setConversationId(conversationId);
			request.setFinal(false);
			
			request.setProcessingCode(proc_code);
			request.setTransactionDateTime(transactionDate);
			request.setMerchantType("6012");
			request.setMerchantId("BTPN");
			request.setTerminalId(terminalId);
			request.setAcquirerId("213");
			
			request.setDescription("Customer Cash Out via Agent");
			
			/** Transaction #1 */
			DebitAccountType debit1 = new DebitAccountType();
			debit1.setNumber(formatMsisdn(cashOutBean.getPayerMsisdn())); 		// CUSTOMER MSISDN
			debit1.setType("MSISDN");
			debit1.setFlags(0);
			
			DebitAccountType credit1 = new DebitAccountType();
			credit1.setNumber(formatMsisdn(cashOutBean.getPayerMsisdn())); 		// CUSTOMER E-MONEY
			credit1.setType("2");
			credit1.setFlags(0);
			
			DebitAmountAndCurrencyType amount1 = new DebitAmountAndCurrencyType();
			amount1.setCurrency("IDR");
			amount1.setValue(cashOutAmount);
			
			DebitTransactionType transaction1 = new DebitTransactionType();
			transaction1.setDebitAccount(debit1);
			transaction1.setCreditAccount(credit1);
			transaction1.setAmount(amount1);
			
			/** Transaction #2 */
			DebitAccountType debit2 = new DebitAccountType();
			debit2.setNumber(formatMsisdn(cashOutBean.getPayerMsisdn())); 		// CUSTOMER E-MONEY
			debit2.setType("2");
			debit2.setFlags(0);
			
			DebitAccountType credit2 = new DebitAccountType();
			credit2.setNumber(formatMsisdn(agentMsisdn)); 						// AGENT MSISDN
			credit2.setType("MSISDN");
			credit2.setFlags(0);
			
			DebitAmountAndCurrencyType amount2 = new DebitAmountAndCurrencyType();
			amount2.setCurrency("IDR");
			amount2.setValue(cashOutAmount);
			
			DebitTransactionType transaction2 = new DebitTransactionType();
			transaction2.setDebitAccount(debit2);
			transaction2.setCreditAccount(credit2);
			transaction2.setAmount(amount2);
			
			request.getTransaction().add(transaction1);
			request.getTransaction().add(transaction2);	
			
			final DebitInquiryResponse response = debitClient.inquiry(request);
			
			Status status = response.getStatus();
			log.info(" ### RESPONSE CASH OUT INQUIRY 1 STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {

				cashOutBean.setConvId(conversationId);
				cashOutBean.setProc_code(proc_code);
				cashOutBean.setPayerMsisdn(formatMsisdn(cashOutBean.getPayerMsisdn()));
				cashOutBean.setTransactionDatetime(transactionDate);
				cashOutBean.setTerminalId(terminalId);
				
				DebitTransactionType obj = response.getTransaction().get(1);
				DebitAmountAndCurrencyType debit = obj.getDebitAccount().getFee();
				long feeDebit = debit.getValue();
				long realAmount = cashOutAmount + feeDebit;
				
				performCashOutInquiry2(cashOutBean, realAmount);

			} else {  
				handleSpecificErrorMessage("error." + response.getStatus().getCode(), "error.cashOut.fail");
			}
			
		} catch (Exception e) {
			log.error(" ### An error occurred while calling service ### ", e);
			error(getLocalizer().getString("inquiry.failure.exception", AgentPortalCustomCashOutPanel.this));
		}
	}
	
	
	
	private void performCashOutInquiry2(AgentCustomCashOutBean bean, long realAmount) {
		
		try {
			
			log.info(" ##### (AgentPortalCustomCashOutPanel::performCashOutInquiry2) ##### ");
			String agentMsisdn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			
			cashOutBean.setAccountNumber(agentMsisdn);
			
			String conversationId = UUID.randomUUID().toString();
			String terminalId = formatMsisdn(agentMsisdn);
			
			/* Get Current Calendar */
			XMLGregorianCalendar transactionDate = PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance());
			
			final DebitInquiryRequest request = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			
			request.setRepeat(Boolean.FALSE);
			request.setConversationId(conversationId);
			request.setFinal(false);
			
			request.setProcessingCode(bean.getProc_code());
			request.setTransactionDateTime(transactionDate);
			request.setMerchantType("6012");
			request.setMerchantId("BTPN");
			request.setTerminalId(terminalId);
			request.setAcquirerId("213");
			
			request.setDescription("Customer Cash Out via Agent");
			
			/** Transaction #1 */
			DebitAccountType debit1 = new DebitAccountType();
			debit1.setNumber(formatMsisdn(cashOutBean.getPayerMsisdn())); 			// CUSTOMER MSISDN
			debit1.setType("MSISDN");
			debit1.setFlags(0);
			
			DebitAccountType credit1 = new DebitAccountType();
			credit1.setNumber(formatMsisdn(cashOutBean.getPayerMsisdn())); 			// CUSTOMER E-MONEY
			credit1.setType("2");
			credit1.setFlags(0);
			
			DebitAmountAndCurrencyType amount1 = new DebitAmountAndCurrencyType();
			amount1.setCurrency("IDR");
			amount1.setValue(realAmount);
			
			DebitTransactionType transaction1 = new DebitTransactionType();
			transaction1.setDebitAccount(debit1);
			transaction1.setCreditAccount(credit1);
			transaction1.setAmount(amount1);
			
			/** Transaction #2 */
			DebitAccountType debit2 = new DebitAccountType();
			debit2.setNumber(formatMsisdn(cashOutBean.getPayerMsisdn())); 			// CUSTOMER E-MONEY
			debit2.setType("2");
			debit2.setFlags(0);
			
			DebitAccountType credit2 = new DebitAccountType();
			credit2.setNumber(formatMsisdn(agentMsisdn)); 							// AGENT MSISDN
			credit2.setType("MSISDN");
			credit2.setFlags(0);
			
			DebitAmountAndCurrencyType amount2 = new DebitAmountAndCurrencyType();
			amount2.setCurrency("IDR");
			amount2.setValue(realAmount);
			
			DebitTransactionType transaction2 = new DebitTransactionType();
			transaction2.setDebitAccount(debit2);
			transaction2.setCreditAccount(credit2);
			transaction2.setAmount(amount2);
			
			request.getTransaction().add(transaction1);
			request.getTransaction().add(transaction2);	
			
			final DebitInquiryResponse response = debitClient.inquiry(request);
			
			Status status = response.getStatus();
			log.info(" ### RESPONSE CASH OUT INQUIRY 2 STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				fillAgentCustomCashOutBean(cashOutBean, response.getTransaction());
				
				cashOutBean.setConvId(conversationId);
				cashOutBean.setProc_code(bean.getProc_code());
				cashOutBean.setPayerMsisdn(formatMsisdn(cashOutBean.getPayerMsisdn()));
				cashOutBean.setTransactionDatetime(transactionDate);
				cashOutBean.setTerminalId(terminalId);
				
				/* Display Data On Page */
				setResponsePage(new AgentPortalCustomCashOutConfirmPage(cashOutBean));
			} else {  
				handleSpecificErrorMessage("error." + response.getStatus().getCode(), "error.cashOut.fail");
			}
		} catch (Exception e) {
			log.error(" ### An error occurred while calling service ### ", e);
			
			error(getLocalizer().getString("inquiry.failure.exception", AgentPortalCustomCashOutPanel.this));
		}
	}
	
	
	private void fillAgentCustomCashOutBean(AgentCustomCashOutBean cashOutBean, List<DebitTransactionType> txnList) {
		for (DebitTransactionType txnBean : txnList) {
			cashOutBean.setDisplayName(txnBean.getCreditAccount().getHolderName());
			cashOutBean.setFeeAmount(Long.valueOf(txnBean.getFee().getValue()));
		}
	}
	
	private String formatMsisdn(String msisdn) {
		return new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig().getDefaultCountryCode()).getInternationalFormat();
	}

	private void handleSpecificErrorMessage(final String messageKey, final String genericKey) {
		String message = getLocalizer().getString(messageKey, this);
		
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString(genericKey, this);
		}
		
		error(message);
	}
}
