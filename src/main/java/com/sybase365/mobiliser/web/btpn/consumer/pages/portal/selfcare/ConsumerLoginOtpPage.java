package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnBaseApplicationPage;
import com.sybase365.mobiliser.web.btpn.application.pages.ConsumerPortalApplicationLoginPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This class is the otp page BTPN Agent Portal Application. This consists of the all the components for the OTP.
 * 
 * @author Vikram Gunda
 */
@AuthorizeInstantiation(BtpnConstants.PRIV_BTPN_CONSUMER_LOGIN)
public class ConsumerLoginOtpPage extends BtpnBaseApplicationPage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConsumerLoginOtpPage.class);

	private String otp;

	/**
	 * Constructor for the Home Page
	 */
	public ConsumerLoginOtpPage() {
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		LOG.debug("AgentLoginOtpPage:initOwnPageComponents() ==> Start");
		super.initOwnPageComponents();
		addAgentOtpComponents();
		LOG.debug("AgentLoginOtpPage:initOwnPageComponents() ==> End");
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void addAgentOtpComponents() {
		final Customer customer = getMobiliserWebSession().getBtpnLoggedInCustomer();
		final StringBuilder otpHeaderInfo = new StringBuilder();

		otpHeaderInfo.append(customer.getDisplayName()).append("(").append(customer.getCustomerId()).append(")");

		// Add Login Header Information
		add(new Label("header.login.info", otpHeaderInfo.toString()));
		add(new Link<String>("header.logo") {

			private static final long serialVersionUID = 1L;

			public void onClick() {
				getWebSession().setLeftMenu(null);
				setResponsePage(ConsumerLoginOtpPage.class);
			}
		});
		Form<ConsumerLoginOtpPage> form = new Form<ConsumerLoginOtpPage>("otpForm",
			new CompoundPropertyModel<ConsumerLoginOtpPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new PasswordTextField("otp").add(new ErrorIndicator()).add(BtpnConstants.OTP_MAX_LENGTH));
		form.add(new Button("otpSubmit") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				ConsumerLoginOtpPage.this.handleOtpValidation();
			}
		});
		add(form);
		add(new Label("footer.about.btpn", getLocalizer().getString("footer.about.btpn", this)).setEscapeModelStrings(
			false).setRenderBodyOnly(true));
		add(new Label("application.session.timeout.seconds", String.valueOf(customer.getSessionTimeout())));
		add(new Link<String>("header.logout") {

			private static final long serialVersionUID = 1L;

			public void onClick() {
				getSession().invalidate();
				getRequestCycle().setRedirect(true);
				setResponsePage(ConsumerPortalApplicationLoginPage.class);
			}
		});
	}

	public void handleOtpValidation() {
		
		try {
			
			LOG.info(" ### (ConsumerLoginOtpPage::handleOtpValidation) OTP PIN ### " +otp);
			if (validateOTP(otp)) {
				// remove the PRIV_GENERATE_OTP_PRIVILEGE_MODE
				this.getMobiliserWebSession().getBtpnLoggedInCustomer().setOtpPrivilege(null);
				setResponsePage(ConsumerPortalHomePage.class);
			} else {
				error(getLocalizer().getString("otp.invalid", this));
			}
			
		} catch (Exception e) {
			error(getLocalizer().getString("otp.exception", this));
			LOG.error("###Exception occured while validating OTP", e);
		}
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

}
