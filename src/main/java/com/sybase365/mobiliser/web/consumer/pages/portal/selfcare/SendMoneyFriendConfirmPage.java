package com.sybase365.mobiliser.web.consumer.pages.portal.selfcare;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.ConfirmVoucher;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.ConfirmVoucherResponse;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.consumer.pages.portal.transaction.SendMoneyFinishPage;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_SEND_MONEY)
public class SendMoneyFriendConfirmPage extends BaseSelfCarePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SendMoneyFriendConfirmPage.class);

    private TransactionBean tab;
    private String creditAmount;
    private String feeAmount;
    private String debitAmount;
    private String recipient;
    private String txnText;
    private boolean isUnknownPayee;
    private String warning;

    public SendMoneyFriendConfirmPage(final TransactionBean tab,
	    boolean isUnknownPayee) {
	super();

	this.tab = tab;
	this.isUnknownPayee = isUnknownPayee;
	initPageComponents();
    }

    @SuppressWarnings({ "unchecked", "serial" })
    protected void initPageComponents() {
	Form<?> form = new Form("sendMoneyConfirm",
		new CompoundPropertyModel<SendMoneyFriendConfirmPage>(this));

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	});
	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		sendMoneyConfirm();
	    };
	});
	// initialise all the labels
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<SendMoneyFriendConfirmPage>(this));

	setCreditAmount(convertAmountToStringWithCurrency(tab.getCreditAmount()));
	setFeeAmount(convertAmountToStringWithCurrency(tab.getFeeAmount()));
	setDebitAmount(convertAmountToStringWithCurrency(tab.getDebitAmount()));
	setRecipient(tab.getPayee().getIdentifier().getValue());
	setTxnText(tab.getText());
	Label warningLbl = new Label("warning");
	labels.add(warningLbl);
	if (!isUnknownPayee) {
	    warningLbl.setVisible(false);
	} else {
	    warning = getLocalizer().getString(
		    "sendMoneyConfirm.remittance.voucher.warning", this);
	}

	labels.add(new Label("creditAmount"));
	labels.add(new Label("feeAmount"));
	labels.add(new Label("debitAmount"));
	labels.add(new Label("recipient"));
	labels.add(new Label("txnText"));
	form.add(labels);
	form.add(new FeedbackPanel("errorMessages"));
	add(form);

    }

    @Override
    protected Class getActiveMenu() {
	return FriendsListPage.class;
    }

    private void handleBack() {
	LOG.debug("SendMoneyFriendConfirmPage.handleBack()");
	setResponsePage(SendMoneyFriendPage.class);
    }

    private void sendMoneyConfirm() {
	LOG.debug("SendMoneyFriendConfirmPage.sendMoneyConfirm()");
	try {
	    if (!isUnknownPayee) {
		if (handleTransaction(tab))
		    setResponsePage(new SendMoneyFriendFinishPage(tab));
		else {
		    if (tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
			setResponsePage(new SendMoneyFinishPage(tab));
		    }
		}

	    } else {
		if (confirmVoucher(tab))
		    setResponsePage(new SendMoneyFriendFinishPage(tab));
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during preauthorization continue", e);
	    error(getLocalizer().getString("preauthorization.continue.error",
		    this));

	}

    }

    private boolean confirmVoucher(TransactionBean transaction)
	    throws Exception {
	LOG.debug("# SendMoneyFriendPage.confirmVoucher()");
	ConfirmVoucherResponse response;
	ConfirmVoucher request = getNewMobiliserRequest(ConfirmVoucher.class);
	request.setReferenceTransaction(transaction.getRefTransaction());
	response = wsConfirmVoucherClient.confirmvoucher(request);
	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error during commit voucher authorization");
	    return false;
	}

	LOG.info(
		"# confirm voucher preauthorise continue transaction[{}] money successfully finished",
		tab.getModule());
	tab.setTxnId(response.getTransaction().getSystemId());
	tab.setAuthCode("");
	setTimestamp(response.getTimestamp());

	return true;

    }

    public String getRecipient() {
	return recipient;
    }

    public void setRecipient(String recipient) {
	this.recipient = recipient;
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

    public String getDebitAmount() {
	return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
	this.debitAmount = debitAmount;
    }

    public String getTxnText() {
	return txnText;
    }

    public void setTxnText(String txnText) {
	this.txnText = txnText;
    }

}