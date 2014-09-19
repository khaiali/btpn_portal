package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.SelectSubAccountPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the SelectSubAccountPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class SelectSubAccountPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	SubAccountsBean subAccountBean;

	public SelectSubAccountPage() {
		super();
		initPageComponents();
	}

	public SelectSubAccountPage(SubAccountsBean subAccountBean) {
		super();
		this.subAccountBean = subAccountBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new SelectSubAccountPanel("selectSubAccountPanel", this, subAccountBean));
	}

}
