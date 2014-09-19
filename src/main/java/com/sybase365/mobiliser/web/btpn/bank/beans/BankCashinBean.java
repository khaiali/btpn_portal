package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.List;

/**
 * This is the bean used for Bank Portal Cash In Operations
 * 
 * @author Narasa Reddy
 */
public class BankCashinBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String customerId;

	private String displayName;

	private String msisdn;

	private String name;

	private Long cashinAmount;

	private String accountNumber;

	private String accountName;

	private String accountType;

	private Long accountBalance;

	private Long totalSVABalance;

	private Long creditAmount;

	private Long totalCashinAmount;

	private Long cashInFeeAmount;

	private long refTransactionId;

	private List<BankCashinBean> cashInList;

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

	public Long getTotalSVABalance() {
		return totalSVABalance;
	}

	public void setTotalSVABalance(Long totalSVABalance) {
		this.totalSVABalance = totalSVABalance;
	}

	public Long getCashinAmount() {
		return cashinAmount;
	}

	public void setCashinAmount(Long cashinAmount) {
		this.cashinAmount = cashinAmount;
	}

	public Long getTotalCashinAmount() {
		return totalCashinAmount;
	}

	public void setTotalCashinAmount(Long totalCashinAmount) {
		this.totalCashinAmount = totalCashinAmount;
	}

	public Long getCashInFeeAmount() {
		return cashInFeeAmount;
	}

	public void setCashInFeeAmount(Long cashInFeeAmount) {
		this.cashInFeeAmount = cashInFeeAmount;
	}

	public long getRefTransactionId() {
		return refTransactionId;
	}

	public void setRefTransactionId(long refTransactionId) {
		this.refTransactionId = refTransactionId;
	}

	public List<BankCashinBean> getCashInList() {
		return cashInList;
	}

	public void setCashInList(List<BankCashinBean> cashInList) {
		this.cashInList = cashInList;
	}

	public Long getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Long creditAmount) {
		this.creditAmount = creditAmount;
	}

}
