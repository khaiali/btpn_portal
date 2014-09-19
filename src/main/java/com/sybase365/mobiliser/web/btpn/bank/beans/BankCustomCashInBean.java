package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;


public class BankCustomCashInBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue glAccount;
	private String msisdn;
	private Long amount;
	private Long fee;

	private String accountName;
	private String orgUnitId;
	
	private String debitAccountType;
	private String creditAccountType;
	
	private String debitAccountNumber;
	private String creditAccountNumber;
	
	private XMLGregorianCalendar transactionDatetime;
	private String conversationId;
	private String merchantType;
	private String merchantId;
	private String terminalId;
	private String acquirerId;
	private Integer status;
	private Integer errorCode;
	private String processingCode;
	private boolean isFinal;
	


	private List<BankCustomCashInBean> cashInList;

	public CodeValue getGlAccount() {
		return glAccount;
	}

	public void setGlAccount(CodeValue glAccount) {
		this.glAccount = glAccount;
	}


	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getOrgUnitId() {
		return orgUnitId;
	}

	public void setOrgUnitId(String orgUnitId) {
		this.orgUnitId = orgUnitId;
	}

	
	public String getDebitAccountType() {
		return debitAccountType;
	}

	public void setDebitAccountType(String debitAccountType) {
		this.debitAccountType = debitAccountType;
	}

	public String getCreditAccountType() {
		return creditAccountType;
	}

	public void setCreditAccountType(String creditAccountType) {
		this.creditAccountType = creditAccountType;
	}

	
	public String getDebitAccountNumber() {
		return debitAccountNumber;
	}

	public void setDebitAccountNumber(String debitAccountNumber) {
		this.debitAccountNumber = debitAccountNumber;
	}

	public String getCreditAccountNumber() {
		return creditAccountNumber;
	}

	public void setCreditAccountNumber(String creditAccountNumber) {
		this.creditAccountNumber = creditAccountNumber;
	}
	
	public Long getAmount() {
		return amount;
	}

	public void setCashinAmount(Long amount) {
		this.amount = amount;
	}
	
	public Long getFee() {
		return fee;
	}

	public void setFee(Long fee) {
		this.fee = fee;
	}
	
	
	public XMLGregorianCalendar getTransactionDatetime() {
		return transactionDatetime;
	}

	public void setTransactionDatetime(XMLGregorianCalendar transactionDatetime) {
		this.transactionDatetime = transactionDatetime;
	}
	
	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	
	public String getMerchantType() {
		return merchantType;
	}

	public void setMerchantType(String merchantType) {
		this.merchantType = merchantType;
	}
	
	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	
	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	
	public String getAcquirerId() {
		return acquirerId;
	}

	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getProcessingCode() {
		return processingCode;
	}

	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}
	
	
	public boolean getIsFinal() {
		return isFinal;
	}
	
	public void setIsFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	
	public List<BankCustomCashInBean> getCashInList() {
		return cashInList;
	}

	public void setCashInList(List<BankCustomCashInBean> cashInList) {
		this.cashInList = cashInList;
	}

}
