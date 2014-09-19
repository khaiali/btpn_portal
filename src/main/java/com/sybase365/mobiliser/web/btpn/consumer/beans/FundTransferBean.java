package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;
import java.util.Date;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;


public class FundTransferBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue accountType;
	private CodeValue transferType;
	private String mobileNumber;
	private String payerMsisdn;
	private String payeeMsisdn;
	private CodeValue favoriteNum;

	private Long amount;
	private String pin;
	private String accountNo;
	private String beneficiaryName;
	private CodeValue bankCode;

	private String beneficiaryAccNo;
	private Long feeAmount;
	private String feeCurrency;
	
	private long piId;
	private String siName;

	private Date startDate;
	private Date expiryDate;
	private CodeValue selectedDay;

	private String frequencyMonth;
	private String frequencyQuarter;
	private String frequencyWeek;
	private String frequencyDate;
	private String frequencyType;

	private boolean selectedFixedAmt;
	private boolean selectedChargedAmt;
	private String additionalData;
	private boolean msisdnRegistered;
	private boolean msisdnUnRegistered;
	private boolean isMobileSelected;

	private String selectedFundTransferType;
	
	private String sourceHolderName;
	private String beneficiaryHolderName;
	private String proc_code;
	private String benefBankCode;
	
	
	private String flag;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public CodeValue getFavoriteNum() {
		return favoriteNum;
	}

	public void setFavoriteNum(CodeValue favoriteNum) {
		this.favoriteNum = favoriteNum;
	}

	public CodeValue getAccountType() {
		return accountType;
	}

	public void setAccountType(CodeValue accountType) {
		this.accountType = accountType;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public CodeValue getBankCode() {
		return bankCode;
	}

	public void setBankCode(CodeValue bankCode) {
		this.bankCode = bankCode;
	}

	public String getBeneficiaryAccNo() {
		return beneficiaryAccNo;
	}

	public void setBeneficiaryAccNo(String beneficiaryAccNo) {
		this.beneficiaryAccNo = beneficiaryAccNo;
	}

	public CodeValue getTransferType() {
		return transferType;
	}

	public void setTransferType(CodeValue transferType) {
		this.transferType = transferType;
	}

	public String getPayeeMsisdn() {
		return payeeMsisdn;
	}

	public void setPayeeMsisdn(String payeeMsisdn) {
		this.payeeMsisdn = payeeMsisdn;
	}

	public Long getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Long feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getPayerMsisdn() {
		return payerMsisdn;
	}

	public void setPayerMsisdn(String payerMsisdn) {
		this.payerMsisdn = payerMsisdn;
	}

	
	public long getPiId() {
		return piId;
	}

	
	public void setPiId(long piId) {
		this.piId = piId;
	}

	public String getSiName() {
		return siName;
	}

	public void setSiName(String siName) {
		this.siName = siName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public CodeValue getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(CodeValue selectedDay) {
		this.selectedDay = selectedDay;
	}

	public String getFrequencyMonth() {
		return frequencyMonth;
	}

	public void setFrequencyMonth(String frequencyMonth) {
		this.frequencyMonth = frequencyMonth;
	}

	public String getFrequencyQuarter() {
		return frequencyQuarter;
	}

	public void setFrequencyQuarter(String frequencyQuarter) {
		this.frequencyQuarter = frequencyQuarter;
	}

	public String getFrequencyWeek() {
		return frequencyWeek;
	}

	public void setFrequencyWeek(String frequencyWeek) {
		this.frequencyWeek = frequencyWeek;
	}

	public String getFrequencyDate() {
		return frequencyDate;
	}

	public void setFrequencyDate(String frequencyDate) {
		this.frequencyDate = frequencyDate;
	}

	public String getFrequencyType() {
		return frequencyType;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

	public boolean isSelectedFixedAmt() {
		return selectedFixedAmt;
	}

	public void setSelectedFixedAmt(boolean selectedFixedAmt) {
		this.selectedFixedAmt = selectedFixedAmt;
	}

	public boolean isSelectedChargedAmt() {
		return selectedChargedAmt;
	}

	public void setSelectedChargedAmt(boolean selectedChargedAmt) {
		this.selectedChargedAmt = selectedChargedAmt;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	public boolean isMsisdnRegistered() {
		return msisdnRegistered;
	}

	public void setMsisdnRegistered(boolean msisdnRegistered) {
		this.msisdnRegistered = msisdnRegistered;
	}

	public boolean isMsisdnUnRegistered() {
		return msisdnUnRegistered;
	}

	public void setMsisdnUnRegistered(boolean msisdnUnRegistered) {
		this.msisdnUnRegistered = msisdnUnRegistered;
	}

	public boolean isMobileSelected() {
		return isMobileSelected;
	}

	public void setMobileSelected(boolean isMobileSelected) {
		this.isMobileSelected = isMobileSelected;
	}

	public String getSelectedFundTransferType() {
		return selectedFundTransferType;
	}

	public void setSelectedFundTransferType(String selectedFundTransferType) {
		this.selectedFundTransferType = selectedFundTransferType;
	}


	public String getFeeCurrency() {
		return feeCurrency;
	}

	public void setFeeCurrency(String feeCurrency) {
		this.feeCurrency = feeCurrency;
	}

	public String getSourceHolderName() {
		return sourceHolderName;
	}

	
	public void setSourceHolderName(String sourceHolderName) {
		this.sourceHolderName = sourceHolderName;
	}

	public String getBeneficiaryHolderName() {
		return beneficiaryHolderName;
	}

	
	public void setBeneficiaryHolderName(String beneficiaryHolderName) {
		this.beneficiaryHolderName = beneficiaryHolderName;
	}

	public String getProc_code() {
		return proc_code;
	}

	public void setProc_code(String proc_code) {
		this.proc_code = proc_code;
	}
	
	public String getBenefBankCode() {
		return benefBankCode;
	}

	public void setBenefBankCode(String benefBankCode) {
		this.benefBankCode = benefBankCode;
	}

	public String getFlag() {
		return flag;
	}

	
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
}
