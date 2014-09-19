package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGLSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGLSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	
	public ManageCustomGLSuccessPage(ManageCustomGeneralLedgerBean cusLedgerBean){
		super();
		this.cusLedgerBean = cusLedgerBean;
		initPageComponents();
	}
	
	protected void initPageComponents() {
		add(new ManageCustomGLSuccessPanel("ManageCusGLSuccessPanel", this, cusLedgerBean));
	}
}
