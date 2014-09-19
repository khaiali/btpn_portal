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
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.ConfirmCashInRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fundtransfer.ConfirmCashInResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCashinDetailsPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCashinSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the CashinConfirmPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashinConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCashinConfirmPanel.class);

	protected BtpnMobiliserBasePage basePage;

	protected BankCashinBean cashInBean;

	public BankPortalCashinConfirmPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCashinBean cashInBean) {
		super(id);
		
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<BankPortalCashinConfirmPanel> form = new Form<BankPortalCashinConfirmPanel>("cashInConfirmForm",
			new CompoundPropertyModel<BankPortalCashinConfirmPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		form.add(new Label("cashInBean.accountNumber"));
		form.add(new Label("cashInBean.msisdn"));
		form.add(new Label("cashInBean.displayName"));
		form.add(new Label("cashInBean.accountType"));
		form.add(new AmountLabel("cashInBean.accountBalance"));
		form.add(new AmountLabel("cashInBean.creditAmount"));
		form.add(new AmountLabel("cashInBean.cashInFeeAmount"));
		form.add(new AmountLabel("cashInBean.totalCashinAmount"));
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
				setResponsePage(new BankPortalCashinDetailsPage(cashInBean));
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
	 * calling confirmCashIn service from fund transfer end point
	 */
	private void handleConfirmCashinRequest() {
		try {
			// calling confirmCashIn service
			ConfirmCashInResponse confirmCashInResponse = basePage.getDepositClient().confirmCashIn(prepareRequest());
			if (basePage.evaluateConsumerPortalMobiliserResponse(confirmCashInResponse)) {
				CashResponseObject cashResponse = confirmCashInResponse.getCashResponseObject();
				if (PortalUtils.exists(cashResponse)) {
					prepareReponse(cashResponse);
				}
				setResponsePage(new BankPortalCashinSuccessPage(cashInBean));
			} else {
				handleSpecificErrorMessage(confirmCashInResponse.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling confirmCashIn service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	/**
	 * This method returns the cash in request object.
	 * 
	 * @return PerformTopAgentCashInRequest PerformTopAgentCashInRequest
	 * @throws Exception
	 */
	private ConfirmCashInRequest prepareRequest() throws Exception {
		final ConfirmCashInRequest confirmCashInRequest = basePage.getNewMobiliserRequest(ConfirmCashInRequest.class);
		CashRequestObject cashRequest = new CashRequestObject();
		cashRequest.setAmount(cashInBean.getCashinAmount());
		Customer loggedInCustomer = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		long customerId = loggedInCustomer.getCustomerId();
		cashRequest.setPayeeId(cashInBean.getMsisdn());
		cashRequest.setPayerId(String.valueOf(customerId));
		cashRequest.setOrderChannel(BtpnConstants.ORDER_CHANNEL);
		confirmCashInRequest.setCashRequestObject(cashRequest);
		confirmCashInRequest.setIdUseCase(BtpnConstants.USECASE_TOPAGENT_CASH_IN_AT_BANK);
		confirmCashInRequest.setRemarks("TopAgentCashin");
		confirmCashInRequest.setReferenceTxnId(cashInBean.getRefTransactionId());
		confirmCashInRequest.setTransactionFees(cashInBean.getCashInFeeAmount());
		return confirmCashInRequest;
	}

	/**
	 * This method sets the cash in response object.
	 */
	private void prepareReponse(CashResponseObject cashResponse) {
		cashInBean.setCashInFeeAmount(cashResponse.getTransactionFees());
		cashInBean.setTotalSVABalance(cashResponse.getPayerBalance());
		cashInBean.setCashinAmount(cashResponse.getTransactionAmount());
		cashInBean.setCreditAmount(cashInBean.getCashinAmount());
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
