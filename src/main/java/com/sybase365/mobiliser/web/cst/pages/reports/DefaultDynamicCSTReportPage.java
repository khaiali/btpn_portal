package com.sybase365.mobiliser.web.cst.pages.reports;

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
@AuthorizeInstantiation(Constants.PRIV_CST_REPORTS)
public class DefaultDynamicCSTReportPage extends CSTReportsMenuGroup {

    public DefaultDynamicCSTReportPage() {
	setBatchPage(false);
    }
}