package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.AddAccountConfirmPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the AddAccountConfirmPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class AddAccountConfirmPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	SubAccountsBean subAccountBean;

	public AddAccountConfirmPage() {
		super();
		initPageComponents();
	}

	public AddAccountConfirmPage(SubAccountsBean subAccountBean) {
		super();
		this.subAccountBean = subAccountBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AddAccountConfirmPanel("addAccountConfirmPanel", this, subAccountBean));
	}

}
