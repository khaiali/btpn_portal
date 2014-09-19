package com.sybase365.mobiliser.web.consumer.pages.portal.billpayment;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.*;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.consumer.pages.portal.BaseConsumerPortalPage;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.ConsumerHomePage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CONSUMER_LOGIN)
public class BaseBillPaymentPage extends BaseConsumerPortalPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BaseBillPaymentPage.class);

    public BaseBillPaymentPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public BaseBillPaymentPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

	LOG.debug("#ConsumerPortalLeftMenuGroup.buildLeftMenu()");

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.billconfiguration", this),
		Constants.PRIV_BILL_PAYMENT, BillConfigurationListPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.openbills", this),
		Constants.PRIV_BILL_PAYMENT, OpenBillsPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.paybill", this),
		Constants.PRIV_BILL_PAYMENT, PayBillPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"consumer.portal.leftmenu.billhistory", this),
		Constants.PRIV_BILL_PAYMENT, BillHistoryPage.class));

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