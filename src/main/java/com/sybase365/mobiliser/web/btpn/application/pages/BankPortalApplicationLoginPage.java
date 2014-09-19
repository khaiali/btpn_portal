package com.sybase365.mobiliser.web.btpn.application.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

/**
 * This class is the login page for all Bank Portal Application. This consists of the all the components related to
 * login and its functionality.
 * 
 * @author Vikram Gunda
 */
public class BankPortalApplicationLoginPage extends BtpnBaseLoginPage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalApplicationLoginPage.class);

	private String userName;

	private String password;

	/**
	 * Constructor that is invoked when page is invoked.
	 */
	public BankPortalApplicationLoginPage() {
		super();
		LOG.debug("Created new BankPortalApplicationLoginPage");
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BankPortalApplicationLoginPage(final PageParameters parameters) {
		super(parameters);
		LOG.debug("Created new BankPortalApplicationLoginPage");
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		LOG.debug("##BankPortalApplicationLoginPage:initOwnPageComponents()===>Start");
		super.initOwnPageComponents();
		// Create Login Header
		createLoginHeader();
		// Create Login Form
		createLoginForm();
		// Create Login Footer
		createLoginFooter();
		LOG.debug("##BankPortalApplicationLoginPage:initOwnPageComponents()===>End");
	}

	/**
	 * This method should be used to create the login header.
	 */
	private void createLoginHeader() {
		add(new Link<String>("loginLogoLink") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(BankPortalApplicationLoginPage.class);
			}
		});
	}

	/**
	 * This method should be used to create the login forms and its components.
	 */
	private void createLoginForm() {
		final Form<BankPortalApplicationLoginPage> loginForm = new Form<BankPortalApplicationLoginPage>("loginForm",
			new CompoundPropertyModel<BankPortalApplicationLoginPage>(this));

		// Add userName, Domain, Password
		loginForm.add(new TextField<String>("userName").setRequired(true).add(BtpnConstants.LOGIN_USER_NAME_MAX_LENGTH)
			.add(new ErrorIndicator()));
		loginForm.add(new PasswordTextField("password").add(BtpnConstants.PASSWORD_MAX_LENGTH)
			.add(new ErrorIndicator()));

		// Add Submit button and onSubmit authenticate the user
		final Button submitButton = new Button("submitButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				LOG.debug("BankPortalApplicationLoginPage:createLoginForm():UserName ====> " + userName);
				BankPortalApplicationLoginPage.this.handleBankStaffLogin(userName, password);
			}

		};
		// Add submitButton, ResetButton, feedbackMessagesPanel
		loginForm.add(submitButton);
		loginForm.add(new Button("resetButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				LOG.debug("BankPortalApplicationLoginPage:ResetForm ===> ");
				loginForm.clearInput();
				userName = "";
			}

		}.setDefaultFormProcessing(false));
		loginForm.add(new FeedbackPanel("feedbackMessagePanel"));
		add(loginForm);
		add(new Label("footer.about.btpn", getLocalizer().getString("footer.about.btpn", this)).setEscapeModelStrings(
			false).setRenderBodyOnly(true));
	}

	/**
	 * This method handles the bank staff login.
	 * 
	 * @param userName User name of the login
	 * @param password Password of the login
	 */
	private void handleBankStaffLogin(final String userName, final String password) {
		try {
			LOG.debug("### BankPortalApplicationLoginPage::handleBankStaffLogin() ====> Start ###");
			
			final MobiliserWebSession session = this.getMobiliserWebSession();
			
			if (session.authenticateBankPortalUser(userName, password, null)) {
				LOG.debug("### BankPortalApplicationLoginPage::handleBankStaffLogin() ====> Login Success ###");
				MobiliserWebSession.setSessionTimeout(session.getBtpnLoggedInCustomer().getSessionTimeout());
				
				setDefaultResponsePageIfNecessary();
			} else {
				error(getLocalizer().getString("login.failed", this));
			}
			
			LOG.debug("### BankPortalApplicationLoginPage::handleBankStaffLogin() ====> End ###");
		} catch (AuthenticationException e) {
			LOG.debug("BankPortalApplicationLoginPage:handleBankStaffLogin() ====> Authentication exception", e);
			handleException(e);
		} catch (Exception e) {
			LOG.error("BankPortalApplicationLoginPage:handleBankStaffLogin() ====> Exception ", e);
			error(getLocalizer().getString("login.exception", this));
		}
	}

	/**
	 * This method should be used to create the login footer.
	 */
	private void createLoginFooter() {
		add(new Label("mobiliser.version", getBuildVersion()));
	}

	/**
	 * This method gets the user name.
	 * 
	 * @return String user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * This method sets the user name.
	 * 
	 * @param userName UserName to be set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * This method gets the password.
	 * 
	 * @return String password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * This method sets the Password.
	 * 
	 * @param password Password to be set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Set a default response
	 */
	private void setDefaultResponsePageIfNecessary() {
		LOG.info("### setDefaultResponsePageIfNecessary ###");
		setResponsePage(BankPortalHomePage.class);
	}
}
