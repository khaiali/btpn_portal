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

public class CustomerActivityContextReport extends BatchContextReport implements
	IContextReport {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(CustomerActivityContextReport.class);

    private Map<String, IContextReportParameter> contextParameters;

    public CustomerActivityContextReport(final MobiliserWebSession webSession) {

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

	// Customer Activity
	IContextReportParameter activity = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getName() {
		return "Activity";
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
		return Arrays
			.asList(new KeyValue[] {
				new KeyValue<String, String>("All", "All"),
				new KeyValue<String, String>(
					"Accept Bank Terms & Conditions",
					"Accept Bank Terms & Conditions"),
				new KeyValue<String, String>(
					"Accept Carrier Terms & Conditions",
					"Accept Carrier Terms & Conditions"),
				new KeyValue<String, String>("Account Balance",
					"Account Balance"),
				new KeyValue<String, String>("Account Listing",
					"Account Listing"),
				new KeyValue<String, String>(
					"Add Contact Point",
					"Add Contact Point"),
				new KeyValue<String, String>(
					"Add Mobile Alert", "Add Mobile Alert"),
				new KeyValue<String, String>(
					"Add Mobile Device",
					"Add Mobile Device"),
				new KeyValue<String, String>("Add Profile",
					"Add Profile"),
				new KeyValue<String, String>("Cancel Payment",
					"Cancel Payment"),
				new KeyValue<String, String>("Cancel Transfer",
					"Cancel Transfer"),
				new KeyValue<String, String>(
					"Delete Contact Point",
					"Delete Contact Point"),
				new KeyValue<String, String>(
					"Delete Mobile Alert",
					"Delete Mobile Alert"),
				new KeyValue<String, String>(
					"Delete Mobile Device",
					"Delete Mobile Device"),
				new KeyValue<String, String>(
					"Device Authentication",
					"Device Authentication"),
				new KeyValue<String, String>(
					"Get Financial Product Information",
					"Get Financial Product Information"),
				new KeyValue<String, String>(
					"Get FX Rate Information",
					"Get FX Rate Information"),
				new KeyValue<String, String>("Get Payments",
					"Get Payments"),
				new KeyValue<String, String>(
					"Get Stop Summary", "Get Stop Summary"),
				new KeyValue<String, String>("Get Transfers",
					"Get Transfers"),
				new KeyValue<String, String>("Help", "Help"),
				new KeyValue<String, String>("List Payees",
					"List Payees"),
				new KeyValue<String, String>("Locate ATM",
					"Locate ATM"),
				new KeyValue<String, String>(
					"Locate Bank Branch",
					"Locate Bank Branch"),
				new KeyValue<String, String>("Lock", "Lock"),
				new KeyValue<String, String>("Login", "Login"),
				new KeyValue<String, String>(
					"Mini Account Statement",
					"Mini Account Statement"),
				new KeyValue<String, String>(
					"Mobile Funds Transfer",
					"Mobile Funds Transfer"),
				new KeyValue<String, String>("Mobile Payment",
					"Mobile Payment"),
				new KeyValue<String, String>("Modify Account",
					"Modify Account"),
				new KeyValue<String, String>(
					"Modify Contact Point",
					"Modify Contact Point"),
				new KeyValue<String, String>(
					"Modify Mobile Alert",
					"Modify Mobile Alert"),
				new KeyValue<String, String>(
					"Modify Mobile Device",
					"Modify Mobile Device"),
				new KeyValue<String, String>("Modify Payment",
					"Modify Payment"),
				new KeyValue<String, String>("Modify Profile",
					"Modify Profile"),
				new KeyValue<String, String>("Modify Transfer",
					"Modify Transfer"),
				new KeyValue<String, String>(
					"Place Stop Check Payment",
					"Place Stop Check Payment"),
				new KeyValue<String, String>(
					"Reject Bank Terms & Conditions",
					"Reject Bank Terms & Conditions"),
				new KeyValue<String, String>(
					"Reject Carrier Terms & Conditions",
					"Reject Carrier Terms & Conditions"),
				new KeyValue<String, String>("Resume", "Resume"),
				new KeyValue<String, String>(
					"SSO Authentication",
					"SSO Authentication"),
				new KeyValue<String, String>("Stop", "Stop"),
				new KeyValue<String, String>("Unlock", "Unlock") });
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

	this.contextParameters.put(activity.getName(), activity);
    }

    @Override
    public String getReportName() {
	return "Customer Activity";
    }

    @Override
    public Map<String, IContextReportParameter> getContextParameters() {
	return this.contextParameters;
    }

}
