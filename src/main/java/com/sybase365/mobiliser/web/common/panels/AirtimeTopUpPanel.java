package com.sybase365.mobiliser.web.common.panels;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.SVA;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.application.pages.BaseApplicationPage;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class AirtimeTopUpPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AirtimeTopUpPanel.class);

    private Class<? extends BasePage> returnPage;
    private BaseApplicationPage basePage;

    private Customer payeeCustomer;
    private Long customerId;
    private int customerTypeId;
    private String amountString;
    private long txnAmount;
    private String recipient;;
    private long operator;

    private KeyValueDropDownChoice<Long, String> invoiceTypesList;

    private TransactionBean txnBean = new TransactionBean();

    private WebMarkupContainer topUpDiv;

    private WebMarkupContainer topUpConfirmDiv;

    private WebMarkupContainer topUpFinishDiv;

    public AirtimeTopUpPanel(String id, BaseApplicationPage basePage,
	    Class<? extends BasePage> returnPage, Long customerId,
	    int customerTypeId, String recipient) {
	super(id);
	this.returnPage = returnPage;
	this.basePage = basePage;
	this.customerId = customerId;
	this.customerTypeId = customerTypeId;
	this.recipient = recipient;
	constructPanel();
    }

    public long getOperator() {
	return operator;
    }

    public void setOperator(long operator) {
	this.operator = operator;
    }

    public TransactionBean getTxnBean() {
	return txnBean;
    }

    public void setTxnBean(TransactionBean txnBean) {
	this.txnBean = txnBean;
    }

    private void constructPanel() {
	this.topUpDiv = addTopUpDiv();
	this.topUpConfirmDiv = addTopUpConfirmDiv();
	this.topUpFinishDiv = addTopUpFinishDiv();
	setContainerVisibilities(true, false, false);
	addOrReplace(new Label("airtimeTopup.title", getLocalizer().getString(
		"airtimeTopup.title", this)));

    }

    private WebMarkupContainer addTopUpDiv() {
	topUpDiv = new WebMarkupContainer("topUpDiv");
	Form<?> form = new Form("topUpDataForm",
		new CompoundPropertyModel<AirtimeTopUpPanel>(this));

	invoiceTypesList = (KeyValueDropDownChoice<Long, String>) new KeyValueDropDownChoice<Long, String>(
		"operator", basePage.getInvoiceTypesList()).setNullValid(true)
		.setRequired(true);

	form.add(invoiceTypesList);

	form.add(new RequiredTextField<String>("amountString")
		.setRequired(true)
		.add(new AmountValidator(basePage, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new Label("currency", basePage.getCurrencySymbol()));

	TextField recipient = new TextField<String>("recipient");

	form.add(recipient
		.add(new PatternValidator(Constants.REGEX_PHONE_NUMBER))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	Label reqMSISDN = new Label("reqMSISDN", "*");
	form.add(reqMSISDN);
	if (customerTypeId != Constants.CONSUMER_IDTYPE) {
	    recipient.setRequired(true);
	    reqMSISDN.setVisible(true);
	} else {
	    recipient.setRequired(false);
	    reqMSISDN.setVisible(false);
	}

	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cancel();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button("next") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		topUpNext();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));

	topUpDiv.add(form);
	add(topUpDiv);
	return topUpDiv;
    }

    protected void cancel() {
	setResponsePage(returnPage);

    }

    protected void topUpNext() {
	LOG.debug("#AirtimeTopUpPanel.topUpNext()");
	try {
	    txnAmount = basePage.convertAmountToLong(amountString);
	    com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = basePage
		    .getMobiliserWebSession().getLoggedInCustomer();

	    // If the msisdn is not specified getting the details of logged in
	    // user
	    if (recipient == null) {
		recipient = basePage.getIdentificationByCustomer(
			loggedInCustomer.getCustomerId(),
			Constants.IDENT_TYPE_MSISDN).getIdentification();
	    }

	    PhoneNumber pn = new PhoneNumber(recipient, basePage
		    .getConfiguration().getCountryCode());

	    payeeCustomer = basePage.getCustomerByIdentification(
		    Constants.IDENT_TYPE_MSISDN, pn.getInternationalFormat());

	    if (!PortalUtils.exists(payeeCustomer)) {
		LOG.warn("# Payee with given MSISDN does not exist or Payee details could not be properly loaded");
		error(getLocalizer().getString(
			"airtimeTopup.payee.not.found.error", this));
		return;
	    }

	    Long invoiceId = basePage.createInvoice(getOperator(), customerId,
		    pn.getInternationalFormat(), txnAmount);
	    txnBean = new TransactionBean();
	    VatAmount vatAmnt = new VatAmount();
	    SVA sva = null;
	    try {
		WalletEntry wallet = basePage.getSvaPI(payeeCustomer.getId());
		if (PortalUtils.exists(wallet))
		    sva = wallet.getSva();
		if (PortalUtils.exists(sva)) {
		    vatAmnt.setCurrency(PortalUtils.exists(sva.getCurrency()) ? sva
			    .getCurrency() : basePage.getConfiguration()
			    .getCurrency());
		} else {
		    vatAmnt.setCurrency(basePage.getConfiguration()
			    .getCurrency());
		}

	    } catch (Exception e1) {
		LOG.error("# Error while getting SVA's payment instrument", e1);
	    }

	    vatAmnt.setValue(txnAmount);
	    txnBean.setAmount(vatAmnt);

	    if (basePage.checkPayInvoice(invoiceId, txnBean)) {
		setContainerVisibilities(false, true, false);
		addOrReplace(new Label("airtimeTopup.title", getLocalizer()
			.getString("airtimeTopup.title", this)
			+ getLocalizer().getString(
				"application.breadcrumb.separator", this)
			+ getLocalizer().getString("airtimeTopupConfirm.title",
				this)));

	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during Preauthorization", e);
	    error(getLocalizer().getString("preauthorization.error", this));
	}

    }

    private WebMarkupContainer addTopUpConfirmDiv() {
	topUpConfirmDiv = new WebMarkupContainer("topUpConfirmDiv");
	Form<?> form = new Form("topUpConfirmForm",
		new CompoundPropertyModel<AirtimeTopUpPanel>(this));

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		back();
	    };
	});
	form.add(new Button("confirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		confirm();
	    };
	});
	form.add(new FeedbackPanel("errorMessages"));
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<AirtimeTopUpPanel>(this));
	labels.add(new Label("debitAmount"));
	labels.add(new Label("feeAmount"));
	labels.add(new Label("creditAmount"));
	labels.add(new Label("recipient"));
	form.add(labels);
	topUpConfirmDiv.add(form);
	add(topUpConfirmDiv);
	return topUpConfirmDiv;
    }

    protected void confirm() {
	LOG.debug("#AirtimeTopUpPanel.confirm()");
	try {
	    if (basePage.continuePayInvoice(getTxnBean())) {
		setContainerVisibilities(false, false, true);
		addOrReplace(new Label("airtimeTopup.title", getLocalizer()
			.getString("airtimeTopup.title", this)
			+ getLocalizer().getString(
				"application.breadcrumb.separator", this)
			+ getLocalizer().getString("airtimeTopupFinish.title",
				this)));
	    } else {
		if (getTxnBean().getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    addTopUpFinishDiv();
		    setContainerVisibilities(false, false, true);
		    addOrReplace(new Label("airtimeTopup.title", getLocalizer()
			    .getString("airtimeTopup.title", this)
			    + getLocalizer().getString(
				    "application.breadcrumb.separator", this)
			    + getLocalizer().getString(
				    "airtimeTopupFinish.title", this)));
		}
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred during preauthorization continue", e);
	    error(getLocalizer().getString("preauthorization.continue.error",
		    this));

	}

    }

    protected void back() {
	setContainerVisibilities(true, false, false);
	addOrReplace(new Label("airtimeTopup.title", getLocalizer().getString(
		"airtimeTopup.title", this)));
    }

    private WebMarkupContainer addTopUpFinishDiv() {
	LOG.debug("#AirtimeTopUpPanel.addTopUpFinishDiv()");
	topUpFinishDiv = new WebMarkupContainer("topUpFinishDiv");
	topUpFinishDiv.setOutputMarkupId(true);
	topUpFinishDiv.setOutputMarkupPlaceholderTag(true);

	Form<?> form = new Form("topUpFinishForm",
		new CompoundPropertyModel<AirtimeTopUpPanel>(this));
	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cancel();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<AirtimeTopUpPanel>(this));
	labels.add(new Label("creditAmount"));
	labels.add(new Label("recipient"));
	labels.add(new Label("txnBean.txnId"));
	labels.add(new Label("txnBean.authCode"));
	form.add(labels);

	Label successLbl = new Label("successMsg", getLocalizer().getString(
		"airtimeTopupFinish.success", this));

	topUpFinishDiv.add(successLbl);

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	topUpFinishDiv.add(pendingLbl);

	if (PortalUtils.exists(getTxnBean())
		&& getTxnBean().getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successLbl.setVisible(false);
	} else
	    pendingLbl.setVisible(false);

	topUpFinishDiv.add(form);
	addOrReplace(topUpFinishDiv);
	return topUpFinishDiv;
    }

    private void setContainerVisibilities(boolean viewTopUpDiv,
	    boolean viewTopUpConfirmDiv, boolean viewTopUpFinishDiv) {
	this.topUpDiv.setVisible(viewTopUpDiv);
	this.topUpConfirmDiv.setVisible(viewTopUpConfirmDiv);
	if (viewTopUpFinishDiv) {
	    basePage.refreshSVABalance();
	}
	this.topUpFinishDiv.setVisible(viewTopUpFinishDiv);
    }

    public String getAmountString() {
	return amountString;
    }

    public void setAmountString(String strAmount) {
	this.amountString = strAmount;
    }

    public String getCreditAmount() {
	return basePage.convertAmountToStringWithCurrency(txnBean
		.getCreditAmount());
    }

    public String getDebitAmount() {
	return basePage.convertAmountToStringWithCurrency(txnBean
		.getDebitAmount());
    }

    public String getFeeAmount() {
	return basePage.convertAmountToStringWithCurrency(txnBean
		.getFeeAmount());
    }

}
