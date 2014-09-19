package com.sybase365.mobiliser.web.dashboard.pages.servers;

import java.util.LinkedList;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.dashboard.pages.BaseDashboardPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class ServersMenuGroup extends BaseDashboardPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ServersMenuGroup.class);

    public ServersMenuGroup() {
	super();
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

	LOG.debug("#ServersMenuGroup.buildLeftMenu()");

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	entries.add(new MenuEntry(getLocalizer().getString(
		"dashboard.leftmenu.servers.list", this),
		Constants.PRIV_DASHBOARD_SERVERS, ServerListPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"dashboard.leftmenu.servers.information", this),
		Constants.PRIV_DASHBOARD_SERVERS, InformationPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"dashboard.leftmenu.servers.services", this),
		Constants.PRIV_DASHBOARD_SERVERS, ServicesPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"dashboard.leftmenu.servers.data", this),
		Constants.PRIV_DASHBOARD_SERVERS, DataPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"dashboard.leftmenu.servers.channels", this),
		Constants.PRIV_DASHBOARD_SERVERS, ChannelsPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"dashboard.leftmenu.servers.events", this),
		Constants.PRIV_DASHBOARD_SERVERS, EventsPage.class));
	entries.add(new MenuEntry(getLocalizer().getString(
		"dashboard.leftmenu.servers.tasks", this),
		Constants.PRIV_DASHBOARD_SERVERS, TasksPage.class));
	// entries.add(new MenuEntry(
	// getLocalizer().getString("dashboard.leftmenu.servers.logs", this),
	// Constants.PRIV_DASHBOARD_SERVERS, LogsPage.class));

	for (IMenuEntry entry : entries) {
	    if (entry instanceof MenuEntry) {
		if (((MenuEntry) entry).getPage().equals(getActiveMenu())) {
		    entry.setActive(true);
		}
	    }
	}

	return entries;
    }

    @Override
    protected boolean isServerContextVisible() {
	return true;
    }

    @Override
    protected boolean isTrackerContextVisible() {
	return false;
    }

}
