package com.sybase365.mobiliser.web.btpn.common.behaviours;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

/**
 * AttributeModifier that prepends the given value, rather than replace it. This is especially useful for adding CSS
 * classes to markup elements, or adding JavaScript snippets to existing element handlers. This prepends the text than
 * appending it as in the case of Attribute Appender.
 * 
 * @author Vikram Gunda
 */
public class AttributePrepender extends AttributeModifier {

	/** For serialization. */
	private static final long serialVersionUID = 1L;

	/**
	 * Separates the existing attribute value and the append value.
	 */
	private final String separator;

	/**
	 * Creates an AttributeModifier that appends the appendModel's value to the current value of the attribute, and will
	 * add the attribute when it is not there already.
	 * 
	 * @param attribute the attribute to append the appendModels value to
	 * @param appendModel the model supplying the value to append
	 * @param separator the separator string, comes between the original value and the append value
	 */
	public AttributePrepender(String attribute, IModel<?> appendModel, String separator) {
		super(attribute, true, appendModel);
		this.separator = separator;
	}

	/**
	 * @see com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender#newValue(java.lang.String, java.lang.String)
	 */
	@Override
	protected String newValue(String currentValue, String prependValue) {
		final int currentValueLen = (currentValue == null) ? 0 : currentValue.length();

		final AppendingStringBuffer sb;
		if (prependValue == null) {
			sb = new AppendingStringBuffer(currentValue + separator.length());
		} else {
			sb = new AppendingStringBuffer(prependValue.length() + currentValueLen + separator.length());
			sb.append(prependValue);
		}

		// if the current value or the append value is empty, the separator is
		// not needed.
		if (!Strings.isEmpty(prependValue) && !Strings.isEmpty(currentValue)) {
			sb.append(separator);
		}

		// only append the value when it is not empty.
		if (!Strings.isEmpty(currentValue)) {
			sb.append(currentValue);
		}
		return sb.toString();
	}
}
