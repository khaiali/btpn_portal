package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

/**
 * This is the Bill Payment Perform bean.
 * 
 * @author Feny Yanti
 */
public class BillPaymentPerformBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String billerLabel;
	private String billerId;

	private String productId;
	private String productLabel;
	
	private CodeValue selectedBillerId;

	private String manualOrFavourite;

	private Long billAmount;
	private Long feeAmount;
	private String feeCurrency;
	private String pin;

	private String txnId;

	private String payDate;

	private String statusMessage;

	private String referenceNumber;
	private String accountNumber;
	private String additionalData;
	private String customerName;

	public String getBillerLabel() {
		return billerLabel;
	}

	public void setBillerLabel(String billerLabel) {
		this.billerLabel = billerLabel;
	}
	
	public String getBillerId() {
		return billerId;
	}

	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}


	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductLabel() {
		return productLabel;
	}

	public void setProductLabel(String productLabel) {
		this.productLabel = productLabel;
	}
	
	public CodeValue getSelectedBillerId() {
		return selectedBillerId;
	}

	public void setSelectedBillerId(CodeValue selectedBillerId) {
		this.selectedBillerId = selectedBillerId;
	}

	public String getManualOrFavourite() {
		return manualOrFavourite;
	}

	public void setManualOrFavourite(String manualOrFavourite) {
		this.manualOrFavourite = manualOrFavourite;
	}

	public Long getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(Long billAmount) {
		this.billAmount = billAmount;
	}

	public Long getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Long feeAmount) {
		this.feeAmount = feeAmount;
	}
	
	public String getFeeCurrency() {
		return feeCurrency;
	}

	public void setFeeCurrency(String feeCurrency) {
		this.feeCurrency = feeCurrency;
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

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}	
	
}
