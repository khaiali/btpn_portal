package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts;

import java.util.ArrayList;
import java.util.List;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsRangeBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageProductsDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsAddPage extends BtpnBaseBankPortalSelfCarePage {

	/**
	 * Constructor for this page.
	 */
	public ManageProductsAddPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		final ManageProductsBean bean = new ManageProductsBean();
		bean.setAdminFee(0L);
		bean.setInitialDeposit(0L);
		bean.setMinBalance(0L);
		// List
		final List<ManageProductsRangeBean> rangeList = new ArrayList<ManageProductsRangeBean>();
		rangeList.add(new ManageProductsRangeBean());
		bean.setRangeList(rangeList);
		// Add panel
		add(new ManageProductsDetailsPanel("manageProductsDetailsPanel", this, bean, true));

	}
}
