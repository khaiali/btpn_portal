package com.sybase365.mobiliser.web.consumer.pages.signup;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class CaptchaValidator extends AbstractValidator<String> {

    private static final long serialVersionUID = 1L;
    private String INVALID_CODE = "captcha.invalid";

    public void onValidate(IValidatable validatable) {
	String kaptchaReceived = (String) validatable.getValue();

	Request request = RequestCycle.get().getRequest();
	HttpServletRequest httpRequest = ((WebRequest) request)
		.getHttpServletRequest();

	String kaptchaExpected = (String) httpRequest.getSession()
		.getAttribute(
			com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);

	if (kaptchaReceived == null
		|| !kaptchaReceived.equalsIgnoreCase(kaptchaExpected)) {
	    error(validatable);
	}

    }

    // validate on numm value as well
    @Override
    public boolean validateOnNullValue() {

	return true;

    }

    @Override
    protected String resourceKey() {
	return INVALID_CODE;
    }
}