package com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveHolidayBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveHolidayPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ApproveHolidayCalendarPage for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveHolidayCalendarPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnMobiliserBasePage basePage;

	ApproveHolidayBean approveHolidayBean;

	public ApproveHolidayCalendarPage() {
		super();
		initPageComponents();
	}

	public ApproveHolidayCalendarPage(ApproveHolidayBean bean) {
		super();
		this.approveHolidayBean = bean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ApproveHolidayPanel("approveHolidayCalendarPanel", this, approveHolidayBean));
	}

}
