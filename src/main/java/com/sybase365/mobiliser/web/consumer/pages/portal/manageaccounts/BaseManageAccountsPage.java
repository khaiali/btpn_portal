package com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.consumer.pages.portal.BaseConsumerPortalPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_CONSUMER_TXN_HISTORY)
public class BaseManageAccountsPage extends BaseConsumerPortalPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseManageAccountsPage.class);

    public BaseManageAccountsPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseManageAccountsPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

	LOG.debug("#BaseManageAccountsPage.buildLeftMenu()");

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	// For Pending External Account
	if (PortalUtils.exists(getMobiliserWebSession().getCustomer())
		&& PortalUtils.exists(getMobiliserWebSession().getCustomer()
			.getTaskId())) {
	    return entries;
	}

	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.manageaccounts", this),
		Constants.PRIV_MANAGE_ACCOUNTS, ManageAccountPage.class));

	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.banklist", this),
		Constants.PRIV_BANK_ACCOUNT_LIST, BankListPage.class));

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