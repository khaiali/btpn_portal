package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageCustomUseCaseFeePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	public ManageCustomUseCaseFeePage(){
		super();
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageCustomUseCaseFeePanel("ManCusUseCaseFeePanel", this));
	}
	
}



