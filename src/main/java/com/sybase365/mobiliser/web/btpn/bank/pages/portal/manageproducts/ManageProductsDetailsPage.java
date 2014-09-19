package com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageProductsDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the Manage Products Details page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	
	private ManageProductsBean bean;
	/**
	 * Constructor for this page.
	 */
	public ManageProductsDetailsPage(final ManageProductsBean bean ) {
		super();
		this.bean =  bean;
		initComponents();
		
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();		

	}
	
	protected void initComponents() {
		add(new ManageProductsDetailsPanel("manageProductsDetailsPanel",this, bean, false));		
	}
}
