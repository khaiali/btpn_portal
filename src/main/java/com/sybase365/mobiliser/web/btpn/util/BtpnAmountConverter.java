package com.sybase365.mobiliser.web.btpn.util;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converters.AbstractConverter;

import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This Btpn Amount converter will convert the amount to cents while sending it to back end and it will convert the
 * amount back to dollars or which ever currency defined.
 * 
 * @author Vikram Gunda
 */
public class BtpnAmountConverter extends AbstractConverter {

	private static final long serialVersionUID = 1L;

	@Override
	public Object convertToObject(String value, Locale locale) {
		// If amount is not of valid format throw conversion exception and show it in the portal
		if (!value.matches(BtpnConstants.AMOUNT_REGEX)) {
			throw new ConversionException("Amount cannot be converted").setVariable("input", value);
		}
		if (PortalUtils.exists(value)) {
			return Long.valueOf(value) * 100;
		}
		return null;
	}

	@Override
	public String convertToString(Object value, Locale locale) {
		if (PortalUtils.exists(value) && value instanceof Long) {
			return String.valueOf((Long) value / 100);
		}
		return null;
	}

	@Override
	protected Class<?> getTargetType() {
		return Long.class;
	}
}
