package com.sybase365.mobiliser.web.btpn.bank.pages.portal.approval;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerDataBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveCustomerDataPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

public class ApproveCustomerData extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnMobiliserBasePage basePage;

	CustomerDataBean customerDataBean;

	/**
	 * Default Constructor for this page.
	 */
	public ApproveCustomerData() {
		super();
		initPageComponents();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public ApproveCustomerData(PageParameters parameters) {
		super();
		initPageComponents();
	}

	public ApproveCustomerData(CustomerDataBean bean) {
		super();
		this.customerDataBean = bean;
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new ApproveCustomerDataPanel("approvePanel",customerDataBean,this));
	}
	

}
