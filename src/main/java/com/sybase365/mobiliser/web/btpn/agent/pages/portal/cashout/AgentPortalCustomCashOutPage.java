package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCustomCashOutPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;


public class AgentPortalCustomCashOutPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public AgentPortalCustomCashOutPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCustomCashOutPanel("agentCustomCashOutPanel", this));
	}

}
