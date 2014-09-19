package com.sybase365.mobiliser.web.btpn.application.pages;

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.btpnwow.core.security.facade.api.SecurityFacade;
import com.btpnwow.core.security.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.security.facade.contract.SendOtpRequest;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.forgotpassword.ConsumerPortalForgotPasswordPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerLoginOtpPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalChangeTemporaryPinPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;
import com.sybase365.mobiliser.web.util.PhoneNumber;

/**
 * This class is the login page for all Consumer Portal Application. This consists of the all the components related to
 * login and its functionality.
 * 
 * @author Vikram Gunda
 */
public class ConsumerPortalApplicationLoginPage extends BtpnBaseLoginPage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ConsumerPortalApplicationLoginPage.class);

	private String userName;

	private String password;

	private FeedbackPanel feedBackPanel;

	@SpringBean(name = "securityClientProvider")
	private SecurityFacade securityFacade;

	public ConsumerPortalApplicationLoginPage() {
		super();
		LOG.debug("Created new ConsumerPortalApplicationLoginPage");
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public ConsumerPortalApplicationLoginPage(final PageParameters parameters) {
		super(parameters);
		LOG.debug("Created new ConsumerPortalApplicationLoginPage");
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		LOG.debug("##AgentPortalApplicationLoginPage:initOwnPageComponents()===>Start");
		super.initOwnPageComponents();
		// Create Login Header
		createLoginHeader();
		// Create Login Form
		createLoginForm();
		// Create Login Footer
		createLoginFooter();
		LOG.debug("##AgentPortalApplicationLoginPage:initOwnPageComponents()===>Start");
	}

	/**
	 * This method should be used to create the login header.
	 */
	private void createLoginHeader() {
//		add(new Link<String>("loginLogoLink") {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void onClick() {
//				setResponsePage(ConsumerPortalApplicationLoginPage.class);
//			}
//		});
	}

	/**
	 * This method should be used to create the login forms and its components.
	 */
	private void createLoginForm() {
		final Form<ConsumerPortalApplicationLoginPage> loginForm = new Form<ConsumerPortalApplicationLoginPage>(
			"loginForm", new CompoundPropertyModel<ConsumerPortalApplicationLoginPage>(this));

		// Add userName, Domain, Password
		loginForm.add(new TextField<String>("userName").setRequired(true).add(new ErrorIndicator())
			.add(BtpnConstants.LOGIN_USER_NAME_MAX_LENGTH));
		loginForm.add(new PasswordTextField("password").add(new ErrorIndicator())
			.add(BtpnConstants.PASSWORD_MAX_LENGTH));

		Link<ConsumerPortalApplicationLoginPage> forgotPasswordLink = new Link<ConsumerPortalApplicationLoginPage>(
			"forgotPasswordLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(ConsumerPortalForgotPasswordPage.class);
			}
		};
		loginForm.add(forgotPasswordLink);

		// Add Submit button and onSubmit authenticate the user
		Button submitButton = new Button("submitButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				LOG.debug("ConsumerPortalApplicationLoginPage:createLoginForm():UserName ====> " + userName);
				LOG.debug("ConsumerPortalApplicationLoginPage:createLoginForm():Pin ====> XXXXXX");
				final PhoneNumber phoneNumber = new PhoneNumber(userName, ConsumerPortalApplicationLoginPage.this
					.getCustomerPortalPrefsConfig().getDefaultCountryCode());
				ConsumerPortalApplicationLoginPage.this.handleConsumerLogin(phoneNumber.getInternationalFormat(),
					password);
			}
		};
		// Add submitButton, ResetButton, feedbackMessagesPanel
		
		loginForm.add(submitButton);
		
//		loginForm.add(new Button("resetButton") {
//
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void onSubmit() {
//				LOG.debug("ConsumerPortalApplicationLoginPage:ResetForm ===> ");
//				loginForm.clearInput();
//				userName = "";
//			}
//
//		}.setDefaultFormProcessing(false));
		
		loginForm.add(new FeedbackPanel("feedbackMessagePanel"));
		
//		add(new Label("footer.about.btpn", getLocalizer().getString("footer.about.btpn", this)).setEscapeModelStrings(
//			false).setRenderBodyOnly(true));
		
		add(loginForm);
	}

	/**
	 * This method handles the bank staff login.
	 * 
	 * @param userName User name of the login
	 * @param password Password of the login
	 * @param selectedDomain Domain Selected for the login
	 */
	private void handleConsumerLogin(final String userName, final String pin) {
		try {
			LOG.debug("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> Start");
			final MobiliserWebSession session = this.getMobiliserWebSession();
			if (session.authenticateCustomer(userName, password, BtpnConstants.PRIV_BTPN_CONSUMER_LOGIN)) {
				LOG.debug("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> Login Success");
				
				MobiliserWebSession.setSessionTimeout(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionTimeout());
				
				setDefaultResponsePageIfNecessary();
			} else {
				error(getLocalizer().getString("login.failed", this));
			}
			LOG.debug("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> End");
		} catch (AuthenticationException e) {
			LOG.debug("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> Authentication exception", e);
			handleException(e);
		} catch (Exception e) {
			LOG.error("ConsumerPortalApplicationLoginPage:handleConsumerLogin() ====> Exception ", e);
			error(getLocalizer().getString("login.exception", this));
		}
	}

	/**
	 * Set a default response
	 */
	private void setDefaultResponsePageIfNecessary() {
		if (getMobiliserWebSession().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(BtpnConstants.PRIV_CHANGE_PASSWORD_EXPIRED))) {
			setResponsePage(ConsumerPortalChangeTemporaryPinPage.class);
		} else {
			if (Application.DEPLOYMENT.equals(getApplication().getConfigurationType())) {
				// send OTP
				CustomerIdentificationType cit = new CustomerIdentificationType();
				cit.setOrgUnitId("");
				cit.setType(1);
				cit.setValue(Long.toString(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
				
				SendOtpRequest request = MobiliserUtils.fill(new SendOtpRequest(), this);
				request.setFlags(0);
				request.setIdentification(cit);
				request.setReference(67825);
				request.setType(BtpnConstants.OTP_TYPE);
				
				try {
					securityFacade.sendOtp(request);
				} catch (Throwable ex) {
					LOG.warn("An exception was thrown by sendOtp() method.", ex);
				}
				
				// redirect to input otp page
				setResponsePage(ConsumerLoginOtpPage.class);
			} else {
				getMobiliserWebSession().getBtpnLoggedInCustomer().setOtpPrivilege(null);
				
				// redirect to home page
				setResponsePage(ConsumerPortalHomePage.class);
			}
		}
	}

	/**
	 * This method should be used to create the login footer.
	 */
	private void createLoginFooter() {
//		add(new Label("mobiliser.version", getBuildVersion()));
	}

	/**
	 * This method gets the user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * This method sets the user name
	 * 
	 * @param userName Username to be set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * This method gets the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * This method sets the Password
	 * 
	 * @param password Password to be set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public FeedbackPanel getFeedBackPanel() {
		return feedBackPanel;
	}

	public void setFeedBackPanel(FeedbackPanel feedBackPanel) {
		this.feedBackPanel = feedBackPanel;
	}
}
