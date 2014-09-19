package com.sybase365.mobiliser.web.btpn.agent.pages.portal.approval;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankCheckerApprovalPanel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the ConsumerApprovalPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class AgentApprovalPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnMobiliserBasePage basePage;

	public AgentApprovalPage() {
		super();
		initPageComponents();
	}

	public AgentApprovalPage(final PageParameters parameters) {
		super(parameters);
	}

	protected void initPageComponents() {
		add(new BankCheckerApprovalPanel("consumerApprovalPanel", this, BtpnConstants.REG_CHILD_AGENT));
	}
}
