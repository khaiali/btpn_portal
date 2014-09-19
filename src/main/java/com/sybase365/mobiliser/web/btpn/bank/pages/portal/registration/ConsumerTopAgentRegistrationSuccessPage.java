package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.RegistrationSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the success Registration Panel for Consumers, Agents in bank portal and Child Agents, Sub agents in agent
 * portal.
 * 
 * @author Vikram Gunda
 */
public class ConsumerTopAgentRegistrationSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	CustomerRegistrationBean customer;

	protected BtpnBaseBankPortalSelfCarePage mobBasePage;

	public ConsumerTopAgentRegistrationSuccessPage() {

	}

	public ConsumerTopAgentRegistrationSuccessPage(CustomerRegistrationBean customer) {
		super();
		this.customer = customer;
		initPageComponents();

	}

	protected void initPageComponents() {
		add(new RegistrationSuccessPanel("consumerRegistrationSuccessPanel", customer, this));
	}

}
