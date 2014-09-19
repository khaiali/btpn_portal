package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageFeeAddDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Add Fee details Page for bank portals. This consists of different view for fixed, slab and confirm
 * fee.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeAddDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageFeeAddDetailsPage(final ManageFeeBean feeBean, final ManageFeeDetailsBean feeDetailsBean) {
		super();
		initThisPageComponents(feeBean, feeDetailsBean);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 * 
	 * @param feeBean feeBean for Manage beanss
	 * @param feeDetailsBean feeDetailsBean for Manage beans
	 */
	private void initThisPageComponents(final ManageFeeBean feeBean, final ManageFeeDetailsBean feeDetailsBean) {
		add(new ManageFeeAddDetailsPanel("manageFeeAddDetailsPanel", this, feeBean, feeDetailsBean, true));
	}
}
