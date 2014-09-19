package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankAdminAndUserRegistrationPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Registration Page for Bank Admin and Staff.
 * 
 * @author Andi Samallangi W
 */
public class BankAdminRegistrationPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor for this page.
	 */
	public BankAdminRegistrationPage() {
		// do nothing
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		add(new BankAdminAndUserRegistrationPanel("adminRegPanel", this, true));
		
		super.initOwnPageComponents();
	}
}
