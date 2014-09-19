package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

import javax.xml.datatype.XMLGregorianCalendar;

public class SearchTransactionCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long customerId;
    private XMLGregorianCalendar fromDateXml;
    private XMLGregorianCalendar toDateXml;
    private Integer maxNumberToFetch;
    private Long joinedCustomerId;
    private Integer txnStatus;
    private Boolean showFaulty;
    private Boolean showInitial;
    private Boolean consumerIsPayer;
    private String orderID;
    private String txnID;
    private Long callerId;
    private Integer useCaseId;
    private boolean showAuthorizedCancel = true;

    public Long getCustomerId() {
	return customerId;
    }

    public void setCustomerId(Long customerId) {
	this.customerId = customerId;
    }

    public XMLGregorianCalendar getFromDateXml() {
	return fromDateXml;
    }

    public void setFromDateXml(XMLGregorianCalendar fromDateXml) {
	this.fromDateXml = fromDateXml;
    }

    public XMLGregorianCalendar getToDateXml() {
	return toDateXml;
    }

    public void setToDateXml(XMLGregorianCalendar toDateXml) {
	this.toDateXml = toDateXml;
    }

    public Integer getMaxNumberToFetch() {
	return maxNumberToFetch;
    }

    public void setMaxNumberToFetch(Integer maxNumberToFetch) {
	this.maxNumberToFetch = maxNumberToFetch;
    }

    public Long getJoinedCustomerId() {
	return joinedCustomerId;
    }

    public void setJoinedCustomerId(Long joinedCustomerId) {
	this.joinedCustomerId = joinedCustomerId;
    }

    public Integer getTxnStatus() {
	return txnStatus;
    }

    public void setTxnStatus(Integer txnStatus) {
	this.txnStatus = txnStatus;
    }

    public Boolean getShowFaulty() {
	return showFaulty;
    }

    public void setShowFaulty(Boolean showFaulty) {
	this.showFaulty = showFaulty;
    }

    public Boolean getShowInitial() {
	return showInitial;
    }

    public void setShowInitial(Boolean showInitial) {
	this.showInitial = showInitial;
    }

    public Boolean getConsumerIsPayer() {
	return consumerIsPayer;
    }

    public void setConsumerIsPayer(Boolean consumerIsPayer) {
	this.consumerIsPayer = consumerIsPayer;
    }

    public String getOrderID() {
	return orderID;
    }

    public void setOrderID(String orderID) {
	this.orderID = orderID;
    }

    public String getTxnID() {
	return txnID;
    }

    public void setTxnID(String txnID) {
	this.txnID = txnID;
    }

    public Long getCallerId() {
	return callerId;
    }

    public void setCallerId(Long callerId) {
	this.callerId = callerId;
    }

    public Integer getUseCaseId() {
	return useCaseId;
    }

    public void setUseCaseId(Integer useCaseId) {
	this.useCaseId = useCaseId;
    }

    public boolean getShowAuthorizedCancel() {
	return showAuthorizedCancel;
    }

    public void setShowAuthorizedCancel(boolean showAuthorizedCancel) {
	this.showAuthorizedCancel = showAuthorizedCancel;
    }

}
