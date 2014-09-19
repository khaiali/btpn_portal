package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageAirtimeTopupFeeDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupFeeDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageAirtimeTopupFeeDetailsPage(final ManageAirtimeTopupFeeBean billPayBean) {
		super();
		initThisPageComponents(billPayBean);
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
	private void initThisPageComponents(final ManageAirtimeTopupFeeBean billPayBean) {
		add(new ManageAirtimeTopupFeeDetailsPanel("manageAirtimeTopupFeeDetailsPanel", this,billPayBean));
	}
}
