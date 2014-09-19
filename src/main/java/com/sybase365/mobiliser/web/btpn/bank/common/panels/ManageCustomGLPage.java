package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomGLPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the Manage Custom General Ledger Page for Bank Portals. It shows the list of GL codes and descriptions.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomGLPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	public ManageCustomGLPage(){
		super();
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageCustomGLPanel("ManageCusGLPanel", this));
	}
	
} 

	

