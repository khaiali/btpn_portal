package com.sybase365.mobiliser.web.btpn.bank.pages.portal;

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.CustomerDetailsPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.SearchCustomerCareMenu;

public class CustomerDetails extends SearchCustomerCareMenu {

	private static final long serialVersionUID = 1L;

	public CustomerRegistrationBean customerRegistrationBean;

	/**
	 * Default Constructor for this page.
	 */
	public CustomerDetails() {
		super();
		
		customerRegistrationBean = getMobiliserWebSession().getCustomerRegistrationBean();
		
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new CustomerDetailsPanel("customerDetailsPanel", this, customerRegistrationBean, null, null));
	}

}
