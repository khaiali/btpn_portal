package com.sybase365.mobiliser.web.distributor.pages.agentcare;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.util.AmountValidator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MANAGE_AGENTS)
public class AgentTransferMoneyPage extends AgentCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AgentTransferMoneyPage.class);

    private String payeeBalance;
    private String amountString;
    private String orderId;

    public AgentTransferMoneyPage() {
	super();
	initPageComponents();
    }

    @SuppressWarnings("unchecked")
    protected void initPageComponents() {
	Form<?> agentTransMoneyForm = new Form("agentTransMoneyForm",
		new CompoundPropertyModel<AgentTransferMoneyPage>(this));
	agentTransMoneyForm.add(new Label("payeeBalance"));

	agentTransMoneyForm.add(new RequiredTextField<String>("amountString")
		.add(new AmountValidator(this, Constants.REGEX_AMOUNT_16_2))
		.add(Constants.amountSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentTransMoneyForm.add(new TextField<String>("orderId")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	agentTransMoneyForm.add(new Button("transferNext") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		tranferMoneyPreAuth();
	    };
	});
	agentTransMoneyForm.add(new FeedbackPanel("errorMessages"));
	addOrReplace((FeedbackPanel) (new FeedbackPanel("globalerrorMessages")
		.setVisible(false)));
	CustomerBean agentToEdit = getMobiliserWebSession().getCustomer();

	if (PortalUtils.exists(agentToEdit)) {
	    // load payee balance...
	    try {
		List<WalletEntry> walletEntryList = getWalletEntryList(
			agentToEdit.getId(), null, null);
		if (walletEntryList != null && !walletEntryList.isEmpty()) {
		    for (WalletEntry wallet : walletEntryList) {
			if (wallet.getSva() != null) {
			    payeeBalance = convertAmountToStringWithCurrency(getSVABalanceAmount(wallet
				    .getPaymentInstrumentId()));
			    break;
			}
		    }
		}

		if (payeeBalance == null)
		    error(getLocalizer().getString(
			    "ERROR.LOAD_PAYEE_BALANCE.NO_SVA", this));

	    } catch (Exception e) {
		LOG.error(
			"# An error occurred while user[{}] tries to load sva balance of user[{}]",
			new Object[] {
				getWebSession().getLoggedInCustomer()
					.getCustomerId(), agentToEdit.getId(),
				e });
		error(getLocalizer()
			.getString("ERROR.LOAD_PAYEE_BALANCE", this));
	    }
	}
	agentTransMoneyForm.setVisible(true);
	if (!getMobiliserWebSession().hasM2MRole(
		getMobiliserWebSession().getCustomer())
		|| !getMobiliserWebSession().hasM2MRole(
			getMobiliserWebSession().getLoggedInCustomer())) {
	    addOrReplace((FeedbackPanel) (new FeedbackPanel(
		    "globalerrorMessages").setVisible(true)));
	    error(getLocalizer().getString("ERROR.MISSING_TRANSFER_MONEY_PRIV",
		    this));
	    agentTransMoneyForm.setVisible(false);
	}
	add(agentTransMoneyForm);
    }

    protected void tranferMoneyPreAuth() {

	try {
	    TransactionBean tab = new TransactionBean();
	    VatAmount vatAmnt = new VatAmount();
	    vatAmnt.setCurrency(Constants.DEFAULT_CURRENCY);
	    vatAmnt.setValue(convertAmountToLong(amountString));

	    // set payee
	    TransactionParticipant payee = new TransactionParticipant();
	    Identifier payeeIdent = new Identifier();
	    payeeIdent.setType(Constants.IDENT_TYPE_CUST_ID);
	    payeeIdent.setValue(Long.toString(getMobiliserWebSession()
		    .getCustomer().getId()));
	    payee.setIdentifier(payeeIdent);
	    payee.setOrgUnitId("0000");

	    // set payer

	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier payerIdent = new Identifier();
	    payerIdent.setType(Constants.IDENT_TYPE_CUST_ID);
	    payerIdent.setValue(Long.toString(getMobiliserWebSession()
		    .getLoggedInCustomer().getCustomerId()));
	    payer.setIdentifier(payerIdent);
	    payer.setOrgUnitId("0000");

	    tab.setAmount(vatAmnt);
	    tab.setAutoCapture(true);
	    tab.setUsecase(getConfiguration().getUseCaseTransfer());
	    tab.setText("text");
	    tab.setPayer(payer);
	    tab.setPayee(payee);
	    tab.setOrderChannel(Constants.ORDER_CHANNEL_WEB);
	    tab.setOrderId(getOrderId());
	    tab.setPreAuthFinished(false);

	    if (handleTransaction(tab)) {
		setResponsePage(new TransferConfirmPage(tab));
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

    public String getPayeeBalance() {
	return payeeBalance;
    }

    public void setPayeeBalance(String payeeBalance) {
	this.payeeBalance = payeeBalance;
    }

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(String orderId) {
	this.orderId = orderId;
    }

}
