package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentPortalHomePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This is the Confirm Registration Panel for Consumers, Agents in bank portal and Child Agents, Sub agents in agent
 * portal.
 * 
 * @author Vikram Gunda
 */
public class RegistrationSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected CustomerRegistrationBean customer;

	protected BtpnMobiliserBasePage mobBasePage;

	public RegistrationSuccessPanel(String id, CustomerRegistrationBean customer, BtpnMobiliserBasePage mobBasePage) {
		super(id);
		this.customer = customer;
		this.mobBasePage = mobBasePage;
		constructPanel();
	}

	protected void constructPanel() {
		Form<RegistrationSuccessPanel> form = new Form<RegistrationSuccessPanel>("registrationSuccessForm",
			new CompoundPropertyModel<RegistrationSuccessPanel>(this));

		add(new FeedbackPanel("errorMessages"));
		form.add(new Label("customer.message").setRenderBodyOnly(true));
		form.add(new Button("backButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				final String customerType = customer.getCustomerType();
				final boolean agentPortal = customerType.equals(BtpnConstants.REG_SUB_AGENT)
						|| customerType.equals(BtpnConstants.REG_CHILD_AGENT);
				if (agentPortal) {
					mobBasePage.handleCancelButtonRedirectToHomePage(AgentPortalHomePage.class);
				} else {
					mobBasePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
				}
			};
		}.setDefaultFormProcessing(false));

		add(form);
	}
}
