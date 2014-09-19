package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;

public abstract class BillConfigurationPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BillConfigurationPanel.class);

    Long customerId;
    MobiliserBasePage basePage;
    InvoiceConfiguration ic;
    boolean isAdd = false;

    String name;
    Long typeId;
    String reference;
    Long piId;

    public BillConfigurationPanel(String id, Long customerId,
	    MobiliserBasePage basePage, InvoiceConfiguration ic) {
	super(id);
	this.customerId = customerId;
	this.basePage = basePage;

	if (ic == null) {
	    this.isAdd = true;
	} else {
	    this.isAdd = false;
	    this.ic = ic;
	    this.name = ic.getAlias();
	    this.typeId = ic.getInvoiceTypeId();
	    this.reference = ic.getReference();
	    this.piId = ic.getDefaultPaymentInstrumentId();
	}

	constructPanel();
    }

    private void constructPanel() {
	Form form = new Form("billConfigurationForm",
		new CompoundPropertyModel<BillConfigurationPanel>(this));

	form.add(new KeyValueDropDownChoice<Long, String>("typeId", basePage
		.getCustomerAddsInvoiceTypes(false)).setNullValid(false)
		.setRequired(true).setEnabled(isAdd ? true : false)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("name")
		.add(new PatternValidator(Constants.REGEX_BILL_NAME))
		.add(Constants.fromThreeToMediumStringValidator)
		.add(new ErrorIndicator()));
	form.add((new RequiredTextField<String>("reference").add(
		new PatternValidator(Constants.REGEX_TXN_TEXT)).add(
		Constants.fromThreeToMediumStringValidator)
		.setEnabled(isAdd ? true : false)).add(new ErrorIndicator()));

	form.add(new KeyValueDropDownChoice<Long, String>("piId",
		getPaymentInstruments()).setNullValid(true));

	form.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSave();
	    };
	});

	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new FeedbackPanel("errorMessages"));

	add(form);

    }

    private void handleBack() {
	setResponsePage(getBillConfigurationListPage());
    }

    private void handleSave() {
	if (isAdd) {
	    String pattern = basePage.getStringFromLongStringList(getTypeId(),
		    basePage.getInvoiceTypesConfigRefPatternList(false));
	    if (pattern != null && !reference.matches(pattern)) {
		error(getLocalizer().getString(
			"billconfiguration.reference.invalid", this));
		return;
	    }

	    InvoiceConfiguration newIc = new InvoiceConfiguration();
	    newIc.setInvoiceTypeId(getTypeId());
	    newIc.setAlias(getName());
	    newIc.setReference(getReference());
	    newIc.setDefaultPaymentInstrumentId(getPiId());
	    newIc.setCustomerId(customerId);
	    newIc.setStatus(0);
	    newIc.setActive(true);

	    try {
		basePage.createInvoiceConfiguration(newIc);
	    } catch (Exception e) {
		LOG.error("# Failed to create new bill configuration", e);
		error(getLocalizer().getString(
			"billconfiguration.create.error", this));
	    }
	} else {
	    ic.setAlias(getName());
	    ic.setDefaultPaymentInstrumentId(getPiId());
	    try {
		basePage.updateInvoiceConfiguration(ic);
	    } catch (Exception e) {
		LOG.error("# Failed to update bill configuration", e);
		error(getLocalizer().getString(
			"billconfiguration.update.error", this));
	    }
	}
	setResponsePage(getBillConfigurationListPage());
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

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Long getTypeId() {
	return typeId;
    }

    public void setType(Long typeId) {
	this.typeId = typeId;
    }

    public String getReference() {
	return reference;
    }

    public void setReference(String reference) {
	this.reference = reference;
    }

    public Long getPiId() {
	return piId;
    }

    public void setPiId(Long piId) {
	this.piId = piId;
    }

    public abstract MobiliserBasePage getBillConfigurationListPage();

}
