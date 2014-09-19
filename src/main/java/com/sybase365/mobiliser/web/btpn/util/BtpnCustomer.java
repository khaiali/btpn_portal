package com.sybase365.mobiliser.web.btpn.util;

import java.io.Serializable;
import java.util.Date;

import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;

public class BtpnCustomer extends Customer implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date createdDate;
	
	private String designation;
	
	private String email;
	
	private Long glCode;

	private String parentSvaId;

	private String territoryCode;

	private int txnReceiptModeId;
	
	private String otpPrivilege;
	
	private int customerTypeCategoryId;

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getGlCode() {
		return glCode;
	}

	public void setGlCode(Long glCode) {
		this.glCode = glCode;
	}

	public String getParentSvaId() {
		return parentSvaId;
	}

	public void setParentSvaId(String parentSvaId) {
		this.parentSvaId = parentSvaId;
	}

	public String getTerritoryCode() {
		return territoryCode;
	}

	public void setTerritoryCode(String territoryCode) {
		this.territoryCode = territoryCode;
	}

	public int getTxnReceiptModeId() {
		return txnReceiptModeId;
	}

	public void setTxnReceiptModeId(int txnReceiptModeId) {
		this.txnReceiptModeId = txnReceiptModeId;
	}

	public String getOtpPrivilege() {
		return otpPrivilege;
	}

	public void setOtpPrivilege(String otpPrivilege) {
		this.otpPrivilege = otpPrivilege;
	}

	public int getCustomerTypeCategoryId() {
		return customerTypeCategoryId;
	}

	public void setCustomerTypeCategoryId(int customerTypeCategoryId) {
		this.customerTypeCategoryId = customerTypeCategoryId;
	}
}
