package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxEditPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add InterestTax details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxEditPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestTaxBean interestTaxBean;

	public ManageInterestTaxEditPage(ManageInterestTaxBean interestTaxBean) {
		super();
		this.interestTaxBean = interestTaxBean;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxEditPanel("ManInterestTaxEditPanel", this, interestTaxBean));
	}
	
}
