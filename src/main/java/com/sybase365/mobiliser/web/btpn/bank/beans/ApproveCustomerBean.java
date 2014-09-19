package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.Date;

public class ApproveCustomerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date date;

	private String requestType;

	private String consumerType;
	
	private int customerTypeCategory;

	private String mobileNumber;

	private String status;

	private String createdBy;

	private String taskId;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String type) {
		this.consumerType = type;
	}
	
	public int getCustomerTypeCategory() {
		return customerTypeCategory;
	}

	public void setCustomerTypeCategory(int customerTypeCategory) {
		this.customerTypeCategory = customerTypeCategory;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
