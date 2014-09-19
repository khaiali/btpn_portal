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
import com.sybase365.mobiliser.web.common.reports.custom.BaseContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReportParameter;
import com.sybase365.mobiliser.web.common.reports.panels.ParameterEntryPanel;
import com.sybase365.mobiliser.web.common.reports.panels.helpers.FindMerchantDealerHelperPanel;
import com.sybase365.mobiliser.web.common.reports.panels.helpers.UseCaseHelperPanel;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class CommissionContextReport extends BaseContextReport implements
	IContextReport {

    private static final long serialVersionUID = 1L;

    private Map<String, IContextReportParameter> contextParameters;

    public CommissionContextReport(final MobiliserWebSession webSession) {

	// compile the list of pre-set parameters for this report
	this.contextParameters = new HashMap<String, IContextReportParameter>();

	// Dealer ID
	IContextReportParameter dealerId = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    private FindMerchantDealerHelperPanel helperPanel;

	    @Override
	    public String getName() {
		return "Dealer ID";
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
		if (helperPanel != null) {
		    return helperPanel.getCustomerList();
		} else {
		    return Collections.EMPTY_LIST;
		}
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

		helperPanel = new FindMerchantDealerHelperPanel(
			"dynValueHelper", basePage, parameterEntryPanel,
			parameter, valueComp);
		return helperPanel;
	    }
	};
	this.contextParameters.put(dealerId.getName(), dealerId);

	// Usecase ID
	IContextReportParameter useCaseId = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    private UseCaseHelperPanel helperPanel;

	    @Override
	    public String getName() {
		return "Usecase ID";
	    }

	    @Override
	    public Class<?> getType() {
		return String.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return null;
	    }

	    @Override
	    public List<?> getValueList(Component component,
		    ILookupMapUtility lookupMapUtility) {
		return null;
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

		helperPanel = new UseCaseHelperPanel("dynValueHelper",
			basePage, parameterEntryPanel, parameter, valueComp);
		return helperPanel;
	    }
	};
	this.contextParameters.put(useCaseId.getName(), useCaseId);
    }

    @Override
    public String getReportName() {

	return "Commission";
    }

    @Override
    public Map<String, IContextReportParameter> getContextParameters() {

	return this.contextParameters;
    }
}
