package com.sybase365.mobiliser.web.btpn.agent.pages.portal.approval;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankCheckerConfirmApprovalPanel;

/**
 * This is the ConsumerApprovalPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class AgentConfirmApprovalPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private CustomerRegistrationBean bean;

	public AgentConfirmApprovalPage(final CustomerRegistrationBean bean) {
		super();
		this.bean = bean;
		initPageComponents();
	}

	public AgentConfirmApprovalPage(final PageParameters parameters) {
		super(parameters);
	}

	protected void initPageComponents() {
		add(new BankCheckerConfirmApprovalPanel("confirmConsumerApprovalPanel", bean, this));
	}

}
