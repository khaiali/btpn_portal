package com.sybase365.mobiliser.web.consumer.pages.signup;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.application.model.AbstractMobiliserApplication;
import com.sybase365.mobiliser.web.application.model.IMobiliserSignupApplication;

/**
 * 
 * @author msw
 */
public class ConsumerSignupApplication extends AbstractMobiliserApplication
	implements IMobiliserSignupApplication {

    @Override
    public void buildMenu(SybaseMenu menu, Roles roles) {

    }

}
