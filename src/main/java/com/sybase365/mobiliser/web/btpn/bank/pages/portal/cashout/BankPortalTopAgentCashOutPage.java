package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashInOutPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the BankPortalCustomCashOutPage page for bank portals.
 * 
 * @author Feny Yanti
 */
public class BankPortalTopAgentCashOutPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	private static final String AGENT_PROCESSING_CODE	= "100201";  
	private BankCustomCashInBean cashInBean;

	public BankPortalTopAgentCashOutPage() {
		super();
		initPageComponents();
	}
	
	public BankPortalTopAgentCashOutPage(BankCustomCashInBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashInOutPanel("agentCashOutPanel", this, cashInBean, AGENT_PROCESSING_CODE, true));
	}
	
}
