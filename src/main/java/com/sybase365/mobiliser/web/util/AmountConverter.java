package com.sybase365.mobiliser.web.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converters.AbstractConverter;

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class AmountConverter extends AbstractConverter {

    private static final long serialVersionUID = 1L;
    private MobiliserBasePage mobiliserBasePage;
    private String fieldName;
    private String regex;
    private boolean isNumber;

    /**
     * @param mobiliserBasePage
     * @param fieldName
     *            : xml property name for corresponding field
     */
    public AmountConverter(MobiliserBasePage mobiliserBasePage, String fieldName) {
	this(mobiliserBasePage, fieldName, null, false);
    }

    public AmountConverter(MobiliserBasePage mobiliserBasePage,
	    String fieldName, String regex, boolean isNumber) {
	this.mobiliserBasePage = mobiliserBasePage;
	this.fieldName = fieldName;
	if (PortalUtils.exists(regex))
	    this.regex = regex;
	else
	    this.regex = Constants.REGEX_AMOUNT_16_2;
	this.isNumber = isNumber;
    }

    @Override
    public Object convertToObject(String value, Locale locale) {
	try {
	    if (PortalUtils.exists(value)) {
		if (mobiliserBasePage.validateAmount(value, mobiliserBasePage
			.getMobiliserWebSession().getLocale(), regex)) {
		    Long amount = null;
		    if (isNumber) {
			NumberFormat format = NumberFormat
				.getInstance(mobiliserBasePage.getLocale());
			amount = format.parse(value).longValue();

		    } else {
			amount = mobiliserBasePage.convertAmountToLong(value);
		    }

		    return amount;
		} else {
		    String errorMessage = "'"
			    + value
			    + "' "
			    + mobiliserBasePage.getLocalizer().getString(
				    "ERROR.AGENT_LIMITSET", mobiliserBasePage);

		    if (PortalUtils.exists(fieldName)) {
			errorMessage = errorMessage
				+ " "
				+ mobiliserBasePage.getLocalizer().getString(
					fieldName, mobiliserBasePage);

		    }
		    throw new ConversionException(errorMessage);
		}
	    }

	} catch (ParseException e) {
	    String errorMessage = "'"
		    + value
		    + "' "
		    + mobiliserBasePage.getLocalizer().getString(
			    "ERROR.AGENT_LIMITSET", mobiliserBasePage);

	    if (PortalUtils.exists(fieldName)) {
		errorMessage = errorMessage
			+ " "
			+ mobiliserBasePage.getLocalizer().getString(fieldName,
				mobiliserBasePage);

	    }
	    throw new ConversionException(errorMessage);
	}
	return null;
    }

    @Override
    public String convertToString(Object value, Locale locale) {
	if (PortalUtils.exists(value) && value instanceof Long) {
	    String amount = null;
	    if (isNumber) {
		NumberFormat format = NumberFormat
			.getInstance(mobiliserBasePage.getLocale());
		amount = format.format((Long) value);
	    } else {
		amount = mobiliserBasePage.convertAmountToString((Long) value);
	    }

	    return amount;
	}
	return "";
    }

    @Override
    protected Class<?> getTargetType() {
	// TODO Auto-generated method stub
	return Long.class;
    }

}