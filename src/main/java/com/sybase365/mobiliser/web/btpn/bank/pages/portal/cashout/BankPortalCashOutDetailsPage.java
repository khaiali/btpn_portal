package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashOutDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashOutDetailsPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BankCashOutBean cashOutBean;

	public BankPortalCashOutDetailsPage() {
		super();
		initPageComponents();
	}

	public BankPortalCashOutDetailsPage(BankCashOutBean cashOutBean) {
		super();
		this.cashOutBean = cashOutBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashOutDetailsPanel("cashOutDetailsPanel", this, cashOutBean));
	}

}
