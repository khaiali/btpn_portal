package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashinSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashinSuccessPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashinSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BankCashinBean cashInBean;

	public BankPortalCashinSuccessPage(BankCashinBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashinSuccessPanel("cashInSuccessPanel", this, cashInBean));
	}

}
