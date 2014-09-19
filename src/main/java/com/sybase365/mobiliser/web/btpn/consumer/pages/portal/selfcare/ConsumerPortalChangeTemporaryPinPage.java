package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import java.util.LinkedList;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;

import com.btpnwow.core.security.facade.api.SecurityFacade;
import com.btpnwow.core.security.facade.contract.CustomerCredentialType;
import com.btpnwow.core.security.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.security.facade.contract.SetCredentialRequest;
import com.btpnwow.core.security.facade.contract.SetCredentialResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.IMenuEntry;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.BtpnBaseConsumerPortalPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

/**
 * This class is the Change Temporary pin page for consumer portal. When the pin expires or user wants to change
 * temporary pin(created through forgot pin page) he is redirect to this page during login. Once he changes his
 * temporary pin he will be redirected to login OTP Page.
 * 
 * @author Vikram Gunda
 */
public class ConsumerPortalChangeTemporaryPinPage extends BtpnBaseConsumerPortalPage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "securityClientProvider")
	private SecurityFacade securityFacade;
	
	private String newPin;

	private String confirmNewPin;

	private static final Logger LOG = LoggerFactory.getLogger(ConsumerPortalChangeTemporaryPinPage.class);

	/**
	 * Constructor for the Home Page.
	 */
	public ConsumerPortalChangeTemporaryPinPage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		constructPage();

	}

	/**
	 * Construct the page components.
	 */
	private void constructPage() {
		final Form<ConsumerPortalChangeTemporaryPinPage> form = new Form<ConsumerPortalChangeTemporaryPinPage>(
			"temporaryPinForm", new CompoundPropertyModel<ConsumerPortalChangeTemporaryPinPage>(this));
		
		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));
		
		PasswordTextField newPin, confirmNewPin;
		
		// Add New pin
		form.add((newPin = new PasswordTextField("newPin"))
				.add(new PatternValidator(BtpnConstants.PIN_REGEX))
				.setRequired(true)
				.add(new ErrorIndicator()));
		
		// Add Confirm New pin
		form.add((confirmNewPin = new PasswordTextField("confirmNewPin"))
				.add(new PatternValidator(BtpnConstants.PIN_REGEX))
				.setRequired(true)
				.add(new ErrorIndicator()));
		
		form.add(new EqualPasswordInputValidator(newPin, confirmNewPin));
		
		form.add(new Button("btnSubmit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleChangePIN();
			};
		});
		
		add(form);

	}

	/**
	 * This method is used to handle temporary change pin
	 * 
	 * @param oldPin oldPin of the user.
	 */
	private void handleChangePIN() {
		try {
			CustomerIdentificationType ident = new CustomerIdentificationType();
			ident.setValue(Long.toString(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
			ident.setType(1);
			
			CustomerCredentialType cred = new CustomerCredentialType();
			cred.setValue(newPin);
			cred.setType(BtpnConstants.CREDENTIAL_TYPE_PIN);
			
			SetCredentialRequest request = new SetCredentialRequest();
			request.setIdentification(ident);
			request.setCredential(cred);
			
			SetCredentialResponse response = securityFacade.setCredential(request);
			
			if (MobiliserUtils.success(response)) {
				this.getMobiliserWebSession().info(getLocalizer().getString("success.change.pin", this));
				
				// handleConsumerLogin(newPin);
				
				setResponsePage(ConsumerPortalApplicationLoginPage.class);
			} else {
				handleSpecificErrorMessage(MobiliserUtils.errorCode(response));
			}
		} catch (Exception e) {
			this.getMobiliserWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while ConsumerPortalChangeExpiredPinPage  ===> ", e);
		}
	}

	/**
	 * This method handles the bank staff login.
	 * 
	 * @param userName User name of the login
	 * @param password Password of the login
	 * @param selectedDomain Domain Selected for the login
	 */
	private void handleConsumerLogin(final String pin) {
		try {
			LOG.debug("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> Start");
			final MobiliserWebSession session = this.getMobiliserWebSession();
			if (session.authenticateCustomer(session.getBtpnLoggedInCustomer().getUsername(), pin, BtpnConstants.PRIV_BTPN_CONSUMER_LOGIN)) {
				LOG.debug("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> Login Success");
				
				setResponsePage(ConsumerLoginOtpPage.class);
			} else {
				this.getMobiliserWebSession().error(getLocalizer().getString("login.failed", this));
			}
			LOG.debug("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> End");
		} catch (CredentialsExpiredException e) {
			setResponsePage(ConsumerPortalChangeTemporaryPinPage.class);
		} catch (AuthenticationException e) {
			LOG.debug("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> Authentication exception", e);
			this.getMobiliserWebSession().error(getLocalizer().getString("login.failed", this));
		} catch (Exception e) {
			LOG.error("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> Exception ", e);
			this.getMobiliserWebSession().error(getLocalizer().getString("login.exception", this));
		}
	}

	@Override
	protected LinkedList<IMenuEntry> buildLeftMenu() {
		return new LinkedList<IMenuEntry>();
	}

	@Override
	protected Class handleLogoClick() {
		return ConsumerPortalChangeTemporaryPinPage.class;
	}

	protected boolean getVisible() {
		return false;
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
			message = getLocalizer().getString("error.temporary.pin", this);
		}
		error(message);
	}
}
