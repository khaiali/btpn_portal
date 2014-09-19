package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.AirTimeTopupBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.TopupDenominationsPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the toup
 * denominations page.
 * 
 * @author Narasa Reddy
 */
public class TopupDenominationsPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AirTimeTopupBean topupBean;

	public TopupDenominationsPage() {
		super();
		initPageComponents();
	}

	public TopupDenominationsPage(AirTimeTopupBean topupBean) {
		super();
		this.topupBean = topupBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new TopupDenominationsPanel("topupDenominationsPanel", this, this.topupBean));
	}

}
