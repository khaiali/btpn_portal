package com.sybase365.mobiliser.web.cst.pages;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.MainMenu;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SubMenu;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.web.application.model.AbstractMobiliserAuthenticatedApplication;
import com.sybase365.mobiliser.web.cst.pages.bulkprocessing.BulkHistoryPage;
import com.sybase365.mobiliser.web.cst.pages.bulkprocessing.ConfirmFilePage;
import com.sybase365.mobiliser.web.cst.pages.bulkprocessing.FileUploadPage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.CouponCategoriesPage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.CouponTypesPage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.SearchCouponPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.CustomerRegistration;
import com.sybase365.mobiliser.web.cst.pages.customercare.FindCustomerPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.FindPendingCustomerPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.FindPendingTransactionPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.FindPendingWalletPage;
import com.sybase365.mobiliser.web.cst.pages.notificationmgr.CreateAttachmentPage;
import com.sybase365.mobiliser.web.cst.pages.notificationmgr.CreateMessagePage;
import com.sybase365.mobiliser.web.cst.pages.notificationmgr.FindAttachmentPage;
import com.sybase365.mobiliser.web.cst.pages.notificationmgr.FindMessagePage;
import com.sybase365.mobiliser.web.cst.pages.notificationmgr.ImportMessagePage;
import com.sybase365.mobiliser.web.cst.pages.reports.DefaultBatchCSTReportPage;
import com.sybase365.mobiliser.web.cst.pages.reports.DefaultDynamicCSTReportPage;
import com.sybase365.mobiliser.web.cst.pages.reports.DownloadCSTReportPage;
import com.sybase365.mobiliser.web.cst.pages.selfcare.CstHomePage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.CurrencyExchangeHistoryPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.CurrencyExchangePage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.CustomerApprovalConfPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.FeeSetConfigurationPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.FeeTypeConfigurationPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.FileApprovalConfPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.GlobalConfigurationPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.LimitClassPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.LimitSetsPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.RestrictionSetsPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.TransactionApprovalConfPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.WalletApprovalConfPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking.ManageNumberRestrictionsPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking.ManageServicePackagesPage;
import com.sybase365.mobiliser.web.cst.pages.systemconfig.mbanking.OptinSettingsPage;
import com.sybase365.mobiliser.web.cst.pages.usermanager.CreateAgentPage;
import com.sybase365.mobiliser.web.cst.pages.usermanager.CreatePrivilegePage;
import com.sybase365.mobiliser.web.cst.pages.usermanager.CreateRolePage;
import com.sybase365.mobiliser.web.cst.pages.usermanager.FindAgentPage;
import com.sybase365.mobiliser.web.cst.pages.usermanager.FindPrivilegePage;
import com.sybase365.mobiliser.web.cst.pages.usermanager.FindRolePage;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * @author msw
 */
public class CstApplication extends AbstractMobiliserAuthenticatedApplication {

