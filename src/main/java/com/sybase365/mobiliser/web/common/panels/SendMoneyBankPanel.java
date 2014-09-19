package com.sybase365.mobiliser.web.common.panels;

import java.security.KeyStoreException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BankAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PaymentInstrument;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class SendMoneyBankPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SendMoneyBankPanel.class);

    private Class<? extends BasePage> returnPage;
    private MobiliserBasePage basePage;
    private String account;
    private String bankCode;
    private String accountHolder;
    private String amountString;
    private String txnText;
    private Customer payeeCustomer;
    private PaymentInstrument pi;
    private long amount;
    private boolean isFromBankList;
    private Long piId;
    private TransactionBean tab;

    private WebMarkupContainer sendMoneyBankDiv;

    private WebMarkupContainer sendMoneyBankConfirmDiv;

    private WebMarkupContainer sendMoneyBankFinishDiv;

    public SendMoneyBankPanel(String id, MobiliserBasePage basePage,
	    Class<? extends BasePage> returnPage, WalletEntry wallet) {
	super(id);
	this.basePage = basePage;
	this.returnPage = returnPage;
	if (wallet != null) {
	    isFromBankList = Boolean.TRUE;
	    piId = wallet.getPaymentInstrumentId();
	    account = wallet.getBankAccount().getDisplayNumber();
	    bankCode = wallet.getBankAccount().getBankCode();
	    accountHolder = wallet.getBankAccount().getAccountHolderName();
	}
	constructPanel();
    }

    public void constructPanel() {

	this.sendMoneyBankDiv = sendMoneyDiv();
	this.sendMoneyBankConfirmDiv = sendMoneyConfirmDiv();
	this.sendMoneyBankFinishDiv = sendMoneyFinishDiv();
	setContainerVisibilities(true, false, false);

    }

    private WebMarkupContainer sendMoneyDiv() {

	WebMarkupContainer sendMoneyDiv = new WebMarkupContainer(
		"sendMoneyBankDiv");
	Form<?> form = new Form("sendMoneytoBankForm",
		new CompoundPropertyModel<SendMoneyBankPanel>(this));

	form.add(new RequiredTextField<String>("account").setRequired(true)
		.add(new PatternValidator(Constants.REGEX_ACC_NUMBER))
		.add(Constants.hugeStringValidator)
		.add(Constants.hugeSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("bankCode").setRequired(true)
		.add(new PatternValidator(Constants.REGEX_BANK_CODE))
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("accountHolder")
		.add(new PatternValidator(Constants.REGEX_BANK_HOLDERNAME))
		.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new RequiredTextField<String>("amountString")
		.setRequired(true)
		.add(new AmountValidator(basePage, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new Label("currency", basePage.getCurrencySymbol()));

	form.add(new TextArea("txnText", new PropertyModel(this, "txnText"))
		.add(new PatternValidator(Constants.REGEX_TXN_TEXT))
		.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));

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
		handleSubmit();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));
	sendMoneyDiv.add(form);
	add(sendMoneyDiv);
	return sendMoneyDiv;

    }

    private WebMarkupContainer sendMoneyConfirmDiv() {

	WebMarkupContainer sendMoneyConfirmDiv = new WebMarkupContainer(
		"sendMoneyBankConfirmDiv");
	Form<?> form = new Form("sendMoneyBankConfirm",
		new CompoundPropertyModel<SendMoneyBankPanel>(this));

	// adding the submit buttons
	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setContainerVisibilities(true, false, false);
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		sendMoneyConfirm();
	    };
	});

	// initialise all the labels
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<SendMoneyBankPanel>(this));

	labels.add(new Label("creditAmount"));
	labels.add(new Label("feeAmount"));
	labels.add(new Label("debitAmount"));
	labels.add(new Label("account"));
	labels.add(new Label("txnText"));

	form.add(labels);
	form.add(new FeedbackPanel("errorMessages"));
	sendMoneyConfirmDiv.add(form);
	add(sendMoneyConfirmDiv);
	return sendMoneyConfirmDiv;
    }

    private WebMarkupContainer sendMoneyFinishDiv() {

	WebMarkupContainer sendMoneyFinishDiv = new WebMarkupContainer(
		"sendMoneyBankFinishDiv");
	sendMoneyFinishDiv.setOutputMarkupId(true);
	sendMoneyFinishDiv.setOutputMarkupPlaceholderTag(true);

	Form<?> form = new Form("sendMoneyBankFinishForm",
		new CompoundPropertyModel<SendMoneyBankPanel>(this));

	// adding the submit buttons
	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleBack();
	    };
	});

	// initialise all the labels
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<SendMoneyBankPanel>(this));

	labels.add(new Label("amount"));
	labels.add(new Label("account"));
	labels.add(new Label("txnId"));
	labels.add(new Label("authCode"));
	form.add(labels);

	Label successLbl = new Label("successMsg", getLocalizer().getString(
		"sendMoneyBankFinish.success", this));

	sendMoneyFinishDiv.add(successLbl);

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	sendMoneyFinishDiv.add(pendingLbl);

	if (PortalUtils.exists(tab)
		&& tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successLbl.setVisible(false);
	} else
	    pendingLbl.setVisible(false);

	form.add(new FeedbackPanel("errorMessages"));
	sendMoneyFinishDiv.add(form);
	addOrReplace(sendMoneyFinishDiv);
	return sendMoneyFinishDiv;

    }

    private void handleBack() {
	setResponsePage(returnPage);
    }

    private void sendMoneyConfirm() {

	try {
	    if (basePage.handleTransaction(tab)) {
		setContainerVisibilities(false, false, true);

	    } else {
		if (tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    sendMoneyFinishDiv();
		    setContainerVisibilities(false, false, true);
		}
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during preauthorization continue", e);
	    error(getLocalizer().getString("preauthorization.continue.error",
		    this));

	}

    }

    private void handleSubmit() {
	LOG.debug("#SendMoneyBankPage.handleSubmit()");
	com.sybase365.mobiliser.util.tools.wicketutils.security.Customer loggedInCustomer = basePage
		.getMobiliserWebSession().getLoggedInCustomer();

	payeeCustomer = basePage.getCustomerByIdentification(
		Constants.IDENT_TYPE_CUST_ID,
		String.valueOf(loggedInCustomer.getCustomerId()));

	if (payeeCustomer != null && !isFromBankList) {

	    BankAccount bankAccount = new BankAccount();
	    try {
		bankAccount.setAccountNumber(basePage.encrypt(account, basePage
			.getConfiguration().getBankAccKeyAlias()));
	    } catch (KeyStoreException e1) {
		LOG.error("# An error occurred while enrypting bank account",
			e1);
		error(getLocalizer().getString("bank.account.error", this));
		return;
	    }

	    String accNumber = account.length() < 4 ? account : account
		    .substring(account.length() - 4);

	    bankAccount.setDisplayNumber("xxxxxx" + accNumber);
	    bankAccount.setAccountHolderName(accountHolder);
	    bankAccount.setBankCode(bankCode);
	    bankAccount.setBankCountry(loggedInCustomer.getCountry());
	    bankAccount.setCurrency(basePage.getConfiguration().getCurrency());
	    bankAccount.setType(Constants.PI_TYPE_EXTERNAL_SEND_MONEYBANK);
	    bankAccount.setMultiCurrency(false);
	    bankAccount.setActive(true);
	    bankAccount.setCustomerId(payeeCustomer.getId());
	    try {
		pi = bankAccountExist(
			payeeCustomer,
			Integer.valueOf(Constants.PI_TYPE_EXTERNAL_SEND_MONEYBANK),
			bankAccount);
	    } catch (Exception e) {
		error(getLocalizer().getString(
			"sendMoneyBank.account.exist.error", this));
		return;
	    }
	}
	convertAmount();
	tab = new TransactionBean();
	VatAmount vatAmnt = new VatAmount();
	vatAmnt.setCurrency(basePage.getConfiguration().getCurrency());
	vatAmnt.setValue(amount);

	tab.setAmount(vatAmnt);
	tab.setAutoCapture(true);
	tab.setUsecase(basePage.getConfiguration().getUseCaseSendMoneyToBank());
	tab.setText(txnText);
	TransactionParticipant payer = new TransactionParticipant();
	Identifier payerId = new Identifier();
	payerId.setType(Constants.IDENT_TYPE_CUST_ID);
	payerId.setValue(String.valueOf(loggedInCustomer.getCustomerId()));
	payer.setIdentifier(payerId);
	tab.setPayer(payer);

	TransactionParticipant payee = new TransactionParticipant();
	Identifier payeeId = new Identifier();
	payeeId.setType(Constants.IDENT_TYPE_CUST_ID);
	payeeId.setValue(String.valueOf(loggedInCustomer.getCustomerId()));
	payee.setIdentifier(payeeId);
	if (!isFromBankList)
	    payee.setPaymentInstrumentId(pi.getId());
	else
	    payee.setPaymentInstrumentId(piId);

	tab.setPayee(payee);
	tab.setModule(Constants.MODULE_SEND_MONEY_BANK);

	try {
	    if (basePage.handleTransaction(tab)) {
		setContainerVisibilities(false, true, false);

	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during Preauthorization", e);
	    error(getLocalizer().getString("preauthorization.error", this));
	}

    }

    protected PaymentInstrument bankAccountExist(Customer customer,
	    Integer piTypeFilter, BankAccount bankAccount) throws Exception {
	LOG.debug("MobiliserBasePage.bankAccountExist()");
	List<WalletEntry> walletEntries = new ArrayList<WalletEntry>();
	List<WalletEntry> extEntries = basePage.getWalletEntryList(
		customer.getId(), Constants.PIS_CLASS_FILTER_BANK_ACCOUNT,
		Constants.PI_TYPE_EXTERNAL_SEND_MONEYBANK);

	walletEntries.addAll(extEntries);

	List<WalletEntry> bankListEntries = basePage.getWalletEntryList(
		customer.getId(), Constants.PIS_CLASS_FILTER_BANK_ACCOUNT,
		Constants.PI_TYPE_EXTERNAL_BA);

	walletEntries.addAll(bankListEntries);

	BankAccount banckAC;
	PaymentInstrument ac;
	boolean isDuplicateBankCode = false;
	for (WalletEntry bankWalletEntry : walletEntries) {
	    banckAC = bankWalletEntry.getBankAccount();

	    if (banckAC != null) {

		if (banckAC.getBranchCode() == null
			&& bankAccount.getBranchCode() == null) {
		    isDuplicateBankCode = true;
		} else if (banckAC.getBranchCode() != null
			&& bankAccount.getBranchCode() != null) {
		    if (bankAccount.getBranchCode().equals(
			    banckAC.getBranchCode())) {
			isDuplicateBankCode = true;
		    }
		}

		if (isDuplicateBankCode
			&& bankAccount.getDisplayNumber().equals(
				banckAC.getDisplayNumber())
			&& bankAccount.getBankCode().equals(
				banckAC.getBankCode())
			&& bankAccount.getAccountHolderName().equals(
				banckAC.getAccountHolderName())) {
		    LOG.info("Bank account with the given details already exists");

		    return banckAC;
		}
	    }
	}
	LOG.info("Creating New Bank Account");

	ac = basePage.addToWallet(customer, bankAccount);
	return ac;
    }

    public void convertAmount() {
	try {
	    amount = FormatUtils.toAmount(amountString, Currency
		    .getInstance(basePage.getConfiguration().getCurrency()),
		    basePage.getMobiliserWebSession().getLocale());
	} catch (ParseException e) {
	    LOG.error("# could not parse amount[" + amountString + "]", e);
	}
    }

    private void setContainerVisibilities(boolean viewSendMoneyDiv,
	    boolean viewSendMoneyConfirmDiv, boolean viewSendMoneyFinishDiv) {
	this.sendMoneyBankDiv.setVisible(viewSendMoneyDiv);
	this.sendMoneyBankConfirmDiv.setVisible(viewSendMoneyConfirmDiv);
	this.sendMoneyBankFinishDiv.setVisible(viewSendMoneyFinishDiv);
    }

    public String getAccount() {
	return account;
    }

    public void setAccount(String account) {
	this.account = account;
    }

    public String getBankCode() {
	return bankCode;
    }

    public void setBankCode(String bankCode) {
	this.bankCode = bankCode;
    }

    public String getAccountHolder() {
	return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
	this.accountHolder = accountHolder;
    }

    public String getAmountString() {
	return amountString;
    }

    public void setAmountString(String amountString) {
	this.amountString = amountString;
    }

    public String getCreditAmount() {
	return basePage
		.convertAmountToStringWithCurrency(tab.getCreditAmount());
    }

    public String getDebitAmount() {
	return basePage.convertAmountToStringWithCurrency(tab.getDebitAmount());
    }

    public String getFeeAmount() {
	return basePage.convertAmountToStringWithCurrency(tab.getFeeAmount());
    }

    public String getTxnId() {
	return String.valueOf(tab.getTxnId());
    }

    public String getAuthCode() {
	return tab.getAuthCode();
    }
}
