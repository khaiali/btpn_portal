package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageBillPaymentFeeSuccessPage extends BtpnBaseBankPortalSelfCarePage {
	
	private ManageBillPaymentFeeBean feeBean;
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private String feesTotalItemString;
	
	private BtpnCustomPagingNavigator navigator;

	/**
	 * Constructor for this page.
	 */
	public ManageBillPaymentFeeSuccessPage(final ManageBillPaymentFeeBean feeBean) {
		super();
		this.feeBean = feeBean;
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();

	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		Form<ManageBillPaymentFeeSuccessPage> form = new Form<ManageBillPaymentFeeSuccessPage>("feeSuccessForm",
			new CompoundPropertyModel<ManageBillPaymentFeeSuccessPage>(this));
		
		// Add the Manage fee container
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
			
		// Add finish button
		billContainer.add(new Button("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageBillPaymentFeePage.class);
			};
		}.setDefaultFormProcessing(false));
				
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

		final String displayTotalItemsText = ManageBillPaymentFeeSuccessPage.this.getLocalizer().getString(
			"fees.totalitems.header", ManageBillPaymentFeeSuccessPage.this);

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
	
	private static String getDiv100(long amount) {
		return new BigDecimal(amount).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString();
	}
	
}
