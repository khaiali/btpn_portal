package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the bean used for Approve Fee bean
 * 
 * @author Vikram Gunda
 */
public class ApproveFeeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue productName;

	private CodeValue useCaseName;

	private String feeType;

	private Date requestDate;
	
	private String taskId;
	
	private Long limitClassId;

	public CodeValue getProductName() {
		return productName;
	}

	public void setProductName(CodeValue productName) {
		this.productName = productName;
	}

	public CodeValue getUseCaseName() {
		return useCaseName;
	}

	public void setUseCaseName(CodeValue useCaseName) {
		this.useCaseName = useCaseName;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Long getLimitClassId() {
		return limitClassId;
	}

	public void setLimitClassId(Long limitClassId) {
		this.limitClassId = limitClassId;
	}	
	
}
