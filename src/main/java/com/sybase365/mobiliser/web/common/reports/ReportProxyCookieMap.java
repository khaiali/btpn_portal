/*
 * Copyright 2012, Sybase Inc.
 * 
 */
package com.sybase365.mobiliser.web.common.reports;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * <p>
 * &copy; 2012, Sybase Inc.
 * </p>
 * @author Allen Lau <alau@sybase.com>
 */
public class ReportProxyCookieMap {

    private ReportProxyCookieMap() {
    }
    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();

    protected static Map<String, String> getMap() {
        return map;
    }
}
