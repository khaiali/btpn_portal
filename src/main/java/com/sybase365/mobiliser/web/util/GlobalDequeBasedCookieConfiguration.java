/**
 *
 */
package com.sybase365.mobiliser.web.util;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import com.sybase365.mobiliser.util.tools.clientutils.api.Cookie;
import com.sybase365.mobiliser.util.tools.clientutils.api.ICookieConfiguration;

/**
 * {@link ICookieConfiguration} suitable for a global client using the same
 * credentials for all calls. New cookies will be created as needed and added to
 * the list and checked out as needed.
 * <p>
 * &copy; 2011 by Sybase, Inc.
 * </p>
 *
 * @author <a href='mailto:Andrew.Clemons@sybase.com'>Andrew Clemons</a>
 */
public class GlobalDequeBasedCookieConfiguration implements
	ICookieConfiguration {

    private String cookieName;

    private final Queue<Cookie> deque = new LinkedBlockingDeque<Cookie>();

    private String requestString;

    @Override
    public void addCookies(final Cookie[] newCookies) {
	if (newCookies != null && newCookies.length > 0) {
	    for (final Cookie c : newCookies) {
		if (this.cookieName.equals(c.getCookieName())) {

		    this.deque.add(c);

		    return;
		}
	    }
	}
    }

    @Override
    public String getCookieRequestString() {
	return this.requestString;
    }

    /**
     * @see com.sybase365.mobiliser.util.tools.clientutils.api.ICookieConfiguration#isUseCookies()
     */
    @Override
    public boolean isUseCookies() {
	return true;
    }

    @Override
    public Cookie pollCookie() {
	return this.deque.poll();
    }

    /**
     * @param cookieName
     *            the cookieName to set
     */
    public void setCookieName(final String cookieName) {
	this.cookieName = cookieName;
    }

    /**
     * @param requestString
     *            the requestString to set
     */
    public void setRequestString(final String requestString) {
	this.requestString = requestString;
    }
}
