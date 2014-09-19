package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.ContinuePendingProductRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.ContinuePendingProductResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsApproveRangeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsApprovePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the Confirm Registration Panel for Consumers, Agents in bank portal and Child Agents, Sub agents in agent
 * portal.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsApproveConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ManageProductsApproveConfirmPanel.class);

	private BtpnMobiliserBasePage mobBasePage;

	private ManageProductsApproveBean approveBean;

	private boolean isAdd;

	public ManageProductsApproveConfirmPanel(String id, BtpnMobiliserBasePage mobBasePage,
		final ManageProductsApproveBean approveBean) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.approveBean = approveBean;
		isAdd = approveBean.getProductName() == null;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<ManageProductsApproveConfirmPanel> form = new Form<ManageProductsApproveConfirmPanel>("approveForm",
			new CompoundPropertyModel<ManageProductsApproveConfirmPanel>(this));

		// Add product id
		form.add(new Label("approveBean.action"));
		form.add(new Label("approveBean.newAction", approveBean.getAction()));

		form.add(new Label("approveBean.productId"));
		form.add(new Label("approveBean.newProductId"));

		form.add(new Label("approveBean.productName"));
		form.add(new Label("approveBean.newProductName"));

		form.add(new Label("approveBean.productType.value"));
		form.add(new Label("approveBean.newProductType.value"));

		form.add(new AmountLabel("approveBean.initialDeposit", true, !isAdd));
		form.add(new AmountLabel("approveBean.newInitialDeposit", true, true));

		form.add(new Label("approveBean.productGLCode.value"));
		form.add(new Label("approveBean.newProductGLCode.value"));

		form.add(new Label("approveBean.feeGLCode.value"));
		form.add(new Label("approveBean.newFeeGLCode.value"));

		form.add(new Label("approveBean.roleName"));
		form.add(new Label("approveBean.newRoleName"));

		form.add(new Label("approveBean.roleDescription"));
		form.add(new Label("approveBean.newRoleDescription"));

		form.add(new AmountLabel("approveBean.adminFee", true, !isAdd));
		form.add(new AmountLabel("approveBean.newAdminFee", true, true));

		form.add(new AmountLabel("approveBean.minBalance", true, !isAdd));
		form.add(new AmountLabel("approveBean.newMinBalance", true, true));

		addProductListView(form);

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
				setResponsePage(ManageProductsApprovePage.class);
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
				setResponsePage(ManageProductsApprovePage.class);
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
				setResponsePage(ManageProductsApprovePage.class);
			};
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method adds the product list view
	 */
	private void addProductListView(final Form<ManageProductsApproveConfirmPanel> form) {

		final ListView<ManageProductsApproveRangeBean> listDetails = new ListView<ManageProductsApproveRangeBean>(
			"productListView", approveBean.getApproveRangeList()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<ManageProductsApproveRangeBean> item) {
				final ManageProductsApproveRangeBean feeDetailsBean = item.getModelObject();
				item.setModel(new CompoundPropertyModel<ManageProductsApproveRangeBean>(feeDetailsBean));
				item.add(new AmountLabel("minRange", true, !isAdd));
				item.add(new AmountLabel("newMinRange", true, true));
				item.add(new AmountLabel("maxRange", true, !isAdd));
				item.add(new AmountLabel("newMaxRange", true, true));
				item.add(new AmountLabel("fixedAmount", true, !isAdd));
				item.add(new AmountLabel("newFixedAmount", true, true));
				item.add(new Label("percentageAmount", isAdd == true ? "" : feeDetailsBean.getPercentageAmount() + " %"));
				item.add(new Label("newPercentageAmount", feeDetailsBean.getNewPercentageAmount() + " %"));
			}
		};
		listDetails.setRenderBodyOnly(true);
		form.add(listDetails);
	}

	/**
	 * This method adds the product list view
	 */
	private void handleApproveOrReject(boolean isApproveOrReject) {
		try {
			final ContinuePendingProductRequest request = this.mobBasePage
				.getNewMobiliserRequest(ContinuePendingProductRequest.class);
			request.setTaskId(approveBean.getTaskId());
			request.setCheckerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setApprove(isApproveOrReject);
			ContinuePendingProductResponse response = this.mobBasePage.getProductClient().continuePendingProduct(
				request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				final String msg = isApproveOrReject ? "approve.success" : "reject.success";
				this.mobBasePage.getWebSession().info(getLocalizer().getString(msg, this));
			} else {
				handleSpecificErrorMessage(isApproveOrReject, response.getStatus().getCode());
			}

		} catch (Exception e) {
			LOG.error("ConfirmRegistrationPanel:handleRegistration() ==> Error Approving/Rejecting ==> ", e);
			error(this.mobBasePage.getLocalizer().getString("error.exception", this.mobBasePage));
		}

	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final boolean isApproveOrReject, final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.approve.product." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			final String msg = isApproveOrReject ? "approve.fail" : "reject.fail";
			message = getLocalizer().getString(msg, this);
		}
		this.mobBasePage.getWebSession().error(message);
	}

}
