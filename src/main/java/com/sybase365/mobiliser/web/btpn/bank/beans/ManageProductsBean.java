package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the bean used for Manage Products
 * 
 * @author Vikram Gunda
 */
public class ManageProductsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer productId;

	private String productName;

	private CodeValue productType;

	private CodeValue productGLCode;

	private CodeValue feeGLCode;

	private String roleName;
	
	private String roleDescription;	
	
	private Long minBalance;

	private Long initialDeposit;

	private Long adminFee;

	private List<ManageProductsRangeBean> rangeList;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public CodeValue getProductType() {
		return productType;
	}

	public void setProductType(CodeValue productType) {
		this.productType = productType;
	}

	public CodeValue getProductGLCode() {
		return productGLCode;
	}

	public void setProductGLCode(CodeValue productGLCode) {
		this.productGLCode = productGLCode;
	}

	public CodeValue getFeeGLCode() {
		return feeGLCode;
	}

	public void setFeeGLCode(CodeValue feeGLCode) {
		this.feeGLCode = feeGLCode;
	}

	public Long getMinBalance() {
		return minBalance;
	}

	public void setMinBalance(Long minBalance) {
		this.minBalance = minBalance;
	}

	public Long getInitialDeposit() {
		return initialDeposit;
	}

	public void setInitialDeposit(Long initialDeposit) {
		this.initialDeposit = initialDeposit;
	}

	public Long getAdminFee() {
		return adminFee;
	}

	public void setAdminFee(Long adminFee) {
		this.adminFee = adminFee;
	}

	public List<ManageProductsRangeBean> getRangeList() {
		if (rangeList == null)
			rangeList = new ArrayList<ManageProductsRangeBean>();
		return rangeList;
	}
	
	public void addRangeBeanToList(final ManageProductsRangeBean bean) {
		if (rangeList == null)
			rangeList = getRangeList() ;
		rangeList.add(bean);
	}
	
	public void removeRangeBeanFromList(final int size) {
		rangeList.remove(size-1);
	}

	public void setRangeList(List<ManageProductsRangeBean> rangeList) {
		this.rangeList = rangeList;

	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}	
}
