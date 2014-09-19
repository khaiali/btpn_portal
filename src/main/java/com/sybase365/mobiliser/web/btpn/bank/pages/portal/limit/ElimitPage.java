package com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ElimitPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the ElimitPage page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public ElimitPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ElimitPanel("elimitPanel", this));
	}
	
}
