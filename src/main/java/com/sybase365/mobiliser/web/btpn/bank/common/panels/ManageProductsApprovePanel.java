package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.FindPendingProductsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.FindPendingProductsResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageProductsApproveProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsApproveConfirmPage;
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
public class ManageProductsApprovePanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ManageProductsApprovePanel.class);

	private BtpnMobiliserBasePage mobBasePage;

	private ManageProductsApproveProvider manageProductsApproveDataProvider;

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

	public ManageProductsApprovePanel(String id, BtpnMobiliserBasePage mobBasePage) {
		super(id);
		this.mobBasePage = mobBasePage;
		constructPanel();
	}

	protected void constructPanel() {

		Form<ManageProductsApprovePanel> form = new Form<ManageProductsApprovePanel>("productForm",
			new CompoundPropertyModel<ManageProductsApprovePanel>(this));
		// Add the Manage Products container
		WebMarkupContainer productsContainer = new WebMarkupContainer("productsContainer");
		productsContainer.add(new FeedbackPanel("errorMessages"));
		manageProductsDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		form.add(productsContainer);
		// Add add Button
		add(form);
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageProductsDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageProductsApproveBean> manageProductsList = fetchManageProductsList();
		// Create the Attachment View
		manageProductsApproveDataProvider = new ManageProductsApproveProvider("maker", manageProductsList);

		final DataView<ManageProductsApproveBean> dataView = new ManageProductsDataView(WICKET_ID_PAGEABLE,
			manageProductsApproveDataProvider);
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
				final String displayTotalItemsText = ManageProductsApprovePanel.this.getLocalizer().getString(
					"products.totalitems.header", ManageProductsApprovePanel.this);
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
		dataViewContainer
			.add(new BtpnOrderByOrder("orderByMaker", "maker", manageProductsApproveDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByName", "name", manageProductsApproveDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByProductType", "productType",
			manageProductsApproveDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByStatus", "status", manageProductsApproveDataProvider,
			dataView));
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageProductsDataView for Managing Products.
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageProductsDataView extends DataView<ManageProductsApproveBean> {

		private static final long serialVersionUID = 1L;

		protected ManageProductsDataView(String id, IDataProvider<ManageProductsApproveBean> dataProvider) {
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
		protected void populateItem(final Item<ManageProductsApproveBean> item) {

			final ManageProductsApproveBean entry = item.getModelObject();
			// Add the File name
			item.add(new Label("maker", entry.getMaker()));
			// Add the uploaded date
			item.add(new Label("productType", entry.getNewProductType().getValue()));
			// Add the uploaded date
			item.add(new Label("name", entry.getNewProductName()));
			// Add the status
			item.add(new Label("status", entry.getStatus()));
			// Add the details Link
			final AjaxLink<ManageProductsApproveBean> detailsLink = new AjaxLink<ManageProductsApproveBean>(
				WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ManageProductsApproveConfirmPage(item.getModelObject()));
				}
			};
			detailsLink.add(new Label(WICKET_ID_LINK_NAME, getLocalizer().getString("detailsLink",
				ManageProductsApprovePanel.this)));
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
	 * This method fetches the products that needs to be Approved/Rejected
	 * 
	 * @return List<ManageProductsApproveBean> manageProductsApproveBean
	 */
	private List<ManageProductsApproveBean> fetchManageProductsList() {
		List<ManageProductsApproveBean> list = new ArrayList<ManageProductsApproveBean>();
		try {

			final FindPendingProductsRequest request = this.mobBasePage
				.getNewMobiliserRequest(FindPendingProductsRequest.class);
			request.setCheckerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final FindPendingProductsResponse response = this.mobBasePage.getProductClient().findPendingProducts(
				request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				list = ConverterUtils.convertToManageProductsApproveBeanList(response.getProducts(),
					this.mobBasePage.getLookupMapUtility(), this);
			} else {
				error(getLocalizer().getString("error.pending", this));
			}
		} catch (Exception e) {
			error(this.mobBasePage.getLocalizer().getString("error.exception", this.mobBasePage));
			LOG.error("Exception occured while fetching updating GL Code Details  ===> ", e);
		}
		return list;
	}

}
