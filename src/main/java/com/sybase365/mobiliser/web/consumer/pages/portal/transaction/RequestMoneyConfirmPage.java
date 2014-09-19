package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.DemandForPayment;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.DemandForPaymentResponse;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_REQUEST_MONEY)
public class RequestMoneyConfirmPage extends BaseTransactionsPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RequestMoneyConfirmPage.class);

    private String creditAmount;
    private String feeAmount;
    private String debitAmonut;
    private String payer;
    private String txnText;
    private boolean isRealTimeTxn;
    private Long invoiceId;

    private TransactionBean tab;

    public RequestMoneyConfirmPage(final TransactionBean tab,
	    boolean isRealTimeTxn) {
	super();

	this.tab = tab;
	this.isRealTimeTxn = isRealTimeTxn;
	initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {

	Form<?> form = new Form("requestMoneyConfirmForm",
		new CompoundPropertyModel<RequestMoneyConfirmPage>(this));

	// adding the submit buttons
	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		next();
	    };
	});

	// initialise all the labels
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<RequestMoneyConfirmPage>(this));

	setCreditAmount(convertAmountToStringWithCurrency(tab.getCreditAmount()));
	setFeeAmount(convertAmountToStringWithCurrency(tab.getFeeAmount()));
	setDebitAmonut(convertAmountToStringWithCurrency(tab.getDebitAmount()));
	setPayer(tab.getPayer().getIdentifier().getValue());
	setTxnText(tab.getText());

	labels.add(new Label("creditAmount"));

	WebMarkupContainer debitDiv = new WebMarkupContainer(
		"debitFeeContainer");
	debitDiv.add(new Label("debitAmonut"));

	debitDiv.add(new Label("feeAmount"));
	if (!isRealTimeTxn()) {
	    debitDiv.setVisible(Boolean.FALSE);
	}
	labels.add(debitDiv);

	labels.add(new Label("payer"));
	labels.add(new Label("txnText"));

	form.add(labels);
	form.add(new FeedbackPanel("errorMessages"));
	add(form);
    }

    @Override
    protected Class getActiveMenu() {
	return RequestMoneyPage.class;
    }

    private void handleBack() {
	LOG.debug("#RequestMoneyConfirmPage.handleBack()");
	setResponsePage(RequestMoneyPage.class);
    }

    private void next() {
	LOG.debug("#RequestMoneyConfirmPage.next)");
	try {
	    if (isRealTimeTxn()) {
		if (handleTransaction(tab))
		    setResponsePage(new RequestMoneyFinish(tab, isRealTimeTxn()));
		else {
		    if (tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
			setResponsePage(new RequestMoneyFinish(tab,
				isRealTimeTxn()));
		    }
		}
	    } else {
		if (demandForPayment(tab))
		    setResponsePage(new RequestMoneyFinish(tab, isRealTimeTxn()));
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during preauthorization continue", e);
	    error(getLocalizer().getString("preauthorization.continue.error",
		    this));
	}

    }

    private boolean demandForPayment(TransactionBean tab) {
	DemandForPaymentResponse response;
	DemandForPayment request;
	try {
	    request = getNewMobiliserRequest(DemandForPayment.class);
	    request.setAmount(tab.getAmount());
	    request.setPayee(tab.getPayee());
	    request.setPayer(tab.getPayer());
	    request.setText(getTxnText());
	    response = wsDemandForPaymentClient.demandForPayment(request);
	    invoiceId = response.getInvoiceId();
	    tab.setTxnId(invoiceId);
	    if (!evaluateMobiliserResponse(response)) {
		return Boolean.FALSE;
	    }
	    return Boolean.TRUE;

	} catch (Exception e) {
	    return Boolean.FALSE;
	}
    }

    public String getCreditAmount() {
	return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
	this.creditAmount = creditAmount;
    }

    public String getFeeAmount() {
	return feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
	this.feeAmount = feeAmount;
    }

    public String getDebitAmonut() {
	return debitAmonut;
    }

    public void setDebitAmonut(String debitAmonut) {
	this.debitAmonut = debitAmonut;
    }

    public String getPayer() {
	return payer;
    }

    public void setPayer(String payer) {
	this.payer = payer;
    }

    public String getTxnText() {
	return txnText;
    }

    public void setTxnText(String txnText) {
	this.txnText = txnText;
    }

    public boolean isRealTimeTxn() {
	return isRealTimeTxn;
    }

    public void setRealTimeTxn(boolean isRealTimeTxn) {
	this.isRealTimeTxn = isRealTimeTxn;
    }

    public Long getInvoiceId() {
	return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
	this.invoiceId = invoiceId;
    }

}
