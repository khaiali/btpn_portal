package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGeneralLedgerConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGeneralLedgerConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private ManageCustomGeneralLedgerBean ledgerBean; 
	
	public ManageCustomGeneralLedgerConfirmPage(ManageCustomGeneralLedgerBean ledgerBean){
		super();
		this.ledgerBean = ledgerBean;
		initPageComponents();
	}
	
	protected void initPageComponents() {
		add(new ManageCustomGeneralLedgerConfirmPanel("customManageGLConfirmPanel", this, ledgerBean));
	}
}
