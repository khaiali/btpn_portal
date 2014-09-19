package com.sybase365.mobiliser.web.application.model;

import java.util.Map;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;

/**
 * 
 * @author msw
 */
public interface IMobiliserApplication {

    Map<String, Class> getBookmarkablePages();

    Class getHomePage();

    void buildMenu(SybaseMenu menu, Roles roles);
}
