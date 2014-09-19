package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGLConfirmApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGLConfirmApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageCustomGeneralLedgerBean cusLedgerBean; 
	protected String flag;
	
	public ManageCustomGLConfirmApprovePage(ManageCustomGeneralLedgerBean cusLedgerBean, String flag){
		super();
		this.cusLedgerBean = cusLedgerBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageCustomGLConfirmApprovePanel("ManageCusGLConfirmAppPanel", this, cusLedgerBean, flag));
	}
	
} 

	

