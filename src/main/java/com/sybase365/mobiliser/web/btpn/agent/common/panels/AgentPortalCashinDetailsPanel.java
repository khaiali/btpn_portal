package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fundtransfer.CashRequestObject;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fundtransfer.CashResponseObject;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.ConfirmCashInRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.ConfirmCashInResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformAgentCashInRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformAgentCashInResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformCashInRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformCashInResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashinBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.AgentPortalCashinSuccessPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashinDetailsPanel page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashinDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(AgentPortalCashinDetailsPanel.class);

	protected BtpnBaseAgentPortalSelfCarePage basePage;

	protected AgentCashinBean cashInBean;

	public AgentPortalCashinDetailsPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage, AgentCashinBean cashInBean) {
		super(id);
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		constructPanel();
	}

	protected void constructPanel() {

		Form<AgentPortalCashinDetailsPanel> form = new Form<AgentPortalCashinDetailsPanel>("cashInDetaislForm",
			new CompoundPropertyModel<AgentPortalCashinDetailsPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new Label("cashInBean.accountNumber"));
		form.add(new Label("cashInBean.accountName"));
		form.add(new Label("cashInBean.accountType"));
		form.add(new Label("cashInBean.payeeMsisdn"));
		form.add(new AmountLabel("cashInBean.accountBalance"));
		form.add(new AmountTextField<Long>("cashInBean.cashinAmount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				final Roles roles = basePage.getMobiliserWebSession().getBtpnRoles();
				AgentCashinBean bean = null;
				if (PortalUtils.exists(roles) && roles.hasRole(BtpnConstants.PRIV_CUSTOMER_CASHIN_AT_AGENT)) {
					bean = performCashIn();
				} else if (roles.hasRole(BtpnConstants.PRIV_AGENT_CASHIN_AT_TOP_AGENT)) {
					bean = performAgentCashIn();
				}
				if (bean.isSuccess()) {
					confirmCashInRequest();
				}
			};
		});

		add(form);
	}

	/**
	 * calling performAgentCashIn service from fund transfer end point
	 */
	private AgentCashinBean performAgentCashIn() {
		try {
			PerformAgentCashInResponse cashInResponse = basePage.getDepositClient().performAgentCashIn(
				createAgentCashinRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(cashInResponse)) {
				CashResponseObject cashResponse = cashInResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponse.getTransactionFees())) {
					long transactionFees = cashResponse.getTransactionFees();
					cashInBean.setFeeAmount(transactionFees);
				}
				int idUseCase = cashInResponse.getIdUseCase();
				cashInBean.setUseCaseId(idUseCase);
				cashInBean.setSuccess(true);
				cashInBean.setRefTransactionId(cashInResponse.getCashResponseObject().getTransactionId());
			} else {
				handleSpecificErrorMessage(cashInResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling performAgentCashIn service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return cashInBean;
	}

	private PerformAgentCashInRequest createAgentCashinRequest() throws Exception {
		final PerformAgentCashInRequest request = basePage.getNewMobiliserRequest(PerformAgentCashInRequest.class);
		CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashInBean.getCashinAmount());
		cashRequest.setPayerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		cashRequest.setPayeeId(cashInBean.getPayeeMsisdn());
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		request.setCashRequestObject(cashRequest);
		return request;
	}

	/**
	 * calling performCashIn service from fund transfer end point
	 */
	private AgentCashinBean performCashIn() {
		
		try {
			
			PerformCashInResponse cashInResponse = basePage.getDepositClient().performCashIn(createCashinRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(cashInResponse)) {
				CashResponseObject cashResponse = cashInResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponse.getTransactionFees())) {
					long transactionFees = cashResponse.getTransactionFees();
					cashInBean.setFeeAmount(transactionFees);
				}
				int idUseCase = cashInResponse.getIdUseCase();
				cashInBean.setUseCaseId(idUseCase);
				cashInBean.setSuccess(true);
				cashInBean.setRefTransactionId(cashInResponse.getCashResponseObject().getTransactionId());
			} else {
				handleSpecificErrorMessage(cashInResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling performCashIn service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return cashInBean;
	}

	private PerformCashInRequest createCashinRequest() throws Exception {
		
		final PerformCashInRequest request = basePage.getNewMobiliserRequest(PerformCashInRequest.class);
		BtpnCustomer loggedInCustomer = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		String payerMsisdn = String.valueOf(loggedInCustomer.getUsername());
		CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashInBean.getCashinAmount());
		cashRequest.setPayerId(payerMsisdn);
		cashRequest.setPayeeId(cashInBean.getPayeeMsisdn());
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		request.setCashRequestObject(cashRequest);
		
		return request;
	}

	/**
	 * calling confirmCashIn service from fund transfer end point
	 */
	private void confirmCashInRequest() {
		
		try {
			
			ConfirmCashInResponse confirmCashInResponse = basePage.getDepositClient().confirmCashIn(
				createConfirmCashinRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(confirmCashInResponse)) {
				CashResponseObject cashResponseObject = confirmCashInResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponseObject)) {
					cashInBean = createConfirmCashinResponse(cashResponseObject);
				}
				setResponsePage(new AgentPortalCashinSuccessPage(cashInBean));
			} else {
				handleSpecificErrorMessage(confirmCashInResponse.getStatus().getCode());
			}
			
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling confirmCashIn service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private ConfirmCashInRequest createConfirmCashinRequest() throws Exception {
		final ConfirmCashInRequest request = basePage.getNewMobiliserRequest(ConfirmCashInRequest.class);
		CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashInBean.getCashinAmount());
		cashRequest.setPayerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		cashRequest.setPayeeId(cashInBean.getPayeeMsisdn());
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		request.setCashRequestObject(cashRequest);
		request.setIdUseCase(cashInBean.getUseCaseId());
		request.setReferenceTxnId(cashInBean.getRefTransactionId());
		if (PortalUtils.exists(cashInBean.getFeeAmount())) {
			request.setTransactionFees(Long.valueOf(cashInBean.getFeeAmount()));
		}
		return request;
	}

	private AgentCashinBean createConfirmCashinResponse(CashResponseObject cashResponseObject) {
		cashInBean.setAccountBalance(cashResponseObject.getPayeeBalance());
		cashInBean.setPayermsisdn((basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername()));
		cashInBean.setFeeAmount(cashResponseObject.getTransactionFees() != null ? cashResponseObject
			.getTransactionFees() : 0);
		Long totalAmount = cashInBean.getCashinAmount() + cashInBean.getFeeAmount();
		cashInBean.setTotalAmount(totalAmount);
		cashInBean.setCreditAmount(cashInBean.getCashinAmount());
		Long debitAmount = cashInBean.getCashinAmount() + cashInBean.getFeeAmount();
		cashInBean.setDebitAmount(debitAmount);
		return cashInBean;
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.cashin.fail", this);
		}
		error(message);
	}

}
