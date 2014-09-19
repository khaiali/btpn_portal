package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;


public class ManageBillPaymentFeeDetailsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private long thresholdAmount;
	private long fixedFee;
	private long percentageFee;
	private Long maximumFee;
	private long minimumFee;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public long getThresholdAmount() {
		return thresholdAmount;
	}

	public void setThresholdAmount(long thresholdAmount) {
		this.thresholdAmount = thresholdAmount;
	}

	public long getFixedFee() {
		return fixedFee;
	}

	public void setFixedFee(long fixedFee) {
		this.fixedFee = fixedFee;
	}
	
	public long getPercentageFee() {
		return percentageFee;
	}

	public void setPercentageFee(long percentageFee) {
		this.percentageFee = percentageFee;
	}

	
	public Long getMaximumFee() {
		return maximumFee;
	}

	public void setMaximumFee(Long maximumFee) {
		this.maximumFee = maximumFee;
	}

	public long getMinimumFee() {
		return minimumFee;
	}

	public void setMinimumFee(long minimumFee) {
		this.minimumFee = minimumFee;
	}
}
