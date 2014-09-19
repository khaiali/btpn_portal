package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest;


import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Interest page for bank portals.
 * 
 * @author Feny Yanti
 */
public class ManageInterestPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	public ManageInterestPage(){
		super();
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestPanel("ManInterestPanel", this));
	}
	
}



