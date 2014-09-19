package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestConfirmApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Interest Approve Confirm Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestConfirmApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestApproveBean interestBean;
	protected String flag;

	public ManageInterestConfirmApprovePage(ManageInterestApproveBean interestBean, String flag) {
		super();
		this.interestBean = interestBean;
		this.flag = flag;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestConfirmApprovePanel("interestConfAprPanel", this, interestBean, flag));
	}
	
}
