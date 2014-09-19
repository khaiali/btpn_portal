package com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration;

import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ConfirmRegistrationPanel;

public class AgentConfirmRegistrationPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	CustomerRegistrationBean customer;

	public AgentConfirmRegistrationPage() {
		initPageComponents();
	}

	public AgentConfirmRegistrationPage(CustomerRegistrationBean customer) {
		super();
		this.customer = customer;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ConfirmRegistrationPanel("confirmRegistrationPanel", this.customer, this));

	}

}
