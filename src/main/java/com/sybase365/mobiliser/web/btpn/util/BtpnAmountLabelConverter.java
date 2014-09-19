package com.sybase365.mobiliser.web.btpn.util;

import java.util.Locale;

import org.apache.wicket.util.convert.converters.AbstractConverter;

import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This Btpn Amount converter will convert the amount to cents while sending it to back end and it will convert the
 * amount back to dollars or which ever currency defined.
 * 
 * @author Vikram Gunda
 */
public class BtpnAmountLabelConverter extends AbstractConverter {

	private static final long serialVersionUID = 1L;

	private boolean showCurrency = true;

	private String actualValue;

	private boolean isFormat;

	public BtpnAmountLabelConverter() {

	}

	public BtpnAmountLabelConverter(final boolean showCurrency, final boolean isFormat) {
		this.showCurrency = showCurrency;
		this.isFormat = isFormat;

	}

	@Override
	public Object convertToObject(String value, Locale locale) {
		if (PortalUtils.exists(actualValue)) {
			return Long.valueOf(actualValue) * 100;
		}
		return null;
	}

	@Override
	public String convertToString(Object value, Locale locale) {
		String amount;
		if (PortalUtils.exists(value) && value instanceof Long) {
			amount = String.valueOf((Long) value);
			amount = isFormat == true ? BtpnUtils.formatAmount(Long.valueOf(amount), locale) : amount;
			amount = showCurrency == true ? amount : amount.replace("IDR ", "");
			actualValue = String.valueOf(value);
			return amount;
		}
		return null;

	}

	@Override
	protected Class<?> getTargetType() {
		return Long.class;
	}

}
