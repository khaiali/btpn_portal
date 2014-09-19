package com.sybase365.mobiliser.web.btpn.bank.pages.portal;

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.TransactionSearchResultPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.SearchCustomerCareMenu;

public class TransactionSearchPage extends SearchCustomerCareMenu {

	private static final long serialVersionUID = 1L;

	public static CustomerRegistrationBean registrationBean;

	/**
	 * Default Constructor for this page.
	 */
	public TransactionSearchPage() {
		super();
		registrationBean = this.getMobiliserWebSession().getCustomerRegistrationBean();
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new TransactionSearchResultPanel("txnSearchPanel", this, registrationBean));
	}
}
