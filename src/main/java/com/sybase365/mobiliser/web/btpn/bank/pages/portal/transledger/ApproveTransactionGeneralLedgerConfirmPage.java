package com.sybase365.mobiliser.web.btpn.bank.pages.portal.transledger;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactiongl.ContinuePendingTransactionGLRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactiongl.ContinuePendingTransactionGLResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageProductsApproveConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Vikram Gunda
 */
public class ApproveTransactionGeneralLedgerConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final Logger LOG = LoggerFactory.getLogger(ApproveTransactionGeneralLedgerConfirmPage.class);

	private TransactionGeneralLedgerBean ledgerBean;

	/**
	 * Constructor for this page.
	 */
	public ApproveTransactionGeneralLedgerConfirmPage(final TransactionGeneralLedgerBean ledgerBean) {
		super();
		this.ledgerBean = ledgerBean;
		constructPage();
	}

	protected void constructPage() {
		final Form<ManageProductsApproveConfirmPanel> form = new Form<ManageProductsApproveConfirmPanel>("approveForm",
			new CompoundPropertyModel<ManageProductsApproveConfirmPanel>(this));
		// Add product id
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("ledgerBean.currentGL.id"));
		form.add(new Label("ledgerBean.newGL.id"));

		form.add(new Label("ledgerBean.useCaseName"));
		form.add(new Label("ledgerBean.useCaseName.new", Model.of(ledgerBean.getUseCaseName())));

		form.add(addApproveButton());
		form.add(addRejectButton());
		form.add(addCancelButton());

		// Add add Button
		add(form);
	}

	/**
	 * This method is for adding Approve button to approve the Consumers, Bank Staff, Top Agents.
	 */
	protected Button addApproveButton() {
		Button approveButton = new Button("btnApprove") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleApproveOrReject(true);
				setResponsePage(ApproveTransactionGeneralLedgerPage.class);
			}
		};
		approveButton.setDefaultFormProcessing(true);
		return approveButton;
	}

	/**
	 * This method adds the reject button for the Reject Panel.
	 */
	protected Button addRejectButton() {
		Button rejectButton = new Button("btnReject") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleApproveOrReject(false);
				setResponsePage(ApproveTransactionGeneralLedgerPage.class);
			}
		};
		return rejectButton;
	}

	/**
	 * This method adds the cancel button for the Registration Panel.
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ApproveTransactionGeneralLedgerPage.class);
			};
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method handleApproveOReject
	 */
	private void handleApproveOrReject(final boolean approveOrReject) {
		try {
			final ContinuePendingTransactionGLRequest request = getNewMobiliserRequest(ContinuePendingTransactionGLRequest.class);
			request.setTaskId(ledgerBean.getTaskId());
			request.setCheckerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setApprove(approveOrReject);
			final ContinuePendingTransactionGLResponse response = this.transactionGLEndPoint
				.continuePendingTransactionGL(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				final String msg = approveOrReject ? "approve.success" : "reject.success";
				getWebSession().info(getLocalizer().getString(msg, this));
			} else {
				final String msg = approveOrReject ? "approve.fail" : "reject.fail";
				getWebSession().error(getLocalizer().getString(msg, this));
			}

		} catch (Exception e) {
			LOG.error(
				"ApproveTransactionGeneralLedgerConfirmPage:handleApproveOrReject() ==> Error Approving/Rejecting ==> ",
				e);
			error(getLocalizer().getString("approval.failure.exception", this));
		}
	}

}
