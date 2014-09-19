package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankAdminAndUserRegistrationSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Registration Page for Bank Admin and Staff.
 * 
 * @author Andi Samallangi W
 */
public class BankAdminRegistrationSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	private BankStaffBean bankStaffBean;
	
	private boolean bankAdmin;
	
	private boolean approvalNeeded;
	
	public BankAdminRegistrationSuccessPage(BankStaffBean bankStaffBean, boolean bankAdmin, boolean approvalNeeded) {
		this.bankStaffBean = bankStaffBean;
		this.bankAdmin = bankAdmin;
		this.approvalNeeded = approvalNeeded;
		
		initPageComponents();
	}

	public BankStaffBean getBankStaffBean() {
		return bankStaffBean;
	}

	public boolean isBankAdmin() {
		return bankAdmin;
	}

	public boolean isApprovalNeeded() {
		return approvalNeeded;
	}

	protected void initPageComponents() {
		add(new BankAdminAndUserRegistrationSuccessPanel("adminRegSuccessPanel", this));
	}
}
