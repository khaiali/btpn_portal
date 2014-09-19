package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the bean for Manual Advice Information.
 * 
 * @author Vikram Gunda
 */
public class ManualAdviceBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long transactionId;

	private Date transactionDate;

	private Long amount;

	private String billerId;

	private Long feeAmount;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getBillerId() {
		return billerId;
	}

	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}

	public Long getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Long feeAmount) {
		this.feeAmount = feeAmount;
	}

}
