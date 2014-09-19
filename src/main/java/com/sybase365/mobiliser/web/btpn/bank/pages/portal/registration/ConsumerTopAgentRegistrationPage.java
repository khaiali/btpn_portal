package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.RegistrationPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ConsumerTopAgentRegistrationPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ConsumerTopAgentRegistrationPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private CustomerRegistrationBean customer;

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public ConsumerTopAgentRegistrationPage(final CustomerRegistrationBean customer) {
		super();
		this.customer = customer;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new RegistrationPanel("consumerRegistrationPanel", this, this.customer));
	}

}
