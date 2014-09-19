package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.List;

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
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.CreateFeeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.CreateFeeResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageFeeDetailsDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageFeePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage Fee Add Panel for bank portals. This panel consists of adding fee as fixed, slab and sharing.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeAddConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final String WICKET_ID_PAGEABLE = "pageable";

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

	private static final Logger LOG = LoggerFactory.getLogger(ManageFeeAddConfirmPanel.class);

	/**
	 * Constructor for this page.
	 * 
	 * @param id id for the panel
	 * @param mobBasePage mobBasePage for the panel
	 * @param feeBean feeBean for the panel
	 */

	public ManageFeeAddConfirmPanel(final String id, final BtpnMobiliserBasePage mobBasePage,
		final ManageFeeBean feeBean) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.feeBean = feeBean;
		isSlabFee = feeBean.getFeeType().equals(BtpnConstants.USECASE_SLAB_RADIO);
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		final Form<ManageFeeAddConfirmPanel> form = new Form<ManageFeeAddConfirmPanel>("feeAddConfirmForm",
			new CompoundPropertyModel<ManageFeeAddConfirmPanel>(this));
		// Add the Manage fee container
		final WebMarkupContainer productsContainer = new WebMarkupContainer("feeContainer");
		productsContainer.add(new Label("feeBean.useCaseName.value"));
		productsContainer.add(new Label("feeBean.productName.value"));
		productsContainer.add(new Label("feeBean.feeType"));
		productsContainer.add(new Label("feeBean.applyToPayee"));
		productsContainer.add(new AmountLabel("feeBean.transactionAmount"));

		final WebMarkupContainer container = new WebMarkupContainer("glCodeContainer");
		final CodeValue glCode = feeBean.getManageDetailsList().get(0).getGlCode();
		container.add(new Label("slab.glCode", glCode == null ? "" : glCode.getValue()));
		container.setVisible(isSlabFee);
		productsContainer.add(container);
		productsContainer.add(new FeedbackPanel("errorMessages"));
		manageFeeDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		productsContainer.add(addCreateButton());
		productsContainer.add(addCancelButton());
		form.add(productsContainer);
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Fee
	 */
	protected Button addCreateButton() {
		Button submitButton = new Button("btnCreate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleCreateFeeBean();
				setResponsePage(ManageFeePage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the Cancel button for the Manage Fee
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageFeePage.class);
			}
		}.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method adds the Data View for manage fee data provider.
	 */
	protected void manageFeeDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageFeeDetailsBean> manageLimitList = feeBean.getManageDetailsList();

		final ManageFeeDetailsDataProvider dataProvider = new ManageFeeDetailsDataProvider(
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
				final String displayTotalItemsText = ManageFeeAddConfirmPanel.this.getLocalizer().getString(
					"fee.totalitems.header", ManageFeeAddConfirmPanel.this);
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
	 * This is the ManageFeeDataView for Managing Fees.
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

			item.add(new Label("feeName", entry.getFeeName()).setVisible(!isSlabFee));
			item.add(new Label("glCode", entry.getGlCode() == null ? "" : entry.getGlCode().getValue())
				.setVisible(!isSlabFee));
			item.add(new AmountLabel("minValue").setVisible(isSlabFee));
			item.add(new AmountLabel("maxValue").setVisible(isSlabFee));
			item.add(new AmountLabel("fixedFee"));
			item.add(new Label("percentageFee", (entry.getPercentageFee() == null
					|| entry.getPercentageFee().equals("0") ? "0.0" : entry.getPercentageFee())
					+ " %"));
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
	 * This method handles the Creation of Fee Bean.
	 */
	private void handleCreateFeeBean() {
		try {
			final CreateFeeRequest request = this.mobBasePage.getNewMobiliserRequest(CreateFeeRequest.class);
			final FeeConfig feeConfig = new FeeConfig();
			feeConfig.setUseCaseFee(ConverterUtils.convertToUseCaseFee(feeBean));
			feeConfig.getFees().addAll(ConverterUtils.convertToAddFeeConfig(feeBean));
			request.setFee(feeConfig);
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final CreateFeeResponse response = this.mobBasePage.feeClient.createFee(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				this.mobBasePage.getWebSession().info(getLocalizer().getString("create.success", this));
				setResponsePage(ManageFeePage.class);
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			this.mobBasePage.getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while creating the fees  ===> ", e);
		}
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.create.fees", this);
		}
		this.mobBasePage.getWebSession().error(message);
	}
}
