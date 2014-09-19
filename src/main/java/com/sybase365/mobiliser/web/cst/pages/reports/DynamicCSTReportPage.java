package com.sybase365.mobiliser.web.cst.pages.reports;

import java.util.LinkedList;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.report.beans.MobiliserReportParameter;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.common.reports.custom.IContextReport;
import com.sybase365.mobiliser.web.common.reports.panels.DynamicReportPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * Page that represents a server-side MobiliserReport. In contrast to other
 * pages, this class is not instantiated by the Wicket framework but instead
 * instantiated on demand based on availability of reports on the backend
 * container.
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * 
 * @author Allen Lau <alau@sybase.com>
 */
@AuthorizeInstantiation(Constants.PRIV_CST_REPORTS)
public class DynamicCSTReportPage extends CSTReportsMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(DynamicCSTReportPage.class);

    private DynamicReportPanel reportPanel;

    public DynamicCSTReportPage(MobiliserReportParameter report,
	    IContextReport customReportInfo) {

	// add the report panel to the page
	this.reportPanel = new DynamicReportPanel("dynamicReportPanel", this,
		report, customReportInfo);
	add(this.reportPanel);

    }

    public DynamicCSTReportPage(MobiliserReportParameter report) {
	this(report, null);
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	//
	// Must not call anything here for this page as this page is intended
	// for user instantiation instead of by wicket.
	//
    }

    @Override
    public LinkedList<IMenuEntry> buildLeftMenu() {
	return new LinkedList<IMenuEntry>();
    }

    @Override
    protected boolean isBatchPage() {
	return false;
    }

    @Override
    protected void onBeforeRender() {
	//
	// call the parent render first to build the left menu.
	//
	super.onBeforeRender();
    }

    @Override
    public void onPageAttached() {
	LOG.debug("PagedAttached");
	super.onPageAttached();
	// delegate resetPage() to the report panel
	if (reportPanel != null) {
	    reportPanel.resetPage();
	}
    }

}