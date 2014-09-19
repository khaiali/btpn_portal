package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCustomCashInPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;

/**
 * This is the CashInPage page for agent portals.
 * 
 * @author Andi Samallangi W
 */
public class AgentPortalCustomCashInPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public AgentPortalCustomCashInPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCustomCashInPanel("agentCustomCashInPanel", this));
	}

}
