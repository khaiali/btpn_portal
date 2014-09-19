package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGLSuccessApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGLSuccessApprovePage extends BtpnBaseBankPortalSelfCarePage {

	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	protected String flag;
	
	public ManageCustomGLSuccessApprovePage(ManageCustomGeneralLedgerBean cusLedgerBean, String flag){
		super();
		this.cusLedgerBean = cusLedgerBean;
		this.flag = flag;
		initPageComponents();
	}
	
	protected void initPageComponents() {
		add(new ManageCustomGLSuccessApprovePanel("ManageCusGLSuccessAppPanel", this, cusLedgerBean, flag));
	}
}
