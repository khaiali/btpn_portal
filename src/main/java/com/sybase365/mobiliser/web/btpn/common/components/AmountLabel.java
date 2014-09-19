package com.sybase365.mobiliser.web.btpn.common.components;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;

import com.sybase365.mobiliser.web.btpn.util.BtpnAmountLabelConverter;

public class AmountLabel extends Label {

	private static final long serialVersionUID = 1L;

	private boolean showCurrency = true;

	private boolean showDefaultValue = true;

	private boolean isFormat = true;

	/**
	 * Constructor
	 * 
	 * @param id See Component
	 */
	public AmountLabel(final String id) {
		super(id);
	}

	/**
	 * Constructor
	 * 
	 * @param id See Component
	 */
	public AmountLabel(final String id, final boolean showCurrency, final boolean showDefaultValue) {
		super(id);
		this.showCurrency = showCurrency;
		this.showDefaultValue = showDefaultValue;
	}

	/**
	 * Constructor
	 * 
	 * @param id See Component
	 */
	public AmountLabel(final String id, final boolean showCurrency, final boolean showDefaultValue,
		final boolean isFormat) {
		super(id);
		this.showCurrency = showCurrency;
		this.showDefaultValue = showDefaultValue;
		this.isFormat = isFormat;
	}

	/**
	 * Convenience constructor. Same as Label(String, new Model&lt;String&gt;(String))
	 * 
	 * @param id See Component
	 * @param label The label text
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public AmountLabel(final String id, String label) {
		this(id, new Model<String>(label));
	}

	/**
	 * @param id
	 * @param model
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public AmountLabel(final String id, IModel<?> model) {
		super(id, model);
	}

	/**
	 * Conveter for Amount text fields
	 */
	public IConverter getConverter(Class<?> type) {
		return new BtpnAmountLabelConverter(showCurrency, isFormat);
	};

	@Override
	protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		super.onComponentTagBody(markupStream, openTag);
		if (getDefaultModelObject() == null && showDefaultValue) {
			Response response = getRequestCycle().getResponse();			 
			String amount = showCurrency == true ? "IDR 0" : "0";
			response.write(amount);
		}
	}

}
