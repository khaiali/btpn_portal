package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import com.sybase365.mobiliser.web.common.panels.CouponTypePanel;

public class CreateCouponTypePage extends BaseCouponAdminPage {

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new CouponTypePanel("couponTypePanel", this, null));
    }
}
