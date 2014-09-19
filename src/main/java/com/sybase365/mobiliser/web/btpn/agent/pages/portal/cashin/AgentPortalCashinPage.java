package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCashinPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;

/**
 * This is the CashInPage page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashinPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public AgentPortalCashinPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCashinPanel("agentCashInPanel", this));
	}

}
