package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGeneralLedgerPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGeneralLedgerPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	public ManageCustomGeneralLedgerPage(){
		super();
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageCustomGeneralLedgerPanel("customManageGLPanel", this));
	}
	
} 

	

