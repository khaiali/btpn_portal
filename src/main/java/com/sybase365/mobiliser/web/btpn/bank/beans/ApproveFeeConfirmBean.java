package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the bean used for Approve Fee bean
 * 
 * @author Vikram Gunda
 */
public class ApproveFeeConfirmBean extends ManageFeeBean {

	private static final long serialVersionUID = 1L;

	private String newAction;

	private CodeValue newProductName;

	private CodeValue newUseCaseName;

	private String newFeeType;

	private boolean newApplyToPayee;

	private Long newTransactionAmount;
	
	private List<Long> newUseCaseFeeId;

	private List<ApproveFeeDetailsBean> feeDetailsBean;

	public String getNewAction() {
		return newAction;
	}

	public void setNewAction(String newAction) {
		this.newAction = newAction;
	}

	public CodeValue getNewProductName() {
		return newProductName;
	}

	public void setNewProductName(CodeValue newProductName) {
		this.newProductName = newProductName;
	}

	public CodeValue getNewUseCaseName() {
		return newUseCaseName;
	}

	public void setNewUseCaseName(CodeValue newUseCaseName) {
		this.newUseCaseName = newUseCaseName;
	}

	public String getNewFeeType() {
		return newFeeType;
	}

	public void setNewFeeType(String newFeeType) {
		this.newFeeType = newFeeType;
	}

	public boolean getNewApplyToPayee() {
		return newApplyToPayee;
	}

	public void setNewApplyToPayee(boolean newApplyToPayee) {
		this.newApplyToPayee = newApplyToPayee;
	}

	public Long getNewTransactionAmount() {
		return newTransactionAmount;
	}

	public void setNewTransactionAmount(Long newTransactionAmount) {
		this.newTransactionAmount = newTransactionAmount;
	}

	public List<ApproveFeeDetailsBean> getFeeDetailsBean() {
		if (null == this.feeDetailsBean)
			this.feeDetailsBean = new ArrayList<ApproveFeeDetailsBean>();
		return feeDetailsBean;
	}

	public void setFeeDetailsBean(List<ApproveFeeDetailsBean> feeDetailsBean) {
		this.feeDetailsBean = feeDetailsBean;
	}

	public List<Long> getNewUseCaseFeeId() {
		return newUseCaseFeeId;
	}

	public void setNewUseCaseFeeId(List<Long> newUseCaseFeeId) {
		this.newUseCaseFeeId = newUseCaseFeeId;
	}	
}
