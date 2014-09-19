package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeLocation;
import com.sybase365.mobiliser.web.common.panels.CouponTypeLocationsPanel;

public class EditCouponTypeLocationsPage extends CouponTypeMenuGroup {

    private CouponTypeLocation couponTypeLocation;

    public EditCouponTypeLocationsPage(CouponTypeLocation couponTypeLocation) {
	super();
	this.couponTypeLocation = couponTypeLocation;
	add(new CouponTypeLocationsPanel("couponTypeLocationsPanel", this,
		this.couponTypeLocation));
    }

    @Override
    protected Class getActiveMenu() {
	return CouponTypeLocationsPage.class;
    }
}
