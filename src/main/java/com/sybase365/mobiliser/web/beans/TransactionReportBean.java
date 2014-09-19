package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

import javax.xml.datatype.XMLGregorianCalendar;

public class TransactionReportBean implements Serializable {

    private Long txnId;
    private XMLGregorianCalendar txnDate;
    private String txnType;
    private String txnAmount;
    private Long useCaseId;
    private String useCaseName;
    private String orderId;
    private String delearName;
    private Long agentId;
    private String agentDisplayName;
    private String commissionAmount;
    private int count;
    private String balance;

    public Long getTxnId() {
        return txnId;
    }

    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public Long getUseCaseId() {
        return useCaseId;
    }

    public void setUseCaseId(Long useCaseId) {
        this.useCaseId = useCaseId;
    }

    public String getUseCaseName() {
        return useCaseName;
    }

    public void setUseCaseName(String useCaseName) {
        this.useCaseName = useCaseName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDelearName() {
        return delearName;
    }

    public void setDelearName(String delearName) {
        this.delearName = delearName;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getAgentDisplayName() {
        return agentDisplayName;
    }

    public void setAgentDisplayName(String agentDisplayName) {
        this.agentDisplayName = agentDisplayName;
    }

    public String getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(String commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setTxnDate(XMLGregorianCalendar txnDate) {
        this.txnDate = txnDate;
    }

    public XMLGregorianCalendar getTxnDate() {
        return txnDate;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalance() {
        return balance;
    }

}
