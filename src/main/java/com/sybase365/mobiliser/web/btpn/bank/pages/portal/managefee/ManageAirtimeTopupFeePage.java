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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.GetAllVendorFeesRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.GetAllVendorFeesResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageAirtimeTopupFeeDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupFeePage extends BtpnBaseBankPortalSelfCarePage {

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_FEENAVIGATOR = "feeNavigator";

	private static final String WICKET_ID_FEETOTALITEMS = "feeHeader";

	private String feeTotalItemString;

	private int feeStartIndex = 0;

	private int feeEndIndex = 0;

	private Label feeHeader;

	private BtpnCustomPagingNavigator navigator;

	private static final Logger LOG = LoggerFactory.getLogger(ManageAirtimeTopupFeePage.class);

	/**
	 * Constructor for this page.
	 */
	public ManageAirtimeTopupFeePage() {
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
		Form<ManageAirtimeTopupFeePage> form = new Form<ManageAirtimeTopupFeePage>("feeForm",
			new CompoundPropertyModel<ManageAirtimeTopupFeePage>(this));
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
				setResponsePage(ManageAirtimeTopupAddFeePage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageLimitDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageAirtimeTopupFeeBean> manageLimitList = fetchmanageFeeList();

		ManageAirtimeTopupFeeDataProvider dataProvider = new ManageAirtimeTopupFeeDataProvider("telco", manageLimitList);

		final DataView<ManageAirtimeTopupFeeBean> dataView = new ManageLimitDataView(WICKET_ID_PAGEABLE, dataProvider);
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
				final String displayTotalItemsText = ManageAirtimeTopupFeePage.this.getLocalizer().getString(
					"fee.totalitems.header", ManageAirtimeTopupFeePage.this);
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

		dataViewContainer.add(new BtpnOrderByOrder("orderByTelco", "telco", dataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByDenomination", "billerCode", dataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageLimitDataView for Managing Limits.
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageLimitDataView extends DataView<ManageAirtimeTopupFeeBean> {

		private static final long serialVersionUID = 1L;

		protected ManageLimitDataView(String id, IDataProvider<ManageAirtimeTopupFeeBean> dataProvider) {
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
		protected void populateItem(final Item<ManageAirtimeTopupFeeBean> item) {

			final ManageAirtimeTopupFeeBean entry = item.getModelObject();
			// Add the File name
			item.add(new Label("telco", entry.getTelco() != null ? entry.getTelco().getId() : null));
			// Add the uploaded date
			item.add(new Label("denomination",
				entry.getDenomination() != null ? entry.getDenomination().getValue() : null));

			// Add the details Link
			final AjaxLink<ManageAirtimeTopupFeeBean> detailsLink = new AjaxLink<ManageAirtimeTopupFeeBean>(
				WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ManageAirtimeTopupFeeDetailsPage(
						(ManageAirtimeTopupFeeBean) item.getModelObject()));
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
	 * This is used to fetch the Manage Bill Payment Fee Bean List
	 * 
	 * @return List<ManageBillPaymentFeeBean> Bill Payment Fee Bean List
	 */
	private List<ManageAirtimeTopupFeeBean> fetchmanageFeeList() {
		final List<ManageAirtimeTopupFeeBean> feeList = new ArrayList<ManageAirtimeTopupFeeBean>();
		try {
			// Transaction GL Request
			final GetAllVendorFeesRequest request = this.getNewMobiliserRequest(GetAllVendorFeesRequest.class);
			request.setFeeType(BtpnConstants.USECASE_AIRTIME_FEE);
			final GetAllVendorFeesResponse response = this.feeClient.getAllVendorFees(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils.convertToManageAiritmeTopupFeeBean(response.getVendorFees());
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
