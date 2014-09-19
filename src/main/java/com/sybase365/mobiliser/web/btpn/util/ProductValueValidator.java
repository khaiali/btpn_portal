package com.sybase365.mobiliser.web.btpn.util;

import java.util.regex.Pattern;

import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Products value validator for Manage Products page. This does the validation of the value entered based on
 * fixed interest or percent interest. portal.
 * 
 * @author Vikram Gunda
 */
public class ProductValueValidator extends AbstractValidator<String> {

	private static final long serialVersionUID = 1L;

	/** Percent Interest Regex. */
	public static final Pattern PERCENT_INTEREST_REGEX = Pattern.compile("\\d{1,3}+(\\.\\d{1,2})?");

	/** Fixed Interest Regex. */
	public static final Pattern FIXED_INTEREST_REGEX = Pattern.compile(BtpnConstants.RANGE_REGEX);

	private final RadioGroup<String> rg;

	/**
	 * Constructor for this page.
	 * 
	 * @param rg the <code>RadioGroup</code> to check
	 */
	public ProductValueValidator(final RadioGroup<String> rg) {
		this.rg = rg;
	}

	/**
	 * Checks a value against this <code>PatternValidator</code>'s {@link Pattern}.
	 * 
	 * @param validatable the <code>IValidatable</code> to check
	 */
	@Override
	protected void onValidate(IValidatable<String> validatable) {
		// Update the Radio model before fetching
		rg.updateModel();

		// Interest Type and Value
		final String interestType = rg.getModelObject();
		final String value = validatable.getValue();

		if (PortalUtils.exists(interestType) && interestType.equals(BtpnConstants.FIXED_INTEREST_RADIO)) {
			// Check fixed interest value against pattern
			if (!FIXED_INTEREST_REGEX.matcher(value).matches()) {
				error(validatable, "FixedInterest.PatternValidator");
				return;
			}
		} else if (PortalUtils.exists(interestType) && interestType.equals(BtpnConstants.PERCENT_INTEREST_RADIO)) {

			// Check percent interest value against pattern
			if (!PERCENT_INTEREST_REGEX.matcher(value).matches()) {
				error(validatable, "PercentInterest.PatternValidator");
				return;
			}

			// Percent Interest should not be greater than zero.
			final double dbValue = Double.parseDouble(value);
			if (dbValue > 100) {
				error(validatable, "PercentInterest.InterestLimit");
				return;
			}

		}
	}
}
