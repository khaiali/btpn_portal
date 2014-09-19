package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.StandingInstructionOtherAccountsPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the fund
 * transfer to other accounts page.
 * 
 * @author Narasa Reddy
 */
public class StandingInstructionOtherAccountsPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	FundTransferBean fundTransferBean;

	public StandingInstructionOtherAccountsPage() {
		super();
		initPageComponents();
	}

	public StandingInstructionOtherAccountsPage(FundTransferBean fundTransferBean) {
		super();
		this.fundTransferBean = fundTransferBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new StandingInstructionOtherAccountsPanel("standingInstructionOtherAccountsPanel", this,
			this.fundTransferBean));
	}

}
