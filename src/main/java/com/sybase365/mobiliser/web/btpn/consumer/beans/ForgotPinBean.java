package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;

public class ForgotPinBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private long customerId;

	private String msisdn;

	private String email;

	private String otp;

	private String passCode;

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getPassCode() {
		return passCode;
	}

	public void setPassCode(String passCode) {
		this.passCode = passCode;
	}

}
