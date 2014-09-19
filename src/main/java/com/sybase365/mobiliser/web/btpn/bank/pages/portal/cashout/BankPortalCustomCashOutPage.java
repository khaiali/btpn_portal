package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashInOutPanel;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCustomCashOutPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the BankPortalCustomCashOutPage page for bank portals.
 * 
 * @author Andi Samallangi W
 * @modified Feny Yanti
 */
public class BankPortalCustomCashOutPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;
	private static final String CUSTOM_PROCESSING_CODE	= "102501";  
	private BankCustomCashInBean cashInBean;

	public BankPortalCustomCashOutPage() {
		super();
		initPageComponents();
	}
	
	public BankPortalCustomCashOutPage(BankCustomCashInBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashInOutPanel("customCashOutPanel", this, cashInBean, CUSTOM_PROCESSING_CODE, true));
	}
	
}
