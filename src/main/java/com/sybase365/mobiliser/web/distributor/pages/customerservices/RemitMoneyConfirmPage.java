package com.sybase365.mobiliser.web.distributor.pages.customerservices;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_WALLET_SERVICES)
public class RemitMoneyConfirmPage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RemitMoneyConfirmPage.class);

    public RemitMoneyConfirmPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */

    public RemitMoneyConfirmPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	Form<?> form = new Form("remitConfirmForm",
		new CompoundPropertyModel<RemitMoneyConfirmPage>(this));

	form.add(new FeedbackPanel("errorMessages"));

	add(form);

	form.add(new Label("creditAmount"));
	form.add(new Label("feeAmount"));
	form.add(new Label("debitAmount"));
	form.add(new Label("accountType", ""));

	WebMarkupContainer wc1 = new WebMarkupContainer("recipientDiv");
	wc1.add(new Label("recipient", ""));
	wc1.setVisible(false);
	form.add(wc1);

	WebMarkupContainer wc2 = new WebMarkupContainer("accountHolderDiv");
	wc2.add(new Label("accountHolder", ""));
	wc2.setVisible(false);
	form.add(wc2);

	WebMarkupContainer wc3 = new WebMarkupContainer("accountNoDiv");
	wc3.add(new Label("accountNo", ""));
	wc3.setVisible(false);
	form.add(wc3);

	WebMarkupContainer wc4 = new WebMarkupContainer("institutionCodeDiv");
	wc4.add(new Label("institutionCode", ""));
	wc4.setVisible(true);
	form.add(wc4);

	form.add(new Label("purposeOfTransfer", ""));

	WebMarkupContainer wc5 = new WebMarkupContainer("fcyCurrencyDiv");
	wc5.add(new Label("fcyCurrency", ""));
	wc5.setVisible(true);
	form.add(wc5);

	WebMarkupContainer wc6 = new WebMarkupContainer("exchangeRateDiv");
	wc6.add(new Label("exchangeRate", ""));
	wc6.setVisible(false);
	form.add(wc6);

	WebMarkupContainer wc7 = new WebMarkupContainer("fcyAmountDiv");
	wc7.add(new Label("fcyAmount", ""));
	wc7.setVisible(true);
	form.add(wc7);

	form.add(new Button("remittanceCancel") {
	    @Override
	    public void onSubmit() {
		setResponsePage(RemittancePage.class);
	    }
	}.setDefaultFormProcessing(false));

	form.add(new Button("remittanceConfirm") {
	    @Override
	    public void onSubmit() {
		remittanceConfirm();
	    }
	});

    }

    private void remittanceConfirm() {
	// To be implemented
	setResponsePage(RemitMoneyFinishPage.class);
    }

     @Override
    protected Class getActiveMenu() {
	return RemittancePage.class;
    }
}
