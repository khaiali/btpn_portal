package com.sybase365.mobiliser.web.common.dataproviders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.GetJobsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetJobsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Job;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class ReportJobDataProvider extends JobDataProvider {

    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(ReportJobDataProvider.class);

    private Long customerId;
    private String reportName;

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public ReportJobDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, Long customerId,
	    String reportName) {
	super(defaultSortProperty, mobBasePage);
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
	this.customerId = customerId;
	this.reportName = reportName;
    }

    @Override
    public void loadJobs() throws DataProviderLoadException {

	try {
	    GetJobsRequest request = mobBasePage
		    .getNewMobiliserRequest(GetJobsRequest.class);

	    GetJobsResponse response = getMobiliserBasePage().wsJobClient
		    .getJobs(request);

	    for (Job cur : response.getJob()) {
		if (cur.getImplementationUrl().toLowerCase()
			.contains("reportjob")) {

		    if (cur.getParameters().contains(
			    "\"owner\":" + customerId.toString())
			    && cur.getParameters().contains(
				    "\"name\":\"" + reportName + "\""))
			this.jobEntries.add(cur);
		}
	    }

	} catch (Exception e) {
	    throw new DataProviderLoadException();
	}
    }
}
