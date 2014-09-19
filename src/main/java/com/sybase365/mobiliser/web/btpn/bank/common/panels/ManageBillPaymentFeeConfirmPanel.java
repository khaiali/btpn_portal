package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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

import com.btpnwow.core.fee.facade.contract.wrk.AddBillerFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.AddBillerFeeWrkResponse;
import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;
import com.btpnwow.core.fee.facade.contract.wrk.RemoveBillerFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.RemoveBillerFeeWrkResponse;
import com.btpnwow.core.fee.facade.contract.wrk.UpdateBillerFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.UpdateBillerFeeWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeeSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Biller Confirm for bank portals. This panel consists of adding fee as fixed, slab and sharing.
 * 
 * @author Feny Yanti
 */
public class ManageBillPaymentFeeConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageBillPaymentFeeConfirmPanel.class);
	
	private BtpnBaseBankPortalSelfCarePage basePage;
	private ManageBillPaymentFeeBean feeBean;
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private String feesTotalItemString;
	
	private BtpnCustomPagingNavigator navigator;

	private String flag;


	public ManageBillPaymentFeeConfirmPanel(final String id, final BtpnBaseBankPortalSelfCarePage basePage,
			ManageBillPaymentFeeBean feeBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.feeBean = feeBean == null ? new ManageBillPaymentFeeBean() : feeBean;
		this.flag = flag;
		constructPanel();
	}

	
	private void constructPanel() {
		
		final Form<ManageBillPaymentFeeConfirmPanel> form = new Form<ManageBillPaymentFeeConfirmPanel>("feeConfirmForm",
			new CompoundPropertyModel<ManageBillPaymentFeeConfirmPanel>(this));
			
		// Add the Manage fee container
		final WebMarkupContainer billContainer = new WebMarkupContainer("feeContainer");
		billContainer.add(new Label("feeBean.description"));
		billContainer.add(new Label("feeBean.useCase", feeBean.getUseCase().getIdAndValue()));
		
		final WebMarkupContainer product = new WebMarkupContainer("productContainer");	
		product.add(new Label("feeBean.productLabel", feeBean.getProductLabel() == null ? "-" : feeBean.getProductLabel()));
		product.setVisible( "223".equals(feeBean.getUseCase().getId()) || "224".equals(feeBean.getUseCase().getId()) );
		billContainer.add(product);
			
		billContainer.add(new Label("customerType", feeBean.getCustomerType() == null ? "-" : feeBean.getCustomerType().getIdAndValue()));
		billContainer.add(new Label("piType", feeBean.getPiType()==null? "-" : feeBean.getPiType().getIdAndValue()));
		billContainer.add(new Label("orgUnit", feeBean.getOrgUnit()==null ? "-" : feeBean.getOrgUnit().getIdAndValue()));
		billContainer.add(new Label("feeBean.validFrom"));
		billContainer.add(new Label("feeBean.applyToPayee",
						  feeBean.getApplyToPayee() ? BtpnConstants.YES_VALUE
						: BtpnConstants.NO_VALUE));
		billContainer.add(new Label("feeBean.glCode", feeBean.getGlCode().getIdAndValue()));
		billContainer.add(new Label("feeBean.currency", feeBean.getCurrency().getIdAndValue()));
		
		billContainer.add(new FeedbackPanel("errorMessages"));
		notificationManageFeesDataView(billContainer);
		billContainer.setOutputMarkupId(true);
		billContainer.setRenderBodyOnly(true);
		
		// add Slab Fee Container;
		final WebMarkupContainer slabFeeContainer = new WebMarkupContainer("slabFeeContainer");
			notificationManageFeesDataView(slabFeeContainer);
			slabFeeContainer.setOutputMarkupId(true);
			slabFeeContainer.setOutputMarkupPlaceholderTag(true);
			slabFeeContainer.setVisibilityAllowed(true);
			slabFeeContainer.setVisible(true);
		billContainer.add(slabFeeContainer);
		
		// Add Confirm button
		Button confirmButton = new Button("btnConfirm"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(){
				
				if (StringUtils.equals("add", flag)){
					handleAddBillerFeeWrk();
				}
				if (StringUtils.equals("update", flag)){
					handleUpdateBillerFeeWrk();
				}
				if (StringUtils.equals("delete", flag)){
					handleDeleteBillerFeeWrk();
				}
			}
		};
		billContainer.add(confirmButton);
		
		
		// Add Cancel button
		billContainer.add(new Button("btnCancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			};
		}.setDefaultFormProcessing(false));
		
		form.add(billContainer);
		// Add add Button
		add(form);
	}
	
	
	
	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer dataViewContainer for the fee.
	 */
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final DataView<FeeEntryWrkType> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<FeeEntryWrkType>(feeBean.getManageDetailsWrkList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsWrkList().size() != 0 ;
			}
			
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsWrkList().size() == 0;

			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageBillPaymentFeeConfirmPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageBillPaymentFeeConfirmPanel.this);

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
				return feeBean.getManageDetailsWrkList().size() != 0;
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
			item.add(new Label("fixedFee", Long.valueOf(entry.getFixedFee()/100).toString() ));
			item.add(new Label("percentageFee", Long.valueOf(entry.getPercentageFee()).toString() ));
			item.add(new Label("thresholdAmount", Long.valueOf(entry.getThresholdAmount()/100).toString() ));
			
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return feeBean.getManageDetailsWrkList().size() != 0;
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
	
	private void handleAddBillerFeeWrk(){
		
		final AddBillerFeeWrkRequest request;
		
		try {
			
			log.info(" ### (ManageCustomBillerFeeConfirmPanel) handleAddBillerFeeWrk ### ");
			request = basePage.getNewMobiliserRequest(AddBillerFeeWrkRequest.class);
			request.setUseCase(Integer.parseInt(feeBean.getUseCase().getId()));
			if(feeBean.getOrgUnit()!=null)
				request.setDebitOrgUnit(feeBean.getOrgUnit().getId());
			if(feeBean.getCustomerType() !=null)
				request.setDebitCustomerType(Integer.parseInt(feeBean.getCustomerType().getId()));
			
			if(feeBean.getPiType() !=null)
				request.setDebitPiType(Integer.parseInt(feeBean.getPiType().getId()));
			
			request.setBillerId(feeBean.getBillerId());
			request.setProductId(feeBean.getProductId());
			request.setDescription(feeBean.getDescription());
			
			Date validFrom = feeBean.getValidFrom();
			Calendar cal = Calendar.getInstance();
			cal.setTime(validFrom);
			request.setValidFrom(PortalUtils.getSaveXMLGregorianCalendar(cal));
	
			request.setPayeeFee(feeBean.getApplyToPayee());
			request.setCurrencyCode(feeBean.getCurrency().getId());
			request.setGlCode(Long.parseLong(feeBean.getGlCode().getId()));
			request.setNote(feeBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.getEntry().addAll(feeBean.getManageDetailsWrkList());
	

			final AddBillerFeeWrkResponse response = basePage.getBillerFeeWrkClient().add(request);
			log.info(" ###  RESPONSE CODE ## "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("add.biller.fee.success", this));
				setResponsePage(new ManageBillPaymentFeeSuccessPage(feeBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Add Biller Fee Wrk ===> ", e);
		}
	}
	
	
	private void handleUpdateBillerFeeWrk(){
		
		final UpdateBillerFeeWrkRequest request;
		
		try {
			
			request = basePage.getNewMobiliserRequest(UpdateBillerFeeWrkRequest.class);
			
			request.setId(feeBean.getId());
			request.setDescription(feeBean.getDescription());
			request.setGlCode(Long.parseLong(feeBean.getGlCode().getId()));
			request.setNote(feeBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.getEntry().addAll(feeBean.getManageDetailsWrkList());
	
			
			final UpdateBillerFeeWrkResponse response = this.basePage.getBillerFeeWrkClient().update(request);
			log.info(" ### handleUpdateBillerFeeWrk ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("update.biller.fee.success", this));
				setResponsePage(new ManageBillPaymentFeeSuccessPage(feeBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Update Biller Fee Wrk ===> ", e);
		}
	}
	
	
	private void handleDeleteBillerFeeWrk(){
		
		final RemoveBillerFeeWrkRequest request;
		
		try {
			
			request = basePage.getNewMobiliserRequest(RemoveBillerFeeWrkRequest.class);
			
			request.setId(feeBean.getId());
			request.setNote(feeBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final RemoveBillerFeeWrkResponse response = this.basePage.getBillerFeeWrkClient().remove(request);
			log.info(" ### handleDeleteBillerFeeWrk ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("delete.biller.fee.success", this));
				setResponsePage(new ManageBillPaymentFeeSuccessPage(feeBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Delete Biller Fee Wrk ===> ", e);
		}
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.usecase.fee" + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("biller.fee.error", this);
		}
		return message;
	}

}
