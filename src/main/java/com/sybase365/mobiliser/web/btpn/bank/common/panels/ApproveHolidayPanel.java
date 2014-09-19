package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.FindPendingApprovalHolidayCalendarRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.FindPendingApprovalHolidayCalendarResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveHolidayBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ApprovalHolidayDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender.ApproveHolidayConfirmPage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ApproveHolidayPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveHolidayPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveHolidayPanel.class);

	protected BtpnMobiliserBasePage basePage;

	ApproveHolidayBean approveHolidayBean;

	private FeedbackPanel feedBack;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_DETAILS_LINK = "detailsLink";

	private static final String WICKET_ID_DETAILS_LINK_NAME = "detailsLinkName";

	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";

	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	private String approvalTotalItemString;

	private int approvalStartIndex = 0;

	private int approvalEndIndex = 0;

	private Label approvalHeader;

	private Label noRecordsLabel;

	private BtpnCustomPagingNavigator navigator;

	public ApproveHolidayPanel(String id, BtpnMobiliserBasePage basePage, ApproveHolidayBean bean) {
		super(id);
		this.basePage = basePage;
		this.approveHolidayBean = bean;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<ApproveHolidayPanel> form = new Form<ApproveHolidayPanel>("approveHolidayForm",
			new CompoundPropertyModel<ApproveHolidayPanel>(this));
		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		form.add(feedBack);

		String message = getLocalizer().getString("label.noDataFound", ApproveHolidayPanel.this);
		noRecordsLabel = new Label("emptyRecordsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setOutputMarkupPlaceholderTag(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		List<ApproveHolidayBean> holidayList = fetchPendingHolidayCalendarList();
		if (!PortalUtils.exists(holidayList)) {
			noRecordsLabel.setVisible(true);
		} else {
			noRecordsLabel.setVisible(false);
		}

		// Add the approval container
		final WebMarkupContainer approveHolidayContainer = new WebMarkupContainer("approveHolidayContainer");
		approveHolidayDataView(approveHolidayContainer);
		approveHolidayContainer.setOutputMarkupId(true);
		form.add(approveHolidayContainer);
		add(form);
	}

	/**
	 * This method adds the approveHolidayDataView for the holiday calendar, and also adds the sorting logic for data
	 * view
	 */
	protected void approveHolidayDataView(final WebMarkupContainer dataViewContainer) {
		// Create the approval View
		final ApprovalHolidayDataProvider approvalDataProvider = new ApprovalHolidayDataProvider("maker",
			fetchPendingHolidayCalendarList());

		final DataView<ApproveHolidayBean> dataView = new ApprovalDataView(WICKET_ID_PAGEABLE, approvalDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approvalDataProvider.size() != 0;

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
				final String displayTotalItemsText = ApproveHolidayPanel.this.getLocalizer().getString(
					"approval.totalitems.header", ApproveHolidayPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, approvalStartIndex,
					approvalEndIndex);
			}

		};
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approvalDataProvider.size() != 0;

			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new OrderByBorder("orderCreatedBy", "createdBy", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});
		dataViewContainer.add(new OrderByBorder("orderByDescription", "description", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.add(new OrderByBorder("orderByStatus", "status", approvalDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the approvalsDataView for approval Calendar.
	 * 
	 * @author Narasa Reddy
	 */
	private class ApprovalDataView extends DataView<ApproveHolidayBean> {

		private static final long serialVersionUID = 1L;

		protected ApprovalDataView(String id, IDataProvider<ApproveHolidayBean> dataProvider) {
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
		protected void populateItem(final Item<ApproveHolidayBean> item) {
			final ApproveHolidayBean entry = item.getModelObject();

			item.add(new Label("cretedBy", entry.getCreatedBy()));
			item.add(new Label("description", entry.getDescription()));
			item.add(new Label("status", entry.getStatus()));

			final AjaxLink<ApproveHolidayBean> detailsLisk = new AjaxLink<ApproveHolidayBean>(WICKET_ID_DETAILS_LINK,
				item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final ApproveHolidayBean approveBean = (ApproveHolidayBean) item.getModelObject();
					setResponsePage(new ApproveHolidayConfirmPage(approveBean));

				}
			};
			detailsLisk.add(new Label(WICKET_ID_DETAILS_LINK_NAME, "Details"));
			item.add(detailsLisk);

			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((ApprovalHolidayDataProvider) internalGetDataProvider()).size() != 0;

		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			approvalTotalItemString = new Integer(size).toString();
			if (size > 0) {
				approvalStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				approvalEndIndex = approvalStartIndex + getItemsPerPage() - 1;
				if (approvalEndIndex > size) {
					approvalEndIndex = size;
				}
			} else {
				approvalStartIndex = 0;
				approvalEndIndex = 0;
			}
		}
	}

	/**
	 * calling findPendingApprovalHolidayCalendar service from holiday calendar end point
	 */
	public List<ApproveHolidayBean> fetchPendingHolidayCalendarList() {
		List<ApproveHolidayBean> approvalList = new ArrayList<ApproveHolidayBean>();
		FindPendingApprovalHolidayCalendarResponse response = null;
		try {
			final FindPendingApprovalHolidayCalendarRequest request = basePage
				.getNewMobiliserRequest(FindPendingApprovalHolidayCalendarRequest.class);
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCheckerId(customerId);
			response = basePage.getHolidayCalendarClient().findPendingApprovalHolidayCalendar(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				return ConverterUtils.convertToApproveHolidayBean(response.getHolidays(), null);
			} else {
				error(response.getStatus().getValue());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling findPendingApprovalHolidayCalendar service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return approvalList;
	}

}
