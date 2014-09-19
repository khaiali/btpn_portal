package com.sybase365.mobiliser.web.cst.pages.reports;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.MenuEntry;
import com.sybase365.mobiliser.web.common.reports.AbstractReportEnabledPage;
import com.sybase365.mobiliser.web.common.reports.custom.BatchContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.cst.pages.BaseCstPage;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.CommissionContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.DailyTransactionContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.SvaBalanceContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.TransactionContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.ActiveCustomerContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.CurrentUserLoginContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.CustomerActivityContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.CustomerActivityStatisticsContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.CustomerAuthSummaryContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.CustomerLoginStatisticsContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.DeviceAuthExceptionContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.EmployeeActivityContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.EmployeeActivityStatisticsContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.LoginUsageContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.RegistrationContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.SuspiciousTransactionContextReport;
import com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking.TransactionsContextReport;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_REPORTS)
public class CSTReportsMenuGroup extends BaseCstPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(CSTReportsMenuGroup.class);

    public CSTReportsMenuGroup() {
	super();
	setReportPage(true);
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public CSTReportsMenuGroup(final PageParameters parameters) {
	super(parameters);
	setReportPage(true);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	// always add a home page as a place for the 'select a report'
	// instruction
	entries.add(new MenuEntry("menu.report.home",
		Constants.PRIV_CST_REPORTS,
		isBatchPage() ? DefaultBatchCSTReportPage.class
			: DefaultDynamicCSTReportPage.class));

	return entries;
    }

    @Override
    public AbstractReportEnabledPage getDynamicReportPageInstance(
	    MobiliserReportParameter report, IContextReport customReportInfo) {

	return new DynamicCSTReportPage(report, customReportInfo);
    }

    @Override
    public AbstractReportEnabledPage getBatchReportPageInstance(
	    MobiliserReportParameter report, IContextReport customReportInfo) {

	return new BatchListCSTReportPage(report, customReportInfo);
    }

    @Override
    public Map<String, IContextReport> getContextReportImplementations() {

	Map<String, IContextReport> contextReports = new HashMap<String, IContextReport>();

	// initialise contextReports
	// context report implementations are custom implementations for the
	// otherwise generic reports, which allow to pre-set or pre-define the
	// value of some report parameters
	contextReports = new HashMap<String, IContextReport>();

	// Distribution Partner Reports Context
	BatchContextReport errOverviewCtxReport = new BatchContextReport(
		"Error Overview");
	contextReports.put("Error Overview", errOverviewCtxReport);

	BatchContextReport feeCommOverviewCtxReport = new BatchContextReport(
		"Fees and Commission Overview");
	contextReports.put("Fees and Commission Overview",
		feeCommOverviewCtxReport);

	BatchContextReport spBalanceOverviewCtxReport = new BatchContextReport(
		"SP Balance Monitor Overview");
	contextReports.put("SP Balance Monitor Overview",
		spBalanceOverviewCtxReport);

	BatchContextReport svaBalanceCtxReport = new BatchContextReport(
		"SVA Balance Detail");
	contextReports.put("SVA Balance Detail", svaBalanceCtxReport);

	CommissionContextReport commCtxReport = new CommissionContextReport(
		getMobiliserWebSession());
	contextReports.put(commCtxReport.getReportName(), commCtxReport);

	DailyTransactionContextReport dailyTxnCtxReport = new DailyTransactionContextReport(
		getMobiliserWebSession());
	contextReports
		.put(dailyTxnCtxReport.getReportName(), dailyTxnCtxReport);

	SvaBalanceContextReport svaBlcCtxReport = new SvaBalanceContextReport(
		getMobiliserWebSession());
	contextReports.put(svaBlcCtxReport.getReportName(), svaBlcCtxReport);

	TransactionContextReport txnCtxReport = new TransactionContextReport(
		getMobiliserWebSession());
	contextReports.put(txnCtxReport.getReportName(), txnCtxReport);

	BatchContextReport txnDetailCtxReport = new BatchContextReport(
		"Transaction Detail");
	contextReports.put("Transaction Detail", txnDetailCtxReport);

	BatchContextReport txnOverviewCtxReport = new BatchContextReport(
		"Transaction Overview");
	contextReports.put("Transaction Overview", txnOverviewCtxReport);

	// mBanking Reports Context
	ActiveCustomerContextReport activeCustomerCtxReport = new ActiveCustomerContextReport(
		getMobiliserWebSession());
	contextReports.put(activeCustomerCtxReport.getReportName(),
		activeCustomerCtxReport);

	CurrentUserLoginContextReport currentUserLoginCtxReport = new CurrentUserLoginContextReport(
		getMobiliserWebSession());
	contextReports.put(currentUserLoginCtxReport.getReportName(),
		currentUserLoginCtxReport);

	CustomerActivityContextReport customerActivityCtxReport = new CustomerActivityContextReport(
		getMobiliserWebSession());
	contextReports.put(customerActivityCtxReport.getReportName(),
		customerActivityCtxReport);

	CustomerActivityStatisticsContextReport customerActivityStatsCtxReport = new CustomerActivityStatisticsContextReport(
		getMobiliserWebSession());
	contextReports.put(customerActivityStatsCtxReport.getReportName(),
		customerActivityStatsCtxReport);

	CustomerAuthSummaryContextReport customerAuthSummaryCtxReport = new CustomerAuthSummaryContextReport(
		getMobiliserWebSession());
	contextReports.put(customerAuthSummaryCtxReport.getReportName(),
		customerAuthSummaryCtxReport);

	CustomerLoginStatisticsContextReport customerLoginStatisticsCtxReport = new CustomerLoginStatisticsContextReport(
		getMobiliserWebSession());
	contextReports.put(customerLoginStatisticsCtxReport.getReportName(),
		customerLoginStatisticsCtxReport);

	DeviceAuthExceptionContextReport deviceAuthExceptionCtxReport = new DeviceAuthExceptionContextReport(
		getMobiliserWebSession());
	contextReports.put(deviceAuthExceptionCtxReport.getReportName(),
		deviceAuthExceptionCtxReport);

	EmployeeActivityContextReport employeeActivityCtxReport = new EmployeeActivityContextReport(
		getMobiliserWebSession());
	contextReports.put(employeeActivityCtxReport.getReportName(),
		employeeActivityCtxReport);

	EmployeeActivityStatisticsContextReport employeeActivityStatsCtxReport = new EmployeeActivityStatisticsContextReport(
		getMobiliserWebSession());
	contextReports.put(employeeActivityStatsCtxReport.getReportName(),
		employeeActivityStatsCtxReport);

	LoginUsageContextReport loginUsageCtxReport = new LoginUsageContextReport(
		getMobiliserWebSession());
	contextReports.put(loginUsageCtxReport.getReportName(),
		loginUsageCtxReport);

	RegistrationContextReport registrationCtxReport = new RegistrationContextReport(
		getMobiliserWebSession());
	contextReports.put(registrationCtxReport.getReportName(),
		registrationCtxReport);

	BatchContextReport retiredCtxReport = new BatchContextReport(
		"Retired/Restricted MSISDN");
	contextReports.put("Retired/Restricted MSISDN", retiredCtxReport);

	SuspiciousTransactionContextReport suspiciousTxnCtxReport = new SuspiciousTransactionContextReport(
		getMobiliserWebSession());
	contextReports.put(suspiciousTxnCtxReport.getReportName(),
		suspiciousTxnCtxReport);

	TransactionsContextReport transactionsCtxReport = new TransactionsContextReport(
		getMobiliserWebSession());
	contextReports.put(transactionsCtxReport.getReportName(),
		transactionsCtxReport);

	return contextReports;
    }
}
