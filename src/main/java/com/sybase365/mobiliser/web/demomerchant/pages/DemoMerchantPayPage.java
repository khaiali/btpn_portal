package com.sybase365.mobiliser.web.demomerchant.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

import com.sybase365.mobiliser.money.contract.v5_0.transaction.WebStart;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.WebStartResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.Identifier;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.TransactionParticipant;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.VatAmount;
import com.sybase365.mobiliser.web.checkout.pages.FirstContactPage;
import com.sybase365.mobiliser.web.demomerchant.util.CartItem;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

@SuppressWarnings("all")
public class DemoMerchantPayPage extends BaseDemoMerchantPage {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(DemoMerchantPayPage.class);

    private String paymentmethod;
    private boolean autoCapture = true;
    private boolean test;
    private boolean deliveryAddress;
    private long amount;
    private String orderId;
    private String text;

    public DemoMerchantPayPage() {
	super();

	LOG.info("Created new DemoMerchantPayPage");
    }

    @Override
    protected void initOwnPageComponents() {
	add(new FeedbackPanel("errorMessages"));
	Form<?> form = new Form("payform",
		new CompoundPropertyModel<DemoMerchantPayPage>(this));

	form.add(new Label("sum", formateAmount(calculaetSum())));
	RadioGroup group = new RadioGroup("paymentmethod");
	group.add(new Radio("payment_method_cc", new Model(
		"PAYMENT_TYPE_CREDIT_CARD")));
	group.add(new Radio("payment_method_mpay", new Model(
		"PAYMENT_TYPE_MYPAY")));
	form.add(group);

	form.add(new CheckBox("autoCapture"));
	form.add(new CheckBox("test"));
	form.add(new CheckBox("deliveryAddress"));
	form.add(new TextField("amount")
		.add(Constants.amountSimpleAttributeModifier));
	form.add(new TextField("orderId").add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier));
	form.add(new TextField("text").add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier));
	form.add(new Button("continue") {
	    @Override
	    public void onSubmit() {
		try {

		    getMobiliserWebSession().setAutoCapture(autoCapture);
		    WebStart request = getNewMobiliserRequest(WebStart.class);
		    VatAmount vatAmount = new VatAmount();
		    vatAmount.setCurrency("EUR");
		    vatAmount.setValue(amount * 100);
		    request.setAmount(vatAmount);
		    request.setAutoCapture(autoCapture);
		    request.setOrderID(orderId);
		    request.setText(text);
		    request.setTest(test);
		    request.setUsecase(199);
		    TransactionParticipant payee = new TransactionParticipant();
		    Identifier iden = new Identifier();
		    iden.setType(1);
		    iden.setValue("202");
		    payee.setIdentifier(iden);
		    payee.setPaymentInstrumentId(20002L);
		    request.setPayee(payee);

		    request.setDeliveryAddress(deliveryAddress);

		    request.setReturnUrl(getReturnUrl());

		    WebStartResponse response = wsWebStartClient
			    .webStart(request);
		    if (!evaluateMobiliserResponse(response)) {
			return;
		    }
		    getRequestCycle().setRequestTarget(
			    new RedirectRequestTarget(getRedirectUrl(response
				    .getTransaction().getSystemId())));
		} catch (Exception e) {
		    LOG.error(
			    "#An error occurred while initializing webtransaction",
			    e);
		    error(getLocalizer().getString("webstart.error", this));
		    return;
		}

	    }
	});
	add(form);
    }

    private long calculaetSum() {
	long sum = 0;
	for (CartItem item : ((MobiliserWebSession) getSession())
		.getCartItems()) {
	    sum = sum + item.getCount() * item.getProduct().getCost();
	}
	return sum / 100;
    }

    private String formateAmount(long amount) {

	return amount + " " + "\u20AC";

    }

    public Long getAmount() {
	this.amount = calculaetSum();
	return this.amount;
    }

    public String getReturnUrl() {
	WebRequest request = getWebRequestCycle().getWebRequest();

	final String relativeURL = getPage().urlFor(
		TransactionStatusPage.class, new PageParameters()).toString();
	// TODO: this works only for servers that are accessible directly (not
	// behind a load balancer/ URL rewriter) The better way is to read the
	// 'official' Server root and application path via preference and concat
	// the relative path. For now and because we are on the same webapp only
	// return the relative part

	return relativeURL;
	// return RequestUtils.toAbsolutePath(relativeURL);
    }

    public String getRedirectUrl(Long systemId) {
	PageParameters param = new PageParameters();
	param.put("systemId", String.valueOf(systemId));
	String redirectURL = urlFor(FirstContactPage.class, param).toString();
	return redirectURL;
    }
}
