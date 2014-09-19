package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.ContinuePendingGeneralLedgerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.ContinuePendingGeneralLedgerResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ApproveGeneralLedgerDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	private ApproveGeneralLedgerBean generalLedgerBean;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveGeneralLedgerDetailsPage.class);

	/**
	 * Constructor for this page.
	 */
	public ApproveGeneralLedgerDetailsPage(final ApproveGeneralLedgerBean generalLedgerBean) {
		super();
		this.generalLedgerBean = generalLedgerBean;
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<ApproveGeneralLedgerDetailsPage> form = new Form<ApproveGeneralLedgerDetailsPage>("approveForm",
			new CompoundPropertyModel<ApproveGeneralLedgerDetailsPage>(this));

		form.add(new FeedbackPanel("errorMessages"));
		// Add product id
		form.add(new Label("generalLedgerBean.glDescription"));
		form.add(new Label("generalLedgerBean.newGlDescription"));

		form.add(new Label("generalLedgerBean.glCode"));
		form.add(new Label("generalLedgerBean.newGlCode"));

		form.add(new Label("generalLedgerBean.isLeaf", generalLedgerBean.getGlCode() == null ? generalLedgerBean
			.getGlCode() : String.valueOf(generalLedgerBean.getIsLeaf())));
		form.add(new Label("generalLedgerBean.newIsLeaf"));

		form.add(new Label("generalLedgerBean.isRoot", generalLedgerBean.getGlCode() == null ? generalLedgerBean
			.getGlCode() : String.valueOf(generalLedgerBean.getIsRoot())));
		form.add(new Label("generalLedgerBean.newIsRoot"));

		form.add(new Label("generalLedgerBean.parentGlCode.id"));
		form.add(new Label("generalLedgerBean.newParentGlCode.id"));

		form.add(new Label("generalLedgerBean.type.value"));
		form.add(new Label("generalLedgerBean.newType.value"));

		form.add(addApproveButton());
		form.add(addConfirmButton());
		form.add(addCancelButton());

		add(form);
	}

	/**
	 * This method is for adding Approve button to approve the Consumers, Bank Staff, Top Agents.
	 */
	protected Button addApproveButton() {
		Button backButton = new Button("btnApprove") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleApproveOrReject(true);
				setResponsePage(ApproveGeneralLedgerPage.class);
			}
		};
		backButton.setDefaultFormProcessing(true);
		return backButton;
	}

	/**
	 * This method adds the reject button for the Reject Panel.
	 */
	protected Button addConfirmButton() {
		Button submitButton = new Button("btnReject") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleApproveOrReject(false);
				setResponsePage(ApproveGeneralLedgerPage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the cancel button for the Registration Panel.
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ApproveGeneralLedgerPage.class);
			};
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method handles the approve or reject of General Ledger beans
	 * 
	 * @param isApprove true if approve, false if reject
	 */
	private void handleApproveOrReject(boolean isApprove) {
		try {
			final ContinuePendingGeneralLedgerRequest request = this
				.getNewMobiliserRequest(ContinuePendingGeneralLedgerRequest.class);
			request.setTaskId(generalLedgerBean.getTaskId());
			request.setApprove(isApprove);
			request.setCheckerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final ContinuePendingGeneralLedgerResponse response = this.generalLedgerClient
				.continuePendingGeneralLedger(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				final String msg = isApprove ? "approve.success" : "reject.success";
				getWebSession().info(getLocalizer().getString(msg, this));
			} else {
				final String msg = isApprove ? "approve.fail" : "reject.fail";
				getWebSession().error(getLocalizer().getString(msg, this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while Approving/Rejecting  ===> ", e);
		}

	}

}
