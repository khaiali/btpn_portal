package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.SubAccountTransferConfirmPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the SubAccountTransferConfirmPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class SubAccountTransferConfirmPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnMobiliserBasePage basePage;

	SubAccountsBean subAccountBean;

	String selectedTransferType;

	public SubAccountTransferConfirmPage() {
		super();
		initPageComponents();
	}

	public SubAccountTransferConfirmPage(SubAccountsBean subAccountBean, String type) {
		super();
		this.subAccountBean = subAccountBean;
		this.selectedTransferType = type;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new SubAccountTransferConfirmPanel("subAccountTransferConfirmPanel", this, subAccountBean,
			selectedTransferType));
	}

}
