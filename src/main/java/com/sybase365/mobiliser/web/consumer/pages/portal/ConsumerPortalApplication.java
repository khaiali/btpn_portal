package com.sybase365.mobiliser.web.consumer.pages.portal;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.application.model.AbstractMobiliserAuthenticatedApplication;
import com.sybase365.mobiliser.web.consumer.pages.portal.billpayment.BillConfigurationListPage;
import com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts.ManageAccountPage;
import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.ConsumerHomePage;
import com.sybase365.mobiliser.web.consumer.pages.portal.transaction.ViewTransactionsPage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class ConsumerPortalApplication extends
	AbstractMobiliserAuthenticatedApplication {

    @Override
    public void buildMenu(SybaseMenu menu, Roles roles) {

	MenuEntry scMenuEntry = new MenuEntry("top.menu.consumer.selfcare",
		Constants.PRIV_CONSUMER_LOGIN, ConsumerHomePage.class);
	scMenuEntry.setActive(true);
	menu.addMenuEntry(scMenuEntry);

	MenuEntry maMenuEntry = new MenuEntry(
		"top.menu.consumer.manageaccounts",
		Constants.PRIV_MANAGE_ACCOUNTS, ManageAccountPage.class);
	maMenuEntry.setActive(false);
	menu.addMenuEntry(maMenuEntry);

	MenuEntry txnMenuEntry = new MenuEntry(
		"top.menu.consumer.transactions",
		Constants.PRIV_CONSUMER_TXN_HISTORY, ViewTransactionsPage.class);
	txnMenuEntry.setActive(false);
	menu.addMenuEntry(txnMenuEntry);

	MenuEntry bpMenuEntry = new MenuEntry("top.menu.consumer.billpayment",
		Constants.PRIV_BILL_PAYMENT, BillConfigurationListPage.class);
	bpMenuEntry.setActive(false);
	menu.addMenuEntry(bpMenuEntry);
    }

}
