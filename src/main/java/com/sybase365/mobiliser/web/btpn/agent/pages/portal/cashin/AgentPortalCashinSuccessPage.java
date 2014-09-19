package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashinBean;
import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCashinSuccessPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;

/**
 * This is the CashinSuccessPage page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashinSuccessPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AgentCashinBean cashInBean;

	public AgentPortalCashinSuccessPage(AgentCashinBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCashinSuccessPanel("cashInSuccessPanel", this, cashInBean));
	}

}
