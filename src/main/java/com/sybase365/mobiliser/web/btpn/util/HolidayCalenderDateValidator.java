package com.sybase365.mobiliser.web.btpn.util;

import java.util.Date;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This validator does the from date and to date validations. Below are the validations done. 1. From date cannot be a
 * future date. 2. To date cannot be a future date. 3. From date cannot be greater than to date.
 * 
 * @author Vikram Gunda
 */
public class HolidayCalenderDateValidator extends AbstractFormValidator {

	private static final long serialVersionUID = 1L;

	private FormComponent<Date> fromDateComponent;

	private FormComponent<Date> toDateComponent;

	/**
	 * Constructor for this page.
	 * 
	 * @param fromDate the From Date Text Field to check
	 * @param toDate the To Date Text Field to check
	 */
	public HolidayCalenderDateValidator(FormComponent<Date> fromDateComponent, FormComponent<Date> toDateComponent) {
		this.fromDateComponent = fromDateComponent;
		this.toDateComponent = toDateComponent;
	}

	@Override
	public FormComponent<?>[] getDependentFormComponents() {
		return new FormComponent[] { fromDateComponent, toDateComponent };
	}

	/**
	 * Checks a value against from date and to date.
	 * 
	 * @param validatable the <code>IValidatable</code> to check
	 */
	@Override
	public void validate(Form<?> form) {
		final Date fromDate = fromDateComponent.getConvertedInput();
		final Date toDate = toDateComponent.getConvertedInput();
		final Date currentDate = new Date();
		final boolean isFromDateExist = PortalUtils.exists(fromDate);
		final boolean isToDateExist = PortalUtils.exists(toDate);
		// Check whether from date is a future date.
		if (isFromDateExist && !fromDate.after(currentDate)) {
			error(fromDateComponent, "future");
		}
		// Check whether to date is a future date.
		if (isToDateExist && !toDate.after(currentDate)) {
			error(toDateComponent, "future");
		}
		// Check whether to date is a future date.
		if (isFromDateExist && isToDateExist && fromDate.after(toDate)) {
			error(toDateComponent, "todate.after.fromdate");
		}

	}
}
