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

public class AddFundsPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AddFundsPanel.class);

    private Class<? extends BasePage> returnPage;
    private BaseApplicationPage basePage;
    private Long customerId;
    private List<WalletEntry> bankAccounts;
    private List<WalletEntry> creditCards;

    private String amountString;
    private String txnText;

    private long txnAmount;
    private SelectBean selectBean;
    private TransactionBean tab;
    private String newBalance;

    private WebMarkupContainer addFundDiv;

    private WebMarkupContainer addFundsConfirmDiv;

    private WebMarkupContainer addFundsFinishDiv;

    public AddFundsPanel(String id, BaseApplicationPage basePage,
	    Class<? extends BasePage> returnPage, Long customerId,
	    final List<WalletEntry> bankAccounts,
	    final List<WalletEntry> creditCards) {
	super(id);
	this.bankAccounts = bankAccounts;
	this.creditCards = creditCards;
	this.returnPage = returnPage;
	this.basePage = basePage;
	this.customerId = customerId;
	constructPnael();
    }

    private void constructPnael() {
	this.addFundDiv = addFundsDiv();
	this.addFundsConfirmDiv = addFundsConfirmDiv();
	this.addFundsFinishDiv = addFundsFinishDiv();
	setContainerVisibilities(true, false, false);
    }

    private WebMarkupContainer addFundsDiv() {
	WebMarkupContainer addFundDiv = new WebMarkupContainer("addFundDiv");

	Form<?> form = new Form("addFundsDataForm",
		new CompoundPropertyModel<AddFundsPanel>(this));

	form.add(new RequiredTextField<String>("amountString")
		.setRequired(true)
		.add(new AmountValidator(basePage, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));

	form.add(new Label("currency", basePage.getCurrencySymbol()));

	form.add(
		new RequiredTextField<String>("txnText").setRequired(true)
			.add(Constants.mediumStringValidator)
			.add(Constants.mediumSimpleAttributeModifier))

	.add(new ErrorIndicator());
	final RadioGroup group = new RadioGroup("account", new PropertyModel(
		this, "selectBean"));
	group.setRequired(true).setLabel(new Model<String>("Account"));

	ListView accounts = new ListView("result", getBanksAndCreditCards()) {
	    protected void populateItem(ListItem item) {
		item.add(new Radio("radio", item.getModel()));
		item.add(new Label("name", new PropertyModel(item.getModel(),
			"name")));

	    }
	};
	form.add(new Button("addFundsCancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		back();
	    };
	}.setDefaultFormProcessing(false));

	form.add(new Button("addFundsNext") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		addFundsNext(selectBean.getId(), selectBean.getName());
	    };
	});

	group.add(accounts);
	form.add(group);
	form.add(new FeedbackPanel("errorMessages"));

	addFundDiv.add(form);
	add(addFundDiv);
	return addFundDiv;

    }

    private WebMarkupContainer addFundsConfirmDiv() {
	WebMarkupContainer addFundConfirmDiv = new WebMarkupContainer(
		"addFundConfirmDiv");
	Form<?> form = new Form("addFundsConfirmForm",
		new CompoundPropertyModel<AddFundsPanel>(this));
	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setContainerVisibilities(true, false, false);
	    };
	}.setDefaultFormProcessing(false));
	form.add(new Button("confirm") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		addFundsConfirm();
	    };
	});
	form.add(new FeedbackPanel("errorMessages"));
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<AddFundsPanel>(this));
	labels.add(new Label("creditAmount"));
	labels.add(new Label("feeAmount"));
	labels.add(new Label("debitAmount"));
	labels.add(new Label("txnText"));
	labels.add(new Label("amountDebitedFrom"));
	form.add(labels);
	addFundConfirmDiv.add(form);
	add(addFundConfirmDiv);
	return addFundConfirmDiv;
    }

    protected void addFundsConfirm() {
	LOG.debug("#AddFundsConfirm.confirm()");
	try {
	    if (basePage.handleTransaction(tab)) {
		WalletEntry wallet = basePage.getSvaPI(customerId);
		if (PortalUtils.exists(wallet))
		    newBalance = basePage
			    .convertAmountToStringWithCurrency(basePage
				    .getSVABalanceAmount(wallet
					    .getPaymentInstrumentId()));
		setNewBalance(newBalance);

		setContainerVisibilities(false, false, true);
	    } else {
		if (tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
		    addFundsFinishDiv();
		    setContainerVisibilities(false, false, true);
		}
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred during preauthorization continue", e);
	    error(getLocalizer().getString("preauthorization.continue.error",
		    this));
	}
    }

    private WebMarkupContainer addFundsFinishDiv() {
	WebMarkupContainer addFundFinishDiv = new WebMarkupContainer(
		"addFundFinishDiv");
	addFundFinishDiv.setOutputMarkupId(true);
	addFundFinishDiv.setOutputMarkupPlaceholderTag(true);

	Form<?> form = new Form("addFundsFinishForm",
		new CompoundPropertyModel<AddFundsPanel>(this));
	form.add(new Button("continue") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		back();
	    };
	});

	form.add(new FeedbackPanel("errorMessages"));
	WebMarkupContainer labels = new WebMarkupContainer("dataContainer",
		new CompoundPropertyModel<AddFundsPanel>(this));
	labels.add(new Label("creditAmount"));
	labels.add(new Label("newBalance"));
	labels.add(new Label("amountDebitedFrom"));
	form.add(labels);

	Label successLbl = new Label("successMsg", getLocalizer().getString(
		"addFundsFinish.success", this));

	addFundFinishDiv.add(successLbl);

	Label pendingLbl = new Label("pendingApprovalMsg", getLocalizer()
		.getString("pendingApproval.msg", this));
	addFundFinishDiv.add(pendingLbl);

	if (PortalUtils.exists(tab)
		&& tab.getStatusCode() == Constants.TXN_STATUS_PENDING_APPROVAL) {
	    labels.setVisible(false);
	    successLbl.setVisible(false);
	} else
	    pendingLbl.setVisible(false);

	addFundFinishDiv.add(form);
	addOrReplace(addFundFinishDiv);
	return addFundFinishDiv;
    }

    private void setContainerVisibilities(boolean viewAddFundDiv,
	    boolean viewAddFundsConfirmDiv, boolean viewAddFundsFinishDiv) {
	this.addFundDiv.setVisible(viewAddFundDiv);
	this.addFundsConfirmDiv.setVisible(viewAddFundsConfirmDiv);
	if (viewAddFundsFinishDiv) {
	    basePage.refreshSVABalance();
	}
	this.addFundsFinishDiv.setVisible(viewAddFundsFinishDiv);
    }

    private List<SelectBean> getBanksAndCreditCards() {
	LOG.debug("#AddFunds.getBanksAndCreditCards()");
	List<SelectBean> result = new ArrayList<SelectBean>();

	for (WalletEntry ba : this.bankAccounts) {
	    String id = String.valueOf(ba.getPaymentInstrumentId());
	    String name = getLocalizer()
		    .getString("addFunds.bankAccount", this) + ba.getAlias();
	    result.add(new SelectBean(id, name));

	}
	for (WalletEntry cc : this.creditCards) {
	    String id = String.valueOf(cc.getPaymentInstrumentId());
	    String name = getLocalizer().getString("addFunds.creditCard", this)
		    + cc.getAlias();
	    result.add(new SelectBean(id, name));

	}
	return result;

    }

    private void addFundsNext(String pId, String piName) {

	LOG.debug("#AddFunds.addFundsNext()");
	tab = new TransactionBean();
	try {
	    tab.setBeneficiaryName(piName);
	    txnAmount = basePage.convertAmountToLong(amountString);
	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(basePage.getConfiguration().getCurrency());

	    vatAmnt.setValue(txnAmount);

	    tab.setAmount(vatAmnt);
	    tab.setAutoCapture(true);
	    tab.setUsecase(Integer.valueOf(Constants.USE_CASE_ADD_FUNDS));
	    tab.setText(txnText);
	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier id = new Identifier();
	    id.setType(Constants.IDENT_TYPE_CUST_ID);
	    id.setValue(String.valueOf(customerId));
	    payer.setIdentifier(id);
	    payer.setPaymentInstrumentId(new Long(pId));
	    tab.setPayer(payer);

	    TransactionParticipant payee = new TransactionParticipant();
	    payee.setIdentifier(id);
	    payee.setPaymentInstrumentId(basePage.getSvaPI(customerId)
		    .getPaymentInstrumentId());
	    tab.setPayee(payee);
	    tab.setModule(Constants.MODULE_ADD_FUNDS);
	    if (basePage.handleTransaction(tab)) {
		setContainerVisibilities(false, true, false);
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred during preauthorization", e);
	    error(getLocalizer().getString("preauthorization.error", this));
	}

    }

    private void back() {
	setResponsePage(returnPage);

    }

    public String getTxnText() {
	return txnText;
    }

    public void setTxnText(String txnText) {
	this.txnText = txnText;
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

    public String getAmountDebitedFrom() {
	return tab.getBeneficiaryName();
    }

    public String getNewBalance() {
	return newBalance;
    }

    public void setNewBalance(String newBalance) {
	this.newBalance = newBalance;
    }

}
