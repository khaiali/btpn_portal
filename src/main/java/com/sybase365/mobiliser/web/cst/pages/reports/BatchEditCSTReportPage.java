package com.sybase365.mobiliser.web.cst.pages.reports;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.panels.BatchReportPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_REPORTS)
public class BatchEditCSTReportPage extends CSTReportsMenuGroup {

    private static final long serialVersionUID = 1L;

    private WebPage backPage;
    final MobiliserReportParameter report;
    private IContextReport customReportInfo;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BatchEditCSTReportPage.class);

    private Job job;

    public BatchEditCSTReportPage(final Job job,
	    final MobiliserReportParameter report, WebPage backPage,
	    IContextReport customReportInfo) {
	super();
	this.job = job;
	this.backPage = backPage;
	this.report = report;
	this.customReportInfo = customReportInfo;
	setBatchPage(true);
	initPageComponents();
    }

    protected void initPageComponents() {
	addOrReplace(new BatchReportPanel("jobPanel", job, report, backPage,
		this, customReportInfo));
    }

    @Override
    protected void onBeforeRender() {
	//
	// call the parent render first to build the left menu.
	//
	super.onBeforeRender();
	setBatchPage(true);
    }

    @Override
    public void onPageAttached() {
	LOG.debug("PagedAttached");
	super.onPageAttached();

    }

    @Override
    protected Class getActiveMenu() {
	return this.getClass();
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	//
	// Must not call anything here for this page as this page is intended
	// for user instantiation instead of by wicket.
	//
    }
}
