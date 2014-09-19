package com.sybase365.mobiliser.web.cst.pages.selfcare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.cst.pages.BaseCstPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class CstHomeMenuGroup extends BaseCstPage {
    private static final long serialVersionUID = 1L;

    public CstHomeMenuGroup() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public CstHomeMenuGroup(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	entries.add(new MenuEntry("menu.home", Constants.PRIV_CST_LOGIN,
		CstHomePage.class));
	entries.add(new MenuEntry("menu.myDetails", Constants.PRIV_CST_LOGIN,
		MyDetailsPage.class));
	entries.add(new MenuEntry("menu.myPrivileges",
		Constants.PRIV_CST_LOGIN, MyPrivilegesPage.class));
	entries.add(new MenuEntry("menu.changePassword",
		Constants.PRIV_CRED_CHANGE, ChangePasswordPage.class));

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
