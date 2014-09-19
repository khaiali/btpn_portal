package com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender;

import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveHolidayBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveHolidayConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage ApproveHolidayConfirmPage for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveHolidayConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	ApproveHolidayBean approveBean;

	public ApproveHolidayConfirmPage() {
		super();
		initPageComponents();
	}

	public ApproveHolidayConfirmPage(ApproveHolidayBean approveBean) {
		super();
		this.approveBean = approveBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ApproveHolidayConfirmPanel("approveHolidayConfirmPanel", this, approveBean));
	}
}
