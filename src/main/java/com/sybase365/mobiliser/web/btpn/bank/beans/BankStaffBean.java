package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Bank Staff Registration or Bank Staff Operations
 * 
 * @author Vikram Gunda
 */
public class BankStaffBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String requestType;
	
	private Long customerId;

	private CodeValue customerType;

	private String userId;

	private String name;

	private String email;

	private String designation;

	private CodeValue language;
	
	private CodeValue glCode;

	private String territoryCode;
	
	private CodeValue orgUnit;

	private String createdBy;

	private String createdDate;

	private String type;

	private String status;
	
	private String workflowId;
	
	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public CodeValue getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CodeValue customerType) {
		this.customerType = customerType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public CodeValue getLanguage() {
		return language;
	}

	public void setLanguage(CodeValue language) {
		this.language = language;
	}

	public CodeValue getGlCode() {
		return glCode;
	}

	public void setGlCode(CodeValue glCode) {
		this.glCode = glCode;
	}

	public String getTerritoryCode() {
		return territoryCode;
	}

	public void setTerritoryCode(String territoryCode) {
		this.territoryCode = territoryCode;
	}

	public CodeValue getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(CodeValue orgUnit) {
		this.orgUnit = orgUnit;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
}
