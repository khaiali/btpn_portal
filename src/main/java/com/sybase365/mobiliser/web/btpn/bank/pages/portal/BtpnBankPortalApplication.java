package com.sybase365.mobiliser.web.btpn.bank.pages.portal;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.application.model.AbstractMobiliserAuthenticatedApplication;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBankPortalAgentCareMenuPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBankPortalCustomerCareMenuPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This class is the Mobiliser Authenticaed Application class for BTPN Bank Portal Application. This consists of the Top
 * Menu functionality for this application. Also, consists of the login privilege and Home Page for this application.
 * 
 * @author Vikram Gunda
 */
public class BtpnBankPortalApplication extends AbstractMobiliserAuthenticatedApplication {

	/**
	 * Builds the top menu for this application
	 * 
	 * @param menu Menu Object for the menus to be displayed
	 * @param roles Roles for this application and logged in user
	 */
	@Override
	public void buildMenu(SybaseMenu menu, Roles roles) {
		MenuEntry scMenuEntry = new MenuEntry("main.menu.consumer.care", BtpnConstants.PRIV_UI_CUSTOMER_CARE,
			BtpnBankPortalCustomerCareMenuPage.class);
		scMenuEntry.setActive(false);
		menu.addMenuEntry(scMenuEntry);

		MenuEntry maMenuEntry = new MenuEntry("main.menu.agent.care", BtpnConstants.PRIV_UI_AGENT_CARE,
			BtpnBankPortalAgentCareMenuPage.class);
		maMenuEntry.setActive(false);
		menu.addMenuEntry(maMenuEntry);
	}
}
