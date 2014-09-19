package com.sybase365.mobiliser.web.common.panels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceType;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.InvoiceDataProvider;
import com.sybase365.mobiliser.web.util.Constants;

public abstract class OpenBillListPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(BillConfigurationListPanel.class);

    Long customerId;
    MobiliserBasePage basePage;

    InvoiceDataProvider dataProvider;

    List<InvoiceConfiguration> invoiceConfigurationList = new ArrayList<InvoiceConfiguration>();
    List<InvoiceType> invoiceTypeList = new ArrayList<InvoiceType>();

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private int rowIndex = 1;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public OpenBillListPanel(String id, MobiliserBasePage basePage,
	    Long customerId) {
	super(id);
	this.basePage = basePage;
	this.customerId = customerId;
	constructPanel();
    }

    private void constructPanel() {
	add(new FeedbackPanel("errorMessages"));

	dataProvider = new InvoiceDataProvider("id", this.basePage);

	final DataView<Invoice> dataView = new DataView<Invoice>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadList(customerId, forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		    invoiceConfigurationList = basePage
			    .getInvoiceConfigurationList(customerId);
		    invoiceTypeList = basePage.getAllInvoiceTypes();
		    // reset rowIndex
		    rowIndex = 1;

		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading bill configuration list",
			    dple);
		    error(getLocalizer().getString("openBillList.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<Invoice> item) {

		final Invoice entry = item.getModelObject();

		final InvoiceConfiguration ic = getInvoiceConfiguration(entry
			.getInvoiceConfigurationId());

		final String icName = (ic == null) ? "" : ic.getAlias();
		final String itName = (ic == null) ? "" : getInvoiceTypeName(ic
			.getInvoiceTypeId());
		item.add(new Label("name", icName));
		item.add(new Label("type", itName));

		item.add(new Label("reference", ic == null ? "" : ic
			.getReference()));
		item.add(new Label("billReference", entry.getReference()));
		item.add(new Label("date", entry.getDate() == null ? ""
			: new SimpleDateFormat("MM/dd/yyyy").format(entry
				.getDate().toGregorianCalendar().getTime())));
		item.add(new Label("amount", basePage
			.convertAmountToString(entry.getAmount())
			+ " "
			+ entry.getCurrency()));

		// Pay Action
		Link<Invoice> payLink = new Link<Invoice>("payAction",
			item.getModel()) {
		    @Override
		    public void onClick() {
			Invoice entry = (Invoice) getModelObject();
			if (ic != null)
			    setResponsePage(getPayBillPage(ic, itName, entry));
			else {
			    LOG.error("The invoice configuration is null.");
			}
		    }
		};
		item.add(payLink);

		// Cancel Action
		Link<Invoice> cancelLink = new Link<Invoice>("cancelAction",
			item.getModel()) {
		    @Override
		    public void onClick() {
			Invoice entry = (Invoice) getModelObject();
			if (ic != null)
			    setResponsePage(getCancelBillPage(ic, itName, entry));
			else {
			    LOG.error("The invoice configuration is null.");
			}
		    }
		};
		item.add(cancelLink);

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {
			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));
	    }

	    private void refreshTotalItemCount() {
		totalItemString = new Integer(dataProvider.size()).toString();
		int total = getItemCount();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total)
			endIndex = total;
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }

	};

	dataView.setItemsPerPage(10);
	add(dataView);

	add(new OrderByBorder("orderByDate", "date", dataProvider) {
	    @Override
	    protected void onSortChanged() {
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	// Navigator example: << < 1 2 > >>
	add(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	add(new Label(WICKET_ID_totalItems, new PropertyModel<String>(this,
		"totalItemString")));

	add(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	add(new Label(WICKET_ID_endIndex, new PropertyModel(this, "endIndex")));

	add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer().getString(
		"openBillList.noItemsMsg", this)) {
	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});
    }

    private String getInvoiceTypeName(Long id) {
	if (id == null)
	    return null;
	String invoiceTypeName = null;
	for (InvoiceType it : invoiceTypeList) {
	    if (it.getId().equals(id)) {
		invoiceTypeName = it.getName();
		break;
	    }
	}
	return invoiceTypeName;
    }

    private InvoiceConfiguration getInvoiceConfiguration(
	    long invoiceConfigurationId) {
	InvoiceConfiguration invoiceConfiguration = null;
	for (InvoiceConfiguration ic : invoiceConfigurationList) {
	    if (ic.getId() == invoiceConfigurationId) {
		invoiceConfiguration = ic;
		break;
	    }
	}
	return invoiceConfiguration;
    }

    public abstract MobiliserBasePage getPayBillPage(InvoiceConfiguration ic,
	    String itName, Invoice invoice);

    public abstract MobiliserBasePage getCancelBillPage(
	    InvoiceConfiguration ic, String itName, Invoice invoice);
}
