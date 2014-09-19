package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add InterestTax details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxConfirmPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestTaxBean interestTaxBean;
	protected String flag;

	public ManageInterestTaxConfirmPage(ManageInterestTaxBean interestTaxBean, String flag) {
		super();
		this.interestTaxBean = interestTaxBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxConfirmPanel("ManInterestTaxConfirmPanel", this, interestTaxBean, flag));
	}
	
}
