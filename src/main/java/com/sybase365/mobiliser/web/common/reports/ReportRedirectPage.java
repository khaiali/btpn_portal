package com.sybase365.mobiliser.web.common.reports;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * <p>
 * &copy; 2011, Sybase Inc.
 * </p>
 * @author Allen Lau <alau@sybase.com>
 */
public class ReportRedirectPage extends WebPage {
    private static final Logger LOG = LoggerFactory.getLogger(ReportRedirectPage.class);

    final String redirectUrl;
    public ReportRedirectPage(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        LOG.debug("redirectUrl:" + redirectUrl);
        getRequestCycle().setRequestTarget(new RedirectRequestTarget(redirectUrl));
    }
}
