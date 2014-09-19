package com.sybase365.mobiliser.web.distributor.pages.reports;

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
@AuthorizeInstantiation(Constants.PRIV_DPP_REPORTS)
public class DownloadReportPage extends ReportsMenuGroup {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(DownloadReportPage.class);

    public DownloadReportPage() {
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