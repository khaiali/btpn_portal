package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponBatch;
import com.sybase365.mobiliser.web.common.panels.CouponTypeBatchPanel;

public class EditCouponTypeBatchPage extends CouponTypeMenuGroup {

    private CouponBatch couponBatch;

    public EditCouponTypeBatchPage(CouponBatch couponBatch) {
	super();
	this.couponBatch = couponBatch;
	initPageComponents();
    }

    private void initPageComponents() {
	add(new CouponTypeBatchPanel("couponTypeBatchPanel", this,
		this.couponBatch));
    }

    @Override
    protected Class getActiveMenu() {
	return CouponTypeBatchesPage.class;
    }
}
