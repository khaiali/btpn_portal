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
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the Approve Fee Details for Fixed fee, Sharing Fee and Slab Fee.
 * 
 * @author Vikram Gunda
 */
public class ApproveFeeDetailsPanel extends ApproveFeePanel {

	private static final long serialVersionUID = 1L;

	private ApproveFeeConfirmBean confirmBean;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveFeeDetailsPanel.class);

	private boolean isSlabFee;

	private boolean isAdd;

	public ApproveFeeDetailsPanel(String id, BtpnMobiliserBasePage mobBasePage, final ApproveFeeBean feeBean,
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

		// Slab Fee
		isSlabFee = feeBean.getFeeType().equals(BtpnConstants.USECASE_SLAB_RADIO);

		// Add product id
		form.add(new Label("confirmBean.action"));
		form.add(new Label("confirmBean.newAction"));

		form.add(new Label("confirmBean.useCaseName.id"));
		form.add(new Label("confirmBean.newUseCaseName.id"));

		form.add(new Label("confirmBean.productName.id"));
		form.add(new Label("confirmBean.newProductName.id"));

		form.add(new Label("confirmBean.applyToPayee",
			isAdd == true ? null : confirmBean.getApplyToPayee() ? BtpnConstants.YES_ID : BtpnConstants.NO_ID));
		form.add(new Label("confirmBean.newApplyToPayee",
			confirmBean.getNewApplyToPayee() ? BtpnConstants.YES_ID : BtpnConstants.NO_ID));

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
				feeNameContainer.setVisible(!isSlabFee);
				item.add(feeNameContainer);

				final WebMarkupContainer glCodeContainer = new WebMarkupContainer("glCodeContainer");
				glCodeContainer.add(new Label("label.glCode", getLocalizer().getString("label.glCode", this)));
				glCodeContainer.add(new Label("glCode.value"));
				glCodeContainer.add(new Label("newGlCode.value"));
				glCodeContainer.setVisible(!isSlabFee);
				item.add(glCodeContainer);

				final WebMarkupContainer minValueContainer = new WebMarkupContainer("minValueContainer");
				minValueContainer.add(new Label("label.minValue", getLocalizer().getString("label.minValue", this)));
				minValueContainer.add(new AmountLabel("minValue", true, !isAdd));
				minValueContainer.add(new AmountLabel("newMinValue"));
				minValueContainer.setVisible(isSlabFee);
				item.add(minValueContainer);

				final WebMarkupContainer maxValueContainer = new WebMarkupContainer("maxValueContainer");
				maxValueContainer.add(new Label("label.maxValue", getLocalizer()
					.getString("label.maxValue", this)));
				maxValueContainer.add(new AmountLabel("maxValue", true, !isAdd));
				maxValueContainer.add(new AmountLabel("newMaxValue"));
				maxValueContainer.setVisible(isSlabFee);
				item.add(maxValueContainer);

				item.add(new AmountLabel("fixedFee", true, !isAdd));
				item.add(new AmountLabel("newFixedFee"));

				item.add(new Label("percentageFee",
					isAdd == true ? null : (feeDetailsBean.getPercentageFee() == null ? "0.0" : feeDetailsBean
						.getPercentageFee().toString()) + " %"));
				item.add(new Label("newPercentageFee", (feeDetailsBean.getNewPercentageFee() != null ? feeDetailsBean
					.getNewPercentageFee().toString() : "0.0") + " %"));
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
			LOG.error("ApproveFeeDetailsPanel:handleRegistration() ==> Error Approving/Rejecting ==> ", e);
			error(this.mobBasePage.getLocalizer().getString("error.exception", this.mobBasePage));
		}

	}

}
