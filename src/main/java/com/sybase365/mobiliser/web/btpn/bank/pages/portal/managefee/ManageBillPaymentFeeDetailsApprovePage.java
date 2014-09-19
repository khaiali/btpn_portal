package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the Manage Add Interest Approve details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageBillPaymentFeeDetailsApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_FEENAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEETOTALITEMS = "feeHeader";

	private String feeTotalItemString;
	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private BtpnCustomPagingNavigator navigator;

	private ManageBillPaymentFeeBean feeBean;
	private FeedbackPanel feedbackPanel;
	WebMarkupContainer feeContainer;
	
	
	public ManageBillPaymentFeeDetailsApprovePage(final ManageBillPaymentFeeBean feeBean) {
		super();
		this.feeBean = feeBean;
		constructPage();
	}
	
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
	}
	
	private void constructPage() {
		final Form<ManageBillPaymentFeeDetailsApprovePage> form = new Form<ManageBillPaymentFeeDetailsApprovePage>("feeForm",
				new CompoundPropertyModel<ManageBillPaymentFeeDetailsApprovePage>(this));

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
		
		billContainer.add(new AjaxButton("approveBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(new ManageBillPaymentFeeConfirmApprovePage(feeBean, BtpnConstants.APPROVED));
			}
		});
		
		billContainer.add(new AjaxButton("rejectBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form){
				setResponsePage(new ManageBillPaymentFeeConfirmApprovePage(feeBean, BtpnConstants.REJECT));
			}
		}.setDefaultFormProcessing(false));
		
		
		
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

		final DataView<FeeEntryWrkType> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<FeeEntryWrkType>(feeBean.getManageDetailsWrkList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEENAVIGATOR, dataView) {

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

		final String displayTotalItemsText = ManageBillPaymentFeeDetailsApprovePage.this.getLocalizer().getString(
			"fees.totalitems.header", ManageBillPaymentFeeDetailsApprovePage.this);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return String.format(displayTotalItemsText, feeTotalItemString, feeStartIndex, feeEndIndex);
			}
		};

		// Add the fee header
		feeHeader = new Label(WICKET_ID_FEETOTALITEMS, headerDisplayModel) {

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
	 * This is the ManageFeeDetailsView for Manage Fees
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
			return feeBean.getManageDetailsWrkList().size() != 0;
		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			feeTotalItemString = new Integer(size).toString();
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
	
}
