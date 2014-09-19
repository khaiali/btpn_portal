package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Manage Limit
 * 
 * @author Vikram Gunda
 */
public class ApproveLimitBean extends ManageLimitBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String newAction;
	
	private CodeValue newProductId;

	private CodeValue newUseCaseId;

	private boolean newIsPerCustomer;

	private boolean newIsApplyToPayee;

	private Long newDailyLimit;

	private Long newWeeklyLimit;

	private Long newMonthlyLimit;

	public CodeValue getNewProductId() {
		return newProductId;
	}

	public void setNewProductId(CodeValue newProductId) {
		this.newProductId = newProductId;
	}

	public CodeValue getNewUseCaseId() {
		return newUseCaseId;
	}

	public void setNewUseCaseId(CodeValue newUseCaseId) {
		this.newUseCaseId = newUseCaseId;
	}

	public Boolean getNewIsPerCustomer() {
		return newIsPerCustomer;
	}

	public void setNewIsPerCustomer(Boolean newIsPerCustomer) {
		this.newIsPerCustomer = newIsPerCustomer;
	}

	public Boolean getNewIsApplyToPayee() {
		return newIsApplyToPayee;
	}

	public void setNewIsApplyToPayee(Boolean newIsApplyToPayee) {
		this.newIsApplyToPayee = newIsApplyToPayee;
	}

	public Long getNewDailyLimit() {
		return newDailyLimit;
	}

	public void setNewDailyLimit(Long newDailyLimit) {
		this.newDailyLimit = newDailyLimit;
	}

	public Long getNewWeeklyLimit() {
		return newWeeklyLimit;
	}

	public void setNewWeeklyLimit(Long newWeeklyLimit) {
		this.newWeeklyLimit = newWeeklyLimit;
	}

	public Long getNewMonthlyLimit() {
		return newMonthlyLimit;
	}

	public void setNewMonthlyLimit(Long newMonthlyLimit) {
		this.newMonthlyLimit = newMonthlyLimit;
	}

	public String getNewAction() {
		return newAction;
	}

	public void setNewAction(String newAction) {
		this.newAction = newAction;
	}
	
}
