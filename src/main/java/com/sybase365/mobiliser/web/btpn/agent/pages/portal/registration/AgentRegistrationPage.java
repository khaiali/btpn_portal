package com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.RegistrationPanel;

/**
 * This is the ConsumerRegistrationPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class AgentRegistrationPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	CustomerRegistrationBean customer;

	public AgentRegistrationPage(CustomerRegistrationBean customer) {
		super();
		this.customer = customer;
		initPageComponents();
	}

	protected void initPageComponents() {
		final Long customerId = this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
		customer.setParentId(customerId);
		add(new RegistrationPanel("consumerRegistrationPanel", this, this.customer));
	}
}
