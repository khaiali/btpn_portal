package com.sybase365.mobiliser.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitClass;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.treetable.utils.TreeNodeBean;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CustomerBean implements Serializable, TreeNodeBean,
	Comparable<CustomerBean> {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long parentId;

    private String msisdn;
    private String networkProvider;
    private Long originalFeeSetId;
    private Long feeSetId;
    private Boolean isIndividualFeeSet;
    private Boolean isIndividualLimitSet;
    private Float feePercentage;
    private Long limitId;
    private Long originalLimitSetId;
    private Integer customerTypeId;
    private boolean active = true;
    protected XMLGregorianCalendar created;
    private String parentName;
    private Integer blackListReason;
    private String userName;
    private String password;
    private String displayName;
    private String kvCountry;
    private List<Attachment> attachmentList;
    private String issuer;
    private Date expirationDate;
    private String identityValue;
    private String identityId;
    private Integer kvIdentityType;
    private Boolean verified;
    private AddressBean address;
    private LimitClass limitClass;
    private int kvKycLevel;
    private int kvInfoMode;
    private String otp;
    String pin;
    private int hierarchyLevel;
    private WalletEntry walletEntry;
    private String timeZone;
    private String language;
    private int cancelationReason;
    private String securityQuestion;
    private String SecQuesAns;
    private Date birthDateString;
    private XMLGregorianCalendar birthDateAsXml;
    private boolean isPendingApproval;

    private List<CustomerBean> children;
    private Integer riskCategoryId;
    private String taskId;
    private String orgUnitId;

    public String getOrgUnitId() {
	return orgUnitId;
    }

    public void setOrgUnitId(String orgUnitId) {
	this.orgUnitId = orgUnitId;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getIssuer() {
	return issuer;
    }

    public void setIssuer(String issuer) {
	this.issuer = issuer;
    }

    public Date getExpirationDate() {
	return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
	this.expirationDate = expirationDate;
    }

    public Boolean isVerified() {
	return verified;
    }

    public void setVerified(Boolean verified) {
	this.verified = verified;
    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getIdentityValue() {
	return identityValue;
    }

    public void setIdentityValue(String identityValue) {
	this.identityValue = identityValue;
    }

    public Integer getKvIdentityType() {
	return kvIdentityType;
    }

    public void setKvIdentityType(Integer kvIdentityType) {
	this.kvIdentityType = kvIdentityType;
    }

    public String getKvCountry() {
	return kvCountry;
    }

    public void setKvCountry(String kvCountry) {
	this.kvCountry = kvCountry;
    }

    public List<Attachment> getAttachmentList() {
	return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
	this.attachmentList = attachmentList;
    }

    public AddressBean getAddress() {
	return address;
    }

    public void setAddress(AddressBean address) {
	this.address = address;
    }

    public void setParentId(Long parentId) {
	this.parentId = parentId;
    }

    public Long getParentId() {
	return parentId;
    }

    public Float getFeePercentage() {
	return feePercentage;
    }

    public void setFeePercentage(Float feePercentage) {
	this.feePercentage = feePercentage;
    }

    public Long getOriginalFeeSetId() {
	return originalFeeSetId;
    }

    public void setOriginalFeeSetId(Long originalFeeSetId) {
	this.originalFeeSetId = originalFeeSetId;
    }

    public Long getFeeSetId() {
	return feeSetId;
    }

    public void setFeeSetId(Long feeSetId) {
	this.feeSetId = feeSetId;
    }

    public Boolean getIsIndividualFeeSet() {
	return isIndividualFeeSet;
    }

    public void setIsIndividualFeeSet(Boolean isIndividualFeeSet) {
	this.isIndividualFeeSet = isIndividualFeeSet;
    }

    public Long getLimitId() {
	return limitId;
    }

    public void setLimitId(Long limitId) {
	this.limitId = limitId;
    }

    public boolean isActive() {
	return active;
    }

    public void setActive(boolean active) {
	this.active = active;
    }

    public XMLGregorianCalendar getCreated() {
	return created;
    }

    public void setCreated(XMLGregorianCalendar created) {
	this.created = created;
    }

    public String getParentName() {
	return parentName;
    }

    public void setParentName(String parentName) {
	this.parentName = parentName;
    }

    public Integer getBlackListReason() {
	return blackListReason;
    }

    public void setBlackListReason(Integer blackListReason) {
	this.blackListReason = blackListReason;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    public void setKvKycLevel(int kvKycLevel) {
	this.kvKycLevel = kvKycLevel;
    }

    public int getKvKycLevel() {
	return kvKycLevel;
    }

    public String getIdentityId() {
	return identityId;
    }

    public void setIdentityId(String identityId) {
	this.identityId = identityId;
    }

    @Override
    public List<CustomerBean> getChildren() {
	if (this.children == null)
	    this.children = new ArrayList<CustomerBean>();
	return this.children;
    }

    public void setChildren(List<CustomerBean> children) {
	this.children = children;
    }

    public void setLimitClass(LimitClass limitClass) {
	this.limitClass = limitClass;
    }

    public LimitClass getLimitClass() {
	return limitClass;
    }

    public void setCustomerTypeId(Integer customerTypeId) {
	this.customerTypeId = customerTypeId;
    }

    public Integer getCustomerTypeId() {
	return customerTypeId;
    }

    public void setHierarchyLevel(int hierarchyLevel) {
	this.hierarchyLevel = hierarchyLevel;
    }

    public void setKvInfoMode(int kvInfoMode) {
	this.kvInfoMode = kvInfoMode;
    }

    public Integer getRiskCategoryId() {
	return riskCategoryId;
    }

    public void setRiskCategoryId(Integer riskCategoryId) {
	this.riskCategoryId = riskCategoryId;
    }

    public Boolean getVerified() {
	return verified;
    }

    public int getKvInfoMode() {
	return kvInfoMode;
    }

    public int getHierarchyLevel() {
	return hierarchyLevel;
    }

    public String getOtp() {
	return otp;
    }

    public void setOtp(String otp) {
	this.otp = otp;
    }

    public WalletEntry getWalletEntry() {
	return walletEntry;
    }

    public void setWalletEntry(WalletEntry walletEntry) {
	this.walletEntry = walletEntry;
    }

    @Override
    public int compareTo(CustomerBean o) {
	if (this.parentId <= o.parentId)
	    return -1;
	else
	    return 1;

    }

    public String getPin() {
	return pin;
    }

    public void setPin(String pin) {
	this.pin = pin;
    }

    public void setTimeZone(String timeZone) {
	this.timeZone = timeZone;
    }

    public String getTimeZone() {
	return timeZone;
    }

    public String getLanguage() {
	return language;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

    public int getCancelationReason() {
	return cancelationReason;
    }

    public void setCancelationReason(int cancelationReason) {
	this.cancelationReason = cancelationReason;
    }

    public String getSecurityQuestion() {
	return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
	this.securityQuestion = securityQuestion;
    }

    public String getSecQuesAns() {
	return SecQuesAns;
    }

    public void setSecQuesAns(String secQuesAns) {
	SecQuesAns = secQuesAns;
    }

    public Date getBirthDateString() {
	return birthDateString;
    }

    public void setBirthDateString(Date birthDateString) {
	this.birthDateString = birthDateString;
    }

    public XMLGregorianCalendar getBirthDateAsXml() {
	if (PortalUtils.exists(birthDateString)) {
	    final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
	    cal.setTime(birthDateString);

	    try {
		return DatatypeFactory
			.newInstance()
			.newXMLGregorianCalendarDate(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DATE),
				birthDateAsXml == null ? DatatypeConstants.FIELD_UNDEFINED
					: birthDateAsXml.getTimezone());
	    } catch (DatatypeConfigurationException e) {
		return null;
	    }
	} else {
	    return null;
	}
    }

    public void setBirthDateAsXml(XMLGregorianCalendar birthDateAsXml) {
	this.birthDateAsXml = birthDateAsXml;
    }

    public Boolean getIsIndividualLimitSet() {
	return isIndividualLimitSet;
    }

    public void setIsIndividualLimitSet(Boolean isIndividualLimitSet) {
	this.isIndividualLimitSet = isIndividualLimitSet;
    }

    public void setOriginalLimitSetId(Long originalLimitSetId) {
	this.originalLimitSetId = originalLimitSetId;
    }

    public Long getOriginalLimitSetId() {
	return originalLimitSetId;
    }

    public void setNetworkProvider(String networkProvider) {
	this.networkProvider = networkProvider;
    }

    public String getNetworkProvider() {
	return networkProvider;
    }

    public String getTaskId() {
	return taskId;
    }

    public void setTaskId(String taskId) {
	this.taskId = taskId;
    }

    public boolean isPendingApproval() {
	return isPendingApproval;
    }

    public void setPendingApproval(boolean isPendingApproval) {
	this.isPendingApproval = isPendingApproval;
    }

}
