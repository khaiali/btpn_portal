package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CustomerRegistrationBean implements Serializable {
	
	public static final String REQ_CREATE = "1";
	public static final String REQ_UPDATE = "2";
	public static final String REQ_REMOVE = "4";

	private static final long serialVersionUID = 1L;
	private long parentId;

	// Field only for front end
	private String customerType;
	private String requestType;
	private int customerTypeId;

	private CodeValue registrationType;
	private CodeValue productType;

	private String product;
	private String name;
	private String shortName;
	private String employeeId;
	private String mothersMaidenName;

	private CodeValue maritalStatus;
	private String placeOfBirth;
	private CodeValue nationality;
	private CodeValue language;
	private CodeValue receiptMode;
	private CodeValue occupation;
	private CodeValue job;
	private CodeValue industryOfEmployee;
	private CodeValue purposeOfAccount;
	private CodeValue sourceofFound;
	private CodeValue income;
	private String idCardNo;
	private CodeValue idType;
	private String idExpirationDate;
	private CodeValue highRiskCustomer;
	private CodeValue highRiskBusiness;
	private String atmCardNo;
	private CodeValue gender;

	private String emailId;
	private String customerNumber;
	private String purposeOfTransaction;
	private CodeValue glCodeId;
	private String dateOfBirth;
	private CodeValue taxExempted;
	private CodeValue optimaActivated;
	private CodeValue religion;
	private CodeValue lastEducation;
	private CodeValue foreCastTransaction;

	private String taxCardNumber;
	private String identificationAttachment;
	private String mobileNumber;
	private String street1;
	private String street2;
	private CodeValue province;
	private CodeValue city;
	private String zipCode;
	private String officerId;
	private String dateCreated;
	private String territoryCode;

	private List<NotificationAttachmentsBean> attachmentsList;

	private String createdBy;
	private String status;
	private String marketingSourceCode;
	private String referralNumber;
	private String agentCode;
	private Date birthDateString;
	private Date expireDateString;
	private String svaNumber;
	private String createdDate;
	private String message;
	private String customerId;
	private String taskId;
	private String parentSvaId;
	private CodeValue blackListReason;
	private CodeValue customerStatus;
	private Long identityId;
	private int identitiStatus;
	private Long addressId;
	private Long atmCardId;
	private boolean active;
	private int blackListReson;
	private int productCategory;


	
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	
	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public CodeValue getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(CodeValue registrationType) {
		this.registrationType = registrationType;
	}
	
	public CodeValue getProductType() {
		return productType;
	}

	public void setProductType(CodeValue productType) {
		this.productType = productType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getMothersMaidenName() {
		return mothersMaidenName;
	}

	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

	public CodeValue getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(CodeValue maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public CodeValue getNationality() {
		return nationality;
	}

	public void setNationality(CodeValue nationality) {
		this.nationality = nationality;
	}

	public CodeValue getLanguage() {
		return language;
	}

	public void setLanguage(CodeValue language) {
		this.language = language;
	}

	public CodeValue getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(CodeValue receiptMode) {
		this.receiptMode = receiptMode;
	}

	public CodeValue getOccupation() {
		return occupation;
	}

	public void setOccupation(CodeValue occupation) {
		this.occupation = occupation;
	}

	public CodeValue getJob() {
		return job;
	}

	public void setJob(CodeValue job) {
		this.job = job;
	}

	public CodeValue getIndustryOfEmployee() {
		return industryOfEmployee;
	}

	public void setIndustryOfEmployee(CodeValue industryOfEmployee) {
		this.industryOfEmployee = industryOfEmployee;
	}

	public CodeValue getPurposeOfAccount() {
		return purposeOfAccount;
	}

	public void setPurposeOfAccount(CodeValue purposeOfAccount) {
		this.purposeOfAccount = purposeOfAccount;
	}

	public CodeValue getSourceofFound() {
		return sourceofFound;
	}

	public void setSourceofFound(CodeValue sourceofFound) {
		this.sourceofFound = sourceofFound;
	}

	public CodeValue getIncome() {
		return income;
	}

	public void setIncome(CodeValue income) {
		this.income = income;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public CodeValue getIdType() {
		return idType;
	}

	public void setIdType(CodeValue idType) {
		this.idType = idType;
	}

	public String getIdExpirationDate() {
		return idExpirationDate;
	}

	public void setIdExpirationDate(String idExpirationDate) {
		this.idExpirationDate = idExpirationDate;
	}

	public CodeValue getHighRiskCustomer() {
		return highRiskCustomer;
	}

	public void setHighRiskCustomer(CodeValue highRiskCustomer) {
		this.highRiskCustomer = highRiskCustomer;
	}

	public CodeValue getHighRiskBusiness() {
		return highRiskBusiness;
	}

	public void setHighRiskBusiness(CodeValue highRiskBusiness) {
		this.highRiskBusiness = highRiskBusiness;
	}

	public String getAtmCardNo() {
		return atmCardNo;
	}

	public void setAtmCardNo(String atmCardNo) {
		this.atmCardNo = atmCardNo;
	}

	public CodeValue getGender() {
		return gender;
	}

	public void setGender(CodeValue gender) {
		this.gender = gender;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPurposeOfTransaction() {
		return purposeOfTransaction;
	}

	public void setPurposeOfTransaction(String purposeOfTransaction) {
		this.purposeOfTransaction = purposeOfTransaction;
	}

	public CodeValue getGlCodeId() {
		return glCodeId;
	}

	public void setGlCodeId(CodeValue glCodeId) {
		this.glCodeId = glCodeId;
	}

	public CodeValue isTaxExempted() {
		return taxExempted;
	}

	public void setTaxExempted(CodeValue taxExempted) {
		this.taxExempted = taxExempted;
	}

	public CodeValue isOptimaActivated() {
		return optimaActivated;
	}

	public void setOptimaActivated(CodeValue optimaActivated) {
		this.optimaActivated = optimaActivated;
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

	public String getMarketingSourceCode() {
		return marketingSourceCode;
	}

	public void setMarketingSourceCode(String marketingSourceCode) {
		this.marketingSourceCode = marketingSourceCode;
	}

	public String getIdentificationAttachment() {
		return identificationAttachment;
	}

	public void setIdentificationAttachment(String identificationAttachment) {
		this.identificationAttachment = identificationAttachment;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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

	public String getOfficerId() {
		return officerId;
	}

	public void setOfficerId(String officerId) {
		this.officerId = officerId;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getTerritoryCode() {
		return territoryCode;
	}

	public void setTerritoryCode(String territoryCode) {
		this.territoryCode = territoryCode;
	}

	public List<NotificationAttachmentsBean> getAttachmentsList() {
		if (attachmentsList == null) {
			attachmentsList = new ArrayList<NotificationAttachmentsBean>();
		}
		return attachmentsList;
	}

	public void addAttachmentBean(final NotificationAttachmentsBean bean) {
		attachmentsList.add(bean);
	}

	public void setAttachmentsList(List<NotificationAttachmentsBean> attachmentsList) {
		this.attachmentsList = attachmentsList;
	}

	public Date getBirthDateString() {
		return birthDateString;
	}

	public void setBirthDateString(Date birthDateString) {
		this.birthDateString = birthDateString;
	}

	public Date getExpireDateString() {
		return expireDateString;
	}

	public void setExpireDateString(Date expireDateString) {
		this.expireDateString = expireDateString;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaxCardNumber() {
		return taxCardNumber;
	}

	public void setTaxCardNumber(String taxCardNumber) {
		this.taxCardNumber = taxCardNumber;
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

	public String getSvaNumber() {
		return svaNumber;
	}

	public void setSvaNumber(String svaNumber) {
		this.svaNumber = svaNumber;
	}

	public CodeValue getTaxExempted() {
		return taxExempted;
	}

	public CodeValue getOptimaActivated() {
		return optimaActivated;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getParentSvaId() {
		return parentSvaId;
	}

	public void setParentSvaId(String parentSvaId) {
		this.parentSvaId = parentSvaId;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public CodeValue getBlackListReason() {
		return blackListReason;
	}

	public void setBlackListReason(CodeValue blackListReason) {
		this.blackListReason = blackListReason;
	}

	public CodeValue getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(CodeValue customerStatus) {
		this.customerStatus = customerStatus;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public int getCustomerTypeId() {
		return customerTypeId;
	}

	public void setCustomerTypeId(int customerTypeId) {
		this.customerTypeId = customerTypeId;
	}

	public Long getIdentityId() {
		return identityId;
	}

	public void setIdentityId(Long identityId) {
		this.identityId = identityId;
	}

	public int getIdentitiStatus() {
		return identitiStatus;
	}

	public void setIdentitiStatus(int identitiStatus) {
		this.identitiStatus = identitiStatus;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public Long getAtmCardId() {
		return atmCardId;
	}

	public void setAtmCardId(Long atmCardId) {
		this.atmCardId = atmCardId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getBlackListReson() {
		return blackListReson;
	}

	public void setBlackListReson(int blackListReson) {
		this.blackListReson = blackListReson;
	}

	public int getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(int productCategory) {
		this.productCategory = productCategory;
	}

}
