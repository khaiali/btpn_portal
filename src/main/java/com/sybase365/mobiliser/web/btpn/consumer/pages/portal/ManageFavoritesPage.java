package com.sybase365.mobiliser.web.btpn.consumer.pages.portal;

import com.sybase365.mobiliser.web.btpn.consumer.common.panels.ManageFavoritesPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the ManageFavoritesPage for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class ManageFavoritesPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public ManageFavoritesPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ManageFavoritesPanel("manageFavoritesPanel", this));
	}

}
