package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;

/**
 * This is the bean used for Manage General Ledger Bean.
 * 
 * @author Vikram Gunda
 */
public class ManageGeneralLedgerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String glCode;

	private String glDescription;

	private boolean isRoot;

	private boolean isLeaf;

	private CodeValue type;

	private CodeValue parentGlCode;

	private String status;

	private String createdBy;

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public String getGlDescription() {
		return glDescription;
	}

	public void setGlDescription(String glDescription) {
		this.glDescription = glDescription;
	}

	public boolean getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public CodeValue getType() {
		return type;
	}

	public void setType(CodeValue type) {
		this.type = type;
	}

	public boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public CodeValue getParentGlCode() {
		return parentGlCode;
	}

	public void setParentGlCode(CodeValue parentGlCode) {
		this.parentGlCode = parentGlCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

}
