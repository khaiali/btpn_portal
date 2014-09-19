package com.sybase365.mobiliser.web.btpn.util;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This validator checks whether amount is less than zero and throws an error.
 * 
 * @author Vikram Gunda
 */
public class BtpnAmountValidator<T> extends AbstractValidator<T> {

	private static final long serialVersionUID = 1L;

	private boolean isZeroValid = true;

	/**
	 * Constructor for this page.
	 */
	public BtpnAmountValidator() {

	}

	/**
	 * Constructor for this page.
	 */
	public BtpnAmountValidator(final boolean isZeroValid) {
		this.isZeroValid = isZeroValid;
	}

	/**
	 * Checks a value if its negative or not
	 * 
	 * @param validatable the <code>IValidatable</code> to check
	 */
	@Override
	protected void onValidate(IValidatable<T> validatable) {
		final T value = validatable.getValue();
		if (PortalUtils.exists(value)) {
			if (Long.valueOf(value.toString()) < 0) {
				error(validatable, "AmountValidator");
				return;
			}
			if (!isZeroValid && Long.valueOf(value.toString()) == 0) {
				error(validatable, "AmountValidator");
				return;
			}
		}
	}

}
