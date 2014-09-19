package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsumerTransactionRequestBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String filterType;

	private Date fromDate;

	private Date toDate;

	private CodeValue txnType;

	private CodeValue subAccount;

	private CodeValue month;

	private List<ConsumerTransactionBean> txnDataList;

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public CodeValue getTxnType() {
		return txnType;
	}

	public void setTxnType(CodeValue txnType) {
		this.txnType = txnType;
	}

	public CodeValue getSubAccount() {
		return subAccount;
	}

	public void setSubAccount(CodeValue subAccount) {
		this.subAccount = subAccount;
	}

	public CodeValue getMonth() {
		return month;
	}

	public void setMonth(CodeValue month) {
		this.month = month;
	}

	public List<ConsumerTransactionBean> getTxnDataList() {
		if (this.txnDataList == null) {
			this.txnDataList = new ArrayList<ConsumerTransactionBean>();
		}
		return txnDataList;
	}

	public void setTxnDataList(List<ConsumerTransactionBean> txnDataList) {
		this.txnDataList = txnDataList;
	}

}
