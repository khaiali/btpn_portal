package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageProductsAddConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsAddConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private ManageProductsBean bean;
	/**
	 * Constructor for this page.
	 */
	public ManageProductsAddConfirmPage(final ManageProductsBean bean) {
		super();
		this.bean = bean;
		initThisPageComponents();
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
	 * This is used for this page components
	 */
	protected void  initThisPageComponents(){
		add(new ManageProductsAddConfirmPanel("manageProductsAddConfirmPanel", this, bean));
		
	}
}
