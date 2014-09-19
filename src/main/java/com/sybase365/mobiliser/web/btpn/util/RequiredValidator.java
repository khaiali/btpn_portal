package com.sybase365.mobiliser.web.btpn.util;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the required validator for bill payment page
 * 
 * @author Vikram Gunda
 */
public class RequiredValidator<T> extends AbstractValidator<T> {

	private static final long serialVersionUID = 1L;

	private final boolean isTelePhonaBillPay;

	/**
	 * Constructor for this page.
	 * 
	 * @param rg the <code>RadioGroup</code> to check
	 */
	public RequiredValidator(final boolean isTelePhonaBillPay) {
		this.isTelePhonaBillPay = isTelePhonaBillPay;
	}

	/**
	 * Checks whether value present or not.
	 * 
	 * @param validatable the <code>IValidatable</code> to check
	 */
	@Override
	protected void onValidate(IValidatable<T> validatable) {
		if (!PortalUtils.exists(validatable.getValue())) {
			error(validatable, isTelePhonaBillPay ? "phonenumber.Required" : "Required");
		}
	}
}
