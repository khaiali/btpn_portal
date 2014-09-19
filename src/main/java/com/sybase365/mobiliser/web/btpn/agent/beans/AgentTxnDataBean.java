package com.sybase365.mobiliser.web.btpn.agent.beans;

import java.io.Serializable;
import java.util.Date;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

public class AgentTxnDataBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date date;
	private String agentId;
	private CodeValue txnType;
	private String txnId;
	private String status;
	private Long fee;
	private double amount;
	private Long txnAmount;
	private String description;
	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public CodeValue getTxnType() {
		return txnType;
	}

	public void setTxnType(CodeValue txnType) {
		this.txnType = txnType;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Long getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(Long txnAmount) {
		this.txnAmount = txnAmount;
	}

	/**
	 * @return the fee
	 */
	public Long getFee() {
		return fee;
	}

	/**
	 * @param fee the fee to set
	 */
	public void setFee(Long fee) {
		this.fee = fee;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
