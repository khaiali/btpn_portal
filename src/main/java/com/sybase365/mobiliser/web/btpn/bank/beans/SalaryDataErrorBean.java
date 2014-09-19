package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the Salary Data for error bank portals.
 * 
 * @author Vikram Gunda
 */
public class SalaryDataErrorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer lineNo;

	private String errorRecord;

	private String errorDescription;

	public Integer getLineNo() {
		return lineNo;
	}

	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}

	public String getErrorRecord() {
		return errorRecord;
	}

	public void setErrorRecord(String errorRecord) {
		this.errorRecord = errorRecord;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

}
