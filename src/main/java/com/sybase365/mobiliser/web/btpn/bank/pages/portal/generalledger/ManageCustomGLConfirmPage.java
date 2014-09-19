package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGLConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGLConfirmPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	protected String flag;
	
	public ManageCustomGLConfirmPage(ManageCustomGeneralLedgerBean cusLedgerBean, String flag){
		super();
		this.cusLedgerBean = cusLedgerBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageCustomGLConfirmPanel("ManageCusGLConfirmPanel", this, cusLedgerBean, flag));
	}
	
} 

	

