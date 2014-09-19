package com.sybase365.mobiliser.web.btpn.bank.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the bean used for Manage Fee bean
 * 
 * @author Vikram Gunda
 */
public class ApproveBillPayOrAirtimeFeeBean extends ManageAirtimeTopupFeeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeValue newUseCase;

	private String newTransactionAmount;

	private String newAction;

	private List<ApproveFeeDetailsBean> feeDetails;

	public CodeValue getNewUseCase() {
		return newUseCase;
	}

	public void setNewUseCase(CodeValue newUseCase) {
		this.newUseCase = newUseCase;
	}

	public String getNewTransactionAmount() {
		return newTransactionAmount;
	}

	public void setNewTransactionAmount(String newTransactionAmount) {
		this.newTransactionAmount = newTransactionAmount;
	}

	public List<ApproveFeeDetailsBean> getFeeDetails() {
		if(!PortalUtils.exists(feeDetails)){
			feeDetails = new ArrayList<ApproveFeeDetailsBean>();
		}
		return feeDetails;
	}

	public void setFeeDetails(List<ApproveFeeDetailsBean> feeDetails) {
		this.feeDetails = feeDetails;
	}

	public String getNewAction() {
		return newAction;
	}

	public void setNewAction(String newAction) {
		this.newAction = newAction;
	}

}
