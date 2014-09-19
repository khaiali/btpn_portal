package com.sybase365.mobiliser.web.checkout.pages;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

public class ErrorPage extends BaseCheckoutPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ErrorPage.class);

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("failTxnForm",
		new CompoundPropertyModel<ErrorPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Button("continue") {
	    @Override
	    public void onSubmit() {
		// TODO
	    }
	});
	add(form);
    }
}