package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

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
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.GetAllFeesRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.GetAllFeesResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageFeePage extends BtpnBaseBankPortalSelfCarePage {

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_FEENAVIGATOR = "feeNavigator";

	private static final String WICKET_ID_FEETOTALITEMS = "feeHeader";

	private String feeTotalItemString;

	private int feeStartIndex = 0;

	private int feeEndIndex = 0;

	private Label feeHeader;

	private BtpnCustomPagingNavigator navigator;

	private static final Logger LOG = LoggerFactory.getLogger(ManageFeePage.class);

	/**
	 * Constructor for this page.
	 */
	public ManageFeePage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		constructPage();

	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		Form<ManageFeePage> form = new Form<ManageFeePage>("feeForm", new CompoundPropertyModel<ManageFeePage>(this));
		// Add the Manage Products container
		WebMarkupContainer productsContainer = new WebMarkupContainer("feeContainer");
		productsContainer.add(new FeedbackPanel("errorMessages"));
		manageLimitDataView(productsContainer);
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
				setResponsePage(ManageFeeAddPage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageLimitDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageFeeBean> manageLimitList = fetchmanageFeeList();

		final DataView<ManageFeeBean> dataView = new ManageLimitDataView(WICKET_ID_PAGEABLE,
			new ListDataProvider<ManageFeeBean>(manageLimitList));
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

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageLimitList.size() == 0;

			}
		}.setRenderBodyOnly(true));

		// Add the header
		final IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ManageFeePage.this.getLocalizer().getString(
					"fee.totalitems.header", ManageFeePage.this);
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

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageLimitDataView for Managing Limits.
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageLimitDataView extends DataView<ManageFeeBean> {

		private static final long serialVersionUID = 1L;

		protected ManageLimitDataView(String id, IDataProvider<ManageFeeBean> dataProvider) {
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
		protected void populateItem(final Item<ManageFeeBean> item) {

			final ManageFeeBean entry = item.getModelObject();
			// Add the File name
			item.add(new Label("useCase", entry.getUseCaseName().getValue()));
			// Add the uploaded date
			item.add(new Label("productId", entry.getProductName() != null ? entry.getProductName().getValue() : null));
			// Add the fee Type
			item.add(new Label("feeType", entry.getFeeType()));

			// Add the details Link
			final AjaxLink<ManageFeeBean> detailsLink = new AjaxLink<ManageFeeBean>(WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ManageFeeDetailsPage((ManageFeeBean) item.getModelObject()));
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
	 * This method returns the Fees from Manage Fee Bean List.
	 * 
	 * @return List<ManageFeeBean> ManageFeeBean list
	 */
	private List<ManageFeeBean> fetchmanageFeeList() {
		final List<ManageFeeBean> feeList = new ArrayList<ManageFeeBean>();
		try {
			// Transaction GL Request
			final GetAllFeesRequest request = this.getNewMobiliserRequest(GetAllFeesRequest.class);
			request.getFeeTypes().addAll(BtpnUtils.getFeesList());
			final GetAllFeesResponse response = this.feeClient.getAllFees(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils.convertToManageFeeBeanList(response.getFees());
			} else {
				error(getLocalizer().getString("error.manage.fees", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
		return feeList;
	}
}
