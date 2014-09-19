package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.util.Constants;

public class ResetCredentialPage extends CustomerCareMenuGroup {
    private static final Logger LOG = LoggerFactory
	    .getLogger(ResetCredentialPage.class);

    private CustomerBean customer;
    private WebPage backPage;
    private String credentialType;
    public static final String RESET_METHOD_PW = "password";
    public static final String RESET_METHOD_PIN = "pin";

    public ResetCredentialPage() {
	super();
    }

    public ResetCredentialPage(CustomerBean customer, WebPage backpage,
	    String credentialType) {
	super();
	this.customer = customer;
	this.backPage = backpage;
	this.credentialType = credentialType;
	initPageComponents();
    }

    protected void initPageComponents() {
	final Form<?> form = new Form("resetCredentialForm",
		new CompoundPropertyModel<ResetCredentialPage>(this));

	WebMarkupContainer resetPinDiv = new WebMarkupContainer("resetPinDiv");
	resetPinDiv.setVisible(!getCredentialType().equals(RESET_METHOD_PW));
	add(resetPinDiv);
	WebMarkupContainer resetPasswordDiv = new WebMarkupContainer(
		"resetPasswordDiv");
	resetPasswordDiv.setVisible(!getCredentialType().equals(
		RESET_METHOD_PIN));
	add(resetPasswordDiv);

	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(getBackPage());
	    };
	});

	form.add(new Button("reset") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (getCredentialType().equals("pin"))
		    doResetPin();
		else if (getCredentialType().equals("password"))
		    doResetPassword();
		else {
		    if (LOG.isDebugEnabled())
			LOG.debug("# Unknown reset method: "
				+ getCredentialType());

		    error(getLocalizer()
			    .getString("reset.unknown.method", this));
		}
	    }

	});

	add(form);
    }

    private void doResetPassword() {
	try {
	    if (!generateTempCredential(getCustomer().getId(),
		    Constants.CREDENTIAL_TYPE_PASSWORD,
		    Constants.ORDER_CHANNEL_WEB_STR))
		return;
	    info(getLocalizer().getString("reset.password.ok", this.getPage()));
	    setResponsePage(new StandingDataPage(getCustomer()));
	} catch (Exception e) {
	    LOG.error("#An error occured while reseting the pin for customer["
		    + getCustomer().getId() + "]", e);
	    error(getLocalizer().getString("reset.password.failed", this));
	}
    }

    private void doResetPin() {
	try {
	    if (!generateTempCredential(getCustomer().getId(),
		    Constants.CREDENTIAL_TYPE_PIN, Constants.ORDER_CHANNEL_SMS))
		return;

	    info(getLocalizer().getString("reset.pin.ok", this));
	    setResponsePage(new StandingDataPage(getCustomer()));
	} catch (Exception e) {
	    LOG.error("#An error occured while reseting the pin for customer["
		    + getCustomer().getId() + "]", e);
	    error(getLocalizer().getString("reset.pin.failed", this));
	}

    };

    public CustomerBean getCustomer() {
	return customer;
    }

    public WebPage getBackPage() {
	return backPage;
    }

    public String getCredentialType() {
	return credentialType;
    }

}
