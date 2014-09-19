package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashInOutConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashInConfirmPage page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalCashInOutConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BankCustomCashInBean cashInBean;
	
	public BankPortalCashInOutConfirmPage(BankCustomCashInBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}
	
	protected void initPageComponents() {
		add(new BankPortalCashInOutConfirmPanel("customCashInConfirmPanel", this, cashInBean));
	}

}
