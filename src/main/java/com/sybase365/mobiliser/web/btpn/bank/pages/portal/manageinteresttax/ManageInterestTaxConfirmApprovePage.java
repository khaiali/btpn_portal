package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxConfirmApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Interest Approve Confirm Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxConfirmApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestTaxApproveBean interestTaxBean;
	protected String flag;

	public ManageInterestTaxConfirmApprovePage(ManageInterestTaxApproveBean interestTaxBean, String flag) {
		super();
		this.interestTaxBean = interestTaxBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxConfirmApprovePanel("interestTaxConfAprPanel", this, interestTaxBean, flag));
	}
	
}
