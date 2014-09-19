package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fundtransfer.CashRequestObject;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fundtransfer.CashResponseObject;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformTopAgentCashOutRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformTopAgentCashOutResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCashOutConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashoutDetailsPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCashOutDetailsPanel.class);

	protected BtpnBaseBankPortalSelfCarePage basePage;

	protected BankCashOutBean cashOutBean;

	public BankPortalCashOutDetailsPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashinDetailsPanel> form = new Form<BankPortalCashinDetailsPanel>("cashOutDetaislForm",
			new CompoundPropertyModel<BankPortalCashinDetailsPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);

		form.add(new TextField<String>("cashOutBean.accountNumber").setEnabled(false));
		form.add(new TextField<String>("cashOutBean.displayName").setEnabled(false));
		form.add(new TextField<String>("cashOutBean.accountType").setEnabled(false));
		form.add(new TextField<String>("cashOutBean.mobileNumber").setEnabled(false));
		form.add(new AmountLabel("cashOutBean.accountBalance"));
		form.add(new AmountTextField<Long>("cashOutBean.cashOutAmount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				final Roles roles = basePage.getMobiliserWebSession().getBtpnRoles();
				if (roles.hasRole(BtpnConstants.PRIV_TOPAGENT_CASHOUT_AT_BANK)) {
					performTopAgentCashOut();
				} else {
					error(getLocalizer().getString("error.no.privilege", this));
				}
			};

		});
		add(form);
	}

	/**
	 * Calling performTopAgentCashOut service
	 */
	private void performTopAgentCashOut() {
		try {
			final PerformTopAgentCashOutResponse cashOutResponse = basePage.getWithdrawClient().performTopAgentCashOut(
				prepareRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(cashOutResponse)) {
				final CashResponseObject cashResponse = cashOutResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponse)) {
					prepareReponse(cashResponse);
					cashOutBean.setRefTransactionId(cashOutResponse.getCashResponseObject().getTransactionId());
				}
				setResponsePage(new BankPortalCashOutConfirmPage(cashOutBean));
			} else {
				handleSpecificErrorMessage(cashOutResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling performTopAgentCashOut service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	/**
	 * This method returns the cash out request object.
	 * 
	 * @return PerformTopAgentCashOutRequest performTopAgentCashOutRequest
	 * @throws Exception
	 */
	private PerformTopAgentCashOutRequest prepareRequest() throws Exception {
		final PerformTopAgentCashOutRequest request = basePage
			.getNewMobiliserRequest(PerformTopAgentCashOutRequest.class);
		final CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashOutBean.getCashOutAmount());
		final long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
		cashRequest.setPayerId(cashOutBean.getMobileNumber());
		cashRequest.setPayeeId(String.valueOf(customerId));
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		request.setCashRequestObject(cashRequest);
		return request;
	}

	/**
	 * This method sets the cash out response object.
	 */
	private void prepareReponse(CashResponseObject cashResponse) {
		cashOutBean.setTotalSVABalance(cashResponse.getPayerBalance());
		cashOutBean.setCashOutAmount(cashResponse.getTransactionAmount());
		cashOutBean.setCreditAmount(cashOutBean.getCashOutAmount());
		cashOutBean.setCashOutFeeAmount(cashResponse.getTransactionFees());
		final Long totalCashinAmount = cashOutBean.getCashOutAmount() + cashResponse.getTransactionFees();
		cashOutBean.setTotalCashOutAmount(totalCashinAmount);
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
