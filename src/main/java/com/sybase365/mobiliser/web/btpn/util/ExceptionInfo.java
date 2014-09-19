package com.sybase365.mobiliser.web.btpn.util;

/**
 * This is used for storing exception additional Info if required.
 * 
 * @author Vikram Gunda
 */
public class ExceptionInfo {

	private int errorCode;
	
	private String errorMesssage;
	
	private String attemptsLeft;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMesssage() {
		return errorMesssage;
	}

	public void setErrorMesssage(String errorMesssage) {
		this.errorMesssage = errorMesssage;
	}

	public String getAttemptsLeft() {
		return attemptsLeft;
	}

	public void setAttemptsLeft(String attemptsLeft) {
		this.attemptsLeft = attemptsLeft;
	}	
}
