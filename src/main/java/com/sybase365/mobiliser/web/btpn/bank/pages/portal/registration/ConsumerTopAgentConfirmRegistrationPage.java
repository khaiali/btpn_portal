package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ConfirmRegistrationPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Confirm Registration Page for Consumers and Top Agents in bank portal.
 * 
 * @author Vikram Gunda
 */
public class ConsumerTopAgentConfirmRegistrationPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private CustomerRegistrationBean customer;

	/**
	 * Constructor for this page.
	 */
	public ConsumerTopAgentConfirmRegistrationPage() {
		initPageComponents();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public ConsumerTopAgentConfirmRegistrationPage(final CustomerRegistrationBean customer) {
		super();
		this.customer = customer;
		initPageComponents();
	}

	/**
	 * Initialize the components
	 */
	protected void initPageComponents() {
		add(new ConfirmRegistrationPanel("confirmRegistrationPanel", this.customer, this));

	}

}
