package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCashOutPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;

/**
 * This is the CashOutPage page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashOutPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public AgentPortalCashOutPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCashOutPanel("agentCashOutPanel", this));
	}

}
