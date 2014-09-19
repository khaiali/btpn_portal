package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btpnwow.core.fee.facade.contract.FeeEntryType;
import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;

/**
 * This is the bean used for Manage Fee bean
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String description;
	private CodeValue useCase;
	private CodeValue customerType;
	private CodeValue debitPiType;
	private CodeValue creditPiType;
	private CodeValue debitOrgUnit;
	private CodeValue creditOrgUnit;
	private Date validFrom;
	private boolean payeeFee;
	private CodeValue currencyCode;
	private CodeValue glCode;
	private String note;
	private String callerId;
	
	private String workFlowId;
	private Date dateRangeBegin;
	private Date dateRangeEnd;
	
	//case edit
	private List<ManageBillPaymentFeeDetailsBean> manageFeeDetailsList;
	
	private List<FeeEntryWrkType> manageDetailsWrkList;
	private List<FeeEntryType> manageDetailsList;
	private List<ManageCustomUseCaseFeeBean> manageCusUseCaseFeeList;
	

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the useCase
	 */
	public CodeValue getUseCase() {
		return useCase;
	}

	/**
	 * @param useCase the useCase to set
	 */
	public void setUseCase(CodeValue useCase) {
		this.useCase = useCase;
	}

	/**
	 * @return the customerType
	 */
	public CodeValue getCustomerType() {
		return customerType;
	}

	/**
	 * @param customerType the customerType to set
	 */
	public void setCustomerType(CodeValue customerType) {
		this.customerType = customerType;
	}

	/**
	 * @return the validFrom
	 */
	public Date getValidFrom() {
		return validFrom;
	}

	/**
	 * @param validFrom the validFrom to set
	 */
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	/**
	 * @return the payeeFee
	 */
	public boolean isPayeeFee() {
		return payeeFee;
	}

	/**
	 * @param payeeFee the payeeFee to set
	 */
	public void setPayeeFee(boolean payeeFee) {
		this.payeeFee = payeeFee;
	}

	/**
	 * @return the currencyCode
	 */
	public CodeValue getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(CodeValue currencyCode) {
		this.currencyCode = currencyCode;
	}

	
	/**
	 * @return the glCode
	 */
	public CodeValue getGlCode() {
		return glCode;
	}

	/**
	 * @param glCode the glCode to set
	 */
	public void setGlCode(CodeValue glCode) {
		this.glCode = glCode;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return the callerId
	 */
	public String getCallerId() {
		return callerId;
	}

	/**
	 * @param callerId the callerId to set
	 */
	public void setCallerId(String callerId) {
		this.callerId = callerId;
	}

	//case edit
	public List<ManageBillPaymentFeeDetailsBean> getManageFeeDetailsList() {
		if (null == this.manageFeeDetailsList)
			this.manageFeeDetailsList = new ArrayList<ManageBillPaymentFeeDetailsBean>();
		return manageFeeDetailsList;
	}
	
	
	
	public List<FeeEntryWrkType> getManageDetailsWrkList() {
		if (null == this.manageDetailsWrkList)
			this.manageDetailsWrkList = new ArrayList<FeeEntryWrkType>();
		return manageDetailsWrkList;
	}
	
	/**
	 * @return the manageCusUseCaseFeeList
	 */
	public List<ManageCustomUseCaseFeeBean> getManageCusUseCaseFeeList() {
		if (this.manageCusUseCaseFeeList==null)
			this.manageCusUseCaseFeeList = new ArrayList<ManageCustomUseCaseFeeBean>();
		return manageCusUseCaseFeeList;
	}

	/**
	 * @param manageCusUseCaseFeeList the manageCusUseCaseFeeList to set
	 */
	public void setManageCusUseCaseFeeList(
			List<ManageCustomUseCaseFeeBean> manageCusUseCaseFeeList) {
		this.manageCusUseCaseFeeList = manageCusUseCaseFeeList;
	}

	
	public List<FeeEntryType> getManageDetailsList() {
		return manageDetailsList;
	}

	public void setManageDetailsList(List<FeeEntryType> manageDetailsList) {
		this.manageDetailsList = manageDetailsList;
	}
	
	/**
	 * @return the debitPiType
	 */
	public CodeValue getDebitPiType() {
		return debitPiType;
	}

	/**
	 * @param debitPiType the debitPiType to set
	 */
	public void setDebitPiType(CodeValue debitPiType) {
		this.debitPiType = debitPiType;
	}

	/**
	 * @return the debitOrgUnit
	 */
	public CodeValue getDebitOrgUnit() {
		return debitOrgUnit;
	}

	/**
	 * @param debitOrgUnit the debitOrgUnit to set
	 */
	public void setDebitOrgUnit(CodeValue debitOrgUnit) {
		this.debitOrgUnit = debitOrgUnit;
	}

	/**
	 * @return the creditPiType
	 */
	public CodeValue getCreditPiType() {
		return creditPiType;
	}

	/**
	 * @param creditPiType the creditPiType to set
	 */
	public void setCreditPiType(CodeValue creditPiType) {
		this.creditPiType = creditPiType;
	}

	/**
	 * @return the creditOrgUnit
	 */
	public CodeValue getCreditOrgUnit() {
		return creditOrgUnit;
	}

	/**
	 * @param creditOrgUnit the creditOrgUnit to set
	 */
	public void setCreditOrgUnit(CodeValue creditOrgUnit) {
		this.creditOrgUnit = creditOrgUnit;
	}

	/**
	 * @return the workFlowId
	 */
	public String getWorkFlowId() {
		return workFlowId;
	}

	/**
	 * @param workFlowId the workFlowId to set
	 */
	public void setWorkFlowId(String workFlowId) {
		this.workFlowId = workFlowId;
	}

	/**
	 * @return the dateRangeBegin
	 */
	public Date getDateRangeBegin() {
		return dateRangeBegin;
	}

	/**
	 * @param dateRangeBegin the dateRangeBegin to set
	 */
	public void setDateRangeBegin(Date dateRangeBegin) {
		this.dateRangeBegin = dateRangeBegin;
	}

	/**
	 * @return the dateRangeEnd
	 */
	public Date getDateRangeEnd() {
		return dateRangeEnd;
	}

	/**
	 * @param dateRangeEnd the dateRangeEnd to set
	 */
	public void setDateRangeEnd(Date dateRangeEnd) {
		this.dateRangeEnd = dateRangeEnd;
	}

}
