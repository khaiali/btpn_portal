package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Manage Limit
 * 
 * @author Vikram Gunda
 */
public class ManageLimitBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String action;

	private CodeValue productId;

	private CodeValue useCaseId;

	private boolean isPerCustomer = true;

	private boolean isApplyToPayee;

	private Long dailyLimit;

	private Long weeklyLimit;

	private Long monthlyLimit;

	private Long limitClassId;
	
	private Long limitSetId;

	public CodeValue getProductId() {
		return productId;
	}

	public void setProductId(final CodeValue productId) {
		this.productId = productId;
	}

	public CodeValue getUseCaseId() {
		return useCaseId;
	}

	public void setUseCaseId(final CodeValue useCaseId) {
		this.useCaseId = useCaseId;
	}

	public boolean getIsPerCustomer() {
		return isPerCustomer;
	}

	public boolean getIsApplyToPayee() {
		return isApplyToPayee;
	}

	public void setIsApplyToPayee(boolean isApplyToPayee) {
		this.isApplyToPayee = isApplyToPayee;
	}

	public Long getDailyLimit() {
		return dailyLimit;
	}

	public void setDailyLimit(Long dailyLimit) {
		this.dailyLimit = dailyLimit;
	}

	public Long getWeeklyLimit() {
		return weeklyLimit;
	}

	public void setWeeklyLimit(Long weeklyLimit) {
		this.weeklyLimit = weeklyLimit;
	}

	public Long getMonthlyLimit() {
		return monthlyLimit;
	}

	public void setMonthlyLimit(Long monthlyLimit) {
		this.monthlyLimit = monthlyLimit;
	}

	public void setIsPerCustomer(boolean isPerCustomer) {
		this.isPerCustomer = isPerCustomer;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getLimitClassId() {
		return limitClassId;
	}

	public void setLimitClassId(Long limitClassId) {
		this.limitClassId = limitClassId;
	}

	public Long getLimitSetId() {
		return limitSetId;
	}

	public void setLimitSetId(Long limitSetId) {
		this.limitSetId = limitSetId;
	}
	
}
