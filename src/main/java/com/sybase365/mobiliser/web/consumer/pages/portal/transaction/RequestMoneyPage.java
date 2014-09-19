package com.sybase365.mobiliser.web.consumer.pages.portal.transaction;

import java.text.ParseException;
import java.util.Currency;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_REQUEST_MONEY)
public class RequestMoneyPage extends BaseTransactionsPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RequestMoneyPage.class);

    private String payer;
    private String amountString;
    private long amount;
    private String txnText;
    private boolean isRealTimeTxn;

    @SuppressWarnings({ "unchecked", "serial" })
    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	Form<?> form = new Form("requestMoneyForm",
		new CompoundPropertyModel<RequestMoneyPage>(this));

	form.add(new RequiredTextField<String>("payer").setRequired(true)
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("amountString")
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_16_2))
		.setRequired(true).add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new Label("currency", getCurrencySymbol()));

	form.add(new TextArea<String>("txnText", new PropertyModel(this,
		"txnText")).setRequired(true)
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new CheckBox("isRealTimeTxn"));

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
		requestMoneyNext();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));
	add(form);

    }

    private void handleBack() {
	LOG.debug("#SendMoney.handleBack()");
	setResponsePage(ViewTransactionsPage.class);
    }

    private void requestMoneyNext() {

	LOG.debug("#SendMoney.requestMoneyNext()");

	com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = getMobiliserWebSession()
		.getLoggedInCustomer();

	convertAmount();
	final TransactionBean tab = new TransactionBean();
	final VatAmount vatAmnt = new VatAmount();
	vatAmnt.setCurrency(getConfiguration().getCurrency());
	vatAmnt.setValue(amount);

	tab.setAmount(vatAmnt);
	tab.setAutoCapture(true);
	tab.setUsecase(Integer.valueOf(Constants.USE_CASE_REQUEST_MONEY));
	tab.setText(txnText);

	final TransactionParticipant txnPayer = new TransactionParticipant();
	final Identifier payerIdent = new Identifier();
	payerIdent.setType(Constants.IDENT_TYPE_MSISDN);
	final PhoneNumber pn = new PhoneNumber(payer, getConfiguration()
		.getCountryCode());
	payerIdent.setValue(pn.getInternationalFormat());
	txnPayer.setIdentifier(payerIdent);
	tab.setPayer(txnPayer);

	final TransactionParticipant payee = new TransactionParticipant();
	final Identifier payeeId = new Identifier();
	payeeId.setType(Constants.IDENT_TYPE_CUST_ID);
	payeeId.setValue(String.valueOf(loggedInCustomer.getCustomerId()));
	payee.setIdentifier(payeeId);
	tab.setPayee(payee);
	tab.setModule(Constants.MODULE_REQUEST_MONEY);
	try {
	    if (isRealTimeTxn()) {
		if (handleTransaction(tab))
		    setResponsePage(new RequestMoneyConfirmPage(tab,
			    Boolean.TRUE));
	    } else {
		Customer payerCustomer = getCustomerByIdentification(
			Constants.IDENT_TYPE_MSISDN,
			pn.getInternationalFormat());
		if (!PortalUtils.exists(payerCustomer)) {
		    LOG.info("# Unknown Payee");
		    error(getLocalizer().getString(
			    "requestMoney.nonreal.txn.payer.not.found", this));
		    return;
		}
		tab.setCreditAmount(tab.getAmount().getValue());
		setResponsePage(new RequestMoneyConfirmPage(tab, Boolean.FALSE));
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred during Preauthorization", e);
	    error(getLocalizer().getString("preauthorization.error", this));
	    return;
	}

    }

    public void convertAmount() {
	LOG.debug("#RequestMoneyPage.convertAmount()");
	try {
	    amount = FormatUtils.toAmount(amountString,
		    Currency.getInstance(getConfiguration().getCurrency()),
		    getMobiliserWebSession().getLocale());
	} catch (ParseException e) {
	    LOG.error("# could not parse amount [{}]", amountString, e);
	}
    }

    public String getPayer() {
	return payer;
    }

    public void setPayer(String payer) {
	this.payer = payer;
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

    public boolean isRealTimeTxn() {
	return isRealTimeTxn;
    }

    public void setRealTimeTxn(boolean isRealTimeTxn) {
	this.isRealTimeTxn = isRealTimeTxn;
    }

}
