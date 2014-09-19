package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponType;
import com.sybase365.mobiliser.web.common.panels.CouponTypePanel;

public class EditCouponTypePage extends CouponTypeMenuGroup {

    private CouponType couponType;

    public EditCouponTypePage() {
	super();
	couponType = getMobiliserWebSession().getCouponType();
	initPageComponents();
    }

    public EditCouponTypePage(CouponType couponType) {
	super();
	this.couponType = couponType;
	initPageComponents();
    }

    private void initPageComponents() {
	add(new CouponTypePanel("couponTypePanel", this, this.couponType));
    }

    @Override
    protected Class getActiveMenu() {
	return EditCouponTypePage.class;
    }
}
