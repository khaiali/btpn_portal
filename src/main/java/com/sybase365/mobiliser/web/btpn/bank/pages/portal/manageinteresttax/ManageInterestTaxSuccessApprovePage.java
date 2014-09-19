package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxSuccessApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Fee details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Andi Samallangi W
 */
public class ManageInterestTaxSuccessApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestTaxApproveBean interestBean;
	protected String flag;

	public ManageInterestTaxSuccessApprovePage(ManageInterestTaxApproveBean interestBean, String flag) {
		super();
		this.interestBean = interestBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxSuccessApprovePanel("interestSucAprPanel", this, interestBean, flag));
	}
	
}
