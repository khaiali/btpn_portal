package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;
import java.util.Date;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

public class BillPaymentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue billerType;
	private CodeValue subBillerType;
	private CodeValue favoriteNumber;
	private CodeValue selectedBillerId;
	
	private String phonenumber;
	private Long baseAmount;
	private String amountFixed;
	private String amountCharged;
	private String siName;
	private Date startDate;
	private Date expiryDate;
	private CodeValue selectedDay;
	private String frequencyMonth;
	private String frequencyQuarter;
	private String frequencyWeek;
	private String frequencyDate;
	private String frequencyType;
	private boolean selectedFixedAmt;
	private boolean selectedChargedAmt;
	private String billNumber;
	
	private String manualOrFavourite;
	private String label;
	private String productId;
	private String billerId;
	private String productName;
	
	
	public CodeValue getBillerType() {
		return billerType;
	}

	public void setBillerType(CodeValue billerType) {
		this.billerType = billerType;
	}

	public CodeValue getSubBillerType() {
		return subBillerType;
	}

	public void setSubBillerType(CodeValue subBillerType) {
		this.subBillerType = subBillerType;
	}

	public CodeValue getFavoriteNumber() {
		return favoriteNumber;
	}

	public void setFavoriteNumber(CodeValue favoriteNumber) {
		this.favoriteNumber = favoriteNumber;
	}

	public CodeValue getSelectedBillerId() {
		return selectedBillerId;
	}

	public void setSelectedBillerId(CodeValue selectedBillerId) {
		this.selectedBillerId = selectedBillerId;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public Long getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(Long baseAmount) {
		this.baseAmount = baseAmount;
	}

	public String getAmountFixed() {
		return amountFixed;
	}

	public void setAmountFixed(String amountFixed) {
		this.amountFixed = amountFixed;
	}

	public String getAmountCharged() {
		return amountCharged;
	}

	public void setAmountCharged(String amountCharged) {
		this.amountCharged = amountCharged;
	}

	public String getSiName() {
		return siName;
	}

	public void setSiName(String siName) {
		this.siName = siName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public CodeValue getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(CodeValue selectedDay) {
		this.selectedDay = selectedDay;
	}

	public String getFrequencyMonth() {
		return frequencyMonth;
	}

	public void setFrequencyMonth(String frequencyMonth) {
		this.frequencyMonth = frequencyMonth;
	}

	public String getFrequencyQuarter() {
		return frequencyQuarter;
	}

	public void setFrequencyQuarter(String frequencyQuarter) {
		this.frequencyQuarter = frequencyQuarter;
	}

	public String getFrequencyWeek() {
		return frequencyWeek;
	}

	public void setFrequencyWeek(String frequencyWeek) {
		this.frequencyWeek = frequencyWeek;
	}

	public String getFrequencyDate() {
		return frequencyDate;
	}

	public void setFrequencyDate(String frequencyDate) {
		this.frequencyDate = frequencyDate;
	}

	public String getFrequencyType() {
		return frequencyType;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

	public boolean isSelectedFixedAmt() {
		return selectedFixedAmt;
	}

	public void setSelectedFixedAmt(boolean selectedFixedAmt) {
		this.selectedFixedAmt = selectedFixedAmt;
	}

	public boolean isSelectedChargedAmt() {
		return selectedChargedAmt;
	}

	public void setSelectedChargedAmt(boolean selectedChargedAmt) {
		this.selectedChargedAmt = selectedChargedAmt;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getManualOrFavourite() {
		return manualOrFavourite;
	}

	public void setManualOrFavourite(String manualOrFavourite) {
		this.manualOrFavourite = manualOrFavourite;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getBillerId() {
		return billerId;
	}

	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

}
