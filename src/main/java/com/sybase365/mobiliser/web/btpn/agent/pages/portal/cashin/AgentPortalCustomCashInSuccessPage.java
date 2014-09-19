package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCustomCashInSuccessPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;


public class AgentPortalCustomCashInSuccessPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AgentCustomCashInBean cashInBean;

	public AgentPortalCustomCashInSuccessPage(AgentCustomCashInBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCustomCashInSuccessPanel("agentCustomCashInSuccessPanel", this, cashInBean));
	}

}
