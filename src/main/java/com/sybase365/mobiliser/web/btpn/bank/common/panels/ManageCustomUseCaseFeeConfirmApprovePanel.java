package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.fee.facade.contract.wrk.ApproveUseCaseFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.ApproveUseCaseFeeWrkResponse;
import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;
import com.btpnwow.core.fee.facade.contract.wrk.RejectUseCaseFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.RejectUseCaseFeeWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeDetailApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeSuccessApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee Add Panel for bank portals. This panel consists of adding fee as fixed, slab and sharing.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeeConfirmApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageCustomUseCaseFeeConfirmApprovePanel.class);
	
	private BtpnBaseBankPortalSelfCarePage basePage;
	private ManageCustomUseCaseFeeBean ucFeeBean;
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private String feesTotalItemString;
	private BtpnCustomPagingNavigator navigator;

	private String flag;
	
	public ManageCustomUseCaseFeeConfirmApprovePanel(final String id, final BtpnBaseBankPortalSelfCarePage basePage,
		ManageCustomUseCaseFeeBean ucFeeBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.ucFeeBean = ucFeeBean;
		this.flag= flag;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageCustomUseCaseFeeConfirmApprovePanel) constructPanel ###");
		
		final Form<ManageCustomUseCaseFeeConfirmApprovePanel> form = new Form<ManageCustomUseCaseFeeConfirmApprovePanel>("ucFeeConfAprForm",
			new CompoundPropertyModel<ManageCustomUseCaseFeeConfirmApprovePanel>(this));
		
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		// Add fee management fields
		form.add(new Label("useCase", ucFeeBean.getUseCase().getIdAndValue()));
		form.add(new Label("debitOrgUnit", ucFeeBean.getDebitOrgUnit()==null ? "-" : ucFeeBean.getDebitOrgUnit().getIdAndValue()));
		form.add(new Label("creditOrgUnit", ucFeeBean.getCreditOrgUnit() == null ? "-" : ucFeeBean.getCreditOrgUnit().getIdAndValue()));
		form.add(new Label("glCode", ucFeeBean.getGlCode().getIdAndValue()));
		form.add(new Label("debitPiType",  ucFeeBean.getDebitPiType()==null ? "-" : ucFeeBean.getDebitPiType().getIdAndValue()));
		form.add(new Label("creditPiType", ucFeeBean.getCreditPiType()==null ? "-" : ucFeeBean.getCreditPiType().getIdAndValue()));
		form.add(new Label("customerType", ucFeeBean.getCustomerType()==null ? "-" : ucFeeBean.getCustomerType().getIdAndValue()));
		form.add(new Label("ucFeeBean.validFrom"));
		form.add(new Label("ucFeeBean.payeeFee", ucFeeBean.isPayeeFee() ? BtpnConstants.YES_VALUE
						: BtpnConstants.NO_VALUE));
		
		form.add(new Label("currencyCode", ucFeeBean.getCurrencyCode().getIdAndValue()));
		form.add(new Label("ucFeeBean.description"));
		form.add(new Label("ucFeeBean.note"));
		
		// add Slab Fee Container;
		final WebMarkupContainer slabFeeContainer = new WebMarkupContainer("slabFeeContainer");
				
			notificationManageFeesDataView(slabFeeContainer);
			slabFeeContainer.setOutputMarkupId(true);
			slabFeeContainer.setOutputMarkupPlaceholderTag(true);
			slabFeeContainer.setVisibilityAllowed(true);
			slabFeeContainer.setVisible(true);
		form.add(slabFeeContainer);
		
		form.add(new AjaxButton("confirmBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(ucFeeBean)){
					ucFeeBean = new ManageCustomUseCaseFeeBean();
				}
				if (StringUtils.equals(BtpnConstants.APPROVED, flag))
					handleApprove();
				if (StringUtils.equals(BtpnConstants.REJECT, flag))
					handleReject();
			}
		});
		
		form.add(new AjaxButton("backBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(new ManageCustomUseCaseFeeDetailApprovePage(ucFeeBean));
			}
		}.setDefaultFormProcessing(false));
		
		
		form.add(new AjaxButton("cancelBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			}
		}.setDefaultFormProcessing(false));
		
		// Add add Button
		add(form);
	}
	
	
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final DataView<FeeEntryWrkType> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<FeeEntryWrkType>(ucFeeBean.getManageDetailsWrkList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageDetailsWrkList().size() != 0 ;
			}
			
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageDetailsWrkList().size() == 0;

			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageCustomUseCaseFeeConfirmApprovePanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageCustomUseCaseFeeConfirmApprovePanel.this);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return String.format(displayTotalItemsText, feesTotalItemString, feeStartIndex, feeEndIndex);
			}
		};

		// Add the fee header
		feeHeader = new Label(WICKET_ID_FEESTOTALITEMS, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageDetailsWrkList().size() != 0;
			}
		};
		dataViewContainer.add(feeHeader);
		feeHeader.setOutputMarkupId(true);
		feeHeader.setOutputMarkupPlaceholderTag(true);

		dataViewContainer.addOrReplace(dataView);
	}
	
	
	/**
	 * This is the ManageFeeDetailsView for Manage Fees of Sharing and Slab Fee
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageBillPaymentDetailsView extends DataView<FeeEntryWrkType> {

		private static final long serialVersionUID = 1L;

		protected ManageBillPaymentDetailsView(String id, IDataProvider<FeeEntryWrkType> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
		}

		@Override
		protected void onBeforeRender() {
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<FeeEntryWrkType> item) {
			final FeeEntryWrkType entry = item.getModelObject();
			
			item.setModel(new CompoundPropertyModel<FeeEntryWrkType>(entry));
			
			item.add(new Label("minFee", String.valueOf(entry.getMinimumFee()) ));
			item.add(new Label("maxFee", (entry.getMaximumFee()!=null) ? String.valueOf(entry.getMaximumFee()) : "" ));
			item.add(new Label("fixedFee", Long.toString(entry.getFixedFee()/100)));
			item.add(new Label("percentageFee", getDiv100(entry.getPercentageFee())));
			item.add(new Label("thresholdAmount", Long.valueOf(entry.getThresholdAmount()/100).toString() ));
	
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ucFeeBean.getManageDetailsWrkList().size() != 0;
		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			feesTotalItemString = new Integer(size).toString();
			if (size > 0) {
				feeStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				feeEndIndex = feeStartIndex + getItemsPerPage() - 1;
				if (feeEndIndex > size) {
					feeEndIndex = size;
				}
			} else {
				feeStartIndex = 0;
				feeEndIndex = 0;
			}
		}	
	}
	
	
	private void handleApprove(){
	
		final ApproveUseCaseFeeWrkRequest request;
		
		try {
			
			request = this.basePage.getNewMobiliserRequest(ApproveUseCaseFeeWrkRequest.class);
			request.setWorkflowId(ucFeeBean.getWorkFlowId());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setNote(ucFeeBean.getNote());
			
			final ApproveUseCaseFeeWrkResponse response = basePage.getUcFeeWrkClient().approve(request);
			log.info(" ### (ManageCustomUseCaseFeeConfirmApprovePanel) handleApprove RESPONSE CODE ### "+response.getStatus().getCode());
			if(basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("approve.usecase.fee.success", this));
				setResponsePage(new ManageCustomUseCaseFeeSuccessApprovePage(ucFeeBean, flag));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Approving  ===> ", e);
		}
	}
	
	
	private void handleReject(){
		
		final RejectUseCaseFeeWrkRequest request;
	
		try {
			
			request = this.basePage.getNewMobiliserRequest(RejectUseCaseFeeWrkRequest.class);
			log.info(" ### (ManageCustomUseCaseFeeConfirmApprovePanel) handleReject WF ID ### "+ucFeeBean.getWorkFlowId());
			request.setWorkflowId(ucFeeBean.getWorkFlowId());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setNote(ucFeeBean.getNote());
			
			final RejectUseCaseFeeWrkResponse response = basePage.getUcFeeWrkClient().reject(request);
			log.info(" ### (ManageCustomUseCaseFeeConfirmApprovePanel) handleReject RESPONSE CODE ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("reject.usecase.fee.success", this));
				setResponsePage(new ManageCustomUseCaseFeeSuccessApprovePage(ucFeeBean, flag));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Rejecting  ===> ", e);
		}
	}
	
	private static String getDiv100(long amount) {
		return new BigDecimal(amount).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString();
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.usecase.fee" + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("usecase.fee.error", this);
		}
		return message;
	}

}
