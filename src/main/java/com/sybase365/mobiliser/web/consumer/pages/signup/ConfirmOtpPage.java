package com.sybase365.mobiliser.web.consumer.pages.signup;

import org.apache.wicket.markup.html.WebPage;

import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.common.panels.ConfirmOtpPanel;

public class ConfirmOtpPage extends ConsumerSignupPage {

    private static final long serialVersionUID = 1L;

    CustomerBean customer;
    WebPage backPage;
    private String sentOtp;

    public ConfirmOtpPage(CustomerBean customer, String sentOtp) {
	super();
	this.customer = customer;
	this.sentOtp = sentOtp;
	initPageComponents();

    }

    protected void initPageComponents() {
	add(new ConfirmOtpPanel("confirmOtpPanel", customer, this, sentOtp));
    }
}
