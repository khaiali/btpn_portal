package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.fee.facade.contract.wrk.AddUseCaseFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.AddUseCaseFeeWrkResponse;
import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;
import com.btpnwow.core.fee.facade.contract.wrk.RemoveUseCaseFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.RemoveUseCaseFeeWrkResponse;
import com.btpnwow.core.fee.facade.contract.wrk.UpdateUseCaseFeeWrkRequest;
import com.btpnwow.core.fee.facade.contract.wrk.UpdateUseCaseFeeWrkResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeAddPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeEditPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee Add Panel for bank portals. This panel consists of adding fee as fixed, slab and sharing.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeeConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageCustomUseCaseFeeConfirmPanel.class);
	
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

	private FeedbackPanel feedBackPanel;
	private String flag;
	
	

	public ManageCustomUseCaseFeeConfirmPanel(final String id, final BtpnBaseBankPortalSelfCarePage basePage,
			ManageCustomUseCaseFeeBean ucFeeBean, String flag) {
		super(id);
		this.basePage = basePage;
		this.ucFeeBean = ucFeeBean;
		this.flag = flag;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageCustomUseCaseFeeConfirmPanel) constructPanel ###");
		
		final Form<ManageCustomUseCaseFeeConfirmPanel> form = new Form<ManageCustomUseCaseFeeConfirmPanel>("ucFeeConfirmForm",
			new CompoundPropertyModel<ManageCustomUseCaseFeeConfirmPanel>(this));
		
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
		form.add(new Label("ucFeeBean.payeeFee",
				ucFeeBean.isPayeeFee() ? BtpnConstants.YES_VALUE
						: BtpnConstants.NO_VALUE));
		form.add(new Label("ucFeeBean.fixedFee"));
		form.add(new Label("ucFeeBean.percentageFee"));
		form.add(new Label("ucFeeBean.maximumFee"));
		form.add(new Label("ucFeeBean.minimumFee"));
		form.add(new AmountLabel("ucFeeBean.thresholdAmount"));
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
		
		// Add Confirm button
		Button confirmButton = new Button("submitConfirm"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(){
				if (!PortalUtils.exists(ucFeeBean)){
					ucFeeBean = new ManageCustomUseCaseFeeBean();
				}
				if (StringUtils.equals(BtpnConstants.ADD, flag)){
					handleAddUseCaseFeeWrk();
				}
				if (StringUtils.equals(BtpnConstants.UPDATE, flag)){
					handleUpdateUseCaseFeeWrk();
				}
				if (StringUtils.equals(BtpnConstants.DELETE, flag)){
					handleDeleteUseCaseFeeWrk();
				}
			}
		};
		confirmButton.add(new AttributePrepender("onclick", Model.of("loading(submitConfirm)"), ";"));
		form.add(confirmButton);
		
		// Add Back button
		form.add(new AjaxButton("submitBack"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (StringUtils.equals(BtpnConstants.ADD, flag)){
					setResponsePage(new ManageCustomUseCaseFeeAddPage(ucFeeBean));
				}
				if (StringUtils.equals(BtpnConstants.UPDATE, flag)){
					setResponsePage(new ManageCustomUseCaseFeeEditPage(ucFeeBean));
				}
			}
		}.setDefaultFormProcessing(false));
		
		// Add Cancel button
		form.add(new Button("submitCancle") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			};
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

		final String displayTotalItemsText = ManageCustomUseCaseFeeConfirmPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageCustomUseCaseFeeConfirmPanel.this);

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
			item.add(new Label("percentageFee", getDiv100(entry.getPercentageFee()/100)));
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
	
	private static String getDiv100(long amount) {
		return new BigDecimal(amount).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString();
	}
	
	private void handleAddUseCaseFeeWrk(){
		
		final AddUseCaseFeeWrkRequest request;
		
		try {
			
			request = basePage.getNewMobiliserRequest(AddUseCaseFeeWrkRequest.class);
			request.setDescription(ucFeeBean.getDescription());
			request.setUseCase(Integer.parseInt(ucFeeBean.getUseCase().getId()));
			if(ucFeeBean.getDebitOrgUnit() !=null)	
				request.setDebitOrgUnit(ucFeeBean.getDebitOrgUnit().getId()) ;
			
			if(ucFeeBean.getCreditOrgUnit() !=null)
				request.setCreditOrgUnit(ucFeeBean.getCreditOrgUnit().getId());
			
			if(ucFeeBean.getCustomerType() !=null)
				request.setCustomerType(Integer.parseInt(ucFeeBean.getCustomerType().getId()));
			
			if(ucFeeBean.getDebitPiType() !=null)
				request.setDebitPiType(Integer.parseInt(ucFeeBean.getDebitPiType().getId()));
			if(ucFeeBean.getCreditPiType() !=null)
				request.setCreditPiType(Integer.parseInt(ucFeeBean.getCreditPiType().getId()));
			
			Date validFrom = ucFeeBean.getValidFrom();
			Calendar cal = Calendar.getInstance();
			cal.setTime(validFrom);
			request.setValidFrom(PortalUtils.getSaveXMLGregorianCalendar(cal));
			
//			Calendar cal = Calendar.getInstance();
//			request.setValidFrom(PortalUtils.getSaveXMLGregorianCalendar(cal));
//			log.info(" ### (ManageCustomUseCaseFeeConfirmPanel) GREGORIAN CAL ### "+PortalUtils.getSaveXMLGregorianCalendar(cal));
			
			request.setPayeeFee(ucFeeBean.isPayeeFee());
			request.setCurrencyCode(ucFeeBean.getCurrencyCode().getId());
			request.setGlCode(Long.parseLong(ucFeeBean.getGlCode().getId()));
			
			request.getEntry().addAll(ucFeeBean.getManageDetailsWrkList());
			request.setNote(ucFeeBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			
			final AddUseCaseFeeWrkResponse response = basePage.getUcFeeWrkClient().add(request);
			log.info(" ### (ManageCustomUseCaseFeeConfirmPanel) handleAddUseCaseFeeWrk RESPONSE CODE ## "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("add.usecase.fee.success", this));
				setResponsePage(new ManageCustomUseCaseFeeSuccessPage(ucFeeBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Add Use Case Fee ===> ", e);
		}
	}
	
	
	private void handleUpdateUseCaseFeeWrk(){
		
		final UpdateUseCaseFeeWrkRequest request;
		
		try {
			
			request = basePage.getNewMobiliserRequest(UpdateUseCaseFeeWrkRequest.class);

			request.setId(ucFeeBean.getId());
			request.setDescription(ucFeeBean.getDescription());
			request.setGlCode(Long.parseLong(ucFeeBean.getGlCode().getId()));
			request.getEntry().addAll(ucFeeBean.getManageDetailsWrkList());
			request.setNote(ucFeeBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final UpdateUseCaseFeeWrkResponse response = this.basePage.getUcFeeWrkClient().update(request);
			log.info(" ### (ManageCustomUseCaseFeeConfirmPanel) handleUpdateUseCaseFeeWrk ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("update.usecase.fee.success", this));
				setResponsePage(new ManageCustomUseCaseFeeSuccessPage(ucFeeBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Update Use Case Fee Wrk ===> ", e);
		}
	}
	
	private void handleDeleteUseCaseFeeWrk(){
		
		final RemoveUseCaseFeeWrkRequest request;
		
		try {
			
			request = basePage.getNewMobiliserRequest(RemoveUseCaseFeeWrkRequest.class);

			request.setId(ucFeeBean.getId());
			request.setNote(ucFeeBean.getNote());
			request.setCallerId(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final RemoveUseCaseFeeWrkResponse response = this.basePage.getUcFeeWrkClient().remove(request);
			log.info(" ### (ManageCustomUseCaseFeeConfirmPanel) handleUpdateUseCaseFeeWrk ### "+response.getStatus().getCode());
			if (basePage.evaluateBankPortalMobiliserResponse(response)){
				getSession().info(getLocalizer().getString("delete.usecase.fee.success", this));
				setResponsePage(new ManageCustomUseCaseFeeSuccessPage(ucFeeBean));
			}else{
				error(MobiliserUtils.errorMessage(response.getStatus().getCode(), response.getStatus().getValue(), getLocalizer(), this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Exception occured while Update Use Case Fee Wrk ===> ", e);
		}
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.usecase.fee" + errorCode;
		String message = this.basePage.getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = this.basePage.getLocalizer().getString("usecase.fee.error", this);
		}
		return message;
	}

}
