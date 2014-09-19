package com.sybase365.mobiliser.web.btpn.consumer.pages.portal;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.application.model.AbstractMobiliserAuthenticatedApplication;

/**
 * This class is the Mobiliser Authenticaed Application class for BTPN Consumer Portal Application. This consists of the Top
 * Menu functionality for this application. Also, consists of the login privilege and Home Page for this application.
 * 
 * @author Vikram Gunda
 */
public class BtpnConsumerPortalApplication extends
	AbstractMobiliserAuthenticatedApplication {
	
	/**
	 * Builds the top menu for this application
	 * 
	 * @param menu Menu Object for the menus to be displayed
	 * @param roles Roles for this application and logged in user
	 */
    @Override
    public void buildMenu(SybaseMenu menu, Roles roles) {

    }

}
