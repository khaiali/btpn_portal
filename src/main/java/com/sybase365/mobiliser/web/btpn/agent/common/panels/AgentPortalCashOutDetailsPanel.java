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
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.ConfirmCashOutRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.ConfirmCashOutResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformAgentCashOutRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformAgentCashOutResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformCashOutRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformCashOutResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout.AgentPortalCashOutSuccessPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashOutDetailsPanel page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashOutDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(AgentPortalCashOutDetailsPanel.class);

	protected BtpnBaseAgentPortalSelfCarePage basePage;

	protected AgentCashOutBean cashOutBean;

	protected FeedbackPanel feedBack;

	public AgentPortalCashOutDetailsPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage,
		AgentCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		constructPanel();
	}

	protected void constructPanel() {

		Form<AgentPortalCashOutDetailsPanel> form = new Form<AgentPortalCashOutDetailsPanel>("cashOutDetaislForm",
			new CompoundPropertyModel<AgentPortalCashOutDetailsPanel>(this));

		// Add feedback panel for Error Messages
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		form.add(new Label("cashOutBean.accountNumber"));
		form.add(new Label("cashOutBean.accountName"));
		form.add(new Label("cashOutBean.accountType"));
		form.add(new Label("cashOutBean.payeeMsisdn"));
		form.add(new AmountLabel("cashOutBean.accountBalance"));
		form.add(new AmountTextField<Long>("cashOutBean.cashOutAmount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				final Roles roles = basePage.getMobiliserWebSession().getBtpnRoles();
				AgentCashOutBean bean = null;
				if (PortalUtils.exists(roles) && roles.hasRole(BtpnConstants.PRIV_CUSTOMER_CASHOUT_AT_AGENT)) {
					bean = performCashOutRequest();
				} else if (roles.hasRole(BtpnConstants.PRIV_AGENT_CASHOUT_AT_TOP_AGENT)) {
					bean = PerformAgentCashOutRequest();
				}
				if (bean.isSuccess()) {
					confirmCashOutRequest(cashOutBean);
				}
			};
		});

		add(form);
	}

	/**
	 * calling performAgentCashOut service from fund transfer end point
	 */
	private AgentCashOutBean PerformAgentCashOutRequest() {
		try {
			PerformAgentCashOutResponse cashOutResponse = basePage.getWithdrawClient().performAgentCashOut(
				createAgentCashoutRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(cashOutResponse)) {
				CashResponseObject cashResponse = cashOutResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponse.getTransactionFees())) {
					long transactionFees = cashResponse.getTransactionFees();
					cashOutBean.setFeeAmount(transactionFees);
				}
				int idUseCase = cashOutResponse.getIdUseCase();
				cashOutBean.setUseCaseId(idUseCase);
				cashOutBean.setSuccess(true);
				cashOutBean.setRefTransactionId(cashOutResponse.getCashResponseObject().getTransactionId());
			} else {
				handleSpecificErrorMessage(cashOutResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling performAgentCashOut service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return cashOutBean;
	}

	private PerformAgentCashOutRequest createAgentCashoutRequest() throws Exception {
		final PerformAgentCashOutRequest request = basePage.getNewMobiliserRequest(PerformAgentCashOutRequest.class);
		CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashOutBean.getCashOutAmount());
		cashRequest.setPayerId(cashOutBean.getPayeeMsisdn());
		cashRequest.setPayeeId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		request.setCashRequestObject(cashRequest);
		return request;
	}

	/**
	 * calling performCashOut service from fund transfer end point
	 * 
	 * @param bean
	 * @return
	 */
	private AgentCashOutBean performCashOutRequest() {
		try {
			PerformCashOutResponse cashOutResponse = basePage.getWithdrawClient()
				.performCashOut(createCashOutRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(cashOutResponse)) {
				CashResponseObject cashResponse = cashOutResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponse.getTransactionFees())) {
					long transactionFees = cashResponse.getTransactionFees();
					cashOutBean.setFeeAmount(transactionFees);
				}
				int idUseCase = cashOutResponse.getIdUseCase();
				cashOutBean.setUseCaseId(idUseCase);
				cashOutBean.setSuccess(true);
				cashOutBean.setRefTransactionId(cashOutResponse.getCashResponseObject().getTransactionId());
			} else {
				handleSpecificErrorMessage(cashOutResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling performCashOut service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return cashOutBean;
	}

	private PerformCashOutRequest createCashOutRequest() throws Exception {
		final PerformCashOutRequest request = basePage.getNewMobiliserRequest(PerformCashOutRequest.class);
		CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashOutBean.getCashOutAmount());
		cashRequest.setPayerId(cashOutBean.getPayeeMsisdn());
		cashRequest.setPayeeId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		request.setCashRequestObject(cashRequest);
		return request;
	}

	/**
	 * calling confirmCashOut service from fund transfer end point
	 * 
	 * @param bean
	 * @return
	 */
	private void confirmCashOutRequest(AgentCashOutBean bean) {
		try {
			ConfirmCashOutResponse confirmCashOutResponse = basePage.getWithdrawClient().confirmCashOut(
				createConfirmCashOutRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(confirmCashOutResponse)) {
				CashResponseObject cashResponseObject = confirmCashOutResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponseObject)) {
					cashOutBean = createConfirmCashOutResponse(cashResponseObject);
				}
				setResponsePage(new AgentPortalCashOutSuccessPage(cashOutBean));
			} else {
				handleSpecificErrorMessage(confirmCashOutResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling confirmCashOut service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private ConfirmCashOutRequest createConfirmCashOutRequest() throws Exception {
		final ConfirmCashOutRequest request = basePage.getNewMobiliserRequest(ConfirmCashOutRequest.class);
		CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashOutBean.getCashOutAmount());
		cashRequest.setPayerId(cashOutBean.getPayeeMsisdn());
		cashRequest.setPayeeId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		request.setCashRequestObject(cashRequest);
		request.setIdUseCase(cashOutBean.getUseCaseId());
		request.setReferenceTxnId(cashOutBean.getRefTransactionId());
		if (PortalUtils.exists(cashOutBean.getFeeAmount())) {
			request.setTransactionFees(Long.valueOf(cashOutBean.getFeeAmount()));
		}
		return request;
	}

	private AgentCashOutBean createConfirmCashOutResponse(CashResponseObject cashResponseObject) {
		Long payerBalance = cashResponseObject.getPayerBalance();
		cashOutBean.setAccountBalance(payerBalance);
		cashOutBean.setFeeAmount(cashResponseObject.getTransactionFees() != null ? cashResponseObject
			.getTransactionFees() : 0);
		Long totalAmount = cashOutBean.getCashOutAmount() + cashOutBean.getFeeAmount();
		cashOutBean.setTotalAmount(totalAmount);
		cashOutBean.setCreditAmount(cashOutBean.getCashOutAmount());
		Long debitAmount = cashOutBean.getCashOutAmount() + cashOutBean.getFeeAmount();
		cashOutBean.setDebitAmount(debitAmount);
		return cashOutBean;
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
			message = getLocalizer().getString("error.cashOut.fail", this);
		}
		error(message);
	}

}
