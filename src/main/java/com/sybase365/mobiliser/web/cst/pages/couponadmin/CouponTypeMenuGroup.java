package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.LinkedList;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.util.Constants;

public class CouponTypeMenuGroup extends BaseCouponAdminPage {

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	entries.add(new MenuEntry("menu.edit.coupon.type",
		Constants.PRIV_VIEW_COUPON_TYPES, EditCouponTypePage.class));
	entries.add(new MenuEntry("menu.description.coupon.type",
		Constants.PRIV_CPN_DESCRIPTION, CouponTypeDescriptionPage.class));
	entries.add(new MenuEntry("menu.coupon.type.keyword",
		Constants.PRIV_CPN_TAG, CouponTypeKeywordsPage.class));
	entries.add(new MenuEntry("menu.coupon.type.categories",
		Constants.PRIV_CPN_CATEGORY, CouponTypeCategoriesPage.class));
	entries.add(new MenuEntry("menu.coupon.type.generate",
		Constants.PRIV_CPN_GENERATE_BATCH,
		GenerateCouponBatchPage.class));
	entries.add(new MenuEntry("menu.coupon.type.batches",
		Constants.PRIV_CPN_BATCH, CouponTypeBatchesPage.class));
	entries.add(new MenuEntry("menu.coupon.type.locations",
		Constants.PRIV_CPN_LOCATION, CouponTypeLocationsPage.class));
	entries.add(new MenuEntry("menu.coupon.type.assign",
		Constants.PRIV_CPN_ASSIGN, CouponTypeAssignPage.class));
	for (IMenuEntry entry : entries) {
	    if (entry instanceof MenuEntry) {
		if (((MenuEntry) entry).getPage().equals(getActiveMenu())) {
		    entry.setActive(true);
		}
	    }
	}
	return entries;
    }

}
