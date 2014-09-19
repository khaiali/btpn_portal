package com.sybase365.mobiliser.web.btpn.bank.pages.portal.transaction;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionReversalBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.TransactionReversalPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the TransactionReversalPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class TransactionReversalPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnBaseBankPortalSelfCarePage basePage;

	TransactionReversalBean txnBean;

	public TransactionReversalPage() {
		super();
		initPageComponents();
	}

	public TransactionReversalPage(TransactionReversalBean txnBean) {
		super();
		this.txnBean = txnBean;
		initPageComponents();
	}

	public TransactionReversalPage(final PageParameters parameters) {
		super(parameters);
	}

	protected void initPageComponents() {
		add(new TransactionReversalPanel("transactionReversalPanel", this, this.txnBean));
	}

}
