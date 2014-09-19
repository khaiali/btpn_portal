package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the bean used for Bank Portal Cash Out Operations
 * 
 * @author Narasa Reddy
 */
public class BankCashOutBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String customerId;

	private String displayName;

	private String mobileNumber;

	private String name;

	private String accountNumber;

	private String accountName;

	private String accountType;

	private Long accountBalance;

	private Long cashOutAmount;

	private Long totalSVABalance;

	private Long totalCashOutAmount;

	private Long creditAmount;

	private Long debitAmount;

	private Long CashOutFeeAmount;

	private long refTransactionId;

	private List<BankCashOutBean> cashOutList;

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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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

	public Long getCashOutAmount() {
		return cashOutAmount;
	}

	public void setCashOutAmount(Long cashOutAmount) {
		this.cashOutAmount = cashOutAmount;
	}

	public Long getTotalSVABalance() {
		return totalSVABalance;
	}

	public void setTotalSVABalance(Long totalSVABalance) {
		this.totalSVABalance = totalSVABalance;
	}

	public Long getTotalCashOutAmount() {
		return totalCashOutAmount;
	}

	public void setTotalCashOutAmount(Long totalCashOutAmount) {
		this.totalCashOutAmount = totalCashOutAmount;
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

	public Long getCashOutFeeAmount() {
		return CashOutFeeAmount;
	}

	public void setCashOutFeeAmount(Long cashOutFeeAmount) {
		CashOutFeeAmount = cashOutFeeAmount;
	}

	public long getRefTransactionId() {
		return refTransactionId;
	}

	public void setRefTransactionId(long refTransactionId) {
		this.refTransactionId = refTransactionId;
	}

	public List<BankCashOutBean> getCashOutList() {
		return cashOutList;
	}

	public void setCashOutList(List<BankCashOutBean> cashOutList) {
		this.cashOutList = cashOutList;
	}

}
