package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the ApproveCustomerDataBean for to display list of approve customer details.
 * 
 * @author Narasa Reddy
 */
public class ApproveCustomerDataBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String atmCardNo;

	private String balckListReason;

	private String dateOfBirth;

	private String emailId;

	private String employeeId;

	private String gender;

	private String highRiskBusiness;

	private String highRiskCustomer;

	private String idCardNo;

	private String idExpirationDate;

	private String idType;

	private String income;

	private String industryOfEmployer;

	private String taxExempted;

	private String job;

	private String language;

	private String maritalStatus;

	private String mothersMaidenName;

	private String name;

	private String nationality;

	private String occupation;

	private CodeValue religion;

	private CodeValue lastEducation;

	private CodeValue foreCastTransaction;

	private String taxCardNumber;

	private String placeOfBirth;

	private String purposeOfAccount;

	private String receiptMode;

	private String shortName;

	private String sourceofFound;

	private String status;

	private String street1;

	private String street2;

	private CodeValue province;

	private CodeValue city;

	private String zipCode;

	private Long glCode;

	private String marketingSourceCode;

	private String referralNumber;

	private String agentCode;

	private ApproveCustomerDataBean currentValue;

	private ApproveCustomerDataBean newValue;

	private String taskId;

	private boolean approveSuccess = false;

	private boolean rejectSuccess = false;

	public String getAtmCardNo() {
		return atmCardNo;
	}

	public void setAtmCardNo(String atmCardNo) {
		this.atmCardNo = atmCardNo;
	}

	public String getBalckListReason() {
		return balckListReason;
	}

	public void setBalckListReason(String balckListReason) {
		this.balckListReason = balckListReason;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHighRiskBusiness() {
		return highRiskBusiness;
	}

	public void setHighRiskBusiness(String highRiskBusiness) {
		this.highRiskBusiness = highRiskBusiness;
	}

	public String getHighRiskCustomer() {
		return highRiskCustomer;
	}

	public void setHighRiskCustomer(String highRiskCustomer) {
		this.highRiskCustomer = highRiskCustomer;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getIdExpirationDate() {
		return idExpirationDate;
	}

	public void setIdExpirationDate(String idExpirationDate) {
		this.idExpirationDate = idExpirationDate;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getIndustryOfEmployer() {
		return industryOfEmployer;
	}

	public void setIndustryOfEmployer(String industryOfEmployer) {
		this.industryOfEmployer = industryOfEmployer;
	}

	public String getTaxExempted() {
		return taxExempted;
	}

	public void setTaxExempted(String taxExempted) {
		this.taxExempted = taxExempted;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getMothersMaidenName() {
		return mothersMaidenName;
	}

	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public String getPurposeOfAccount() {
		return purposeOfAccount;
	}

	public void setPurposeOfAccount(String purposeOfAccount) {
		this.purposeOfAccount = purposeOfAccount;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getSourceofFound() {
		return sourceofFound;
	}

	public void setSourceofFound(String sourceofFound) {
		this.sourceofFound = sourceofFound;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public ApproveCustomerDataBean getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(ApproveCustomerDataBean currentValue) {
		this.currentValue = currentValue;
	}

	public ApproveCustomerDataBean getNewValue() {
		return newValue;
	}

	public void setNewValue(ApproveCustomerDataBean newValue) {
		this.newValue = newValue;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isApproveSuccess() {
		return approveSuccess;
	}

	public void setApproveSuccess(boolean approveSuccess) {
		this.approveSuccess = approveSuccess;
	}

	public boolean isRejectSuccess() {
		return rejectSuccess;
	}

	public void setRejectSuccess(boolean rejectSuccess) {
		this.rejectSuccess = rejectSuccess;
	}

	public Long getGlCode() {
		return glCode;
	}

	public void setGlCode(Long glCode) {
		this.glCode = glCode;
	}

	public CodeValue getReligion() {
		return religion;
	}

	public void setReligion(CodeValue religion) {
		this.religion = religion;
	}

	public CodeValue getLastEducation() {
		return lastEducation;
	}

	public void setLastEducation(CodeValue lastEducation) {
		this.lastEducation = lastEducation;
	}

	public CodeValue getForeCastTransaction() {
		return foreCastTransaction;
	}

	public void setForeCastTransaction(CodeValue foreCastTransaction) {
		this.foreCastTransaction = foreCastTransaction;
	}

	public String getTaxCardNumber() {
		return taxCardNumber;
	}

	public void setTaxCardNumber(String taxCardNumber) {
		this.taxCardNumber = taxCardNumber;
	}

	public CodeValue getProvince() {
		return province;
	}

	public void setProvince(CodeValue province) {
		this.province = province;
	}

	public CodeValue getCity() {
		return city;
	}

	public void setCity(CodeValue city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getMarketingSourceCode() {
		return marketingSourceCode;
	}

	public void setMarketingSourceCode(String marketingSourceCode) {
		this.marketingSourceCode = marketingSourceCode;
	}

	public String getReferralNumber() {
		return referralNumber;
	}

	public void setReferralNumber(String referralNumber) {
		this.referralNumber = referralNumber;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
}
