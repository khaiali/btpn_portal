package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageFeeAddPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeAddPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageFeeAddPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		add(new ManageFeeAddPanel("manageFeeAddPanel"));
	}
}
