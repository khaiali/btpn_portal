package com.sybase365.mobiliser.web.btpn.consumer.pages.portal;

import com.sybase365.mobiliser.web.btpn.consumer.common.panels.AddFavoritePanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the AddFavoritePage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class AddFavoritePage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	public AddFavoritePage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AddFavoritePanel("addFavoritePanel", this));
	}

}
