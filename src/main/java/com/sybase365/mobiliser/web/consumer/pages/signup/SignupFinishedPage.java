package com.sybase365.mobiliser.web.consumer.pages.signup;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.consumer.pages.portal.selfcare.ConsumerHomePage;

public class SignupFinishedPage extends ConsumerSignupPage {

    private static final long serialVersionUID = 1L;
    private boolean isSuccess;

    public SignupFinishedPage(boolean isSuccess) {
	super();
	this.isSuccess = isSuccess;
	initPageComponents();
    }

    protected void initPageComponents() {

	// super.initOwnPageComponents();

	Form<?> form = new Form("signupFinishForm",
		new CompoundPropertyModel<SignupFinishedPage>(this));

	String succsessMsg = getLocalizer().getString("signup.finished", this);

	if (!this.isSuccess) {
	    succsessMsg = getLocalizer().getString("pendingApproval.msg", this);
	}

	form.add(new WebMarkupContainer("continueMsg")
		.setVisible(this.isSuccess));

	form.add(new Label("successMsg", succsessMsg)
		.add(new SimpleAttributeModifier("font-weight", "bold")));

	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cleanupSession();
		setResponsePage(ConsumerHomePage.class);
	    };
	});
	add(form);
    }
}
