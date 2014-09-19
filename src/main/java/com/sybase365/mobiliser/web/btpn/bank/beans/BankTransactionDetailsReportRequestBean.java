package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is the transaction details report request bean for bank portal.
 * 
 * @author Vikram Gunda
 */
public class BankTransactionDetailsReportRequestBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date fromDate;

	private Date toDate;

	private String agentMsisdn;

	private String agentType;

	private CodeValue reportScope;

	private CodeValue txnType;

	private List<TransactionDetailsReportAgentBean> txnDataList;

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

	public String getAgentMsisdn() {
		return agentMsisdn;
	}

	public void setAgentMsisdn(String agentMsisdn) {
		this.agentMsisdn = agentMsisdn;
	}

	public CodeValue getTxnType() {
		return txnType;
	}

	public void setTxnType(CodeValue txnType) {
		this.txnType = txnType;
	}

	public CodeValue getReportScope() {
		return reportScope;
	}

	public void setReportScope(CodeValue reportScope) {
		this.reportScope = reportScope;
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public List<TransactionDetailsReportAgentBean> getTxnDataList() {
		if (this.txnDataList == null) {
			this.txnDataList = new ArrayList<TransactionDetailsReportAgentBean>();
		}
		return txnDataList;
	}

	public void setTxnDataList(List<TransactionDetailsReportAgentBean> txnDataList) {
		this.txnDataList = txnDataList;

	}

}
