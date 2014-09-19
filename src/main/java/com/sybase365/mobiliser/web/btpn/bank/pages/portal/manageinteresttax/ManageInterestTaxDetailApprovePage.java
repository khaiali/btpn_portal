package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestDetailApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestTaxDetailApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Interest Approve details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxDetailApprovePage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestTaxApproveBean interestBean;

	public ManageInterestTaxDetailApprovePage(ManageInterestTaxApproveBean interestBean) {
		super();
		this.interestBean = interestBean;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestTaxDetailApprovePanel("interestDetAprPanel", this, interestBean));
	}
	
}
