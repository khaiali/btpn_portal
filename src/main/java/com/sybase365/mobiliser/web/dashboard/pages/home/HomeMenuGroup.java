package com.sybase365.mobiliser.web.dashboard.pages.home;

import java.util.LinkedList;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.dashboard.pages.BaseDashboardPage;
import com.sybase365.mobiliser.web.dashboard.pages.home.job.JobListPage;
import com.sybase365.mobiliser.web.dashboard.pages.home.preferences.PreferencesPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_LOGIN)
public class HomeMenuGroup extends BaseDashboardPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(HomeMenuGroup.class);

    public HomeMenuGroup() {
        super();
    }

    @Override
    protected void initOwnPageComponents() {
        super.initOwnPageComponents();
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {

    	LOG.debug("#HomeMenuGroup.buildLeftMenu()");

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	entries.add(new MenuEntry(
		getLocalizer().getString("dashboard.leftmenu.home", this),
		Constants.PRIV_DASHBOARD_LOGIN, DashboardHomePage.class));
	entries.add(new MenuEntry(
		getLocalizer().getString("dashboard.leftmenu.home.preferences", this),
		Constants.PRIV_DASHBOARD_PREFS, PreferencesPage.class));
	entries.add(new MenuEntry(
		getLocalizer().getString("dashboard.leftmenu.home.job", this),
		Constants.PRIV_DASHBOARD_JOBS, JobListPage.class));
	entries.add(new MenuEntry(
		getLocalizer().getString("dashboard.leftmenu.home.changepassword", this),
		Constants.PRIV_DASHBOARD_LOGIN, ChangePasswordPage.class));

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
	return false;
    }
}
