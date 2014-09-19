package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.FindProductsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.FindProductsResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageProductsDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsAddPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsDetailsPage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Confirm Registration Panel for Consumers, Agents in bank portal and Child Agents, Sub agents in agent
 * portal.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private BtpnMobiliserBasePage mobBasePage;

	private ManageProductsDataProvider manageProductsDataProvider;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_LINK_NAME = "linkName";

	private static final String WICKET_ID_PRODUCTSNAVIGATOR = "productsNavigator";

	private static final String WICKET_ID_PRODUCTSTOTALITEMS = "productsHeader";

	private String productsTotalItemString;

	private int productsStartIndex = 0;

	private int productsEndIndex = 0;

	private Label productsHeader;

	private BtpnCustomPagingNavigator navigator;

	private static final Logger LOG = LoggerFactory.getLogger(ManageProductsPanel.class);

	public ManageProductsPanel(String id, BtpnMobiliserBasePage mobBasePage) {
		super(id);
		this.mobBasePage = mobBasePage;
		constructPanel();
	}

	protected void constructPanel() {

		Form<ManageProductsPanel> form = new Form<ManageProductsPanel>("productForm",
			new CompoundPropertyModel<ManageProductsPanel>(this));
		// Add the Manage Products container
		WebMarkupContainer productsContainer = new WebMarkupContainer("productsContainer");
		productsContainer.add(new FeedbackPanel("errorMessages"));
		manageProductsDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		productsContainer.add(addAddButton());
		form.add(productsContainer);
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addAddButton() {
		Button submitButton = new Button("btnAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageProductsAddPage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageProductsDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageProductsBean> manageProductsList = fetchManageProductsList();
		// Create the Attachment View
		manageProductsDataProvider = new ManageProductsDataProvider("productId", manageProductsList);

		final DataView<ManageProductsBean> dataView = new ManageProductsDataView(WICKET_ID_PAGEABLE,
			manageProductsDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_PRODUCTSNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageProductsList.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		// No Products available
		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageProductsList.size() == 0;

			}
		}.setRenderBodyOnly(true));

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ManageProductsPanel.this.getLocalizer().getString(
					"products.totalitems.header", ManageProductsPanel.this);
				return String.format(displayTotalItemsText, productsTotalItemString, productsStartIndex,
					productsEndIndex);
			}

		};
		productsHeader = new Label(WICKET_ID_PRODUCTSTOTALITEMS, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageProductsList.size() != 0;

			}
		};
		dataViewContainer.add(productsHeader);
		productsHeader.setOutputMarkupId(true);
		productsHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByProductId", "productId", manageProductsDataProvider,
			dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByName", "productName", manageProductsDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByType", "productType", manageProductsDataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageProductsDataView for Managing Products.
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageProductsDataView extends DataView<ManageProductsBean> {

		private static final long serialVersionUID = 1L;

		protected ManageProductsDataView(String id, IDataProvider<ManageProductsBean> dataProvider) {
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
		protected void populateItem(final Item<ManageProductsBean> item) {

			final ManageProductsBean entry = item.getModelObject();
			// Add the File name
			item.add(new Label("productId", String.valueOf(entry.getProductId())));
			// Add the uploaded date
			item.add(new Label("name", entry.getProductName()));
			// Add the uploaded date
			item.add(new Label("type", entry.getProductType().getValue()));
			// Add the details Link
			final AjaxLink<ManageProductsBean> detailsLink = new AjaxLink<ManageProductsBean>(WICKET_ID_LINK,
				item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ManageProductsDetailsPage((ManageProductsBean) item.getModelObject()));
				}
			};
			detailsLink.add(new Label(WICKET_ID_LINK_NAME, getLocalizer().getString("detailsLink",
				ManageProductsPanel.this)));
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
			productsTotalItemString = new Integer(size).toString();
			if (size > 0) {
				productsStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				productsEndIndex = productsStartIndex + getItemsPerPage() - 1;
				if (productsEndIndex > size) {
					productsEndIndex = size;
				}
			} else {
				productsStartIndex = 0;
				productsEndIndex = 0;
			}
		}
	}

	/**
	 * This is the method fetches the product details
	 * 
	 * @author Vikram Gunda
	 */
	private List<ManageProductsBean> fetchManageProductsList() {
		try {
			final FindProductsRequest request = this.mobBasePage.getNewMobiliserRequest(FindProductsRequest.class);
			final FindProductsResponse response = this.mobBasePage.getProductClient().findProducts(request);
			if (mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils
					.convertToManageProductsBeanList(response, mobBasePage.getLookupMapUtility(), this);
			} else {
				error(getLocalizer().getString("error.products", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("exception.products", this));
			LOG.error("Error Occured while fetching Products List", e);

		}
		return new ArrayList<ManageProductsBean>();
	}
}
