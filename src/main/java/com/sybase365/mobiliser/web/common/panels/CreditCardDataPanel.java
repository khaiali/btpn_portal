package com.sybase365.mobiliser.web.common.panels;

import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdatePaymentInstrumentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdatePaymentInstrumentResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.UpdateWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.CreditCard;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PaymentInstrument;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.consumer.pages.portal.manageaccounts.CreditCardDataPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CreditCardDataPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CreditCardDataPanel.class);

    private WalletEntry walletEntry;
    private Class<? extends BasePage> returnPage;
    private MobiliserBasePage basePage;
    private CustomerBean customer;
    private boolean isForApproval;

    private String oldAlias;
    private String oldCardDisplayNo;
    private String oldCardNo;

    public CreditCardDataPanel(String id, MobiliserBasePage basePage,
	    Class<? extends BasePage> returnPage, WalletEntry walletEntry,
	    CustomerBean customer) {
	super(id);
	this.walletEntry = walletEntry;
	this.isForApproval = walletEntry instanceof PendingWalletEntry;
	this.returnPage = returnPage;
	this.basePage = basePage;
	this.customer = customer;
	if (walletEntry != null) {
	    this.oldAlias = walletEntry.getAlias();
	    this.oldCardDisplayNo = walletEntry.getCreditCard()
		    .getDisplayNumber();
	    this.oldCardNo = walletEntry.getCreditCard().getCardNumber();
	    walletEntry.getCreditCard().setCardNumber(
		    walletEntry.getCreditCard().getDisplayNumber());
	}
	consructPanel();
    }

    protected void consructPanel() {
	Form<?> form = new Form<CreditCardDataPage>("creditCardDataForm",
		new CompoundPropertyModel<CreditCardDataPage>(this));
	form.add(
		new RequiredTextField<String>("walletEntry.alias")
			.add(new PatternValidator(Constants.REGEX_CARD_NICKNAME))
			.setRequired(true).add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add((LocalizableLookupDropDownChoice<Long>) new LocalizableLookupDropDownChoice<Long>(
		"walletEntry.creditCard.cardType", Long.class, "cardtypes",
		this, false, true).setNullValid(true).setRequired(true)
		.add(new ErrorIndicator()));

	form.add(
		new RequiredTextField<String>(
			"walletEntry.creditCard.cardNumber").setRequired(true)
			.add(Constants.hugeStringValidator)
			.add(Constants.hugeSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"walletEntry.creditCard.monthExpiry", Integer.class,
		Constants.RESOURCE_BUNDLE_EXPIRY_MONTH, basePage, true, true)
		.setNullValid(false).setRequired(true));
	form.add(new KeyValueDropDownChoice<Integer, String>(
		"walletEntry.creditCard.yearExpiry", getExpiryYears())
		.setNullValid(false).setRequired(true));
	form.add(
		new TextField<String>("walletEntry.creditCard.securityNumber")
			.add(Constants.smallStringValidator).add(
				Constants.smallSimpleAttributeModifier)).add(
		new ErrorIndicator());
	form.add(
		new RequiredTextField<String>(
			"walletEntry.creditCard.cardHolderName")
			.add(new PatternValidator(
				Constants.REGEX_CARD_HOLDERNAME))
			.setRequired(true).add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	}.setDefaultFormProcessing(false).setVisible(!isForApproval));

	form.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		next();
	    };
	}.setVisible(!isForApproval));

	form.add(new Button("approve") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		basePage.approve(walletEntry, true);
	    };
	}.setDefaultFormProcessing(false).setVisible(isForApproval));

	form.add(new Button("reject") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		basePage.approve(walletEntry, false);
	    };
	}.setDefaultFormProcessing(false).setVisible(isForApproval));

	form.add(new FeedbackPanel("errorMessages"));

	if (isForApproval) {
	    Iterator iter = form.iterator();
	    Component component;
	    for (int i = 0; iter.hasNext(); i++) {
		component = (Component) iter.next();

		if (component instanceof TextField
			|| component instanceof LocalizableLookupDropDownChoice
			|| component instanceof KeyValueDropDownChoice) {
		    component.setEnabled(false);
		    component.add(new SimpleAttributeModifier("readonly",
			    "readonly"));
		    component.add(new SimpleAttributeModifier("style",
			    "background-color: #E6E6E6;"));

		}
	    }
	}

	add(form);

    }

    private void handleBack() {
	setResponsePage(returnPage);
    }

    private boolean validateCreditCard() {
	boolean valid = true;
	int month = walletEntry.getCreditCard().getMonthExpiry() - 1;
	int year = walletEntry.getCreditCard().getYearExpiry();
	LOG.trace("# Date after correction for java.util.Calendar comparison: Month = "
		+ month + " / Year = " + year);

	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(System.currentTimeMillis());

	LOG.trace("# java.util.Calendar date to compare to: Month = "
		+ c.get(Calendar.MONTH) + " / Year = " + c.get(Calendar.YEAR));

	if (year < c.get(Calendar.YEAR)
		|| (month < c.get(Calendar.MONTH) && year <= c
			.get(Calendar.YEAR))) {

	    LOG.trace("# Date check failed!");
	    LOG.trace("# Minimum Month = " + c.get(Calendar.MONTH)
		    + " / Actual Month = " + month);
	    LOG.trace("# Minimum Year = " + c.get(Calendar.YEAR)
		    + " / Actual Year = " + year);

	    error(new StringBuffer()
		    .append("'")
		    .append(walletEntry.getCreditCard().getMonthExpiry())
		    .append("/")
		    .append(year)
		    .append("' ")
		    .append(getLocalizer().getString(
			    "creditcard.edit.error.past", this).toString()));
	    valid = false;
	}
	if (!PortalUtils.isLuhnCheck(walletEntry.getCreditCard()
		.getCardNumber())) {
	    error("'"
		    + walletEntry.getCreditCard().getCardNumber()
		    + "'"
		    + getLocalizer().getString(
			    "creditcard.number.invalid.error", this));
	    valid = false;
	}
	return valid;
    }

    private void next() {
	// check expiry date
	// -1 to correct to 0-based month counting according to
	// Calendar.get(Calendar.MONTH)
	if (validateCreditCard()) {
	    walletEntry.getCreditCard().setCustomerId(customer.getId());
	    walletEntry.getCreditCard().setCurrency(
		    basePage.getConfiguration().getCurrency());
	    CreditCard card = Converter.getInstance().getCreditCard(
		    customer.getId(), walletEntry);
	    try {
		if (!card.getCardNumber().equals(oldCardDisplayNo))
		    card.setCardNumber(basePage.encrypt(card.getCardNumber(),
			    basePage.getConfiguration().getCreditCardKeyAlias()));
		else
		    card.setCardNumber(oldCardNo);
	    } catch (KeyStoreException e) {
		LOG.error("# An error occurred while enrypting credit card", e);
		error(getLocalizer().getString("credit.card.error", this));
		return;
	    }
	    if (walletEntry.getId() == null) {
		try {
		    Long weId = basePage.createWallet(walletEntry.getAlias(),
			    card, customer.getId());
		    if (!PortalUtils.exists(weId))
			return;
		    else {
			if (weId.longValue() == -1L) {
			    basePage.getMobiliserWebSession().info(
				    getLocalizer().getString(
					    "pendingApproval.msg", this));

			} else
			    LOG.debug("# Successfully created WalletEntry");

			setResponsePage(returnPage);
		    }

		} catch (Exception e) {
		    LOG.error("# An error occurred while creating credit card",
			    e);
		    error(getLocalizer().getString("create.credit.card.error",
			    this));
		}
	    } else {
		try {
		    if (updatePaymentInstrument(card))
			LOG.debug("# Successfully updated Credit Card");
		    else
			return;
		    if (!oldAlias.equals(walletEntry.getAlias())) {
			if (updateWalletEntry(walletEntry))
			    LOG.debug("# Successfully updated WalletEntry");
			else
			    return;
		    }
		    setResponsePage(returnPage);
		} catch (Exception e) {
		    LOG.error("# An error occurred while updating credit card",
			    e);
		    error(getLocalizer().getString("update.credit.card.error",
			    this));

		}
	    }
	}
    }

    private boolean updatePaymentInstrument(PaymentInstrument pi)
	    throws Exception {
	UpdatePaymentInstrumentRequest updatePiRequest = basePage
		.getNewMobiliserRequest(UpdatePaymentInstrumentRequest.class);
	updatePiRequest.setPaymentInstrument(pi);
	UpdatePaymentInstrumentResponse updatePiResponse = basePage.wsWalletClient
		.updatePaymentInstrument(updatePiRequest);
	if (!basePage.evaluateMobiliserResponse(updatePiResponse))
	    return false;
	return true;

    }

    private boolean updateWalletEntry(WalletEntry walletEntry) throws Exception {
	UpdateWalletEntryRequest updateWeRequest = basePage
		.getNewMobiliserRequest(UpdateWalletEntryRequest.class);
	walletEntry.setCreditCard(null);
	updateWeRequest.setWalletEntry(walletEntry);
	UpdateWalletEntryResponse upWalletResp = basePage.wsWalletClient
		.updateWalletEntry(updateWeRequest);

	if (!basePage.evaluateMobiliserResponse(upWalletResp))
	    return false;

	return true;

    }

    public List<KeyValue<Integer, String>> getExpiryYears() {
	LOG.debug("# MobiliserBasePage.getExpiryYears()");
	List<KeyValue<Integer, String>> EXPIRY_YEAR = new ArrayList<KeyValue<Integer, String>>();

	Calendar cal = Calendar.getInstance();
	int currentYear = cal.get(Calendar.YEAR);
	int expiryYear = currentYear + 4;
	int year = currentYear;
	int i = 1;
	while (year <= expiryYear) {
	    EXPIRY_YEAR.add(new KeyValue<Integer, String>(
		    Integer.valueOf(year), getLocalizer().getString(
			    String.valueOf(year), this)));
	    year++;
	    i++;
	}
	return EXPIRY_YEAR;
    }

    public void setWalletEntry(WalletEntry walletEntry) {
	this.walletEntry = walletEntry;
    }

}
