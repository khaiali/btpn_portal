package com.sybase365.mobiliser.web.distributor.pages.reports;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.util.Constants;

/**
 * 
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * 
 * @author Allen Lau <alau@sybase.com>
 */
@AuthorizeInstantiation(Constants.PRIV_DPP_REPORTS)
public class DefaultBatchReportPage extends ReportsMenuGroup {

    public DefaultBatchReportPage() {
	setBatchPage(true);
    }
}