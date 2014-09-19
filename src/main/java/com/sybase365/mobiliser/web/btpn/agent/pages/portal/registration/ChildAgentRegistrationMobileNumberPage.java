package com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration;


import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.btpn.agent.common.panels.AgentRegistrationMobileNumberPanel;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;


public class ChildAgentRegistrationMobileNumberPage extends BtpnBaseAgentPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	BtpnBaseBankPortalSelfCarePage basePage;
	CustomerRegistrationBean customer;

	public ChildAgentRegistrationMobileNumberPage() {
		super();
		initPageComponents();
	}

	public ChildAgentRegistrationMobileNumberPage(CustomerRegistrationBean customer) {
		super();
		this.customer = customer;
		initPageComponents();
	}

	public ChildAgentRegistrationMobileNumberPage(final PageParameters parameters) {
		super(parameters);
	}

	protected void initPageComponents() {
		initilaizeCustomerBean();
		add(new AgentRegistrationMobileNumberPanel("registrationMobileNumberPanel", this, this.customer));
	}

	private void initilaizeCustomerBean() {
		
		customer = new CustomerRegistrationBean();
		// set default registration type as Consumer
		customer.setRegistrationType(new CodeValue(getLocalizer().getString("registrationType.default.key", this),
			getLocalizer().getString("registrationType.default.value", this)));
		// set default nationality
		customer.setNationality(new CodeValue(getLocalizer().getString("nationality.default.key", this), getLocalizer()
			.getString("nationality.default.value", this)));
		// set default Reciept Mode
		customer.setReceiptMode(new CodeValue(getLocalizer().getString("recieptmode.default.key", this), getLocalizer()
			.getString("recieptmode.default.value", this)));
		customer.setOptimaActivated(new CodeValue(getLocalizer().getString("optimaactivated.default.key", this),
			getLocalizer().getString("optimaactivated.default.value", this)));
		customer.setCustomerType(BtpnConstants.REG_CHILD_AGENT);
	}

}
