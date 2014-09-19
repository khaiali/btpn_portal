package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestDetailPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Fee details Page for bank portals. This consists of different view for fixed, slab and confirm
 * interest.
 * 
 * @author Feny Yanti
 */
public class ManageInterestDetailPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	protected ManageInterestBean interestBean;

	public ManageInterestDetailPage(ManageInterestBean interestBean) {
		super();
		this.interestBean = interestBean;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestDetailPanel("ManInterestDetailPanel", this, interestBean));
	}
	
}
