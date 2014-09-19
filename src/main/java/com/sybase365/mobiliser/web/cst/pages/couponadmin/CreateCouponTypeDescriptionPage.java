package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import com.sybase365.mobiliser.web.common.panels.CouponTypeDescriptionPanel;

public class CreateCouponTypeDescriptionPage extends CouponTypeMenuGroup {
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new CouponTypeDescriptionPanel("couponTypeDescriptionPanel", this,
		null));
    }

    @Override
    protected Class getActiveMenu() {
	return CouponTypeDescriptionPage.class;
    }
}
