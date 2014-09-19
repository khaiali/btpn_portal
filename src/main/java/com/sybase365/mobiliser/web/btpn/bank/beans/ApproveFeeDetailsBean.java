package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This is the bean used for Manage Fee bean
 * 
 * @author Vikram Gunda
 */
public class ApproveFeeDetailsBean extends ManageFeeDetailsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String newFeeName;

	private CodeValue newGlCode;

	private Long newFixedFee;

	private BigDecimal newPercentageFee;

	private Long newAmount;

	private String newAmountType;

	private Long newMinValue;

	private Long newMaxValue;

	public String getNewFeeName() {
		return newFeeName;
	}

	public void setNewFeeName(String newFeeName) {
		this.newFeeName = newFeeName;
	}

	public CodeValue getNewGlCode() {
		return newGlCode;
	}

	public void setNewGlCode(CodeValue newGlCode) {
		this.newGlCode = newGlCode;
	}

	public Long getNewFixedFee() {
		return newFixedFee;
	}

	public void setNewFixedFee(Long newFixedFee) {
		this.newFixedFee = newFixedFee;
	}

	public BigDecimal getNewPercentageFee() {
		return newPercentageFee;
	}

	public void setNewPercentageFee(BigDecimal newPercentageFee) {
		this.newPercentageFee = newPercentageFee;
	}

	public Long getNewAmount() {
		return newAmount;
	}

	public void setNewAmount(Long newAmount) {
		this.newAmount = newAmount;
	}

	public String getNewAmountType() {
		return newAmountType;
	}

	public void setNewAmountType(String newAmountType) {
		this.newAmountType = newAmountType;
	}

	public Long getNewMinValue() {
		return newMinValue;
	}

	public void setNewMinValue(Long newMinValue) {
		this.newMinValue = newMinValue;
	}

	public Long getNewMaxValue() {
		return newMaxValue;
	}

	public void setNewMaxValue(Long newMaxValue) {
		this.newMaxValue = newMaxValue;
	}

}
