package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGLDetailPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGLDetailPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageCustomGeneralLedgerBean cusLedgerBean; 
	
	public ManageCustomGLDetailPage(ManageCustomGeneralLedgerBean cusLedgerBean){
		super();
		this.cusLedgerBean = cusLedgerBean;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageCustomGLDetailPanel("ManageCusGLDetailPanel", this, cusLedgerBean));
	}
	
} 

	

