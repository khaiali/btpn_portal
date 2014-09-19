package com.sybase365.mobiliser.web.util;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class AmountValidator extends AbstractValidator<String> {

    private MobiliserBasePage basePage;
    private String regex;

    public AmountValidator(MobiliserBasePage basePage, String regex) {
	this.basePage = basePage;
	this.regex = regex;
    }

    @Override
    protected void onValidate(IValidatable<String> validatable) {
	String amount = validatable.getValue();
	if (!basePage.validateAmount(amount, basePage.getMobiliserWebSession()
		.getLocale(), regex))
	    error(validatable);
    }

}
