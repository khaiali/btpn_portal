package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCustomCashOutSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashOutSuccessPage page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalCustomCashOutSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BankCustomCashOutBean cashOutBean;

	public BankPortalCustomCashOutSuccessPage(BankCustomCashOutBean cashOutBean) {
		super();
		this.cashOutBean = cashOutBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCustomCashOutSuccessPanel("customCashOutSuccessPanel", this, cashOutBean));
	}

}
