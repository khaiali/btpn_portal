package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;


import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	public ManageInterestTaxPage(){
		super();
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxPanel("ManInterestTaxPanel", this));
	}
	
}



