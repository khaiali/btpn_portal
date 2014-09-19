package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.ChangeMsisdnPanel;

/**
 * This is the ChangeMsisdnPage for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ChangeMsisdnPage extends SearchCustomerCareMenu {

	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor for this page.
	 */
	public ChangeMsisdnPage() {
		super();
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new ChangeMsisdnPanel("changeMsisdnPanel", this));
	}

}
