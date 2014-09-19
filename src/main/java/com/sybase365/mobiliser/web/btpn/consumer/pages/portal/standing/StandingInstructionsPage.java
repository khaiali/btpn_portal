package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.StandingInstructionsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.StandingInstructionsPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the StandingInstructionsPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class StandingInstructionsPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	StandingInstructionsBean instructionsBean;

	public StandingInstructionsPage() {
		super();
		initPageComponents();
	}

	public StandingInstructionsPage(StandingInstructionsBean instructionsBean) {
		super();
		this.instructionsBean = instructionsBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new StandingInstructionsPanel("standingInstructionsPanel", this));
	}

}
