package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGeneralLedgerSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGeneralLedgerSuccessPage extends BtpnBaseBankPortalSelfCarePage {

	protected ManageCustomGeneralLedgerBean ledgerBean; 
	
	public ManageCustomGeneralLedgerSuccessPage(ManageCustomGeneralLedgerBean ledgerBean){
		super();
		this.ledgerBean = ledgerBean;
		initPageComponents();
	}
	
	protected void initPageComponents() {
		add(new ManageCustomGeneralLedgerSuccessPanel("customManageGLSuccessPanel", this, ledgerBean));
	}
}
