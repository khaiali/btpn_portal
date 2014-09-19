package com.sybase365.mobiliser.web.btpn.application.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class BankPortalApplicationStartPage extends BtpnMobiliserBasePage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BankPortalApplicationStartPage.class);

	public BankPortalApplicationStartPage() {
		super();
		LOG.debug("Created new ApplicationStartPage");
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BankPortalApplicationStartPage(final PageParameters parameters) {
		super(parameters);
		LOG.debug("Created new ApplicationStartPage");
	}

	@Override
	protected void initOwnPageComponents() {
		if (getWebSession().isSignedIn()) {
			if (getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionTimeout() > 0) {
				MobiliserWebSession.setSessionTimeout(getMobiliserWebSession().getBtpnLoggedInCustomer()
					.getSessionTimeout());
			}
			final Roles roles = ((MobiliserWebSession) getSession()).getBtpnRoles();
			if (roles.hasRole(BtpnConstants.PRIV_BTPN_BANK_PORTAL_LOGIN)
					&& roles.hasRole(BtpnConstants.PRIV_UI_HOME_BANK_PORTAL)) {
				throw new RestartResponseAtInterceptPageException(BankPortalHomePage.class);
			}
			LOG.debug("BankPortalApplicationStartPage:Logged in user doesnt have priviliges to login ==> ",
				getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername());
			logoutCustomer(getMobiliserWebSession().getBtpnLoggedInCustomer().getSessionId());
			this.getWebSession().invalidate();
			BankPortalApplicationLoginPage loginPage = new BankPortalApplicationLoginPage();
			final String message = loginPage.getLocalizer().getString("login.error", loginPage);
			loginPage.getSession().error(message);				
			throw new RestartResponseException(loginPage);
		} else {
			throw new RestartResponseAtInterceptPageException(BankPortalApplicationLoginPage.class);
		}
	}
}
