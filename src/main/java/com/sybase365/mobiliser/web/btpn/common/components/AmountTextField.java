package com.sybase365.mobiliser.web.btpn.common.components;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import com.sybase365.mobiliser.web.btpn.util.BtpnAmountConverter;
import com.sybase365.mobiliser.web.btpn.util.BtpnAmountValidator;

public class AmountTextField<T> extends TextField<T> {

	private static final long serialVersionUID = 1L;

	public AmountTextField(String id) {
		super(id);
		add(new BtpnAmountValidator<T>());
	}

	/**
	 * @param id See Component
	 * @param type Type for field validation
	 */
	public AmountTextField(final String id, final Class<T> type) {
		super(id);
		setType(type);
		add(new BtpnAmountValidator<T>());
	}

	/**
	 * @param id See Component
	 * @param type Type for field validation
	 */
	public AmountTextField(final String id, final Class<T> type, final boolean isZeroValid) {
		super(id);
		setType(type);
		add(new BtpnAmountValidator<T>(isZeroValid));
	}

	/**
	 * @param id
	 * @param model
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public AmountTextField(final String id, final IModel<T> model) {
		super(id, model);
		add(new BtpnAmountValidator<T>());
	}

	/**
	 * @param id See Component
	 * @param model See Component
	 * @param type The type to use when updating the model for this text field
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public AmountTextField(final String id, IModel<T> model, Class<T> type) {
		super(id, model);
		setType(type);
		add(new BtpnAmountValidator<T>());
	}

	/**
	 * Conveter for Amount text fields
	 */
	public IConverter getConverter(Class<?> type) {
		return new BtpnAmountConverter();
	};

}
