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
public class CashOutPage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CashOutPage.class);

    private String amountString;

    private TransactionBean txnBean;
    private String orderId;

    // container instances
    private WebMarkupContainer cashOutDiv;
    private WebMarkupContainer cashOutConfirmDiv;
    private WebMarkupContainer cashOutFinishDiv;

    public CashOutPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public CashOutPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	this.cashOutDiv = addCashOutDiv();
	this.cashOutConfirmDiv = addCashOutConfirmDiv();
	this.cashOutFinishDiv = addCashOutFinishDiv();
	setContainerVisibilities(true, false, false);

	addOrReplace(new Label("cashOut.title", getLocalizer().getString(
		"cashOut.title", this)));
    }

    private WebMarkupContainer addCashOutDiv() {
	WebMarkupContainer cashOutDiv = new WebMarkupContainer("cashOutDiv");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("cashOutForm",
		new CompoundPropertyModel<CashOutPage>(this));
	form.add(new RequiredTextField<String>("amountString")
		.setRequired(true)
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("orderId").add(
		Constants.mediumStringValidator).add(
		Constants.mediumSimpleAttributeModifier));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Button("cashOutNext") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cashOutPreAuth();
	    }
	});
	cashOutDiv.add(form);
	add(cashOutDiv);

	return cashOutDiv;
    }

    private WebMarkupContainer addCashOutConfirmDiv() {
	WebMarkupContainer cashOutConfirmDiv = new WebMarkupContainer(
		"cashOutConfirmDiv");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("cashOutConfirmForm",
		new CompoundPropertyModel<CashOutPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Label("creditAmount"));
	form.add(new Label("feeAmount"));
	form.add(new Label("debitAmount"));
	form.add(new Label("msisdn"));
	form.add(new Label("orderId"));
	form.add(new Button("cashOutBack") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setTxnBean(null);
		setResponsePage(CashOutPage.class);
	    }
	});
	form.add(new Button("cashOutConfirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cashOutConfirm();
	    }
	});
	cashOutConfirmDiv.add(form);
	add(cashOutConfirmDiv);

	return cashOutConfirmDiv;
    }

    private WebMarkupContainer addCashOutFinishDiv() {
	WebMarkupContainer cashOutFinishDiv = new WebMarkupContainer(
		"cashOutFinishDiv");
	cashOutFinishDiv.setOutputMarkupId(true);
	cashOutFinishDiv.setOutputMarkupPlaceholderTag(true);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("cashOutFinishForm",
		new CompoundPropertyModel<CashOutPage>(this));
	form.add(new FeedbackPanel("errorMessages"));

	WebMarkupContainer labels = new WebMarkupContainer("dataContainer");

	labels.add(new Label("debitAmount"));
	labels.add(new Label("msisdn"));
	labels.add(new Label("txnBean.txnId"));
	labels.add(new Label("txnBean.authCode"));
	form.add(labels);

	Label successLbl = new Label("successMsg", getLocalizer().getString(
		"cashOutFinish.success", this));

	cashOutFinishDiv.add(successLbl);

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	cashOutFinishDiv.add(pendingLbl);

	if (PortalUtils.exists(getTxnBean())
		&& getTxnBean().getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successLbl.setVisible(false);
	} else
	    pendingLbl.setVisible(false);

	form.add(new Button("cashOutFinished") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(CustomerDetailsPage.class);
	    }
	});
	cashOutFinishDiv.add(form);
	addOrReplace(cashOutFinishDiv);

	return cashOutFinishDiv;
    }

    private void setContainerVisibilities(boolean viewCashOutDiv,
	    boolean viewCashOutConfirmDiv, boolean viewCashOutFinishDiv) {
	this.cashOutDiv.setVisible(viewCashOutDiv);
	this.cashOutConfirmDiv.setVisible(viewCashOutConfirmDiv);
	if (viewCashOutFinishDiv) {
	    refreshSVABalance();
	}
	this.cashOutFinishDiv.setVisible(viewCashOutFinishDiv);
    }

    protected void cashOutPreAuth() {

	try {
	    Customer customer = getMobiliserWebSession().getLoggedInCustomer();
	    CustomerBean agent = getMobiliserWebSession().getCustomer();

	    this.txnBean = new TransactionBean();
	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(Constants.DEFAULT_CURRENCY);
	    vatAmnt.setValue(convertAmountToLong(amountString));

	    // set payee
	    TransactionParticipant payee = new TransactionParticipant();
	    Identifier payeeIdent = new Identifier();
	    payeeIdent.setType(Constants.IDENT_TYPE_CUST_ID);
	    payeeIdent.setValue(Long.toString(customer.getCustomerId()));
	    payee.setIdentifier(payeeIdent);
	    this.txnBean.setPayee(payee);

	    // set payer
	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier payerIdent = new Identifier();
	    payerIdent.setType(Constants.IDENT_TYPE_CUST_ID);
	    payerIdent.setValue(Long.toString(agent.getId()));
	    payer.setIdentifier(payerIdent);
	    this.txnBean.setPayer(payer);

	    this.txnBean.setAmount(vatAmnt);
	    this.txnBean.setAutoCapture(true);
	    this.txnBean.setUsecase(getConfiguration().getUseCaseCashOut());
	    this.txnBean.setPayer(payer);
	    this.txnBean.setPayee(payee);
	    this.txnBean.setOrderChannel(Constants.ORDER_CHANNEL_WEB);
	    txnBean.setOrderId(getOrderId());
	    if (handleTransaction(getTxnBean())) {
		setContainerVisibilities(false, true, false);
		addOrReplace(new Label("cashOut.title", getLocalizer()
			.getString("cashOut.title", this)
			+ getLocalizer().getString(
				"application.breadcrumb.separator", this)
			+ getLocalizer()
				.getString("cashOutConfirm.title", this)));
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during preauthorization", e);
	    error(getLocalizer().getString("preauthorization.error", this));
	}
    }

    protected void cashOutConfirm() {

	try {
	    // AuthorisationResponse aurhResp = handleAuthorisation();

	    if (handleTransaction(getTxnBean())) {
		setContainerVisibilities(false, false, true);
		addOrReplace(new Label("cashOut.title", getLocalizer()
			.getString("cashOut.title", this)
			+ getLocalizer().getString(
				"application.breadcrumb.separator", this)
			+ getLocalizer().getString("cashOutFinish.title", this)));
	    } else {
		if (getTxnBean().getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    addCashOutFinishDiv();
		    setContainerVisibilities(false, false, true);
		}
	    }

	} catch (Exception e) {
	    LOG.error("# Error in cashOut Authorisation", e);
	    error(getLocalizer().getString("preauthorization.continue.error",
		    this));
	}
    }

    public String getMsisdn() {
	return getMobiliserWebSession().getCustomer().getMsisdn();
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

    public long getTxnId() {

	return getTxnBean() != null ? getTxnBean().getTxnId() : 0L;
    }

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(String orderId) {
	this.orderId = orderId;
    }

}
