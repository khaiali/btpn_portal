package com.sybase365.mobiliser.web.distributor.pages.reports;

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
import com.sybase365.mobiliser.web.distributor.pages.BaseDistributorPage;
import com.sybase365.mobiliser.web.distributor.pages.reports.custom.CommissionContextReport;
import com.sybase365.mobiliser.web.distributor.pages.reports.custom.DailyTransactionContextReport;
import com.sybase365.mobiliser.web.distributor.pages.reports.custom.SvaBalanceContextReport;
import com.sybase365.mobiliser.web.distributor.pages.reports.custom.TransactionContextReport;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DPP_REPORTS)
public class ReportsMenuGroup extends BaseDistributorPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(ReportsMenuGroup.class);

    public ReportsMenuGroup() {
	super();
	setReportPage(true);
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public ReportsMenuGroup(final PageParameters parameters) {
	super(parameters);
	setReportPage(true);
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();

	// always add a home page as a place for the 'select a report'
	// instruction
	entries.add(new MenuEntry("menu.report.home",
		Constants.PRIV_DPP_REPORTS,
		isBatchPage() ? DefaultBatchReportPage.class
			: DefaultDynamicReportPage.class));

	return entries;
    }

    @Override
    public AbstractReportEnabledPage getDynamicReportPageInstance(
	    MobiliserReportParameter report, IContextReport customReportInfo) {

	return new DynamicReportPage(report, customReportInfo);
    }

    @Override
    public AbstractReportEnabledPage getBatchReportPageInstance(
	    MobiliserReportParameter report, IContextReport customReportInfo) {

	return new BatchListReportPage(report, customReportInfo);
    }

    @Override
    public Map<String, IContextReport> getContextReportImplementations() {

	Map<String, IContextReport> contextReports = new HashMap<String, IContextReport>();

	// initialise contextReports
	// context report implementations are custom implementatios for the
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
		this, getMobiliserWebSession());
	contextReports
		.put(dailyTxnCtxReport.getReportName(), dailyTxnCtxReport);

	SvaBalanceContextReport svaBlcCtxReport = new SvaBalanceContextReport(
		getMobiliserWebSession());
	contextReports.put(svaBlcCtxReport.getReportName(), svaBlcCtxReport);

	TransactionContextReport txnCtxReport = new TransactionContextReport(
		this, getMobiliserWebSession());
	contextReports.put(txnCtxReport.getReportName(), txnCtxReport);

	BatchContextReport txnDetailCtxReport = new BatchContextReport(
		"Transaction Detail");
	contextReports.put("Transaction Detail", txnDetailCtxReport);

	BatchContextReport txnOverviewCtxReport = new BatchContextReport(
		"Transaction Overview");
	contextReports.put("Transaction Overview", txnOverviewCtxReport);

	return contextReports;
    }
}