    @Override
    public void buildMenu(SybaseMenu menu, Roles roles) {

	MenuEntry cstMenuEntry = new MenuEntry("top.menu.cst.selfcare",
		Constants.PRIV_CST_LOGIN, CstHomePage.class);
	cstMenuEntry.setActive(true);
	menu.addMenuEntry(cstMenuEntry);

	final MainMenu customerCare = new MainMenu(
		"top.menu.cst.customer.care", Constants.PRIV_CST_LOGIN);
	customerCare.addMenuEntry(new MenuEntry("menu.cst.register.custormer",
		Constants.PRIV_CUST_WRITE, CustomerRegistration.class));
	customerCare.addMenuEntry(new MenuEntry("menu.cst.find.customer",
		Constants.PRIV_CUST_READ, FindCustomerPage.class));

	SubMenu approvalsSubMenu = new SubMenu("menu.cst.approvals",
		Constants.PRIV_FIND_PENDING_APPROVALS);

	approvalsSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.approve.custormer",
		Constants.PRIV_APPROVE_PENDING_CUSTOMER,
		FindPendingCustomerPage.class));

	approvalsSubMenu.addMenuEntry(new MenuEntry("menu.cst.approve.wallets",
		Constants.PRIV_APPROVE_PENDING_WALLET,
		FindPendingWalletPage.class));

	approvalsSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.approve.transactions",
		Constants.PRIV_APPROVE_PENDING_TRANSACTION,
		FindPendingTransactionPage.class));

	customerCare.addSubMenu(approvalsSubMenu);

	if (isMenuVisible(customerCare, roles)) {
	    menu.addMainMenu(customerCare);
	}

	final MainMenu configuration = new MainMenu(
		"top.menu.cst.configuration", Constants.PRIV_CST_LOGIN);
	SubMenu exchangeRateSubMenu = new SubMenu("menu.cst.exchange.rate",
		Constants.PRIV_FOREX_READ);

	exchangeRateSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.currency.exchange", Constants.PRIV_FOREX_READ,
		CurrencyExchangePage.class));

	exchangeRateSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.currency.exchangehistory", Constants.PRIV_FOREX_READ,
		CurrencyExchangeHistoryPage.class));
	configuration.addSubMenu(exchangeRateSubMenu);
	configuration.addMenuEntry(new MenuEntry("menu.cst.service.packages",
		Constants.PRIV_CST_MBANKING, ManageServicePackagesPage.class));
	configuration.addMenuEntry(new MenuEntry(
		"menu.cst.number.restrictions", Constants.PRIV_CST_MBANKING,
		ManageNumberRestrictionsPage.class));
	configuration.addMenuEntry(new MenuEntry("menu.cst.optin.settings",
		Constants.PRIV_CST_MBANKING, OptinSettingsPage.class));
	SubMenu feeTypeSubMenu = new SubMenu("menu.cst.configuration.fee",
		Constants.PRIV_FEE_CONFIGURATION);

	feeTypeSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.configuration.fee.feeTypes",
		Constants.PRIV_FEE_CONFIGURATION,
		FeeTypeConfigurationPage.class));

	feeTypeSubMenu
		.addMenuEntry(new MenuEntry(
			"menu.cst.configuration.fee.feeSets",
			Constants.PRIV_FEE_CONFIGURATION,
			FeeSetConfigurationPage.class));
	configuration.addSubMenu(feeTypeSubMenu);
	SubMenu limitsSubMenu = new SubMenu("menu.cst.configuration.limits",
		Constants.PRIV_CST_LIMITS);
	limitsSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.configuration.limitsets", Constants.PRIV_CST_LIMITS,
		LimitSetsPage.class));
	limitsSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.configuration.limitClasses",
		Constants.PRIV_CST_LIMITS, LimitClassPage.class));
	configuration.addSubMenu(limitsSubMenu);

	SubMenu privilegesSubMenu = new SubMenu(
		"menu.cst.configuration.privileges",
		Constants.PRIV_CST_PRIVILEGES);

	privilegesSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.configuration.privileges.customers",
		Constants.PRIV_CST_PRIVILEGES_CUSTOMER,
		CustomerApprovalConfPage.class));

	privilegesSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.configuration.privileges.wallets",
		Constants.PRIV_CST_PRIVILEGES_WALLET,
		WalletApprovalConfPage.class));

	privilegesSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.configuration.privileges.transactions",
		Constants.PRIV_CST_PRIVILEGES_TXN,
		TransactionApprovalConfPage.class));

	privilegesSubMenu.addMenuEntry(new MenuEntry(
		"menu.cst.configuration.privileges.files",
		Constants.PRIV_CST_PRIVILEGES_FILETYPE,
		FileApprovalConfPage.class));

	configuration.addSubMenu(privilegesSubMenu);

	configuration
		.addMenuEntry(new MenuEntry("menu.cst.configuration.global",
			Constants.PRIV_CST_GLOBAL_CONFIG,
			GlobalConfigurationPage.class));

	configuration.addMenuEntry(new MenuEntry("menu.cst.restrictions",
		Constants.PRIV_EDIT_RESTRICTIONS, RestrictionSetsPage.class));

	if (isMenuVisible(configuration, roles)) {
	    menu.addMainMenu(configuration);
	}

	// Bulk Processing
	final MainMenu bulk = new MainMenu("top.menu.cst.bulk.processing",
		Constants.PRIV_CST_BULK_PROCESSING);
	bulk.addMenuEntry(new MenuEntry("menu.bulk.processing.file.upload",
		Constants.PRIV_CST_BULK_UPLOAD, FileUploadPage.class));
	bulk.addMenuEntry(new MenuEntry("menu.bulk.processing.confirm.file",
		Constants.PRIV_CST_BULK_CONFIRM, ConfirmFilePage.class));
	bulk.addMenuEntry(new MenuEntry("menu.bulk.processing.history.file",
		Constants.PRIV_CST_BULK_HISTORY, BulkHistoryPage.class));

	if (isMenuVisible(bulk, roles)) {
	    menu.addMainMenu(bulk);
	}

	// Coupons
	final MainMenu coupons = new MainMenu("top.menu.cst.coupons",
		Constants.PRIV_CST_LOGIN);
	coupons.addMenuEntry(new MenuEntry("menu.coupon.types",
		Constants.PRIV_VIEW_COUPON_TYPES, CouponTypesPage.class));
	coupons.addMenuEntry(new MenuEntry("menu.coupon.categories",
		Constants.PRIV_VIEW_CPN_CATEGORY, CouponCategoriesPage.class));
	coupons.addMenuEntry(new MenuEntry("menu.coupon.search.coupon",
		Constants.PRIV_SEARCH_COUPON, SearchCouponPage.class));

	if (isMenuVisible(coupons, roles)) {
	    menu.addMainMenu(coupons);
	}

	// User Manager
	final MainMenu userManager = new MainMenu("top.menu.cst.user.manager",
		Constants.PRIV_CST_UMGR);
	userManager.addMenuEntry(new MenuEntry("menu.create.agent",
		Constants.PRIV_UMGR_CREATE_AGENT, CreateAgentPage.class));
	userManager.addMenuEntry(new MenuEntry("menu.find/edit.agent",
		Constants.PRIV_UMGR_FIND_AGENT, FindAgentPage.class));
	userManager.addMenuEntry(new MenuEntry("menu.role.agent",
		Constants.PRIV_CST_UMGR_CREATE_ROLE, CreateRolePage.class));
	userManager.addMenuEntry(new MenuEntry("menu.find/edit.role.agent",
		Constants.PRIV_CST_UMGR_EDIT_ROLE, FindRolePage.class));
	userManager.addMenuEntry(new MenuEntry("menu.create.privilege.agent",
		Constants.PRIV_CST_UMGR_CREATE_PRIVILEGE,
		CreatePrivilegePage.class));
	userManager
		.addMenuEntry(new MenuEntry("menu.find/edit.privilege.agent",
			Constants.PRIV_CST_UMGR_EDIT_PRIVILEGE,
			FindPrivilegePage.class));

	if (isMenuVisible(userManager, roles)) {
	    menu.addMainMenu(userManager);
	}

	// Config manager
	// TODO add actual conf mgr privileges
	final MainMenu notificationMgr = new MainMenu(
		"top.menu.cst.notification.mgr", Constants.PRIV_CST_LOGIN);
	notificationMgr.addMenuEntry(new MenuEntry("menu.cst.create.message",
		Constants.PRIV_NMGR_CREATE, CreateMessagePage.class));
	notificationMgr.addMenuEntry(new MenuEntry("menu.cst.find.message",
		Constants.PRIV_NMGR_EDIT, FindMessagePage.class));
	notificationMgr.addMenuEntry(new MenuEntry("menu.cst.import.message",
		Constants.PRIV_NMGR_CREATE, ImportMessagePage.class));
	notificationMgr.addMenuEntry(new MenuEntry(
		"menu.cst.create.attachment", Constants.PRIV_NMGR_CREATE,
		CreateAttachmentPage.class));
	notificationMgr.addMenuEntry(new MenuEntry("menu.cst.find.attachment",
		Constants.PRIV_NMGR_EDIT, FindAttachmentPage.class));

	if (isMenuVisible(notificationMgr, roles)) {
	    menu.addMainMenu(notificationMgr);
	}

	// reports
	final MainMenu reports = new MainMenu("top.menu.reports",
		Constants.PRIV_CST_REPORTS);

	reports.addMenuEntry(new MenuEntry("top.menu.reports.online",
		Constants.PRIV_CST_REPORTS, DefaultDynamicCSTReportPage.class));
	reports.addMenuEntry(new MenuEntry("top.menu.reports.batch",
		Constants.PRIV_CST_REPORTS, DefaultBatchCSTReportPage.class));
	reports.addMenuEntry(new MenuEntry("top.menu.reports.batch.download",
		Constants.PRIV_CST_REPORTS, DownloadCSTReportPage.class));

	if (isMenuVisible(reports, roles)) {
	    menu.addMainMenu(reports);
	}
    }

}
