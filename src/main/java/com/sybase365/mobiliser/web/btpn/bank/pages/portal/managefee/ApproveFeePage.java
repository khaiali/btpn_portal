package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.FindPendingApprovalFeesRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.FindPendingApprovalFeesResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.FindPendingApprovalLimitsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.FindPendingApprovalLimitsResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ApproveFeeDataProvider;
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
public class ApproveFeePage extends BtpnBaseBankPortalSelfCarePage {

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_FEENAVIGATOR = "feeNavigator";

	private static final String WICKET_ID_FEETOTALITEMS = "feeHeader";

	private String feeTotalItemString;

	private int feeStartIndex = 0;

	private int feeEndIndex = 0;

	private Label feeHeader;

	private BtpnCustomPagingNavigator navigator;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveFeePage.class);

	/**
	 * Constructor for this page.
	 */
	public ApproveFeePage() {
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
		Form<ApproveFeePage> form = new Form<ApproveFeePage>("feeForm", new CompoundPropertyModel<ApproveFeePage>(this));
		// Add the Manage Products container
		WebMarkupContainer productsContainer = new WebMarkupContainer("feeContainer");
		productsContainer.add(new FeedbackPanel("errorMessages"));
		manageLimitDataView(productsContainer);
		productsContainer.setOutputMarkupId(true);
		productsContainer.setRenderBodyOnly(true);
		form.add(productsContainer);
		// Add add Button
		add(form);

	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageLimitDataView(final WebMarkupContainer dataViewContainer) {

		// Manage Fee List
		final List<ApproveFeeBean> manageLimitList = fetchmanageFeeList();
		// Manage Limit List
		fetchLimitList(manageLimitList);

		final ApproveFeeDataProvider dataProvider = new ApproveFeeDataProvider("requestDate", manageLimitList);

		final DataView<ApproveFeeBean> dataView = new ApproveFeeDataView(WICKET_ID_PAGEABLE, dataProvider);
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
		final IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ApproveFeePage.this.getLocalizer().getString(
					"fee.totalitems.header", ApproveFeePage.this);
				return String.format(displayTotalItemsText, feeTotalItemString, feeStartIndex, feeEndIndex);
			}

		};

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return manageLimitList.size() == 0;

			}
		}.setRenderBodyOnly(true));

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

		dataViewContainer.add(new BtpnOrderByOrder("orderByrequestDate", "requestDate", dataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageLimitDataView for Managing Limits.
	 * 
	 * @author Vikram Gunda
	 */
	private class ApproveFeeDataView extends DataView<ApproveFeeBean> {

		private static final long serialVersionUID = 1L;

		protected ApproveFeeDataView(String id, IDataProvider<ApproveFeeBean> dataProvider) {
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
		protected void populateItem(final Item<ApproveFeeBean> item) {

			final ApproveFeeBean entry = item.getModelObject();
			// Add the File name
			item.add(new Label("useCase", entry.getUseCaseName().getValue()));
			// Add the uploaded date
			item.add(new Label("productId", entry.getProductName() != null ? entry.getProductName().getId() : null));
			// Request date for fees
			final Date requestDate = entry.getRequestDate();
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss");
			// Add the fee Type
			item.add(new Label("feeDate", sdf.format(requestDate)));
			// Add the fee Type
			item.add(new Label("feeType", entry.getFeeType()));

			// Add the details Link
			final AjaxLink<ApproveFeeBean> detailsLink = new AjaxLink<ApproveFeeBean>(WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					setResponsePage(new ApproveFeeDetailsPage(item.getModelObject()));
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
	 * Fetch Approve Fee List from service
	 * 
	 * @return List<ApproveFeeBean> list of Approve Fee Beans
	 */
	private List<ApproveFeeBean> fetchmanageFeeList() {
		final List<ApproveFeeBean> feeList = new ArrayList<ApproveFeeBean>();
		try {
			// Approve Fee Bean Fees Request
			final FindPendingApprovalFeesRequest request = this
				.getNewMobiliserRequest(FindPendingApprovalFeesRequest.class);
			request.setCheckerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final FindPendingApprovalFeesResponse response = this.feeClient.findPendingApprvoalFees(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils.convertToApproveFeeBeanList(response.getFee());
			} else {
				error(getLocalizer().getString("error.approve.fees", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
		return feeList;
	}

	/**
	 * Fetch Limit List from service
	 * 
	 * @return List<ApproveFeeBean> list of Approve Fee Beans
	 */
	private void fetchLimitList(final List<ApproveFeeBean> limitList) {
		try {
			// Approve Fee Bean Fees Request
			final FindPendingApprovalLimitsRequest request = this
				.getNewMobiliserRequest(FindPendingApprovalLimitsRequest.class);
			request.setCheckerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final FindPendingApprovalLimitsResponse response = this.limitClient.findPendingApprovalLimits(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				ConverterUtils.convertToApproveLimit(response.getLimit(), limitList, this.getLookupMapUtility(), this);
			} else {
				error(getLocalizer().getString("error.approve.limit", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
	}
}
