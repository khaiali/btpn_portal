package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import java.util.LinkedList;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.btpnwow.portal.common.util.MenuBuilder;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.BtpnBaseBankPortalPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.addhelp.AddHelpPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.billpayment.BankBillPaymentPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCustomCashInPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalTopAgentCashInPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCustomCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalTopAgentCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGLPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender.ApproveHolidayCalendarPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.holidaycalender.HolidayCalender;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ApproveFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageAirtimeTopupFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeeApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managelimit.ManageLimitPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsApprovePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankAdminRegistrationPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankCheckerApprovalPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankUserRegistrationPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.ConsumerRegistrationMobileNumberPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.TopAgentRegistrationMobileNumberPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload.SalaryUploadPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload.SearchSalaryDataPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.transaction.ApproveTxnReversalPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.transaction.TransactionReversalPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.transdetailsreport.TransactionDetailsReportPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.transledger.ApproveTransactionGeneralLedgerPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.transledger.TransactionGeneralLedgerPage;
import com.sybase365.mobiliser.web.btpn.common.components.LeftMenuView;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
//import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCustomCashInPage;
//import com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger.ManageCustomGeneralLedgerPage;

/**
 * This is the base page for all the bank portal self care operations. This consist of the left menu preparation for all
 * the self care operations for the bank portal users.
 * 
 * @author Vikram Gunda
 */
