package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Manage Products
 * 
 * @author Vikram Gunda
 */
public class ManageProductsApproveRangeBean extends ManageProductsRangeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long newMinRange;

	private Long newMaxRange;

	private String newInterest;

	private Long newFixedAmount;

	private String newPercentageAmount;

	public Long getNewMinRange() {
		return newMinRange;
	}

	public void setNewMinRange(Long newMinRange) {
		this.newMinRange = newMinRange;
	}

	public Long getNewMaxRange() {
		return newMaxRange;
	}

	public void setNewMaxRange(Long newMaxRange) {
		this.newMaxRange = newMaxRange;
	}

	public String getNewInterest() {
		return newInterest;
	}

	public void setNewInterest(String newInterest) {
		this.newInterest = newInterest;
	}

	public Long getNewFixedAmount() {
		return newFixedAmount;
	}

	public void setNewFixedAmount(Long newFixedAmount) {
		this.newFixedAmount = newFixedAmount;
	}

	public String getNewPercentageAmount() {
		return newPercentageAmount;
	}

	public void setNewPercentageAmount(String newPercentageAmount) {
		this.newPercentageAmount = newPercentageAmount;
	}

}
