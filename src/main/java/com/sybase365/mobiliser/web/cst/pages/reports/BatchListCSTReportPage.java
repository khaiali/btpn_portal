package com.sybase365.mobiliser.web.cst.pages.reports;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.panels.BatchReportOverviewPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_REPORTS)
public class BatchListCSTReportPage extends CSTReportsMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(BatchListCSTReportPage.class);

    MobiliserReportParameter report;

    private IContextReport customReportInfo;

    public BatchListCSTReportPage(MobiliserReportParameter report,
	    IContextReport customReportInfo) {
	super();
	setBatchPage(true);
	this.report = report;
	this.customReportInfo = customReportInfo;
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	//
	// Must not call anything here for this page as this page is intended
	// for user instantiation instead of by wicket.
	//
    }

    @Override
    protected void onBeforeRender() {

	super.onBeforeRender();
	setBatchPage(true);
	BatchReportOverviewPanel<BatchListCSTReportPage> boP = new BatchReportOverviewPanel<BatchListCSTReportPage>(
		"reportOverviewPanel", report, this) {

	    private static final long serialVersionUID = 1L;

	    protected void editReportJob(Job job,
		    MobiliserReportParameter report, MobiliserBasePage basePage) {
		setResponsePage(new BatchEditCSTReportPage(job, report,
			basePage, customReportInfo));
	    }

	    protected void addReportJob(MobiliserReportParameter report,
		    MobiliserBasePage basePage) {
		setResponsePage(new BatchAddCSTReportPage(null, report,
			basePage, customReportInfo));
	    }

	};
	this.addOrReplace(boP);

    }
}