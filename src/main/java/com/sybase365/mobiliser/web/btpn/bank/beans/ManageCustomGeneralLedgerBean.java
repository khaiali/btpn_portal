package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the bean used for Manage General Ledger Bean.
 * 
 * @author Andi Samallangi
 */
public class ManageCustomGeneralLedgerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String glCode;
	private String glDescription;
	private CodeValue selectedType;
	private CodeValue selectedParentGlCode;
	private String status;
	private String createdBy;
	private String note;
	
	private String workFlowId;
	
	private List<ManageCustomGeneralLedgerBean> generalLedgerList;
	
	
	/**
	 * @return the glCode
	 */
	public String getGlCode() {
		return glCode;
	}
	
	/**
	 * @param glCode the glCode to set
	 */
	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}
	
	/**
	 * @return the glDescription
	 */
	public String getGlDescription() {
		return glDescription;
	}
	
	/**
	 * @param glDescription the glDescription to set
	 */
	public void setGlDescription(String glDescription) {
		this.glDescription = glDescription;
	}
	
	/**
	 * @return the selectedType
	 */
	public CodeValue getSelectedType() {
		return selectedType;
	}
	
	/**
	 * @param selectedType the selectedType to set
	 */
	public void setSelectedType(CodeValue selectedType) {
		this.selectedType = selectedType;
	}
	
	/**
	 * @return the selectedParentGlCode
	 */
	public CodeValue getSelectedParentGlCode() {
		return selectedParentGlCode;
	}
	
	/**
	 * @param selectedParentGlCode the selectedParentGlCode to set
	 */
	public void setSelectedParentGlCode(CodeValue selectedParentGlCode) {
		this.selectedParentGlCode = selectedParentGlCode;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	 * @return the generalLedgerList
	 */
	public List<ManageCustomGeneralLedgerBean> getGeneralLedgerList() {
		if (this.generalLedgerList == null)
			this.generalLedgerList = new ArrayList<ManageCustomGeneralLedgerBean>();
		return generalLedgerList;
	}

	/**
	 * @param generalLedgerList the generalLedgerList to set
	 */
	public void setGeneralLedgerList(
			List<ManageCustomGeneralLedgerBean> generalLedgerList) {
		this.generalLedgerList = generalLedgerList;
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
	
}
