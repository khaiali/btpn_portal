package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashInOutPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


public class BankPortalTopAgentCashInPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	
	private BankCustomCashInBean cashInBean;
	private static final String AGENT_PROCESSING_CODE	= "100101"; 
	
	public BankPortalTopAgentCashInPage() {
		super();
		initPageComponents();
	}
	
	public BankPortalTopAgentCashInPage(BankCustomCashInBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashInOutPanel("agentCashInPanel", this, cashInBean, AGENT_PROCESSING_CODE, false));
	}
	
}
