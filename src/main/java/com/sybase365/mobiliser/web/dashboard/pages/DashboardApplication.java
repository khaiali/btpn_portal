package com.sybase365.mobiliser.web.dashboard.pages;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.application.model.AbstractMobiliserAuthenticatedApplication;
import com.sybase365.mobiliser.web.dashboard.pages.home.DashboardHomePage;
import com.sybase365.mobiliser.web.dashboard.pages.servers.ServerListPage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.AllTrackersPage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class DashboardApplication extends
	AbstractMobiliserAuthenticatedApplication {

    @Override
    public void buildMenu(SybaseMenu menu, Roles roles) {

	MenuEntry dashHomeMenuEntry = new MenuEntry("top.menu.dashboard.home",
		Constants.PRIV_DASHBOARD_LOGIN, DashboardHomePage.class);
	dashHomeMenuEntry.setActive(true);

	menu.addMenuEntry(dashHomeMenuEntry);
	menu.addMenuEntry(new MenuEntry("top.menu.dashboard.servers",
		Constants.PRIV_DASHBOARD_SERVERS, ServerListPage.class));
	menu.addMenuEntry(new MenuEntry("top.menu.dashboard.trackers",
		Constants.PRIV_DASHBOARD_TRACKERS, AllTrackersPage.class));
    }

}
