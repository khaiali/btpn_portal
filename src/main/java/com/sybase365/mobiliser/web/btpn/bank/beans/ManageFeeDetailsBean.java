package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the bean used for Manage Fee bean
 * 
 * @author Vikram Gunda
 */
public class ManageFeeDetailsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String feeName;

	private CodeValue glCode;

	private Long fixedFee;

	private BigDecimal percentageFee;

	private String amount;

	private String amountType;

	private Long minValue;

	private Long maxValue;

	private Integer feeTypeId;

	private Long feeScaleId;

	private Long feeScalePeriodId;

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public CodeValue getGlCode() {
		return glCode;
	}

	public void setGlCode(CodeValue glCode) {
		this.glCode = glCode;
	}

	public Long getFixedFee() {
		return fixedFee;
	}

	public void setFixedFee(Long fixedFee) {
		this.fixedFee = fixedFee;
	}

	public BigDecimal getPercentageFee() {
		return percentageFee;
	}

	public void setPercentageFee(BigDecimal percentageFee) {
		this.percentageFee = percentageFee;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAmountType() {
		if (!PortalUtils.exists(amountType)) {
			amountType = BtpnConstants.FIXED_INTEREST_RADIO;
		}
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public Long getMinValue() {
		return minValue;
	}

	public void setMinValue(Long minValue) {
		this.minValue = minValue;
	}

	public Long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}

	public Integer getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(Integer feeTypeId) {
		this.feeTypeId = feeTypeId;
	}

	public Long getFeeScaleId() {
		return feeScaleId;
	}

	public void setFeeScaleId(Long feeScaleId) {
		this.feeScaleId = feeScaleId;
	}

	public Long getFeeScalePeriodId() {
		return feeScalePeriodId;
	}

	public void setFeeScalePeriodId(Long feeScalePeriodId) {
		this.feeScalePeriodId = feeScalePeriodId;
	}	
}
