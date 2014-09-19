package com.sybase365.mobiliser.web.common.reports.custom;

import java.io.Serializable;
import java.util.Map;

public interface IContextReport extends Serializable {

    String getReportName();

    Map<String, IContextReportParameter> getContextParameters();

    boolean isBatchReport();
}
