package com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare;

import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentPortalChangePinPanel;

/**
 * This class is the change pin page for BTPN Agent Portal Application.
 * 
 * @author Narasa reddy
 */
public class AgentPortalChangePinPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the Home Page
	 */
	public AgentPortalChangePinPage() {
		super();
		initPageComponents();
	}

	protected void initPageComponents() {
		add(new AgentPortalChangePinPanel("changePinPanel", this));
	}

}
