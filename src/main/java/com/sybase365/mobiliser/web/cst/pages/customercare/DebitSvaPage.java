package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;

public class DebitSvaPage extends CustomerCareMenuGroup {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(DebitSvaPage.class);

    private TransactionBean txnBean;
    private String debitAmount;
    private String text;
    private String orderId;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final Form<?> form = new Form("debitSvaForm",
		new CompoundPropertyModel<DebitSvaPage>(this));
	form.add(new RequiredTextField<String>("debitAmount").setRequired(true)
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("text").add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier));
	form.add(new TextField<String>("orderId").add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Button("submit") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		debitSva();
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

    protected void debitSva() {
	txnBean = new TransactionBean();
	try {
	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(Constants.DEFAULT_CURRENCY);
	    vatAmnt.setValue(convertAmountToLong(debitAmount));
	    txnBean.setAmount(vatAmnt);
	    txnBean.setAutoCapture(true);
	    txnBean.setUsecase(getConfiguration().getUseCaseCashOut());
	    txnBean.setText(getText());
	    txnBean.setOrderChannel(Constants.ORDER_CHANNEL_WEB);
	    txnBean.setOrderId(getOrderId());
	    // set payee (logged-in agent)
	    // load customer id and piId from prefs
	    TransactionParticipant payee = new TransactionParticipant();
	    Identifier idPayee = new Identifier();
	    idPayee.setType(Constants.IDENT_TYPE_CUST_ID);
	    idPayee.setValue(Long.toString(getConfiguration()
		    .getCreditDebitCustomerId()));
	    payee.setIdentifier(idPayee);
	    payee.setPaymentInstrumentId(getConfiguration()
		    .getCreditDebitPiId());
	    txnBean.setPayee(payee);
	    // set payer (money customer)
	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier idPayer = new Identifier();
	    idPayer.setType(Constants.IDENT_TYPE_CUST_ID);
	    idPayer.setValue(Long.toString(getMobiliserWebSession()
		    .getCustomer().getId()));
	    payer.setIdentifier(idPayer);
	    payer.setPaymentInstrumentId(getSvaPI(
		    getMobiliserWebSession().getCustomer().getId())
		    .getPaymentInstrumentId());
	    txnBean.setPayer(payer);
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
			getLocalizer().getString("debit.sva.success", this));
		setResponsePage(ManageAccountPage.class);
	    }
	} catch (Exception e) {
	    LOG.error("Debit of SVA falied. ", e);
	    error(getLocalizer().getString("debit.sva.error", this));
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
	return debitAmount;
    }

    public void setAmountString(String amountString) {
	this.debitAmount = amountString;
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
