package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCashinDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashinDetailsPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashinDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BankCashinBean cashInBean;

	public BankPortalCashinDetailsPage() {
		super();
		initPageComponents();
	}

	public BankPortalCashinDetailsPage(BankCashinBean cashInBean) {
		super();
		this.cashInBean = cashInBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new BankPortalCashinDetailsPanel("cashInDetailsPanel", this, cashInBean));
	}

}
