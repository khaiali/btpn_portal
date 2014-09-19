package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageProductsApprovePanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsApprovePage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageProductsApprovePage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		add(new ManageProductsApprovePanel("manageProductsApprovePanel", this));

	}
}
