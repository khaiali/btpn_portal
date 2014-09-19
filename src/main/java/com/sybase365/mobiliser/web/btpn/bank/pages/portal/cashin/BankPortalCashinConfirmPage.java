package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashinConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashinConfirmPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashinConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BankCashinBean cashInBean;

	public BankPortalCashinConfirmPage(BankCashinBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}
	
	protected void initPageComponents() {
		add(new BankPortalCashinConfirmPanel("cashInConfirmPanel", this, cashInBean));
	}

}
