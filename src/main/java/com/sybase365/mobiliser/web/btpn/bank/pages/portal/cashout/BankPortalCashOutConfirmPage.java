package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashOutConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashOutConfirmPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BankCashOutBean cashOutBean;

	public BankPortalCashOutConfirmPage(BankCashOutBean cashOutBean) {
		super();
		this.cashOutBean = cashOutBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashOutConfirmPanel("cashOutConfirmPanel", this, cashOutBean));
	}

}
