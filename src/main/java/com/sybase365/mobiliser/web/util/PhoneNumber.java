package com.sybase365.mobiliser.web.util;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The <code>PhoneNumber</code> is mainly used to transform an MSISDN between
 * national and international notation.
 * </p>
 * <p>
 * &copy; 2005 by paybox solutions AG
 * </p>
 * 
 * @author markus-hueck
 * @version $Revision: 17986 $ $Name: not supported by cvs2svn $
 */
public class PhoneNumber {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTANTS ///////////////////////////////////////////////////////////////

    /** A list of the country prefixes consisting of 2 chars. */
    private static final List<String> PREFIXES_SIZE2 = Arrays
	    .asList(new String[] {
		    // AFRICA
		    "20", "27",
		    // EUROPE
		    "30", "31", "32", "33", "34", "36", "39", "40", "41", "43",
		    "44", "45", "46", "47", "48", "49",
		    // SOUTH AMERICA
		    "51", "52", "53", "54", "55", "56", "57", "58",
		    // SOUTH PACIFIC, OCEANIA
		    "60", "61", "62", "63", "64", "65", "66",
		    // EAST ASIA
		    "81", "82", "83", "84", "86",
		    // OTHER ASIA
		    "90", "91", "92", "93", "94", "95", "98",
		    // OBSOLETE
		    "37", // German Democratic Republic
		    "38", // Yugoslavia
		    "42" // Czechoslovakia
	    });

    /** A default country code. */
    public static final String DEFAULT_COUNTRY_CODE = "49";

    // /////////////////////////////////////////////////////////////////////////
    // FIELDS //////////////////////////////////////////////////////////////////

    /** The internationalized MSISDN. */
    private final String msisdn;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS ////////////////////////////////////////////////////////////

    /**
     * <p>
     * Creates a new instance of <code>PhoneNumber</code>. Tries to parse the
     * specified MSISDN and falls back to use the specified country code if no
     * internationalised format is recognized.
     * </p>
     * 
     * @param msisdn
     *            the phone number (not necessarily a mobile phone number); e.g.
     *            +491791234567
     * @param countryCode
     *            the country code; e.g. 49
     */
    public PhoneNumber(String msisdn, String countryCode) {
	msisdn = msisdn.replaceAll("[^0-9]", "");

	// special rule for north america
	if ("1".equals(countryCode) && msisdn.length() == 10)
	    this.msisdn = "+1" + msisdn;
	else if (msisdn.startsWith("0000")) // paybox alias with additional 0
	    this.msisdn = "+" + countryCode + msisdn.substring(1);
	else if (msisdn.startsWith("000")) // starts with paybox alias
	    this.msisdn = "+" + countryCode + msisdn;
	else if (msisdn.startsWith("00")) // national country prefix
	    this.msisdn = "+" + msisdn.substring(2);
	else if (msisdn.startsWith("0")) // local network prefix
	    this.msisdn = "+" + countryCode + msisdn.substring(1);
	else
	    this.msisdn = "+" + msisdn;
    }

    /**
     * <p>
     * Creates a new instance of <code>PhoneNumber</code>. Tries to parse the
     * specified MSISDN and falls back to use the {@link #DEFAULT_COUNTRY_CODE}
     * if no internationalised format is recognized.
     * </p>
     * 
     * @param msisdn
     *            the phone number (not necessarily a mobile phone number)
     */
    public PhoneNumber(String msisdn) {
	this(msisdn, DEFAULT_COUNTRY_CODE);
    }

    // /////////////////////////////////////////////////////////////////////////
    // METHODS /////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Returns the phone number in international form (e.g. +491701234567).
     * </p>
     * 
     * @return the MSISDN in international format
     */
    public String getInternationalFormat() {
	return this.msisdn;
    }

    /**
     * <p>
     * Returns the phone number in numeric international form (e.g.
     * 00491701234567 for +491701234567).
     * </p>
     * 
     * @return the MSISDN in numeric international format
     */
    public String getNumericInternationalFormat() {
	return "00" + this.msisdn.substring(1);
    }

    /**
     * <p>
     * Returns the phone number in a truncated international form (e.g.
     * 491701234567 for +491701234567).
     * </p>
     * 
     * @return the MSISDN in truncated international format
     */
    public String getShortInternationalFormat() {
	return this.msisdn.substring(1);
    }

    /**
     * <p>
     * Returns the phone number in the national form.<br/>
     * An example is 01701234567 for the phone number +491701234567
     * </p>
     * 
     * @return the MSISDN in national format
     */
    public String getNationalFormat() {
	int chars = 3;
	// North America, Russia or Kazakhstan
	if (this.msisdn.charAt(1) == '1' || this.msisdn.charAt(1) == '7')
	    chars = 1;
	if (PREFIXES_SIZE2.contains(this.msisdn.substring(1, 3)))
	    chars = 2;
	return "0" + this.msisdn.substring(chars + 1);
    }

    // java.lang.Object/////////////////////////////////////////////////////////

    /**
     * <p>
     * Returns a string representation of this instance.
     * </p>
     * 
     * @return {@link #getInternationalFormat()}
     */
    @Override
    public String toString() {
	return getInternationalFormat();
    }

    /**
     * <p>
     * Indicates if another object is <i>equal to</i> this one.
     * </p>
     * 
     * @param o
     *            the <code>Object</code> to compare with
     * @return <code>true</code> if <code>o</code> is a <code>PhoneNumber</code>
     *         representing the same MSISDNF
     */
    @Override
    public boolean equals(Object o) {
	if (!(o instanceof PhoneNumber))
	    return false;
	return getInternationalFormat().equals(
		((PhoneNumber) o).getInternationalFormat());
    }

    /**
     * <p>
     * Returns a hash value.
     * </p>
     * 
     * @return a hash value for this instance
     */
    @Override
    public int hashCode() {
	return getInternationalFormat().hashCode();
    }

}