package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashinPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashInPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashinPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public BankPortalCashinPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashinPanel("cashInPanel", this));
	}

}
