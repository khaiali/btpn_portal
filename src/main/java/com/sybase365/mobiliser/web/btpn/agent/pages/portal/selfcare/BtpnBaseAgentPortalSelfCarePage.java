package com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.BtpnBaseAgentPortalPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.AgentPortalCustomCashInPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout.AgentPortalCustomCashOutPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.profile.AgentProfilePage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration.ChildAgentRegistrationMobileNumberPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration.SubAgentRegistrationMobileNumberPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.transaction.AgentTransactionsPage;
import com.sybase365.mobiliser.web.btpn.common.components.LeftMenuView;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the base page for all the bank portal self care operations. This consist of the left menu preparation for all
 * the self care operations for the bank portal users.
 * 
 * @author Vikram Gunda
 */
public abstract class BtpnBaseAgentPortalSelfCarePage extends BtpnBaseAgentPortalPage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
		.getLogger(BtpnBaseAgentPortalSelfCarePage.class);

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 */
	public BtpnBaseAgentPortalSelfCarePage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseAgentPortalSelfCarePage(final PageParameters parameters) {
		super(parameters);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		LOG.debug("###BtpnBasePage:initPageComponents()====> Start");
		super.initOwnPageComponents();
		addBaseBankPortalSelfCarePageComponents();
		LOG.debug("###BtpnBasePage:initPageComponents()====> End");
	}

	/**
	 * Add the bank portal self care page components.
	 */
	protected void addBaseBankPortalSelfCarePageComponents() {
		final BtpnCustomer customer = this.getMobiliserWebSession().getBtpnLoggedInCustomer();
		// show customer Id
		add(new Label("agentId", " :  " + String.valueOf(customer.getCustomerId())).setRenderBodyOnly(true));
		// show available balance
		final String msisdn = getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
		final Long svaBalLong = getSvaBalance(msisdn);
		String balance = displayAmount(svaBalLong);
		if (!PortalUtils.exists(balance)) {
			balance = "IDR 0";
		}
		this.setSvaBalance(svaBalLong == null ? 0 : svaBalLong);
		add(new Label("agentBalance", " : " + balance));
		LinkedList<IMenuEntry> menuEntries = getMobiliserWebSession().getLeftMenu();
		if (menuEntries == null || menuEntries.size() == 0) {
			menuEntries = buildLeftMenu();
			getMobiliserWebSession().setLeftMenu(menuEntries);
		}
		add(new LeftMenuView("leftMenu", new Model<LinkedList<IMenuEntry>>(menuEntries), getMobiliserWebSession()
			.getBtpnRoles()).setRenderBodyOnly(true));
	}

	/**
	 * Build the left menu
	 * 
	 * @return LinkedList<IMenuEntry> list of left menu entries along with their priviliges
	 */
	protected LinkedList<IMenuEntry> buildLeftMenu() {

		final LinkedList<IMenuEntry> menuItems = new LinkedList<IMenuEntry>();

		// Add Home Page to left menu
		final MenuEntry hmMenuEntry = new MenuEntry("left.menu.home", BtpnConstants.PRIV_HOME_AGENT_PORTAL,
			AgentPortalHomePage.class);
		hmMenuEntry.setActive(true);
		menuItems.add(hmMenuEntry);

		// Add Manage Profile Page to left menu
		final MenuEntry regMenuEntry = new MenuEntry("left.menu.agent.registration",
			BtpnConstants.PRIV_AGENT_REGISTRATION, ChildAgentRegistrationMobileNumberPage.class);
		regMenuEntry.setActive(false);
		menuItems.add(regMenuEntry);

		// Add Manage Profile Page to left menu
		final MenuEntry subRegMenuEntry = new MenuEntry("left.menu.sub.agent.registration",
			BtpnConstants.PRIV_SUB_AGENT_REGISTRATION, SubAgentRegistrationMobileNumberPage.class);
		subRegMenuEntry.setActive(false);
		menuItems.add(subRegMenuEntry);

		// Add Manage Profile Page to left menu
		final MenuEntry mpMenuEntry = new MenuEntry("left.menu.agent.manageprofile",
			BtpnConstants.PRIV_AGENT_MANAGE_PROFILE, AgentProfilePage.class);
		mpMenuEntry.setActive(false);
		menuItems.add(mpMenuEntry);

		// Add Cash in Page to left menu
//		final MenuEntry cashinMenuEntry = new MenuEntry("left.menu.agent.cashin",
//			BtpnConstants.PRI_AGENT_PORTAL_CASH_IN, AgentPortalCashinPage.class);
//		cashinMenuEntry.setActive(false);
//		menuItems.add(cashinMenuEntry);
		
		final MenuEntry cashinMenuEntry = new MenuEntry("left.menu.agent.cashin",
		BtpnConstants.PRI_AGENT_PORTAL_CASH_IN, AgentPortalCustomCashInPage.class);
		cashinMenuEntry.setActive(false);
		menuItems.add(cashinMenuEntry);

		// Add Cash out Page to left menu
//		final MenuEntry cashoutMenuEntry = new MenuEntry("left.menu.agent.cashout",
//			BtpnConstants.PRIV_AGENT_PORTAL_CASH_OUT, AgentPortalCashOutPage.class);
//		cashoutMenuEntry.setActive(false);
//		menuItems.add(cashoutMenuEntry);
		
		final MenuEntry cashoutMenuEntry = new MenuEntry("left.menu.agent.cashout",
		BtpnConstants.PRIV_AGENT_PORTAL_CASH_OUT, AgentPortalCustomCashOutPage.class);
		cashoutMenuEntry.setActive(false);
		menuItems.add(cashoutMenuEntry);

		// Add View Transaction History Page to left menu
		final MenuEntry txnHistory = new MenuEntry("left.menu.agent.txnhistory",
			BtpnConstants.PRIV_VIEW_AGENT_TXN_HISTORY, AgentTransactionsPage.class);
		txnHistory.setActive(false);
		menuItems.add(txnHistory);

		// Add Change Pin Page to left menu
		final MenuEntry changePinMenuEntry = new MenuEntry("left.menu.changepin", BtpnConstants.PRIV_AGENT_CHANGE_PIN,
			AgentPortalChangePinPage.class);
		changePinMenuEntry.setActive(false);
		menuItems.add(changePinMenuEntry);

		final MenuEntry changeLanguageMenu = new MenuEntry("left.menu.agent.changeLanguage",
			BtpnConstants.PRIV_HOME_AGENT_PORTAL, AgentChangeLanguagePage.class);
		changeLanguageMenu.setActive(false);
		menuItems.add(changeLanguageMenu);

		final MenuEntry viewHelpMenu = new MenuEntry("left.menu.agent.viewHelp",
			BtpnConstants.PRIV_AGENT_PORTAL_VIEW_HELP, AgentViewHelpPage.class);
		viewHelpMenu.setActive(false);
		menuItems.add(viewHelpMenu);

		return menuItems;

	}

}
