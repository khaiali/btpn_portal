package com.sybase365.mobiliser.web.cst.pages.reports;

import java.util.LinkedList;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.common.reports.panels.DownloadReportPanel;
import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * 
 * @author Mark De Vries <mdevries@sybase.com>
 */
@AuthorizeInstantiation(Constants.PRIV_CST_REPORTS)
public class DownloadCSTReportPage extends CSTReportsMenuGroup {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(DownloadCSTReportPage.class);

    public DownloadCSTReportPage() {
	super();
	LOG.debug("Inside DownloadReportPage");
	this.addOrReplace(new DownloadReportPanel("downloadPanel", this));

    }

    @Override
    protected LinkedList<IMenuEntry> buildDynamicLeftMenu() {
	LinkedList<IMenuEntry> entries = new LinkedList<IMenuEntry>();
	// leave left menu empty
	return entries;
    }

}