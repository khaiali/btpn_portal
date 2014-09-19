package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.RemoveAccountConfirmPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the RemoveAccountConfirmPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class RemoveAccountConfirmPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	SubAccountsBean subAccountBean;

	public RemoveAccountConfirmPage() {
		super();
		initPageComponents();
	}

	public RemoveAccountConfirmPage(SubAccountsBean subAccountBean) {
		super();
		this.subAccountBean = subAccountBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new RemoveAccountConfirmPanel("removeAccountConfirmPanel", this, subAccountBean));
	}

}
