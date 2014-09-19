package com.sybase365.mobiliser.web.btpn.util;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.repeater.data.DataView;

public class BtpnOrderByOrder extends OrderByBorder {

	private static final long serialVersionUID = 1L;

	private DataView<?> dataView;

	public BtpnOrderByOrder(String id, String property, ISortStateLocator stateLocator, DataView<?> dataview) {
		super(id, property, stateLocator);
		this.dataView = dataview;
	}

	@Override
	protected void onSortChanged() {
		dataView.setCurrentPage(0);
	}

}
