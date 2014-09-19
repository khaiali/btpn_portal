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
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.AgentPortalCustomCashInConfirmPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class AgentPortalCustomCashInPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(AgentPortalCustomCashInPanel.class);

	private static final String PROCODE_TOPAGENT = "102101";
	private static final String PROCODE_CUST = "100301";
	
	private static final String PARENT_ID = "parentId";
	private static final String CUST_TYPE_CATEGORY = "custTypeCategory";
	
	protected BtpnMobiliserBasePage basePage;
	
	protected AgentCustomCashInBean cashInBean;
	
	private FeedbackPanel feedBack;
	private Component msisdnComponent;
	private Component amountComponent;
	
	@SpringBean(name = "customerClient")
	private CustomerFacade customerClient;
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitClient; 

	public AgentPortalCustomCashInPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage) {
		super(id);
		
		this.basePage = basePage;
		
		constructPanel();
	}

	protected void constructPanel() {
		log.info(" ### (AgentPortalCustomCashInPanel::constructPanel) ### ");
		
		final Form<AgentPortalCustomCashInPanel> form = new Form<AgentPortalCustomCashInPanel>(
				"agentCustomCashInForm", new CompoundPropertyModel<AgentPortalCustomCashInPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		
		form.add(msisdnComponent = new TextField<String>("cashInBean.msisdn")
				.setRequired(true)
				.add(BtpnConstants.PHONE_NUMBER_VALIDATOR)
				.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
				.add(new ErrorIndicator()));
		msisdnComponent.setOutputMarkupId(true);
		
		form.add(amountComponent = new AmountTextField<Long>("cashInBean.cashinAmount", Long.class, false)
				.setRequired(true)
				.add(new ErrorIndicator()));
		amountComponent.setOutputMarkupId(true);
		
		form.add(new Button("submitButton") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onSubmit() {
				if (!PortalUtils.exists(cashInBean)){
					cashInBean = new AgentCustomCashInBean();
				}
				
				beforeInquiry();
			}
		});
		
		add(form);
	}
	
	private void beforeInquiry() {
		BtpnCustomer payer = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		/* Cek Customer Type (TOP AGENT OR AGENT) */
		if (payer.getCustomerTypeCategoryId() == BtpnConstants.VALID_TOP_AGENT_MSISDN_RESPONSE_CODE) {
			Map<String, String> payee = getCustomerEx(formatMsisdn(cashInBean.getMsisdn()));
			
			if (payee == null) {
				return;
			}
			
			if (Long.toString(payer.getCustomerId()).equals(payee.get(PARENT_ID))) {
				performCashInInqTopAgent(PROCODE_TOPAGENT);

			} else {
				error(getLocalizer().getString("cashInBean.payeeMsisdn.NotValid", this));
			}
		} else {
			/* Cash in Customer By Agent */
			performCashInInquiry(PROCODE_CUST);
		}
	}
	
	private Map<String, String> getCustomerEx(String msisdn) {
		GetCustomerExResponse response = null;
		
		Map<String, String> ctcMap = new HashMap<String, String>();
		
		try {
			CustomerIdentificationType cit = new CustomerIdentificationType();
			cit.setType(0);
			cit.setValue(msisdn);
			
			GetCustomerExRequest request = basePage.getNewMobiliserRequest(GetCustomerExRequest.class);
			request.setFlags(0);
			request.setIdentification(cit);
			
			response = customerClient.get(request);
			log.info(" ### (AgentPortalCustomCashInPanel::getCustomerParentId) RESPONSE ### "+response.getStatus().getCode());
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				Long parentId = response.getInformation().getParentId();
				Integer custTypeCategory = response.getInformation().getCustomerTypeCategory();
				
				ctcMap.put(PARENT_ID, String.valueOf(parentId));
				ctcMap.put(CUST_TYPE_CATEGORY, String.valueOf(custTypeCategory));
				
				return ctcMap;
			}
			
			// Specific error message handling
			error(MobiliserUtils.errorMessage(response, basePage));
		} catch (Exception ex) {
			log.error("#An error occurred while calling getCustomerByIdentification service.", ex);
			
			error(getLocalizer().getString("error.exception", this));
		}
		
		return null;
	}
	
	private void performCashInInqTopAgent(String proc_code) {
		String topAgentMsisdn = formatMsisdn(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		
		cashInBean.setAccountNumber(topAgentMsisdn);
		
		try {
			long cashInAmount = cashInBean.getCashinAmount().longValue();
			
			String conversationId = UUID.randomUUID().toString();
			String terminalId = topAgentMsisdn;
			
			XMLGregorianCalendar transactionDate = PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance());
			
			DebitInquiryRequest request = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			
			request.setRepeat(Boolean.FALSE);
			request.setConversationId(conversationId);
			request.setFinal(false);
			
			request.setProcessingCode(proc_code);
			request.setTransactionDateTime(transactionDate);
			request.setMerchantType("6012");
			request.setMerchantId("BTPN");
			request.setTerminalId(terminalId);
			request.setAcquirerId("213");
			
			request.setDescription("Agent Cash In via Top Agent");
			
			DebitAmountAndCurrencyType amount = new DebitAmountAndCurrencyType();
			amount.setCurrency("IDR");
			amount.setValue(cashInAmount);
			
			/* Request For Debit Account */
			DebitAccountType debit = new DebitAccountType();
			debit.setNumber(topAgentMsisdn);
			debit.setType("MSISDN");
			debit.setFlags(0);
			
			/* Request For Credit Account */
			DebitAccountType credit = new DebitAccountType();
			credit.setNumber(formatMsisdn(cashInBean.getMsisdn()));
			credit.setType("MSISDN");
			credit.setFlags(0);
			
			DebitTransactionType transaction = new DebitTransactionType();
			transaction.setDebitAccount(debit);
			transaction.setCreditAccount(credit);
			transaction.setAmount(amount);
			
			request.getTransaction().add(transaction);
			
			final DebitInquiryResponse response = debitClient.inquiry(request);
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				fillAgentCustomCashInBean(cashInBean, response.getTransaction());
				
				cashInBean.setConvId(conversationId);
				cashInBean.setProc_code(proc_code);
				cashInBean.setTransactionDatetime(transactionDate);
				cashInBean.setTerminalId(terminalId);
				
				setResponsePage(new AgentPortalCustomCashInConfirmPage(cashInBean));
			} else {  
				error(MobiliserUtils.errorMessage(response, basePage));
			}
		} catch (Exception e) {
			log.error(" ### An error occurred while calling service ### ", e);
			
			error(getLocalizer().getString("inquiry.failure.exception", AgentPortalCustomCashInPanel.this));
		}
	}
	
	/**
	 * This method handles the cash in inquiry.
	 */
	private void performCashInInquiry(String proc_code) {
		String agentMsisdn = formatMsisdn(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		
		String customerMsisdn = formatMsisdn(cashInBean.getMsisdn());
		
		cashInBean.setAccountNumber(agentMsisdn);
		
		try {
			long cashInAmount = cashInBean.getCashinAmount().longValue();
			
			String conversationId = UUID.randomUUID().toString();
			String terminalId = agentMsisdn;
			
			XMLGregorianCalendar transactionDate = PortalUtils.getSaveXMLGregorianCalendar(Calendar.getInstance());
			
			DebitInquiryRequest request = basePage.getNewMobiliserRequest(DebitInquiryRequest.class); 
			
			request.setRepeat(Boolean.FALSE);
			request.setConversationId(conversationId);
			request.setFinal(false);
			
			request.setProcessingCode(proc_code);
			request.setTransactionDateTime(transactionDate);
			request.setMerchantType("6012");
			request.setMerchantId("BTPN");
			request.setTerminalId(terminalId);
			request.setAcquirerId("213");
			
			request.setDescription("Customer Cash In via Agent");
			
			/* transaction #1 */
			DebitAccountType debit1 = new DebitAccountType();
			debit1.setNumber(agentMsisdn); // agent msisdn
			debit1.setType("MSISDN");
			debit1.setFlags(0);
			
			DebitAccountType credit1 = new DebitAccountType();
			credit1.setNumber(customerMsisdn); // customer e-money
			credit1.setType("2");
			credit1.setFlags(0);
			
			DebitAmountAndCurrencyType amount1 = new DebitAmountAndCurrencyType();
			amount1.setCurrency("IDR");
			amount1.setValue(cashInAmount);
			
			DebitTransactionType transaction1 = new DebitTransactionType();
			transaction1.setDebitAccount(debit1);
			transaction1.setCreditAccount(credit1);
			transaction1.setAmount(amount1);
			
			/* transaction #2 */
			DebitAccountType debit2 = new DebitAccountType();
			debit2.setNumber(customerMsisdn); // customer e-money
			debit2.setType("2");
			debit2.setFlags(0);
			
			DebitAccountType credit2 = new DebitAccountType();
			credit2.setNumber(customerMsisdn); // customer msisdn
			credit2.setType("MSISDN");
			credit2.setFlags(0);
			
			DebitAmountAndCurrencyType amount2 = new DebitAmountAndCurrencyType();
			amount2.setCurrency("IDR");
			amount2.setValue(cashInAmount);
			
			DebitTransactionType transaction2 = new DebitTransactionType();
			transaction2.setDebitAccount(debit2);
			transaction2.setCreditAccount(credit2);
			transaction2.setAmount(amount2);
			
			request.getTransaction().add(transaction1);
			request.getTransaction().add(transaction2);	
			
			final DebitInquiryResponse response = debitClient.inquiry(request);
			
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				fillAgentCustomCashInBean(cashInBean, response.getTransaction());
				
				cashInBean.setConvId(conversationId);
				cashInBean.setProc_code(proc_code);
				cashInBean.setTransactionDatetime(transactionDate);
				cashInBean.setTerminalId(terminalId);
				
				setResponsePage(new AgentPortalCustomCashInConfirmPage(cashInBean));
			} else {  
				error(MobiliserUtils.errorMessage(response, basePage));
			}
		} catch (Exception ex) {
			log.error(" ### An error occurred while calling service ### ", ex);
			
			error(getLocalizer().getString("inquiry.failure.exception", AgentPortalCustomCashInPanel.this));
		}
	}
	
	private void fillAgentCustomCashInBean(AgentCustomCashInBean cashInBean, List<DebitTransactionType> txnList) {
		for (DebitTransactionType txnBean : txnList) {
			cashInBean.setDisplayName(txnBean.getCreditAccount().getHolderName());
		}
	}
	
	private String formatMsisdn(String msisdn) {
		return new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig().getDefaultCountryCode()).getInternationalFormat();
	}
}
