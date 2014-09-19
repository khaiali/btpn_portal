package com.sybase365.mobiliser.web.common.reports;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.report.ReportListBeanRequest;
import com.sybase365.mobiliser.util.contract.v5_0.report.ReportListBeanResponse;
import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.DynamicMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseLeftMenuView;
import com.sybase365.mobiliser.web.application.pages.BaseApplicationPage;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;

public abstract class AbstractReportEnabledPage extends BaseApplicationPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(AbstractReportEnabledPage.class);

    private boolean isReportPage;

    private boolean isBatchPage;

    private Map<String, IContextReport> contextReports;

    public AbstractReportEnabledPage() {
	super();
    }

    public AbstractReportEnabledPage getDynamicReportPageInstance(
	    MobiliserReportParameter report, IContextReport customReportInfo) {

	// empty default impl;
	return null;
    }

    public AbstractReportEnabledPage getBatchReportPageInstance(
	    MobiliserReportParameter report, IContextReport customReportInfo) {

	// empty default impl;
	return null;
    }

    public Map<String, IContextReport> getContextReportImplementations() {

	// empty default impl;
	return null;
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public AbstractReportEnabledPage(final PageParameters parameters) {
	super(parameters);

    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	this.contextReports = getContextReportImplementations();
    }

    @Override
    protected void onBeforeRender() {
	LOG.debug("onBeforeRender");

	// if we have to deal iwth a report page, we have to render the left
	// menu
	// dynamically
	if (isReportPage()) {
	    setLeftMenu(buildDynamicLeftMenu());
	}

	super.onBeforeRender();
    }

    public void setLeftMenu(LinkedList<IMenuEntry> entries) {
	getMobiliserWebSession().setLeftMenu(entries);
	addOrReplace(new SybaseLeftMenuView("leftMenu",
		new Model<LinkedList<IMenuEntry>>(entries)));
    };

    /**
     * Dynamically create the left menu entries based on the list of reports
     * available from the backend container.
     * 
     * @return List of DynamiceReport menu entries.
     */
    protected LinkedList<IMenuEntry> buildDynamicLeftMenu() {

	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>(this
		.getMobiliserWebSession().getLeftMenu());

	LOG.debug("# Getting list of available reports");

	try {
	    ReportListBeanRequest request = new ReportListBeanRequest();
	    this.prepareMobiliserRequest(request);
	    ReportListBeanResponse response = wsReportClient
		    .getAvailableReports(request);

	    if (response != null) {

		List<MobiliserReportParameter> reports = response
			.getReportElement().getReports();
		//
		// Cleanup reports that may have been removed on the backend.
		//
		Set<Integer> entriesToRemove = new HashSet<Integer>();
		for (int i = 0; i < entries.size(); i++) {
		    boolean found = false;
		    IMenuEntry entry = entries.get(i);
		    if (!entry.getName().equals("menu.report.home")) {
			String name = entry.getName();
			for (MobiliserReportParameter report : reports) {
			    if (report.getName().equals(name)) {
				found = true;
				break;
			    }
			}
			if (!found) {
			    entriesToRemove.add(i);
			}
		    }
		}

		for (Integer index : entriesToRemove) {
		    entries.remove(index.intValue());
		}

		//
		// Generate a complete list for the menu display.
		//
		for (MobiliserReportParameter report : reports) {
		    boolean found = false;
		    String name = report.getName();
		    for (IMenuEntry entry : entries) {
			if (entry.getName().equals(name)) {
			    found = true;
			    break;
			}
		    }
		    if (!found) {

			// this builds the dynamic report page based on the
			// current report
			// the page the menu entry directs to is the dynamically
			// created report page, which's components are build
			// just before rendering, not during initialisation
			Page dynPage = null;
			if (isBatchReport(report, contextReports == null ? null
				: this.contextReports.get(name))
				&& isBatchPage()) {
			    dynPage = getBatchReportPageInstance(report,
				    contextReports == null ? null
					    : this.contextReports.get(name));
			} else if (!isBatchPage()) {

			    // see if there exists a "customized" report entry
			    // we have to pass to the constructor
			    dynPage = getDynamicReportPageInstance(report,
				    contextReports == null ? null
					    : this.contextReports.get(name));
			}

			if (dynPage != null) {
			    entries.add(new DynamicMenuEntry(report.getName(),
				    report.getPrivilege(), dynPage));
			    LOG.debug(
				    "# Added dynamic report: {} with privilege: {}",
				    report.getName(), report.getPrivilege());
			} else {
			    LOG.debug(
				    "# No dynamic report: {} .. not added to menu ",
				    report.getName());
			}
		    }

		}
	    }

	} catch (Exception e) {
	    LOG.error("# Error while getting dynamic reports list", e);
	}

	Collections.sort(entries, new Comparator<IMenuEntry>() {

	    @Override
	    public int compare(IMenuEntry o1, IMenuEntry o2) {
		if (o1.getName().equals("menu.report.home")) {
		    return -1;
		} else if (o2.getName().equals("menu.report.home")) {
		    return +1;
		}
		return o1.getName().compareTo(o2.getName());
	    }
	});

	return entries;
    }

    protected boolean isBatchReport(MobiliserReportParameter report,
	    IContextReport contextReport) {

	if (contextReport != null) {
	    return contextReport.isBatchReport();
	}

	return false;
    }

    protected boolean isBatchPage() {
	return this.isBatchPage;

    }

    public void setBatchPage(boolean isBatchPage) {
	this.isBatchPage = isBatchPage;
    }

    public boolean isReportPage() {
	return isReportPage;
    }

    public void setReportPage(boolean isReportPage) {
	this.isReportPage = isReportPage;
    }

}
