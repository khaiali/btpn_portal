package com.sybase365.mobiliser.web.consumer.pages.signup;

import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.ConfirmRegistrationPanel;

public class ConfirmDataPage extends ConsumerSignupPage {

    private static final long serialVersionUID = 1L;

    CustomerBean customer;
    WebPage backPage;

    public ConfirmDataPage(CustomerBean customer, WebPage backPage) {
	super();
	this.customer = customer;
	this.backPage = backPage;
	initPageComponents();

    }

    protected void initPageComponents() {
	add(new ConfirmRegistrationPanel("confirmRegistrationPanel", customer,
		this, this.backPage));
    }

}
