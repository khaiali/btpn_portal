package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactionreversal.ContinuePendingTransactionReversalRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactionreversal.ContinuePendingTransactionReversalResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionReversalBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.transaction.ApproveTxnReversalPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ApproveTxnReversalConfirmPanel for bank portal portal.
 * 
 * @author Narasa Reddy
 */
public class ApproveTxnReversalConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveTxnReversalConfirmPanel.class);

	private BtpnMobiliserBasePage basePage;

	TransactionReversalBean txnBean;

	public ApproveTxnReversalConfirmPanel(String id, BtpnMobiliserBasePage basePage, TransactionReversalBean txnBean) {
		super(id);
		this.basePage = basePage;
		this.txnBean = txnBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<ApproveTxnReversalConfirmPanel> form = new Form<ApproveTxnReversalConfirmPanel>(
			"approvetxnReversalConfirmForm", new CompoundPropertyModel<ApproveTxnReversalConfirmPanel>(this));

		// txnReversal header
		String message = "";
		if (PortalUtils.exists(txnBean)) {
			if (txnBean.getSelectedLink().equals("Approve")) {
				message = getLocalizer().getString("header.approveTxnReversal", ApproveTxnReversalConfirmPanel.this);
			} else if (txnBean.getSelectedLink().equals("Reject")) {
				message = getLocalizer().getString("header.rejectTxnReversal", ApproveTxnReversalConfirmPanel.this);
			}
		}

		form.add(new Label("headerMessage", message));

		form.add(new Label("txnBean.currentValue.mobileNumber"));
		form.add(new Label("txnBean.newValue.mobileNumber"));

		form.add(new Label("txnBean.currentValue.transactionAmount"));
		form.add(new Label("txnBean.newValue.transactionAmount"));

		form.add(new Label("txnBean.currentValue.transactionDate"));
		form.add(new Label("txnBean.newValue.transactionDate"));

		form.add(new Label("txnBean.currentValue.transactionID"));
		form.add(new Label("txnBean.newValue.transactionID"));

		form.add(new Label("txnBean.currentValue.transactionName"));
		form.add(new Label("txnBean.newValue.transactionName"));

		form.add(new Label("txnBean.currentValue.useCase"));
		form.add(new Label("txnBean.newValue.useCase"));

		form.add(addApproveButton());
		form.add(addCancelButton());

		// Add form
		add(form);
	}

	/**
	 * This method is for adding Approve button to approve the txnReversal details.
	 */
	protected Button addApproveButton() {
		Button backButton = new Button("confirmButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (PortalUtils.exists(txnBean)) {
					String selectedLinkName = txnBean.getSelectedLink();
					if (selectedLinkName.equals("Approve")) {
						txnBean = continuePendingTransactionReversal(txnBean, true);
						if (txnBean.isApproveSuccess()) {
							basePage.getWebSession().info(
								getLocalizer().getString("approve.success", ApproveTxnReversalConfirmPanel.this));
						}
					} else if (selectedLinkName.equals("Reject")) {
						txnBean = continuePendingTransactionReversal(txnBean, false);
						if (txnBean.isRejectSuccess()) {
							basePage.getWebSession().info(
								getLocalizer().getString("reject.success", ApproveTxnReversalConfirmPanel.this));
						}
					}
					setResponsePage(new ApproveTxnReversalPage(txnBean));
				}
			}
		};
		backButton.setDefaultFormProcessing(true);
		return backButton;
	}

	/**
	 * This method adds the cancel button for the txnReversal Panel.
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("cancelButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ApproveTxnReversalPage.class);
			};
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * calling continuePendingTransactionReversal service from change msisdn end point
	 * 
	 * @param bean
	 * @return
	 */
	private TransactionReversalBean continuePendingTransactionReversal(TransactionReversalBean bean, boolean flag) {
		ContinuePendingTransactionReversalResponse txnReversalResponse = null;
		try {
			final ContinuePendingTransactionReversalRequest request = basePage
				.getNewMobiliserRequest(ContinuePendingTransactionReversalRequest.class);
			request.setApprove(flag);
			request.setTaskId(bean.getTaskId());
			long checkerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCheckerId(checkerId);
			txnReversalResponse = basePage.getTransactionReversalClient().continuePendingTransactionReversal(request);
			if (basePage.evaluateBankPortalMobiliserResponse(txnReversalResponse)
					&& txnReversalResponse.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				if (flag) {
					bean.setApproveSuccess(true);
				} else {
					bean.setRejectSuccess(true);
				}
			} else {
				error(txnReversalResponse.getStatus().getValue());
			}

		} catch (Exception ex) {
			LOG.error("#An error occurred while calling continuePendingTransactionReversal service", ex);
			error("We are experiencing technical difficulties. Please try again later.");
		}
		return bean;
	}

}
