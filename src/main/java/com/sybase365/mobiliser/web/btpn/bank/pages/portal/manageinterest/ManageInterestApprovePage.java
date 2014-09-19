package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage InterestApprove page for bank portals.
 * 
 * @author Feny Yanti
 */
public class ManageInterestApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	public ManageInterestApprovePage(){
		super();
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestApprovePanel("interestApprovePanel", this));
	}
	
}



