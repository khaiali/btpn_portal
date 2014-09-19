package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageFeeAddConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Confirm Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeAddConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 * 
	 * @param feeDBean feeDBean for fees.
	 */
	public ManageFeeAddConfirmPage(final ManageFeeBean feeDBean) {
		super();
		initThisPageComponents(feeDBean);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	protected void initThisPageComponents(final ManageFeeBean feeDBean) {
		add(new ManageFeeAddConfirmPanel("manageFeeAddConfirmPanel", this, feeDBean));
	}
}
