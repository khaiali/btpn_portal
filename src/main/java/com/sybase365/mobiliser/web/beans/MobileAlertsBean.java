/**
 * 
 */
package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;

/**
 * @author Pavan Raya
 * 
 */
public class MobileAlertsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String amount;
    private String minAmount;
    private String maxAmount;
    private String transactionType;
    private String logicOperator;

    public void setAmount(String amount) {
	this.amount = amount;
    }

    public String getAmount() {
	return amount;
    }

    public void setMinAmount(String minAmount) {
	this.minAmount = minAmount;
    }

    public String getMinAmount() {
	return minAmount;
    }

    public void setMaxAmount(String maxAmount) {
	this.maxAmount = maxAmount;
    }

    public String getMaxAmount() {
	return maxAmount;
    }

    public void setTransactionType(String transactionType) {
	this.transactionType = transactionType;
    }

    public String getTransactionType() {
	return transactionType;
    }

    public void setLogicOperator(String logicOperator) {
	this.logicOperator = logicOperator;
    }

    public String getLogicOperator() {
	return logicOperator;
    }

}
