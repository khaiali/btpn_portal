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

@AuthorizeInstantiation(Constants.PRIV_REQUEST_MONEY)
public class RequestMoneyFinish extends BaseTransactionsPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RequestMoneyFinish.class);

    private String amount;
    private String payer;
    private String txnId;
    private String authCode;
    private boolean isRealTimeTxn;
    private String invoiceId;
    private String successMsg;

    private TransactionBean tab;

    public RequestMoneyFinish(final TransactionBean tab, boolean isRealTimeTxn) {
	super();

	this.tab = tab;
	this.isRealTimeTxn = isRealTimeTxn;
	if (this.isRealTimeTxn) {
	    this.successMsg = getLocalizer().getString(
		    "requestMoneyFinish.realTimeTxn.success", this);
	} else {
	    this.successMsg = getLocalizer().getString(
		    "requestMoneyFinish.invoice.success", this);
	}
	initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {
	Form<?> form = new Form("requesstMoneyFinishForm",
		new CompoundPropertyModel<RequestMoneyFinish>(this));

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

	setAmount(convertAmountToStringWithCurrency(tab.getCreditAmount()));
	setPayer(tab.getPayer().getIdentifier().getValue());
	setTxnId(String.valueOf(tab.getTxnId()));
	setAuthCode(tab.getAuthCode());
	labels.add(new Label("amount"));
	labels.add(new Label("successMsg"));
	labels.add(new Label("payer"));

	WebMarkupContainer txnIdDiv = new WebMarkupContainer("txnIdContainer");
	txnIdDiv.add(new Label("txnId"));
	labels.add(txnIdDiv);

	if (!isRealTimeTxn()) {
	    txnIdDiv.setVisible(Boolean.FALSE);
	}

	WebMarkupContainer invoiceIdDiv = new WebMarkupContainer(
		"invoiceIdContainer");
	invoiceIdDiv.add(new Label("invoiceId"));
	labels.add(invoiceIdDiv);

	if (isRealTimeTxn()) {
	    invoiceIdDiv.setVisible(Boolean.FALSE);
	}

	labels.add(new Label("authCode"));
	form.add(labels);

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	add(pendingLbl);

	Label successMsgLbl = new Label("sendMoneySuccess", getLocalizer()
		.getString("requestMoneyFinish.success", this));
	add(successMsgLbl);

	if (tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successMsgLbl.setVisible(false);
	} else {
	    pendingLbl.setVisible(false);
	}

	form.add(new FeedbackPanel("errorMessages"));
	add(form);

    }

    @Override
    protected Class getActiveMenu() {
	return RequestMoneyPage.class;
    }

    public void next() {
	LOG.debug("#RequestMoneyFinish.next()");
	setResponsePage(ViewTransactionsPage.class);
    }

    public String getAmount() {
	return amount;
    }

    public void setAmount(String amount) {
	this.amount = amount;
    }

    public String getPayer() {
	return payer;
    }

    public void setPayer(String payer) {
	this.payer = payer;
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

    public boolean isRealTimeTxn() {
	return isRealTimeTxn;
    }

    public String getInvoiceId() {
	return txnId;
    }

}
