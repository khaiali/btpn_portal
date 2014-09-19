package com.sybase365.mobiliser.web.application.model;

import java.util.Map;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MainMenu;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SubMenu;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;

/**
 * 
 * @author msw
 */
public abstract class AbstractMobiliserApplication implements
	IMobiliserApplication {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AbstractMobiliserApplication.class);

    private Map<String, Class> bookmarkablePages;

    @Override
    public Map<String, Class> getBookmarkablePages() {
	return this.bookmarkablePages;
    }

    public void setBookmarkablePages(Map<String, Class> value) {
	this.bookmarkablePages = value;
    }

    private Class homePage;

    @Override
    public Class getHomePage() {
	return this.homePage;
    }

    public void setHomePage(Class value) {
	this.homePage = value;
    }

    @Override
    public abstract void buildMenu(SybaseMenu menu, Roles roles);

    /**
     * Return true if the list of roles allows access to at least one of the
     * menu items in the MainMenu, including SubMenu's
     * 
     * @param menu
     * @param roles
     * @return
     */
    protected boolean isMenuVisible(MainMenu menu, Roles roles) {
	for (IMenuEntry menuEntry : menu.getChildren()) {
	    if (menuEntry.isSubmenu()) {
		if (isSubMenuVisible((SubMenu) menuEntry, roles)) {
		    if (LOG.isTraceEnabled()) {
			LOG.trace(
				"Show main menu: {} because sub menu is visible",
				menu.getName());
		    }
		    return true;
		}
	    } else {
		if (roles.hasRole(menuEntry.getRequiredPrivilege())) {
		    if (LOG.isTraceEnabled()) {
			LOG.trace(
				"Show main menu: {} because of privilege: {} for menu item: {}",
				new Object[] { menu.getName(),
					menuEntry.getRequiredPrivilege(),
					menuEntry.getName() });
		    }
		    return true;
		}
	    }
	}
	if (LOG.isTraceEnabled()) {
	    LOG.trace(
		    "Hide main menu: {} because no privileges to menu items or sub menu items",
		    menu.getName());
	}
	return false;
    }

    protected boolean isSubMenuVisible(SubMenu menu, Roles roles) {
	for (IMenuEntry menuEntry : menu.getChildren()) {
	    if (roles.hasRole(menuEntry.getRequiredPrivilege())) {
		if (LOG.isTraceEnabled()) {
		    LOG.trace(
			    "Show sub menu: {} because of privilege: {} for menu item: {}",
			    new Object[] { menu.getName(),
				    menuEntry.getRequiredPrivilege(),
				    menuEntry.getName() });
		}
		return true;
	    }
	}
	return false;
    }
}
