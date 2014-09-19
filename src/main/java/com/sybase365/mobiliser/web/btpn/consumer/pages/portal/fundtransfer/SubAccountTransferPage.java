package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.SubAccountTransferPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the sub
 * account fund transfer page.
 * 
 * @author Narasa Reddy
 */
public class SubAccountTransferPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	SubAccountsBean subAccountsBean;

	public SubAccountTransferPage() {
		super();
		initPageComponents();
	}

	public SubAccountTransferPage(SubAccountsBean subAccountsBean) {
		super();
		this.subAccountsBean = subAccountsBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new SubAccountTransferPanel("subAccountTransferPanel", this, this.subAccountsBean));
	}

}
