package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;
import java.util.Date;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;


public class AirTimeTopupBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue selectedTelco;
	private CodeValue selectedMsisdn;
	private String phonenumber;
	private CodeValue favoriteNumber;
	private CodeValue denomination;
	private String topupAmount;
	private String topupMobile;
	private String siName;
	private Date startDate;
	private Date expiryDate;
	private CodeValue selectedDay;
	private String frequencyMonth;
	private String frequencyQuarter;
	private String frequencyWeek;
	private String frequencyDate;
	private String frequencyType;

	private String manualOrFavourite;
	private String label;
	private String productId;
	private String billerId;
	private String productName;
	
	
	public CodeValue getSelectedTelco() {
		return selectedTelco;
	}

	public void setSelectedTelco(CodeValue selectedTelco) {
		this.selectedTelco = selectedTelco;
	}

	public CodeValue getSelectedMsisdn() {
		return selectedMsisdn;
	}

	public void setSelectedMsisdn(CodeValue selectedMsisdn) {
		this.selectedMsisdn = selectedMsisdn;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public CodeValue getFavoriteNumber() {
		return favoriteNumber;
	}

	public void setFavoriteNumber(CodeValue favoriteNumber) {
		this.favoriteNumber = favoriteNumber;
	}

	public CodeValue getDenomination() {
		return denomination;
	}

	public void setDenomination(CodeValue denomination) {
		this.denomination = denomination;
	}

	public String getTopupAmount() {
		return topupAmount;
	}

	public void setTopupAmount(String topupAmount) {
		this.topupAmount = topupAmount;
	}

	public String getTopupMobile() {
		return topupMobile;
	}

	public void setTopupMobile(String topupMobile) {
		this.topupMobile = topupMobile;
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
