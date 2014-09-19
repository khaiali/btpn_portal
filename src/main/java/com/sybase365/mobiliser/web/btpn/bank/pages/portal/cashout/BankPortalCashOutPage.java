package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashOutPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashOutPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public BankPortalCashOutPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashOutPanel("cashOutPanel", this));
	}

}
