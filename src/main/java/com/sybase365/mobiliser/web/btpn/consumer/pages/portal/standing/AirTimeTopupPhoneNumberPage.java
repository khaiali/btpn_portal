package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.AirTimeTopupBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.AirtimeTopupPhoneNumberPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This consists of the all the components for the bill payment details
 * 
 * @author Narasa Reddy
 */
public class AirTimeTopupPhoneNumberPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	AirTimeTopupBean topupBean;

	public AirTimeTopupPhoneNumberPage() {
		super();
		initPageComponents();
	}

	public AirTimeTopupPhoneNumberPage(AirTimeTopupBean topupBean) {
		super();
		this.topupBean = topupBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AirtimeTopupPhoneNumberPanel("airTimeTopupPhoneNumberPanel", this, topupBean));
	}

}
