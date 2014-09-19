package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeDescription;
import com.sybase365.mobiliser.web.common.panels.CouponTypeDescriptionPanel;

public class EditCouponTypeDescriptionPage extends CouponTypeMenuGroup {

    public EditCouponTypeDescriptionPage(
	    CouponTypeDescription couponTypeDescription) {
	super();
	add(new CouponTypeDescriptionPanel("couponTypeDescriptionPanel", this,
		couponTypeDescription));
    }

    @Override
    protected Class getActiveMenu() {
	return CouponTypeDescriptionPage.class;
    }
}
