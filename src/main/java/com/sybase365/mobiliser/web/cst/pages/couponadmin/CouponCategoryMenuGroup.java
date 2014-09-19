package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.LinkedList;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.util.Constants;

public class CouponCategoryMenuGroup extends BaseCouponAdminPage {

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	entries.add(new MenuEntry("menu.coupon.category.main",
		Constants.PRIV_VIEW_CPN_CATEGORY,
		EditCouponCategoriesPage.class));
	entries.add(new MenuEntry("menu.coupon.category.description",
		Constants.PRIV_CPN_CTAEGORY_DESCRIPTION,
		CouponCategoriesDescriptionPage.class));
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
