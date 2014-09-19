package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_SEND_MONEY)
public class SendMoneyFinishPage extends BaseTransactionsPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SendMoneyFinishPage.class);

    private String amount;
    private String recipient;
    private String txnId;
    private String authCode;

    private TransactionBean tab;

    public SendMoneyFinishPage(final TransactionBean tab) {
	super();

	this.tab = tab;

	setAmount(convertAmountToStringWithCurrency(tab.getDebitAmount()));
	setRecipient(tab.getPayee().getIdentifier().getValue());
	setTxnId(String.valueOf(tab.getTxnId()));
	setAuthCode(tab.getAuthCode());

	initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {
	Form<?> form = new Form("sendMoneyFinishForm",
		new CompoundPropertyModel<SendMoneyFinishPage>(this));

	// adding the submit buttons
	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		next();
	    };
	});

	// initialise all the labels
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<SendMoneyFinishPage>(this));
	labels.add(new Label("amount"));
	labels.add(new Label("recipient"));
	labels.add(new Label("txnId"));
	labels.add(new Label("authCode"));

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	add(pendingLbl);

	Label successMsgLbl = new Label("sendMoneySuccess", getLocalizer()
		.getString("sendMoneyFinish.success", this));
	add(successMsgLbl);

	if (tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successMsgLbl.setVisible(false);
	} else {
	    pendingLbl.setVisible(false);
	}

	form.add(labels);
	form.add(new FeedbackPanel("errorMessages"));
	add(form);
    }

    @Override
    protected Class getActiveMenu() {
	return SendMoneyPage.class;
    }

    public void next() {
	LOG.debug("SendMoneyFinishPage.next()");
	setResponsePage(ViewTransactionsPage.class);
    }

    public String getAmount() {
	return amount;
    }

    public void setAmount(String amount) {
	this.amount = amount;
    }

    public String getRecipient() {
	return recipient;
    }

    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }

    public String getTxnId() {
	return txnId;
    }

    public void setTxnId(String txnId) {
	this.txnId = txnId;
    }

    public String getAuthCode() {
	return authCode;
    }

    public void setAuthCode(String authCode) {
	this.authCode = authCode;
    }

}
