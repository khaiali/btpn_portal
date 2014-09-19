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
public class RemitMoneyFinishPage extends CustomerServicesMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(RemitMoneyConfirmPage.class);

    public RemitMoneyFinishPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */

    public RemitMoneyFinishPage(PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	Form<?> form = new Form("remittanceFinishForm",
		new CompoundPropertyModel<RemitMoneyFinishPage>(this));

	form.add(new FeedbackPanel("errorMessages"));

	add(form);

	form.add(new Label("amount", ""));
	form.add(new Label("sender", ""));
	form.add(new Label("senderMsisdn", ""));

	WebMarkupContainer wc1 = new WebMarkupContainer("remitAccMsisdnDiv");
	wc1.add(new Label("remitAccMsisdn", "")).setVisible(false);
	form.add(wc1);
	WebMarkupContainer wc3 = new WebMarkupContainer("accountHolderDiv");
	wc3.add(new Label("accountHolder", "")).setVisible(true);
	form.add(wc3);
	WebMarkupContainer wc4 = new WebMarkupContainer("accountNoDiv");
	wc4.add(new Label("accountNo", "")).setVisible(false);
	form.add(wc4);
	WebMarkupContainer wc5 = new WebMarkupContainer("institutionCodeDiv");
	wc5.add(new Label("institutionCode", "")).setVisible(false);
	form.add(wc5);
	form.add(new Label("purposeOfTransfer", ""));
	form.add(new Label("txnId", ""));
	form.add(new Label("authCode", ""));

	form.add(new Button("remittancePrint") {
	    @Override
	    public void onSubmit() {
		// To be implemented
		setResponsePage(RemittancePage.class);
	    }
	});

	form.add(new Button("remittanceContinue") {
	    @Override
	    public void onSubmit() {
		setResponsePage(RemittancePage.class);
	    }
	});

    }

    @Override
    protected Class getActiveMenu() {
	return RemittancePage.class;
    }
}
