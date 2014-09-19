package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponCategoriesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponCategoriesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategory;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.cst.pages.BaseCstPage;

public class BaseCouponAdminPage extends BaseCstPage {

    public static final Logger LOG = LoggerFactory
	    .getLogger(BaseCouponAdminPage.class);

    public BaseCouponAdminPage() {
	super();
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	return entries;
    }

    public List<KeyValue<Long, String>> getAllCouponCategories() {

	List<KeyValue<Long, String>> couponCategories = new ArrayList<KeyValue<Long, String>>();

	try {
	    GetAllCouponCategoriesRequest request = getNewMobiliserRequest(GetAllCouponCategoriesRequest.class);
	    GetAllCouponCategoriesResponse response = wsCouponsClient
		    .getAllCouponCategories(request);
	    if (evaluateMobiliserResponse(response)) {
		for (CouponCategory category : response.getCategory()) {
		    couponCategories.add(new KeyValue<Long, String>(category
			    .getId(), category.getName()));
		}
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred while loading coupon categories", e);
	    error(getLocalizer().getString("load.coupon.category.error", this));

	}
	return couponCategories;
    }
}
