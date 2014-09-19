package com.sybase365.mobiliser.web.consumer.pages.signup;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.application.pages.ApplicationLoginPage;

public class SignupCancelPage extends ConsumerSignupPage {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SignupCancelPage.class);

    public SignupCancelPage() {
	super();

	LOG.info("Created new SignupCancelPage");
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public SignupCancelPage(final PageParameters parameters) {
	super(parameters);

	LOG.info("Created new SignupCancelPage");
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	final Class loginPageClass = ApplicationLoginPage.class;
	final Page signupStartPage = new SignupStartPage();

	FeedbackPanel fbp = new FeedbackPanel("errorMessages");
	add(fbp);
	add(new WebMarkupContainer("cancelationMessage").setVisible(!fbp
		.anyErrorMessage()));

	@SuppressWarnings({ "rawtypes", "unchecked" })
	Form<?> form = new Form("restartForm",
		new CompoundPropertyModel<SignupCancelPage>(this));

	add(form);

	form.add(new Button("restartButton") {
	    @Override
	    public void onSubmit() {
		setResponsePage(signupStartPage);
	    }
	});

	form.add(new Button("cancel") {
	    @Override
	    public void onSubmit() {
		cleanupSession();
		setResponsePage(loginPageClass);
	    }
	});

    }
}
