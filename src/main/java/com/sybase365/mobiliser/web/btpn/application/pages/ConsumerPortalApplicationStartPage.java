package com.sybase365.mobiliser.web.btpn.application.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerLoginOtpPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalChangeTemporaryPinPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class ConsumerPortalApplicationStartPage extends BtpnMobiliserBasePage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
		.getLogger(ConsumerPortalApplicationStartPage.class);

	public ConsumerPortalApplicationStartPage() {
		super();
		LOG.debug("Created new ConsumerPortalApplicationStartPage");
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public ConsumerPortalApplicationStartPage(final PageParameters parameters) {
		super(parameters);
		LOG.debug("Created new ConsumerPortalApplicationStartPage");
	}

	@Override
	protected void initOwnPageComponents() {
		if (getWebSession().isSignedIn()) {
			BtpnCustomer btpnCust = getMobiliserWebSession().getBtpnLoggedInCustomer();
			if (btpnCust.getSessionTimeout() > 0) {
				MobiliserWebSession.setSessionTimeout(getMobiliserWebSession().getBtpnLoggedInCustomer()
					.getSessionTimeout());
			}
			final Roles roles = ((MobiliserWebSession) getSession()).getBtpnRoles();
			if (roles.hasRole(BtpnConstants.PRIV_CHANGE_PASSWORD_EXPIRED)) {
				throw new RestartResponseAtInterceptPageException(ConsumerPortalChangeTemporaryPinPage.class);
			}
			if (btpnCust.getOtpPrivilege() != null
					&& btpnCust.getOtpPrivilege().equals(BtpnConstants.PRIV_GENERATE_OTP_PRIVILEGE_MODE)) {
				throw new RestartResponseAtInterceptPageException(ConsumerLoginOtpPage.class);
			}
			if (roles.hasRole(BtpnConstants.PRIV_BTPN_CONSUMER_LOGIN)
					&& roles.hasRole(BtpnConstants.PRIV_HOME_CONSUMER_PORTAL)) {
				throw new RestartResponseAtInterceptPageException(ConsumerPortalHomePage.class);
			}
			LOG.debug("AgentPortalApplicationStartPage:Logged in user doesnt have priviliges to login ==> ",
				getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
			logoutCustomer(getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
			this.getWebSession().invalidate();
			final ConsumerPortalApplicationLoginPage loginPage = new ConsumerPortalApplicationLoginPage();
			final String message = loginPage.getLocalizer().getString("login.error", loginPage);
			loginPage.getSession().error(message);
			throw new RestartResponseException(loginPage);
		} else {
			throw new RestartResponseAtInterceptPageException(ConsumerPortalApplicationLoginPage.class);
		}
	}
}
