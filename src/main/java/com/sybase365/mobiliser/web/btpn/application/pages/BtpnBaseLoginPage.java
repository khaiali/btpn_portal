package com.sybase365.mobiliser.web.btpn.application.pages;

import org.apache.wicket.PageParameters;
import org.springframework.security.core.AuthenticationException;

import com.sybase365.mobiliser.web.btpn.util.ExceptionInfo;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This class is the page which need to have all the common components that the BTPN application login pages need.
 * 
 * @author Vikram Gunda
 */
public class BtpnBaseLoginPage extends BtpnMobiliserBasePage {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BtpnBaseLoginPage.class);

	/**
	 * Constructor that is invoked.
	 */
	public BtpnBaseLoginPage() {
		super();
		LOG.debug("Created new BaseBtpnLoginPage");
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters Page parameters
	 */
	public BtpnBaseLoginPage(final PageParameters parameters) {
		super(parameters);
		LOG.debug("Created new BaseBtpnLoginPage");
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
	}

	/**
	 * This method handles the bank staff login exception
	 */
	@SuppressWarnings("deprecation")
	protected void handleException(AuthenticationException e) {
		// Display error message for specific error code
		if (e.getExtraInformation() != null) {
			final ExceptionInfo info = (ExceptionInfo) e.getExtraInformation();
			final String errorKey = "login.failed." + info.getErrorCode();
			final String errorMessage = getLocalizer().getString(errorKey, this);
			if (!errorMessage.equals(errorKey)) {
				final String attemptsLeft = info.getAttemptsLeft();
				final int attemptsLeftInt = PortalUtils.exists(attemptsLeft) ? Integer.valueOf(attemptsLeft) : 0;
				switch (attemptsLeftInt) {
				case 0:
					error(errorMessage);
					return;
				case 1:
					error(getLocalizer().getString(errorKey + ".1", this));
					return;
				default:
					error(String.format(errorMessage, attemptsLeft));
					return;
				}
			}
		}
		// If no specific error message display generic message
		error(getLocalizer().getString("login.failed", this));
	}
}
