package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import org.apache.wicket.spring.injection.annot.SpringBean;

import com.btpnwow.core.customer.facade.api.UserFacade;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankAdminAndUserRegistrationConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the BankAdminRegistrationConfirmPage Page for Bank Admin and Staff.
 * 
 * @author Andi Samallangi W
 */
public class BankAdminRegistrationConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	@SpringBean(name = "userFacade")
	private UserFacade userFacade;
	
	private BankStaffBean bankStaffBean;
	
	private boolean isBankAdmin;
	
	public BankAdminRegistrationConfirmPage(BankStaffBean bankStaffBean, boolean isBankAdmin) {
		this.bankStaffBean = bankStaffBean;
		this.isBankAdmin = isBankAdmin;
		
		initPageComponents();
	}

	public UserFacade getUserFacade() {
		return userFacade;
	}

	public BankStaffBean getBankStaffBean() {
		return bankStaffBean;
	}

	public boolean isBankAdmin() {
		return isBankAdmin;
	}

	protected void initPageComponents() {
		add(new BankAdminAndUserRegistrationConfirmPanel("adminRegConfirmPanel", this));
	}
}
