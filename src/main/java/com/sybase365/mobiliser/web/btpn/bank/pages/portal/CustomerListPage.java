package com.sybase365.mobiliser.web.btpn.bank.pages.portal;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.bank.common.panels.SearchCustomerPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBankPortalCustomerCareMenuPage;

public class CustomerListPage extends BtpnBankPortalCustomerCareMenuPage {
	
	private static final long serialVersionUID = 1L;

//	CustomerSearchBean customerSearchBean;
	
	private String searchFor;
	
	private String type;

	/**
	 * Default Constructor for this page.
	 */
	public CustomerListPage() {
		super();
		
		initPageComponents();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public CustomerListPage(PageParameters parameters) {
		super();
		
		initPageComponents();
	}

	public CustomerListPage(String searchFor, String type) {
		super();
		
//		this.customerSearchBean = customerSearchBean;
		
		this.searchFor = searchFor;
		this.type = type;
		
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new SearchCustomerPanel("searchCustomerListPanel", this, searchFor, type));
	}
}
