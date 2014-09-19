package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Manage Fee bean
 * 
 * @author Vikram Gunda
 */
public class ManageFeeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String action;
	
	private CodeValue customerType;
	
	private CodeValue piType;
	
	private String description;
	
	private CodeValue orgUnit;

	private CodeValue product;

	private CodeValue useCase;

	private boolean applyToPayee;
	
	private CodeValue currency;

	private Long thresholdAmount;
	
	private Long fixedFee;
	
	private Long percentageFee;
	
	private Long maximumFee;
	
	private Long minimumFee;
	
	private CodeValue glCode;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public CodeValue getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CodeValue customerType) {
		this.customerType = customerType;
	}

	public CodeValue getPiType() {
		return piType;
	}

	public void setPiType(CodeValue piType) {
		this.piType = piType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CodeValue getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(CodeValue orgUnit) {
		this.orgUnit = orgUnit;
	}

	public CodeValue getProduct() {
		return product;
	}

	public void setProduct(CodeValue product) {
		this.product = product;
	}

	public CodeValue getUseCase() {
		return useCase;
	}

	public void setUseCase(CodeValue useCase) {
		this.useCase = useCase;
	}

	public boolean isApplyToPayee() {
		return applyToPayee;
	}

	public void setApplyToPayee(boolean applyToPayee) {
		this.applyToPayee = applyToPayee;
	}

	public CodeValue getCurrency() {
		return currency;
	}

	public void setCurrency(CodeValue currency) {
		this.currency = currency;
	}

	public Long getThresholdAmount() {
		return thresholdAmount;
	}

	public void setThresholdAmount(Long thresholdAmount) {
		this.thresholdAmount = thresholdAmount;
	}

	public Long getFixedFee() {
		return fixedFee;
	}

	public void setFixedFee(Long fixedFee) {
		this.fixedFee = fixedFee;
	}

	public Long getPercentageFee() {
		return percentageFee;
	}

	public void setPercentageFee(Long percentageFee) {
		this.percentageFee = percentageFee;
	}

	public Long getMaximumFee() {
		return maximumFee;
	}

	public void setMaximumFee(Long maximumFee) {
		this.maximumFee = maximumFee;
	}

	public Long getMinimumFee() {
		return minimumFee;
	}

	public void setMinimumFee(Long minimumFee) {
		this.minimumFee = minimumFee;
	}

	public CodeValue getGlCode() {
		return glCode;
	}

	public void setGlCode(CodeValue glCode) {
		this.glCode = glCode;
	}
}
