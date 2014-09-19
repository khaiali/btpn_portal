package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import com.sybase365.mobiliser.web.btpn.consumer.common.panels.ConsumerPortalChangePinPanel;

/**
 * This class is the change pin page for BTPN Consumer Portal Application.
 * 
 * @author Narasa reddy
 */
public class ConsumerPortalChangePinPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the Home Page
	 */
	public ConsumerPortalChangePinPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ConsumerPortalChangePinPanel("changePinPanel", this));
	}

}
