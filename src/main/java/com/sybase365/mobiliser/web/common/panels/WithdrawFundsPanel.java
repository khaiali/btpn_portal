package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.web.application.pages.BaseApplicationPage;
import com.sybase365.mobiliser.web.beans.SelectBean;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class WithdrawFundsPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(WithdrawFundsPanel.class);

    private Class<? extends BasePage> returnPage;
    private BaseApplicationPage basePage;
    private Long customerId;
    private List<WalletEntry> bankAccounts;
    private String amountString;
    private long txnAmount;
    private SelectBean selectBean;
    private String referenceText;
    private String newBalance;

    private TransactionBean tab;

    private WebMarkupContainer withdrawFundsDiv;

    private WebMarkupContainer withdrawFundsConfirmDiv;

    private WebMarkupContainer withdrawFundsFinishDiv;

    public WithdrawFundsPanel(String id, BaseApplicationPage basePage,
	    Class<? extends BasePage> returnPage, Long customerId,
	    final List<WalletEntry> bankAccounts) {
	super(id);
	this.bankAccounts = bankAccounts;
	this.returnPage = returnPage;
	this.basePage = basePage;
	this.customerId = customerId;
	constructPanel();
    }

    private void constructPanel() {
	this.withdrawFundsDiv = withdrawFundsDiv();
	this.withdrawFundsConfirmDiv = withdrawFundsConfirmDiv();
	this.withdrawFundsFinishDiv = withdrawFundsFinishDiv();
	setContainerVisibilities(true, false, false);
    }

    private WebMarkupContainer withdrawFundsDiv() {
	withdrawFundsDiv = new WebMarkupContainer("withdrawFundsDiv");
	Form<?> form = new Form("withdrawFundsDataForm",
		new CompoundPropertyModel<WithdrawFundsPanel>(this));

	form.add(new RequiredTextField<String>("amountString")
		.setRequired(true)
		.add(new AmountValidator(basePage, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new Label("currency", basePage.getCurrencySymbol()));

	form.add(
		new RequiredTextField<String>("referenceText")
			.setRequired(true).add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier)).add(
		new ErrorIndicator());

	final RadioGroup group = new RadioGroup("accountGroup",
		new PropertyModel(this, "selectBean"));
	group.setRequired(true).setLabel(new Model<String>("Account"));

	ListView accounts = new ListView("result", getBanks()) {
	    protected void populateItem(ListItem item) {
		item.add(new Radio("radio", item.getModel()));
		item.add(new Label("name", new PropertyModel(item.getModel(),
			"name")));

	    }
	};
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
		withdrawFundsNext(selectBean.getId(), selectBean.getName());
	    };
	});

	group.add(accounts);
	form.add(group);
	form.add(new FeedbackPanel("errorMessages"));

	withdrawFundsDiv.add(form);
	add(withdrawFundsDiv);
	return withdrawFundsDiv;
    }

    protected void cancel() {
	setResponsePage(returnPage);

    }

    protected void withdrawFundsNext(String pId, String name) {
	LOG.debug("#WithdrawFunds.withDrawFundsNext()");
	try {
	    txnAmount = basePage.convertAmountToLong(amountString);

	    tab = new TransactionBean();
	    tab.setBeneficiaryName(name);
	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(basePage.getConfiguration().getCurrency());

	    vatAmnt.setValue(txnAmount);

	    tab.setAmount(vatAmnt);
	    tab.setAutoCapture(true);
	    tab.setUsecase(Constants.USE_CASE_WITHDRAW_FUNDS);
	    tab.setText(referenceText);
	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier ident = new Identifier();
	    ident.setType(1);
	    ident.setValue(String.valueOf(customerId));
	    payer.setIdentifier(ident);
	    payer.setPaymentInstrumentId(basePage.getSvaPI(customerId)
		    .getPaymentInstrumentId());
	    tab.setPayer(payer);

	    TransactionParticipant payee = new TransactionParticipant();
	    payee.setIdentifier(ident);
	    payee.setPaymentInstrumentId(new Long(pId));
	    tab.setPayee(payee);

	    tab.setModule(Constants.MODULE_WITHDRAW_FUNDS);
	    if (basePage.handleTransaction(tab)) {
		setContainerVisibilities(false, true, false);
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during Preauthorization", e);
	    error(getLocalizer().getString("preauthorization.error", this));
	}

    }

    private WebMarkupContainer withdrawFundsConfirmDiv() {
	withdrawFundsConfirmDiv = new WebMarkupContainer(
		"withdrawFundsConfirmDiv");
	Form<?> form = new Form("withdrawFundsConfirmForm",
		new CompoundPropertyModel<WithdrawFundsPanel>(this));

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
		new CompoundPropertyModel<WithdrawFundsPanel>(this));
	labels.add(new Label("debitAmount"));
	labels.add(new Label("feeAmount"));
	labels.add(new Label("creditAmount"));
	labels.add(new Label("referenceText"));
	labels.add(new Label("amountCreditedTo"));
	form.add(labels);
	withdrawFundsConfirmDiv.add(form);
	add(withdrawFundsConfirmDiv);
	return withdrawFundsConfirmDiv;
    }

    protected void confirm() {
	LOG.debug("#WithdrawFundsConfirm.confirm()");
	try {
	    if (basePage.handleTransaction(tab)) {
		WalletEntry wallet = basePage.getSvaPI(customerId);
		if (PortalUtils.exists(wallet))
		    newBalance = basePage
			    .convertAmountToStringWithCurrency(basePage
				    .getSVABalanceAmount(wallet
					    .getPaymentInstrumentId()));
		setContainerVisibilities(false, false, true);
	    } else {
		if (tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    withdrawFundsFinishDiv();
		    setContainerVisibilities(false, false, true);
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
    }

    private WebMarkupContainer withdrawFundsFinishDiv() {
	withdrawFundsFinishDiv = new WebMarkupContainer(
		"withdrawFundsFinishDiv");
	withdrawFundsFinishDiv.setOutputMarkupId(true);
	withdrawFundsFinishDiv.setOutputMarkupPlaceholderTag(true);

	Form<?> form = new Form("withdrawFundsFinishForm",
		new CompoundPropertyModel<WithdrawFundsPanel>(this));
	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		cancel();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<WithdrawFundsPanel>(this));
	labels.add(new Label("debitAmount"));
	labels.add(new Label("newBalance"));
	labels.add(new Label("amountCreditedTo"));
	form.add(labels);

	Label successLbl = new Label("successMsg", getLocalizer().getString(
		"withdrawFundsFinish.success", this));

	withdrawFundsFinishDiv.add(successLbl);

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	withdrawFundsFinishDiv.add(pendingLbl);

	if (PortalUtils.exists(tab)
		&& tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successLbl.setVisible(false);
	} else
	    pendingLbl.setVisible(false);

	withdrawFundsFinishDiv.add(form);
	addOrReplace(withdrawFundsFinishDiv);
	return withdrawFundsFinishDiv;
    }

    private void setContainerVisibilities(boolean viewWithdrawFundsDiv,
	    boolean viewWithdrawFundsConfirmDiv,
	    boolean viewWithdrawFundsFinishDiv) {
	this.withdrawFundsDiv.setVisible(viewWithdrawFundsDiv);
	this.withdrawFundsConfirmDiv.setVisible(viewWithdrawFundsConfirmDiv);
	if (viewWithdrawFundsFinishDiv) {
	    basePage.refreshSVABalance();
	}
	this.withdrawFundsFinishDiv.setVisible(viewWithdrawFundsFinishDiv);
    }

    private List<SelectBean> getBanks() {
	LOG.debug("#WithdrawFunds.getBanks()");
	List<SelectBean> result = new ArrayList<SelectBean>();

	for (WalletEntry ba : bankAccounts) {
	    String id = String.valueOf(ba.getPaymentInstrumentId());
	    String name = getLocalizer().getString("withdrawFunds.bankAccount",
		    this)
		    + ba.getAlias();
	    result.add(new SelectBean(id, name));

	}
	return result;

    }

    public String getReferenceText() {
	return referenceText;
    }

    public void setReferenceText(String referenceText) {
	this.referenceText = referenceText;
    }

    public String getAmountString() {
	return amountString;
    }

    public void setAmountString(String strAmount) {
	this.amountString = strAmount;
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

    public String getAmountCreditedTo() {
	return tab.getBeneficiaryName();
    }

    public String getNewBalance() {
	return newBalance;
    }

    public void setNewBalance(String newBalance) {
	this.newBalance = newBalance;
    }

}
