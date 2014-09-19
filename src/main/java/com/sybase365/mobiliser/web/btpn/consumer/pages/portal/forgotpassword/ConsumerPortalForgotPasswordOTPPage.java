package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.forgotpassword;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.TemporaryPasswordRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.TemporaryPasswordResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ForgotPinBean;

/**
 * This class is the page which need to forgot password OTP.
 * 
 * @author Narasa Reddy
 */
public class ConsumerPortalForgotPasswordOTPPage extends BtpnMobiliserBasePage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ConsumerPortalForgotPasswordOTPPage.class);

	ForgotPinBean forgotPinBean;

	public ConsumerPortalForgotPasswordOTPPage() {
		super();
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 */
	public ConsumerPortalForgotPasswordOTPPage(final ForgotPinBean forgotPinBean) {
		super();
		this.forgotPinBean = forgotPinBean;
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
	 * This method should be used to create the forgot password OTP forms and its components.
	 */
	private void createForm() {
		Form<ConsumerPortalForgotPasswordOTPPage> form = new Form<ConsumerPortalForgotPasswordOTPPage>(
			"forgotPasswordOtpForm", new CompoundPropertyModel<ConsumerPortalForgotPasswordOTPPage>(this));

		// Add Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new PasswordTextField("forgotPinBean.passCode"));

		// Add submit button
		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleForgotPinRequest();
			}
		});
		add(form);
	}

	private void handleForgotPinRequest() {
		try {
			final TemporaryPasswordRequest request = getNewMobiliserRequest(TemporaryPasswordRequest.class);
			TemporaryPasswordResponse response = getSupportClient().temporaryPassword(
				createTemporaryPasswordRequest(request));
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				setResponsePage(new ConsumerPortalForgotPasswordFinishPage());
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception ex) {
			LOG.error("#An error occurred while calling temporaryPassword service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

	private TemporaryPasswordRequest createTemporaryPasswordRequest(TemporaryPasswordRequest request) {
		request.setCustomerId(forgotPinBean.getCustomerId());
		request.setMsisdn(forgotPinBean.getMsisdn());
		request.setEmail(forgotPinBean.getEmail());
		request.setOtp(forgotPinBean.getPassCode());
		return request;
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
			message = getLocalizer().getString("forgotpin.fail", this);
		}
		error(message);
	}
}