public abstract class BtpnBaseBankPortalSelfCarePage extends BtpnBaseBankPortalPage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BtpnBaseBankPortalSelfCarePage.class);

	@SpringBean(name = "menuBuilder")
	private MenuBuilder menuBuilder;
	
	/**
	 * Constructor for this page.
	 */
	public BtpnBaseBankPortalSelfCarePage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseBankPortalSelfCarePage(final PageParameters parameters) {
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
		addSelfCarePageComponents();
		LOG.debug("###BtpnBasePage:initPageComponents()====> End");
	}

	/**
	 * Add the bank portal self care page components
	 */
	protected void addSelfCarePageComponents() {
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

		menuItems.addAll(menuBuilder.getMenuList("bankportal"));
		
		// Add Home Page to left menu
		final MenuEntry hmMenuEntry = new MenuEntry("left.menu.home", BtpnConstants.PRIV_UI_HOME_BANK_PORTAL,
			BankPortalHomePage.class);
		hmMenuEntry.setActive(true);
		menuItems.add(hmMenuEntry);

		// Add Manage Profile Page to left menu
		final MenuEntry mpMenuEntry = new MenuEntry("left.menu.manageprofile",
			BtpnConstants.PRIV_UI_BANK_MANAGE_PROFILE, ManageProfilePage.class);
		mpMenuEntry.setActive(false);
		menuItems.add(mpMenuEntry);
		
		// Add Sample Menu 1
		final MenuEntry mpSample1 = new MenuEntry("left.menu.elimit",
			BtpnConstants.PRIV_UI_BANK_LIMITEX, ElimitPage.class);
		mpSample1.setActive(false);
		menuItems.add(mpSample1);
		
		// Add Sample Menu 2
		final MenuEntry mpSample2 = new MenuEntry("left.menu.approvalelimit",
			BtpnConstants.PRIV_UI_BANK_LIMITEX_APPROVE, ElimitApprovePage.class);
		mpSample2.setActive(false);
		menuItems.add(mpSample2);
		
		// Add Bank Admin Registration Page to left menu
		final MenuEntry bankAdmRegMenuEntry = new MenuEntry("left.menu.bank.admin.registration",
			BtpnConstants.PRIV_UI_BANK_ADMIN_REGISTRATION, BankAdminRegistrationPage.class);
		bankAdmRegMenuEntry.setActive(false);
		menuItems.add(bankAdmRegMenuEntry);
		
		// Add Custom Cash In Page to left menu
		final MenuEntry cashInCustomMenuEntry = new MenuEntry("left.menu.bank.portal.cashIn",
			BtpnConstants.PRIV_CUSTOMER_CASHIN_AT_AGENT, BankPortalCustomCashInPage.class);
		cashInCustomMenuEntry.setActive(false);
		menuItems.add(cashInCustomMenuEntry); 
		
		// Add Custom Cash Out Page to left men
		final MenuEntry cashOutCustomMenuEntry = new MenuEntry("left.menu.bank.portal.cashOut",
			BtpnConstants.PRIV_CUSTOMER_CASHOUT_AT_AGENT, BankPortalCustomCashOutPage.class);
		cashOutCustomMenuEntry.setActive(false);
		menuItems.add(cashOutCustomMenuEntry);

		// Add Bank User Registration Page to left menu
		final MenuEntry bankUserRegMenuEntry = new MenuEntry("left.menu.bank.user.registration",
			BtpnConstants.PRIV_UI_BANK_USER_REGISTRATION, BankUserRegistrationPage.class);
		bankUserRegMenuEntry.setActive(false);
		menuItems.add(bankUserRegMenuEntry);

		// Add Change Language Page to left menu
		final MenuEntry approvalRegPage = new MenuEntry("left.menu.consumer.approval",
			BtpnConstants.PRIV_UI_CUSTOMER_REGISTRATION_APPROVAL, BankCheckerApprovalPage.class);
		approvalRegPage.setActive(false);
		menuItems.add(approvalRegPage);

		// Registration Menu Entry
		final MenuEntry registrationMenuEntry = new MenuEntry("left.menu.consumerRegistration",
			BtpnConstants.PRIV_UI_CUSTOMER_REGISTRATION, ConsumerRegistrationMobileNumberPage.class);
		registrationMenuEntry.setActive(false);
		menuItems.add(registrationMenuEntry);

		final MenuEntry officerAgentMenuEntry = new MenuEntry("left.menu.officer.approval",
			BtpnConstants.PRIV_UI_OFFICER_AGENT_APPROVAL, BankCheckerApprovalPage.class);
		officerAgentMenuEntry.setActive(false);
		menuItems.add(officerAgentMenuEntry);

		// Add Activate/Deactivate Menu to left menu
		final MenuEntry activateDeactivateMenu = new MenuEntry("left.menu.bank.activate.deactivate.menu",
			BtpnConstants.PRIV_UI_BANK_PORTAL_ACTIVATE_DEACTIVATE_MENU, ActivateDeactivateMenuPage.class);
		activateDeactivateMenu.setActive(false);
		menuItems.add(activateDeactivateMenu);

		//TOP Agen Cash IN
		/*
		final MenuEntry cashInMenuEntry = new MenuEntry("left.menu.bank.portal.AgentCashIn",
			BtpnConstants.PRIV_TOPAGENT_CASHIN_AT_BANK, BankPortalCashinPage.class);
		cashInMenuEntry.setActive(false);
		menuItems.add(cashInMenuEntry);
		*/
		//change to:
		final MenuEntry cashInMenuEntry = new MenuEntry("left.menu.bank.portal.AgentCashIn",
			BtpnConstants.PRIV_TOPAGENT_CASHIN_AT_BANK, BankPortalTopAgentCashInPage.class);
		cashInMenuEntry.setActive(false);
		menuItems.add(cashInMenuEntry);
		
		
		//TOP Agen Cash OUT
		/*
		final MenuEntry cashOutMenuEntry = new MenuEntry("left.menu.bank.portal.AgentCashOut",
			BtpnConstants.PRIV_UI_TOP_AGENT_CASH_OUT, BankPortalCashOutPage.class);
		cashOutMenuEntry.setActive(false);
		menuItems.add(cashOutMenuEntry);
		*/
		//change to
		final MenuEntry cashOutMenuEntry = new MenuEntry("left.menu.bank.portal.AgentCashOut",
				BtpnConstants.PRIV_TOPAGENT_CASHOUT_AT_BANK, BankPortalTopAgentCashOutPage.class);
		cashOutMenuEntry.setActive(false);
		menuItems.add(cashOutMenuEntry);
			
		
		// Approve Customer data
		final MenuEntry transReport = new MenuEntry("left.menu.transReport",
			BtpnConstants.PRIV_UI_TRANSACTION_DETAILS_REPORT, TransactionDetailsReportPage.class);
		transReport.setActive(false);
		menuItems.add(transReport);

		// Add Change Language Page to left menu
		final MenuEntry clMenuEntry = new MenuEntry("left.menu.changeLanguage", BtpnConstants.PRIV_UI_CHANGE_LANGUAGE,
			ChangeLanguagePage.class);
		clMenuEntry.setActive(false);
		menuItems.add(clMenuEntry);
		
		// Add Search Salary upload to left menu
		/*//jadi satu di : UI_BATCH_UPLOAD_CHECKER_PRIVILEGE
		final MenuEntry batchTrans = new MenuEntry("left.menu.batch.transaction",
			BtpnConstants.PRIV_UI_BATCH_TRANSACTION_UPLOAD, SearchSalaryDataPage.class);
		batchTrans.setActive(false);
		menuItems.add(batchTrans);
		*/
		
		// Add Search Salary upload to left menu
		/* //jadi satu di : UI_BATCH_UPLOAD_CHECKER_PRIVILEGE
		 final MenuEntry searchTrans = new MenuEntry("left.menu.search.transaction",
			BtpnConstants.PRIV_UI_SEARCH_TRANSACTION_DATA, SearchSalaryDataPage.class);
		searchTrans.setActive(false);
		menuItems.add(searchTrans);
		*/
		
		// Add Manage Products Menu
		final MenuEntry mgProductsMenuEntry = new MenuEntry("left.menu.manageProducts",
			BtpnConstants.PRIV_UI_MANAGE_PRODUCTS, ManageProductsPage.class);
		mgProductsMenuEntry.setActive(false);
		menuItems.add(mgProductsMenuEntry);

		// Add Manage Products Menu
		final MenuEntry mgLimitMenuEntry = new MenuEntry("left.menu.manageLimit", BtpnConstants.PRIV_UI_MANAGE_LIMIT,
			ManageLimitPage.class);
		mgLimitMenuEntry.setActive(false);
		menuItems.add(mgLimitMenuEntry);

		// Add Manage Fee Page
		final MenuEntry mgFeeEntry = new MenuEntry("left.menu.manageFee", BtpnConstants.PRIV_UI_MANAGE_FEE,
				ManageCustomUseCaseFeePage.class);
		mgFeeEntry.setActive(false);   
		menuItems.add(mgFeeEntry);


		// Add Approve Fee Page
		final MenuEntry aprFeeEntry = new MenuEntry("left.menu.approveFee", BtpnConstants.PRIV_UI_MANAGE_FEE_APPROVAL,
				ManageCustomUseCaseFeeApprovePage.class);
		mgFeeEntry.setActive(false);   
		menuItems.add(aprFeeEntry);
		
		// Add BillPayment by : Imam
		final MenuEntry pgBillPayment = new MenuEntry("left.menu.billPayment", BtpnConstants.PRIV_UI_BANK_PORTAL_BILL_PAYMENT,
				BankBillPaymentPage.class);
		pgBillPayment.setActive(false);   
		menuItems.add(pgBillPayment);
		
		// Add Manage Biller Fee Page 
		//@author :Andi Samalangi ManageCustomBillerFeePage, 
		//@modified: Feny -> dipindahkan ke ManageBillPaymentFee
		final MenuEntry mgBillerFeeEntry = new MenuEntry("left.menu.manageBillPaymentFee", BtpnConstants.PRIV_UI_MANAGE_BILL_PAYMENT_FEE,
				ManageBillPaymentFeePage.class);
		mgBillerFeeEntry.setActive(false);   
		menuItems.add(mgBillerFeeEntry);
		
		// Add Approve Biller Fee page
		final MenuEntry mgAppBillerFeeEntry = new MenuEntry("left.menu.manageAppBillPaymentFee", BtpnConstants.PRIV_UI_MANAGE_BILL_PAYMENT_FEE_APPROVAL,
				ManageBillPaymentFeeApprovePage.class);
		mgAppBillerFeeEntry.setActive(false);   
		menuItems.add(mgAppBillerFeeEntry);
		
		// Add Manage Fee Page
		final MenuEntry mgAttFeeEntry = new MenuEntry("left.menu.manageAttFee",
			BtpnConstants.PRIV_UI_MANAGE_AIRTIME_TOPUP_FEE, ManageAirtimeTopupFeePage.class);
		mgAttFeeEntry.setActive(false);
		menuItems.add(mgAttFeeEntry);

		// Add Manage Products Menu
		final MenuEntry apprvProductsMenuEntry = new MenuEntry("left.menu.approveProducts",
			BtpnConstants.PRIV_UI_APPROVE_PRODUCTS, ManageProductsApprovePage.class);
		apprvProductsMenuEntry.setActive(false);
		menuItems.add(apprvProductsMenuEntry);
		
		// Add Approve Fee/Limit Menu
		final MenuEntry apprvfeeLimitMenuEntry = new MenuEntry("left.menu.approve.fee.limit",
			BtpnConstants.PRIV_UI_APPROVE_FEE_LIMIT, ApproveFeePage.class);
		apprvfeeLimitMenuEntry.setActive(false);
		menuItems.add(apprvfeeLimitMenuEntry);

		// Add Approve Fee/Limit Menu
		final MenuEntry childSubAgentApprovalMenuEntry = new MenuEntry("left.menu.child.sub.agent.approval",
			BtpnConstants.PRIV_UI_CHILD_SUB_AGENT_APPROVAL, BankCheckerApprovalPage.class);
		childSubAgentApprovalMenuEntry.setActive(false);
		menuItems.add(childSubAgentApprovalMenuEntry);
		
		// Add Change Language Page to left menu
		final MenuEntry topupRegPage = new MenuEntry("left.menu.topup.agent.registration",
			BtpnConstants.PRIV_UI_TOP_AGENT_REGISTRATION, TopAgentRegistrationMobileNumberPage.class);
		topupRegPage.setActive(false);
		menuItems.add(topupRegPage);

		// Add Transaction Reversal Page to left menu
//		final MenuEntry manageGLTransaction = new MenuEntry("left.menu.manageGL", BtpnConstants.PRIV_UI_MANAGE_GL,
//			ManageGeneralLedgerPage.class);
//		manageGLTransaction.setActive(false);
//		menuItems.add(manageGLTransaction);
		
		// Add Manage GL Page to left menu
		final MenuEntry manageGLTransaction = new MenuEntry("left.menu.manageGL", BtpnConstants.PRIV_UI_MANAGE_GL,
				ManageCustomGLPage.class);
		manageGLTransaction.setActive(false);
		menuItems.add(manageGLTransaction);

		// Add Transaction Reversal Page to left menu
		final MenuEntry approveGL = new MenuEntry("left.menu.approveGL", BtpnConstants.PRIV_UI_APPROVE_GL,
			ManageCustomGLApprovePage.class);
		approveGL.setActive(false);
		menuItems.add(approveGL);

		// Add Transaction GL
		final MenuEntry transactionGL = new MenuEntry("left.menu.transactionGL",
			BtpnConstants.PRIV_UI_MANAGE_TRANSACTION_GL, TransactionGeneralLedgerPage.class);
		transactionGL.setActive(false);
		menuItems.add(transactionGL);

		// Add Transaction GL
		final MenuEntry approveTransactionGL = new MenuEntry("left.menu.approveTransactionGL",
			BtpnConstants.PRIV_UI_APPROVE_TRANSACTION_GL, ApproveTransactionGeneralLedgerPage.class);
		approveTransactionGL.setActive(false);
		menuItems.add(approveTransactionGL);

		// Add Salary Upload to left menu
		final MenuEntry salaryUpload = new MenuEntry("left.menu.bulk.upload", 
			"UI_BATCH_UPLOAD_MAKER_PRIVILEGE", SalaryUploadPage.class);
		salaryUpload.setActive(false);
		menuItems.add(salaryUpload);

		// Add Search Salary upload to left menu
		final MenuEntry searchSalaryUpload = new MenuEntry("left.menu.bulk.search",
			"UI_BATCH_UPLOAD_CHECKER_PRIVILEGE", SearchSalaryDataPage.class);
		searchSalaryUpload.setActive(false);
		menuItems.add(searchSalaryUpload);

		// Add Transaction Reversal Page to left menu
		final MenuEntry transactionReversalPage = new MenuEntry("left.menu.transactionReversal",
			BtpnConstants.PRIV_UI_TRANSACTION_REVERSAL, TransactionReversalPage.class);
		transactionReversalPage.setActive(false);
		menuItems.add(transactionReversalPage);

		// Add Approve Transaction Reversal Page to left menu
		final MenuEntry approveTransactionReversalPage = new MenuEntry("left.menu.approveTransactionReversal",
			BtpnConstants.PRIV_UI_APPROVE_TRANSACTION_REVERSAL, ApproveTxnReversalPage.class);
		approveTransactionReversalPage.setActive(false);
		menuItems.add(approveTransactionReversalPage);

		// Add Approve MSISDN Page to left menu
		final MenuEntry approveMsisdnPage = new MenuEntry("left.menu.approveMsisdn",
			BtpnConstants.PRIV_CHANGE_MSISDN_CHECKER, ApproveMsisdnPage.class);
		approveMsisdnPage.setActive(false);
		menuItems.add(approveMsisdnPage);

		/*// jadi satu : UI_BATCH_UPLOAD_MAKER_PRIVILEGE
		final MenuEntry consumerUpload = new MenuEntry("left.menu.consumerUpload",
			BtpnConstants.PRIV_UI_CUSTOMER_UPLOAD, RegUploadPage.class);
		consumerUpload.setActive(false);
		menuItems.add(consumerUpload);
		*/
		
		/*//jadi satu di : UI_BATCH_UPLOAD_CHECKER_PRIVILEGE
		final MenuEntry searchConsumerRegData = new MenuEntry("left.menu.searchConsumerRegData",
			BtpnConstants.PRIV_UI_SEARCH_CUSTOMER_DATA, SearchRegDataPage.class);
		searchConsumerRegData.setActive(false);
		menuItems.add(searchConsumerRegData);
		*/

		// Add Manage Interest Page
		final MenuEntry mgIntEntry = new MenuEntry("left.menu.manageInterest",
			BtpnConstants.PRIV_INTEREST_MAKER, ManageInterestPage.class);
		mgIntEntry.setActive(false);
		menuItems.add(mgIntEntry);
		
		// Add Manage Interest Tax Page
		final MenuEntry mgIntTaxEntry = new MenuEntry("left.menu.manageInterestTax",
			BtpnConstants.PRIV_INTEREST_TAX_MAKER, ManageInterestTaxPage.class);
		mgIntEntry.setActive(false);
		menuItems.add(mgIntTaxEntry);
		
		// Add Manage Interest Approve Page
		final MenuEntry mgIntAppEntry = new MenuEntry("left.menu.manageInterestApprove",
			BtpnConstants.PRIV_INTEREST_CHECKER, ManageInterestApprovePage.class);
		mgIntAppEntry.setActive(false);
		menuItems.add(mgIntAppEntry);
		
		// Add Manage Interest Tax Approve Page
		final MenuEntry mgIntTaxAppEntry = new MenuEntry("left.menu.manageInterestTaxApprove",
			BtpnConstants.PRIV_INTEREST_TAX_CHECKER, ManageInterestTaxApprovePage.class);
		mgIntTaxAppEntry.setActive(false);
		menuItems.add(mgIntTaxAppEntry);
		
		
		final MenuEntry holidayCalender = new MenuEntry("left.menu.holidayCalender",
			BtpnConstants.PRIV_UI_HOLIDAY_CALENDAR, HolidayCalender.class);
		holidayCalender.setActive(false);
		menuItems.add(holidayCalender);

		// Approve Holiday Calendar Menu
		final MenuEntry approveHolidayCalendar = new MenuEntry("left.menu.pproveHolidays",
			BtpnConstants.PRIV_UI_HOLIDAY_CALENDAR_APPROVAL, ApproveHolidayCalendarPage.class);
		approveHolidayCalendar.setActive(false);
		menuItems.add(approveHolidayCalendar);

		// FIXME Approve Customer data
//		final MenuEntry approveCustomerData = new MenuEntry("left.menu.approveCustomerData",
//			BtpnConstants.PRIV_UI_APPROVE_CUSTOMER_DATA, ApproveCustomerData.class);
//		approveCustomerData.setActive(false);
//		menuItems.add(approveCustomerData);
		
		// FIXME Approve Customer data
//		final MenuEntry approveAllAgentsData = new MenuEntry("left.menu.approveAgentData",
//			BtpnConstants.PRIV_UI_APPROVE_ALL_AGENTS_DATA, ApproveCustomerData.class);
//		approveCustomerData.setActive(false);
//		menuItems.add(approveAllAgentsData);

		// Help
		final MenuEntry addHelp = new MenuEntry("left.menu.addHelp", BtpnConstants.PRIV_UI_ADD_HELP, AddHelpPage.class);
		addHelp.setActive(false);
		menuItems.add(addHelp);

		return menuItems;
	}
}
