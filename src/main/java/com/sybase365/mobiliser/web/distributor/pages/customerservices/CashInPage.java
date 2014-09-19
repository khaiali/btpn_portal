package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
import com.sybase365.mobiliser.util.tools.wicketutils.security.Customer;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class CashInPage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CashInPage.class);

    private String msisdn;
    private String amountString;
    private String orderId;
    private TransactionBean txnBean;

    // container instances
    private WebMarkupContainer cashInDiv;
    private WebMarkupContainer cashInConfirmDiv;
    private WebMarkupContainer cashInFinishDiv;

    public CashInPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public CashInPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	this.cashInDiv = addCashInDiv();
	this.cashInConfirmDiv = addCashInConfirmDiv();
	this.cashInFinishDiv = addCashInFinishDiv();
	setContainerVisibilities(true, false, false);

	addOrReplace(new Label("cashIn.title", getLocalizer().getString(
		"cashIn.title", this)));
    }

    private WebMarkupContainer addCashInDiv() {
	WebMarkupContainer cashInDiv = new WebMarkupContainer("cashInDiv");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("cashInForm",
		new CompoundPropertyModel<CashInPage>(this));
	form.add(new RequiredTextField<String>("amountString")
		.setRequired(true)
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("orderId").add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Button("cashInNext") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cashInPreAuth();
	    }
	});
	cashInDiv.add(form);
	add(cashInDiv);

	return cashInDiv;
    }

    private WebMarkupContainer addCashInFinishDiv() {
	WebMarkupContainer cashInFinishDiv = new WebMarkupContainer(
		"cashInFinishDiv");
	cashInFinishDiv.setOutputMarkupId(true);
	cashInFinishDiv.setOutputMarkupPlaceholderTag(true);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("cashInFinishForm",
		new CompoundPropertyModel<CashInPage>(this));

	form.add(new FeedbackPanel("errorMessages"));

	WebMarkupContainer labels = new WebMarkupContainer("dataContainer");

	labels.add(new Label("debitAmount"));
	labels.add(new Label("msisdn"));
	labels.add(new Label("txnBean.txnId"));
	labels.add(new Label("txnBean.authCode"));
	form.add(new Button("cashInFinished") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(CustomerDetailsPage.class);
	    }
	});
	form.add(labels);

	Label successLbl = new Label("successMsg", getLocalizer().getString(
		"cashInFinish.success", this));

	cashInFinishDiv.add(successLbl);

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	cashInFinishDiv.add(pendingLbl);

	if (PortalUtils.exists(getTxnBean())
		&& getTxnBean().getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successLbl.setVisible(false);
	} else
	    pendingLbl.setVisible(false);

	cashInFinishDiv.add(form);
	addOrReplace(cashInFinishDiv);

	return cashInFinishDiv;
    }

    private WebMarkupContainer addCashInConfirmDiv() {
	WebMarkupContainer cashInConfirmDiv = new WebMarkupContainer(
		"cashInConfirmDiv");

	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("cashInConfirmForm",
		new CompoundPropertyModel<CashInPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Label("creditAmount"));
	form.add(new Label("feeAmount"));
	form.add(new Label("debitAmount"));
	form.add(new Label("msisdn"));
	form.add(new Label("orderId"));
	form.add(new Button("cashInBack") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setTxnBean(null);
		setResponsePage(CashInPage.class);
	    }
	});
	form.add(new Button("cashInConfirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cashInConfirm();
	    }
	});
	cashInConfirmDiv.add(form);
	add(cashInConfirmDiv);

	return cashInConfirmDiv;
    }

    private void setContainerVisibilities(boolean viewCashInDiv,
	    boolean viewCashInConfirmDiv, boolean viewCashInFinishDiv) {
	this.cashInDiv.setVisible(viewCashInDiv);
	this.cashInConfirmDiv.setVisible(viewCashInConfirmDiv);
	if (viewCashInFinishDiv) {
	    refreshSVABalance();
	}
	this.cashInFinishDiv.setVisible(viewCashInFinishDiv);
    }

    protected void cashInConfirm() {
	try {
	    // AuthorisationResponse aurhResp = handleAuthorisation();
	    if (handleTransaction(getTxnBean())) {
		setContainerVisibilities(false, false, true);
		addOrReplace(new Label("cashIn.title", getLocalizer()
			.getString("cashIn.title", this)
			+ getLocalizer().getString(
				"application.breadcrumb.separator", this)
			+ getLocalizer().getString("cashInFinish.title", this)));
	    } else {
		if (getTxnBean().getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    addCashInFinishDiv();
		    setContainerVisibilities(false, false, true);
		}
	    }

	} catch (Exception e) {
	    LOG.error("# Error in cashIn Authorisation", e);
	    error(getLocalizer().getString("preauthorization.continue.error",
		    this));
	}
    }

    protected void cashInPreAuth() {

	try {
	    CustomerBean customer = getMobiliserWebSession().getCustomer();
	    Customer agent = getMobiliserWebSession().getLoggedInCustomer();

	    this.txnBean = new TransactionBean();
	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(Constants.DEFAULT_CURRENCY);
	    vatAmnt.setValue(convertAmountToLong(amountString));

	    // set payee
	    TransactionParticipant payee = new TransactionParticipant();
	    Identifier payeeIdent = new Identifier();
	    payeeIdent.setType(Constants.IDENT_TYPE_CUST_ID);
	    payeeIdent.setValue(Long.toString(customer.getId()));
	    payee.setIdentifier(payeeIdent);

	    // set payer
	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier payerIdent = new Identifier();
	    payerIdent.setType(Constants.IDENT_TYPE_CUST_ID);
	    payerIdent.setValue(Long.toString(agent.getCustomerId()));
	    payer.setIdentifier(payerIdent);

	    txnBean.setAmount(vatAmnt);
	    txnBean.setAutoCapture(true);
	    txnBean.setUsecase(getConfiguration().getUseCaseCashIn());
	    txnBean.setPayer(payer);
	    txnBean.setPayee(payee);
	    txnBean.setOrderChannel(Constants.ORDER_CHANNEL_WEB);
	    txnBean.setOrderId(getOrderId());
	    // PreAuthorisationResponse preAuthResp =
	    // handlePreAuthorisation(tab);
	    if (handleTransaction(getTxnBean())) {
		setContainerVisibilities(false, true, false);
		addOrReplace(new Label("cashIn.title", getLocalizer()
			.getString("cashIn.title", this)
			+ getLocalizer().getString(
				"application.breadcrumb.separator", this)
			+ getLocalizer().getString("cashInConfirm.title", this)));
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during preauthorization", e);
	    error(getLocalizer().getString("preauthorization.error", this));
	}
    }

    public void setAmountString(String amountString) {
	this.amountString = amountString;
    }

    public String getAmountString() {
	return amountString;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getMsisdn() {
	this.msisdn = getMobiliserWebSession().getCustomer().getMsisdn();
	return this.msisdn;
    }

    public TransactionBean getTxnBean() {
	return txnBean;
    }

    public final void setTxnBean(TransactionBean txnBean) {
	this.txnBean = txnBean;
    }

    public String getFeeAmount() {
	return convertAmountToStringWithCurrency(getTxnBean() != null ? getTxnBean()
		.getFeeAmount() : 0L);
    }

    public String getDebitAmount() {
	return convertAmountToStringWithCurrency(getTxnBean() != null ? getTxnBean()
		.getDebitAmount() : 0L);
    }

    public String getCreditAmount() {

	return convertAmountToStringWithCurrency(getTxnBean() != null ? getTxnBean()
		.getCreditAmount() : 0L);
    }

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(String orderId) {
	this.orderId = orderId;
    }

}
