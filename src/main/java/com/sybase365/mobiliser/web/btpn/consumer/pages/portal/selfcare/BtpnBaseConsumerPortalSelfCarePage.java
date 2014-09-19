package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.btpn.common.components.LeftMenuView;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.BtpnBaseConsumerPortalPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.ManageFavoritesPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.airtimetopup.AirtimeTopupPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.billpayment.BillPaymentPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.FundTransferPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.ManageSubAccountsPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SubAccountTransferPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.StandingInstructionTopupPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.StandingInstructionsPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.transaction.ConsumerTransactionPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the base page for all the bank portal self care operations. This consist of the left menu preparation for all
 * the self care operations for the bank portal users.
 * 
 * @author Vikram Gunda
 */
@AuthorizeInstantiation(BtpnConstants.PRIV_BTPN_CONSUMER_LOGIN)
public abstract class BtpnBaseConsumerPortalSelfCarePage extends BtpnBaseConsumerPortalPage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
		.getLogger(BtpnBaseConsumerPortalSelfCarePage.class);

	public BtpnBaseConsumerPortalSelfCarePage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseConsumerPortalSelfCarePage(final PageParameters parameters) {
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
	 * Add the bank portal self care page components
	 */
	protected void addBaseBankPortalSelfCarePageComponents() {
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

		LinkedList<IMenuEntry> menuItems = new LinkedList<IMenuEntry>();

		// Add Home Page to left menu
		MenuEntry hmMenuEntry = new MenuEntry("left.menu.home", BtpnConstants.PRIV_HOME_CONSUMER_PORTAL,
			ConsumerPortalHomePage.class);
		hmMenuEntry.setActive(true);
		menuItems.add(hmMenuEntry);

		// Add Manage Profile Page to left menu
		MenuEntry mpMenuEntry = new MenuEntry("left.menu.viewtransaction",
			BtpnConstants.PRIV_CONSUMER_PORTAL_VIEW_TXN_HISTORY, ConsumerTransactionPage.class);
		mpMenuEntry.setActive(false);
		menuItems.add(mpMenuEntry);

		// Add Change Pin Page to left menu
		MenuEntry changePinMenuEntry = new MenuEntry("left.menu.changepin", BtpnConstants.PRIV_CUSTOMER_CHANGE_PIN,
			ConsumerPortalChangePinPage.class);
		changePinMenuEntry.setActive(false);
		menuItems.add(changePinMenuEntry);

		// Add View Profile Page to left menu
		MenuEntry registrationMenuEntry = new MenuEntry("left.menu.viewprofile",
			BtpnConstants.PRIV_CONSUMER_PORTAL_VIEW_PROFILE, ViewCustomerProfile.class);
		registrationMenuEntry.setActive(false);
		menuItems.add(registrationMenuEntry);

		// Add View Profile Page to left menu
		MenuEntry changeLanguageMenuEntry = new MenuEntry("left.menu.changelanguage",
			BtpnConstants.PRIV_CONSUMER_PORTAL_CHANGE_LANGUAGE, ConsumerChangeLanguagePage.class);
		changeLanguageMenuEntry.setActive(false);
		menuItems.add(changeLanguageMenuEntry);

		// Add Fund Transfer Page to left menu
		MenuEntry fundTransferMenuEntry = new MenuEntry("left.menu.fundTrasfer",
			BtpnConstants.PRIV_CONSUMER_PORTAL_FUND_TRANSFER, FundTransferPage.class);
		fundTransferMenuEntry.setActive(false);
		menuItems.add(fundTransferMenuEntry);

		// Bill payments Page to left menu
		MenuEntry billPaymentsMenuEntry = new MenuEntry("left.menu.billPayment",
			BtpnConstants.PRIV_CONSUMER_PORTAL_BILL_PAYMENT, BillPaymentPage.class);
		billPaymentsMenuEntry.setActive(false);
		menuItems.add(billPaymentsMenuEntry);

		// AirTime Top-up Page to left menu
		MenuEntry topupMenuEntry = new MenuEntry("left.menu.airTimetopup",
			BtpnConstants.PRIV_CONSUMER_PORTAL_AIRTIME_TOPUP, AirtimeTopupPage.class);
		topupMenuEntry.setActive(false);
		menuItems.add(topupMenuEntry);

		// Manage Favorites Page to left menu
		MenuEntry manageFavoritesMenuEntry = new MenuEntry("left.menu.manageFavorites",
			BtpnConstants.PRIV_CONSUMER_PORTAL_MANAGE_FAVORITES, ManageFavoritesPage.class);
		manageFavoritesMenuEntry.setActive(false);
		menuItems.add(manageFavoritesMenuEntry);

		// Manage SubAccounts Page to left menu
		MenuEntry manageSubAccountsMenuEntry = new MenuEntry("left.menu.manageSubAccounts",
			BtpnConstants.PRIV_CONSUMER_PORTAL_MANAGE_SUB_ACCOUNTS, ManageSubAccountsPage.class);
		manageSubAccountsMenuEntry.setActive(false);
		menuItems.add(manageSubAccountsMenuEntry);

		// SubAccount Transfer Page to left menu
		MenuEntry subAccountsMenuEntry = new MenuEntry("left.menu.subAccountTransfer",
			BtpnConstants.PRIV_CONSUMER_PORTAL_SUB_ACCOUNT_TRANSFER, SubAccountTransferPage.class);
		subAccountsMenuEntry.setActive(false);
		menuItems.add(subAccountsMenuEntry);

		// Standing Instructions Page to left menu
		MenuEntry standingInstructionsMenuEntry = new MenuEntry("left.menu.standingInstructions",
			BtpnConstants.PRIV_CONSUMER_PORTAL_MANAGE_STANDING_INSTRUCTIONS, StandingInstructionsPage.class);
		standingInstructionsMenuEntry.setActive(false);
		menuItems.add(standingInstructionsMenuEntry);
		
		// Standing Instructions Page to left menu
		MenuEntry changeRecieptModePage = new MenuEntry("left.menu.changeRecieptMode",
			BtpnConstants.PRIV_CONSUMER_PORTAL_CHANGE_RECIEPT_MODE, ChangeRecieptModePage.class);
		changeRecieptModePage.setActive(false);
		menuItems.add(changeRecieptModePage);
		
		// Standing Instructions Page to left menu
		MenuEntry viewHelpMenuEntry = new MenuEntry("left.menu.viewHelp",
			BtpnConstants.PRIV_CONSUMER_PORTAL_VIEW_HELP, ConsumerViewHelpPage.class);
		viewHelpMenuEntry.setActive(false);
		menuItems.add(viewHelpMenuEntry);

		// Manual Advice Page to left menu
		MenuEntry manualAdviceMenuEntry = new MenuEntry("left.menu.manualAdvice",
			BtpnConstants.PRIV_CONSUMER_PORTAL_MANUAL_ADVICES, ManualAdvicePage.class);
		manualAdviceMenuEntry.setActive(false);
		menuItems.add(manualAdviceMenuEntry);
		
		return menuItems;

	}
}
