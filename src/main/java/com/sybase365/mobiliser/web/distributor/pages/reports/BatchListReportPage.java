package com.sybase365.mobiliser.web.distributor.pages.reports;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteJobAndHistoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteJobAndHistoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.panels.BatchReportOverviewPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DPP_REPORTS)
public class BatchListReportPage extends ReportsMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(BatchListReportPage.class);

    MobiliserReportParameter report;
    private IContextReport customReportInfo;

    public BatchListReportPage(MobiliserReportParameter report,
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
	BatchReportOverviewPanel<BatchListReportPage> boP = new BatchReportOverviewPanel<BatchListReportPage>(
		"reportOverviewPanel", report, this) {
	    protected void editReportJob(Job job,
		    MobiliserReportParameter report, MobiliserBasePage basePage) {
		setResponsePage(new BatchEditReportPage(job, report, basePage,
			customReportInfo));
	    }

	    protected void removeReportJob(Job job,
		    MobiliserReportParameter report, MobiliserBasePage basePage) {
		try {
		    DeleteJobAndHistoryRequest request = basePage
			    .getNewMobiliserRequest(DeleteJobAndHistoryRequest.class);
		    request.setJobId(job.getJobId());

		    DeleteJobAndHistoryResponse response = basePage.wsJobClient
			    .deleteJobAndHistory(request);

		    if (!basePage.evaluateMobiliserResponse(response)) {
			LOG.warn("# An error occurred while deleting the job");
			return;
		    }

		} catch (Exception e) {
		    LOG.error(
			    "# An error occurred while deleting job [{}] from the job list",
			    job.getJobId(), e);

		    error(getLocalizer().getString("report.batch.remove.error",
			    this));
		}
	    }

	    protected void addReportJob(MobiliserReportParameter report,
		    MobiliserBasePage basePage) {
		setResponsePage(new BatchAddReportPage(null, report, basePage,
			customReportInfo));
	    }
	};
	this.addOrReplace(boP);

    }
}