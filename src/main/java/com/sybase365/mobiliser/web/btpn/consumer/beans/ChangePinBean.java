package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;

/**
 * This is the bean used for consumer Portal change pin operation
 * 
 * @author Narasa Reddy
 */
public class ChangePinBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oldPin;

	private String newPin;

	private String confirmNewPin;

	private boolean successMessage;

	public String getOldPin() {
		return oldPin;
	}

	public void setOldPin(String oldPin) {
		this.oldPin = oldPin;
	}

	public String getNewPin() {
		return newPin;
	}

	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}

	public String getConfirmNewPin() {
		return confirmNewPin;
	}

	public void setConfirmNewPin(String confirmNewPin) {
		this.confirmNewPin = confirmNewPin;
	}

	public boolean isSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(boolean successMessage) {
		this.successMessage = successMessage;
	}

}
