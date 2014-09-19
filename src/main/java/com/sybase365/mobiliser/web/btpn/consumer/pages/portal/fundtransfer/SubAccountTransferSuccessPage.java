package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.SubAccountTransferSuccessPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the SubAccountTransferSuccessPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class SubAccountTransferSuccessPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnMobiliserBasePage basePage;

	SubAccountsBean subAccountBean;

	public SubAccountTransferSuccessPage() {
		super();
		initPageComponents();
	}

	public SubAccountTransferSuccessPage(SubAccountsBean subAccountBean) {
		super();
		this.subAccountBean = subAccountBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new SubAccountTransferSuccessPanel("subAccountTransferSuccessPanel", this, subAccountBean));
	}

}
