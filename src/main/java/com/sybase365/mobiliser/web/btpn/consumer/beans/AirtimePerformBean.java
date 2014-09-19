package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;


public class AirtimePerformBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue telco;
	private CodeValue denomination;
	private CodeValue selectedMsisdn;
	private String sourceMsisdn;
	private String manualOrFavourite;
	private Long feeAmount;
	private String feeCurrency;
	private String pin;
	private Long transactionId;
	private String reference;
	private String statusMessage;
	private String label;
	private String productId;
	private String billerId;
	private String billerDescription;
	
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

	public CodeValue getSelectedMsisdn() {
		return selectedMsisdn;
	}

	public void setSelectedMsisdn(CodeValue selectedMsisdn) {
		this.selectedMsisdn = selectedMsisdn;
	}

	public String getManualOrFavourite() {
		return manualOrFavourite;
	}

	public void setManualOrFavourite(String manualOrFavourite) {
		this.manualOrFavourite = manualOrFavourite;
	}

	public Long getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Long feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getSourceMsisdn() {
		return sourceMsisdn;
	}

	public void setSourceMsisdn(String sourceMsisdn) {
		this.sourceMsisdn = sourceMsisdn;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public void setBillerId(String billerId){
		this.billerId = billerId;
	}
	
	public String getBillerId(){
		return billerId;
	}
	
	public String getBillerDescription() {
		return billerDescription;
	}

	public void setBillerDescription(String billerDescription) {
		this.billerDescription = billerDescription;
	}

	public String getFeeCurrency() {
		return feeCurrency;
	}

	public void setFeeCurrency(String feeCurrency) {
		this.feeCurrency = feeCurrency;
	}
	
	
}
