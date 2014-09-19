package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.ContinuePendingFeeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.ContinuePendingFeeResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeConfirmBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the ConsumerApprovalPanel page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ApproveBillPayOrAirtimeFeePanel extends ApproveFeePanel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveFeeDetailsPanel.class);

	private ApproveFeeConfirmBean confirmBean;

	private ApproveFeeBean feeBean;

	private boolean isAdd;

	public ApproveBillPayOrAirtimeFeePanel(String id, BtpnMobiliserBasePage mobBasePage, final ApproveFeeBean feeBean,
		final ApproveFeeConfirmBean confirmBean) {
		super(id, mobBasePage);
		this.feeBean = feeBean;
		this.confirmBean = confirmBean;
		isAdd = confirmBean.getUseCaseName() == null;
		constructPanel();
	}

	/**
	 * This constructs the fee panel for fixed Fee, Sharing Fee, SLab Fee
	 */
	protected void constructPanel() {

		final Form<ApproveFeeDetailsPanel> form = new Form<ApproveFeeDetailsPanel>("approveForm",
			new CompoundPropertyModel<ApproveFeeDetailsPanel>(this));

		// Add product id
		form.add(new Label("confirmBean.action"));
		form.add(new Label("confirmBean.newAction"));

		form.add(new Label("confirmBean.useCaseName.value", isAdd ? null : confirmBean.getNewUseCaseName().getValue()));
		form.add(new Label("confirmBean.newUseCaseName.value", confirmBean.getNewUseCaseName().getValue()));

		form.add(new AmountLabel("confirmBean.transactionAmount", true, !isAdd));
		form.add(new AmountLabel("confirmBean.newTransactionAmount"));

		addFeeListView(form);

		form.add(addApproveButton());
		form.add(addConfirmButton());
		form.add(addCancelButton());

		// Add add Button
		add(form);
	}

	/**
	 * This method fetches the pending fee details.
	 */
	private void addFeeListView(final Form<ApproveFeeDetailsPanel> form) {
		ListView<ApproveFeeDetailsBean> listDetails = new ListView<ApproveFeeDetailsBean>("feeListView",
			confirmBean.getFeeDetailsBean()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<ApproveFeeDetailsBean> item) {
				final ApproveFeeDetailsBean feeDetailsBean = item.getModelObject();
				item.setModel(new CompoundPropertyModel<ApproveFeeDetailsBean>(feeDetailsBean));

				final WebMarkupContainer feeNameContainer = new WebMarkupContainer("feeNameContainer");
				feeNameContainer.add(new Label("label.feeName", getLocalizer().getString("label.feeName", this)));
				feeNameContainer.add(new Label("feeName"));
				feeNameContainer.add(new Label("newFeeName"));
				item.add(feeNameContainer);

				final WebMarkupContainer glCodeContainer = new WebMarkupContainer("glCodeContainer");
				glCodeContainer.add(new Label("label.glCode", getLocalizer().getString("label.glCode", this)));
				glCodeContainer.add(new Label("glCode.id"));
				glCodeContainer.add(new Label("newGlCode.id"));
				item.add(glCodeContainer);

				item.add(new AmountLabel("fixedFee", true, !isAdd));
				item.add(new AmountLabel("newFixedFee"));

				item.add(new Label("percentageFee"));
				item.add(new Label("newPercentageFee"));
			}
		};
		listDetails.setRenderBodyOnly(true);
		form.add(listDetails);
	}

	@Override
	protected void handleApproveOrReject(boolean isApproveOrReject) {
		try {
			final ContinuePendingFeeRequest request = this.mobBasePage
				.getNewMobiliserRequest(ContinuePendingFeeRequest.class);
			request.setTaskId(feeBean.getTaskId());
			request.setCheckerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setApprove(isApproveOrReject);
			final ContinuePendingFeeResponse response = this.mobBasePage.feeClient.continuePendingFee(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				final String msg = isApproveOrReject ? "approve.success" : "reject.success";
				this.mobBasePage.getWebSession().info(getLocalizer().getString(msg, this));
			} else {
				final String msg = isApproveOrReject ? "approve.fail" : "reject.fail";
				this.mobBasePage.getWebSession().error(getLocalizer().getString(msg, this));
			}

		} catch (Exception e) {
			LOG.error("ApproveBillPayOrAirtimeFeePanel:handleApproveOrReject() ==> Error Approving/Rejecting ==> ", e);
			error(this.mobBasePage.getLocalizer().getString("error.exception", this.mobBasePage));
		}

	}

}
