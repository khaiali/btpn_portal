package com.sybase365.mobiliser.web.checkout.pages;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.web.checkout.models.Transaction;
import com.sybase365.mobiliser.web.consumer.pages.signup.PersonalDataPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

@SuppressWarnings("all")
public class GetMsisdnPage extends BaseCheckoutPage {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(GetMsisdnPage.class);

    private String msisdn;

    public GetMsisdnPage() {
	super();
	LOG.info("Created new GetMsisdnPage");
	initPageComponents();
    }

    public GetMsisdnPage(String msisdn) {
	super();
	LOG.info("Created new GetMsisdnPage");
	PhoneNumber pn = new PhoneNumber(msisdn);
	this.msisdn = pn.getInternationalFormat();
	initPageComponents();
    }

    protected void initPageComponents() {
	add(new FeedbackPanel("errorMessages"));
	add(new Label("txnPayeeDisplayName", getMobiliserWebSession()
		.getTxnPayee().getDisplayName()));
	add(new Label("txnAmount", formateAmount(getMobiliserWebSession()
		.getTransaction().getTxnDetail().getAmount().getValue())));
	add(new Label("txnText", getMobiliserWebSession().getTransaction()
		.getTxnDetail().getText()));

	Form<?> form = new Form("getMsisdnForm",
		new CompoundPropertyModel<GetMsisdnPage>(this));
	TextField<String> msisdn = new TextField<String>("msisdn");
	form.add(msisdn.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier));
	if (PortalUtils.exists(getMsisdn())) {
	    msisdn.add(new SimpleAttributeModifier("style",
		    "background-color: #E6E6E6;"));
	    msisdn.add(new SimpleAttributeModifier("readonly", "readonly"));
	}
	form.add(new Button("checkMsisdn") {
	    @Override
	    public void onSubmit() {
		PhoneNumber pn = new PhoneNumber(getMsisdn());
		Customer customer = getCustomerByIdentification(
			Constants.IDENT_TYPE_MSISDN,
			pn.getInternationalFormat());
		setMsisdn(pn.getInternationalFormat());
		if (customer == null) {
		    getMobiliserWebSession().setContinueToCheckout(true);
		    setResponsePage(new PersonalDataPage(getMsisdn()));
		} else {
		    getMobiliserWebSession().setTxnPayer(customer);
		    getMobiliserWebSession().setMsisdn(getMsisdn());
		    setResponsePage(CheckPinPage.class);
		}
	    }
	});
	form.add(new Button("cancel") {
	    @Override
	    public void onSubmit() {
		Transaction txn = null;
		try {
		    txn = getMobiliserWebSession().getTransaction();

		    if (LOG.isDebugEnabled())
			LOG.debug("Consumer canceled transaction. [TransactionID="
				+ txn.getSystemId() + "]");

		    if (txn.getStatusCode() == 0)
			txn.failTransaction(2541);
		    else
			txn.failTransaction(txn.getStatusCode());

		    // Invalidate Session
		    getMobiliserWebSession().invalidate();

		    if (LOG.isDebugEnabled())
			LOG.debug("Returning to merchant: "
				+ txn.getReturnUrl());

		    getWebRequestCycle().getWebResponse()
			    .getHttpServletResponse()
			    .sendRedirect(txn.getTxnDetail().getReturnUrl());
		} catch (Exception e) {
		    LOG.error(
			    "#An error occured while canceling the transaction["
				    + txn.getSystemId() + "]", e);
		    error(getLocalizer().getString("txn.cancel.error", this));
		    return;
		}
	    }
	}.setDefaultFormProcessing(false));
	add(form);

	WebMarkupContainer deliveryAddress = new WebMarkupContainer(
		"deliveryAddress");
	add(deliveryAddress);

	deliveryAddress.setVisible(getMobiliserWebSession().getTransaction()
		.getTxnDetail().isRetrieveDeliveryAddress());

    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    private String formateAmount(long amount) {

	return amount / 100 + " " + "\u20AC";

    }

}
