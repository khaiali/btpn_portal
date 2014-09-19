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

public class LoginUsageContextReport extends BatchContextReport implements
	IContextReport {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(LoginUsageContextReport.class);

    private Map<String, IContextReportParameter> contextParameters;

    private static final List<KeyValue> SELECTABLE_TIMES = Arrays
	    .asList(new KeyValue[] {
		    new KeyValue<String, String>("00:00", "00:00"),
		    new KeyValue<String, String>("00:30", "00:30"),
		    new KeyValue<String, String>("01:00", "01:00"),
		    new KeyValue<String, String>("01:30", "01:30"),
		    new KeyValue<String, String>("02:00", "02:00"),
		    new KeyValue<String, String>("02:30", "02:30"),
		    new KeyValue<String, String>("03:00", "03:00"),
		    new KeyValue<String, String>("03:30", "03:00"),
		    new KeyValue<String, String>("04:00", "04:30"),
		    new KeyValue<String, String>("04:30", "04:00"),
		    new KeyValue<String, String>("05:00", "05:00"),
		    new KeyValue<String, String>("05:30", "05:30"),
		    new KeyValue<String, String>("06:00", "06:00"),
		    new KeyValue<String, String>("06:30", "06:30"),
		    new KeyValue<String, String>("07:00", "07:00"),
		    new KeyValue<String, String>("07:30", "07:30"),
		    new KeyValue<String, String>("08:00", "08:00"),
		    new KeyValue<String, String>("08:30", "08:30"),
		    new KeyValue<String, String>("09:00", "09:00"),
		    new KeyValue<String, String>("09:30", "09:30"),
		    new KeyValue<String, String>("10:00", "10:00"),
		    new KeyValue<String, String>("10:30", "10:30"),
		    new KeyValue<String, String>("11:00", "11:00"),
		    new KeyValue<String, String>("11:30", "11:30"),
		    new KeyValue<String, String>("12:00", "12:00"),
		    new KeyValue<String, String>("12:30", "12:30"),
		    new KeyValue<String, String>("13:00", "13:00"),
		    new KeyValue<String, String>("13:30", "13:00"),
		    new KeyValue<String, String>("14:00", "14:30"),
		    new KeyValue<String, String>("14:30", "14:00"),
		    new KeyValue<String, String>("15:00", "15:00"),
		    new KeyValue<String, String>("15:30", "15:30"),
		    new KeyValue<String, String>("16:00", "16:00"),
		    new KeyValue<String, String>("16:30", "16:30"),
		    new KeyValue<String, String>("17:00", "17:00"),
		    new KeyValue<String, String>("17:30", "17:30"),
		    new KeyValue<String, String>("18:00", "18:00"),
		    new KeyValue<String, String>("18:30", "18:30"),
		    new KeyValue<String, String>("19:00", "19:00"),
		    new KeyValue<String, String>("19:30", "19:30"),
		    new KeyValue<String, String>("20:00", "20:00"),
		    new KeyValue<String, String>("20:30", "20:30"),
		    new KeyValue<String, String>("21:00", "21:00"),
		    new KeyValue<String, String>("21:30", "21:30"),
		    new KeyValue<String, String>("22:00", "22:00"),
		    new KeyValue<String, String>("22:30", "22:30"),
		    new KeyValue<String, String>("23:00", "23:00"),
		    new KeyValue<String, String>("23:30", "23:30") });

    public LoginUsageContextReport(final MobiliserWebSession webSession) {

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
		return List.class;
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

	IContextReportParameter startTime = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getName() {
		return "Start Time";
	    }

	    @Override
	    public Class<?> getType() {
		return List.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return "00:00";
	    }

	    @Override
	    public List<?> getValueList(Component component,
		    ILookupMapUtility lookupMapUtility) {
		return SELECTABLE_TIMES;
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

	this.contextParameters.put(startTime.getName(), startTime);

	IContextReportParameter endTime = new IContextReportParameter() {

	    private static final long serialVersionUID = 1L;

	    private FindCustomerHelperPanel helperPanel;

	    @Override
	    public String getName() {
		return "End Time";
	    }

	    @Override
	    public Class<?> getType() {
		return List.class;
	    }

	    @Override
	    public Object getValueDefault() {
		return "24:00";
	    }

	    @Override
	    public List<?> getValueList(Component component,
		    ILookupMapUtility lookupMapUtility) {
		return SELECTABLE_TIMES;
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

	this.contextParameters.put(endTime.getName(), endTime);
    }

    @Override
    public String getReportName() {
	return "Login Usage Pattern";
    }

    @Override
    public Map<String, IContextReportParameter> getContextParameters() {
	return this.contextParameters;
    }

}
