package com.sybase365.mobiliser.web.common.panels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;

public abstract class OpenBillPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(OpenBillPanel.class);

    Long customerId;
    MobiliserBasePage basePage;
    Invoice invoice;
    InvoiceConfiguration invoiceConfiguration;
    String invoiceTypeName;

    TransactionBean txBean;

    boolean isPay;
    boolean isOnDemand;

    String billReference;
    String amountString;

    Long piId;

    public OpenBillPanel(String id, Long customerId,
	    MobiliserBasePage basePage,
	    InvoiceConfiguration invoiceConfiguration, String invoiceTypeName,
	    Invoice invoice, TransactionBean txBean, boolean isPay,
	    boolean isOnDemand) {
	super(id);
	this.customerId = customerId;
	this.basePage = basePage;
	this.invoice = invoice;
	this.invoiceConfiguration = invoiceConfiguration;
	this.invoiceTypeName = invoiceTypeName;

	this.txBean = txBean;
	this.isPay = isPay;
	this.isOnDemand = isOnDemand;

	this.piId = invoiceConfiguration.getDefaultPaymentInstrumentId();
	this.billReference = (invoice == null) ? null : invoice.getReference();
	this.amountString = (invoice == null) ? "" : basePage
		.convertAmountToString(invoice.getAmount());

	constructPanel();
    }

    private void constructPanel() {
	Form form = new Form("openBillForm",
		new CompoundPropertyModel<OpenBillPanel>(this));

	form.add(new Label(
		"status",
		txBean == null ? ""
			: String.valueOf(txBean.getStatusCode())
				+ "-"
				+ ((txBean.getStatusCode() == 0) ? Constants.SERVICE_RETURNED_STATUS_SUCCESS
					: txBean.getStatusString()))
		.setVisible(isStatus()));
	form.add(new Label("transactionId", txBean == null ? "" : txBean
		.getRefTransaction().getSystemId().toString())
		.setVisible(isStatus()));

	form.add(new Label("name", invoiceConfiguration.getAlias()));
	form.add(new Label("type", invoiceTypeName));
	form.add(new Label("reference", invoiceConfiguration.getReference()));

	form.add(new Label("billReferenceLabel", invoice == null ? "" : invoice
		.getReference()).setVisible(!isPayOnDemand()));

	RequiredTextField billReferenceInput = new RequiredTextField(
		"billReference");
	billReferenceInput.add(Constants.mediumStringValidator);
	billReferenceInput.setVisible(isPayOnDemand());
	form.add(billReferenceInput);

	form.add(new Label("date", invoice == null ? "" : new SimpleDateFormat(
		"MM/dd/yyyy").format((invoice.getDate() == null) ? new Date()
		: invoice.getDate().toGregorianCalendar().getTime()))
		.setVisible(!isPayOnDemand()));

	form.add(new Label("amountLabel", invoice == null ? "" : basePage
		.convertAmountToString(invoice.getAmount())
		+ " "
		+ invoice.getCurrency()).setVisible(!isPayOnDemand()));

	RequiredTextField<String> amountInput = new RequiredTextField<String>(
		"amountString");
	amountInput.add(new AmountValidator(basePage,
		Constants.REGEX_AMOUNT_16_2));
	amountInput.add(Constants.amountSimpleAttributeModifier);
	amountInput.add(new ErrorIndicator());
	amountInput.setVisible(isPayOnDemand());
	form.add(amountInput);

	form.add(new Label(
		"currency",
		invoice == null ? getCurrencyFromInvoiceType(invoiceConfiguration
			.getInvoiceTypeId()) : invoice.getCurrency()));
	form.add(new KeyValueDropDownChoice<Long, String>("piId",
		getPaymentInstruments()).setNullValid(false)
		.setVisible(isPay()));
	form.add(new Label("fee", txBean == null ? "0" : String.valueOf(txBean
		.getPayerFee())
		+ " "
		+ (invoice == null ? "" : invoice.getCurrency()))
		.setVisible(isConfirm()));

	form.add(new Button("pay") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handlePay();

	    }
	}.setVisible(isPay()));

	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleCancel();
	    }

	}.setDefaultFormProcessing(false).setVisible(isCancel()));

	form.add(new Button("confirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleConfirm();
	    }

	}.setDefaultFormProcessing(false).setVisible(isConfirm()));

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false).setVisible(
		!isConfirm() && !isStatus()));

	form.add(new Button("cancelAction") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    }

	}.setDefaultFormProcessing(false).setVisible(isConfirm()));

	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    }

	}.setDefaultFormProcessing(false).setVisible(isStatus()));

	form.add(new FeedbackPanel("errorMessages"));

	add(form);
    }

    private List<KeyValue<Long, String>> getPaymentInstruments() {
	List<KeyValue<Long, String>> keyValueList = new ArrayList<KeyValue<Long, String>>();
	// List<PaymentInstrument> piEntries = basePage
	// .getPaymentInstrumentsByCustomer(customerId);

	List<WalletEntry> walletEntries = basePage.getWalletEntryList(
		customerId, null, null);

	for (WalletEntry wallet : walletEntries) {
	    keyValueList.add(new KeyValue<Long, String>(wallet
		    .getPaymentInstrumentId(),
		    wallet.getAlias() == null ? "SVA" : wallet.getAlias()));
	}

	return keyValueList;

    }

    private String getCurrencyFromInvoiceType(long invoiceTypeId) {
	List<KeyValue<Long, String>> currencyList = basePage
		.getInvoiceTypesCurrencyList(false);
	for (KeyValue<Long, String> kv : currencyList) {
	    if (kv.getKey().longValue() == invoiceTypeId) {
		return kv.getValue();
	    }
	}
	return "";
    }

    protected void handlePay() {

	Long invoiceId = null;

	if (isPayOnDemand()) {

	    // check bill reference

	    String pattern = basePage.getStringFromLongStringList(
		    invoiceConfiguration.getInvoiceTypeId(),
		    basePage.getInvoiceTypesInvoiceRefPatternList(false));
	    if (pattern != null && !getBillReference().matches(pattern)) {
		error(getLocalizer().getString(
			"openBill.billreference.invalid", this));
		return;
	    }

	    // create new invoice
	    try {
		invoiceId = basePage.createInvoice(
			invoiceConfiguration.getInvoiceTypeId(), customerId,
			getBillReference(),
			basePage.convertAmountToLong(getAmountString()));
	    } catch (Exception e) {
		LOG.error("# Failed to create new invoice", e);
		error(getLocalizer().getString("openBill.pay.error", this));
	    }

	    try {
		invoice = basePage.getInvoice(invoiceId);
	    } catch (Exception e) {
		LOG.error("# Failed to get invoice", e);
		error(getLocalizer().getString("openBill.pay.error", this));
	    }

	} else {
	    invoiceId = invoice.getId();
	}
	if (invoiceId != null) {
	    try {
		TransactionBean txBean = new TransactionBean();

		if (basePage.checkPayInvoice(invoiceId, txBean)) {
		    setResponsePage(getConfirmPage(invoice, txBean));
		} else {
		    LOG.error("# Check pay invoice returned false.");
		}
	    } catch (Exception e) {
		LOG.error("# Failed to check pay invoice", e);
		error(getLocalizer().getString("openBill.pay.error", this));
	    }
	}
    };

    protected void handleCancel() {
	try {
	    if (basePage.cancelPayInvoice(invoice.getId())) {
		setResponsePage(getBillPaymentListPage());
	    } else {
		LOG.error("# Cancel invoice returned false.");
	    }
	} catch (Exception e) {
	    LOG.error("# Failed to cancel invoice", e);
	    error(getLocalizer().getString("openBill.cancel.error", this));
	}

    }

    protected void handleConfirm() {
	try {
	    if (basePage.continuePayInvoice(txBean)) {
		info(getLocalizer().getString("openBill.pay.success", this));
		setResponsePage(getStatusPage(invoice, txBean));
	    } else {
		if (txBean.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    info(getLocalizer().getString("pendingApproval.msg", this));
		    setResponsePage(getStatusPage(invoice, txBean));
		}

		LOG.error("# Pay invoice returned false.");
	    }
	} catch (Exception e) {
	    LOG.error("# Failed to pay invoice", e);
	    error(getLocalizer().getString("openBill.pay.error", this));
	}

    }

    protected void handleBack() {
	setResponsePage(getBillPaymentListPage());
    }

    public Long getPiId() {
	return piId;
    }

    public void setPiId(Long piId) {
	this.piId = piId;
    }

    public String getBillReference() {
	return billReference;
    }

    public void setBillReference(String billReference) {
	this.billReference = billReference;
    }

    public String getAmountString() {
	return amountString;
    }

    public void setAmountString(String amountString) {
	this.amountString = amountString;
    }

    private boolean isPay() {
	return isPay && (txBean == null);
    }

    private boolean isConfirm() {
	return isPay && (txBean != null);
    }

    private boolean isStatus() {
	return !isPay && (txBean != null);
    }

    private boolean isCancel() {
	return !isPay && (txBean == null);
    }

    private boolean isPayOnDemand() {
	return isPay() && isOnDemand;
    }

    public abstract MobiliserBasePage getBillPaymentListPage();

    public abstract MobiliserBasePage getConfirmPage(Invoice invoice,
	    TransactionBean txBean);

    public abstract MobiliserBasePage getStatusPage(Invoice invoice,
	    TransactionBean txBean);
}
