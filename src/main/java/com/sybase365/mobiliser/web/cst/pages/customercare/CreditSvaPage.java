package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;

public class CreditSvaPage extends CustomerCareMenuGroup {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CreditSvaPage.class);

    private TransactionBean txnBean;
    private String creditAmount;
    private String text;
    private String orderId;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final Form<?> form = new Form("creditSvaForm",
		new CompoundPropertyModel<CreditSvaPage>(this));
	form.add(new RequiredTextField<String>("creditAmount")
		.setRequired(true)
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("text")
		.add(new PatternValidator(Constants.REGEX_TXN_TEXT))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("orderId")
		.add(new PatternValidator(Constants.REGEX_ORDERID))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Button("submit") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		creditSva();
	    }
	});
	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(ManageAccountPage.class);
	    }
	}.setDefaultFormProcessing(false));
	add(form);
    }

    protected void creditSva() {
	txnBean = new TransactionBean();
	try {
	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(Constants.DEFAULT_CURRENCY);
	    vatAmnt.setValue(convertAmountToLong(creditAmount));
	    txnBean.setAmount(vatAmnt);
	    txnBean.setAutoCapture(true);
	    txnBean.setUsecase(getConfiguration().getUseCaseCashIn());
	    txnBean.setText(getText());
	    txnBean.setOrderChannel(Constants.ORDER_CHANNEL_WEB);
	    txnBean.setOrderId(getOrderId());
	    // set payer (logged-in agent)
	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier idPayer = new Identifier();
	    idPayer.setType(Constants.IDENT_TYPE_CUST_ID);
	    // load customer id and piId from prefs
	    idPayer.setValue(Long.toString(getConfiguration()
		    .getCreditDebitCustomerId()));
	    payer.setIdentifier(idPayer);
	    payer.setPaymentInstrumentId(getConfiguration()
		    .getCreditDebitPiId());
	    txnBean.setPayer(payer);
	    // set payee (money customer)
	    TransactionParticipant payee = new TransactionParticipant();
	    Identifier idPayee = new Identifier();
	    idPayee.setType(Constants.IDENT_TYPE_CUST_ID);
	    idPayee.setValue(Long.toString(getMobiliserWebSession()
		    .getCustomer().getId()));
	    payee.setIdentifier(idPayee);
	    payee.setPaymentInstrumentId(getSvaPI(
		    getMobiliserWebSession().getCustomer().getId())
		    .getPaymentInstrumentId());
	    txnBean.setPayee(payee);
	    if (!handleAuthorisation(txnBean)) {
		if (txnBean.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    getSession().info(
			    getLocalizer().getString("pendingApproval.msg",
				    this));
		    setResponsePage(ManageAccountPage.class);
		} else
		    return;
	    } else {
		getSession().info(
			getLocalizer().getString("credit.sva.success", this));
		setResponsePage(ManageAccountPage.class);
	    }
	} catch (Exception e) {
	    LOG.error("Credit of SVA falied. ", e);
	    error(getLocalizer().getString("credit.sva.error", this));
	}

    }

    @Override
    protected Class getActiveMenu() {
	return ManageAccountPage.class;
    }

    public void setTxnBean(TransactionBean txnBean) {
	this.txnBean = txnBean;
    }

    public TransactionBean getTxnBean() {
	return txnBean;
    }

    public String getAmountString() {
	return creditAmount;
    }

    public void setAmountString(String amountString) {
	this.creditAmount = amountString;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(String orderId) {
	this.orderId = orderId;
    }
}
