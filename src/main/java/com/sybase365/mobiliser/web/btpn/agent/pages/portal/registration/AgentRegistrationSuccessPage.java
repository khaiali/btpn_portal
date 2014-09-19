package com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.RegistrationSuccessPanel;

/**
 * This is the AgentRegistrationSuccessPage page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentRegistrationSuccessPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private CustomerRegistrationBean customer;

	public AgentRegistrationSuccessPage() {

	}

	public AgentRegistrationSuccessPage(CustomerRegistrationBean customer) {
		super();
		this.customer = customer;
		initPageComponents();

	}

	protected void initPageComponents() {
		add(new RegistrationSuccessPanel("consumerRegistrationSuccessPanel", customer, this));
	}

}
