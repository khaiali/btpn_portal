package com.sybase365.mobiliser.web.btpn.agent.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;


public class AgentCustomCashInBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String customerId;

	private String displayName;

	private String msisdn;

	private String payermsisdn;

	private String payeeMsisdn;

	private String name;

	private String accountNumber;

	private String accountName;

	private String accountType;

	private Long accountBalance;

	private Long cashinAmount;

	private Long feeAmount;

	private Long totalAmount;

	private Long balance;

	private int useCaseId;

	private boolean success;

	private long refTransactionId;
	
	private Long creditAmount;
	private Long debitAmount;

	private List<AgentCustomCashInBean> cashInList;
	
	private String proc_code;
	
	private String convId;
	
	private String terminalId;
	
	private XMLGregorianCalendar transactionDatetime;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Long getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Long accountBalance) {
		this.accountBalance = accountBalance;
	}

	public Long getCashinAmount() {
		return cashinAmount;
	}

	public void setCashinAmount(Long cashinAmount) {
		this.cashinAmount = cashinAmount;
	}

	public List<AgentCustomCashInBean> getCashInList() {
		if (cashInList == null)
			cashInList = new ArrayList<AgentCustomCashInBean>();
		return cashInList;
	}

	public void setCashInList(List<AgentCustomCashInBean> cashInList) {
		this.cashInList = cashInList;
	}

	public Long getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Long feeAmount) {
		this.feeAmount = feeAmount;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public int getUseCaseId() {
		return useCaseId;
	}

	public void setUseCaseId(int useCaseId) {
		this.useCaseId = useCaseId;
	}

	public long getRefTransactionId() {
		return refTransactionId;
	}

	public void setRefTransactionId(long refTransactionId) {
		this.refTransactionId = refTransactionId;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPayermsisdn() {
		return payermsisdn;
	}

	public void setPayermsisdn(String payermsisdn) {
		this.payermsisdn = payermsisdn;
	}

	public String getPayeeMsisdn() {
		return payeeMsisdn;
	}

	public void setPayeeMsisdn(String payeeMsisdn) {
		this.payeeMsisdn = payeeMsisdn;
	}

	public Long getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Long creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Long getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Long debitAmount) {
		this.debitAmount = debitAmount;
	}

	/**
	 * @return the proc_code
	 */
	public String getProc_code() {
		return proc_code;
	}

	/**
	 * @param proc_code the proc_code to set
	 */
	public void setProc_code(String proc_code) {
		this.proc_code = proc_code;
	}

	public String getConvId() {
		return convId;
	}

	public void setConvId(String convId) {
		this.convId = convId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public XMLGregorianCalendar getTransactionDatetime() {
		return transactionDatetime;
	}

	public void setTransactionDatetime(XMLGregorianCalendar transactionDatetime) {
		this.transactionDatetime = transactionDatetime;
	}
}
