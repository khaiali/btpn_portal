package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestSuccessApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Fee details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Andi Samallangi W
 */
public class ManageInterestSuccessApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestApproveBean interestBean;
	protected String flag;

	public ManageInterestSuccessApprovePage(ManageInterestApproveBean interestBean, String flag) {
		super();
		this.interestBean = interestBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestSuccessApprovePanel("ManInterestSucAprPanel", this, interestBean, flag));
	}
	
}
