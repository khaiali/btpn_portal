package com.sybase365.mobiliser.web.util;

import org.springframework.beans.factory.InitializingBean;

import com.sybase365.mobiliser.util.prefs.api.IPreferences;

public class Configuration implements InitializingBean {

    private IPreferences node;

    public static final String DASHBOARD_SERVER_LIST_KEY = "dashboardServerList";
    public static final String DASHBOARD_SERVER_LIST_TYPE = "java.lang.String";
    public static final String DASHBOARD_SERVER_LIST_DESC = "Comma separated list of mobiliser server URLs accessible through dashboard";

    protected Configuration() {

    }

    /*
     * Following preferences are not been moved from 4.6 to 5.0 in DPP
     * 
     * txnSelectableStartMonth txnSelectableStartYear autoLogoutMillis
     * sessionTimeout idReceiptMod infoMode idProduct idUserRight
     * gcashPhoneNumberRegex gcashExtAccCurrency gcashBankList
     * useCaseRemitMoneyToWallet gcashCountryList useCaseRemitMoneyToBank
     */
    public void setNode(IPreferences node) {
	this.node = node;
    }

    @Override
    public void afterPropertiesSet() {
	if (this.node == null) {
	    throw new IllegalStateException("Configuration requires node");
	}
    }

    private IPreferences getNode() {
	return node;
    }
    
    public String getServicePackage() {
	return getNode().get("servicepackage", Constants.DEFAULT_SERVICE_PACKAGE_ID);
    }

    public String getCurrency() {
	return getNode().get("currency", Constants.DEFAULT_CURRENCY);
    }

    public String getCountryCode() {
	return getNode().get("defaultCountryCode",
		Constants.DEFAULT_COUNTRY_CODE);
    }

    public String getCountryId() {
	return getNode().get("defaultCountryId", Constants.DEFAULT_COUNTRY_ID);
    }

    public String getLanguage() {
	return getNode().get("defaultLanguage", Constants.DEFAULT_LANGUAGE);
    }

    public String getTxnMaxNumberToFetch() {
	return getNode().get("txnMaxNumberToFetch",
		Constants.txnMaxNumberToFetch);
    }

    public Long getTxnCancelTimeBuffer() {
	return getNode().getLong("txnCancelTimeBufferInSec", 600L);
    }

    public String getAirtimeTopupPIClass() {
	return getNode().get("airTimeTopupPiClass", "0");
    }

    public Integer getUseCaseCashIn() {
	return getNode().getInt("useCaseCashIn", Constants.USE_CASE_CASH_IN);
    }

    public Integer getUseCaseCashOut() {
	return getNode().getInt("useCaseCashOut", Constants.USE_CASE_CASH_OUT);
    }

    public Integer getUseCaseAirtimeTopup() {
	return getNode().getInt("useCaseAirtimeTopup",
		Constants.USE_CASE_AIRTIME_TOPUP);
    }

    public Integer getUseCaseTransfer() {
	return getNode().getInt("useCaseTransfer",
		Constants.USE_CASE_MONEY_TRANSFER);
    }

    public Integer getUseCaseSendMoneyToBank() {
	return getNode().getInt("useCaseSendMoneyToBank",
		Constants.USE_CASE_SENDMONEY_BANK);
    }

    public String getSvaCurrency() {
	return getNode().get("svaCurrency", "EUR");
    }

    public boolean isTestConsumer() {
	return getNode().getBoolean("isTestConsumer", false);
    }

    public Integer getUseCasePickup() {
	return getNode().getInt("useCasePickup", Constants.USE_CASE_PICK_UP);
    }

    public Integer getMaxRecordsForReport() {
	return getNode().getInt("maxRecordsForReport",
		Constants.MAX_RECORD_FETCH_REPORT);
    }

    public String getReportServerUrl() {
	return getNode().get("reportServerUrl",
		Constants.DEFAULT_REPORT_SERVER_URL);
    }

    public String getReportProxyServerUrl() {
	return getNode().get("reportProxyServerUrl",
		Constants.DEFAULT_REPORT_PROXY_SERVER_URL);
    }

