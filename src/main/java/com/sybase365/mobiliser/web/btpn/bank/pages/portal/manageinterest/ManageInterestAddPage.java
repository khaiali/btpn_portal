package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestAddPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Interest details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestAddPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	
	protected ManageInterestBean interestBean;

	public ManageInterestAddPage() {
		super();
		initPageComponent();
	}
	
	public ManageInterestAddPage(ManageInterestBean interestBean) {
		super();
		this.interestBean = interestBean;
		initPageComponents();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestAddPanel("ManInterestAddPanel", this));
	}
	
	protected void initPageComponents(){
		add(new ManageInterestAddPanel("ManInterestAddPanel", this, interestBean));
	}
	
}
