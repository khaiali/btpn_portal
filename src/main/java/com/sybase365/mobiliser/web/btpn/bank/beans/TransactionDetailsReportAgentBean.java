package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * This is used for holding Transaction details report Agent bean.
 * 
 * @author Vikram Gunda
 */
public class TransactionDetailsReportAgentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date date;

	private String customerAccount;

	private String agentType;

	private Long agentId;

	private String transactionType;

	private Long amount;

	private String biller;

	private String beneificary;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getBiller() {
		return biller;
	}

	public void setBiller(String biller) {
		this.biller = biller;
	}

	public String getBeneificary() {
		return beneificary;
	}

	public void setBeneificary(String beneificary) {
		this.beneificary = beneificary;
	}

}
