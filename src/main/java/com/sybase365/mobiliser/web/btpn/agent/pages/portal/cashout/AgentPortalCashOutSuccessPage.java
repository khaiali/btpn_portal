package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalCashOutSuccessPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;

/**
 * This is the CashOutSuccessPage page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashOutSuccessPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AgentCashOutBean cashOutBean;

	public AgentPortalCashOutSuccessPage(AgentCashOutBean cashOutBean) {
		super();
		this.cashOutBean = cashOutBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalCashOutSuccessPanel("cashOutSuccessPanel", this, cashOutBean));
	}

}
