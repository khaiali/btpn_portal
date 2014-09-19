package com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender;

import com.sybase365.mobiliser.web.btpn.bank.beans.HolidayListBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.HolidayCalenderPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * @author Sreenivasulu
 */
public class HolidayCalender extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	String type = "maker";

	HolidayListBean holidayListBean;

	public HolidayCalender() {
		super();
		initPageComponents();
	}

	public HolidayCalender(HolidayListBean bean) {
		super();
		this.holidayListBean = bean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new HolidayCalenderPanel("holidayCalenderPanel", holidayListBean, type, this));
	}

}
