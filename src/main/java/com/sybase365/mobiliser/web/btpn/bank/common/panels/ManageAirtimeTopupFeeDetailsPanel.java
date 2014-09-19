package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.GetFeeDetailRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.GetFeeDetailResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageFeeDetailsDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageAirtimeTopupFeeSubDetailsPage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage Fee Add Details Panel for fixed, slab, sharing fee.
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupFeeDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ManageAirtimeTopupFeeDetailsPanel.class);

	private BtpnMobiliserBasePage mobBasePage;

	private ManageAirtimeTopupFeeBean feeBean;

	private String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";

	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private String feesTotalItemString;

	private int feeStartIndex = 0;

	private int feeEndIndex = 0;

	private Label feeHeader;

	private BtpnCustomPagingNavigator navigator;

	private WebMarkupContainer airtimeFeeContainer;

	private DataView<ManageFeeDetailsBean> dataView;

	/**
	 * Constructor for this page.
	 * 
	 * @param id id of the panel.
	 * @param mobBasePage base Page of the mobiliser.
	 * @param feeBean fee bean for the fees.
	 * @param feeDetailsBean fee details bean for the fee.
	 */
	public ManageAirtimeTopupFeeDetailsPanel(final String id, final BtpnMobiliserBasePage mobBasePage,
		final ManageAirtimeTopupFeeBean feeBean) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.feeBean = feeBean;
		fetchmanageFeeDetailsList();
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		final Form<ManageAirtimeTopupFeeDetailsPanel> form = new Form<ManageAirtimeTopupFeeDetailsPanel>("feeForm",
			new CompoundPropertyModel<ManageAirtimeTopupFeeDetailsPanel>(this));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		airtimeFeeContainer = new WebMarkupContainer("airtimeFeeContainer");
		form.add(airtimeFeeContainer);
		addBillPaymentFeeContainer(codeValueChoiceRender);
		// Add add Button
		add(form);
	}

	/**
	 * This method addBillerCategoryContainer for creating the fee.
	 */
	private void addBillPaymentFeeContainer(final IChoiceRenderer<CodeValue> codeValueChoiceRender) {
		airtimeFeeContainer.add(new Label("feeBean.useCaseName.id"), new Label("feeBean.telco.id"), new Label(
			"feeBean.productName.id"), new Label("feeBean.productName.value"), new AmountLabel("feeBean.transactionAmount"),
			new FeedbackPanel("errorMessages"));
		notificationManageFeesDataView(airtimeFeeContainer);

	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer dataViewContainer for the fee.
	 * @param isSlabFee isSlabFee for the container.
	 */
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final ManageFeeDetailsDataProvider dataProvider = new ManageFeeDetailsDataProvider("feeName",
			feeBean.getManageDetailsList());
		dataView = new ManageFeeDetailsView(WICKET_ID_PAGEABLE, dataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsList().size() != 0;

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

		final String displayTotalItemsText = ManageAirtimeTopupFeeDetailsPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageAirtimeTopupFeeDetailsPanel.this);

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

		dataViewContainer.add(new BtpnOrderByOrder("orderByFeeName", "feeName", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByglCode", "glCode", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByfixedFee", "fixedFee", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderBypercentageFee", "percentageFee", dataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageFeeDetailsView for Manage Fees of Sharing and Slab Fee
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageFeeDetailsView extends DataView<ManageFeeDetailsBean> {

		private static final long serialVersionUID = 1L;

		protected ManageFeeDetailsView(String id, IDataProvider<ManageFeeDetailsBean> dataProvider) {
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
		protected void populateItem(final Item<ManageFeeDetailsBean> item) {
			final ManageFeeDetailsBean entry = (ManageFeeDetailsBean) item.getModelObject();
			item.setModel(new CompoundPropertyModel<ManageFeeDetailsBean>(entry));
			// Add the File name
			item.add(new Label("feeName", entry.getFeeName()));
			item.add(new Label("glCode", entry.getGlCode().getId()));
			item.add(new AmountLabel("fixedFee"));
			item.add(new Label("percentageFee", entry.getPercentageFee().toString() + " %"));
			// Add the details Link
			final AjaxLink<ManageFeeDetailsBean> detailsLink = new AjaxLink<ManageFeeDetailsBean>(WICKET_ID_LINK,
				item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ManageAirtimeTopupFeeSubDetailsPage(feeBean,
						(ManageFeeDetailsBean) item.getModelObject()));
				}

			};
			item.add(detailsLink);

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

	/**
	 * Fetch the Manage Fee Details List from service.
	 * 
	 * @return List<ManageFeeDetailsBean> Manage Fee Details bean for ManageFeeBean
	 */
	private void fetchmanageFeeDetailsList() {
		try {
			// Transaction GL Request
			final GetFeeDetailRequest request = this.mobBasePage.getNewMobiliserRequest(GetFeeDetailRequest.class);
			request.getUseCaseFeeId().addAll(feeBean.getUseCaseFeeId());
			final GetFeeDetailResponse response = this.mobBasePage.feeClient.getFeeDetail(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				ConverterUtils.convertToManageAirtimeFeeBean(response.getFeeDetail().getUseCaseFee(), feeBean);
				feeBean.setManageDetailsList(ConverterUtils.convertToManageFeeDetailsBean(response.getFeeDetail()
					.getFees()));
				feeBean.setUseCaseFeeId(request.getUseCaseFeeId());
			} else {
				error(getLocalizer().getString("error.manage.fees", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetchmanageBillPayFeeDetailsList ===> ", e);
		}
	}
}
