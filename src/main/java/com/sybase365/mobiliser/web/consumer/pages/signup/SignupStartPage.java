package com.sybase365.mobiliser.web.consumer.pages.signup;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.util.Constants;

public class SignupStartPage extends ConsumerSignupPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SignupStartPage.class);

    public SignupStartPage() {
	super();

	LOG.info("Created new SignupStartPage");
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public SignupStartPage(final PageParameters parameters) {
	super(parameters);

	LOG.info("Created new SignupStartPage");
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	final CustomerBean customer = new CustomerBean();

	// initialize the page's form as the "base" component using a
	// compound model
	// the compound model shares it's content with the children of the
	// component allowing to create child components without an own model
	// in this case the model (a compound property) is the page itself
	// (this), which means wicket searches for properties which's names are
	// identical to the wicket IDs used, to populate the model (customer is
	// the only one for this page and is used to track all entries directly
	// into one bean, instead of having to copy each one separately)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	Form<?> form = new Form("msisdnForm",
		new CompoundPropertyModel<SignupStartPage>(this));

	// add the form's child components
	// add the msisdn text field. Since the form uses a compound property
	// model we do not have to explicitly add a model to the component
	// since the msisdn is obligatory, we use a RequiredTextField and add a
	// custom ErrorIndicator, which only adds a certain css class to the
	// rendered tag to indicate the error
	// additionally we add a pattern validator to ensure that there are only
	// digits

	// since we want error messages to be displayed, we have to include a
	// feedback panel
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new Link("continueMoney") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		customer.setCustomerTypeId(Integer.valueOf(getConfiguration()
			.getDefaultTypeIdForNewCustomer()));
		setResponsePage(new PersonalDataPage(customer));
	    }

	});

	form.add(new Link("continueMBanking") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick() {
		customer.setCustomerTypeId(Integer
			.valueOf(Constants.MBANKING_CUSTOMER_TYPE));
		setResponsePage(new PersonalDataPage(customer));
	    }
	});

	form.add(new Button("cancel") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(SignupCancelPage.class);
	    }

	});
	// last but not least add the form as a component of the page
	add(form);
    }

}
