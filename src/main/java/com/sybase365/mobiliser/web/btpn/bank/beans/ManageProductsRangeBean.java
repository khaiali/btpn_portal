package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Manage Products
 * 
 * @author Vikram Gunda
 */
public class ManageProductsRangeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long minRange;

	private Long maxRange;

	private String interest;

	private String value;

	private Long fixedAmount;

	private String percentageAmount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMinRange() {
		return minRange;
	}

	public void setMinRange(Long minRange) {
		this.minRange = minRange;
	}

	public Long getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Long maxRange) {
		this.maxRange = maxRange;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getFixedAmount() {
		return fixedAmount;
	}

	public void setFixedAmount(Long fixedAmount) {
		this.fixedAmount = fixedAmount;
	}

	public String getPercentageAmount() {
		return percentageAmount;
	}

	public void setPercentageAmount(String percentageAmount) {
		this.percentageAmount = percentageAmount;
	}

}
