package com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.panels.BankCheckerConfirmApprovalPanel;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the ConsumerApprovalPage page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class ConsumerTopAgentConfirmApprovalPage extends BtpnBaseBankPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private CustomerRegistrationBean bean;

	public ConsumerTopAgentConfirmApprovalPage(final CustomerRegistrationBean bean) {
		super();
		this.bean = bean;
		initPageComponents();
	}

	public ConsumerTopAgentConfirmApprovalPage(final PageParameters parameters) {
		super(parameters);
	}

	protected void initPageComponents() {
		add(new BankCheckerConfirmApprovalPanel("ConsumerTopAgentConfirmApprovalPage", bean, this));
	}

}
