package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Interest Tax Success details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxSuccessPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestTaxBean interestTaxBean;
	
	public ManageInterestTaxSuccessPage(ManageInterestTaxBean interestTaxBean) {
		super();
		this.interestTaxBean = interestTaxBean;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxSuccessPanel("ManInterestTaxSuccessPanel", this, interestTaxBean));
	}
	
}
