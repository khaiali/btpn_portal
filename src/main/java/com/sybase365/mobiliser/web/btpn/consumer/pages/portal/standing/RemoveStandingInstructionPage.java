package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing;

import com.sybase365.mobiliser.web.btpn.consumer.beans.StandingInstructionsBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.RemoveStandingInstructionPanel;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the RemoveStandingInstructionPage page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class RemoveStandingInstructionPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	StandingInstructionsBean instructionsBean;

	public RemoveStandingInstructionPage() {
		super();
		initPageComponents();
	}

	public RemoveStandingInstructionPage(StandingInstructionsBean instructionsBean) {
		super();
		this.instructionsBean = instructionsBean;
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new RemoveStandingInstructionPanel("removeStandingInstructionsPanel", this, instructionsBean));
	}

}
