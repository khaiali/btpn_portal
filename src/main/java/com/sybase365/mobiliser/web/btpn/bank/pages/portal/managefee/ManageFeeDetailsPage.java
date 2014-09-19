package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageFeeDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageFeeDetailsPage(final ManageFeeBean bean) {		
		super();
		initThisPageComponents(bean);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */

	protected void initThisPageComponents(final ManageFeeBean bean) {
		add(new ManageFeeDetailsPanel("managefeesDetailsPanel",this, bean));

	}
}
