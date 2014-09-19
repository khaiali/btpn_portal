package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.AirTimeTopupBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.ConfirmTopupPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This consists of the all the components for the Topup confirm details
 * 
 * @author Narasa Reddy
 */
public class ConfirmTopupPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AirTimeTopupBean topupBean;

	/**
	 * Constructor for the Home Page
	 */
	public ConfirmTopupPage() {
		super();
		initPageComponents();
	}

	public ConfirmTopupPage(AirTimeTopupBean topupBean) {
		super();
		this.topupBean = topupBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new ConfirmTopupPanel("confirmTopupPanel", this, topupBean));
	}

}
