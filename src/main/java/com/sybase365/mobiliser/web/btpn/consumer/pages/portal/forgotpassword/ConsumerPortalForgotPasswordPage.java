package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.forgotpassword;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.ForgotpinRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.ForgotpinResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ForgotPinBean;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This class is the page which need to forgot password.
 * 
 * @author Narasa Reddy
 */
public class ConsumerPortalForgotPasswordPage extends BtpnMobiliserBasePage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ConsumerPortalForgotPasswordPage.class);

	ForgotPinBean forgotPinBean;

	public ConsumerPortalForgotPasswordPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		createLoginHeader();
		createForm();
	}

	private void createLoginHeader() {
		add(new Link<String>("loginLogoLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(ConsumerPortalApplicationLoginPage.class);
			}
		});
	}

	/**
	 * This method should be used to create the forgot password forms and the components.
	 */
	private void createForm() {
		Form<ConsumerPortalForgotPasswordPage> form = new Form<ConsumerPortalForgotPasswordPage>("forgotPasswordForm",
			new CompoundPropertyModel<ConsumerPortalForgotPasswordPage>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new TextField<String>("forgotPinBean.msisdn").setRequired(true)
			.add(new PatternValidator(getBankPortalPrefsConfig().getMobileRegex()))
			.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH)
			.add(new ErrorIndicator()));

		form.add(new TextField<String>("forgotPinBean.email").setRequired(true)
			.add(new PatternValidator(BtpnConstants.EMAIL_ID_REGEX)).add(BtpnConstants.EMAIL_ID_MAX_LENGTH)
			.add(new ErrorIndicator()));

		// Add submit button
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (!PortalUtils.exists(forgotPinBean)) {
					forgotPinBean = new ForgotPinBean();
				}
				handleForgotPinRequest();
			}
		});

		// Add cancel Button
		form.add(new Button("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ConsumerPortalApplicationLoginPage.class);
			}
		}.setDefaultFormProcessing(false));

		add(form);
	}

	private void handleForgotPinRequest() {
		try {
			forgotPinBean.setMsisdn(formateMsisdn(forgotPinBean.getMsisdn()));
			final ForgotpinRequest request = getNewMobiliserRequest(ForgotpinRequest.class);
			request.setMsisdn(forgotPinBean.getMsisdn());
			request.setEmail(forgotPinBean.getEmail());
			ForgotpinResponse response = getSupportClient().forgotPin(request);
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				setResponsePage(new ConsumerPortalForgotPasswordOTPPage(prepareResponse(response)));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling forgotPin service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private ForgotPinBean prepareResponse(ForgotpinResponse response) {
		forgotPinBean.setCustomerId(response.getCustomerId());
		forgotPinBean.setEmail(response.getEmail());
		forgotPinBean.setMsisdn(response.getMsisdn());
		forgotPinBean.setOtp(response.getOtp());
		return forgotPinBean;
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generic error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("no.user.found", this);
		}
		error(message);
	}

	private String formateMsisdn(String msisdn) {
		final PhoneNumber phoneNumber = new PhoneNumber(msisdn, getAgentPortalPrefsConfig().getDefaultCountryCode());
		return phoneNumber.getInternationalFormat();
	}

}
