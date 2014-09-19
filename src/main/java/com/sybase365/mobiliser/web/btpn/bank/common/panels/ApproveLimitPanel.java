package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.ContinuePendingLimitRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.ContinuePendingLimitResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ApproveFeePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the ConsumerApprovalPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveLimitPanel extends ApproveFeePanel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveFeeDetailsPanel.class);

	private ApproveLimitBean limitBean;

	private boolean isAdd;

	public ApproveLimitPanel(String id, BtpnMobiliserBasePage mobBasePage, final ApproveFeeBean feeBean,
		final ApproveLimitBean limitBean) {
		super(id, mobBasePage);
		this.feeBean = feeBean;
		this.limitBean = limitBean;
		isAdd = limitBean.getUseCaseId() == null;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<ManageProductsApproveConfirmPanel> form = new Form<ManageProductsApproveConfirmPanel>("approveForm",
			new CompoundPropertyModel<ManageProductsApproveConfirmPanel>(this));

		// Add product id
		form.add(new Label("limitBean.action"));
		form.add(new Label("limitBean.newAction"));

		form.add(new Label("limitBean.useCaseId.id"));
		form.add(new Label("limitBean.newUseCaseId.id"));

		form.add(new Label("limitBean.productId.value"));
		form.add(new Label("limitBean.newProductId.value"));

		form.add(new Label("limitBean.applyToPayee",
			isAdd == true ? null : limitBean.getIsApplyToPayee() ? BtpnConstants.YES_ID : BtpnConstants.NO_ID));
		form.add(new Label("limitBean.newApplyToPayee",
			limitBean.getNewIsApplyToPayee() ? BtpnConstants.YES_ID : BtpnConstants.NO_ID));

		form.add(new Label("limitBean.isPerCustomer",
			isAdd == true ? null : limitBean.getIsPerCustomer() ? BtpnConstants.YES_ID : BtpnConstants.NO_ID));
		form.add(new Label("limitBean.newIsPerCustomer",
			limitBean.getNewIsPerCustomer() ? BtpnConstants.YES_ID : BtpnConstants.NO_ID));

		form.add(new AmountLabel("limitBean.dailyLimit", true, !isAdd));
		form.add(new AmountLabel("limitBean.newDailyLimit",true, true));

		form.add(new AmountLabel("limitBean.weeklyLimit", true, !isAdd));
		form.add(new AmountLabel("limitBean.newWeeklyLimit",true,true));

		form.add(new AmountLabel("limitBean.monthlyLimit", true, !isAdd));
		form.add(new AmountLabel("limitBean.newMonthlyLimit",true,true));

		form.add(addApproveButton());
		form.add(addConfirmButton());
		form.add(addCancelButton());

		// Add add Button
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
	protected void handleApproveOrReject(boolean isApproveOrReject) {
		try {
			final ContinuePendingLimitRequest request = this.mobBasePage
				.getNewMobiliserRequest(ContinuePendingLimitRequest.class);
			request.setTaskId(feeBean.getTaskId());
			request.setCheckerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setApprove(isApproveOrReject);
			final ContinuePendingLimitResponse response = this.mobBasePage.limitClient.continuePendingLimit(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				final String msg = isApproveOrReject ? "approve.success" : "reject.success";
				this.mobBasePage.getWebSession().info(getLocalizer().getString(msg, this));
			} else {
				final String msg = isApproveOrReject ? "approve.fail" : "reject.fail";
				this.mobBasePage.getWebSession().error(getLocalizer().getString(msg, this));
			}

		} catch (Exception e) {
			LOG.error("ConfirmRegistrationPanel:handleRegistration() ==> Error Approving/Rejecting ==> ", e);
			error(this.mobBasePage.getLocalizer().getString("error.exception", this.mobBasePage));
		}

	}
}
