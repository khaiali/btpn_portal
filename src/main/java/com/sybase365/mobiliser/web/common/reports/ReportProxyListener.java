/*
 * Copyright 2012, Sybase Inc.
 * 
 */
package com.sybase365.mobiliser.web.common.reports;

import java.util.Enumeration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web application lifecycle listener.
 * @author Allen Lau <alau@sybase.com>
 */
public class ReportProxyListener implements HttpSessionListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(ReportProxyListener.class);
    private static final String ATTR_KEY = ReportProxyServlet.PROXY_COOKIE_PREFIX;

    @Override
    public void sessionCreated(HttpSessionEvent hse) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("hse created: " + hse.getSession());
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
        Enumeration attributes = 
            hse.getSession().getAttributeNames();
        while(attributes.hasMoreElements()) {
            String attributeName = (String) attributes.nextElement();
            if (attributeName.startsWith(ATTR_KEY)) {
                 ReportProxyCookieMap.getMap().remove(attributeName);
                 if (LOG.isDebugEnabled()) {
                    LOG.debug("Remove cookie {} from map", attributeName);
                 }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("hse destroyed: " + hse.getSession());
        }
    }
}
