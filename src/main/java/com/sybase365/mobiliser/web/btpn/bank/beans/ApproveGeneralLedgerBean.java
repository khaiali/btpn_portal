package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Approve General Ledger Bean.
 * 
 * @author Vikram Gunda
 */
public class ApproveGeneralLedgerBean extends ManageGeneralLedgerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String newGlCode;

	private String newGlDescription;

	private boolean newIsRoot;

	private boolean newIsLeaf;

	private CodeValue newParentGlCode;

	private CodeValue newType;

	private String taskId;

	public String getNewGlCode() {
		return newGlCode;
	}

	public void setNewGlCode(String newGlCode) {
		this.newGlCode = newGlCode;
	}

	public String getNewGlDescription() {
		return newGlDescription;
	}

	public void setNewGlDescription(String newGlDescription) {
		this.newGlDescription = newGlDescription;
	}

	public boolean getIsNewIsRoot() {
		return newIsRoot;
	}

	public void setNewIsRoot(boolean newIsRoot) {
		this.newIsRoot = newIsRoot;
	}

	public boolean getIsNewIsLeaf() {
		return newIsLeaf;
	}

	public void setNewIsLeaf(boolean newIsLeaf) {
		this.newIsLeaf = newIsLeaf;
	}

	public CodeValue getNewType() {
		return newType;
	}

	public void setNewType(CodeValue newType) {
		this.newType = newType;
	}

	public CodeValue getNewParentGlCode() {
		return newParentGlCode;
	}

	public void setNewParentGlCode(CodeValue newParentGlCode) {
		this.newParentGlCode = newParentGlCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
