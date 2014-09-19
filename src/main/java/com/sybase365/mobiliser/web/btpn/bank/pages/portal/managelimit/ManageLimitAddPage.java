package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managelimit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageLimitDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageLimitAddPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageLimitAddPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		// Add panel
		add(new ManageLimitDetailsPanel("managelimitDetailsPanel", this, new ManageLimitBean(), true));

	}
}
