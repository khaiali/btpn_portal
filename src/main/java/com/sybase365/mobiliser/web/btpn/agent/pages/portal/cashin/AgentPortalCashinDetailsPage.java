package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashinBean;
import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCashinDetailsPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;

/**
 * This is the CashinDetailsPage page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashinDetailsPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AgentCashinBean cashInBean;

	public AgentPortalCashinDetailsPage(AgentCashinBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCashinDetailsPanel("cashInDetailsPanel", this, cashInBean));
	}

}
