package com.sybase365.mobiliser.web.checkout.pages;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.WebContinue;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.web.checkout.models.Transaction;
import com.sybase365.mobiliser.web.checkout.util.SmsAuthenticationThread;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@SuppressWarnings("all")
public class SmsAuthenticationPage extends BaseCheckoutPage {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SmsAuthenticationPage.class);
    private AjaxResponse response;
    private WebMarkupContainer cancelDiv;
    private WebMarkupContainer retryDiv;
    private Label errorMsg;
    private boolean isRedirected;

    public SmsAuthenticationPage() {
	super();
	LOG.info("Created new CheckPinPage");
    }

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	if (LOG.isDebugEnabled())
	    LOG.debug("Continue Authentication");

	if (getMobiliserWebSession().getTxnPayer() == null) {
	    setResponsePage(ErrorPage.class);
	    return;
	}

	Transaction txn = getMobiliserWebSession().getTransaction();
	if (LOG.isDebugEnabled())
	    LOG.debug("Request retry count: " + txn.getContinueRetryCounter());
	// Create Thread.
	SmsAuthenticationThread t = new SmsAuthenticationThread(txn,
		getMobiliserWebSession().getMsisdn(),
		createWebContinueRequest());
	t.setName("TxnThread" + txn.getTxnId());

	// Store Thread in Context
	getMobiliserWebSession().setSmsThread(t);

	if (LOG.isDebugEnabled())
	    LOG.debug("Starting Authentication Thread. [Msisdn="
		    + txn.getPayer().getId() + "] [txn=" + txn.getTxnId() + "]");

	// Start Thread
	t.start();

	constructPageComponent();

    }

    private WebContinue createWebContinueRequest() {
	WebContinue request = null;
	try {
	    request = getNewMobiliserRequest(WebContinue.class);
	    TransactionParticipant participant = new TransactionParticipant();
	    Identifier identifier = new Identifier();
	    identifier.setType(Constants.IDENT_TYPE_MSISDN);
	    identifier.setValue(getMobiliserWebSession().getMsisdn());
	    participant.setIdentifier(identifier);
	    request.setParticipant(participant);
	    com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction t = new com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction();
	    t.setSystemId(getMobiliserWebSession().getTransaction().getTxnId());
	    request.setReferenceTransaction(t);
	    request.setOrderChannel(Constants.ORDER_CHANNEL_WEB);
	    request.setTraceNo(UUID.randomUUID().toString());
	} catch (Exception e) {
	    LOG.error("#An error occurred while creating web continue request",
		    e);
	    error(getLocalizer().getString("sms.authenticate.error", this));
	}
	return request;

    }

    private void constructPageComponent() {
	add(new FeedbackPanel("errorMessages"));

	errorMsg = new Label("smsAuthenticationError", getLocalizer()
		.getString("smsAuthentication.error", this));
	errorMsg.setVisible(false);
	errorMsg.setOutputMarkupId(true);
	errorMsg.setOutputMarkupPlaceholderTag(true);

	final WebMarkupContainer backDiv = new WebMarkupContainer("backDiv");
	backDiv.setOutputMarkupId(true);
	backDiv.setVisible(false);
	backDiv.setOutputMarkupPlaceholderTag(true);
	Form<?> backForm = new Form("backForm",
		new CompoundPropertyModel<SmsAuthenticationPage>(this));
	backForm.add(new Button("back") {
	    @Override
	    public void onSubmit() {
		// TODO
	    }
	});
	backDiv.add(backForm);

	retryDiv = new WebMarkupContainer("retryDiv");
	retryDiv.setOutputMarkupId(true);
	retryDiv.setOutputMarkupPlaceholderTag(true);
	retryDiv.setVisible(false);
	Form<?> retryForm = new Form("retryForm",
		new CompoundPropertyModel<SmsAuthenticationPage>(this));
	retryForm.add(new Button("retry") {
	    @Override
	    public void onSubmit() {
		// TODO
	    }
	});
	retryDiv.add(retryForm);

	cancelDiv = new WebMarkupContainer("cancelDiv");
	cancelDiv.setOutputMarkupId(true);
	cancelDiv.setOutputMarkupPlaceholderTag(true);
	cancelDiv.setVisible(false);
	Form<?> cancelForm = new Form("cancelForm",
		new CompoundPropertyModel<SmsAuthenticationPage>(this));
	cancelForm.add(new Button("cancel") {
	    @Override
	    public void onSubmit() {
		// TODO
	    }
	}.setVisible(false));
	cancelDiv.add(cancelForm);

	final AbstractDefaultAjaxBehavior behave = new AbstractDefaultAjaxBehavior() {
	    protected void respond(final AjaxRequestTarget target) {
		if (!isRedirected) {
		    checkStatus(target);
		}
	    }
	};
	add(behave);

	Form<?> ajaxForm = new Form("ajaxForm",
		new CompoundPropertyModel<SmsAuthenticationPage>(this)) {
	    @Override
	    protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.put("action", behave.getCallbackUrl());

	    }

	};
	add(errorMsg);
	add(backDiv);
	add(retryDiv);
	add(cancelDiv);
	add(ajaxForm);

    }

    protected void checkStatus(AjaxRequestTarget target) {
	if (PortalUtils.exists(response)) {
	    checkResponse(target);
	}
	// get current Thread
	SmsAuthenticationThread t = getMobiliserWebSession().getSmsThread();

	// if technical problem...
	// Thread not started
	if (t == null) {
	    prepairResponse(new AjaxResponse(9999, "Thread not started"), null);
	    return;
	}

	// exception was thrown from thread
	if (t.getException() != null) {
	    prepairResponse(new AjaxResponse(9999, "Exception: "
		    + t.getException().getMessage()), null);
	    return;
	}

	// Still processing request
	if (t.isActive()) {
	    prepairResponse(new AjaxResponse(-1, ""), null);
	    return;
	}

	// Thread is finished

	// no answer from Mobiliser. Technical error
	if (t.getResponse() == null) {
	    prepairResponse(
		    new AjaxResponse(9999, "No response from Mobiliser"), null);
	    return;
	}

	Transaction txn = getMobiliserWebSession().getTransaction();

	// check if second try is exceeded. Cancel Transaction by second
	// retry
	if (txn.getContinueRetryCounter() >= 2
		&& t.getResponse().getStatus().getCode() == 2853) {

	    if (LOG.isDebugEnabled())
		LOG.debug("retry count exceeded. Canceling transaction");

	    try {
		// Fail transaction
		txn.failTransaction(2853);

	    } catch (Exception e) {
		LOG.error("#failTransaction: " + e, e);
	    }

	    // Kill Session
	    getMobiliserWebSession().invalidate();

	    prepairResponse(new AjaxResponse(9999, "Cancel Response"), txn);
	    return;
	}

	// check status of mobiliser response
	if (t.getResponse().getStatus() != null
		&& t.getResponse().getStatus().getCode() != 0) {
	    // Mobiliser request failed with an error code

	    AjaxResponse response = new AjaxResponse(t.getResponse()
		    .getStatus().getCode(), t.getResponse().getStatus()
		    .getValue());

	    switch (t.getResponse().getStatus().getCode()) {
	    case 2853: // Transaction timed out
		response.setRetryVisible(true);

	    default:
		response.setRedirect(true);
	    }

	    prepairResponse(response, txn);
	    return;
	}

	// everything was OK
	prepairResponse(new AjaxResponse(0, "OK"), txn);
    }

    private void checkResponse(AjaxRequestTarget target) {
	// Show cancel
	if (response.isCancelVisible()) {
	    cancelDiv.setVisible(true);
	    target.addComponent(cancelDiv);
	}
	// Show retry
	if (response.isRetryVisible()) {
	    retryDiv.setVisible(true);
	    target.addComponent(retryDiv);
	}

	// Show error Text
	if (PortalUtils.exists(response.getErrorMessage())) {
	    errorMsg.setVisible(true);
	    target.addComponent(errorMsg);
	}

	// Return to URL
	if (PortalUtils.exists(response.getReturnUrl())
		&& response.isRedirect())
	    try {
		getWebRequestCycle().getWebResponse().getHttpServletResponse()
			.sendRedirect(response.getReturnUrl());
		isRedirected = true;
	    } catch (IOException e) {
		LOG.error(
			"#An error occurred while redirecting to the return url["
				+ response.getReturnUrl() + "]", e);
		return;
	    }

    }

    private void prepairResponse(AjaxResponse obj, Transaction txn) {
	if (LOG.isDebugEnabled())
	    LOG.debug("Return Ajax status " + obj.getStatus() + " - "
		    + obj.getErrorMessage());

	if (txn != null) {
	    obj.setReturnUrl(txn.getReturnUrl());
	    obj.setRetry(txn.getContinueRetryCounter());
	    obj.setRedirect(true);

	    if (LOG.isDebugEnabled()) {
		Date pollStart = (Date) txn.getContinueStartDate();
		Date currentTime = new Date();
		long dif = currentTime.getTime() - pollStart.getTime();
		LOG.debug("Ajax Poll: [RetryCount=" + (obj.getRetry())
			+ "] [Elapsed Time=" + (dif / 1000) + "s]");
	    }
	}
	response = obj;
    }
}
