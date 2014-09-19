package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage InterestTaxApprove page for bank portals.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	public ManageInterestTaxApprovePage(){
		super();
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxApprovePanel("interesTaxApprovePanel", this));
	}
	
}



