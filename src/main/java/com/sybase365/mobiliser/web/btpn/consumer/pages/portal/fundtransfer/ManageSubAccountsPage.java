package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.consumer.common.panels.ManageSubAccountsPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the ManageSubAccountsPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class ManageSubAccountsPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public ManageSubAccountsPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ManageSubAccountsPanel("manageSubAccountsPanel", this));
	}

}
