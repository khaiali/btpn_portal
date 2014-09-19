package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;
import java.util.List;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

public class SubAccountsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue accountType;

	private CodeValue transferType;

	private String name;

	private String accountId;

	private String description;

	private Long amount;

	private Long balance;

	private String remarks;

	private boolean removeSuccessFlag = false;

	private boolean addSuccessFlag = false;

	List<SubAccountsBean> subAccountsList;

	public CodeValue getAccountType() {
		return accountType;
	}

	public void setAccountType(CodeValue accountType) {
		this.accountType = accountType;
	}

	public CodeValue getTransferType() {
		return transferType;
	}

	public void setTransferType(CodeValue transferType) {
		this.transferType = transferType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<SubAccountsBean> getSubAccountsList() {
		return subAccountsList;
	}

	public void setSubAccountsList(List<SubAccountsBean> subAccountsList) {
		this.subAccountsList = subAccountsList;
	}

	public boolean isRemoveSuccessFlag() {
		return removeSuccessFlag;
	}

	public void setRemoveSuccessFlag(boolean removeSuccessFlag) {
		this.removeSuccessFlag = removeSuccessFlag;
	}

	public boolean isAddSuccessFlag() {
		return addSuccessFlag;
	}

	public void setAddSuccessFlag(boolean addSuccessFlag) {
		this.addSuccessFlag = addSuccessFlag;
	}

}
