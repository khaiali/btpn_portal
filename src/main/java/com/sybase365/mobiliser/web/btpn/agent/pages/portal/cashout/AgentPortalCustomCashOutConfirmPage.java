package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCustomCashOutConfirmPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;


public class AgentPortalCustomCashOutConfirmPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AgentCustomCashOutBean cashOutBean;

	public AgentPortalCustomCashOutConfirmPage(AgentCustomCashOutBean cashOutBean) {
		super();
		this.cashOutBean = cashOutBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCustomCashOutConfirmPanel("agentCustomCashOutConfirmPanel", this, cashOutBean));
	}

}