    public String getReportProxyServerPath() {
	return getNode().get("reportProxyServerPath",
		Constants.DEFAULT_REPORT_PROXY_SERVER_PATH);
    }

    public int getSimilarNamesMaxErrors() {
	return getNode().getInt("similarNames_maxErrors",
		Constants.SIMILAR_NAMES_MAX_ERRORS);
    }

    public float getSimilarNamesMinPercentage() {
	return getNode().getFloat("similarNames_minPercentage",
		Constants.SIMILAR_NAMES_MIN_PERCENTAGE);
    }

    public int getDefaultTypeIdForNewCustomer() {
	return getNode().getInt("defaultTypeIdForNewCustomer",
		Constants.DEFAULT_TYPE_ID_FOR_NEW_CUSTOMER);
    }

    public int getDefaultTypeIdForNewCustomerDpp() {
	return getNode().getInt("defaultTypeIdForNewCustomerDpp",
		Constants.DEFAULT_TYPE_ID_FOR_NEW_CUSTOMER_DPP);
    }

    public int getDefaultTypeIdForInternalCustomer() {
	return getNode().getInt("defaultTypeIdForInternalCustomer",
		Constants.DEFAULT_TYPE_ID_FOR_INTERNAL_CUSTOMER);
    }

    public int getDefaultRiskCatForNewCustomer() {
	return getNode().getInt("defaultRiskCatForNewCustomer",
		Constants.DEFAULT_RISK_CAT_FOR_NEW_CUSTOMER);
    }

    public String getSsoSecret() {
	return getNode().get("ssoSecret", Constants.SSO_SECRET);
    }

    public String getKeyStorePw() {
	return getNode().get("keyStorePw", "6NWFRQnUPFI=");
    }

    public String getPublicKeyStore() {
	return getNode().get("publicKeyStore", "mobiliser_pub.jks");
    }

    public String getCreditCardKeyAlias() {
	return getNode().get("creditCardKeyAlias", "mobiliser_card");
    }

    public String getBankAccKeyAlias() {
	return getNode().get("bankAccKeyAlias", "mobiliser_bank");
    }

    public boolean isMsisdnOtpConfirmed() {
	return getNode().getBoolean("msisdnOtpConfirmed", false);
    }

    public long getCreditDebitPiId() {
	return getNode().getLong("creditDebitPiId", 20001);
    }

    public long getCreditDebitCustomerId() {
	return getNode().getLong("creditDebitCustomerId", 201);
    }

    public String getChannelForTestMessage() {
	return getNode().get("channelForTestMessage", null);
    }

    public String getSmsOtpTemplate() {
	return getNode().get("smsOtpTemplate", "otpsignup");
    }

    public String getChangeMsisdnTemplate() {
	return getNode().get("changeMsisdnTemplate", "CUSTOMER_MSISDN_OTP");
    }

    public Boolean getOtpConfirmationRegistration() {
	return getNode().getBoolean("otpConfirmationRegistration", false);
    }

    public String getBalanceAlertTemplate() {
	return getNode().get("balanceAlertTemplate", "balance.alert.template");
    }

    public String getDashboardServerList() {
	return getNode().get(DASHBOARD_SERVER_LIST_KEY, "localhost:8080");
    }

    public static String getNodePath() {
	return Configuration.class.getCanonicalName().replace(".", "/");
    }

    public String getTimeZoneForOrgUnit(String orgUnitId) {
	return getNode().get(orgUnitId, null);
    }

    public Integer getResourceCacheDefaultDuration() {
	int duration = getNode().getInt("resourceCacheDefaultDuration", -1);
	return duration > 0 ? new Integer(duration) : null;
    }

    public String getSVAAlias() {
	return getNode().get("defaultSVAAlias", "SVA");
    }

    public String getLookupKeys(String lookupName) {
	return getNode().get(lookupName, null);
    }

    public String getMerchantAgentTypeIds() {
	return getNode().get("merchantAgentTypeIds", "8");
    }
    
    public int getConnectionTimeOutInMillis(){
    	return getNode().getInt("connectionTimeOutInMillis", 60 * 1000);
    }
    
    public int getReadTimeOutInMillis(){
    	return getNode().getInt("readTimeOutInMillis", 60 * 1000);
    }
}
