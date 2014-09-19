package com.sybase365.mobiliser.web.dashboard.pages.trackers;

import java.util.LinkedList;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.dashboard.pages.BaseDashboardPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_TRACKERS)
public class TrackersMenuGroup extends BaseDashboardPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(TrackersMenuGroup.class);

    public TrackersMenuGroup() {
        super();
    }

    @Override
    protected void initOwnPageComponents() {
        super.initOwnPageComponents();
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

    	LOG.debug("#TrackersMenuGroup.buildLeftMenu()");

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	entries.add(new MenuEntry(
		getLocalizer().getString("dashboard.leftmenu.trackers.all", this),
		Constants.PRIV_DASHBOARD_TRACKERS, AllTrackersPage.class));
	entries.add(new MenuEntry(
		getLocalizer().getString("dashboard.leftmenu.trackers.view", this),
		Constants.PRIV_DASHBOARD_TRACKERS, ViewTrackerPage.class));

	// TODO - add these pages in later, if necessary and when required
	//
	//entries.add(new MenuEntry(
	//	getLocalizer().getString("dashboard.leftmenu.trackers.consolidate", this),
	//	Constants.PRIV_DASHBOARD_TRACKERS, ConsolidatePage.class));
	//entries.add(new MenuEntry(
	//	getLocalizer().getString("dashboard.leftmenu.trackers.separate", this),
	//	Constants.PRIV_DASHBOARD_TRACKERS, SeparatePage.class));
	//entries.add(new MenuEntry(
	//	getLocalizer().getString("dashboard.leftmenu.trackers.history", this),
	//	Constants.PRIV_DASHBOARD_TRACKERS, HistoryPage.class));

	for (IMenuEntry entry : entries) {
	    if (entry instanceof MenuEntry) {
		if (((MenuEntry)entry).getPage().equals(getActiveMenu())) {
			entry.setActive(true);
		}
	    }
	}

	return entries;
    }

    @Override
    protected boolean isServerContextVisible() {
	return false;
    }

    @Override
    protected boolean isTrackerContextVisible() {
	return true;
    }
}
