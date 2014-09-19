package com.sybase365.mobiliser.web.distributor.pages.selfcare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.distributor.pages.BaseDistributorPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_LOGIN)
public class SelfCareMenuGroup extends BaseDistributorPage {

    private static final long serialVersionUID = 1L;

    public SelfCareMenuGroup() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public SelfCareMenuGroup(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	entries.add(new MenuEntry("menu.home", Constants.PRIV_MERCHANT_LOGIN,
		SelfCareHomePage.class));
	entries.add(new MenuEntry("menu.agentData",
		Constants.PRIV_MERCHANT_LOGIN, AgentDataPage.class));
	entries.add(new MenuEntry("menu.changePassword",
		Constants.PRIV_MERCHANT_LOGIN, ChangePasswordPage.class));
	entries.add(new MenuEntry("menu.attachment", Constants.PRIV_ATTACHMENT,
		AttachmentPage.class));
	PageParameters params = new PageParameters();
	params.add("isFromTopMenu", "true");
	entries.add(new MenuEntry("menu.transaction.history",
		Constants.PRIV_MERCHANT_TXN_HISTORY,
		TransactionHistoryPage.class, params));

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
