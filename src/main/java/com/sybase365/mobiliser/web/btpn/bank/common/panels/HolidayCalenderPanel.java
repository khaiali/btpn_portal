package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.DeleteHolidayRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.DeleteHolidayResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.GetAllHolidaysRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.GetAllHolidaysResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.HolidayListBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.HolidayCalenderDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender.AddHolidayCalender;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Sreenivasulu
 */
public class HolidayCalenderPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(HolidayCalenderPanel.class);

	protected BtpnMobiliserBasePage basePage;

	private FeedbackPanel feedBack;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";

	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	private String approvalTotalItemString;

	private int approvalStartIndex = 0;

	private int approvalEndIndex = 0;

	private Label approvalHeader;

	private Label noRecordsLabel;

	private BtpnCustomPagingNavigator navigator;

	HolidayListBean holidayListBean;

	String type;

	public HolidayCalenderPanel(String id) {
		super(id);
	}

	public HolidayCalenderPanel(String id, HolidayListBean holidayListBean, String type, BtpnMobiliserBasePage basePage) {
		super(id);
		this.type = type;
		this.basePage = basePage;
		this.holidayListBean = holidayListBean;
		LOG.debug("calling construct panel..");
		constructPanel();
	}

	public void constructPanel() {
		Form<HolidayCalenderPanel> form = new Form<HolidayCalenderPanel>("holidayPanel",
			new CompoundPropertyModel<HolidayCalenderPanel>(HolidayCalenderPanel.class));

		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);
		if (PortalUtils.exists(holidayListBean)) {
			if (holidayListBean.isAddHolidaysuccess()) {
				String successMessage = getLocalizer().getString("addHoliday.success", this);
				basePage.getMobiliserWebSession().info(successMessage);
			}
		}

		String message = getLocalizer().getString("label.noDataFound", HolidayCalenderPanel.this);
		noRecordsLabel = new Label("emptyRecordsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setOutputMarkupPlaceholderTag(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		List<HolidayListBean> holidayList = getHolidayList();
		if (!PortalUtils.exists(holidayList)) {
			noRecordsLabel.setVisible(true);
		} else {
			noRecordsLabel.setVisible(false);
		}

		// Add the approval container
		final WebMarkupContainer holidayListContainer = new WebMarkupContainer("holidayListContainer");
		createHolidayCalenderList(holidayListContainer);
		holidayListContainer.setOutputMarkupId(true);
		form.add(holidayListContainer);
		AjaxButton addButton = new AjaxButton("addButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(AddHolidayCalender.class);

			};
		};
		form.add(addButton);
		add(form);
	}

	protected void createHolidayCalenderList(final WebMarkupContainer dataViewContainer) {
		// Create the approval View
		final HolidayCalenderDataProvider holidayCalenderDataProvider = new HolidayCalenderDataProvider("fromDate",
			getHolidayList());

		final DataView<HolidayListBean> dataView = new HolidayCalenderDataView(WICKET_ID_PAGEABLE,
			holidayCalenderDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return holidayCalenderDataProvider.size() != 0;

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
				final String displayTotalItemsText = HolidayCalenderPanel.this.getLocalizer().getString(
					"approval.totalitems.header", HolidayCalenderPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, approvalStartIndex,
					approvalEndIndex);
			}

		};
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return holidayCalenderDataProvider.size() != 0;

			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new OrderByBorder("orderByFromDate", "fromDate", holidayCalenderDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});
		dataViewContainer.add(new OrderByBorder("orderBytoDate", "toDate", holidayCalenderDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.add(new OrderByBorder("orderByDescription", "description", holidayCalenderDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.addOrReplace(dataView);
	}

	private class HolidayCalenderDataView extends DataView<HolidayListBean> {

		private static final long serialVersionUID = 1L;

		private static final String WICKET_ID_LINK_NAME = "deleteLinkName";

		protected HolidayCalenderDataView(String id, IDataProvider<HolidayListBean> dataProvider) {
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
		protected void populateItem(final Item<HolidayListBean> item) {
			final HolidayListBean entry = item.getModelObject();

			item.add(new Label("fromDate", entry.getFromDate()));
			item.add(new Label("toDate", entry.getToDate()));
			item.add(new Label("description", entry.getDescription()));

			// Add the delete Link
			AjaxLink<HolidayListBean> deleteLink = new AjaxLink<HolidayListBean>("deleteLink", item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					HolidayListBean holidayBean = (HolidayListBean) item.getModelObject();
					long holidayId = holidayBean.getHolidayId();
					LOG.debug("Selected Holiday ID :" + holidayId);
					if (deleteHilidayCalendar(holidayId)) {
						String successMessage = getLocalizer().getString("addHoliday.success", this);
						basePage.getMobiliserWebSession().info(successMessage);
					}
					target.addComponent(feedBack);
				}
			};
			deleteLink.add(new Label(WICKET_ID_LINK_NAME, getLocalizer().getString("delete", this)));
			item.add(deleteLink);

			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((HolidayCalenderDataProvider) internalGetDataProvider()).size() != 0;

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
	 * This method fetches the list of holiday calendar List.
	 * 
	 * @return List<HolidayListBean> returns the list of HolidayListBean beans
	 */
	private List<HolidayListBean> getHolidayList() {
		final List<HolidayListBean> holidayList = new ArrayList<HolidayListBean>();
		try {
			final GetAllHolidaysRequest request = basePage.getNewMobiliserRequest(GetAllHolidaysRequest.class);
			GetAllHolidaysResponse response = basePage.getHolidayCalendarClient().getAllHolidays(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				return ConverterUtils.convertToHolidayListBean(response.getHolidays(), null);
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling getAllHolidays service.", ex);
		}
		return holidayList;
	}

	/**
	 * calling deleteHoliday service from holiday calendar end point
	 * 
	 * @param holidayId
	 * @return
	 */
	private boolean deleteHilidayCalendar(long holidayId) {
		boolean removeHoliday = false;
		try {
			final DeleteHolidayRequest request = basePage.getNewMobiliserRequest(DeleteHolidayRequest.class);
			long makerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setMakerId(makerId);
			request.setHolidayId(holidayId);
			DeleteHolidayResponse response = basePage.getHolidayCalendarClient().deleteHoliday(request);
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				removeHoliday = true;
			} else {
				error("Failed to submit Holiday for Approval.");
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling deleteHoliday service", ex);
		}
		return removeHoliday;
	}

}
