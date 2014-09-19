package com.sybase365.mobiliser.web.distributor.pages.reports.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.custom.BaseContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReportParameter;
import com.sybase365.mobiliser.web.common.reports.panels.ParameterEntryPanel;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class TransactionContextReport extends BaseContextReport implements
	IContextReport {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(TransactionContextReport.class);

    private Map<String, IContextReportParameter> contextParameters;

    public TransactionContextReport(final MobiliserBasePage page,
	    final MobiliserWebSession webSession) {

	// compile the list of pre-set parameters for this report
	this.contextParameters = new HashMap<String, IContextReportParameter>();

	// Agent ID
	IContextReportParameter agentId = new IContextReportParameter() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getName() {
		return "Agent ID";
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

	// Dealer ID
	IContextReportParameter dealerId = new IContextReportParameter() {
	    private static final long serialVersionUID = 1L;

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

		List<KeyValue<String, String>> dealerL = new ArrayList<KeyValue<String, String>>();

		try {
		    FindHierarchicalCustomerRequest request = page
			    .getNewMobiliserRequest(FindHierarchicalCustomerRequest.class);
		    request.setAgentId(webSession.getLoggedInCustomer()
			    .getCustomerId());

		    List<CustomerBean> dealers = page.findCustomer(request);
		    if (dealers != null && !dealers.isEmpty()) {
			for (CustomerBean cb : dealers) {
			    dealerL.add(new KeyValue<String, String>(String
				    .valueOf(cb.getId()), cb.getDisplayName()));
			}
		    }
		} catch (Exception e) {
		    LOG.warn("Could not retrieve dealer information.", e);
		    return null;
		}

		return dealerL;
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
		return null;
	    }
	};

	this.contextParameters.put(dealerId.getName(), dealerId);
    }

    @Override
    public String getReportName() {

	return "Transaction";
    }

    @Override
    public Map<String, IContextReportParameter> getContextParameters() {

	return this.contextParameters;
    }
}
