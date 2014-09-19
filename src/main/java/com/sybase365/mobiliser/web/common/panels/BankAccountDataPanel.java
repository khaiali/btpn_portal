package com.sybase365.mobiliser.web.common.panels;

import java.security.KeyStoreException;
import java.util.Iterator;

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
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BankAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PaymentInstrument;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;

public class BankAccountDataPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BankAccountDataPanel.class);

    private WalletEntry walletEntry;
    private Class<? extends BasePage> returnPage;
    private MobiliserBasePage basePage;
    private int accountType = Constants.PI_TYPE_DEFAULT_BA;
    private CustomerBean customer;
    private boolean isForApproval;

    private String oldAlias;
    private String oldAccountNo;
    private String oldDisplayNumber;
    private PrivilegedBehavior cstPriv;

    public BankAccountDataPanel(String id, MobiliserBasePage basePage,
	    Class<? extends BasePage> returnPage, Integer accountType,
	    WalletEntry walletEntry, CustomerBean customer) {
	super(id);
	this.isForApproval = walletEntry instanceof PendingWalletEntry;
	this.walletEntry = walletEntry;
	if (accountType != null)
	    this.accountType = accountType.intValue();
	this.returnPage = returnPage;
	this.basePage = basePage;
	this.customer = customer;
	cstPriv = new PrivilegedBehavior(basePage, Constants.PRIV_CST_LOGIN);
	if (walletEntry != null) {
	    this.oldAlias = walletEntry.getAlias();
	    this.oldDisplayNumber = walletEntry.getBankAccount()
		    .getDisplayNumber();
	    this.oldAccountNo = walletEntry.getBankAccount().getAccountNumber();
	    walletEntry.getBankAccount().setAccountNumber(
		    walletEntry.getBankAccount().getDisplayNumber());
	}

	constructPanel();
    }

    private void constructPanel() {
	Form<?> form = new Form("bankAccountDataForm",
		new CompoundPropertyModel<BankAccountDataPanel>(this));

	form.add(new RequiredTextField<String>("walletEntry.alias")
		.add(new PatternValidator(Constants.REGEX_BANK_NICKNAME))
		.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>(
		"walletEntry.bankAccount.accountNumber")
		.add(new PatternValidator(Constants.REGEX_ACC_NUMBER))
		.setRequired(true).add(Constants.hugeStringValidator)
		.add(Constants.hugeSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(
		new TextField<String>("walletEntry.bankAccount.bankCode")
			.add(new PatternValidator(Constants.REGEX_BANK_CODE))
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add(
		new TextField<String>("walletEntry.bankAccount.branchCode")
			.add(new PatternValidator(Constants.REGEX_BRANCH_CODE))
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	form.add(
		new TextField<String>(
			"walletEntry.bankAccount.accountHolderName")
			.add(new PatternValidator(
				Constants.REGEX_BANK_HOLDERNAME))
			.setRequired(true).add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());
	
	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"walletEntry.bankAccount.status", Integer.class,
		"bankAccountStatus", this, false, true).setRequired(true).add(
		new ErrorIndicator()));

	// adding the submit buttons
	// the back button should not validate the form's input
	form.add(new Button("cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cancel();
	    };
	}.setDefaultFormProcessing(false).setVisible(!isForApproval));

	form.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		save();
	    };
	}.setVisible(!isForApproval));

	Button approveBtn = new Button("approve") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		basePage.approve(walletEntry, true);
	    };
	};

	approveBtn.setDefaultFormProcessing(false).setVisible(isForApproval);
	approveBtn.add(cstPriv);
	form.add(approveBtn);

	form.add(new Button("reject") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		basePage.approve(walletEntry, false);
	    };
	}.setDefaultFormProcessing(false).setVisible(isForApproval)
		.add(cstPriv));

	form.add(new FeedbackPanel("errorMessages"));

	if (isForApproval) {
	    Iterator iter = form.iterator();
	    Component component;
	    for (int i = 0; iter.hasNext(); i++) {
		component = (Component) iter.next();

		if (component instanceof TextField) {
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

    private void cancel() {
	setResponsePage(returnPage);
    }

    private void save() {
	if (walletEntry.getBankAccount().getType() == 0)
	    walletEntry.getBankAccount().setType(accountType);
	walletEntry.getBankAccount().setCustomerId(customer.getId());
	if (customer.getKvCountry() != null)
	    walletEntry.getBankAccount()
		    .setBankCountry(customer.getKvCountry());
	else
	    walletEntry.getBankAccount().setBankCountry(
		    basePage.getConfiguration().getCountryId());
	walletEntry.getBankAccount().setCurrency(
		basePage.getConfiguration().getCurrency());
	BankAccount bankAccount = Converter.getInstance().getBankAccount(
		walletEntry);
	try {
	    if (!bankAccount.getAccountNumber().equals(oldDisplayNumber))
		bankAccount.setAccountNumber(basePage.encrypt(bankAccount
			.getAccountNumber(), basePage.getConfiguration()
			.getBankAccKeyAlias()));
	    else
		bankAccount.setAccountNumber(oldAccountNo);
	} catch (KeyStoreException e) {
	    LOG.error("# An error occurred while enrypting bank account", e);
	    error(getLocalizer().getString("bank.account.error", this));
	    return;
	}
	if (walletEntry.getId() == null) {
	    try {

		Long weId = basePage.createWallet(walletEntry.getAlias(),
			bankAccount, customer.getId());
		if (weId != null) {
		    if (weId.longValue() == -1L) {
			basePage.getMobiliserWebSession().info(
				getLocalizer().getString("pendingApproval.msg",
					this));

		    } else
			LOG.debug("# Successfully created WalletEntry");
		} else {
		    return;
		}
		setResponsePage(returnPage);
	    } catch (Exception e) {
		LOG.error("# An error occurred while creating WalletEntry", e);
		error(getLocalizer().getString("create.bank.account.error",
			this));
	    }
	} else {
	    try {
		if (updatePaymentInstrument(bankAccount))
		    LOG.debug("# Successfully updated Bank Account");
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
		LOG.error("# An error occurred while updating WalletEntry", e);
		error(getLocalizer().getString("update.bank.account.error",
			this));
	    }
	}
    }

    private boolean updateWalletEntry(WalletEntry walletEntry) throws Exception {
	UpdateWalletEntryRequest updateWeRequest = basePage
		.getNewMobiliserRequest(UpdateWalletEntryRequest.class);
	walletEntry.setBankAccount(null);
	updateWeRequest.setWalletEntry(walletEntry);
	UpdateWalletEntryResponse upWalletResp = basePage.wsWalletClient
		.updateWalletEntry(updateWeRequest);

	if (!basePage.evaluateMobiliserResponse(upWalletResp))
	    return false;

	return true;

    }

    public void setWalletEntry(WalletEntry walletEntry) {
	this.walletEntry = walletEntry;
    }

    private boolean updatePaymentInstrument(PaymentInstrument pi)
	    throws Exception {
	LOG.debug("# BankAccountDataPanel.updatePaymentInstrument(...)");
	UpdatePaymentInstrumentRequest updatePiRequest = basePage
		.getNewMobiliserRequest(UpdatePaymentInstrumentRequest.class);
	updatePiRequest.setPaymentInstrument(pi);
	UpdatePaymentInstrumentResponse updatePiResponse = basePage.wsWalletClient
		.updatePaymentInstrument(updatePiRequest);
	if (!basePage.evaluateMobiliserResponse(updatePiResponse))
	    return false;
	return true;

    }

}
