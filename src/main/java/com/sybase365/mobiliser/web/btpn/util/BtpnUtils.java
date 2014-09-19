package com.sybase365.mobiliser.web.btpn.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.apache.wicket.Component;
import org.apache.wicket.Page;

import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.approval.AgentConfirmApprovalPage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ConsumerTransactionBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.ConsumerTopAgentConfirmApprovalPage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This class consists of utility methods for the btpn applications.
 * 
 * @author Vikram Gunda
 */
public class BtpnUtils {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BtpnUtils.class);

	static char[] ALPHABET_NUMERIC = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	public static List<String> feesList;

	public static List<String> billPayOrAirtimeList;

	private static String CURRENCY_CODE_IDR = "IDR";

	private static char DECIMAL_POINT = '.';

	private static char COMMA = ',';

	/**
	 * This returns Customer Type
	 * 
	 * @return consumerType customer Type
	 */
	public static String fetchProductType(final String consumerType) {
		if (consumerType.equalsIgnoreCase(BtpnConstants.REG_CONSUMER)) {
			return BtpnConstants.RESOURCE_BUNDLE_PRODUCT_CUSTOMERS;
		} else if (consumerType.equalsIgnoreCase(BtpnConstants.REG_TOPUP_AGENT)) {
			return BtpnConstants.RESOURCE_BUNDLE_PRODUCT_TOP_AGENT;
		} else if (consumerType.equalsIgnoreCase(BtpnConstants.REG_CHILD_AGENT)) {
			return BtpnConstants.RESOURCE_BUNDLE_PRODUCT_AGENTS;
		} else {
			return BtpnConstants.RESOURCE_BUNDLE_PRODUCT_SUB_AGENTS;
		}
	}

	/**
	 * This returns the set Response page based on Consumer Type
	 */
	public static Page setResponsePageForConfirm(final CustomerRegistrationBean customer,
		final ILookupMapUtility lookupmap, final Component component) {

		final String id = customer.getProductType().getId();
		if (checkConsumerProductType(id, lookupmap, component, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_CUSTOMERS)) {
			customer.setCustomerType(BtpnConstants.REG_CONSUMER);
			return new ConsumerTopAgentConfirmApprovalPage(customer);
		} else if (checkConsumerProductType(id, lookupmap, component, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_TOP_AGENT)) {
			customer.setCustomerType(BtpnConstants.REG_TOPUP_AGENT);
			return new ConsumerTopAgentConfirmApprovalPage(customer);
		} else if (checkConsumerProductType(id, lookupmap, component, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_AGENTS)) {
			customer.setCustomerType(BtpnConstants.REG_CHILD_AGENT);
			return new AgentConfirmApprovalPage(customer);
		} else {
			customer.setCustomerType(BtpnConstants.REG_SUB_AGENT);
			return new AgentConfirmApprovalPage(customer);
		}
	}

	/**
	 * This returns the set Response page based on Consumer Type
	 */
	public static boolean checkConsumerProductType(final String id, final ILookupMapUtility lookupmap,
		final Component component, final String productType) {
		final String value = getDropdownValueFromId(lookupmap.getLookupNamesMap(productType, component), productType
				+ "." + id);
		if (PortalUtils.exists(value)) {
			return true;
		}
		return false;
	}

	/**
	 * This returns the set Response page based on Consumer Type
	 */
	public static String getDropdownValueFromId(final Map<String, String> lookupMap, final String id) {
		if (lookupMap != null && !lookupMap.isEmpty()) {
			for (Entry<String, String> entry : lookupMap.entrySet()) {
				if (id != null && id.equalsIgnoreCase(entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * This returns the set Response page based on Consumer Type
	 */
	public static String getDropdownIdFromValue(final Map<String, String> lookupMap, final String value) {
		if (lookupMap != null && !lookupMap.isEmpty()) {
			for (Entry<String, String> entry : lookupMap.entrySet()) {
				if (value != null && value.equalsIgnoreCase(entry.getValue())) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public static long getReferenceId(Long seed) {

		Random r = new Random(UUID.randomUUID().getMostSignificantBits() + (seed == null ? 0L : seed.longValue()));

		char[] otp = new char[9];
		for (int i = otp.length; i-- > 0;) {
			otp[i] = ALPHABET_NUMERIC[r.nextInt(ALPHABET_NUMERIC.length)];
		}
		return Long.valueOf(new String(otp));
	}


	/**
	 * This returns for the Fixed fee or Slab Fee or shaList.
	 */
	public static List<String> getFeesList() {
		if (!PortalUtils.exists(feesList)) {
			feesList = new ArrayList<String>();
			feesList.add(BtpnConstants.USECASE_FIXED_RADIO);
			feesList.add(BtpnConstants.USECASE_SLAB_RADIO);
			feesList.add(BtpnConstants.USECASE_SHARING_RADIO);
		}
		return feesList;
	}

	/**
	 * This returns for the Bill Pay or Airitme Fee List.
	 */
	public static List<String> getBillPayOrAirtimeFeesList() {
		if (!PortalUtils.exists(billPayOrAirtimeList)) {
			billPayOrAirtimeList = new ArrayList<String>();
			billPayOrAirtimeList.add(BtpnConstants.USECASE_BILLPAYMENT_FEE);
			billPayOrAirtimeList.add(BtpnConstants.USECASE_AIRTIME_FEE);
		}
		return billPayOrAirtimeList;
	}

	/**
	 * this method will return formated amount with currency
	 * 
	 * @param amount
	 * @param locale
	 * @return
	 */
	public static String formatAmount(Long amount, Locale locale) {
		if (amount == null) {
			amount = 0L;
		}
		String formatAmount = formatAmount(amount, CURRENCY_CODE_IDR, locale);
		LOG.info("Formated Amount with currency :" + formatAmount);
		return formatAmount;
	}

	public static String formatAmount(long amount, String currency, Locale locale) {
		String c = PortalUtils.exists(currency) ? currency : CURRENCY_CODE_IDR;
		locale = new Locale("en", "ID");
		c = c
				+ " "
				+ formatCurrencyAmount(amount,
					java.util.Currency.getInstance(PortalUtils.exists(c) ? currency : CURRENCY_CODE_IDR), locale);
		// CY added for IDR Currency formating.
		if (currency.equals(CURRENCY_CODE_IDR)) {
			int i = c.indexOf(DECIMAL_POINT);
			if (i >= 0) {
				c = c.substring(0, i);
			}
			c = c.replace(COMMA, DECIMAL_POINT);
		}
		return c;
	}

	public static final String formatCurrencyAmount(long amount, Currency currency, Locale locale) {
		return formatCurrencyAmount(toCurrency(amount, currency), currency, locale);
	}

	public static final String formatCurrencyAmount(BigDecimal amount, Currency currency, Locale locale) {
		NumberFormat format = NumberFormat.getNumberInstance(locale);
		format.setMaximumFractionDigits(currency.getDefaultFractionDigits());
		format.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		return format.format(amount);
	}

	public static BigDecimal toCurrency(long amount, Currency currency) {
		return new BigDecimal(amount).divide(new BigDecimal(10).pow(currency.getDefaultFractionDigits())).setScale(
			currency.getDefaultFractionDigits());
	}

	/**
	 * This method builds the transaction csv file.
	 * 
	 * @param header
	 * @param transactionList
	 * @return
	 */
	public static StringBuilder generateCsvFile(final StringBuilder header,
		final List<ConsumerTransactionBean> transactionList) {
		final StringBuilder builder = new StringBuilder();
		builder.append(header);
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		for (ConsumerTransactionBean bean : transactionList) {
			builder.append("\n");
			builder.append(df.format(bean.getDate()));;
			builder.append(",");
			builder.append(checkValueExists(bean.getType()));
			builder.append(",");
			builder.append(checkValueExists(bean.getErrorCode()));
			builder.append(",");
			builder.append(checkValueExists(bean.getTxnId()));
			builder.append(",");
			builder.append(checkValueExists(bean.getName()));
			builder.append(",");
			builder.append(checkValueExists(bean.getDetails()));
			builder.append(",");
			builder.append(formatAmount(bean.getFee() == null ? 0 : bean.getFee(), new Locale("en", "ID")));
			builder.append(",");
			builder.append(formatAmount(bean.getAmount() == null ? 0 : bean.getAmount(), new Locale("en", "ID")));
		}
		return builder;
	}

	/**
	 * Check whether value exists
	 * 
	 * @param value
	 */
	public static String checkValueExists(final String value) {
		return value == null ? "" : value;
	}
}
