package com.sybase365.mobiliser.web.cst.pages.reports.custom;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.custom.BatchContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReportParameter;
import com.sybase365.mobiliser.web.common.reports.panels.ParameterEntryPanel;
import com.sybase365.mobiliser.web.common.reports.panels.helpers.FindCustomerHelperPanel;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class SvaBalanceContextReport extends BatchContextReport implements
	IContextReport {

    private static final long serialVersionUID = 1L;

    private Map<String, IContextReportParameter> contextParameters;

    private static final String REPORT_NAME = "SVA Balance Detail";

    public SvaBalanceContextReport(final MobiliserWebSession webSession) {

	super(REPORT_NAME);

	// compile the list of pre-set parameters for this report
	this.contextParameters = new HashMap<String, IContextReportParameter>();

	// Customer ID
	IContextReportParameter agentId = new IContextReportParameter() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getName() {
		return "Customer ID";
	    }

	    @Override
	    public Class<?> getType() {
		return List.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return null;
	    }

	    @Override
	    public List<?> getValueList(Component component,
		    ILookupMapUtility lookupMapUtility) {
		return Collections.EMPTY_LIST;
	    }

	    @Override
	    public boolean allowOverride() {
		return true;
	    }

	    @Override
	    public Panel getHelperPanel(final MobiliserBasePage basePage,
		    final ParameterEntryPanel parameterEntryPanel,
		    final ReportRequestParameter parameter,
		    final DynamicComponent valueComp) {
		return new FindCustomerHelperPanel("dynValueHelper", basePage,
			parameterEntryPanel, parameter, valueComp);
	    }
	};
	this.contextParameters.put(agentId.getName(), agentId);
    }

    @Override
    public String getReportName() {

	return this.reportName;
    }

    @Override
    public Map<String, IContextReportParameter> getContextParameters() {

	return this.contextParameters;
    }
}
