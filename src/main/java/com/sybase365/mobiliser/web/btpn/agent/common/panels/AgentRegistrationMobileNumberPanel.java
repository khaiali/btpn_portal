package com.sybase365.mobiliser.web.btpn.agent.common.panels;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationResponse;
import com.sybase365.mobiliser.money.services.api.ICustomerEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration.AgentRegistrationPage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.common.panels.AttachmentsPanel;
import com.sybase365.mobiliser.web.util.PhoneNumber;


public class AgentRegistrationMobileNumberPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(AgentRegistrationMobileNumberPanel.class);

	protected BtpnMobiliserBasePage basePage;
	protected CustomerRegistrationBean customer;
	
	@SpringBean(name = "systemAuthCustomerClient")
	private ICustomerEndpoint customerEndpoint;

	public AgentRegistrationMobileNumberPanel(String id, BtpnMobiliserBasePage basePage,
		CustomerRegistrationBean customer) {
		this(id, basePage, null, customer);
	}

	public AgentRegistrationMobileNumberPanel(String id, BtpnMobiliserBasePage basePage,
		AttachmentsPanel attachmentsPanel, CustomerRegistrationBean customer) {
		super(id);
		this.basePage = basePage;
		this.customer = customer;
		log.debug("AgentRegistrationMobileNumberPanel Started");
		constructPanel();
	}

	/**
	 * This method constructs the panel.
	 */
	protected void constructPanel() {

		final Form<AgentRegistrationMobileNumberPanel> form = new Form<AgentRegistrationMobileNumberPanel>(
			"registrationMobileNumberForm", new CompoundPropertyModel<AgentRegistrationMobileNumberPanel>(this));

		final String customerType = customer.getCustomerType();
		// Legend Registration
		form.add(new Label("label.registration", getLocalizer().getString(customerType + ".label.registration", this)));
		// Mobile Number Label
		form.add(new Label("label.mobilenumber", getLocalizer().getString(customerType + ".label.mobilenumber", this)
				+ BtpnConstants.REQUIRED_SYMBOL).setEscapeModelStrings(false));
		// Error Messages Panel
		form.add(new FeedbackPanel("errorMessages"));

		// Mobile Number Text Field
//		final String mobileRegex = basePage.getAgentPortalPrefsConfig().getMobileRegex();
//		form.add(new TextField<String>("customer.mobileNumber").setRequired(true)
//			.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(new PatternValidator(mobileRegex))
//			.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH).add(new ErrorIndicator()));
		
		form.add(new TextField<String>("customer.mobileNumber").setRequired(true)
				.add(BtpnConstants.PHONE_NUMBER_VALIDATOR)
				.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH).add(new ErrorIndicator()));
		
		
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				AgentRegistrationMobileNumberPanel.this.handleFormSubmit();
			};
		});

		add(form);
	}
	
	
	protected void handleFormSubmit() {
		
		log.debug("AgentRegistrationMobileNumberPanel:handleFormSubmit() ==> Start");
		
		try {
			 
			final String msisdn = customer.getMobileNumber();
			
			final PhoneNumber phoneNumber = new PhoneNumber(msisdn, this.basePage.getBankPortalPrefsConfig().getDefaultCountryCode());
			
			GetCustomerByIdentificationRequest request = MobiliserUtils.fill(new GetCustomerByIdentificationRequest(), basePage);
			request.setIdentification(phoneNumber.getInternationalFormat());
			request.setIdentificationType(BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO);
			
			GetCustomerByIdentificationResponse response = customerEndpoint.getCustomerByIdentification(request);
			
			if (MobiliserUtils.success(response)) {
				if (response.getCustomer() == null) {
					setResponsePage(new AgentRegistrationPage(customer));
				} else {
					error(getLocalizer().getString("mobileNumber.already.exists", this));
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
			
		} catch (Exception e) {
			log.error("AgentRegistrationMobileNumberPanel:handleFormSubmit() ==> Exception occured while checking the msisdn", e);
			error(getLocalizer().getString("error.customer.exception", this));
		}
		log.debug("AgentRegistrationMobileNumberPanel:handleFormSubmit() ==> End");
	}
	
}
