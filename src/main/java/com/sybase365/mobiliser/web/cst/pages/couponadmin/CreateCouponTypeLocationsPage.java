package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import com.sybase365.mobiliser.web.common.panels.CouponTypeLocationsPanel;

public class CreateCouponTypeLocationsPage extends CouponTypeMenuGroup {
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new CouponTypeLocationsPanel("couponTypeLocationsPanel", this, null));
    }

    @Override
    protected Class getActiveMenu() {
	return CouponTypeLocationsPage.class;
    }
}
