package com.sybase365.mobiliser.web.distributor.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.MainMenu;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.application.model.AbstractMobiliserAuthenticatedApplication;
import com.sybase365.mobiliser.web.distributor.pages.agentcare.ActivateAgentPage;
import com.sybase365.mobiliser.web.distributor.pages.agentcare.AgentCreatePage;
import com.sybase365.mobiliser.web.distributor.pages.agentcare.AgentEditPage;
import com.sybase365.mobiliser.web.distributor.pages.agentcare.AgentFindPage;
import com.sybase365.mobiliser.web.distributor.pages.bulkprocessing.BulkHistoryPage;
import com.sybase365.mobiliser.web.distributor.pages.bulkprocessing.ConfirmFilePage;
import com.sybase365.mobiliser.web.distributor.pages.bulkprocessing.FileUploadPage;
import com.sybase365.mobiliser.web.distributor.pages.customerservices.AirTimeTopup;
import com.sybase365.mobiliser.web.distributor.pages.customerservices.RegisterCustomerPage;
import com.sybase365.mobiliser.web.distributor.pages.customerservices.SearchCustomerPage;
import com.sybase365.mobiliser.web.distributor.pages.reports.DefaultBatchReportPage;
import com.sybase365.mobiliser.web.distributor.pages.reports.DefaultDynamicReportPage;
import com.sybase365.mobiliser.web.distributor.pages.reports.DownloadReportPage;
import com.sybase365.mobiliser.web.distributor.pages.selfcare.SelfCareHomePage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class DistributorApplication extends
	AbstractMobiliserAuthenticatedApplication {

    @Override
    public void buildMenu(SybaseMenu menu, Roles roles) {

	MenuEntry mEntry = new MenuEntry("top.menu.selfcare",
		Constants.PRIV_MERCHANT_LOGIN, SelfCareHomePage.class);
	mEntry.setActive(true);
	menu.addMenuEntry(mEntry);

	final MainMenu custServices = new MainMenu(
		"top.menu.customer.services", Constants.PRIV_WALLET_SERVICES);
	custServices.addMenuEntry(new MenuEntry("menu.search.customer",
		Constants.PRIV_WALLET_SERVICES, SearchCustomerPage.class));
	custServices.addMenuEntry(new MenuEntry("menu.register.customer",
		Constants.PRIV_WALLET_SERVICES, RegisterCustomerPage.class));
	custServices.addMenuEntry(new MenuEntry("menu.airtime.topup",
		Constants.PRIV_MERCHANT_TRANSACTION, AirTimeTopup.class));

	if (isMenuVisible(custServices, roles)) {
	    menu.addMainMenu(custServices);
	}

	PageParameters params = new PageParameters();
	params.add("isFromTopMenu", "true");

	final MainMenu bulkProcessing = new MainMenu(
		"top.menu.bulk.processing", Constants.PRIV_WALLET_SERVICES);

	bulkProcessing.addMenuEntry(new MenuEntry("menu.bulk.upload",
		Constants.PRIV_MERCHANT_LOGIN, FileUploadPage.class));
	bulkProcessing.addMenuEntry(new MenuEntry("menu.bulk.confirm",
		Constants.PRIV_MERCHANT_LOGIN, ConfirmFilePage.class));
	bulkProcessing.addMenuEntry(new MenuEntry("menu.bulk.history",
		Constants.PRIV_MERCHANT_LOGIN, BulkHistoryPage.class));

	if (isMenuVisible(bulkProcessing, roles)) {
	    menu.addMainMenu(bulkProcessing);
	}

	final MainMenu manageAgents = new MainMenu("top.menu.manage.agents",
		Constants.PRIV_MANAGE_AGENTS);
	manageAgents.addMenuEntry(new MenuEntry("menu.agent.hierarchy",
		Constants.PRIV_MANAGE_AGENTS, AgentEditPage.class, params));
	manageAgents.addMenuEntry(new MenuEntry("menu.activate.agent",
		Constants.PRIV_ACTIVATE_DESCENDANTS, ActivateAgentPage.class));
	manageAgents.addMenuEntry(new MenuEntry("menu.find.agent",
		Constants.PRIV_MANAGE_AGENTS, AgentFindPage.class));
	manageAgents.addMenuEntry(new MenuEntry("menu.create.agent",
		Constants.PRIV_MANAGE_AGENTS, AgentCreatePage.class, params));

	if (isMenuVisible(manageAgents, roles)) {
	    menu.addMainMenu(manageAgents);
	}

	final MainMenu reports = new MainMenu("top.menu.reports",
		Constants.PRIV_DPP_REPORTS);
	reports.addMenuEntry(new MenuEntry("top.menu.reports.online",
		Constants.PRIV_DPP_REPORTS, DefaultDynamicReportPage.class));

	reports.addMenuEntry(new MenuEntry("top.menu.reports.batch",
		Constants.PRIV_DPP_REPORTS, DefaultBatchReportPage.class));

	reports.addMenuEntry(new MenuEntry("top.menu.reports.batch.download",
		Constants.PRIV_DPP_REPORTS, DownloadReportPage.class));

	if (isMenuVisible(manageAgents, roles)) {
	    menu.addMainMenu(reports);
	}

    }

}
