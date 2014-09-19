package com.sybase365.mobiliser.web.btpn.bank.pages.portal.transaction;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionReversalBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveTxnReversalPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ApproveTxnReversalPage for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveTxnReversalPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnMobiliserBasePage basePage;

	TransactionReversalBean txnReversalBean;

	/**
	 * Default Constructor for this page.
	 */
	public ApproveTxnReversalPage() {
		super();
		initPageComponents();
	}

	/**
	 * Constructor for this page.
	 * 
	 * @param customer customer Object
	 */
	public ApproveTxnReversalPage(PageParameters parameters) {
		super();
		initPageComponents();
	}

	public ApproveTxnReversalPage(TransactionReversalBean bean) {
		super();
		this.txnReversalBean = bean;
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new ApproveTxnReversalPanel("approveTxnReversalPanel", this, txnReversalBean));
	}

}
