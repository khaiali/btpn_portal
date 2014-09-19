package com.sybase365.mobiliser.web.checkout.pages;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.security.CheckCredentialRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.CheckCredentialResponse;
import com.sybase365.mobiliser.web.checkout.models.Transaction;
import com.sybase365.mobiliser.web.util.Constants;

@SuppressWarnings("all")
public class CheckPinPage extends BaseCheckoutPage {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CheckPinPage.class);
    private String pin;

    public CheckPinPage() {
	super();
	LOG.info("Created new CheckPinPage");
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));

	Form<?> form = new Form("checkPinForm",
		new CompoundPropertyModel<CheckPinPage>(this));

	form.add(new PasswordTextField("pin").setRequired(true)
		.add(Constants.largeStringValidator)
		.add(new SimpleAttributeModifier("autocomplete", "off"))
		.add(Constants.largeSimpleAttributeModifier));
	form.add(new Button("checkPin") {
	    @Override
	    public void onSubmit() {
		try {
		    int status = checkCredential();
		    if (status != 0) {
			return;
		    } else {
			Transaction transaction = getMobiliserWebSession()
				.getTransaction();
			transaction.setPayer(getMobiliserWebSession()
				.getTxnPayer());
			transaction.setStatusCode(status);

		    }

		} catch (Exception e) {
		    LOG.error(
			    "#An error occurred while checking the pin for web transaction["
				    + getMobiliserWebSession().getTransaction()
					    .getTxnId() + "]", e);
		    error(getLocalizer().getString("check.pin.error", this));
		    return;
		}
		setResponsePage(SmsAuthenticationPage.class);
	    }
	});
	form.add(new Button("cancel") {
	    @Override
	    public void onSubmit() {
		Transaction txn = null;
		try {
		    txn = getMobiliserWebSession().getTransaction();

		    if (LOG.isDebugEnabled())
			LOG.debug("Consumer canceled transaction. [TransactionID="
				+ txn.getSystemId() + "]");

		    if (txn.getStatusCode() == 0)
			txn.failTransaction(2541);
		    else
			txn.failTransaction(txn.getStatusCode());

		    // Invalidate Session
		    getMobiliserWebSession().invalidate();

		    if (LOG.isDebugEnabled())
			LOG.debug("Returning to merchant: "
				+ txn.getReturnUrl());

		    getWebRequestCycle().getWebResponse()
			    .getHttpServletResponse()
			    .sendRedirect(txn.getReturnUrl());

		} catch (Exception e) {
		    LOG.error(
			    "#An error occured while canceling the transaction["
				    + txn.getSystemId() + "]", e);
		    error(getLocalizer().getString("txn.cancel.error", this));
		    return;
		}
	    }
	}.setDefaultFormProcessing(false));

	add(form);

    }

    private int checkCredential() throws Exception {
	CheckCredentialRequest request = getNewMobiliserRequest(CheckCredentialRequest.class);
	request.setCredential(getPin());
	request.setCustomerId(getMobiliserWebSession().getTxnPayer().getId());
	request.setCredentialType(Constants.CREDENTIAL_TYPE_PIN);
	CheckCredentialResponse response = wsSecurityClient
		.checkCredential(request);
	if (!evaluateMobiliserResponse(response)) {
	    return response.getStatus().getCode();
	}
	return response.getStatus().getCode();
    }

    public String getPin() {
	return pin;
    }

}
