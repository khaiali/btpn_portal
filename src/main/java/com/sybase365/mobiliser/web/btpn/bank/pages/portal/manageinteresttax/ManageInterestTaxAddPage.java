package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxAddPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Interest details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxAddPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	protected ManageInterestTaxBean interestTaxBean;

	public ManageInterestTaxAddPage() {
		super();
		initPageComponent();
	}
	
	public ManageInterestTaxAddPage(ManageInterestTaxBean interestTaxBean) {
		super();
		this.interestTaxBean = interestTaxBean;
		initPageComponents();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxAddPanel("interestTaxAddPanel", this));
	}
	
	protected void initPageComponents(){
		add(new ManageInterestTaxAddPanel("interestTaxAddPanel", this, interestTaxBean));
	}
	
}
