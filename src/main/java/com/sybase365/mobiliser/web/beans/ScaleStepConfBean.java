package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class ScaleStepConfBean implements Serializable {

    public String getCurrency() {
	return currency;
    }

    public void setCurrency(String currency) {
	this.currency = currency;
    }

    public Long getMinAmount() {
	return minAmount;
    }

    public void setMinAmount(Long minAmount) {
	this.minAmount = minAmount;
    }

    public Long getMaxAmount() {
	return maxAmount;
    }

    public void setMaxAmount(Long maxAmount) {
	this.maxAmount = maxAmount;
    }

    public BigDecimal getPercentage() {
	return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
	this.percentage = percentage;
    }

    public Long getOnTop() {
	return onTop;
    }

    public void setOnTop(Long onTop) {
	this.onTop = onTop;
    }

    private Long scalePeriodId;

    public Long getScalePeriodId() {
	return scalePeriodId;
    }

    public void setScalePeriodId(Long scalePeriodId) {
	this.scalePeriodId = scalePeriodId;
    }

    private String feeTypeName;

    public String getFeeTypeName() {
	return feeTypeName;
    }

    public void setFeeTypeName(String feeTypeName) {
	this.feeTypeName = feeTypeName;
    }

    private Integer feeTypeId;

    public Integer getFeeTypeId() {
	return feeTypeId;
    }

    public void setFeeTypeId(Integer feeTypeId) {
	this.feeTypeId = feeTypeId;
    }

    private String currency;
    private Long thresholdAmount;

    public Long getThresholdAmount() {
	return thresholdAmount;
    }

    public void setThresholdAmount(Long thresholdAmount) {
	this.thresholdAmount = thresholdAmount;
    }

    private Long minAmount;
    private Long maxAmount;
    private BigDecimal percentage;
    private Long onTop;
    private String cssStyle;

    public String getCssStyle() {
	return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
	this.cssStyle = cssStyle;
    }

}
