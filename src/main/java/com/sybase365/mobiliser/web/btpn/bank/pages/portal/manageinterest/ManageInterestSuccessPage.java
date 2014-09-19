package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageInterestSuccessPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Interest details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Feny Yanti
 */
public class ManageInterestSuccessPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final long serialVersionUID = 1L;
	protected ManageInterestBean interestBean;
	
	public ManageInterestSuccessPage(ManageInterestBean interestBean) {
		super();
		this.interestBean = interestBean;
		initPageComponent();
	}
	
	protected void initPageComponent(){
		add(new ManageInterestSuccessPanel("interestSuccessPanel", this, interestBean));
	}
	
}
