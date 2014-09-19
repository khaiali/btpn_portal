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
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformTopAgentCashInRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.PerformTopAgentCashInResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCashinConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashinDetailsPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashinDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCashinDetailsPanel.class);

	protected BtpnBaseBankPortalSelfCarePage basePage;

	protected BankCashinBean cashInBean;

	public BankPortalCashinDetailsPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCashinBean cashInBean) {
		super(id);
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashinDetailsPanel> form = new Form<BankPortalCashinDetailsPanel>("cashInDetaislForm",
			new CompoundPropertyModel<BankPortalCashinDetailsPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		form.add(new TextField<String>("cashInBean.accountNumber").setEnabled(false));
		form.add(new TextField<String>("cashInBean.displayName").setEnabled(false));
		form.add(new TextField<String>("cashInBean.accountType").setEnabled(false));
		form.add(new TextField<String>("cashInBean.msisdn").setEnabled(false));
		form.add(new AmountLabel("cashInBean.accountBalance"));
		form.add(new AmountTextField<Long>("cashInBean.cashinAmount", Long.class, false).setRequired(true).add(
			new ErrorIndicator()));
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				final Roles roles = basePage.getMobiliserWebSession().getBtpnRoles();
				if (roles.hasRole(BtpnConstants.PRIV_TOPAGENT_CASHIN_AT_BANK)) {
					performTopAgentCashIn();
				} else {
					error(getLocalizer().getString("error.no.privilege", this));
				}
			};

		});
		add(form);
	}

	/**
	 * Calling performTopAgentCashIn service
	 */
	private void performTopAgentCashIn() {
		try {
			final PerformTopAgentCashInResponse cashInResponse = basePage.getDepositClient().performTopAgentCashIn(
				prepareRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(cashInResponse)) {
				final CashResponseObject cashResponse = cashInResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponse)) {
					prepareReponse(cashResponse);
					cashInBean.setRefTransactionId(cashInResponse.getCashResponseObject().getTransactionId());
				}
				setResponsePage(new BankPortalCashinConfirmPage(cashInBean));
			} else {
				handleSpecificErrorMessage(cashInResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling performTopAgentCashIn service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	/**
	 * This method returns the cash in request object.
	 * 
	 * @return PerformTopAgentCashInRequest PerformTopAgentCashInRequest
	 * @throws Exception
	 */
	private PerformTopAgentCashInRequest prepareRequest() throws Exception {
		final PerformTopAgentCashInRequest request = basePage
			.getNewMobiliserRequest(PerformTopAgentCashInRequest.class);
		final CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashInBean.getCashinAmount());
		final long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
		cashRequest.setPayerId(String.valueOf(customerId));
		cashRequest.setPayeeId(cashInBean.getMsisdn());
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		request.setCashRequestObject(cashRequest);
		return request;
	}

	/**
	 * This method sets the cash in response object.
	 */
	private void prepareReponse(CashResponseObject cashResponse) {
		cashInBean.setTotalSVABalance(cashResponse.getPayerBalance());
		cashInBean.setCashinAmount(cashResponse.getTransactionAmount());
		cashInBean.setCreditAmount(cashInBean.getCashinAmount());
		cashInBean.setCashInFeeAmount(cashResponse.getTransactionFees());
		final Long totalCashinAmount = cashInBean.getCashinAmount() + cashResponse.getTransactionFees();
		cashInBean.setTotalCashinAmount(totalCashinAmount);
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
