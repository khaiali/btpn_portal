package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveMsisdnBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApprovalMsisdnPanel;

/**
 * This is the ApproveMsisdnPage for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveMsisdnPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnMobiliserBasePage basePage;

	ApproveMsisdnBean approveBean;

	/**
	 * Default Constructor for this page.
	 */
	public ApproveMsisdnPage() {
		super();
		initPageComponents();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public ApproveMsisdnPage(PageParameters parameters) {
		super();
		initPageComponents();
	}

	public ApproveMsisdnPage(ApproveMsisdnBean bean) {
		super();
		this.approveBean = bean;
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new ApprovalMsisdnPanel("approveMsisdnPanel", this, approveBean));
	}

}
