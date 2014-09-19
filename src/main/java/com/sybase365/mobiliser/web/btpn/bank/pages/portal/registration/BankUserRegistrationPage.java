package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankAdminAndUserRegistrationPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Registration Page for Bank Admin and Staff.
 * 
 * @author Vikram Gunda
 */
public class BankUserRegistrationPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor for this page.
	 */
	public BankUserRegistrationPage() {
		// do nothing
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		add(new BankAdminAndUserRegistrationPanel("userRegistrationPanel", this, false));
		
		super.initOwnPageComponents();
	}
}
