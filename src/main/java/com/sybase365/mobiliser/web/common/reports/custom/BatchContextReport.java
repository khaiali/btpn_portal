package com.sybase365.mobiliser.web.common.reports.custom;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchContextReport extends BaseContextReport implements
	IContextReport {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(BatchContextReport.class);

    protected String reportName;

    public BatchContextReport() {
    }

    public BatchContextReport(final String reportName) {
	this.reportName = reportName;
    }

    @Override
    public String getReportName() {
	return this.reportName;
    }

    @Override
    public Map<String, IContextReportParameter> getContextParameters() {
	return null;
    }

    @Override
    public boolean isBatchReport() {
	return true;
    }
}
