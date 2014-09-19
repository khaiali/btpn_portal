package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

public class CustomerSearchBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String msisdn;

	private String email;

	private String displayName;

	private String employeeId;

	private CodeValue custStatus;

	/**
	 * @return the msisdn
	 */
	public String getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the employeeId
	 */
	public String getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the custStatus
	 */
	public CodeValue getCustStatus() {

		return custStatus;
	}

	/**
	 * @param custStatus the custStatus to set
	 */
	public void setCustStatus(CodeValue custStatus1) {
		custStatus = custStatus1;
	}

}
