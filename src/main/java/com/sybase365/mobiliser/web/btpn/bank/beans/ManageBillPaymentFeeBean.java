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
 * @author Vikram Gunda
 */
public class ManageBillPaymentFeeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String action;
	private String description;

	private Long id; 
	private CodeValue useCase;
	private String billerId;
	private String productLabel;
	private String productId;
	private CodeValue customerType;
	private CodeValue piType;
	private CodeValue orgUnit;
	private boolean applyToPayee;
	private Date validFrom;
	private CodeValue currency;
	private CodeValue glCode;
	private String note;
	
	private Date dateRangeBegin;
	private Date dateRangeEnd;
	private String workflowId;
	private String lastModifiedByName;
	private String lastModifiedByDate;
	
	//case edit
	private List<ManageBillPaymentFeeDetailsBean> manageFeeDetailsList;
	
	private List<FeeEntryWrkType> manageDetailsWrkList;
	
	private List<FeeEntryType> manageDetailsList;
	private List<ManageBillPaymentFeeBean> manageBillerList;

	
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public CodeValue getUseCase() {
		return useCase;
	}

	public void setUseCase(CodeValue useCase) {
		this.useCase = useCase;
	}
	
	public String getBillerId() {
		return billerId;
	}

	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}
	
	public String getProductLabel() {
		return productLabel;
	}

	public void setProductLabel(String productLabel) {
		this.productLabel = productLabel;
	}
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public CodeValue getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CodeValue customerType) {
		this.customerType = customerType;
	}

	public CodeValue getPiType() {
		return piType;
	}

	public void setPiType(CodeValue piType) {
		this.piType = piType;
	}

	public CodeValue getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(CodeValue orgUnit) {
		this.orgUnit = orgUnit;
	}

	public boolean getApplyToPayee() {
		return applyToPayee;
	}

	public void setApplyToPayee(boolean applyToPayee) {
		this.applyToPayee = applyToPayee;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	
	
	public CodeValue getCurrency() {
		return currency;
	}

	public void setCurrency(CodeValue currency) {
		this.currency = currency;
	}

	
	public CodeValue getGlCode() {
		return glCode;
	}

	public void setGlCode(CodeValue glCode) {
		this.glCode = glCode;
	}
	
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
	
	public Date getDateRangeBegin()
	{
	    return this.dateRangeBegin;
	}
	
	public void setDateRangeBegin(Date value)
	{
	    this.dateRangeBegin = value;
	}
	
	public Date getDateRangeEnd()
	{
	    return this.dateRangeEnd;
	}
	
	public void setDateRangeEnd(Date value)
	{
	    this.dateRangeEnd = value;
	}
	  
	 public String getWorkflowId()
	 {
	    return this.workflowId;
	 }
	
	 public void setWorkflowId(String workflowId)
	 {
	    this.workflowId = workflowId;
	 }
	
	public List<FeeEntryWrkType> getManageDetailsWrkList() {
		if (null == this.manageDetailsWrkList)
			this.manageDetailsWrkList = new ArrayList<FeeEntryWrkType>();
		return manageDetailsWrkList;
	}
	
	public String getLastModifiedByName() {
		return lastModifiedByName;
	}
	
	public void setLastModifiedByName(String lastModifiedByName) {
		this.lastModifiedByName = lastModifiedByName;
	}
	
	public String getLastModifiedByDate() {
		return lastModifiedByDate;
	}
	
	public void setLastModifiedByDate(String lastModifiedByDate) {
		this.lastModifiedByDate = lastModifiedByDate;
	}
	
	
	//case edit
	public List<ManageBillPaymentFeeDetailsBean> getManageFeeDetailsList() {
		if (null == this.manageFeeDetailsList)
			this.manageFeeDetailsList = new ArrayList<ManageBillPaymentFeeDetailsBean>();
		return manageFeeDetailsList;
	}
	
	public List<FeeEntryType> getManageDetailsList() {
		return manageDetailsList;
	}

	public void setManageDetailsList(List<FeeEntryType> manageDetailsList) {
		this.manageDetailsList = manageDetailsList;
	}
	
	public List<ManageBillPaymentFeeBean> getManageBillerList() {
		return this.manageBillerList;
	}

	public void setManageBillerList(List<ManageBillPaymentFeeBean> manageBillerList) {
		this.manageBillerList = manageBillerList;
	}
}
