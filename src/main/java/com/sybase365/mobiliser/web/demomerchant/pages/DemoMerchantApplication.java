package com.sybase365.mobiliser.web.demomerchant.pages;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.application.model.AbstractMobiliserApplication;
import com.sybase365.mobiliser.web.application.model.IMobiliserUnauthenticatedApplication;

/**
 * 
 * @author msw
 */
public class DemoMerchantApplication extends AbstractMobiliserApplication
	implements IMobiliserUnauthenticatedApplication {

    @Override
    public void buildMenu(SybaseMenu menu, Roles roles) {

    }

}
