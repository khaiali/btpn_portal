package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.io.Streams;

import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteAttachmentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteAttachmentResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CustomerServicesMenuGroup extends BaseCustomerServicesPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CustomerServicesMenuGroup.class);

    public CustomerServicesMenuGroup() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public CustomerServicesMenuGroup(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	CustomerBean customer = getMobiliserWebSession().getCustomer();
	if (PortalUtils.exists(customer)) {
	    entries.add(new MenuEntry("menu.customer.details",
		    Constants.PRIV_WALLET_SERVICES, CustomerDetailsPage.class));
	    entries.add(new MenuEntry("submenu.cashin",
		    Constants.PRIV_MERCHANT_TRANSACTION, CashInPage.class));
	    entries.add(new MenuEntry("submenu.cashout",
		    Constants.PRIV_MERCHANT_TRANSACTION, CashOutPage.class));
	    entries.add(new MenuEntry("submenu.remittance",
		    Constants.PRIV_MERCHANT_TRANSACTION, RemittancePage.class));
	    entries.add(new MenuEntry("submenu.pickup",
		    Constants.PRIV_MERCHANT_TRANSACTION, PickUpPage.class));
	    entries.add(new MenuEntry("submenu.txnHistory",
		    Constants.PRIV_MERCHANT_TXN_HISTORY, ViewTransactionsPage.class));
	    entries.add(new MenuEntry("submenu.kycUpgrade",
		    Constants.PRIV_WALLET_SERVICES, KYCUpgradePage.class));
	}

	for (IMenuEntry entry : entries) {
	    if (entry instanceof MenuEntry) {
		if (((MenuEntry)entry).getPage().equals(getActiveMenu())) {
			entry.setActive(true);
		}
	    }
	}

	return entries;
    }

}
