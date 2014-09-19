package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

public class ApproveMsisdnBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String createdBy;

	private String mobileNumber;

	private String changeRequest;

	private String oldMobile;

	private String newMobile;

	private String status;

	private String taskId;

	private boolean approveSuccess = false;

	private boolean rejectSuccess = false;

	private String selectedLink;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getChangeRequest() {
		return changeRequest;
	}

	public void setChangeRequest(String changeRequest) {
		this.changeRequest = changeRequest;
	}

	public String getOldMobile() {
		return oldMobile;
	}

	public void setOldMobile(String oldMobile) {
		this.oldMobile = oldMobile;
	}

	public String getNewMobile() {
		return newMobile;
	}

	public void setNewMobile(String newMobile) {
		this.newMobile = newMobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isApproveSuccess() {
		return approveSuccess;
	}

	public void setApproveSuccess(boolean approveSuccess) {
		this.approveSuccess = approveSuccess;
	}

	public boolean isRejectSuccess() {
		return rejectSuccess;
	}

	public void setRejectSuccess(boolean rejectSuccess) {
		this.rejectSuccess = rejectSuccess;
	}

	public String getSelectedLink() {
		return selectedLink;
	}

	public void setSelectedLink(String selectedLink) {
		this.selectedLink = selectedLink;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
