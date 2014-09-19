package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxDetailPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Interest Tax details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxDetailPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	protected ManageInterestTaxBean interestTaxBean;

	public ManageInterestTaxDetailPage(ManageInterestTaxBean interestTaxBean) {
		super();
		this.interestTaxBean = interestTaxBean;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxDetailPanel("ManInterestTaxDetailPanel", this, interestTaxBean));
	}
	
}
