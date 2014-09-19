package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCustomCashInSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashinSuccessPage page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalCustomCashInSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BankCustomCashInBean cashInBean;

	public BankPortalCustomCashInSuccessPage(BankCustomCashInBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCustomCashInSuccessPanel("customCashInSuccessPanel", this, cashInBean));
	}

}
