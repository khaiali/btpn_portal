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
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.CreateProductRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.CreateProductResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsRangeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsPage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the ManageProductsDetailsPanel for Consumers, Agents in bank portal and Child Agents, Sub agents in agent
 * portal.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsAddConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private BtpnMobiliserBasePage mobBasePage;

	private ManageProductsBean productsBean;

	private ListDataProvider<ManageProductsRangeBean> manageProductsDataProvider;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private WebMarkupContainer productsRangeContainer;

	private String currency;

	private String defaultPercent;

	private String defaultFixed;

	private static final Logger LOG = LoggerFactory.getLogger(ManageProductsAddConfirmPanel.class);

	public ManageProductsAddConfirmPanel(String id, BtpnMobiliserBasePage mobBasePage,
		final ManageProductsBean productsBean) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.productsBean = productsBean;
		currency = getLocalizer().getString("currency", this);
		defaultPercent = getLocalizer().getString("default.percentage", this);
		defaultFixed = getLocalizer().getString("default.fixed", this);
		constructPanel();
	}

	protected void constructPanel() {
		Form<ManageProductsAddConfirmPanel> form = new Form<ManageProductsAddConfirmPanel>("productDetailsForm",
			new CompoundPropertyModel<ManageProductsAddConfirmPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("productsBean.productName"));
		form.add(new Label("productsBean.productType.value"));
		form.add(new Label("productsBean.productGLCode.value"));
		form.add(new Label("productsBean.feeGLCode.value"));
		form.add(new Label("productsBean.roleName"));
		form.add(new Label("productsBean.roleDescription"));
		form.add(new AmountLabel("productsBean.minBalance"));
		form.add(new AmountLabel("productsBean.initialDeposit"));
		form.add(new AmountLabel("productsBean.adminFee"));

		// Add the Manage Products container
		productsRangeContainer = new WebMarkupContainer("productsRangeContainer");
		manageProductsRangeDataView(productsRangeContainer);
		productsRangeContainer.setOutputMarkupId(true);
		form.add(productsRangeContainer);
		// Add add Button
		form.add(addConfirmButton());
		// Add add Button
		form.add(addCancelButton());

		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addConfirmButton() {
		Button addButton = new Button("confirmBtn") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleAddProduct();
				setResponsePage(ManageProductsPage.class);
			}
		};
		return addButton;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("cancelBtn") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageProductsPage.class);
			}
		}.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageProductsRangeDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageProductsRangeBean> manageProductsRangeList = productsBean.getRangeList();
		// Create the Attachment View
		manageProductsDataProvider = new ListDataProvider<ManageProductsRangeBean>(manageProductsRangeList);
		final DataView<ManageProductsRangeBean> dataView = new ManageProductsRangeDataView(WICKET_ID_PAGEABLE,
			manageProductsDataProvider);
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageProductsDataView for Managing Products Range Details.
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageProductsRangeDataView extends DataView<ManageProductsRangeBean> {

		private static final long serialVersionUID = 1L;

		protected ManageProductsRangeDataView(String id, IDataProvider<ManageProductsRangeBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);

		}

		@Override
		protected void onBeforeRender() {
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<ManageProductsRangeBean> item) {

			final ManageProductsRangeBean entry = item.getModelObject();
			item.setModel(new CompoundPropertyModel<ManageProductsRangeBean>(entry));
			// Add the Min Range
			item.add(new AmountLabel("minRange"));
			// Add the Max Range
			item.add(new AmountLabel("maxRange"));

			if (BtpnConstants.FIXED_INTEREST_RADIO.equals(entry.getInterest())) {
				item.add(new Label("fixedInterest", currency + entry.getValue()));
				item.add(new Label("percentInterest", defaultPercent + " %"));
				entry.setFixedAmount(Long.valueOf(entry.getValue()));
				entry.setPercentageAmount("0.0");
			} else {
				item.add(new Label("fixedInterest", currency + defaultFixed));
				item.add(new Label("percentInterest", entry.getValue() + " %"));
				entry.setFixedAmount(0L);
				entry.setPercentageAmount(entry.getValue());
			}
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return internalGetDataProvider().size() != 0;

		}
	}

	/**
	 * This method handles the edit product
	 * 
	 * @author Vikram Gunda
	 */
	private void handleAddProduct() {
		try {
			final CreateProductRequest request = this.mobBasePage.getNewMobiliserRequest(CreateProductRequest.class);
			request.setProduct(ConverterUtils.convertToProduct(productsBean));
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			CreateProductResponse response = this.mobBasePage.getProductClient().createProduct(request);
			if (mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				mobBasePage.getWebSession().info(
					getLocalizer().getString("confirm.success", ManageProductsAddConfirmPanel.this));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			mobBasePage.getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Error Occured while updating Product ", e);
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
			message = getLocalizer().getString("error.create.products", this);
		}
		this.mobBasePage.getWebSession().error(message);
	}
}
