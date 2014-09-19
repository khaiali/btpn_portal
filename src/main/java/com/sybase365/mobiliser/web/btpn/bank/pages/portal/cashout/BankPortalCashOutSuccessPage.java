package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashOutSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashOutSuccessPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BankCashOutBean cashOutBean;

	public BankPortalCashOutSuccessPage(BankCashOutBean cashOutBean) {
		super();
		this.cashOutBean = cashOutBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashOutSuccessPanel("cashOutSuccessPanel", this, cashOutBean));
	}

}
