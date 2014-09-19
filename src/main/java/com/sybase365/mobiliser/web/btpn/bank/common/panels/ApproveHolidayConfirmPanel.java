package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.ContinuePendingHolidayCalendarRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.holidaycalendar.ContinuePendingHolidayCalendarResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveHolidayBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender.ApproveHolidayCalendarPage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ApproveHolidayConfirmPanel for bank portal portal.
 * 
 * @author Narasa Reddy
 */
public class ApproveHolidayConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveHolidayConfirmPanel.class);

	private BtpnMobiliserBasePage basePage;

	ApproveHolidayBean approveBean;

	public ApproveHolidayConfirmPanel(String id, BtpnMobiliserBasePage basePage, ApproveHolidayBean approveBean) {
		super(id);
		this.basePage = basePage;
		this.approveBean = approveBean;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<ApproveHolidayConfirmPanel> form = new Form<ApproveHolidayConfirmPanel>("approveHolidayConfirmForm",
			new CompoundPropertyModel<ApproveHolidayConfirmPanel>(this));

		form.add(new Label("approveBean.action"));
		form.add(new Label("approveBean.description"));
		form.add(new Label("approveBean.fromDate"));
		form.add(new Label("approveBean.toDate"));

		form.add(addApproveButton());
		form.add(addRejectButton());
		form.add(addBackButton());

		add(form);
	}

	/**
	 * This method is for adding Approve button to approveHoliday panel.
	 */
	protected Button addApproveButton() {
		Button approveButton = new Button("approveButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (PortalUtils.exists(approveBean)) {
					approveBean = continuePendingHolidayCalendar(true);
					if (approveBean.isApproveSuccess()) {
						basePage.getWebSession().info(
							getLocalizer().getString("approve.success", ApproveHolidayConfirmPanel.this));
					}
					setResponsePage(new ApproveHolidayCalendarPage(approveBean));
				}
			}
		};
		approveButton.setDefaultFormProcessing(true);
		return approveButton;
	}

	/**
	 * This method adds the reject button for the approveHoliday Panel.
	 */
	protected Button addRejectButton() {
		Button rejectButton = new Button("rejectButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (PortalUtils.exists(approveBean)) {
					approveBean = continuePendingHolidayCalendar(false);
					if (approveBean.isRejectSuccess()) {
						basePage.getWebSession().info(
							getLocalizer().getString("reject.success", ApproveHolidayConfirmPanel.this));
					}
					setResponsePage(new ApproveHolidayCalendarPage(approveBean));
				}
			};
		};
		rejectButton.setDefaultFormProcessing(false);
		return rejectButton;
	}

	/**
	 * This method adds the back button for the approveHoliday Panel.
	 */
	protected Button addBackButton() {
		Button backButton = new Button("backButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ApproveHolidayCalendarPage.class);
			};
		};
		backButton.setDefaultFormProcessing(false);
		return backButton;
	}

	/**
	 * calling continuePendingHolidayCalendar service from holiday calendar end point
	 */
	private ApproveHolidayBean continuePendingHolidayCalendar(boolean flag) {
		try {
			final ContinuePendingHolidayCalendarRequest request = basePage
				.getNewMobiliserRequest(ContinuePendingHolidayCalendarRequest.class);
			ContinuePendingHolidayCalendarResponse response = basePage.getHolidayCalendarClient()
				.continuePendingHolidayCalendar(populateHolidayCalendarRequest(request, flag));
			if (basePage.evaluateBankPortalMobiliserResponse(response)) {
				if (flag) {
					approveBean.setApproveSuccess(true);
				} else {
					approveBean.setRejectSuccess(true);
				}
			} else if (flag) {
				error(getLocalizer().getString("unable.approve.request", ApproveHolidayConfirmPanel.this));
			} else {
				error(getLocalizer().getString("unable.reject.request", ApproveHolidayConfirmPanel.this));
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling continuePendingHolidayCalendar service", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return approveBean;
	}

	private ContinuePendingHolidayCalendarRequest populateHolidayCalendarRequest(
		ContinuePendingHolidayCalendarRequest request, boolean flag) {
		request.setApprove(flag);
		request.setTaskId(approveBean.getTaskId());
		long checkerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
		request.setCheckerId(checkerId);
		return request;
	}
}
