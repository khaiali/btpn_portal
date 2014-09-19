package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationResponse;
import com.sybase365.mobiliser.money.services.api.ICustomerEndpoint;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.ConsumerTopAgentRegistrationPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PhoneNumber;

/**
 * This is the RegistrationMobileNumberPanel page for bank portals and checks whether msisdn is already registered or
 * not.
 * 
 * @author Vikram Gunda
 */
public class RegistrationMobileNumberPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationMobileNumberPanel.class);

	@SpringBean(name = "systemAuthCustomerClient")
	private ICustomerEndpoint customerEndpoint;
	
	private BtpnMobiliserBasePage basePage;

	private CustomerRegistrationBean customer;

	/**
	 * Constructor for this page.
	 * 
	 * @param id id Of the Panel
	 * @param basePage basePage of the Panel
	 * @param customer customer Object
	 */
	public RegistrationMobileNumberPanel(String id, BtpnMobiliserBasePage basePage, CustomerRegistrationBean customer) {
		super(id);
		
		this.basePage = basePage;
		this.customer = customer;
		
		LOG.debug("ConsumerRegistrationPanel Started");
		
		constructPanel();
	}

	/**
	 * This method constructs the panel.
	 */
	protected void constructPanel() {
		final Form<RegistrationMobileNumberPanel> form = new Form<RegistrationMobileNumberPanel>(
			"registrationMobileNumberForm", new CompoundPropertyModel<RegistrationMobileNumberPanel>(this));

		// Error Messages Panel
		form.add(new FeedbackPanel("errorMessages"));
		
//		final String customerType = customer.getCustomerType();

		// Mobile Number Label
//		form.add(new Label("label.mobilenumber", getLocalizer().getString(customerType + ".label.mobilenumber", this)
//				+ BtpnConstants.REQUIRED_SYMBOL).setEscapeModelStrings(false));
		
		final String mobileRegex = basePage.getBankPortalPrefsConfig().getMobileRegex();
		
		// Mobile Number Text Field
		form.add(new TextField<String>("customer.mobileNumber")
				.setRequired(true)
				.add(BtpnConstants.PHONE_NUMBER_VALIDATOR)
				.add(new PatternValidator(mobileRegex))
				.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
				.add(new ErrorIndicator()));
		
		form.add(new Button("submitButton") {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				RegistrationMobileNumberPanel.this.handleFormSubmit();
			};
		});

		add(form);
	}

	/**
	 * This method handles the form submit and checks whether msisdn is already registered or not.
	 */
	protected void handleFormSubmit() {
		try {
			final String msisdn = customer.getMobileNumber();
			
			final PhoneNumber phoneNumber = new PhoneNumber(msisdn, this.basePage.getBankPortalPrefsConfig().getDefaultCountryCode());
			
			GetCustomerByIdentificationRequest request = MobiliserUtils.fill(new GetCustomerByIdentificationRequest(), basePage);
			request.setIdentification(phoneNumber.getInternationalFormat());
			request.setIdentificationType(BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO);
			
			GetCustomerByIdentificationResponse response = customerEndpoint.getCustomerByIdentification(request);
			
			if (MobiliserUtils.success(response)) {
				if (response.getCustomer() == null) {
					setResponsePage(new ConsumerTopAgentRegistrationPage(customer));
				} else {
					error(getLocalizer().getString("mobileNumber.already.exists", this));
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Throwable ex) {
			LOG.error("An exception was thrown", ex);
			
			error(getLocalizer().getString("error.customer.exception", this));
		}
	}
}
