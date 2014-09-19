package com.sybase365.mobiliser.web.btpn.bank.common.panels;

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
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.btpnwow.core.messaging.facade.api.MessagingFacade;
import com.btpnwow.core.messaging.facade.contract.ListEntry;
import com.btpnwow.core.messaging.facade.contract.SendUssdPushRequest;
import com.btpnwow.core.messaging.facade.contract.SendUssdPushResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType.Status;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCashInOutSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCustomCashInPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalTopAgentCashInPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCustomCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalTopAgentCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the BankPortalCashinConfirmPanel -> BankPortalCashInOutConfirmPanel  for bank portals.
 * 
 * @author Andi Samallangi W
 * @modified Feny Yanti
 */
public class BankPortalCashInOutConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BankPortalCashInOutConfirmPanel.class);

	/* DEBIT ATTRIBUTE TYPE */
//	private static final int POST_DB_ATTR_TYPE_KEY = 110;
//	private static final String POST_DB_ATTR_TYPE_VALUE = "1";

	protected BtpnMobiliserBasePage basePage;
	
	protected BankCustomCashInBean cashInBean;

	private FeedbackPanel feedBack;
	
	@SpringBean(name = "messagingClient")
	private MessagingFacade messagingClient;
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitClient;
	
	public BankPortalCashInOutConfirmPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashInBean cashInBean) {
		super(id);
		
		this.basePage = basePage;
		this.cashInBean = cashInBean == null ? new BankCustomCashInBean() : cashInBean;
		
		constructPanel();
	}

	protected void constructPanel() {
		final Form<BankPortalCashInOutConfirmPanel> form = new Form<BankPortalCashInOutConfirmPanel>(
				"customCashInConfirmForm",
				new CompoundPropertyModel<BankPortalCashInOutConfirmPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);
	
		form.add(new Label("headLine.cashinConfirm", cashInBean.getIsFinal()?"Cash-Out Confirm" : "Cash-In Confirm"));
		form.add(new Label("cashInBean.glAccount", cashInBean.getGlAccount().getIdAndValue()));
		form.add(new Label("cashInBean.msisdn"));
		form.add(new Label("cashInBean.accountName"));
		form.add(new AmountLabel("cashInBean.amount", true, true));
		form.add(new AmountLabel("cashInBean.fee"));
		
		// Add Confirm button
		Button confirmButton = new AjaxButton("submitConfirm") {
			private static final long serialVersionUID = 1L;
			
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					if (cashInBean.getIsFinal()) {
						performCashOutPosting();
					} else {
						performCashInPosting();
					}	
				} catch (Exception e) {
					log.error("#An error occurred while calling performCashInPosting", e);
					
					error(getLocalizer().getString("posting.failure.exception", BankPortalCashInOutConfirmPanel.this));
				}
				
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
				if(cashInBean.getProcessingCode().equals("100101")){
					setResponsePage(new BankPortalTopAgentCashInPage());
				}
				else if(cashInBean.getProcessingCode().equals("100201")){
					setResponsePage(new BankPortalTopAgentCashOutPage());
				}
				else if(cashInBean.getProcessingCode().equals("102401")){
					setResponsePage(new BankPortalCustomCashInPage());
				}
				else if(cashInBean.getProcessingCode().equals("102501")){
					setResponsePage(new BankPortalCustomCashOutPage());
				}
				
			}
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
			
			log.info(" ### (BankPortalCustomCashInOutConfirmPanel) performDebitPosting ### "); 
			final DebitPostingRequest req = basePage.getNewMobiliserRequest(DebitPostingRequest.class); 
		
			req.setConversationId(cashInBean.getConversationId());  //inquiry
			req.setFinal(true); //true
			req.setProcessingCode(cashInBean.getProcessingCode()); 
			req.setTransactionDateTime(cashInBean.getTransactionDatetime()); 
			
			req.setMerchantType(cashInBean.getMerchantType());
			req.setMerchantId(cashInBean.getMerchantId());
			req.setTerminalId(cashInBean.getTerminalId());
			req.setAcquirerId(cashInBean.getAcquirerId());
			req.setDescription(cashInBean.getIsFinal() ? "CASH OUT DEBIT POSTING" : "CASH IN DEBIT POSTING");
			
			final DebitPostingResponse response = debitClient.posting(req);
			Status status = response.getStatus();
			log.info(" ### RESPONSE POSTING STATUS ### " + status.getCode() +"   "+status.getValue());
		
			if (MobiliserUtils.success(response)) {
				log.info(" ### (BankPortalCustomCashInConfirmPanel) performCashInPosting ACCT NAME ### " + cashInBean.getAccountName());
		
				setResponsePage(new BankPortalCashInOutSuccessPage(cashInBean));
			} else {
				error(handleSpecificErrorMessage(response.getStatus().getCode()));
			}
		
		} catch (Exception e) {
			log.error(" ### An Error occured while calling callDebitPostingService ### ",e);
			error(getLocalizer().getString("posting.failure.exception", BankPortalCashInOutConfirmPanel.this));
		}
	}
	
	
	private ListEntry newListEntry(int order, String value) {
		ListEntry e = new ListEntry();
		
		e.setOrder(Integer.valueOf(order));
		e.setValue(value);
		
		return e;
	}
	
	/**
	 * This method handles the cash out posting via ussd request
	 */
	protected void performCashOutPosting(){
		
		try {
			log.info(" ### (BankPortalCustomCashInOutConfirmPanel) performDebitPosting ### "); 
			
		    SendUssdPushRequest request = new SendUssdPushRequest();
			request.setCode("CashoutNoAgentInit");
				
			request.getParameter().add(newListEntry(1, Long.valueOf(cashInBean.getAmount()/100).toString())); // amount
			request.getParameter().add(newListEntry(2, Long.valueOf(cashInBean.getFee()/100).toString())); // fee 
			
			request.getParameter().add(newListEntry(3, cashInBean.getMsisdn())); // customer msisdn 
			request.getParameter().add(newListEntry(4, cashInBean.getAccountName())); // customer name
			request.getParameter().add(newListEntry(5, "")); // customer id
			
			request.getParameter().add(newListEntry(6, "")); // agent name
			request.getParameter().add(newListEntry(7, "Bank BTPN")); // agent name
			request.getParameter().add(newListEntry(8, cashInBean.getGlAccount().getId())); // agent id

			request.getParameter().add(newListEntry(9, cashInBean.getConversationId())); // conversation id
			request.getParameter().add(newListEntry(10, String.valueOf( cashInBean.getTransactionDatetime().toGregorianCalendar().getTimeInMillis() ))); // transaction date 
			request.getParameter().add(newListEntry(11, cashInBean.getProcessingCode()));
			request.getParameter().add(newListEntry(12, cashInBean.getMerchantType()));
			request.getParameter().add(newListEntry(13, cashInBean.getMerchantId()));
			request.getParameter().add(newListEntry(14, cashInBean.getTerminalId())); //terminalId
			
			request.getParameter().add(newListEntry(15, cashInBean.getAcquirerId()));
			request.setMsisdn( cashInBean.getMsisdn()); // customer id
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
			error(getLocalizer().getString("posting.failure.exception", BankPortalCashInOutConfirmPanel.this));
		}
	}
	

	protected boolean performDebitAdvice() {
		boolean done;
		
		try {
			
			log.info(" ### (BankPortalCustomCashInOutConfirmPanel) performDebitAdvice ### "); 
			final DebitAdviceRequest req = basePage.getNewMobiliserRequest(DebitAdviceRequest.class); 
		
			req.setConversationId(cashInBean.getConversationId());  //inquiry
			req.setProcessingCode(cashInBean.getProcessingCode()); 
			req.setTransactionDateTime(cashInBean.getTransactionDatetime()); 
			
			req.setMerchantType(cashInBean.getMerchantType());
			req.setMerchantId(cashInBean.getMerchantId());
			req.setTerminalId(cashInBean.getTerminalId());
			req.setAcquirerId(cashInBean.getAcquirerId());
		
			final DebitAdviceResponse response = debitClient.advice(req);
			Status status = response.getStatus();
			log.info(" ### RESPONSE POSTING STATUS ### " + status.getCode() +"   "+status.getValue());
		
			if (MobiliserUtils.success(response)) {
				log.info(" ### (BankPortalCustomCashInConfirmPanel) performCashInPosting ACCT NAME ### " + cashInBean.getAccountName());
				
				if ((response.getTransaction() != null) && !response.getTransaction().isEmpty()) {
					for (DebitTransactionType transaction : response.getTransaction()) {
						cashInBean.setStatus(transaction.getStatus());
						cashInBean.setErrorCode(transaction.getErrorCode());
					}
				}
				log.info("status:"+cashInBean.getStatus()+" | errorCode:"+cashInBean.getErrorCode());
				
				if(cashInBean.getStatus() !=2 && cashInBean.getErrorCode() == 0 ){ // status != 2 and errorCode == 0
					done = true;
					
					getRequestCycle().setRedirect(true);
					setResponsePage(new BankPortalCashInOutSuccessPage(cashInBean));
				}
				else if(cashInBean.getStatus() == 2 && cashInBean.getErrorCode() == 0 ){ // status == 2 and errorCode == 0, return false
					done = false;
				}
				else{
					//error dan return true
					basePage.getMobiliserWebSession().error(MobiliserUtils.errorMessage(cashInBean.getErrorCode().intValue(), null, basePage));
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
			log.error(" ### An Error occured while calling callDebitPostingService ### ",e);
			
			basePage.getMobiliserWebSession().error(getLocalizer().getString("posting.failure.exception", BankPortalCashInOutConfirmPanel.this));
			getRequestCycle().setRedirect(true);
			setResponsePage(basePage);
			
			done = true;
		}
		
		return done;
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
