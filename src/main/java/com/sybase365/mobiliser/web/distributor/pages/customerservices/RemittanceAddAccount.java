package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreatePaymentInstrumentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreatePaymentInstrumentResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.ExternalAccount;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.beans.RemittanceAccountBean;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class RemittanceAddAccount extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RemittanceAddAccount.class);

    private RemittanceAccountBean inputRemittanceAccount;

    public RemittanceAddAccount() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */

    public RemittanceAddAccount(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	final Form<?> form = new Form("addRemitAcc",
		new CompoundPropertyModel<RemittanceAddAccount>(this));
	add(form);
	final FeedbackPanel messages = new FeedbackPanel("errorMessages");
	messages.setOutputMarkupId(true);
	messages.setOutputMarkupPlaceholderTag(true);
	form.add(messages);

	final WebMarkupContainer walletDiv = new WebMarkupContainer("walletDiv");

	walletDiv.add(new RequiredTextField<String>(
		"inputRemittanceAccount.msisdn")
		.setRequired(true)
		.add(StringValidator.lengthBetween(Constants.MIN_LENGTH_MSISDN,
			Constants.MAX_LENGTH_MSISDN))
		.add(new PatternValidator(Constants.REGEX_MSISDN))
		.add(new ErrorIndicator()));

	walletDiv.setVisible(false);
	walletDiv.setOutputMarkupId(true);
	walletDiv.setOutputMarkupPlaceholderTag(true);

	form.add(walletDiv);

	final WebMarkupContainer bankAccountDiv = new WebMarkupContainer(
		"bankAccDiv");

	bankAccountDiv.setVisible(false);
	bankAccountDiv.setOutputMarkupId(true);
	bankAccountDiv.setOutputMarkupPlaceholderTag(true);

	bankAccountDiv.add(new RequiredTextField<String>(
		"inputRemittanceAccount.accountHolder"));

	bankAccountDiv.add(new RequiredTextField<String>(
		"inputRemittanceAccount.accountNo"));

	bankAccountDiv.add(new KeyValueDropDownChoice<String, String>(
		"inputRemittanceAccount.institutionCode", getBankList(false))
		.setRequired(true));

	bankAccountDiv.add(new TextField<String>(
		"inputRemittanceAccount.accountHolderAddress"));

	bankAccountDiv
		.add(new TextField<String>("inputRemittanceAccount.city"));

	bankAccountDiv.add(new TextField<String>("inputRemittanceAccount.zip"));

	LocalizableLookupDropDownChoice<String> country = new LocalizableLookupDropDownChoice<String>(
		"inputRemittanceAccount.country", String.class,
		Constants.RESOURCE_BUNDLE_COUNTIRES, this, false, true);

	bankAccountDiv.add(country);

	form.add(bankAccountDiv);

	form.add(new LocalizableLookupDropDownChoice<Integer>(
		"inputRemittanceAccount.type", Integer.class,
		Constants.RESOURCE_BUNDLE_REMITTANCE_ACC, this, false, true)
		.setRequired(true).add(new OnChangeAjaxBehavior() {

		    @Override
		    protected void onUpdate(AjaxRequestTarget target) {
			int kv = inputRemittanceAccount.getType();
			if (kv == Constants.PI_TYPE_GCASH_WALLET) {
			    walletDiv.setVisible(true);
			    bankAccountDiv.setVisible(false);
			    form.addOrReplace(new FeedbackPanel("errorMessages"));
			} else if (kv == Constants.PI_TYPE_GCASH_BANK_ACCOUNT) {
			    bankAccountDiv.setVisible(true);
			    walletDiv.setVisible(false);
			    form.addOrReplace(new FeedbackPanel("errorMessages"));
			}
			target.addComponent(walletDiv);
			target.addComponent(bankAccountDiv);
			form.clearInput();
			target.addComponent(form);
		    }
		}).add(new ErrorIndicator()));

	form.add(new Button("cancel") {
	    @Override
	    public void onSubmit() {
		setResponsePage(RemittancePage.class);
	    }
	}.setDefaultFormProcessing(false));

	form.add(new Button("next") {
	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }
	});

    }

    private void handleSubmit() {
	CreatePaymentInstrumentRequest piReq;
	CreatePaymentInstrumentResponse createPiRes = null;
	try {
	    piReq = getNewMobiliserRequest(CreatePaymentInstrumentRequest.class);
	    ExternalAccount extAc = Converter.getInstance()
		    .convertToExternalAccount(inputRemittanceAccount);
	    extAc.setCustomerId(getMobiliserWebSession().getCustomer().getId());
	    piReq.setPaymentInstrument(extAc);
	    createPiRes = wsWalletClient.createPaymentInstrument(piReq);

	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while setting the external account for user["
			    + getMobiliserWebSession().getCustomer().getId()
			    + "]", e);
	    error(getLocalizer().getString(
		    "remittance.add.externalAccount.error", this));
	}
	if (!evaluateMobiliserResponse(createPiRes)) {
	    LOG.warn(
		    "# An error occurred while setting the external account for user[{}]",
		    +getMobiliserWebSession().getCustomer().getId());
	    return;
	} else {
	    getSession().info(
		    getLocalizer().getString(
			    "remittance.account.add.confirmation", this));
	    setResponsePage(RemittancePage.class);
	}
    }

    public RemittanceAccountBean getInputRemittanceAccount() {
	return inputRemittanceAccount;
    }

    public void setInputRemittanceAccount(
	    RemittanceAccountBean inputRemittanceAccount) {
	this.inputRemittanceAccount = inputRemittanceAccount;
    }

    @Override
    protected Class getActiveMenu() {
	return RemittancePage.class;
    }
}
