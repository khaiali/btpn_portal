package com.sybase365.mobiliser.web.btpn.bank.pages.portal.transaction;

import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionReversalBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.ApproveTxnReversalConfirmPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Approve Transaction Reversal page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ApproveTxnReversalConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	TransactionReversalBean txnReversalBean;

	/**
	 * Constructor for this page.
	 */
	public ApproveTxnReversalConfirmPage() {
		super();
		initPageComponents();
	}

	public ApproveTxnReversalConfirmPage(TransactionReversalBean txnReversalBean) {
		super();
		this.txnReversalBean = txnReversalBean;
		initPageComponents();
	}

	/**
	 * Initialize the Components.
	 */
	protected void initPageComponents() {
		add(new ApproveTxnReversalConfirmPanel("approveTxnReversalConfirmPanel", this, txnReversalBean));
	}
}
