package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

public class RemittanceAccountBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private int type;
    private String msisdn;
    private String accountHolder;
    private String accountNo;
    private String institutionCode;
    private String accountHolderAddress;
    private String city;
    private String zip;
    private String country;
    private String pId;
    private String accTypeId;
    private String institutionCodeId;

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getAccountHolder() {
	return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
	this.accountHolder = accountHolder;
    }

    public String getAccountNo() {
	return accountNo;
    }

    public void setAccountNo(String accountNo) {
	this.accountNo = accountNo;
    }

    public String getInstitutionCode() {
	return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
	this.institutionCode = institutionCode;
    }

    public String getAccountHolderAddress() {
	return accountHolderAddress;
    }

    public void setAccountHolderAddress(String accountHolderAddress) {
	this.accountHolderAddress = accountHolderAddress;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getZip() {
	return zip;
    }

    public void setZip(String zip) {
	this.zip = zip;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getpId() {
	return pId;
    }

    public void setpId(String pId) {
	this.pId = pId;
    }

    public String getAccTypeId() {
	return accTypeId;
    }

    public void setAccTypeId(String accTypeId) {
	this.accTypeId = accTypeId;
    }

    public String getInstitutionCodeId() {
	return institutionCodeId;
    }

    public void setInstitutionCodeId(String institutionCodeId) {
	this.institutionCodeId = institutionCodeId;
    }

}
