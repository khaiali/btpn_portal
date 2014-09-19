package com.sybase365.mobiliser.web.cst.pages.usermanager;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class UserManagerMenuGroup extends BaseUserManagerPage {

    public UserManagerMenuGroup() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public UserManagerMenuGroup(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	if (PortalUtils.exists(getMobiliserWebSession().getCustomer())
		&& getMobiliserWebSession().getCustomer().isPendingApproval()) {
	    return entries;
	}

	entries.add(new MenuEntry("menu.usrmgr.data",
		Constants.PRIV_UMGR_EDIT_AGENT, EditAgentPage.class));
	entries.add(new MenuEntry("menu.usrmgr.changePassword",
		Constants.PRIV_UMGR_EDIT_AGENT, ChangePasswordPage.class));
	entries.add(new MenuEntry("menu.usrmgr.addRolesPriv",
		Constants.PRIV_UMGR_EDIT_AGENT,
		EditAgentRolePrivilegePage.class));

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