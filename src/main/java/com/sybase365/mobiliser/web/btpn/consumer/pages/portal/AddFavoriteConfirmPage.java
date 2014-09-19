package com.sybase365.mobiliser.web.btpn.consumer.pages.portal;

import com.sybase365.mobiliser.web.btpn.consumer.beans.ManageFavoritesBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.AddFavoriteConfirmPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the AddAccountConfirmPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class AddFavoriteConfirmPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	ManageFavoritesBean favoritesBean;

	public AddFavoriteConfirmPage() {
		super();
		initPageComponents();
	}

	public AddFavoriteConfirmPage(ManageFavoritesBean favoritesBean) {
		super();
		this.favoritesBean = favoritesBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AddFavoriteConfirmPanel("addFavoriteConfirmPanel", this, favoritesBean));
	}

}
