package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAdviceRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitAdviceResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.btpnwow.core.messaging.facade.api.MessagingFacade;
import com.btpnwow.core.messaging.facade.contract.ListEntry;
import com.btpnwow.core.messaging.facade.contract.SendUssdPushRequest;
import com.btpnwow.core.messaging.facade.contract.SendUssdPushResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout.AgentPortalCustomCashOutPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout.AgentPortalCustomCashOutSuccessPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentPortalHomePage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class AgentPortalCustomCashOutConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(AgentPortalCustomCashOutConfirmPanel.class);

	protected BtpnBaseAgentPortalSelfCarePage basePage;

	protected AgentCustomCashOutBean cashOutBean;
	
	protected FeedbackPanel feedBack;
	
	@SpringBean(name = "messagingClient")
	private MessagingFacade messagingClient;
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitClient;

	public AgentPortalCustomCashOutConfirmPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage, AgentCustomCashOutBean cashOutBean) {
		super(id);
		
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		
		constructPanel();
	}

	protected void constructPanel() {
		Form<AgentPortalCustomCashOutConfirmPanel> form = new Form<AgentPortalCustomCashOutConfirmPanel>(
				"cashOutConfirmForm", new CompoundPropertyModel<AgentPortalCustomCashOutConfirmPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		form.add(new Label("cashOutBean.accountNumber"));
		form.add(new Label("cashOutBean.payerMsisdn"));
		form.add(new Label("cashOutBean.displayName"));
		form.add(new AmountLabel("cashOutBean.cashOutAmount", true, true));

		// Add Confirm Button
		Button confirmButton = new AjaxButton("submitConfirm") {
			
			private static final long serialVersionUID = 1L;

			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (!PortalUtils.exists(cashOutBean)) {
					cashOutBean = new AgentCustomCashOutBean();
				}
				
				performCashOutPosting();
				
				target.addComponent(feedBack);
			}
		};
		
		confirmButton.add(new AttributePrepender("onclick", Model.of("loading(submitConfirm)"), ";"));
		form.add(confirmButton);
		
		// Add Back button
		form.add(new Button("submitBack") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new AgentPortalCustomCashOutPage());
			}
		}.setDefaultFormProcessing(false));
		
		// Add Cancel button
		form.add(new Button("submitCancle") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(AgentPortalHomePage.class);
			}
		}.setDefaultFormProcessing(false));

		add(form);
	}

	/**
	 * This method handles the cash out posting via ussd request
	 */
	protected void performCashOutPosting() {
		BtpnCustomer payee = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		try {
			log.info(" ### (AgentPortalCustomCashOutConfirmPanel) performDebitPosting ### "); 
			
		    SendUssdPushRequest request = new SendUssdPushRequest();
		    
			request.setCode("custinitemoney2");
			
			request.getParameter().add(newListEntry(1, Long.toString(cashOutBean.getCashOutAmount().longValue() / 100))); 		// Amount
			request.getParameter().add(newListEntry(2, Long.toString(cashOutBean.getFeeAmount().longValue() / 100))); 			// Fee 
			
			request.getParameter().add(newListEntry(3, formatMsisdn(cashOutBean.getPayerMsisdn()))); 		// Customer MSISDN 
			request.getParameter().add(newListEntry(4, cashOutBean.getDisplayName())); 						// Customer Name
			request.getParameter().add(newListEntry(5, cashOutBean.getCustomerId())); 						// Customer Id
			
			request.getParameter().add(newListEntry(6, formatMsisdn(payee.getUsername()))); 				// Agent msisdn
			request.getParameter().add(newListEntry(7, payee.getDisplayName())); 							// Agent name
			request.getParameter().add(newListEntry(8, Long.toString(payee.getCustomerId())));			 	// Agent id

			request.getParameter().add(newListEntry(9,  cashOutBean.getConvId())); 							// Conversation id
			request.getParameter().add(newListEntry(10,Long.toString(cashOutBean.getTransactionDatetime()
					.toGregorianCalendar().getTimeInMillis()))); 											// Transaction date 
//			
			request.getParameter().add(newListEntry(11, cashOutBean.getProc_code()));						// Terminal Id
			request.getParameter().add(newListEntry(12, "6012"));											// Merchant Type
			request.getParameter().add(newListEntry(13, "BTPN"));											// Merchant Id
			request.getParameter().add(newListEntry(14, cashOutBean.getTerminalId()));						// Terminal Id
			request.getParameter().add(newListEntry(15, "213"));											// Terminal Id
			
			request.setMsisdn(formatMsisdn(cashOutBean.getPayerMsisdn())); 									// Customer Login
			request.setFlags(0);
				
			SendUssdPushResponse response = this.messagingClient.sendUssdPush(request);
			
			Status status = response.getStatus();
			
			log.info(" ### RESPONSE POSTING STATUS ### " + status.getCode() +"   "+status.getValue());
			
			if (MobiliserUtils.success(response)) {
				feedBack.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)) {

					private static final long serialVersionUID = 1L;
					
					private final AtomicInteger counter = new AtomicInteger(0); 
					
					@Override
					protected void onPostProcessTarget(AjaxRequestTarget target) {
						if (performDebitAdvice()) {
							stop();
						} else {
							log.info("##Counter:"+counter.incrementAndGet()+"##");
							
							if (counter.incrementAndGet() > 12) {
								stop();
								
								basePage.getMobiliserWebSession().error("Transaction timeout");
								
								getRequestCycle().setRedirect(true);
								setResponsePage(basePage);
							}
						}
					}
				});
			} else {
				error(handleSpecificErrorMessage(response.getStatus().getCode()));
			}
		} catch (Exception e) {
			log.error(" ### An Error occured while calling callDebitPostingService ### ",e);
			error(getLocalizer().getString("posting.failure.exception", AgentPortalCustomCashOutConfirmPanel.this));
		}
	}
	
	protected boolean performDebitAdvice() {
		boolean done;
		
		try {
			
			log.info(" ### (AgentPortalCustomCashOutConfirmPanel) performDebitAdvice ### "); 
			final DebitAdviceRequest req = basePage.getNewMobiliserRequest(DebitAdviceRequest.class); 
		
			req.setConversationId(cashOutBean.getConvId());  //inquiry
			log.info(" ### (AgentPortalCustomCashOutConfirmPanel) performDebitAdvice :: CONV ID ### "+cashOutBean.getConvId()); 
			req.setProcessingCode(cashOutBean.getProc_code()); 
			req.setTransactionDateTime(cashOutBean.getTransactionDatetime()); 
			
			req.setMerchantType("6012");
			req.setMerchantId("BTPN");
			req.setTerminalId(cashOutBean.getTerminalId());
			req.setAcquirerId("213");
		
			final DebitAdviceResponse response = debitClient.advice(req);
			
			Status status = response.getStatus();
			log.info(" ### RESPONSE DEBIT ADVICE STATUS ### " + status.getCode() +"   "+status.getValue());
		
			if (MobiliserUtils.success(response)) {
				log.info(" ### (BankPortalCustomCashInConfirmPanel) performCashInPosting ACCT NAME ### " + cashOutBean.getAccountName());
				
				if ((response.getTransaction() != null) && !response.getTransaction().isEmpty()) {
					for (DebitTransactionType transaction : response.getTransaction()) {
						cashOutBean.setStatus(transaction.getStatus());
						cashOutBean.setErrorCode(transaction.getErrorCode());
					}
				}
				
				log.info("status: " +cashOutBean.getStatus() +" | errorCode: " +cashOutBean.getErrorCode());
				
				if ((cashOutBean.getStatus().intValue() != 2) && (cashOutBean.getErrorCode().intValue() == 0)) { // status != 2 and errorCode == 0
					done = true;
					
					getRequestCycle().setRedirect(true);
					
					setResponsePage(new AgentPortalCustomCashOutSuccessPage(cashOutBean));
				} else if((cashOutBean.getStatus().intValue() == 2) && (cashOutBean.getErrorCode().intValue() == 0)) { // status == 2 and errorCode == 0, return false
					done = false;
				} else {
					//error dan return true
					basePage.getMobiliserWebSession().error(MobiliserUtils.errorMessage(cashOutBean.getErrorCode().intValue(), null, basePage));
					
					getRequestCycle().setRedirect(true);
					setResponsePage(basePage);
					
					done =  true;
				}
			} else {
				basePage.getMobiliserWebSession().error(MobiliserUtils.errorMessage(response, basePage));
				
				getRequestCycle().setRedirect(true);
				setResponsePage(basePage);
				
				done = true;
			}
		} catch (Exception e) {
			log.error(" ### An Error occured while calling callDebitPostingService ### ", e);
			
			basePage.getMobiliserWebSession().error(getLocalizer().getString("posting.failure.exception", AgentPortalCustomCashOutConfirmPanel.this));
			
			getRequestCycle().setRedirect(true);
			setResponsePage(basePage);
			
			done = true;
		}
		
		return done;
	}
	
	private ListEntry newListEntry(int order, String value) {
	
		ListEntry e = new ListEntry();
		e.setOrder(Integer.valueOf(order));
		e.setValue(value);
		
		return e;
	}
	
	private String formatMsisdn(String msisdn) {
		return new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig().getDefaultCountryCode()).getInternationalFormat();
	}

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
