package com.sybase365.mobiliser.web.btpn.util;

import org.springframework.beans.factory.InitializingBean;

import com.sybase365.mobiliser.util.prefs.api.IPreferences;

/**
 * This class consist of all the preferences for Bank Portals.
 * 
 * @author Vikram Gunda
 */
public class AgentPortalConfiguration implements InitializingBean {

	private IPreferences node;

	/**
	 * Constructor that is invoked.
	 */
	public AgentPortalConfiguration() {
	}

	/**
	 * Set preferences to set.
	 * 
	 * @param node prefernces to be set
	 */
	public void setNode(IPreferences node) {
		this.node = node;
	}

	/**
	 * After Poperties set check for Prefs node.
	 */
	@Override
	public void afterPropertiesSet() {
		if (this.node == null) {
			throw new IllegalStateException("BankPortalConfiguration requires node");
		}
	}

	/**
	 * Get the Mobile Regex.
	 * 
	 * @return String return Mobile Regex
	 */
	public String getMobileRegex() {
		return getNode().get("mobileRegex", BtpnConstants.REGEX_PHONE_NUMBER);
	}

	/**
	 * After Poperties set check for Prefs node.
	 */
	private IPreferences getNode() {
		return node;
	}

	/**
	 * Get the Default bank login portal url suffix
	 * 
	 * @return String Bank Portal Login Url Suffix
	 */
	public String getLoginUrlSuffix() {
		return getNode().get("loginUrlSuffix", BtpnConstants.AGENT_PORTAL_LOGIN_URL_SUFFIX);
	}

	/**
	 * Get the Default Country Code
	 * 
	 * @return String Default Country Code
	 */
	public String getDefaultCountryCode() {
		return getNode().get("defaultCountryCode", BtpnConstants.DEFAULT_COUNTRY_CODE);
	}



	/**
	 * Get the Default Language
	 * 
	 * @return String Default Language
	 */
	public String getDefaultLanguage() {
		return getNode().get("defaultLanguage", BtpnConstants.DEFAULT_LANGUAGE);
	}
	
	/**
	 * Get the Default Country
	 * 
	 * @return String Default Country
	 */
	public String getDefaultCountry() {
		return getNode().get("defaultCountry", BtpnConstants.DEFAULT_COUNTRY);
	}
}
