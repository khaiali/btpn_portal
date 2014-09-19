package com.sybase365.mobiliser.web.util;

import java.io.Serializable;

import com.sybase365.mobiliser.util.prefs.api.IPreferences;
import com.sybase365.mobiliser.util.tools.clientutils.api.IClientConfiguration;

/**
 */
public class DynamicServiceConfiguration implements IClientConfiguration,
	Serializable {

    private static final long serialVersionUID = 1L;

    private String hostname;

    private String port;

    private IPreferences preferences;

    protected DynamicServiceConfiguration() {
	// empty
    }

    private String getHostname() {
    	return this.preferences.get("mobiliser.hostname", "localhost");
    }

    @Override
    public String getMobiliserEndpointUrl() {
	return getProtocol() + "://" + getHostname() + ":" + getPort() + "/"
		+ getServletName();
    }

    private String getPort() {
	return this.preferences.get("mobiliser.port", "8080");
    }

    public String getProtocol() {
	return this.preferences.get("mobiliser.protocol", "http");
    }

    public String getServletName() {
	return this.preferences.get("mobiliser.servlet", "mobiliser");
    }

    @Override
    public String getWsPassword() {
	return this.preferences.get("mobiliser.password", "secret");
    }

    @Override
    public String getWsUserName() {
	return this.preferences.get("mobiliser.user", "mobiliser");
    }

    /**
     * @param hostname
     *            the hostname to set
     */
    public void setHostname(final String hostname) {
	this.hostname = hostname;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(final String port) {
	this.port = port;
    }

    /**
     * @param preferences
     *            the preferences to set
     */
    public void setPreferences(final IPreferences preferences) {
	this.preferences = preferences;
    }

    @Override
    public String toString() {
	return getMobiliserEndpointUrl() + "/" + getWsUserName() + "/****";
    }
}
