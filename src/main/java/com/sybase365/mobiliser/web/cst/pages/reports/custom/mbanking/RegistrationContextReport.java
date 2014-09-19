package com.sybase365.mobiliser.web.cst.pages.reports.custom.mbanking;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.custom.BatchContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReportParameter;
import com.sybase365.mobiliser.web.common.reports.panels.ParameterEntryPanel;
import com.sybase365.mobiliser.web.common.reports.panels.helpers.FindCustomerHelperPanel;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class RegistrationContextReport extends BatchContextReport implements
	IContextReport {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(RegistrationContextReport.class);

    private Map<String, IContextReportParameter> contextParameters;

    public RegistrationContextReport(final MobiliserWebSession webSession) {

	// compile the list of pre-set parameters for this report
	this.contextParameters = new HashMap<String, IContextReportParameter>();

	// Affiliate Bank (OrgUnit)
	IContextReportParameter orgUnit = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getName() {
		return "Affiliate Bank";
	    }

	    @Override
	    public Class<?> getType() {
		return List.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return "All";
	    }

	    @Override
	    public List<?> getValueList(Component component,
		    ILookupMapUtility lookupMapUtility) {
		try {
		    return getChoiceList(
			    "orgunits",
			    lookupMapUtility,
			    component,
			    Boolean.TRUE,
			    true,
			    Arrays.asList(new KeyValue[] { new KeyValue<String, String>(
				    "All", "All") }), null);
		} catch (Exception e) {
		    LOG.warn(
			    "There was a problem loading orgunit lookup values: {}",
			    e.getMessage());
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

		return null;
	    }
	};

	this.contextParameters.put(orgUnit.getName(), orgUnit);

	// Customer Type
	IContextReportParameter customerType = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getName() {
		return "Customer Type";
	    }

	    @Override
	    public Class<?> getType() {
		return List.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return "All";
	    }

	    @Override
	    public List<?> getValueList(Component component,
		    ILookupMapUtility lookupMapUtility) {

		try {
		    return getChoiceList(
			    "customertypes",
			    lookupMapUtility,
			    component,
			    Boolean.TRUE,
			    true,
			    Arrays.asList(new KeyValue[] { new KeyValue<String, String>(
				    "All", "All") }), null);
		} catch (Exception e) {
		    LOG.warn(
			    "There was a problem loading customer type lookup values: {}",
			    e.getMessage());
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

		return null;
	    }
	};

	this.contextParameters.put(customerType.getName(), customerType);

	// Channel
	IContextReportParameter serviceChannel = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getName() {
		return "Channel";
	    }

	    @Override
	    public Class<?> getType() {
		return List.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return "All";
	    }

	    @Override
	    public List<?> getValueList(Component component,
		    ILookupMapUtility lookupMapUtility) {
		try {
		    return getChoiceList(
			    "servicechannels",
			    lookupMapUtility,
			    component,
			    Boolean.TRUE,
			    true,
			    Arrays.asList(new KeyValue[] {
				    new KeyValue<String, String>("All", "All"),
				    new KeyValue<String, String>(
					    "mobiliser-web", "Web") }), null);
		} catch (Exception e) {
		    LOG.warn(
			    "There was a problem loading service channel lookup values: {}",
			    e.getMessage());
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

		return null;
	    }
	};

	this.contextParameters.put(serviceChannel.getName(), serviceChannel);

	IContextReportParameter customer = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    private FindCustomerHelperPanel helperPanel;

	    @Override
	    public String getName() {
		return "Customer";
	    }

	    @Override
	    public Class<?> getType() {
		return String.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return "All";
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

		helperPanel = new FindCustomerHelperPanel("dynValueHelper",
			basePage, parameterEntryPanel, parameter, valueComp);

		return helperPanel;
	    }
	};

	this.contextParameters.put(customer.getName(), customer);
    }

    @Override
    public String getReportName() {
	return "Registration";
    }

    @Override
    public Map<String, IContextReportParameter> getContextParameters() {
	return this.contextParameters;
    }

}
