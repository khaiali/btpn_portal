/**
 * 
 */
package com.sybase365.mobiliser.web.common.reports;

/**
 * <p>
 * &copy; 2012 by Sybase, Inc.
 * </p>
 *
 * @author <a href='mailto:Andrew.Clemons@sybase.com'>Andrew Clemons</a>
 */
public abstract class ReportUtil {

    /** special non user supplied parameter for the user's timezone. */
    public static final String PARAM_USER_TIMEZONE = "_PARAM_USER_TIMEZONE";

    /** special non user supplied parameter for the user's timezone. */
    public static final String PARAM_RAW_USER_TIMEZONE = "_PARAM_RAW_USER_TIMEZONE";

    /** special non user supplied parameter midnight of today in the user's timezone. */
    public static final String PARAM_MIDNIGHT_USER_TIMEZONE = "_MIDNIGHT_USER_TIMEZONE";

    /** special non user supplied parameter for the server's timezone. */
    public static final String PARAM_SERVER_TIMEZONE = "_PARAM_SERVER_TIMEZONE";
}
