package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import java.text.ParseException;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.StartVoucher;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.StartVoucherResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.MoneyFeeType;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.SVA;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_SEND_MONEY)
public class SendMoneyPage extends BaseTransactionsPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SendMoneyPage.class);

    private String recipient;
    private String amountString;
    private String txnText;
    private long amount;
    private Transaction transaction;

    public SendMoneyPage() {
	super();
    }

    public SendMoneyPage(final String recipient) {
	super();
	this.recipient = recipient;
    }

    @SuppressWarnings({ "unchecked", "serial" })
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	Form<?> form = new Form("sendMoneyForm",
		new CompoundPropertyModel<SendMoneyPage>(this));

	form.add(new RequiredTextField<String>("recipient").setRequired(true)
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField("amountString", new PropertyModel(this,
		"amountString")).setRequired(true)
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new Label("currency", getCurrencySymbol()));
	form.add(new TextArea("txnText", new PropertyModel(this, "txnText"))
		.setRequired(true)
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new SimpleAttributeModifier("size",
			Constants.MAX_LENGTH_SEND_MONEY_TXN_TEXT))
		.add(new ErrorIndicator()));

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));
	form.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		sendMoneyNext();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));
	add(form);

    }

    private void handleBack() {
	LOG.debug("#SendMoneyPage.handleBack()");

	setResponsePage(ViewTransactionsPage.class);
    }

    private void sendMoneyNext() {
	LOG.debug("#SendMoneyPage.sendMoneyNext()");

	if (getTxnText().length() > Integer.valueOf(
		Constants.MAX_LENGTH_SEND_MONEY_TXN_TEXT).intValue()) {
	    error(getLocalizer().getString("sendMoney.text.length.error", this));
	    return;
	}

	PhoneNumber pn = new PhoneNumber(getRecipient(), getConfiguration()
		.getCountryCode());

	com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = getMobiliserWebSession()
		.getLoggedInCustomer();

	convertAmount();
	TransactionBean tab = new TransactionBean();
	VatAmount vatAmnt = new VatAmount();
	SVA sva = null;
	try {
	    WalletEntry wallet = getSvaPI(getMobiliserWebSession()
		    .getLoggedInCustomer().getCustomerId());
	    if (PortalUtils.exists(wallet))
		sva = wallet.getSva();
	    if (PortalUtils.exists(sva)) {
		vatAmnt.setCurrency(PortalUtils.exists(sva.getCurrency()) ? sva
			.getCurrency() : getConfiguration().getCurrency());
	    } else {
		vatAmnt.setCurrency(getConfiguration().getCurrency());
	    }

	} catch (Exception e1) {
	    LOG.error("# Error while getting SVA's payment instrument", e1);
	}

	vatAmnt.setValue(amount);

	tab.setAmount(vatAmnt);
	tab.setAutoCapture(true);
	tab.setUsecase(Integer.valueOf(Constants.USE_CASE_SEND_MONEY));
	tab.setText(txnText);

	TransactionParticipant payer = new TransactionParticipant();
	Identifier payerId = new Identifier();
	payerId.setType(Constants.IDENT_TYPE_CUST_ID);
	payerId.setValue(String.valueOf(loggedInCustomer.getCustomerId()));
	payer.setIdentifier(payerId);
	tab.setPayer(payer);

	TransactionParticipant payee = new TransactionParticipant();
	Identifier payeeId = new Identifier();
	payeeId.setType(Constants.IDENT_TYPE_MSISDN);
	payeeId.setValue(pn.getInternationalFormat());
	payee.setIdentifier(payeeId);
	tab.setPayee(payee);
	tab.setModule(Constants.MODULE_SEND_MONEY);

	Customer payeeCustomer = getCustomerByIdentification(
		Constants.IDENT_TYPE_MSISDN, pn.getInternationalFormat());

	try {
	    if (PortalUtils.exists(payeeCustomer)) {
		if (handleTransaction(tab))
		    setResponsePage(new SendMoneyConfirmPage(tab, false));
	    } else {
		tab.setUsecase(Constants.USE_CASE_SEND_VOUCHER_UNKNOWN);
		if (startVoucher(tab))
		    setResponsePage(new SendMoneyConfirmPage(tab, true));
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during Preauthorization", e);
	    error(getLocalizer().getString("preauthorization.error", this));
	    return;
	}

    }

    private boolean startVoucher(TransactionBean tab) throws Exception {
	LOG.debug("# SendMoneyPage.startVoucher()");
	StartVoucher request = getNewMobiliserRequest(StartVoucher.class);
	StartVoucherResponse response = null;
	request.setAmount(tab.getAmount());
	request.setAutoCapture(tab.isAutoCapture());
	request.setOrderChannel(tab.getOrderChannel());
	request.setOrderID(tab.getOrderId());
	request.setPayee(tab.getPayee());
	request.setPayer(tab.getPayer());
	request.setText(tab.getText());
	request.setUsecase(tab.getUsecase());
	response = wsStartVoucherClient.startvoucher(request);
	transaction = response.getTransaction();

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error during start voucher preauthorization");
	    return false;
	}

	LOG.info(
		"# start voucher preauthorise transaction[{}] money successfully finished",
		tab.getModule());

	// Calculate fees and amounts
	long payeeFee = 0;
	long payerFee = 0;
	for (MoneyFeeType mft : response.getMoneyFee()) {
	    if (mft.isPayee()) {
		payeeFee += mft.getValue();
		payeeFee += mft.getVat();
	    } else {
		// isPayer
		payerFee += mft.getValue();
		payerFee += mft.getVat();
	    }
	}

	tab.setFeeAmount(payerFee + payeeFee);
	tab.setDebitAmount(tab.getAmount().getValue() + payerFee);
	tab.setCreditAmount(tab.getAmount().getValue() - payeeFee);
	if (PortalUtils.exists(response.getAuthenticationMethods())) {
	    if (PortalUtils.exists(response.getAuthenticationMethods()
		    .getAuthMethodPayee()))
		tab.setAuthenticationMethodPayee(Integer.valueOf(response
			.getAuthenticationMethods().getAuthMethodPayee()
			.getId()));
	    if (PortalUtils.exists(response.getAuthenticationMethods()
		    .getAuthMethodPayer()))
		tab.setAuthenticationMethodPayer(Integer.valueOf(response
			.getAuthenticationMethods().getAuthMethodPayer()
			.getId()));
	}
	tab.setPreAuthFinished(true);
	tab.setRefTransaction(response.getTransaction());
	return true;

    }

    public void convertAmount() {
	LOG.debug("#SendMoneyPage.convertAmount()");
	try {
	    amount = convertAmountToLong(amountString);
	} catch (ParseException e) {
	    LOG.error("# could not parse amount[" + amountString + "]", e);
	}
    }

    public String getRecipient() {
	return recipient;
    }

    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }

    public String getAmountString() {
	return amountString;
    }

    public void setAmountString(String amountString) {
	this.amountString = amountString;
    }

    public String getTxnText() {
	return txnText;
    }

    public void setTxnText(String txnText) {
	this.txnText = txnText;
    }

}
