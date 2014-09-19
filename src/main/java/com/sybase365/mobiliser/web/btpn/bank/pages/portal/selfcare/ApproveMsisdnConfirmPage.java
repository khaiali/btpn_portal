package com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveMsisdnBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveMsisdnConfirmPanel;

/**
 * This is the ApproveMsisdnConfirmPage for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveMsisdnConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnMobiliserBasePage basePage;

	ApproveMsisdnBean approveMsisdnBean;

	/**
	 * Default Constructor for this page.
	 */
	public ApproveMsisdnConfirmPage() {
		super();
		initPageComponents();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public ApproveMsisdnConfirmPage(PageParameters parameters) {
		super();
		initPageComponents();
	}

	public ApproveMsisdnConfirmPage(ApproveMsisdnBean bean) {
		super();
		this.approveMsisdnBean = bean;
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new ApproveMsisdnConfirmPanel("approveMsisdnConfirmPanel", this, approveMsisdnBean));
	}

}
