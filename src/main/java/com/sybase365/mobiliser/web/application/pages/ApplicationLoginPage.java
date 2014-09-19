package com.sybase365.mobiliser.web.application.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import com.sybase365.mobiliser.util.tools.wicketutils.security.LoginForm;
import com.sybase365.mobiliser.web.application.MobiliserApplication;
import com.sybase365.mobiliser.web.application.model.IMobiliserSignupApplication;

public class ApplicationLoginPage extends BaseLoginPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ApplicationLoginPage.class);

    public ApplicationLoginPage() {
	super();
	LOG.debug("Created new ApplicationLoginPage");
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public ApplicationLoginPage(final PageParameters parameters) {
	super(parameters);
	LOG.debug("Created new ApplicationLoginPage");
    }

    /**
     * @see com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage#initOwnComponents
    @Override
     */
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	Form<?> form = new LoginForm("loginForm");

	// since we want error messages to be displayed, we have to include a
	// feedback panel
	form.add(new FeedbackPanel("errorMessages"));

	// look for a signup application in the list of signup apps
	// future support may allow adding all signup apps to the page (hence
	// signup apps is a list) but for now, assume we have just one
	boolean hasSignupApp = false;
	Class signupAppHomePage = ApplicationLoginPage.class;

	for (IMobiliserSignupApplication signupApp : 
		((MobiliserApplication)getApplication()).getSignupApplications()) {
	    hasSignupApp = Boolean.TRUE;
	    signupAppHomePage = signupApp.getHomePage();
	}

	final Class signupHomePage = signupAppHomePage;

	form.add(new Link("consumerSignup") {
	    @Override
	    public void onClick() {
		setResponsePage(signupHomePage);
	    }
	}.setVisible(hasSignupApp));

	form.add(new Link("forgotPassword") {
	    @Override
	    public void onClick() {
		setResponsePage(ForgotPasswordPage.class);
	    }
	}.setVisible(false));

	// last but not least add the form as a component of the page
	add(form);

	add(new Label("sessionRefresh", new Model(""))
		.setOutputMarkupId(true)
		.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(60))));
    }

}
