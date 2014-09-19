package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageAirtimeTopupFeeAddPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupFeeSubDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageAirtimeTopupFeeSubDetailsPage(final ManageAirtimeTopupFeeBean feeBean,
		ManageFeeDetailsBean feeDetailsBean) {
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
	private void initThisPageComponents(final ManageAirtimeTopupFeeBean feeBean, ManageFeeDetailsBean feeDetailsBean) {
		add(new ManageAirtimeTopupFeeAddPanel("manageAirtimeTopupSubDetailsPanel", this, false, feeBean, feeDetailsBean));
	}
}
