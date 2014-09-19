package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCustomCashInConfirmPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;


public class AgentPortalCustomCashInConfirmPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AgentCustomCashInBean cashInBean;

	public AgentPortalCustomCashInConfirmPage(AgentCustomCashInBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCustomCashInConfirmPanel("agentCustomCashInConfirmPanel", this, cashInBean));
	}

}
