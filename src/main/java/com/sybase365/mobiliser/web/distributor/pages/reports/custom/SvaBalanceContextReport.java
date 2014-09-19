package com.sybase365.mobiliser.web.distributor.pages.reports.custom;

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
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class SvaBalanceContextReport extends BatchContextReport implements
	IContextReport {

    private static final long serialVersionUID = 1L;

    private Map<String, IContextReportParameter> contextParameters;

    public SvaBalanceContextReport(final MobiliserWebSession webSession) {

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
		return Long.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return new Long(webSession.getLoggedInCustomer()
			.getCustomerId());
	    }

	    @Override
	    public List<?> getValueList(Component component,
		    ILookupMapUtility lookupMapUtility) {
		return null;
	    }

	    @Override
	    public boolean allowOverride() {
		return false;
	    }

	    @Override
	    public Panel getHelperPanel(final MobiliserBasePage basePage,
		    final ParameterEntryPanel parameterEntryPanel,
		    final ReportRequestParameter parameter,
		    final DynamicComponent valueComp) {
		return null;
	    }
	};
	this.contextParameters.put(agentId.getName(), agentId);
    }

    @Override
    public String getReportName() {

	return "SVA Balance Report";
    }

    @Override
    public Map<String, IContextReportParameter> getContextParameters() {

	return this.contextParameters;
    }
}
