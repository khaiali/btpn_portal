package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is the bean used for Manage Interest bean
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxApproveBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String description;
	private Date dateRangeBegin;
	private Date dateRangeEnd;
	private String lastModifiedByName;
	private Long lastModifiedById;
	
	private String customerIdentifier;
	private Integer customerIdentifierType;
	private CodeValue customerTypeId;
	private Long paymentInstrumentId;
	private CodeValue paymentInstrumentTypeId;
	private CodeValue accrueGLCode;
	private CodeValue taxGLCode;
	private Date validFrom;
	private Long thresholdAmount;
	private Long fixedFee;
	private String percentageFee;
	private Long maximumFee;
	private Long minimumFee;
	
	private String note;
	private String callerId;	
	private String workFlowId;

	
	private List<ManageInterestTaxApproveBean> manageInterestTaxList;
	

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
	 * @return the customerIdentifier
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
	 * @return the customerIdentifier
	 */
	public String getCustomerIdentifier() {
		return customerIdentifier;
	}

	/**
	 * @param customerIdentifier the customerIdentifier to set
	 */
	public void setCustomerIdentifier(String customerIdentifier) {
		this.customerIdentifier = customerIdentifier;
	}
	
	/**
	 * @return the customerIdentifierType
	 */
	public Integer getCustomerIdentifierType() {
		return customerIdentifierType;
	}

	/**
	 * @param customerIdentifierType the customerIdentifierType to set
	 */
	public void setCustomerIdentifierType(Integer customerIdentifierType) {
		this.customerIdentifierType = customerIdentifierType;
	}
	
	
	/**
	 * @return the customerTypeId
	 */
	public CodeValue getCustomerTypeId() {
		return customerTypeId;
	}

	/**
	 * @param customerTypeId the customerTypeId to set
	 */
	public void setCustomerTypeId(CodeValue customerTypeId) {
		this.customerTypeId = customerTypeId;
	}
	
	
	/**
	 * @return the paymentInstrumentId
	 */
	public Long getPaymentInstrumentId() {
		return paymentInstrumentId;
	}

	/**
	 * @param paymentInstrumentId the paymentInstrumentId to set
	 */
	public void setPaymentInstrumentId(Long paymentInstrumentId) {
		this.paymentInstrumentId = paymentInstrumentId;
	}
	
	/**
	 * @return the paymentInstrumentTypeId
	 */
	public CodeValue getPaymentInstrumentTypeId() {
		return paymentInstrumentTypeId;
	}

	/**
	 * @param paymentInstrumentTypeId the paymentInstrumentTypeId to set
	 */
	public void setPaymentInstrumentTypeId(CodeValue paymentInstrumentTypeId) {
		this.paymentInstrumentTypeId = paymentInstrumentTypeId;
	}
	
	
	/**
	 * @return the accrueGLCode
	 */
	public CodeValue getAccrueGLCode() {
		return accrueGLCode;
	}

	/**
	 * @param  the accrueGLCode to set
	 */
	public void setAccrueGLCode(CodeValue accrueGLCode) {
		this.accrueGLCode = accrueGLCode;
	}

	/**
	 * @return the taxGLCode
	 */
	public CodeValue getTaxGLCode() {
		return taxGLCode;
	}

	/**
	 * @param taxGLCode the taxGLCode to set
	 */
	public void setTaxGLCode(CodeValue taxGLCode) {
		this.taxGLCode = taxGLCode;
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
	public String getPercentageFee() {
		return percentageFee;
	}

	/**
	 * @param percentageFee the percentageFee to set
	 */
	public void setPercentageFee(String percentageFee) {
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
	 * @return the manageInterestList
	 */
	public List<ManageInterestTaxApproveBean> getManageInterestTaxList() {
		if (this.manageInterestTaxList==null)
			this.manageInterestTaxList = new ArrayList<ManageInterestTaxApproveBean>();
		return manageInterestTaxList;
	}

	/**
	 * @param manageInterestList the manageInterestList to set
	 */
	public void setManageInterestTaxList( List<ManageInterestTaxApproveBean> manageInterestTaxList) {
		this.manageInterestTaxList = manageInterestTaxList;
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
	
	/**
	 * @return the lastModifiedById
	 */
	public Long getLastModifiedById() {
		return lastModifiedById;
	}

	/**
	 * @param lastModifiedById the lastModifiedById to set
	 */
	public void setLastModifiedById(Long lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}
	
	/**
	 * @return the lastModifiedByName
	 */
	public String getLastModifiedByName() {
		return lastModifiedByName;
	}

	/**
	 * @param lastModifiedByName the lastModifiedByName to set
	 */
	public void setLastModifiedByName(String lastModifiedByName) {
		this.lastModifiedByName = lastModifiedByName;
	}
	
}
