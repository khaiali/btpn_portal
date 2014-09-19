package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashInOutPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


public class BankPortalCustomCashInPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	private static final String CUSTOM_PROCESSING_CODE	= "102401";  
	private BankCustomCashInBean cashInBean;

	public BankPortalCustomCashInPage() {
		super();
		initPageComponents();
	}
	
	public BankPortalCustomCashInPage(BankCustomCashInBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashInOutPanel("customCashInPanel", this, cashInBean, CUSTOM_PROCESSING_CODE, false));
	}
	
}
