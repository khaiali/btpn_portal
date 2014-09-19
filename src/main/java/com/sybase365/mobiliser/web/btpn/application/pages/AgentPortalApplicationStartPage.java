package com.sybase365.mobiliser.web.btpn.application.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentLoginOtpPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class AgentPortalApplicationStartPage extends BtpnMobiliserBasePage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
		.getLogger(AgentPortalApplicationStartPage.class);

	public AgentPortalApplicationStartPage() {
		super();
		LOG.debug("Created new AgentPortalApplicationStartPage");
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public AgentPortalApplicationStartPage(final PageParameters parameters) {
		super(parameters);
		LOG.debug("Created new AgentPortalApplicationStartPage");
	}

	@Override
	protected void initOwnPageComponents() {
		if (getWebSession().isSignedIn()) {
			BtpnCustomer btpnCust = getMobiliserWebSession().getBtpnLoggedInCustomer();
			if (getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionTimeout() > 0) {
				MobiliserWebSession.setSessionTimeout(getMobiliserWebSession().getBtpnLoggedInCustomer()
					.getSessionTimeout());
			}
			final Roles roles = ((MobiliserWebSession) getSession()).getBtpnRoles();
			if (btpnCust.getOtpPrivilege() != null
					&& btpnCust.getOtpPrivilege().equals(BtpnConstants.PRIV_GENERATE_OTP_PRIVILEGE_MODE)) {
				throw new RestartResponseAtInterceptPageException(AgentLoginOtpPage.class);
			}
			if (roles.hasRole(BtpnConstants.PRIV_BTPN_AGENT_LOGIN)
					&& roles.hasRole(BtpnConstants.PRIV_HOME_AGENT_PORTAL)) {
				throw new RestartResponseAtInterceptPageException(AgentPortalHomePage.class);
			}
			LOG.debug("AgentPortalApplicationStartPage:Logged in user doesnt have priviliges to login ==> ",
				getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
			logoutCustomer(getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
			this.getWebSession().invalidate();
			final AgentPortalApplicationLoginPage loginPage = new AgentPortalApplicationLoginPage();
			final String message = loginPage.getLocalizer().getString("login.error", loginPage);
			loginPage.getSession().error(message);
			throw new RestartResponseException(loginPage);
		} else {
			throw new RestartResponseAtInterceptPageException(AgentPortalApplicationLoginPage.class);
		}
	}
}
