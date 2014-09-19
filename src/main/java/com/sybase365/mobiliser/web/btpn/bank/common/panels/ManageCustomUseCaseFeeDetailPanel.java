package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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

import com.btpnwow.core.fee.facade.contract.FeeEntryType;
import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeEditPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee Add Details Panel.
 * 
 */
public class ManageCustomUseCaseFeeDetailPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ManageCustomUseCaseFeeDetailPanel.class);

	protected BtpnBaseBankPortalSelfCarePage basePage;
	protected ManageCustomUseCaseFeeBean ucFeeBean;
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private String feesTotalItemString;
	
	private BtpnCustomPagingNavigator navigator;
	
	public ManageCustomUseCaseFeeDetailPanel(final String id, BtpnBaseBankPortalSelfCarePage basePage,  ManageCustomUseCaseFeeBean ucFeeBean) {
		super(id);
		this.basePage = basePage;
		this.ucFeeBean = ucFeeBean;
		constructPanel();
	}

	
	private void constructPanel() {
		
		log.info(" ### (ManageCustomUseCaseFeeDetailPanel) constructPanel ### ");
		
		final Form<ManageCustomUseCaseFeeDetailPanel> form = new Form<ManageCustomUseCaseFeeDetailPanel>("ucFeeDetForm",
			new CompoundPropertyModel<ManageCustomUseCaseFeeDetailPanel>(this));

		// Add feedback panel for Error Messages
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		form.add(feedBackPanel);
		
		// Add fee management fields
		form.add(new Label("useCase", ucFeeBean.getUseCase().getIdAndValue()));
		form.add(new Label("debitOrgUnit", ucFeeBean.getDebitOrgUnit().getIdAndValue()));
		form.add(new Label("creditOrgUnit", ucFeeBean.getCreditOrgUnit().getIdAndValue()));
		form.add(new Label("glCode", ucFeeBean.getGlCode().getIdAndValue()));
		form.add(new Label("debitPiType", ucFeeBean.getDebitPiType() == null ? "-" : ucFeeBean.getDebitPiType().getIdAndValue()));
		form.add(new Label("creditPiType", ucFeeBean.getCreditPiType()==null ? "-" : ucFeeBean.getCreditPiType().getIdAndValue()));
		form.add(new Label("customerType", ucFeeBean.getCustomerType()==null ? "-" : ucFeeBean.getCustomerType().getIdAndValue()));
		form.add(new Label("ucFeeBean.validFrom"));
		form.add(new Label("ucFeeBean.payeeFee",
				ucFeeBean.isPayeeFee() ? BtpnConstants.YES_VALUE
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
		
		form.add(new AjaxButton("editBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(ucFeeBean)){
					ucFeeBean = new ManageCustomUseCaseFeeBean();
				}
				ucFeeBean.getManageFeeDetailsList().addAll(convertToFeeDetails(ucFeeBean.getManageDetailsList()));
				setResponsePage(new ManageCustomUseCaseFeeEditPage(ucFeeBean));
			}
		});
		
		form.add(new AjaxButton("delBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				if (!PortalUtils.exists(ucFeeBean)){
					ucFeeBean = new ManageCustomUseCaseFeeBean();
				}
				ucFeeBean.getManageDetailsWrkList().addAll(convertToFeeWrk(ucFeeBean.getManageDetailsList()));
				setResponsePage(new ManageCustomUseCaseFeeConfirmPage(ucFeeBean, BtpnConstants.DELETE));
			}
		});
		
		form.add(new AjaxButton("backBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(ManageCustomUseCaseFeePage.class);
			}
		});
		
		// Add add Button
		add(form);
	}
	
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final DataView<FeeEntryType> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<FeeEntryType>(ucFeeBean.getManageDetailsList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageDetailsList().size() != 0 ;
			}
			
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageDetailsList().size() == 0;
			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageCustomUseCaseFeeDetailPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageCustomUseCaseFeeDetailPanel.this);

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
				return ucFeeBean.getManageDetailsList().size() != 0;
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
	private class ManageBillPaymentDetailsView extends DataView<FeeEntryType> {

		private static final long serialVersionUID = 1L;

		protected ManageBillPaymentDetailsView(String id, IDataProvider<FeeEntryType> dataProvider) {
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
		protected void populateItem(final Item<FeeEntryType> item) {
			final FeeEntryType entry = item.getModelObject();
			
			item.setModel(new CompoundPropertyModel<FeeEntryType>(entry));
			
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
			return ucFeeBean.getManageDetailsList().size() != 0;
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
	
	//List FeeDetails
	public static List<ManageBillPaymentFeeDetailsBean> convertToFeeDetails(
			final List<FeeEntryType> txnList) {
		
		final List<ManageBillPaymentFeeDetailsBean> ucFeeList = new ArrayList<ManageBillPaymentFeeDetailsBean>();
		int i=0;
		for (final FeeEntryType uc : txnList) {
			final ManageBillPaymentFeeDetailsBean ucBean = new ManageBillPaymentFeeDetailsBean();
			ucBean.setFixedFee(uc.getFixedFee());
			ucBean.setPercentageFee( uc.getPercentageFee());
			ucBean.setThresholdAmount(uc.getThresholdAmount());
			ucBean.setMinimumFee(uc.getMinimumFee());
			if(uc.getMaximumFee()!=null){
				ucBean.setMaximumFee(uc.getMaximumFee());
			}	
			ucBean.setId(i);
			ucFeeList.add(ucBean);
			i++;
		}
		return ucFeeList;
	}
	
	//List FeeWrk
	public static List<FeeEntryWrkType> convertToFeeWrk(
			final List<FeeEntryType> txnList) {
		
		final List<FeeEntryWrkType> ucFeeList = new ArrayList<FeeEntryWrkType>();
		
		for (final FeeEntryType uc : txnList) {
			final FeeEntryWrkType ucBean = new FeeEntryWrkType();
			ucBean.setFixedFee(uc.getFixedFee());
			ucBean.setPercentageFee( uc.getPercentageFee());
			ucBean.setThresholdAmount(uc.getThresholdAmount());
			ucBean.setMinimumFee(uc.getMinimumFee());
			if(uc.getMaximumFee()!=null){
				ucBean.setMaximumFee(uc.getMaximumFee());
			}	
			
			ucFeeList.add(ucBean);
		
		}
		return ucFeeList;
	}
	
}
