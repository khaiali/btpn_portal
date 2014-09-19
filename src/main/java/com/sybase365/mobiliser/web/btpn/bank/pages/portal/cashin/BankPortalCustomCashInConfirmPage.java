package com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankPortalCustomCashInConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashInConfirmPage page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalCustomCashInConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BankCustomCashInBean cashInBean;
	private String PROCESSING_CODE;

	public BankPortalCustomCashInConfirmPage(BankCustomCashInBean cashInBean, String processing_code) {
		super();
		this.cashInBean = cashInBean;
		this.PROCESSING_CODE = processing_code;
		initPageComponents();
	}
	
	protected void initPageComponents() {
		add(new BankPortalCustomCashInConfirmPanel("customCashInConfirmPanel", this, cashInBean, PROCESSING_CODE));
	}

}
