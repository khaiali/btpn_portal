package com.sybase365.mobiliser.web.beans.portal;

import java.io.Serializable;

public class TransactionHistoryBean implements Serializable {

    public TransactionHistoryBean() {
    }

    public TransactionHistoryBean(String date, String type, String errorCode,
	    String reference, String participantName, String details,
	    String amount, String detLink, String fee) {
	super();
	this.date = date;
	this.type = type;
	this.errorCode = errorCode;
	this.reference = reference;
	this.participantName = participantName;
	this.details = details;
	this.amount = amount;
	this.detailsLink = detLink;
	this.fee = fee;
    }

    private String fee;

    public String getFee() {
	return fee;
    }

    public void setFee(String fee) {
	this.fee = fee;
    }

    private String detailsLink;

    public String getDetailsLink() {
	return detailsLink;
    }

    public void setDetailsLink(String detailsLink) {
	this.detailsLink = detailsLink;
    }

    private String date;

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getErrorCode() {
	return errorCode;
    }

    public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
    }

    public String getReference() {
	return reference;
    }

    public void setReference(String reference) {
	this.reference = reference;
    }

    public String getParticipantName() {
	return participantName;
    }

    public void setParticipantName(String participantName) {
	this.participantName = participantName;
    }

    public String getDetails() {
	return details;
    }

    public void setDetails(String details) {
	this.details = details;
    }

    public String getAmount() {
	return amount;
    }

    public void setAmount(String amount) {
	this.amount = amount;
    }

    private String type;
    private String errorCode;
    private String reference;
    private String participantName;
    private String details;
    private String amount;

}
