package com.sybase365.mobiliser.web.common.reports.custom;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.ReportRequestParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.reports.components.DynamicComponent;
import com.sybase365.mobiliser.web.common.reports.panels.ParameterEntryPanel;

public interface IContextReportParameter extends Serializable {

    String getName();

    Object getValueDefault();

    List<?> getValueList(Component component, ILookupMapUtility lookupMapUtility);

    Class<?> getType();

    boolean allowOverride();

    Panel getHelperPanel(MobiliserBasePage basePage, ParameterEntryPanel panel,
	    ReportRequestParameter param, DynamicComponent val);
}
