package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.PreAuthorisationContinue;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PreAuthorisationContinueResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PrePickupMoney;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PrePickupMoneyResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.MoneyFeeType;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Transaction;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class PickUpPage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(PickUpPage.class);

    private String pickUpCode;
    private String txnId;
    private String feeAmount;
    private String originalAmount;
    private String agentAmount;
    private String msisdn;
    // container instances
    private WebMarkupContainer pickUpDiv;
    private WebMarkupContainer pickUpConfirmDiv;
    private WebMarkupContainer pickUpFinishDiv;

    private PrePickupMoneyResponse moneyResponse;

    private Transaction transaction;

    public PickUpPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public PickUpPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	pickUpDiv = addPickUpDiv();
	pickUpConfirmDiv = addPickUpConfirmDiv();
	pickUpFinishDiv = addPickUpFinishDiv();
	setContainerVisibilities(true, false, false);

	addOrReplace(new Label("pickUp.title", getLocalizer().getString(
		"pickUp.title", this)));
    }

    private WebMarkupContainer addPickUpDiv() {
	pickUpDiv = new WebMarkupContainer("pickUpDiv");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("pickUpForm",
		new CompoundPropertyModel<PickUpPage>(this));
	form.add(new RequiredTextField<String>("pickUpCode").setRequired(true)
		.add(new ErrorIndicator()));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Button("pickUpNext") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (verifyPickupCode(getPickupCode())) {
		    setContainerVisibilities(false, true, false);
		    addOrReplace(new Label("pickUp.title", getLocalizer()
			    .getString("pickUp.title", this)
			    + getLocalizer().getString(
				    "application.breadcrumb.separator", this)
			    + getLocalizer().getString("pickUpConfirm.title",
				    this)));
		}
	    }
	});
	pickUpDiv.add(form);
	add(pickUpDiv);
	return pickUpDiv;
    }

    private WebMarkupContainer addPickUpFinishDiv() {
	pickUpFinishDiv = new WebMarkupContainer("pickUpFinishDiv");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("pickUpFinishForm",
		new CompoundPropertyModel<PickUpPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Label("txnId"));
	form.add(new Label("msisdn"));
	form.add(new Label("agentAmount"));
	form.add(new Button("pickUpFinished") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(CustomerDetailsPage.class);
	    }
	});
	pickUpFinishDiv.add(form);
	add(pickUpFinishDiv);
	return pickUpFinishDiv;
    }

    private WebMarkupContainer addPickUpConfirmDiv() {
	pickUpConfirmDiv = new WebMarkupContainer("pickUpConfirmDiv");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final Form<?> form = new Form("pickUpConfirmForm",
		new CompoundPropertyModel<PickUpPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new Label("feeAmount"));
	form.add(new Label("originalAmount"));
	form.add(new Label("agentAmount"));
	form.add(new Button("payoutNow") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (doPayout()) {
		    setContainerVisibilities(false, false, true);
		    addOrReplace(new Label("pickUp.title", getLocalizer()
			    .getString("pickUp.title", this)
			    + getLocalizer().getString(
				    "application.breadcrumb.separator", this)
			    + getLocalizer().getString("pickUpFinish.title",
				    this)));
		}
	    }
	});
	pickUpConfirmDiv.add(form);

	add(pickUpConfirmDiv);
	return pickUpConfirmDiv;
    }

    protected boolean doPayout() {
	try {
	    PreAuthorisationContinue request = getNewMobiliserRequest(PreAuthorisationContinue.class);
	    request.setReferenceTransaction(this.moneyResponse.getTransaction());
	    PreAuthorisationContinueResponse response = wsPreAuthContinueClient
		    .preAuthorisationContinue(request);
	    if (evaluateMobiliserResponse(response)) {
		this.transaction = response.getTransaction();
		txnId = String.valueOf(transaction.getSystemId());
		msisdn = getMobiliserWebSession().getCustomer().getMsisdn();
		return true;
	    }
	} catch (Exception e) {
	    LOG.error("Error in pickup voucher payout ", e);
	    error(getLocalizer().getString("pickup.payout.error", this));
	}
	return false;
    }

    protected boolean verifyPickupCode(String pickUpStr) {
	try {
	    PrePickupMoney request = getNewMobiliserRequest(PrePickupMoney.class);
	    request.setAutoCapture(Boolean.TRUE);
	    request.setPickupCode(pickUpStr);
	    request.setUsecase(getConfiguration().getUseCasePickup());
	    TransactionParticipant payer = new TransactionParticipant();
	    Identifier ident = new Identifier();
	    ident.setType(Constants.IDENT_TYPE_CUST_ID);
	    ident.setValue(Long.toString(getMobiliserWebSession().getCustomer()
		    .getId()));
	    payer.setIdentifier(ident);
	    request.setPayer(payer);
	    TransactionParticipant payee = new TransactionParticipant();
	    Identifier identPayee = new Identifier();
	    identPayee.setType(Constants.IDENT_TYPE_CUST_ID);
	    identPayee.setValue(Long.toString(getMobiliserWebSession()
		    .getLoggedInCustomer().getCustomerId()));
	    payee.setIdentifier(identPayee);
	    request.setPayee(payee);
	    VatAmount amount = new VatAmount();
	    amount.setCurrency(getConfiguration().getCurrency());
	    amount.setValue(0);
	    request.setAmount(amount);
	    PrePickupMoneyResponse mResponse = wsPrePickupMoneyClient
		    .prePickupMoney(request);
	    if (evaluateMobiliserResponse(mResponse)) {
		this.moneyResponse = mResponse;
		long payeeFee = 0;
		long payerFee = 0;
		String currency = "";
		for (MoneyFeeType mft : moneyResponse.getMoneyFee()) {
		    if (mft.isPayee()) {
			payeeFee += mft.getValue();
			payeeFee += mft.getVat();
			currency = mft.getCurrency();
		    } else {
			// isPayer
			payerFee += mft.getValue();
			payerFee += mft.getVat();
		    }
		}
		originalAmount = convertAmountToStringWithCurrency(moneyResponse
			.getVoucherAmount());
		if (PortalUtils.exists(moneyResponse.getPayeeExchangeRate())) {
		    // TODO check if double calculation can be converted to long
		    double sourceAmount = Double
			    .parseDouble(convertAmountToString(moneyResponse
				    .getVoucherAmount().getValue()));
		    double targetAmount = sourceAmount
			    / moneyResponse.getPayeeExchangeRate()
				    .getFromAmount()
			    * moneyResponse.getPayeeExchangeRate().getRate()
				    .doubleValue()
			    * moneyResponse.getPayeeExchangeRate()
				    .getToAmount();
		    targetAmount -= payeeFee;
		    agentAmount = targetAmount
			    + moneyResponse.getPayeeExchangeRate()
				    .getToCurrency();
		    if (!PortalUtils.exists(currency)) {
			currency = moneyResponse.getPayeeExchangeRate()
				.getToCurrency();
		    }
		} else {
		    if (!PortalUtils.exists(currency)) {
			currency = moneyResponse.getVoucherAmount()
				.getCurrency();
		    }
		    agentAmount = convertAmountToStringWithCurrency(
			    moneyResponse.getVoucherAmount().getValue()
				    - payeeFee, currency);
		}
		feeAmount = convertAmountToStringWithCurrency(payeeFee,
			currency);
		return true;
	    }
	} catch (Exception e) {
	    LOG.error("Error in calling PrePickupMoney request", e);
	    error(getLocalizer().getString("pickup.verify.error", this));
	}
	return false;
    }

    private void setContainerVisibilities(boolean viewPickUpDiv,
	    boolean viewPickUpConfirmDiv, boolean viewPickUpFinishDiv) {
	this.pickUpDiv.setVisible(viewPickUpDiv);
	this.pickUpConfirmDiv.setVisible(viewPickUpConfirmDiv);
	this.pickUpFinishDiv.setVisible(viewPickUpFinishDiv);
    }

    public String getPickupCode() {
	return pickUpCode;
    }

    public void setPickupCode(String pickUpCode) {
	this.pickUpCode = pickUpCode;
    }

    public String getTxnId() {
	return txnId;
    }

    public String getOriginalAmount() {
	return originalAmount;
    }

    public void setOriginalAmount(String originalAmount) {
	this.originalAmount = originalAmount;
    }

    public String getAgentAmount() {
	return agentAmount;
    }

    public void setAgentAmount(String agentAmount) {
	this.agentAmount = agentAmount;
    }

    public Transaction getTransaction() {
	return transaction;
    }

    public void setTransaction(Transaction transaction) {
	this.transaction = transaction;
    }

    public PrePickupMoneyResponse getMoneyResponse() {
	return moneyResponse;
    }

    public void setMoneyResponse(PrePickupMoneyResponse moneyResponse) {
	this.moneyResponse = moneyResponse;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getMsisdn() {
	return msisdn;
    }

}