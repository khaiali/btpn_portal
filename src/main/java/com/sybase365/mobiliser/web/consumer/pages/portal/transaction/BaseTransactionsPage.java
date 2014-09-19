package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.consumer.pages.portal.BaseConsumerPortalPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CONSUMER_TXN_HISTORY)
public class BaseTransactionsPage extends BaseConsumerPortalPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseTransactionsPage.class);

    public BaseTransactionsPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseTransactionsPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

	LOG.debug("#BaseTransactionsPage.buildLeftMenu()");

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.viewtransaction", this),
		Constants.PRIV_CONSUMER_TXN_HISTORY, ViewTransactionsPage.class));

	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.sendmoney", this),
		Constants.PRIV_SEND_MONEY, SendMoneyPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.sendmoneybank", this),
		Constants.PRIV_SEND_MONEY_BANK, SendMoneyBankPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.requestmoney", this),
		Constants.PRIV_REQUEST_MONEY, RequestMoneyPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.airtimetopup", this),
		Constants.PRIV_AIRTIME_TOPUP, AirTimeTopupPage.class));

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