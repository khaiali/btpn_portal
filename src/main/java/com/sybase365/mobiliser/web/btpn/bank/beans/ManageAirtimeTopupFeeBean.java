package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Manage Fee bean
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupFeeBean extends ManageFeeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue telco;

	private CodeValue denomination;

	public CodeValue getTelco() {
		return telco;
	}

	public void setTelco(CodeValue telco) {
		this.telco = telco;
	}

	public CodeValue getDenomination() {
		return denomination;
	}

	public void setDenomination(CodeValue denomination) {
		this.denomination = denomination;
	}
}
