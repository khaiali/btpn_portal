package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managelimit;

import com.sybase365.mobiliser.web.btpn.bank.beans.ManageLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ManageLimitDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;


/**
 * This is the Manage Products Details page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageLimitDetailsPage extends BtpnBaseBankPortalSelfCarePage {

	
	private ManageLimitBean bean;
	/**
	 * Constructor for this page.
	 */
	public ManageLimitDetailsPage(final ManageLimitBean bean ) {
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
		add(new ManageLimitDetailsPanel("managelimitDetailsPanel",this, bean, false));		
	}
}
