package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import org.apache.commons.lang.StringUtils;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGeneralLedgerAddPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGeneralLedgerAddPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L; 
	
	protected ManageCustomGeneralLedgerBean cusLedgerBean;
	protected String flag;
	
	public ManageCustomGeneralLedgerAddPage(ManageCustomGeneralLedgerBean cusLedgerBean, String flag){		
		super();
		this.cusLedgerBean = cusLedgerBean;
		this.flag = flag;
		initPageComponents(cusLedgerBean, flag);
	}
	
	protected void initPageComponents(ManageCustomGeneralLedgerBean cusLedgerBean, String flag) {
		if (StringUtils.equalsIgnoreCase("add", flag))
			add(new ManageCustomGeneralLedgerAddPanel("customManageGLAddPanel", this));
		
		if (StringUtils.equalsIgnoreCase("update", flag))
			add(new ManageCustomGeneralLedgerAddPanel("customManageGLAddPanel", this, cusLedgerBean));
	}
}
