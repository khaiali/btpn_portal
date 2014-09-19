package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCashOutDetailsPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;

/**
 * This is the CashOutDetailsPage page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashOutDetailsPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AgentCashOutBean cashOutBean;

	public AgentPortalCashOutDetailsPage(AgentCashOutBean cashOutBean) {
		super();
		this.cashOutBean = cashOutBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCashOutDetailsPanel("cashOutDetailsPanel", this, cashOutBean));
	}

}
