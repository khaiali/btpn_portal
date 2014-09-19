package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer;

import com.sybase365.mobiliser.web.btpn.consumer.common.panels.AddAccountPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the AddAccountPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class AddAccountPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public AddAccountPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AddAccountPanel("addAccountPanel", this));
	}

}
