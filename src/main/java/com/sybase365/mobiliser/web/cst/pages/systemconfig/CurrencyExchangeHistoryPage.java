package com.sybase365.mobiliser.web.cst.pages.systemconfig;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.ExchangeRateHistory;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.ExchangeRateHistDataProvider;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CurrencyExchangeHistoryPage extends BaseSystemConfigurationPage {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CurrencyExchangePage.class);

    private String fromCurrency;
    private String toCurrency;
    private ExchangeRateHistDataProvider dataProvider;
    private List<ExchangeRateHistory> exchangeRateHistList;

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
    private static final String WICKET_ID_orderByfromCurrency = "orderByfromCurrency";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_fromCurrency = "fromCurrency";
    private static final String WICKET_ID_toCurrency = "toCurrency";
    private static final String WICKET_ID_ratio = "ratio";
    private static final String WICKET_ID_rate = "rate";
    private static final String WICKET_ID_validTo = "validTo";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));
	final Form<?> form = new Form("exchangeRateHistoryForm",
		new CompoundPropertyModel<CurrencyExchangeHistoryPage>(this));
	form.add(new LocalizableLookupDropDownChoice<String>("fromCurrency",
		String.class, "currencies", this, true, true));

	form.add(new LocalizableLookupDropDownChoice<String>("toCurrency",
		String.class, "currencies", this, true, true));

	final WebMarkupContainer exchangeRateHistViewContainer = new WebMarkupContainer(
		"historyView");
	createExchangeRateHistoryDataView(exchangeRateHistViewContainer);
	form.add(new Button("search") {
	    @Override
	    public void onSubmit() {
		forceReload = true;
		createExchangeRateHistoryDataView(exchangeRateHistViewContainer);
		exchangeRateHistViewContainer.setVisible(true);
	    }
	});
	add(form);
	add(exchangeRateHistViewContainer);

    }

    private void createExchangeRateHistoryDataView(
	    WebMarkupContainer exchangeRateHistViewContainer) {
	exchangeRateHistViewContainer.setVisible(false);
	dataProvider = new ExchangeRateHistDataProvider(WICKET_ID_fromCurrency,
		this);
	exchangeRateHistList = new ArrayList<ExchangeRateHistory>();

	final DataView<ExchangeRateHistory> dataView = new DataView<ExchangeRateHistory>(
		WICKET_ID_pageable, dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadExchangeRateHistory(getFromCurrency(),
			    getToCurrency(), forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading exchange rate history",
			    dple);
		    error(getLocalizer().getString(
			    "exchangeratehistory.load.error", this));
		}
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}
		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    private void refreshTotalItemCount() {
		totalItemString = Integer.toString(dataProvider.size());
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

	    @Override
	    protected void populateItem(final Item<ExchangeRateHistory> item) {
		final ExchangeRateHistory entry = item.getModelObject();
		exchangeRateHistList.add(entry);
		item.add(new Label(WICKET_ID_fromCurrency, entry
			.getFromCurrency()));
		item.add(new Label(WICKET_ID_toCurrency, entry.getToCurrency()));
		item.add(new Label(WICKET_ID_ratio, entry.getFromAmount() + ":"
			+ entry.getToAmount()));
		item.add(new Label(WICKET_ID_rate, String.valueOf(entry
			.getRate())));
		item.add(new Label(WICKET_ID_validTo, String
			.valueOf(PortalUtils.getFormattedDateTime(
				entry.getCreationDate(),
				getMobiliserWebSession().getLocale(),
				getMobiliserWebSession().getTimeZone()))));

	    }

	};

	dataView.setItemsPerPage(10);
	exchangeRateHistViewContainer.addOrReplace(dataView);

	exchangeRateHistViewContainer.addOrReplace(new OrderByBorder(
		WICKET_ID_orderByfromCurrency, WICKET_ID_fromCurrency,
		dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	exchangeRateHistViewContainer.addOrReplace(new MultiLineLabel(
		WICKET_ID_noItemsMsg, getLocalizer().getString(
			"exchangerate.history.noItemsMsg", this)) {
	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	// Navigator example: << < 1 2 > >>
	exchangeRateHistViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	exchangeRateHistViewContainer.addOrReplace(new Label(
		WICKET_ID_totalItems, new PropertyModel<String>(this,
			"totalItemString")));

	exchangeRateHistViewContainer.addOrReplace(new Label(
		WICKET_ID_startIndex, new PropertyModel(this, "startIndex")));

	exchangeRateHistViewContainer.addOrReplace(new Label(
		WICKET_ID_endIndex, new PropertyModel(this, "endIndex")));

    }

    public String getFromCurrency() {
	return fromCurrency;
    }

    public void setFromCurrency(final String fromCurrency) {
	this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
	return toCurrency;
    }

    public void setToCurrency(final String toCurrency) {
	this.toCurrency = toCurrency;
    }

}
