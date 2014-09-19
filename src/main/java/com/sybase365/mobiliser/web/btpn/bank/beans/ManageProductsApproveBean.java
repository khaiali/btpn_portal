package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the bean used for Manage Products
 * 
 * @author Vikram Gunda
 */
public class ManageProductsApproveBean extends ManageProductsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String maker;

	private String taskId;

	private String status;

	private String action;
	
	private Integer newProductId;

	private String newProductName;

	private CodeValue newProductType;

	private CodeValue newProductGLCode;

	private CodeValue newFeeGLCode;

	private String newRoleName;

	private String newRoleDescription;

	private Long newMinBalance;

	private Long newInitialDeposit;

	private Long newAdminFee;

	private List<ManageProductsApproveRangeBean> approveRangeList;

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getNewProductId() {
		return newProductId;
	}

	public void setNewProductId(Integer newProductId) {
		this.newProductId = newProductId;
	}

	public String getNewProductName() {
		return newProductName;
	}

	public void setNewProductName(String newProductName) {
		this.newProductName = newProductName;
	}

	public CodeValue getNewProductType() {
		return newProductType;
	}

	public void setNewProductType(CodeValue newProductType) {
		this.newProductType = newProductType;
	}

	public CodeValue getNewProductGLCode() {
		return newProductGLCode;
	}

	public void setNewProductGLCode(CodeValue newProductGLCode) {
		this.newProductGLCode = newProductGLCode;
	}

	public CodeValue getNewFeeGLCode() {
		return newFeeGLCode;
	}

	public void setNewFeeGLCode(CodeValue newFeeGLCode) {
		this.newFeeGLCode = newFeeGLCode;
	}

	public Long getNewMinBalance() {
		return newMinBalance;
	}

	public void setNewMinBalance(Long newMinBalance) {
		this.newMinBalance = newMinBalance;
	}

	public Long getNewInitialDeposit() {
		return newInitialDeposit;
	}

	public void setNewInitialDeposit(Long newInitialDeposit) {
		this.newInitialDeposit = newInitialDeposit;
	}

	public Long getNewAdminFee() {
		return newAdminFee;
	}

	public void setNewAdminFee(Long newAdminFee) {
		this.newAdminFee = newAdminFee;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public List<ManageProductsApproveRangeBean> getApproveRangeList() {
		if (null == approveRangeList) {
			approveRangeList = new ArrayList<ManageProductsApproveRangeBean>();
		}
		return approveRangeList;
	}

	public void setApproveRangeList(List<ManageProductsApproveRangeBean> approveRangeList) {
		this.approveRangeList = approveRangeList;
	}

	public String getNewRoleName() {
		return newRoleName;
	}

	public void setNewRoleName(String newRoleName) {
		this.newRoleName = newRoleName;
	}

	public String getNewRoleDescription() {
		return newRoleDescription;
	}

	public void setNewRoleDescription(String newRoleDescription) {
		this.newRoleDescription = newRoleDescription;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}
