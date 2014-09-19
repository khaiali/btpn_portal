package com.sybase365.mobiliser.web.btpn.bank.common.panels;

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
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeeConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeeEditPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the Manage Fee Add Details Panel for fixed, slab, sharing fee.
 * 
 * @author Vikram Gunda
 */
public class ManageBillPaymentFeeDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ManageBillPaymentFeeDetailsPanel.class);

	private BtpnMobiliserBasePage mobBasePage;

	private ManageBillPaymentFeeBean feeBean;
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private String feesTotalItemString;
	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private FeedbackPanel feedback;
	private BtpnCustomPagingNavigator navigator;

	
	/**
	 * Constructor for this page.
	 * 
	 * @param id id of the panel.
	 * @param mobBasePage base Page of the mobiliser.
	 * @param feeBean fee bean for the fees.
	 */
	public ManageBillPaymentFeeDetailsPanel(final String id, final BtpnMobiliserBasePage mobBasePage,
		final ManageBillPaymentFeeBean feeBean) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.feeBean = feeBean == null ? new ManageBillPaymentFeeBean() : feeBean;
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		final Form<ManageBillPaymentFeeDetailsPanel> form = new Form<ManageBillPaymentFeeDetailsPanel>("feeForm",
			new CompoundPropertyModel<ManageBillPaymentFeeDetailsPanel>(this));

		feedback = new FeedbackPanel("errorMessages");
		feedback.setOutputMarkupId(true);
		form.add(feedback);
		
		final WebMarkupContainer billContainer = new WebMarkupContainer("feeContainer");
		billContainer.add(new Label("feeBean.description"));
		billContainer.add(new Label("feeBean.useCase", feeBean.getUseCase().getIdAndValue()));
		
		final WebMarkupContainer product = new WebMarkupContainer("productContainer");	
		product.add(new Label("feeBean.productLabel", feeBean.getProductLabel() == null ? "-" : feeBean.getProductLabel()));
		product.setVisible( "223".equals(feeBean.getUseCase().getId()) || "224".equals(feeBean.getUseCase().getId()) );
		billContainer.add(product);
		
		billContainer.add(new Label("customerType", feeBean.getCustomerType()==null ? "-" : feeBean.getCustomerType().getIdAndValue()));
		billContainer.add(new Label("piType", feeBean.getPiType()==null ? "-" : feeBean.getPiType().getIdAndValue()));
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
		
		billContainer.add(new AjaxButton("editBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				feeBean.getManageFeeDetailsList().addAll(convertToFeeDetails(feeBean.getManageDetailsList()));
				setResponsePage(new ManageBillPaymentFeeEditPage(feeBean));
			}
		});
		
		billContainer.add(new AjaxButton("delBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				feeBean.getManageDetailsWrkList().addAll(convertToFeeWrk(feeBean.getManageDetailsList()));
				setResponsePage(new ManageBillPaymentFeeConfirmPage(feeBean, BtpnConstants.DELETE));
			}
		});
		
		billContainer.add(new AjaxButton("backBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(ManageBillPaymentFeePage.class);
			}
		});
		form.add(billContainer);
		add(form);
	}


	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer dataViewContainer for the fee.
	 */
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final DataView<FeeEntryType> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<FeeEntryType>(feeBean.getManageDetailsList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsList().size() != 0 ;
			}
			
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsList().size() == 0;

			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageBillPaymentFeeDetailsPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageBillPaymentFeeDetailsPanel.this);

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
				return feeBean.getManageDetailsList().size() != 0;
			}
		};
		dataViewContainer.add(feeHeader);
		feeHeader.setOutputMarkupId(true);
		feeHeader.setOutputMarkupPlaceholderTag(true);

		dataViewContainer.addOrReplace(dataView);
	}
	
	
	/**
	 * This is the ManageFeeDetailsView for Manage Fees
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
			item.add(new Label("fixedFee", Long.valueOf(entry.getFixedFee()/100).toString() ));
			item.add(new Label("percentageFee", Long.valueOf(entry.getPercentageFee()).toString() ));
			item.add(new Label("thresholdAmount", Long.valueOf(entry.getThresholdAmount()/100).toString() ));
			
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return feeBean.getManageDetailsList().size() != 0;
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
