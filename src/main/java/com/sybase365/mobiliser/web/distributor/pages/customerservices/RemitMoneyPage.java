package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetPaymentInstrumentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetPaymentInstrumentResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.ExternalAccount;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.RemittanceAccountBean;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class RemitMoneyPage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RemitMoneyPage.class);

    private String purposeOfTransfer;;
    private String amountString;
    private RemittanceAccountBean remittanceData;
    private ExternalAccount extAcc;

    public RemitMoneyPage() {
	super();
    }

    public RemitMoneyPage(ExternalAccount extAcc) {
	super();
	this.extAcc = extAcc;
	initPageComponents();
    }

    protected void initPageComponents() {

	Form<?> form = new Form("remitMoneyForm",
		new CompoundPropertyModel<RemitMoneyPage>(this));

	add(form);

	form.add(new FeedbackPanel("errorMessages"));

	form.add(new LocalizableLookupDropDownChoice<String>(
		"purposeOfTransfer", String.class,
		Constants.RESOURCE_BUNDLE_REMITTANCE_PURPOSE, this, false, true)
		.setRequired(true).add(new ErrorIndicator()));

	remittanceData = new RemittanceAccountBean();
	remittanceData.setAccountHolder(extAcc.getId2());
	remittanceData.setType(extAcc.getType());
	remittanceData.setAccountNo(extAcc.getId4());
	remittanceData.setInstitutionCodeId(extAcc.getId5());
	remittanceData.setpId(String.valueOf(extAcc.getId()));

	form.add(new TextField<String>("amountString").setRequired(true));

	form.add(new Button("remittanceNext") {
	    @Override
	    public void onSubmit() {
		handleNext();
	    }
	});

	form.add(new Button("remittanceCancel") {
	    @Override
	    public void onSubmit() {
		getMobiliserWebSession().setRemittanceData(null);
		setResponsePage(RemittancePage.class);
	    }
	}.setDefaultFormProcessing(false));

    }

    private void handleNext() {
	try {
	    TransactionBean tab = new TransactionBean();
	    VatAmount vatAmount = new VatAmount();
	    vatAmount.setCurrency(Constants.DEFAULT_CURRENCY);
	    vatAmount.setValue(convertAmountToLong(amountString));
	    tab.setAmount(vatAmount);
	    tab.setAutoCapture(true);
	    tab.setModule(Constants.MODULE_REMIT_MONEY);

	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier payerIdent = new Identifier();
	    payerIdent.setType(Constants.IDENT_TYPE_CUST_ID);
	    payerIdent.setValue(String.valueOf(getMobiliserWebSession()
		    .getCustomer().getId()));
	    payer.setIdentifier(payerIdent);
	    payer.setPaymentInstrumentType(Constants.PI_TYPE_DEFAULT_SVA);
	    tab.setPayer(payer);

	    TransactionParticipant payee = new TransactionParticipant();
	    Identifier payeeIdent = new Identifier();
	    payeeIdent.setType(Constants.IDENT_TYPE_CUST_ID);
	    payeeIdent.setValue(String.valueOf(getMobiliserWebSession()
		    .getCustomer().getId()));
	    payee.setIdentifier(payerIdent);
	    payee.setPaymentInstrumentId(Long.valueOf(remittanceData.getpId()));
	    tab.setPayee(payee);

	    // getting the external account corresponding to the pid selected
	    // for remittance
	    GetPaymentInstrumentRequest piReq = getNewMobiliserRequest(GetPaymentInstrumentRequest.class);
	    piReq.setPaymentInstrumentId(Long.valueOf(remittanceData.getpId()));
	    GetPaymentInstrumentResponse piRes = wsWalletClient
		    .getPaymentInstrument(piReq);
	    if (!evaluateMobiliserResponse(piRes)) {
		return;
	    }
	    ExternalAccount PayeeExtAcc = (ExternalAccount) piRes
		    .getPaymentInstrument();
	    String recieverMsisdn = PayeeExtAcc.getId1();
	    String recieverBankName = PayeeExtAcc.getId5();
	    if (PayeeExtAcc.getType() == Constants.PI_TYPE_GCASH_WALLET) {
		tab.setUsecase(Constants.USE_CASE_REMIT_MONEY_TO_WALLET);
		tab.setText("GCASH Wallet ( " + recieverMsisdn + ")");
	    } else if (PayeeExtAcc.getType() == Constants.PI_TYPE_GCASH_BANK_ACCOUNT) {
		tab.setUsecase(Constants.USE_CASE_REMIT_MONEY_TO_BANK);
		tab.setText("GCASH Bank Account ( " + recieverBankName + ")");
	    }

	    tab.setPurposeOfTransfer(purposeOfTransfer);
	    tab.setPreAuthFinished(false);
	    // PreAuthorisationResponse preAuthRes =
	    // handlePreAuthorisation(tab);
	    if (handleTransaction(tab)) {
		setResponsePage(RemitMoneyConfirmPage.class);
	    }

	} catch (Exception e) {
	    LOG.error("# An error occurred durring pre authorisation", e);
	    error(getLocalizer().getString("preAuthorisation.error", this));
	    return;
	}

    }

    @Override
    protected Class getActiveMenu() {
	return RemittancePage.class;
    }
}
