package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.Date;


public class ConsumerTransactionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date date;
	private int useCaseId;
	private String reserved1;
	private String reserved2;
	private boolean debitFlags;
	private Long amount;
	private Long fee;
	private Long balance;
	private String type;
	private String errorCode;
	private String txnId;
	private String name;
	private String details;
	
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the useCaseId
	 */
	public int getUseCaseId() {
		return useCaseId;
	}
	
	/**
	 * @param useCaseId the useCaseId to set
	 */
	public void setUseCaseId(int useCaseId) {
		this.useCaseId = useCaseId;
	}
	
	/**
	 * @return the reserved1
	 */
	public String getReserved1() {
		return reserved1;
	}
	
	/**
	 * @param reserved1 the reserved1 to set
	 */
	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}
	
	/**
	 * @return the reserved2
	 */
	public String getReserved2() {
		return reserved2;
	}
	
	/**
	 * @param reserved2 the reserved2 to set
	 */
	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	
	/**
	 * @return the debitFlags
	 */
	public boolean isDebitFlags() {
		return debitFlags;
	}
	
	/**
	 * @param debitFlags the debitFlags to set
	 */
	public void setDebitFlags(boolean debitFlags) {
		this.debitFlags = debitFlags;
	}
	
	/**
	 * @return the amount
	 */
	public Long getAmount() {
		return amount;
	}
	
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Long amount) {
		this.amount = amount;
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
	 * @return the balance
	 */
	public Long getBalance() {
		return balance;
	}
	
	/**
	 * @param balance the balance to set
	 */
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * @return the txnId
	 */
	public String getTxnId() {
		return txnId;
	}
	
	/**
	 * @param txnId the txnId to set
	 */
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}
	
	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
	}
	
}
