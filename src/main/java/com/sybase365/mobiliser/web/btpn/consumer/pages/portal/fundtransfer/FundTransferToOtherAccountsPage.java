package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.FundTransferToOtherAccountsPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the fund
 * transfer to other accounts page.
 * 
 * @author Narasa Reddy
 */
public class FundTransferToOtherAccountsPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnBaseConsumerPortalSelfCarePage basePage;

	FundTransferBean fundTransferBean;

	/**
	 * Constructor for the Home Page
	 */
	public FundTransferToOtherAccountsPage() {
		super();
		initPageComponents();
	}

	public FundTransferToOtherAccountsPage(FundTransferBean fundTransferBean) {
		super();
		this.fundTransferBean = fundTransferBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new FundTransferToOtherAccountsPanel("fundTransferToOtherAccountsPanel", this, fundTransferBean));
	}

}
