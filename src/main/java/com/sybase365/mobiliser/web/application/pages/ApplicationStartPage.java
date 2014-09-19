package com.sybase365.mobiliser.web.application.pages;

import java.util.List;
import java.util.Set;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.strategies.role.Roles;

import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.web.application.MobiliserApplication;
import com.sybase365.mobiliser.web.application.model.IMobiliserAuthenticatedApplication;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

public class ApplicationStartPage extends MobiliserBasePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ApplicationStartPage.class);

    public ApplicationStartPage() {
	super();
	LOG.debug("Created new ApplicationStartPage");
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public ApplicationStartPage(final PageParameters parameters) {
	super(parameters);
	LOG.debug("Created new ApplicationStartPage");
    }

    @Override
    protected void initOwnPageComponents() {

	if (getWebSession().isSignedIn()) {

	    if (getMobiliserWebSession().getLoggedInCustomer()
		    .getSessionTimeout() > 0) {
		MobiliserWebSession.setSessionTimeout(getMobiliserWebSession()
			.getLoggedInCustomer().getSessionTimeout());
	    }

	    final Roles roles = ((BaseWebSession) getSession()).getRoles();

	    final Set<String> rolesRequiringSelfAuth = ((MobiliserApplication) getApplication())
		    .getOwnAuthenticationRequired();

	    if (rolesRequiringSelfAuth != null
		    && !rolesRequiringSelfAuth.isEmpty()) {

		boolean selfAuthRequired = false;
		for (final String role : rolesRequiringSelfAuth) {
		    if (roles.hasRole(role)) {
			selfAuthRequired = true;
		    }
		}

		if (selfAuthRequired
			&& getSession() instanceof MobiliserWebSession) {
		    ((MobiliserWebSession) getSession())
			    .setSelfAuthenticationRequired(true);
		}
	    }

	    List<IMobiliserAuthenticatedApplication> knownApps = ((MobiliserApplication) getApplication())
		    .getAuthenticatedApplications();

	    for (IMobiliserAuthenticatedApplication app : knownApps) {

		LOG.debug("Check if signed-in user has privilege {}",
			app.getLoginPrivilege());

		if (roles.hasRole(app.getLoginPrivilege())) {

		    LOG.debug(
			    "Login has privilege {} -> restart response at {}",
			    app.getLoginPrivilege(), app.getHomePage()
				    .getSimpleName());

		    throw new RestartResponseAtInterceptPageException(
			    app.getHomePage());
		}
	    }

	    // check for temporarily assigned roles specific to web ui
	    if (roles.hasRole("CHANGE_TEMPORARY_PASSWORD")) {
		LOG.debug("Login has limited access privilege CHANGE_TEMPORARY_PASSWORD -> restart response at ChangeTempPinPage");
		throw new RestartResponseAtInterceptPageException(
			ChangeTempPinPage.class);
	    } else if (roles.hasRole("CHANGE_EXPIRED_PASSWORD")) {
		LOG.debug("Login has limited access privilege CHANGE_EXPIRED_PASSWORD -> restart response at ChangeExpiredPasswordPage");
		throw new RestartResponseAtInterceptPageException(
			ChangeExpiredPasswordPage.class);
	    }

	    Class loginPageClass = getPage(ApplicationLoginPage.class);

	    // otherwise no matching known role for installed apps - invalidate
	    // session, show and error and revert to login page again
	    LOG.info(
		    "Login has no privileges matching known applications -> restart response at {}",
		    loginPageClass.getSimpleName());

	    // message sent back to user must NOT indicate any information
	    // that shows username and password where either known or successful
	    // -
	    // it could be that an externally facing login could then be used
	    // to try to validate internal-only logins - if we sent back a msg
	    // saying login matched to no privileges, we are implying the
	    // username exists and the password is valid
	    error(getLocalizer().getString("application.login.failed", this));

	    // invalidate the session to ensure that the logged in user doesn't
	    // continue; use invalidate to do this as it allows the error msg
	    // to be displayed to the user (invalidateNow() and signOut() would
	    // also clear the logged in session, but they would also clear down
	    // the message, giving an implication that this login was also
	    // different). The downside of using invalidate() is that the next
	    // attempted login would not work (fails with no msg) because the
	    // login page session is invalid and only another redirect will
	    // create
	    // a new one.
	    this.getWebSession().invalidate();

	    throw new RestartResponseAtInterceptPageException(loginPageClass);
	} else {
	    Class loginPageClass = getPage(ApplicationLoginPage.class);

	    LOG.debug("Not logged in -> restart response at {}",
		    loginPageClass.getSimpleName());
	    throw new RestartResponseAtInterceptPageException(loginPageClass);
	}
    }
}
