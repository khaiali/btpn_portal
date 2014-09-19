package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is the bean used for Manage Fee bean
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeeApproveBean implements Serializable {

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
	private Long thresholdAmount;
	private Long fixedFee;
	private Long percentageFee;
	private Long maximumFee;
	private Long minimumFee;
	private CodeValue glCode;
	private String note;
	private String callerId;
	
	private List<ManageCustomUseCaseFeeApproveBean> manageCusUseCaseFeeList;
	

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
	 * @return the thresholdAmount
	 */
	public Long getThresholdAmount() {
		return thresholdAmount;
	}

	/**
	 * @param thresholdAmount the thresholdAmount to set
	 */
	public void setThresholdAmount(Long thresholdAmount) {
		this.thresholdAmount = thresholdAmount;
	}

	/**
	 * @return the fixedFee
	 */
	public Long getFixedFee() {
		return fixedFee;
	}

	/**
	 * @param fixedFee the fixedFee to set
	 */
	public void setFixedFee(Long fixedFee) {
		this.fixedFee = fixedFee;
	}

	/**
	 * @return the percentageFee
	 */
	public Long getPercentageFee() {
		return percentageFee;
	}

	/**
	 * @param percentageFee the percentageFee to set
	 */
	public void setPercentageFee(Long percentageFee) {
		this.percentageFee = percentageFee;
	}

	/**
	 * @return the maximumFee
	 */
	public Long getMaximumFee() {
		return maximumFee;
	}

	/**
	 * @param maximumFee the maximumFee to set
	 */
	public void setMaximumFee(Long maximumFee) {
		this.maximumFee = maximumFee;
	}

	/**
	 * @return the minimumFee
	 */
	public Long getMinimumFee() {
		return minimumFee;
	}

	/**
	 * @param minimumFee the minimumFee to set
	 */
	public void setMinimumFee(Long minimumFee) {
		this.minimumFee = minimumFee;
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

	/**
	 * @return the manageCusUseCaseFeeList
	 */
	public List<ManageCustomUseCaseFeeApproveBean> getManageCusUseCaseFeeList() {
		if (this.manageCusUseCaseFeeList==null)
			this.manageCusUseCaseFeeList = new ArrayList<ManageCustomUseCaseFeeApproveBean>();
		return manageCusUseCaseFeeList;
	}

	/**
	 * @param manageCusUseCaseFeeList the manageCusUseCaseFeeList to set
	 */
	public void setManageCusUseCaseFeeList(
			List<ManageCustomUseCaseFeeApproveBean> manageCusUseCaseFeeList) {
		this.manageCusUseCaseFeeList = manageCusUseCaseFeeList;
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

}
