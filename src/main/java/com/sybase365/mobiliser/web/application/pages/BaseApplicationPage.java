package com.sybase365.mobiliser.web.application.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenu;
import com.sybase365.mobiliser.util.tools.wicketutils.menu.SybaseMenuView;
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.application.MobiliserApplication;
import com.sybase365.mobiliser.web.cst.pages.customercare.ContactNotePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;
import com.sybase365.mobiliser.web.util.PortalUtils;

public abstract class BaseApplicationPage extends MobiliserBasePage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(BaseApplicationPage.class);

    public BaseApplicationPage() {
	super();
    }

    public BaseApplicationPage(final PageParameters parameters) {
	super(parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	add(new Label("applicationName", getApplicationName()));

	add(new Label("applicationAboutName", MobiliserApplication.VERSION.NAME));
	add(new Label("applicationAboutVersion",
		MobiliserApplication.VERSION.VERSION));
	add(new Label("applicationAboutDate", MobiliserApplication.VERSION.DATE));
	add(new Label("applicationAboutTag", MobiliserApplication.VERSION.TAG));
	add(new Label("applicationAboutRevision",
		MobiliserApplication.VERSION.REVISION));

	add(new SybaseMenuView("menu", new Model<SybaseMenu>(getWebSession()
		.getMenu())));

	if (this.supportsSvaBalance()) {
	    add(new Label("svaBalance", getBalance(wsSystemAuthWalletClient)));
	} else {
	    add(new Label("svaBalance", ""));
	}
	add(new Label("loginDetails", getLoginDetails()));

	Link logoutLink = new Link("logout") {
	    @Override
	    public void onClick() {
		if (getMobiliserWebSession().isShowContact()
			&& getMobiliserWebSession().hasPrivilege(
				Constants.PRIV_NOTE_READ)) {

		    setResponsePage(new ContactNotePage(null));

		} else {
		    logoutCustomer(getWebSession().getLoggedInCustomer()
			    .getSessionId());
		    getSession().invalidate();
		    getRequestCycle().setRedirect(true);
		    setResponsePage(getApplication().getHomePage());
		}

	    }
	};

	if (!getWebSession().isSignedIn()) {
	    logoutLink.setVisible(false);
	}

	add(logoutLink);

	add(new Link("changeToEnglish") {
	    @Override
	    public void onClick() {
		getWebSession().setLocale(getUpdatedLocale("en_US"));
		getSession().info(
			getLocalizer().getString("language.change.success",
				this));
		setResponsePage(getApplication().getHomePage());
	    }
	});

	add(new Link("changeToGerman") {
	    @Override
	    public void onClick() {
		getWebSession().setLocale(getUpdatedLocale("de_DE"));
		getSession().info(
			getLocalizer().getString("language.change.success",
				this));
		setResponsePage(getApplication().getHomePage());
	    }
	});

	add(new Label("application.session.timeout.seconds", new Model(
		MobiliserWebSession.getSessionTimeout())));
    }

    protected String getLoginDetails() {

	if (getWebSession().isSignedIn()) {

	    StringBuilder displayName = new StringBuilder();
	    Customer loggedInCustomer = getWebSession().getLoggedInCustomer();

	    // favour display name to show as logged in user
	    if (PortalUtils.exists(loggedInCustomer.getDisplayName())) {
		displayName.append(loggedInCustomer.getDisplayName());
	    }
	    // but it is optional, so might not be set, in which case fallback
	    // to username
	    else if (PortalUtils.exists(loggedInCustomer.getUsername())) {
		displayName.append(loggedInCustomer.getUsername());
	    }

	    displayName.append(" (").append(loggedInCustomer.getCustomerId())
		    .append(")");

	    return displayName.toString();
	}
	return "";
    }

    public void refreshSVABalance() {
	if (this.supportsSvaBalance()) {
	    addOrReplace(new Label("svaBalance", getBalance()));
	}

    }

    protected abstract String getApplicationName();

    protected abstract Class getActiveMenu();

    protected abstract boolean supportsSvaBalance();

}
