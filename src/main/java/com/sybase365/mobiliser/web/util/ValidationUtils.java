package com.sybase365.mobiliser.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class ValidationUtils {
    private static final Logger LOG = LoggerFactory
	    .getLogger(ValidationUtils.class);

    private static Pattern CARD_NUMBER_PATTERN = Pattern.compile("^[0-9]*$");

    public static boolean isLuhnCheck(String cardNumber) {
	if (!(CARD_NUMBER_PATTERN.matcher(cardNumber).matches())) {
	    return false;
	}
	int sum = 0;

	boolean alternate = false;
	for (int i = cardNumber.length() - 1; i >= 0; --i) {
	    int n = Integer.parseInt(cardNumber.substring(i, i + 1));
	    if (alternate) {
		n *= 2;
		if (n > 9)
		    n = n % 10 + 1;
	    }
	    sum += n;
	    alternate = !(alternate);
	}

	return (sum % 10 == 0);
    }

    @Deprecated
    public static Calendar isValidGermanDate(String date) {
	return isValidGermanDate(date, TimeZone.getDefault());
    }

    public static Calendar isValidGermanDate(String date, TimeZone timeZone) {
	String mask = "^[0-3]?[0-9]\\.[0-1]?[0-9]\\.([1-2][0-9]{3}|[0-9]{2})$";

	Calendar c = null;
	try {
	    c = isValidDate(date, mask, "\\.", DateFields.DAY,
		    DateFields.MONTH, DateFields.YEAR, timeZone);
	} catch (DateValidationException e) {
	    LOG.warn("Invalid date {}", date);
	}

	return c;
    }

    @Deprecated
    public static Calendar isValidEnglishDate(String date) {
	return isValidEnglishDate(date, TimeZone.getDefault());
    }

    public static Calendar isValidEnglishDate(String date, TimeZone timeZone) {
	String mask = "^[0-1]?[0-9]\\.[0-3]?[0-9]\\.([1-2][0-9]{3}|[0-9]{2})$";

	Calendar c = null;
	try {
	    c = isValidDate(date, mask, "\\.", DateFields.MONTH,
		    DateFields.DAY, DateFields.YEAR, timeZone);
	} catch (DateValidationException e) {
	    LOG.info("Date Validation error occured", e);
	}

	return c;
    }

    @Deprecated
    public static Calendar isValidDate(String date, String[] formats) {
	return isValidDate(date, formats, TimeZone.getDefault());
    }

    public static Calendar isValidDate(String date, String[] formats,
	    TimeZone timeZone) {
	if ((!(PortalUtils.exists(date))) || (!(PortalUtils.exists(formats)))) {
	    return null;
	}
	for (String format : formats) {
	    try {
		DateFormat df = new SimpleDateFormat(format);
		df.setTimeZone(timeZone);
		Date d = df.parse(date);
		String s = df.format(d);

		if (date.equals(s)) {
		    Calendar c = Calendar.getInstance(timeZone);
		    c.setTime(d);
		    return c;
		}
	    } catch (Exception e) {
		LOG.info("Date Validation error occured", e);
	    }
	}

	return null;
    }

    @Deprecated
    public static Calendar isValidDate(String date, List<Property> list)
	    throws ValidationUtils.DateValidationException {
	return isValidDate(date, list, TimeZone.getDefault());
    }

    public static Calendar isValidDate(String date, List<Property> list,
	    TimeZone timeZone) throws ValidationUtils.DateValidationException {
	if (!(PortalUtils.exists(list))) {
	    return null;
	}
	Calendar c = null;
	for (Property p : list) {
	    c = isValidDate(date, p.getFormatExpression(),
		    p.getSplitExpression(), p.getPos1(), p.getPos2(),
		    p.getPos3(), timeZone);

	    if (c != null) {
		return c;
	    }
	}
	return null;
    }

    @Deprecated
    public static Calendar isValidDate(String date, String formatExpression,
	    String splitExpression, DateFields pos1, DateFields pos2,
	    DateFields pos3) throws ValidationUtils.DateValidationException {
	return isValidDate(date, formatExpression, splitExpression, pos1, pos2,
		pos3, TimeZone.getDefault());
    }

    public static Calendar isValidDate(String date, String formatExpression,
	    String splitExpression, DateFields pos1, DateFields pos2,
	    DateFields pos3, TimeZone timeZone)
	    throws ValidationUtils.DateValidationException {
	try {
	    if ((!(PortalUtils.exists(date)))
		    || (!(PortalUtils.exists(splitExpression)))
		    || ((PortalUtils.exists(formatExpression)) && (!(date
			    .matches(formatExpression))))) {
		return null;
	    }
	    String[] parts = date.split(splitExpression);
	    if (parts.length != 3) {
		return null;
	    }

	    if (!(isDisjunct(pos1.dateType, pos2.dateType, pos3.dateType))) {
		throw new ValidationUtils.DateValidationException(
			"The three position parameters pos1, pos2 and pos3 are not disjunct.");
	    }

	    int day = (pos3 == DateFields.DAY) ? Integer.parseInt(parts[2])
		    : (pos2 == DateFields.DAY) ? Integer.parseInt(parts[1])
			    : (pos1 == DateFields.DAY) ? Integer
				    .parseInt(parts[0]) : -1;

	    if (day == -1) {
		throw new ValidationUtils.DateValidationException(
			"invalid positioning for DAY");
	    }

	    int month = (pos3 == DateFields.MONTH) ? Integer.parseInt(parts[2])
		    : (pos2 == DateFields.MONTH) ? Integer.parseInt(parts[1])
			    : (pos1 == DateFields.MONTH) ? Integer
				    .parseInt(parts[0]) : -1;

	    if (month == -1) {
		throw new ValidationUtils.DateValidationException(
			"invalid positioning for MONTH");
	    }

	    int year = (pos3 == DateFields.YEAR) ? Integer.parseInt(parts[2])
		    : (pos2 == DateFields.YEAR) ? Integer.parseInt(parts[1])
			    : (pos1 == DateFields.YEAR) ? Integer
				    .parseInt(parts[0]) : -1;

	    if (year == -1) {
		throw new ValidationUtils.DateValidationException(
			"invalid positioning for YEAR");
	    }

	    return isValidDate(day, month, year, timeZone);
	} catch (DateValidationException dve) {
	    throw dve;
	} catch (Exception e) {
	    LOG.info("Date Validation error occured", e);
	}
	return null;
    }

    @Deprecated
    public static Calendar isValidDate(int day, int month, int year) {
	return isValidDate(day, month, year, TimeZone.getDefault());
    }

    public static Calendar isValidDate(int day, int month, int year,
	    TimeZone timeZone) {
	Calendar c = Calendar.getInstance(timeZone);
	c.set(5, day);
	c.set(2, month - 1);
	c.set(1, year);

	c.add(6, -1);
	c.set(11, 23);
	c.set(12, 59);
	c.set(13, 59);
	c.set(14, 999);
	c.add(14, 1);

	if ((c.get(5) == day) && (c.get(2) == month - 1) && (c.get(1) == year)) {
	    return c;
	}

	return null;
    }

    public static String[] getFormatVariants(String format, String delimiter)
	    throws ValidationUtils.DateValidationException {
	if (!(PortalUtils.exists(format))) {
	    throw new ValidationUtils.DateValidationException(
		    "format mut not be null or an empty String");
	}
	if (!(PortalUtils.exists(delimiter))) {
	    throw new ValidationUtils.DateValidationException(
		    "delimiter mut not be null or an empty String");
	}
	try {
	    List l = new ArrayList();

	    int indexP1 = format.indexOf(delimiter) + delimiter.length();
	    int indexP2 = format.lastIndexOf(delimiter) + delimiter.length();

	    if ((indexP1 < 0) || (indexP2 <= 0) || (indexP1 == indexP2)) {
		throw new IllegalArgumentException("no valid format expression");
	    }
	    String first = format.substring(0, 1);
	    String second = format.substring(indexP1, indexP1 + 1);
	    String third = format.substring(indexP2, indexP2 + 1);

	    if (first.equalsIgnoreCase("y")) {
		l.add(first + first + first + first + delimiter + second
			+ second + delimiter + third + third);

		l.add(first + first + first + first + delimiter + second
			+ delimiter + third + third);

		l.add(first + first + first + first + delimiter + second
			+ second + delimiter + third);

		l.add(first + first + first + first + delimiter + second
			+ delimiter + third);

		l.add(first + first + delimiter + second + second + delimiter
			+ third + third);

		l.add(first + first + delimiter + second + delimiter + third
			+ third);

		l.add(first + first + delimiter + second + second + delimiter
			+ third);

		l.add(first + first + delimiter + second + delimiter + third);
	    } else if (second.equalsIgnoreCase("y")) {
		l.add(first + first + delimiter + second + second + second
			+ second + delimiter + third + third);

		l.add(first + delimiter + second + second + second + second
			+ delimiter + third + third);

		l.add(first + first + delimiter + second + second + second
			+ second + delimiter + third);

		l.add(first + delimiter + second + second + second + second
			+ delimiter + third);

		l.add(first + first + delimiter + second + second + delimiter
			+ third + third);

		l.add(first + delimiter + second + second + delimiter + third
			+ third);

		l.add(first + first + delimiter + second + second + delimiter
			+ third);

		l.add(first + delimiter + second + second + delimiter + third);
	    } else if (third.equalsIgnoreCase("y")) {
		l.add(first + first + delimiter + second + second + delimiter
			+ third + third + third + third);

		l.add(first + delimiter + second + second + delimiter + third
			+ third + third + third);

		l.add(first + first + delimiter + second + delimiter + third
			+ third + third + third);

		l.add(first + delimiter + second + delimiter + third + third
			+ third + third);

		l.add(first + first + delimiter + second + second + delimiter
			+ third + third);

		l.add(first + delimiter + second + second + delimiter + third
			+ third);

		l.add(first + first + delimiter + second + delimiter + third
			+ third);

		l.add(first + delimiter + second + delimiter + third + third);
	    } else {
		throw new ValidationUtils.DateValidationException(
			"no valid format expression");
	    }
	    return ((String[]) l.toArray(new String[l.size()]));
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ValidationUtils.DateValidationException(e);
	}
    }

    private static boolean isDisjunct(int a, int b, int c) {
	return ((a != b) && (a != c) && (b != c));
    }

    public static class Property {
	private String formatExpression;
	private String splitExpression;
	private ValidationUtils.DateFields pos1;
	private ValidationUtils.DateFields pos2;
	private ValidationUtils.DateFields pos3;

	public Property() {
	    this(null, null, null, null, null);
	}

	public Property(String formatExpression, String splitExpression,
		ValidationUtils.DateFields pos1,
		ValidationUtils.DateFields pos2, ValidationUtils.DateFields pos3) {
	    this.formatExpression = formatExpression;
	    this.splitExpression = splitExpression;
	    this.pos1 = pos1;
	    this.pos2 = pos2;
	    this.pos3 = pos3;
	}

	public String getFormatExpression() {
	    return this.formatExpression;
	}

	public void setFormatExpression(String formatExpression) {
	    this.formatExpression = formatExpression;
	}

	public String getSplitExpression() {
	    return this.splitExpression;
	}

	public void setSplitExpression(String splitExpression) {
	    this.splitExpression = splitExpression;
	}

	public ValidationUtils.DateFields getPos1() {
	    return this.pos1;
	}

	public void setPos1(ValidationUtils.DateFields pos1) {
	    this.pos1 = pos1;
	}

	public ValidationUtils.DateFields getPos2() {
	    return this.pos2;
	}

	public void setPos2(ValidationUtils.DateFields pos2) {
	    this.pos2 = pos2;
	}

	public ValidationUtils.DateFields getPos3() {
	    return this.pos3;
	}

	public void setPos3(ValidationUtils.DateFields pos3) {
	    this.pos3 = pos3;
	}
    }

    public static class DateValidationException extends Exception {
	private static final long serialVersionUID = "DateValidationException"
		.hashCode();

	public DateValidationException(Throwable t) {
	    super(t);
	}

	public DateValidationException(String message) {
	    super(message);
	}
    }

    public static enum DateFields {
	DAY(1), MONTH(0), YEAR(2);

	public int dateType;

	DateFields(int type) {
	    this.dateType = type;
	}

	public static DateFields parse(int dateType) {
	    for (DateFields df : values()) {
		if (df.dateType == dateType)
		    return df;
	    }
	    throw new IllegalArgumentException("The value [" + dateType
		    + "] represents no DateField!");
	}
    }
}
