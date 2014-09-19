package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.GetExchangeRateRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetExchangeRateResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.ExchangeRate;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class ExchangeRateDataProvider extends
	SortableDataProvider<ExchangeRate> {

    List<ExchangeRate> exchangeRateList = new ArrayList<ExchangeRate>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(ExchangeRateDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public ExchangeRateDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends ExchangeRate> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<ExchangeRate> model(final ExchangeRate object) {
	IModel<ExchangeRate> model = new LoadableDetachableModel<ExchangeRate>() {
	    @Override
	    protected ExchangeRate load() {
		ExchangeRate set = null;
		for (ExchangeRate obj : exchangeRateList) {
		    if ((obj.getFromCurrency().equals(object.getFromCurrency()) && (obj
			    .getToCurrency().equals(object.getToCurrency())))) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<ExchangeRate>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (exchangeRateList == null) {
	    return count;
	}

	return exchangeRateList.size();
    }

    protected List<ExchangeRate> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<ExchangeRate> sublist = getIndex(exchangeRateList, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<ExchangeRate> getIndex(List<ExchangeRate> exchangeRateList,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(exchangeRateList, asc);
	} else {
	    return exchangeRateList;
	}
    }

    private List<ExchangeRate> sort(List<ExchangeRate> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<ExchangeRate>() {

		@Override
		public int compare(ExchangeRate arg0, ExchangeRate arg1) {
		    return (arg0).getFromCurrency().compareTo(
			    (arg1).getFromCurrency());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<ExchangeRate>() {

		@Override
		public int compare(ExchangeRate arg0, ExchangeRate arg1) {
		    return (arg1).getFromCurrency().compareTo(
			    (arg0).getFromCurrency());
		}
	    });
	}
	return entries;
    }

    public void loadExchangeRates(boolean forcedReload)
	    throws DataProviderLoadException {
	if (exchangeRateList == null || forcedReload) {
	    GetExchangeRateRequest request;
	    try {
		request = getMobiliserBasePage().getNewMobiliserRequest(
			GetExchangeRateRequest.class);
		GetExchangeRateResponse response = getMobiliserBasePage().wsSystemConfClient
			.getExchangeRate(request);
		if (!getMobiliserBasePage().evaluateMobiliserResponse(response))
		    return;
		this.exchangeRateList = response.getExchangeRates();
	    } catch (Exception e) {
		LOG.error("# An error occurred while fetching Exchange rate", e);
		DataProviderLoadException dple = new DataProviderLoadException(
			e.getMessage());
		throw dple;
	    }
	}

    }

}
