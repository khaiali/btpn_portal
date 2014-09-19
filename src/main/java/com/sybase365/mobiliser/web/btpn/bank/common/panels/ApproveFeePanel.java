package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ApproveFeePage;

/**
 * This is the ConsumerApprovalPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public abstract class ApproveFeePanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage mobBasePage;

	protected ApproveFeeBean feeBean;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ApproveFeePanel.class);

	public ApproveFeePanel(final String id, final BtpnMobiliserBasePage mobBasePage) {
		super(id);
		this.mobBasePage = mobBasePage;
	}

	protected abstract void constructPanel();

	/**
	 * This method is for adding Approve button to approve the Consumers, Bank Staff, Top Agents.
	 */
	protected Button addApproveButton() {
		Button backButton = new Button("btnApprove") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleApproveOrReject(true);
				setResponsePage(ApproveFeePage.class);
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
				setResponsePage(ApproveFeePage.class);
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
				setResponsePage(ApproveFeePage.class);
			};
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method adds the product list view
	 */
	protected abstract void handleApproveOrReject(boolean isApproveOrReject);
}
