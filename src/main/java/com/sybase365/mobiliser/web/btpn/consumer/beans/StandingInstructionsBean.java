package com.sybase365.mobiliser.web.btpn.consumer.beans;

import java.io.Serializable;
import java.util.List;

import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;

public class StandingInstructionsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String payer;
	private String payee;
	private String type;
	private String startDate;
	private String expiryDate;
	private String frequency;
	private String weekDay;
	private String amount;

	private boolean removeSuccessFlag = false;
	private boolean addSuccessFlag = false;
	private boolean topupSuccess = false;
	private boolean ftSuccess = false;
	private CodeValue instructionType;

	private List<StandingInstructionsBean> instructionsList;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public boolean isRemoveSuccessFlag() {
		return removeSuccessFlag;
	}

	public void setRemoveSuccessFlag(boolean removeSuccessFlag) {
		this.removeSuccessFlag = removeSuccessFlag;
	}

	public boolean isAddSuccessFlag() {
		return addSuccessFlag;
	}

	public void setAddSuccessFlag(boolean addSuccessFlag) {
		this.addSuccessFlag = addSuccessFlag;
	}

	public CodeValue getInstructionType() {
		return instructionType;
	}

	public void setInstructionType(CodeValue instructionType) {
		this.instructionType = instructionType;
	}

	public List<StandingInstructionsBean> getInstructionsList() {
		return instructionsList;
	}

	public void setInstructionsList(List<StandingInstructionsBean> instructionsList) {
		this.instructionsList = instructionsList;
	}

	public boolean isTopupSuccess() {
		return topupSuccess;
	}

	public void setTopupSuccess(boolean topupSuccess) {
		this.topupSuccess = topupSuccess;
	}

	public boolean isFtSuccess() {
		return ftSuccess;
	}

	public void setFtSuccess(boolean ftSuccess) {
		this.ftSuccess = ftSuccess;
	}

}
