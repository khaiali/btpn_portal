package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fundtransfer.CashRequestObject;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fundtransfer.CashResponseObject;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.ConfirmCashOutRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.ConfirmCashOutResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCashOutDetailsPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCashOutSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashOutConfirmPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCashOutConfirmPanel.class);

	protected BtpnMobiliserBasePage basePage;

	protected BankCashOutBean cashOutBean;

	public BankPortalCashOutConfirmPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		LOG.debug("BankPortalcashOutDetailsPanel Started.");
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashOutConfirmPanel> form = new Form<BankPortalCashOutConfirmPanel>("cashOutConfirmForm",
			new CompoundPropertyModel<BankPortalCashOutConfirmPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		form.add(new Label("cashOutBean.accountNumber"));
		form.add(new Label("cashOutBean.mobileNumber"));
		form.add(new Label("cashOutBean.displayName"));
		form.add(new Label("cashOutBean.accountType"));
		form.add(new AmountLabel("cashOutBean.accountBalance"));
		form.add(new AmountLabel("cashOutBean.cashOutAmount"));
		form.add(new AmountLabel("cashOutBean.cashOutFeeAmount"));
		form.add(new AmountLabel("cashOutBean.totalCashOutAmount"));

		// Add Confirm button
		Button confirmButton = new Button("submitConfirm") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleConfirmCashinRequest();
			}
		};
		confirmButton.add(new AttributePrepender("onclick", Model.of("loading(submitConfirm)"), ";"));
		form.add(confirmButton);

		// Add Back button
		form.add(new Button("submitBack") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new BankPortalCashOutDetailsPage(cashOutBean));
			};
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
	 * calling confirmCashOut service from fund transfer end point
	 */
	private void handleConfirmCashinRequest() {
		try {
			// calling confirmCashIn service
			ConfirmCashOutResponse confirmCashOutResponse = basePage.getWithdrawClient().confirmCashOut(
				prepareRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(confirmCashOutResponse)) {
				CashResponseObject cashResponse = confirmCashOutResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponse)) {
					prepareReponse(cashResponse);
				}
				setResponsePage(new BankPortalCashOutSuccessPage(cashOutBean));
			} else {
				handleSpecificErrorMessage(confirmCashOutResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling confirmCashIn service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	/**
	 * This method returns the cash out request object.
	 * 
	 * @return confirmCashOutRequest confirmCashOutRequest
	 * @throws Exception
	 */
	private ConfirmCashOutRequest prepareRequest() throws Exception {
		final ConfirmCashOutRequest confirmCashOutRequest = basePage
			.getNewMobiliserRequest(ConfirmCashOutRequest.class);
		BtpnCustomer loggedInCustomer = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		CashRequestObject cashRequest = new CashRequestObject();
		String customerId = String.valueOf(loggedInCustomer.getCustomerId());
		cashRequest.setPayeeId(customerId);
		cashRequest.setPayerId(cashOutBean.getMobileNumber());
		cashRequest.setAmount(cashOutBean.getCashOutAmount());
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		confirmCashOutRequest.setCashRequestObject(cashRequest);
		confirmCashOutRequest.setIdUseCase(BtpnConstants.USECASE_TOPAGENT_CASH_OUT_AT_BANK);
		confirmCashOutRequest.setRemarks("TopAgentCashOut");
		confirmCashOutRequest.setReferenceTxnId(cashOutBean.getRefTransactionId());
		confirmCashOutRequest.setTransactionFees(cashOutBean.getCashOutFeeAmount());
		return confirmCashOutRequest;
	}

	/**
	 * This method sets the cash in response object.
	 */
	private void prepareReponse(CashResponseObject cashResponse) {
		cashOutBean.setCashOutFeeAmount(cashResponse.getTransactionFees());
		cashOutBean.setTotalSVABalance(cashResponse.getPayerBalance());
		cashOutBean.setCashOutAmount(cashResponse.getTransactionAmount());
		cashOutBean.setDebitAmount(cashOutBean.getCashOutAmount());
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
