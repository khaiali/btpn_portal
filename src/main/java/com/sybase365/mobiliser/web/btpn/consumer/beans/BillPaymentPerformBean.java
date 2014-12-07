package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;

import javax.xml.bind.JAXBElement;

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

	private String amountOption;

	private String amountOrUnsold;
	private String unsold1;
	private String unsold2;

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
	private JAXBElement<String> additionalData2;

	private String customerName;

	private String meterNumber;
	private String tarif;
	private String daya;
	private String materai;
	private String ppn;
	private String ppj;
	private String angsuran;
	private String tokenAmount;
	private String kwh;
	private String token;
	private String monthYear;
	private String stdMeter;
	private String dateReg;
	private String regNumber;
	private String billNumber;
	
	// tambahan khaidir
	private String rptagpln;
	private String adminbank;
	private String totalbayar;
	private String amount;
	private String blntunggak;

	/**
	 * Substring 2 digit in front of billPayBean.selectedBillerId.id for PAN number
	 */
	private String partPan;
	


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
		partPan = selectedBillerId.getValue().toString();
		parseBillerId(partPan);
	}

	public String getManualOrFavourite() {
		return manualOrFavourite;
	}

	public void setManualOrFavourite(String manualOrFavourite) {
		this.manualOrFavourite = manualOrFavourite;
	}
	

	public String getAmountOption() {
		return amountOption;
	}

	public void setAmountOption(String amountOption) {
		this.amountOption = amountOption;
	}
	


	public String getAmountOrUnsold() {
		return amountOrUnsold;
	}

	public void setAmountOrUnsold(String amountOrUnsold) {
		this.amountOrUnsold = amountOrUnsold;
	}

	
	public String getUnsold1() {
		return unsold1;
	}

	public void setUnsold1(String unsold1) {
		this.unsold1 = unsold1;
	}

	public String getUnsold2() {
		return unsold2;
	}

	public void setUnsold2(String unsold2) {
		this.unsold2 = unsold2;
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
	//	parseAdditionalData(additionalData);
		

	}
	
	public JAXBElement<String> getAdditionalData2() {
		return additionalData2;
	}

	public void setAdditionalData2(JAXBElement<String> jaxbElement) {
		this.additionalData2 = jaxbElement;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}	

	public String getMeterNumber() {
		return meterNumber;
	}

	public void setMeterNumber(String meterNumber) {
		this.meterNumber = meterNumber;
	}
	
	public String getTarif() {
		return tarif;
	}

	public void setTarif(String tarif) {
		this.tarif = tarif;
	}

	public String getDaya() {
		return daya;
	}

	public void setDaya(String daya) {
		this.daya = daya;
	}

	public String getMaterai() {
		return materai;
	}

	public void setMaterai(String materai) {
		this.materai = materai;
	}

	public String getPpn() {
		return ppn;
	}

	public void setPpn(String ppn) {
		this.ppn = ppn;
	}

	public String getPpj() {
		return ppj;
	}

	public void setPpj(String ppj) {
		this.ppj = ppj;
	}

	public String getAngsuran() {
		return angsuran;
	}

	public void setAngsuran(String angsuran) {
		this.angsuran = angsuran;
	}

	public String getTokenAmount() {
		return tokenAmount;
	}

	public void setTokenAmount(String tokenAmount) {
		this.tokenAmount = tokenAmount;
	}

	public String getKwh() {
		return kwh;
	}

	public void setKwh(String kwh) {
		this.kwh = kwh;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMonthYear() {
		return monthYear;
	}

	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}

	public String getStdMeter() {
		return stdMeter;
	}

	public void setStdMeter(String stdMeter) {
		this.stdMeter = stdMeter;
	}

	public String getDateReg() {
		return dateReg;
	}

	public void setDateReg(String dateReg) {
		this.dateReg = dateReg;
	}
	
	public String getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	
	public void parseBillerId(String add) {
		this.partPan = add.substring(0,2);
		
	}

	public String getRptagpln() {
		return rptagpln;
	}

	public void setRptagpln(String rptagpln) {
		this.rptagpln = rptagpln;
	}

	public String getAdminbank() {
		return adminbank;
	}

	public void setAdminbank(String adminbank) {
		this.adminbank = adminbank;
	}

	public String getTotalbayar() {
		return totalbayar;
	}

	public void setTotalbayar(String totalbayar) {
		this.totalbayar = totalbayar;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBlntunggak() {
		return blntunggak;
	}

	public void setBlntunggak(String blntunggak) {
		this.blntunggak = blntunggak;
	}
	
}
