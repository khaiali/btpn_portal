package com.sybase365.mobiliser.web.cst.pages.usermanager;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CST_LOGIN)
public class ChangePasswordPage extends UserManagerMenuGroup {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChangePasswordPage.class);

    private String newPassword;
    private String confirmPassword;

    public ChangePasswordPage() {
	super();

	LOG.info("Created new ChangePasswordPage");
    }

    public ChangePasswordPage(final PageParameters parameters) {
	super(parameters);

	LOG.info("Created new ChangePasswordPage");
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("changepasswordform",
		new CompoundPropertyModel<ChangePasswordPage>(this));

	PasswordTextField pswd = new PasswordTextField("newPassword");

	// NOTE: you must ensure that there is a customised validation error
	// message supplied for password fields - otherwise the default message
	// will put the entered value in cleartext into the message.
	// Use the validator message key;
	// [myformfieldname>].StringValidator.range
	pswd.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(new ErrorIndicator());

	PasswordTextField cnfrmPswd = new PasswordTextField("confirmPassword");
	// don't need a required validator or a length between validator
	// for confirm password, because there is an equal check on the
	// new password field, which does have a required and length between
	// validator
	cnfrmPswd.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator());

	form.add(pswd);

	form.add(cnfrmPswd);

	form.add(new EqualPasswordInputValidator(pswd, cnfrmPswd));

	form.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));

	add(form);

    }

    private void handleSubmit() {

	try {
	    if (valedatePasswordStrength(getConfirmPassword(),
		    Constants.CREDENTIAL_TYPE_PASSWORD)) {
		if (setCustomerCredential(getMobiliserWebSession()
			.getCustomer().getId(), getConfirmPassword())) {

		    getSession().info(
			    getLocalizer().getString("change.password.success",
				    this));
		    LOG.debug("# Successfully changed password");
		    setResponsePage(com.sybase365.mobiliser.web.cst.pages.usermanager.ChangePasswordPage.class);

		}
	    }

	} catch (Exception e) {
	    LOG.error("# Failed to update password", e);
	    error(getLocalizer().getString("change.password.error", this));
	}
    }

    public String getNewPassword() {
	return newPassword;
    }

    public void setNewPassword(String newPassword) {
	this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
	return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
	this.confirmPassword = confirmPassword;
    }

    @Override
    protected Class getActiveMenu() {
	return ChangePasswordPage.class;
    }
}
