package com.sybase365.mobiliser.web.btpn.util;

import org.springframework.beans.factory.InitializingBean;

import com.sybase365.mobiliser.util.prefs.api.IPreferences;

/**
 * This class consist of all the preferences for Consumer Portals.
 * 
 * @author Vikram Gunda
 */
public class ConsumerPortalConfiguration implements InitializingBean {

	private IPreferences node;

	/**
	 * Constructor that is invoked.
	 */
	public ConsumerPortalConfiguration() {
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
			throw new IllegalStateException("ConsumerPortalConfiguration requires node");
		}
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
		return getNode().get("loginUrlSuffix", BtpnConstants.CONSUMER_PORTAL_LOGIN_URL_SUFFIX);
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
	 * Get the Default Country Code
	 * 
	 * @return String Default Country Code
	 */
	public int getTransactionStartMonth() {
		return getNode().getInt("transactionStartMonth", BtpnConstants.DEFAULT_TRANSACTION_START_MONTH);
	}

	/**
	 * Get the Default Country Code
	 * 
	 * @return String Default Country Code
	 */
	public int getTransactionStartYear() {
		return getNode().getInt("transactionStartYear", BtpnConstants.DEFAULT_TRANSACTION_START_YEAR);
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
	
	/**
	 * Get the default PLN PrePaid code
	 * 
	 * @return String Default PLN PrePaid Code
	 */
	public String getDefaultPlnPrePaid() {
		return getNode().get("defaultPlnPrePaid", BtpnConstants.DEFAULT_PLN_PREPAID);
	}
	
	/**
	 * Get the default PLN PrePaid code
	 * 
	 * @return String Default PLN PrePaid Code
	 */
	public String getPlnPrePaidNosForCustomerName() {
		return getNode().get("plnNumbersForCustomerName", BtpnConstants.DEFAULT_PLN_NOS_FOR_CUSTOMER_NAME);
	}
}
