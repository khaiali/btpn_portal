package com.sybase365.mobiliser.web.consumer.pages.signup;

import org.apache.wicket.PageParameters;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.RegisterCustomerPanel;

public class PersonalDataPage extends ConsumerSignupPage {

    private static final long serialVersionUID = 1L;

    CustomerBean customer;

    public PersonalDataPage() {
	super();
	initPageComponents();
    }

    public PersonalDataPage(CustomerBean customer) {
	this.customer = customer;
	initPageComponents();
    }

    public PersonalDataPage(String msisdn) {
	super();
	this.customer.setMsisdn(msisdn);
    }

    public PersonalDataPage(final PageParameters parameters) {
	super(parameters);
    }

    public void setCustomer(CustomerBean value) {
	this.customer = value;
    }

    protected void initPageComponents() {
	if (this.customer == null) {
	    this.customer = new CustomerBean();
	}
	add(new RegisterCustomerPanel("registerCustomerPanel", this,
		this.customer));
    }
}
