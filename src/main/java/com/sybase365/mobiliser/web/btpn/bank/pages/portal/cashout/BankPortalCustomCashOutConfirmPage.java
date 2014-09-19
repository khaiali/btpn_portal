package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCustomCashOutConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashOutConfirmPage page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalCustomCashOutConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BankCustomCashOutBean cashOutBean;

	public BankPortalCustomCashOutConfirmPage(BankCustomCashOutBean cashOutBean) {
		super();
		this.cashOutBean = cashOutBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCustomCashOutConfirmPanel("customCashOutConfirmPanel", this, cashOutBean));
	}

}
