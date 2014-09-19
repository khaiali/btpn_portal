package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.security.ChangeCredentialRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.ChangeCredentialResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.BaseWebSession;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_CHANGE_PIN)
public class ChangePinPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChangePinPage.class);

    private String oldPin;

    public String getOldPin() {
	return oldPin;
    }

    public void setOldPin(String oldPin) {
	this.oldPin = oldPin;
    }

    private String newPin;
    private String confirmNewPin;

    public ChangePinPage() {
	super();

	LOG.info("Created new ChangePinPage");
    }

    public ChangePinPage(final PageParameters parameters) {
	super(parameters);

	LOG.info("Created new ChangePinPage");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();

	Form<?> form = new Form("changepinform",
		new CompoundPropertyModel<ChangePinPage>(this));

	form.add(new PasswordTextField("oldPin").setRequired(true)
		.add(Constants.mediumStringValidator)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new PasswordTextField("newPin").setRequired(true)
		.add(Constants.mediumStringValidator)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new PasswordTextField("confirmNewPin").setRequired(true)
		.add(Constants.mediumStringValidator)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new FeedbackPanel("errorMessages"));

	form.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    };
	});
	add(form);

    }

    private void handleSubmit() {

	if (!getNewPin().equals(getConfirmNewPin())) {
	    error(getLocalizer().getString("customer.pin.notConfirmed", this));
	    return;
	}

	if (!valedatePasswordStrength(getNewPin(),
		Constants.CREDENTIAL_TYPE_PIN)) {
	    return;
	}

	try {

	    com.sybase365.mobiliser.util.tools.wicketutils.security.Customer customer = ((BaseWebSession) getWebSession())
		    .getLoggedInCustomer();

	    ChangeCredentialRequest chCreReq = getNewMobiliserRequest(ChangeCredentialRequest.class);
	    chCreReq.setOldCredential(getOldPin());
	    chCreReq.setNewCredential(getNewPin());
	    chCreReq.setCustomerId(customer.getCustomerId());
	    chCreReq.setCredentialType(Constants.CREDENTIAL_TYPE_PIN);
	    ChangeCredentialResponse chCreResp = wsSystemAuthSecurityClient
		    .changeCredential(chCreReq);

	    if (!evaluateMobiliserResponse(chCreResp))
		return;
	    else {
		getSession().info(
			getLocalizer().getString("change.pin.success", this));
		LOG.debug("# Successfully changed customer pin");
		setResponsePage(ConsumerHomePage.class);
	    }
	} catch (Exception e) {
	    LOG.error("# Failed to update customer pin", e);
	    error(getLocalizer().getString("change.pin.error", this));
	}

    }

    public String getNewPin() {
	return newPin;
    }

    public void setNewPin(String newPin) {
	this.newPin = newPin;
    }

    public String getConfirmNewPin() {
	return confirmNewPin;
    }

    public void setConfirmNewPin(String confirmNewPin) {
	this.confirmNewPin = confirmNewPin;
    }

}
