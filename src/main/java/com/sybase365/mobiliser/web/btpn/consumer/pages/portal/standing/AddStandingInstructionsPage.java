package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.StandingInstructionsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.AddStandingInstructionsPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This class is the home page BTPN Consumer Portal Application. This consists of the all the components for the
 * standing instructions.
 * 
 * @author Narasa Reddy
 */
public class AddStandingInstructionsPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	StandingInstructionsBean instructionsBean;

	public AddStandingInstructionsPage() {
		super();
		initPageComponents();
	}

	public AddStandingInstructionsPage(final StandingInstructionsBean instructionsBean) {
		this.instructionsBean = instructionsBean;
	}

	protected void initPageComponents() {
		add(new AddStandingInstructionsPanel("StandingInstructionsPanel", this, instructionsBean));
	}

}
