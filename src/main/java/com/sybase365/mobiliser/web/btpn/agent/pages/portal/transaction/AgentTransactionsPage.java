package com.sybase365.mobiliser.web.btpn.agent.pages.portal.transaction;

import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentTransactionDetailsPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;

public class AgentTransactionsPage extends BtpnBaseAgentPortalSelfCarePage {
	public AgentTransactionsPage(){
		initPageComponents();
	}
	
	public void initPageComponents(){
		add(new AgentTransactionDetailsPanel("agentTxnDetailsPanel",this));
	}
}
