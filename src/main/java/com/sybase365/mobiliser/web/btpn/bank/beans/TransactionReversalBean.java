package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This is the bean used for Bank Portal transaction Operations
 * 
 * @author Narasa Reddy
 */
public class TransactionReversalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String transactionID;

	private String transactionName;

	private String transactionDate;

	private XMLGregorianCalendar txnDate;

	private String transactionAmount;

	private String mobileNumber;

	private boolean msisdn = false;

	private String maker;

	private String useCase;

	private String status;

	private String selectedLink;

	private TransactionReversalBean currentValue;

	private TransactionReversalBean newValue;

	private String taskId;

	private boolean approveSuccess = false;

	private boolean rejectSuccess = false;

	private boolean success;

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public boolean isMsisdn() {
		return msisdn;
	}

	public void setMsisdn(boolean msisdn) {
		this.msisdn = msisdn;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSelectedLink() {
		return selectedLink;
	}

	public void setSelectedLink(String selectedLink) {
		this.selectedLink = selectedLink;
	}

	public TransactionReversalBean getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(TransactionReversalBean currentValue) {
		this.currentValue = currentValue;
	}

	public TransactionReversalBean getNewValue() {
		return newValue;
	}

	public void setNewValue(TransactionReversalBean newValue) {
		this.newValue = newValue;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isApproveSuccess() {
		return approveSuccess;
	}

	public void setApproveSuccess(boolean approveSuccess) {
		this.approveSuccess = approveSuccess;
	}

	public boolean isRejectSuccess() {
		return rejectSuccess;
	}

	public void setRejectSuccess(boolean rejectSuccess) {
		this.rejectSuccess = rejectSuccess;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public XMLGregorianCalendar getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(XMLGregorianCalendar txnDate) {
		this.txnDate = txnDate;
	}

}
