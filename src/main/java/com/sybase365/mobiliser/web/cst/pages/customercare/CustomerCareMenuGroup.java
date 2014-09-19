package com.sybase365.mobiliser.web.cst.pages.customercare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.cst.pages.customercare.alerts.ContactPointsPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.alerts.MobileAlertsPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CustomerCareMenuGroup extends BaseCustomerCarePage {

    public CustomerCareMenuGroup() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public CustomerCareMenuGroup(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	if (PortalUtils.exists(getMobiliserWebSession().getCustomer())
		&& !PortalUtils.exists(getMobiliserWebSession().getCustomer()
			.getTaskId())) {

	    entries.add(new MenuEntry("menu.standing.data",
		    Constants.PRIV_CUST_READ, StandingDataPage.class));
	    entries.add(new MenuEntry("menu.accounts",
		    Constants.PRIV_CUST_READ, ManageAccountPage.class));
	    entries.add(new MenuEntry("menu.history", Constants.PRIV_CUST_READ,
		    ShowHistoryPage.class));
	    entries.add(new MenuEntry("menu.contacts",
		    Constants.PRIV_NOTE_READ, ShowContactsPage.class));
	    entries.add(new MenuEntry("menu.transactions",
		    Constants.PRIV_TXN_READ, TransactionsPage.class));
	    entries.add(new MenuEntry("menu.agent.transactions",
		    Constants.PRIV_AGENT_TXN_READ, AgentTransactionsPage.class));
	    entries.add(new MenuEntry("menu.attachments",
		    Constants.PRIV_CUST_READ, AttachmentPage.class));
	    entries.add(new MenuEntry("menu.invoice.configurations",
		    Constants.PRIV_READ_INVOICE,
		    BillConfigurationListPage.class));
	    entries.add(new MenuEntry("menu.invoices",
		    Constants.PRIV_READ_INVOICE, OpenBillsPage.class));
	    entries.add(new MenuEntry("menu.invoice.history",
		    Constants.PRIV_READ_INVOICE, BillHistoryPage.class));
	    entries.add(new MenuEntry("menu.sms.traffic",
		    Constants.PRIV_SMS_TRAFFIC, SmsTrafficPage.class));

	    if (PortalUtils.exists(getMobiliserWebSession().getCustomer()
		    .getIsIndividualFeeSet())) {
		if (getMobiliserWebSession().getCustomer()
			.getIsIndividualFeeSet().booleanValue())
		    entries.add(new MenuEntry("menu.fee.configuration",
			    Constants.PRIV_CUST_READ,
			    IndividualFeeSetConfig.class));
	    }
	    if (PortalUtils.exists(getMobiliserWebSession().getCustomer()
		    .getIsIndividualLimitSet())) {
		if (getMobiliserWebSession().getCustomer()
			.getIsIndividualLimitSet().booleanValue())
		    entries.add(new MenuEntry("menu.limit.configuration",
			    Constants.PRIV_CST_LOGIN,
			    IndividualLimitSetConfig.class));
	    }
	    entries.add(new MenuEntry("menu.contactPoints",
		    Constants.PRIV_CST_LOGIN, ContactPointsPage.class));
	    entries.add(new MenuEntry("menu.mobileAlerts",
		    Constants.PRIV_CST_LOGIN, MobileAlertsPage.class));

	    if (getMobiliserWebSession().getCustomer().getCustomerTypeId()
		    .intValue() == Constants.MBANKING_CUSTOMER_TYPE) {
		entries.add(new MenuEntry("menu.service.packages",
			Constants.PRIV_CST_LOGIN,
			CustomerServicePackagePage.class));
	    }

	    for (IMenuEntry entry : entries) {
		if (entry instanceof MenuEntry) {
		    if (((MenuEntry) entry).getPage().equals(getActiveMenu())) {
			entry.setActive(true);
		    }
		}
	    }
	}

	return entries;
    }
}
