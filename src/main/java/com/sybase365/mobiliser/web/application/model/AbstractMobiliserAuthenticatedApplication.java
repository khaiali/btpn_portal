package com.sybase365.mobiliser.web.application.model;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;

/**
 * 
 * @author msw
 */
public abstract class AbstractMobiliserAuthenticatedApplication extends
	AbstractMobiliserApplication implements
	IMobiliserAuthenticatedApplication {

    private String loginPrivilege;
    private Class homePage;

    @Override
    public String getLoginPrivilege() {
	return this.loginPrivilege;
    }

    public void setLoginPrivilege(String value) {
	this.loginPrivilege = value;
    }

    @Override
    public Class getHomePage() {
	return this.homePage;
    }

    public void setHomePage(Class value) {
	this.homePage = value;
    }

    @Override
    public abstract void buildMenu(SybaseMenu menu, Roles roles);

}
