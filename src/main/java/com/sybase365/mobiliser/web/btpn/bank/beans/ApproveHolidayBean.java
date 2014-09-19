package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Bank Portal approve holiday Operations
 * 
 * @author Narasa Reddy
 */
public class ApproveHolidayBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String createdBy;

	private String description;

	private String status;

	private String action;

	private String fromDate;

	private String toDate;

	private String taskId;

	private boolean approveSuccess = false;

	private boolean rejectSuccess = false;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

}
