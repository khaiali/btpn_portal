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

import com.sybase365.mobiliser.money.contract.v5_0.system.GetExchangeRateHistoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetExchangeRateHistoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.ExchangeRateHistory;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class ExchangeRateHistDataProvider extends
	SortableDataProvider<ExchangeRateHistory> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    List<ExchangeRateHistory> exchangeRateHistList = new ArrayList<ExchangeRateHistory>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(ExchangeRateHistDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public ExchangeRateHistDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends ExchangeRateHistory> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<ExchangeRateHistory> model(final ExchangeRateHistory object) {
	IModel<ExchangeRateHistory> model = new LoadableDetachableModel<ExchangeRateHistory>() {
	    /**
	     * 
	     */
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected ExchangeRateHistory load() {
		ExchangeRateHistory set = null;
		for (ExchangeRateHistory obj : exchangeRateHistList) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<ExchangeRateHistory>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (exchangeRateHistList == null) {
	    return count;
	}

	return exchangeRateHistList.size();
    }

    protected List<ExchangeRateHistory> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<ExchangeRateHistory> sublist = getIndex(exchangeRateHistList,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<ExchangeRateHistory> getIndex(
	    List<ExchangeRateHistory> exchangeRateHistList, String prop,
	    boolean asc) {

	if (prop.equals("name")) {
	    return sort(exchangeRateHistList, asc);
	} else {
	    return exchangeRateHistList;
	}
    }

    private List<ExchangeRateHistory> sort(List<ExchangeRateHistory> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<ExchangeRateHistory>() {

		@Override
		public int compare(ExchangeRateHistory arg0,
			ExchangeRateHistory arg1) {
		    return (arg0).getFromCurrency().compareTo(
			    (arg1).getFromCurrency());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<ExchangeRateHistory>() {

		@Override
		public int compare(ExchangeRateHistory arg0,
			ExchangeRateHistory arg1) {
		    return (arg1).getFromCurrency().compareTo(
			    (arg0).getFromCurrency());
		}
	    });
	}
	return entries;
    }

    public void loadExchangeRateHistory(String fromCurrency, String toCurrency,
	    boolean forcedReload) throws DataProviderLoadException {
	if (exchangeRateHistList == null || forcedReload) {
	    GetExchangeRateHistoryRequest request;
	    try {
		request = getMobiliserBasePage().getNewMobiliserRequest(
			GetExchangeRateHistoryRequest.class);
		request.setFromCurrency(fromCurrency);
		request.setToCurrency(toCurrency);
		GetExchangeRateHistoryResponse response = getMobiliserBasePage().wsSystemConfClient
			.getExchangeRateHistory(request);
		if (!getMobiliserBasePage().evaluateMobiliserResponse(response))
		    return;
		this.exchangeRateHistList = response.getExchangeRateHistory();
	    } catch (Exception e) {
		LOG.error(
			"# An error occurred while fetching Exchange rate History for:from currency:"
				+ fromCurrency + "and to currency:"
				+ toCurrency, e);
	    }
	}
    }
}
