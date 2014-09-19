package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.Date;

public class TxnDataBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;

	private Date date;
	
	private CodeValue useCase;
	
	private String description;

	private String type;

	private String status;

	private long amount;
	
	private long fee;

	private Long debitAmount;

	private Long creditAmount;

	private Long runningBalance;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public CodeValue getUseCase() {
		return useCase;
	}

	public void setUseCase(CodeValue useCase) {
		this.useCase = useCase;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getFee() {
		return fee;
	}

	public void setFee(long fee) {
		this.fee = fee;
	}

	public Long getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Long debitAmount) {
		this.debitAmount = debitAmount;
	}

	public Long getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Long creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Long getRunningBalance() {
		return runningBalance;
	}

	public void setRunningBalance(Long runningBalance) {
		this.runningBalance = runningBalance;
	}
}
