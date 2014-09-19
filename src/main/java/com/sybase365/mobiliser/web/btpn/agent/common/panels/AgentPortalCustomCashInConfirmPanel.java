package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.debit.facade.api.DebitFacade;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingRequest;
import com.btpnwow.core.debit.services.contract.v1_0.DebitPostingResponse;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.AgentPortalCustomCashInPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.AgentPortalCustomCashInSuccessPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentPortalHomePage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class AgentPortalCustomCashInConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(AgentPortalCustomCashInConfirmPanel.class);
	
	protected BtpnBaseAgentPortalSelfCarePage basePage;
	
	protected AgentCustomCashInBean cashInBean;
	
	@SpringBean(name = "debitClient")
	private DebitFacade debitClient;

	public AgentPortalCustomCashInConfirmPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage, AgentCustomCashInBean cashInBean) {
		super(id);
		
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		
		constructPanel();
	}

	protected void constructPanel() {
		Form<AgentPortalCustomCashInConfirmPanel> form = new Form<AgentPortalCustomCashInConfirmPanel>(
				"cashInConfirmForm", new CompoundPropertyModel<AgentPortalCustomCashInConfirmPanel>(this));
		
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new Label("cashInBean.accountNumber"));
		form.add(new Label("cashInBean.msisdn"));
		form.add(new Label("cashInBean.displayName"));
		form.add(new AmountLabel("cashInBean.cashinAmount", true, true));

		// Add Confirm button
		Button confirmButton = new Button("submitConfirm") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (!PortalUtils.exists(cashInBean)) {
					cashInBean = new AgentCustomCashInBean();
				}
				
				performCashIn();
			};
		};
		
		confirmButton.add(new AttributePrepender("onclick", Model.of("loading(submitConfirm)"), ";"));
		
		form.add(confirmButton);
		
		// Add Back button
		form.add(new Button("submitBack") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new AgentPortalCustomCashInPage());
			};
		});

		// Add Cancel button
		form.add(new Button("submitCancle") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(AgentPortalHomePage.class);
			};
		});

		add(form);
	}
	
	private void performCashIn() {
		String terminalId = formatMsisdn(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		
		try {
			final DebitPostingRequest request = basePage.getNewMobiliserRequest(DebitPostingRequest.class);
			
			request.setRepeat(Boolean.FALSE);
			request.setConversationId(cashInBean.getConvId());
			request.setFinal(true);
			
			request.setProcessingCode(cashInBean.getProc_code());
			request.setTransactionDateTime(cashInBean.getTransactionDatetime());
			request.setMerchantType("6012");
			request.setMerchantId("BTPN");
			request.setTerminalId(terminalId);
			request.setAcquirerId("213");
			
			DebitPostingResponse response = debitClient.posting(request);
				
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				List<DebitTransactionType> cashInTransaction = response.getTransaction();				
				
				for (DebitTransactionType txnBean : cashInTransaction){
					cashInBean.setDisplayName(txnBean.getCreditAccount().getHolderName());
					cashInBean.setAccountBalance(Long.valueOf(txnBean.getCreditAccount().getBalance().getValue()));
					cashInBean.setAccountName(txnBean.getCreditAccount().getHolderName());
					cashInBean.setAccountNumber(txnBean.getCreditAccount().getNumber());
					cashInBean.setAccountType(txnBean.getCreditAccount().getType());
					
					setResponsePage(new AgentPortalCustomCashInSuccessPage(cashInBean));
				}
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception ex) {
			log.error("#An error occurred while calling performAgentCashIn service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}
	
	private String formatMsisdn(String msisdn) {
		return new PhoneNumber(msisdn, basePage.getAgentPortalPrefsConfig().getDefaultCountryCode()).getInternationalFormat();
	}

	private void handleSpecificErrorMessage(final int errorCode) {
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.cashin.fail", this);
		}
		
		error(message);
	}
}
