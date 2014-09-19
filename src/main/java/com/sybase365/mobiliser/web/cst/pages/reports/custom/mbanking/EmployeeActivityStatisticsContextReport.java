package com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class EmployeeActivityStatisticsContextReport extends
	EmployeeActivityContextReport implements IContextReport {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(EmployeeActivityStatisticsContextReport.class);

    public EmployeeActivityStatisticsContextReport(
	    final MobiliserWebSession webSession) {
	super(webSession);
    }

    @Override
    public String getReportName() {
	return "Employee Activity Statistics";
    }

}
