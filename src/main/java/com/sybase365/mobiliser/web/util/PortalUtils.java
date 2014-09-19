package com.sybase365.mobiliser.web.util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BankAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.CreditCard;
import com.sybase365.mobiliser.util.tools.encryptionutils.AsymmetricKeyUtils;
import com.sybase365.mobiliser.util.tools.encryptionutils.EncryptionException;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;

/**
 * <p>
 * The <code>PayboxUtils</code> provide convenient methods related to everything and anything.
 * </p>
 * <p>
 * &copy 2000-2006 by paybox solutions AG
 * 
 * @copy 2011 by Sybase365, an SAP Company
 *       </p>
 * @author <a href='mailto:Sebastian.Kirsch@paybox.net'>Sebastian Kirsch</a>
 * @author <a href='mailto:Dirk.Grosskopf@paybox.net'>Dirk Grosskopf</a>
 */
public class PortalUtils {

	private static final Logger LOG = LoggerFactory.getLogger(PortalUtils.class);

	/** Static string containing all vocals */
	private static String VOCALS = "aeiou ";

	private static WeakReference<DatatypeFactory> datatypeFactory;

	/**
	 * <p>
	 * Pattern used to match credit card numbers to ensure that the given number contains digits only
	 * </p>
	 */
	private static Pattern CARD_NUMBER_PATTERN = Pattern.compile("^[0-9 ]*$");

	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS //////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Returns a new <code>array</code> stripped of all <code>null</code> elements.
	 * </p>
	 * <p>
	 * The method returns <code>null</code> either if the specified argument is <code>null</code> or the
	 * <code>array</code> to return would be 0-lengthed.
	 * </p>
	 * 
	 * @param array the <code>array</code> to rid of <code>null</code> s
	 * @return an <code>Array</code> with the same component type like the specified one or <code>null</code>
	 * @since 1.46
	 */
	public static Object[] removeNulls(Object[] array) {
		if (array == null || array.length == 0)
			return null;
		int nulls = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null)
				nulls++;
		}
		Object[] newA = (Object[]) Array.newInstance(array.getClass().getComponentType(), array.length - nulls);
		if (nulls == 0) {
			System.arraycopy(array, 0, newA, 0, array.length);
			return array;
		}
		int newIndex = 0, lastGood = 0;
		int i;
		for (i = 0; i < array.length; i++) {
			if (array[i] == null) {
				System.arraycopy(array, lastGood, newA, newIndex, i - lastGood);
				newIndex += i - lastGood;
				lastGood = i + 1;
			}
		}
		// copy last elements
		System.arraycopy(array, lastGood, newA, newIndex, i - lastGood);
		return newA.length > 0 ? newA : null;
	}

	/**
	 * <p>
	 * Removes all <code>null</code> elements from the specified collection.
	 * </p>
	 * 
	 * @param collection the <code>Collection</code> to rid of <code>null</code> s
	 * @since CVS 1.58
	 */
	public static void removeNullObjects(Collection<?> collection) {
		if (collection == null)
			return;
		for (Iterator<?> it = collection.iterator(); it.hasNext();) {
			if (it.next() == null)
				it.remove();
		}
	}

	/**
	 * <p>
	 * Indicates if a string exists:
	 * </p>
	 * <ol>
	 * <li>it is not <code>null</code></li>
	 * <li>its trimmed length is greater than <tt>0</tt></li>
	 * </ol>
	 * 
	 * @param s the <code>String</code> to examine
	 * @return <code>true</code> if <code>s</code> exists; <code>false</code> otherwise
	 */
	public static boolean exists(String s) {
		return s != null && s.trim().length() > 0;
	}

	/**
	 * <p>
	 * Indicates if a collection exists:
	 * </p>
	 * <ol>
	 * <li>it is not <code>null</code></li>
	 * <li>it is not {@link Collection#isEmpty() empty}</li>
	 * </ol>
	 * 
	 * @param c the <code>Collection</code> to examine
	 * @return <code>true</code> if <code>c</code> exists; <code>false</code> otherwise
	 */
	public static boolean exists(Collection<?> c) {
		return c != null && !c.isEmpty();
	}

	/**
	 * <p>
	 * Indicates if a map exists:
	 * </p>
	 * <ol>
	 * <li>it is not <code>null</code></li>
	 * <li>it is not {@link Map#isEmpty() empty}</li>
	 * </ol>
	 * 
	 * @param m the <code>Map</code> to examine
	 * @return <code>true</code> if <code>m</code> exists; <code>false</code> otherwise
	 */
	public static boolean exists(Map<?, ?> m) {
		return m != null && !m.isEmpty();
	}

	/**
	 * <p>
	 * Indicates if an array exists:
	 * </p>
	 * <ol>
	 * <li>it is not <code>null</code></li>
	 * <li>its length is greater than <tt>0</tt></li>
	 * </ol>
	 * 
	 * @param a the <code>array</code> to examine
	 * @return <code>true</code> if <code>a</code> exists; <code>false</code> otherwise
	 */
	public static boolean exists(Object[] a) {
		return a != null && a.length > 0;
	}

	/**
	 * <p>
	 * Indicates if a file exists:
	 * </p>
	 * <ol>
	 * <li>it is not <code>null</code></li>
	 * <li>{@link File#exists()} returns <code>true</code></li>
	 * </ol>
	 * 
	 * @param f the <code>File</code> to examine
	 * @return <code>true</code> if <code>f</code> exists; <code>false</code> otherwise
	 */
	public static boolean exists(File f) {
		return f != null && f.exists();
	}

	/**
	 * <p>
	 * Indicates if an object exists. In general, an object exists if it is not <code>null</code>. For some types, there
	 * are more specific rules (see list).
	 * </p>
	 * 
	 * @param o the <code>Object</code> to examine
	 * @return <code>true</code> if <code>o</code> exists; <code>false</code> otherwise
	 * @see #exists(Collection)
	 * @see #exists(File)
	 * @see #exists(Map)
	 * @see #exists(Object[])
	 * @see #exists(String)
	 */
	public static boolean exists(Object o) {
		if (o instanceof Collection)
			return exists((Collection<?>) o);
		if (o instanceof File)
			return exists((File) o);
		if (o instanceof Map)
			return exists((Map<?, ?>) o);
		if (o instanceof Object[])
			return exists((Object[]) o);
		if (o instanceof String)
			return exists((String) o);
		return o != null;
	}

	/**
	 * Compresses the specified <code>text</code> by removing at least the lower case consonant, keeping any vocal
	 * (a,e,i,o,u).
	 * <p>
	 * If the <code>leaveUpperCaseLetters</code> flag is set to <code>false</code>, also the upper case consonants are
	 * removed.
	 * 
	 * @param text The text to compress.
	 * @param leaveUpperCaseLetters Flag, which inidcates whether or not to keep upper case vocals.
	 * @return The compresses texts without the consonants respecting the <code>leaveUpperCaseLetters</code> flag.
	 */
	public static String compress(String text, boolean leaveUpperCaseLetters) {
		StringBuffer buf = new StringBuffer();
		char c;

		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);

			if (Character.isLetterOrDigit(c)
					&& ((leaveUpperCaseLetters && Character.isUpperCase(c)) || (VOCALS.indexOf(c) == -1))) {
				buf.append(c);
			}
		}

		return buf.toString();
	}

	/**
	 * <p>
	 * Returns a String listing all array elements.
	 * </p>
	 * 
	 * @param array the <code>Array</code> to dump
	 * @param delimiter a String delimiting the single elements
	 * @param defaultValue the value to return if the <code>Array</code> is <code>null</code>, empty or no
	 *            <code>Array</code> at all
	 * @return a <code>String</code> listing the elements; or the default value
	 * @since 1.51
	 */
	public static String dump(Object array, String delimiter, String defaultValue) {
		if (!exists(array))
			return defaultValue;
		if (array instanceof Object[])
			return dump((Object[]) array, delimiter, defaultValue);
		if (array.getClass().isArray()) {
			int length = Array.getLength(array);
			StringBuffer buf = new StringBuffer(length * 25);
			int i = 0;
			while (true) {
				buf.append(Array.get(array, i));
				if (++i < length)
					buf.append(delimiter);
				else return buf.toString();
			}
		}
		return defaultValue;
	}

	/**
	 * <p>
	 * Returns a String listing all array elements.
	 * </p>
	 * 
	 * @param array the <code>Array</code> to dump
	 * @param delimiter a String delimiting the single elements
	 * @param defaultValue the value to return if the <code>Array</code> is <code>null</code> or empty
	 * @return a <code>String</code> listing the elements; or the default value
	 * @since 1.51
	 */
	public static String dump(Object[] array, String delimiter, String defaultValue) {
		if (exists(array))
			return defaultValue;
		StringBuffer buf = new StringBuffer(array.length * 25);
		int i = 0;
		while (true) {
			buf.append(array[i]);
			if (++i < array.length)
				buf.append(delimiter);
			else return buf.toString();
		}
	}

	/**
	 * <p>
	 * Performs an existence check and returns the <i>existing</i> value or a default value.
	 * </p>
	 * 
	 * @param <T> the value's type
	 * @param value the value to examine and return if it exists
	 * @param defaultValue the value returned if <code>value</code> does not exist
	 * @return <code>value</code> if it exists; <code>defaultValue</code> otherwise
	 * @see #exists(Object)
	 */
	public static <T> T getValue(T value, T defaultValue) {
		return exists(value) ? value : defaultValue;
	}

	/**
	 * <p>
	 * Returns the superordinate locale. For language-only locales, no superior locale exists and thus <code>null</code>
	 * is returned.
	 * </p>
	 * 
	 * @param l the <code>Locale</code> to examine
	 * @return the superordinate <code>Locale</code> for <code>l</code> or <code>null</code> if no such
	 *         <code>Locale</code> exists
	 */
	public static Locale getSuperior(Locale l) {
		if (l.getVariant().length() > 0)
			return new Locale(l.getLanguage(), l.getCountry());
		if (l.getCountry().length() > 0)
			return new Locale(l.getLanguage());
		return null;
	}

	/**
	 * <p>
	 * Creates a merged array including the specified parameters.
	 * </p>
	 * 
	 * @param <T> the array's type
	 * @param array the array to merge
	 * @param element the element to add to the array
	 * @param position the position at which <code>element</code> should be added; automatically set to be greater than
	 *            or equal <tt>0</tt> resp. smaller than or equal <code>array</code>'s length
	 * @return <ul>
	 *         <li><code>array</code> if <code>element</code> is <code>null</code></li>
	 *         <li>an array of size 1 containing <code>element</code> if <code>array</code> is <code>null</code></li>
	 *         <li>an array of <code>T</code> being one element bigger than <code>array</code>, having
	 *         <code>element</code> at the specified position</li>
	 *         </ul>
	 */
	public static <T> T[] merge(T[] array, T element, int position) {
		if (element == null)
			return array;
		if (array == null) {
			@SuppressWarnings("unchecked")
			T[] arr = (T[]) Array.newInstance(element.getClass(), 1);
			arr[0] = element;
			return arr;
		}
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + 1);
		// ensure 0 <= position <= arr.length
		position = Math.max(0, Math.min(position, array.length));
		if (position > 0)
			System.arraycopy(array, 0, arr, 0, Math.min(array.length, position));
		if (position < array.length)
			System.arraycopy(array, position, arr, position + 1, array.length - position);
		arr[position] = element;
		return arr;
	}

	/**
	 * <p>
	 * Merges two arrays into one. The two arrays need to have the same component type. The resulting array will consist
	 * of the first array having the second array appended.
	 * </p>
	 * 
	 * @param array1 the first array
	 * @param array2 the second array
	 * @return <ul>
	 *         <li><tt>array2</tt> if <tt>array1</tt> is <code>null</code></li>
	 *         <li><tt>array1</tt> if <tt>array2</tt> is <code>null</code></li>
	 *         <li>a merged array otherwise</li>
	 *         </ul>
	 */
	public static <T> T[] merge(T[] array1, T[] array2) {
		if (array1 == null)
			return array2;
		if (array2 == null)
			return array1;
		int length1 = Array.getLength(array1), length2 = Array.getLength(array2);
		@SuppressWarnings("unchecked")
		T[] newArray = (T[]) Array.newInstance(array1.getClass().getComponentType(), length1 + length2);
		System.arraycopy(array1, 0, newArray, 0, length1);
		System.arraycopy(array2, 0, newArray, length1, length2);
		return newArray;
	}

	/**
	 * <p>
	 * Copies the specified array.
	 * </p>
	 * 
	 * @param <T> the array's type
	 * @param array the array to copy
	 * @return the copied array
	 */
	public static <T> T[] copy(T[] array) {
		if (array == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);
		System.arraycopy(array, 0, arr, 0, array.length);
		return arr;
	}

	/**
	 * Generates a <code>String</code> containing random characters.<br/>
	 * Characters used are A-Z, a-z, 0-9.<br/>
	 * This method can be used to generate passwords, pins, otps, ...
	 * 
	 * @param length specifies the length of the <code>String</cdoe> that has to be generated.
	 * @return a <code>String</code> of the size of <code>length</code> containing random characters.
	 */
	public static String generateRandomString(int length) {
		char[] alphabet = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
				'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
				'4', '5', '6', '7', '8', '9' };

		return generateRandomString(length, alphabet);
	}

	/**
	 * Generates a <code>String</code> containing random characters.<br/>
	 * This method can be used to generate passwords, pins, otps, ...
	 * 
	 * @param length specifies the length of the <code>String</code> that has to be generated.
	 * @param alphabet specifies the character source from which the <code>String</code> should be generated.
	 * @return a <code>String</code> of the size of <code>length</code> containing random characters from the source
	 *         defined by <code>alphabet</code>.
	 */
	public static String generateRandomString(int length, char[] alphabet) {

		int range = alphabet.length;
		Random r = new Random();
		StringBuffer sb = new StringBuffer(length);

		for (int i = 0; i < length; i++)
			sb.append(alphabet[r.nextInt(range)]);

		return sb.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS ////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Prevents instantiation.
	 * </p>
	 */
	private PortalUtils() {
		super();
	}

	public static DatatypeFactory getDatatypeFactory() {
		if ((datatypeFactory == null) || (datatypeFactory.get() == null)) {
			try {
				DatatypeFactory dF = DatatypeFactory.newInstance();
				datatypeFactory = new WeakReference(dF);
			} catch (DatatypeConfigurationException dCE) {
				throw new RuntimeException("Check yor JVM settings!", dCE);
			}
		}
		return datatypeFactory.get();
	}

	public static String getFormattedDate(XMLGregorianCalendar date, Locale locale) {
		if (date == null)
			return "";
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", locale);
		return formatter.format(FormatUtils.getSaveDate(date));
	}

	public static String getMMDDYYYYDate(XMLGregorianCalendar date, TimeZone tz) {
		if (date == null)
			return "";
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		return formatter.format(FormatUtils.getSaveDate(date));
	}

	public static String getFormattedDateTime(XMLGregorianCalendar date, Locale locale) {
		if (date == null)
			return "";
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
		return formatter.format(FormatUtils.getSaveDate(date));
	}

	public static String getFormattedDateTime(XMLGregorianCalendar date, Locale locale, TimeZone timeZone) {

		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
		if (PortalUtils.exists(timeZone))
			formatter.setTimeZone(timeZone);
		return formatter.format(FormatUtils.getSaveDate(date));
	}

	public static Date getSaveDate(XMLGregorianCalendar date) {
		return (FormatUtils.getSaveDate(date));
	}

	public static String getFormattedTime(XMLGregorianCalendar date, Locale locale) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", locale);
		return formatter.format(FormatUtils.getSaveDate(date));
	}

	// -------------------------- FOR CREDIT CARD ENCRYPTION

	public static void encryptCreditCard(CreditCard card, String pubKeyStore, String pubKeyAlias, String keyStorePass) {

		PublicKey publicKey;
		try {
			publicKey = AsymmetricKeyUtils.getPublicKey(pubKeyStore, keyStorePass, pubKeyAlias);
		} catch (KeyStoreException e) {
			LOG.warn("Cannot retrieve public key from keystore. Please check the preference configuration", e);
			if (LOG.isInfoEnabled())
				LOG.info("-->Do not encrypt card information");
			return;
		}

		// set display number before encryption:
		card.setDisplayNumber("xxxxxxxxxxxx" + card.getCardNumber().substring(card.getCardNumber().length() - 4));
		// encrypt card number and security code
		try {
			card.setCardNumber(AsymmetricKeyUtils.encrypt(card.getCardNumber(), publicKey));
			card.setSecurityNumber(AsymmetricKeyUtils.encrypt(card.getSecurityNumber(), publicKey));
		} catch (EncryptionException ex) {
			LOG.warn("Cannot encrypt card information. Please check the preference configuration", ex);
			return;
		}
	}

	public static void encryptBankAccount(BankAccount account, String pubKeyStore, String pubKeyAlias,
		String keyStorePass) {

		PublicKey publicKey;
		try {
			publicKey = AsymmetricKeyUtils.getPublicKey(pubKeyStore, keyStorePass, pubKeyAlias);
		} catch (KeyStoreException e) {
			LOG.warn("Cannot retrieve public key from keystore. Please check the preference configuration", e);
			if (LOG.isInfoEnabled())
				LOG.info("--> Do not encrypt bank information");
			return;
		}

		// set display number before encryption:
		account.setDisplayNumber("xxxxxxxxxxxx"
				+ account.getAccountNumber().substring(account.getAccountNumber().length() - 4));
		// encrypt number
		try {
			account.setAccountNumber(AsymmetricKeyUtils.encrypt(account.getAccountNumber(), publicKey));
		} catch (EncryptionException e) {
			LOG.warn("Cannot encrypt bank information. Please check the preference configuration", e);
			return;
		}
	}

	public static XMLGregorianCalendar getTimeFrameFromDate(Integer timeframe, TimeZone timeZone) {

		Calendar now = Calendar.getInstance(timeZone);

		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		switch (timeframe.intValue()) {
		case Constants.TXN_TIMEFRAME_ALL:
			return null;
		case Constants.TXN_TIMEFRAME_LAST_THREE_MONTH:
			now.add(Calendar.MONTH, -3);
			break;
		case Constants.TXN_TIMEFRAME_LAST_MONTH:
			now.add(Calendar.MONTH, -1);
			break;
		case Constants.TXN_TIMEFRAME_LAST_TEN_DAYS:
			now.add(Calendar.DAY_OF_MONTH, -10);
			break;
		case Constants.TXN_TIMEFRAME_LAST_WEEK:
			now.add(Calendar.WEEK_OF_MONTH, -1);
			break;
		case Constants.TXN_TIMEFRAME_LAST_TWO_DAYS:
			now.add(Calendar.DAY_OF_MONTH, -2);
			break;
		}

		return PortalUtils.getSaveXMLGregorianCalendar(now);
	}

	public static XMLGregorianCalendar getTimeFrameToDate(TimeZone timeZone) {
		Calendar now = Calendar.getInstance(timeZone);

		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND, 999);

		return PortalUtils.getSaveXMLGregorianCalendar(now);
	}

	/**
	 * <p>
	 * Checks a credit card number for validity
	 * </p>
	 * 
	 * @param cardNumber the credit card number (as a <code>String</code>) that has to be checked
	 * @return <code>true</code> - if the given credit card number is valid<br/>
	 *         <code>false</code> - otherwise
	 */
	public static boolean isLuhnCheck(String cardNumber) {

		if (!CARD_NUMBER_PATTERN.matcher(cardNumber).matches())
			return false;

		cardNumber = cardNumber.replaceAll(" ", "");

		int sum = 0;

		boolean alternate = false;
		for (int i = cardNumber.length() - 1; i >= 0; i--) {

			int n = Integer.parseInt(cardNumber.substring(i, i + 1));
			if (alternate) {
				n *= 2;
				if (n > 9)
					n = (n % 10) + 1;
			}
			sum += n;
			alternate = !alternate;
		}

		return (sum % 10 == 0);
	}

	public static XMLGregorianCalendar getSaveXMLGregorianCalendar(final Calendar date) {
		if (date == null) {
			return null;
		}

		try {
			final GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeZone(date.getTimeZone());
			gc.setTimeInMillis(date.getTimeInMillis());

			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		} catch (final Exception e) {
			return null;
		}
	}

	public static XMLGregorianCalendar getSaveXMLGregorianCalendarFromDate(final Date date, TimeZone timeZone) {
		if (date == null) {
			return null;
		}

		try {
			Calendar cTemp = Calendar.getInstance();
			cTemp.setTime(date);
			Calendar cFrom = PortalUtils.changeTimeZone(cTemp, timeZone);
			final GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeZone(cFrom.getTimeZone());
			gc.setTimeInMillis(cFrom.getTimeInMillis());

			XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			xmlCal.setHour(0);
			xmlCal.setMinute(0);
			xmlCal.setSecond(0);
			xmlCal.setMillisecond(0);
			return xmlCal;
		} catch (final Exception e) {
			return null;
		}
	}

	public static XMLGregorianCalendar getSaveXMLGregorianCalendarToDate(final Date date, TimeZone timeZone) {
		if (date == null) {
			return null;
		}

		try {
			Calendar cTemp = Calendar.getInstance();
			cTemp.setTime(date);
			Calendar cFrom = PortalUtils.changeTimeZone(cTemp, timeZone);
			final GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeZone(cFrom.getTimeZone());
			gc.setTimeInMillis(cFrom.getTimeInMillis());

			XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			xmlCal.setHour(23);
			xmlCal.setMinute(59);
			xmlCal.setSecond(59);
			xmlCal.setMillisecond(999);
			return xmlCal;
		} catch (final Exception e) {
			return null;
		}
	}

	public static void swapFromAndToDate(XMLGregorianCalendar fromDateXml, XMLGregorianCalendar toDateXml) {
		if (fromDateXml.compare(toDateXml) == DatatypeConstants.GREATER) {
			XMLGregorianCalendar temp = fromDateXml;
			fromDateXml = toDateXml;
			toDateXml = temp;
		}
		fromDateXml.setHour(0);
		fromDateXml.setMinute(0);
		fromDateXml.setSecond(0);
		fromDateXml.setMillisecond(0);
		toDateXml.setHour(23);
		toDateXml.setMinute(59);
		toDateXml.setSecond(59);
		toDateXml.setMillisecond(999);

	}

	public static Calendar changeTimeZone(Calendar date, TimeZone timezone) {
		Calendar newDate = null;
		if (PortalUtils.exists(timezone)) {
			newDate = Calendar.getInstance(timezone);
			newDate.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
			newDate.set(Calendar.HOUR, date.get(Calendar.HOUR));
			newDate.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
			newDate.set(Calendar.SECOND, date.get(Calendar.SECOND));
			newDate.set(Calendar.MILLISECOND, date.get(Calendar.MILLISECOND));
			newDate.set(Calendar.MONTH, date.get(Calendar.MONTH));
			newDate.set(Calendar.DATE, date.get(Calendar.DATE));
			newDate.set(Calendar.YEAR, date.get(Calendar.YEAR));
		} else newDate = date;
		return newDate;
	}

	public static XMLGregorianCalendar getXmlFromDateOfMonth(TimeZone timeZone, String month, String year) {

		GregorianCalendar greg = new GregorianCalendar();
		greg.clear();

		greg.set(Calendar.YEAR, Integer.parseInt(year));
		greg.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		greg.set(Calendar.DAY_OF_MONTH, 1);
		CalendarUtilities.setToDaysFirstMillisecond(greg);
		XMLGregorianCalendar fromDateXml = PortalUtils.getDatatypeFactory().newXMLGregorianCalendar(greg);
		return fromDateXml;
	}

	public static XMLGregorianCalendar getXmlToDateOfMonth(TimeZone timeZone, String month, String year) {

		GregorianCalendar greg = new GregorianCalendar();
		greg.clear();

		greg.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		greg.set(Calendar.DAY_OF_MONTH, greg.getActualMaximum(Calendar.DAY_OF_MONTH));
		greg.set(Calendar.YEAR, Integer.parseInt(year));
		greg.set(Calendar.HOUR_OF_DAY, greg.getActualMaximum(Calendar.HOUR_OF_DAY));
		greg.set(Calendar.MINUTE, 59);
		greg.set(Calendar.SECOND, 59);
		greg.set(Calendar.MILLISECOND, 999);
		XMLGregorianCalendar toDateXml = PortalUtils.getDatatypeFactory().newXMLGregorianCalendar(greg);
		return toDateXml;

	}
}