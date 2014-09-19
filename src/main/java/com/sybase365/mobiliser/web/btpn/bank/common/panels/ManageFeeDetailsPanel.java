package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.FeeConfig;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.DeleteFeeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.DeleteFeeResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.GetFeeDetailRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.GetFeeDetailResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageFeeDetailsDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageFeeSubDetailsPage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_FEENAVIGATOR = "feeNavigator";

	private static final String WICKET_ID_FEETOTALITEMS = "feeHeader";

	private String feeTotalItemString;

	private int feeStartIndex = 0;

	private int feeEndIndex = 0;

	private Label feeHeader;

	private BtpnCustomPagingNavigator navigator;

	private BtpnMobiliserBasePage mobBasePage;

	private ManageFeeBean feeBean;

	private boolean isSlabFee;

	private static final Logger LOG = LoggerFactory.getLogger(ManageFeeDetailsPanel.class);

	/**
	 * Constructor for this page.
	 */
	public ManageFeeDetailsPanel(String id, BtpnMobiliserBasePage mobBasePage, final ManageFeeBean feeBean) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.feeBean = feeBean;
		this.isSlabFee = feeBean.getFeeType().equals(BtpnConstants.USECASE_SLAB_RADIO);
		fetchmanageFeeDetailsList();
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		Form<ManageFeeDetailsPanel> form = new Form<ManageFeeDetailsPanel>("feeForm",
			new CompoundPropertyModel<ManageFeeDetailsPanel>(this));
		// Add the Manage fee container
		WebMarkupContainer productsContainer = new WebMarkupContainer("feeContainer");
		productsContainer.add(new Label("feeBean.useCaseName"));
		productsContainer.add(new Label("feeBean.productName"));
		productsContainer.add(new Label("feeBean.feeType"));
		productsContainer.add(new Label("feeBean.applyToPayee"));

		// Transaction Amount
		WebMarkupContainer transContainer = new WebMarkupContainer("transContainer");
		transContainer.add(new AmountLabel("feeBean.transactionAmount"));
		transContainer.setVisible(!isSlabFee);
		productsContainer.add(transContainer);

		String glCode = "";
		String feeName = "";

		final List<ManageFeeDetailsBean> feeList = feeBean.getManageDetailsList();
		if (PortalUtils.exists(feeList)) {
			final ManageFeeDetailsBean feeDetailsBean = feeList.get(0);
			feeName = feeDetailsBean.getFeeName() == null ? "" : feeDetailsBean.getFeeName();
			glCode = feeDetailsBean.getGlCode() == null ? "" : feeDetailsBean.getGlCode().getValue();
		}

		// GLCodeContainer
		WebMarkupContainer glCodeContainer = new WebMarkupContainer("glCodeContainer");
		glCodeContainer.add(new Label("glCode.value", glCode));
		glCodeContainer.setVisible(isSlabFee);
		productsContainer.add(glCodeContainer);

		// feeName Container
		WebMarkupContainer feeNameContainer = new WebMarkupContainer("feeNameContainer");
		feeNameContainer.add(new Label("feeName.value", feeName));
		feeNameContainer.setVisible(isSlabFee);
		productsContainer.add(feeNameContainer);

		productsContainer.add(new FeedbackPanel("errorMessages"));
		manageFeeDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		productsContainer.add(addDeleteButton());
		form.add(productsContainer);
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addDeleteButton() {
		Button submitButton = new Button("btnDelete") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleDeleteFees();
				setResponsePage(ManageFeePage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageFeeDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageFeeDetailsBean> manageLimitList = feeBean.getManageDetailsList();

		ManageFeeDetailsDataProvider dataProvider = new ManageFeeDetailsDataProvider(
			isSlabFee ? "minValue" : "feeName", manageLimitList);

		final DataView<ManageFeeDetailsBean> dataView = new ManageFeeDetailsDataView(WICKET_ID_PAGEABLE, dataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEENAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageLimitList.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ManageFeeDetailsPanel.this.getLocalizer().getString(
					"fee.totalitems.header", ManageFeeDetailsPanel.this);
				return String.format(displayTotalItemsText, feeTotalItemString, feeStartIndex, feeEndIndex);
			}

		};
		feeHeader = new Label(WICKET_ID_FEETOTALITEMS, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageLimitList.size() != 0;

			}
		};
		dataViewContainer.add(feeHeader);
		feeHeader.setOutputMarkupId(true);
		feeHeader.setOutputMarkupPlaceholderTag(true);

		// Add sort for Manage Fees Details provider
		dataViewContainer.add(new BtpnOrderByOrder("orderByFeeName", "feeName", dataProvider, dataView)
			.setVisible(!isSlabFee));
		dataViewContainer.add(new BtpnOrderByOrder("orderByglCode", "glCode", dataProvider, dataView)
			.setVisible(!isSlabFee));
		dataViewContainer.add(new BtpnOrderByOrder("orderByMinRange", "minValue", dataProvider, dataView)
			.setVisible(isSlabFee));
		dataViewContainer.add(new BtpnOrderByOrder("orderByMaxRange", "maxValue", dataProvider, dataView)
			.setVisible(isSlabFee));
		dataViewContainer.add(new BtpnOrderByOrder("orderByfixedFee", "fixedFee", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderBypercentageFee", "percentageFee", dataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageLimitDataView for Managing Limits.
	 * 
	 * @author Vikram Gunda
	 */
	public class ManageFeeDetailsDataView extends DataView<ManageFeeDetailsBean> {

		private static final long serialVersionUID = 1L;

		protected ManageFeeDetailsDataView(String id, IDataProvider<ManageFeeDetailsBean> dataProvider) {
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

			final ManageFeeDetailsBean entry = item.getModelObject();

			item.setModel(new CompoundPropertyModel<ManageFeeDetailsBean>(entry));
			// Add the File name
			item.add(new Label("feeName").setVisible(!isSlabFee));
			// Add the uploaded date
			item.add(new Label("glCode.value").setVisible(!isSlabFee));
			// Add the File name
			item.add(new AmountLabel("minValue").setVisible(isSlabFee));
			// Add the uploaded date
			item.add(new AmountLabel("maxValue").setVisible(isSlabFee));
			// Add the fee Type
			item.add(new AmountLabel("fixedFee"));
			// Add the fee Type
			item.add(new Label("percentageFee", (entry.getPercentageFee() != null ? entry.getPercentageFee() : "0.0")
					+ " %"));

			// Add the details Link
			final AjaxLink<ManageFeeDetailsBean> detailsLink = new AjaxLink<ManageFeeDetailsBean>(WICKET_ID_LINK,
				item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ManageFeeSubDetailsPage(feeBean, (ManageFeeDetailsBean) item.getModelObject()));
				}

			};
			item.add(detailsLink);
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));

		}

		@Override
		public boolean isVisible() {
			return internalGetDataProvider().size() != 0;

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
				feeBean.setManageDetailsList(ConverterUtils.convertToManageFeeDetailsBean(response.getFeeDetail()
					.getFees()));
			} else {
				error(getLocalizer().getString("error.manage.fees", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetchmanageFeeDetailsList ===> ", e);
		}
	}

	/**
	 * Fetch the Manage Fee Details List from service.
	 * 
	 * @return List<ManageFeeDetailsBean> Manage Fee Details bean for ManageFeeBean
	 */
	private void handleDeleteFees() {
		try {
			// Delete Fee Request.
			final DeleteFeeRequest request = this.mobBasePage.getNewMobiliserRequest(DeleteFeeRequest.class);
			final FeeConfig feeConfig = new FeeConfig();
			feeConfig.setUseCaseFee(ConverterUtils.convertToUseCaseFee(feeBean));
			feeConfig.getFees().addAll(ConverterUtils.convertToAddFeeConfig(feeBean));
			request.setFee(feeConfig);
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final DeleteFeeResponse response = this.mobBasePage.feeClient.deleteFee(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				getSession().info(getLocalizer().getString("delete.success", this));
			} else {
				getSession().error(getLocalizer().getString("error.delete.fees", this));
			}
		} catch (Exception e) {
			getSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while handleDeleteFees  ===> ", e);
		}
	}
}
