package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.StandingInstructionFundTransferPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the fund
 * transfer page.
 * 
 * @author Narasa Reddy
 */
public class StandingInstructionFundTransferPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	FundTransferBean fundTransferBean;

	public StandingInstructionFundTransferPage() {
		super();
		initPageComponents();
	}

	public StandingInstructionFundTransferPage(FundTransferBean fundTransferBean) {
		super();
		this.fundTransferBean = fundTransferBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new StandingInstructionFundTransferPanel("standingInstructionFundTransferPanel", this,
			this.fundTransferBean));
	}

}
