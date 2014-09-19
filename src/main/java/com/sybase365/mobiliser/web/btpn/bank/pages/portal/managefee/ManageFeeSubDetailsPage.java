package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageFeeAddDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeSubDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageFeeSubDetailsPage(final ManageFeeBean bean, final ManageFeeDetailsBean detailsBean) {
		super();
		initThisPageComponents(bean, detailsBean);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */

	protected void initThisPageComponents(final ManageFeeBean bean, final ManageFeeDetailsBean detailsBean) {
		add(new ManageFeeAddDetailsPanel("managefeesSubDetailsPanel", this, bean, detailsBean, false));

	}
}
