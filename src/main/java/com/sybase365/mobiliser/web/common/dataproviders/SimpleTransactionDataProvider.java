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

import com.sybase365.mobiliser.money.contract.v5_0.transaction.FindTransactionsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.FindTransactionsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.SearchTransactionCriteria;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class SimpleTransactionDataProvider extends
	SortableDataProvider<SimpleTransaction> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(SimpleTransactionDataProvider.class);
    private transient List<SimpleTransaction> transactionEntries = new ArrayList<SimpleTransaction>();
    private MobiliserBasePage mobBasePage;

    public SimpleTransactionDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns SimpleTransaction starting with index <code>first</code> and
     * ending with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<SimpleTransaction> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>SimpleTransaction</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (transactionEntries == null) {
	    return count;
	}

	return transactionEntries.size();
    }

    @Override
    public final IModel<SimpleTransaction> model(final SimpleTransaction object) {
	IModel<SimpleTransaction> model = new LoadableDetachableModel<SimpleTransaction>() {
	    @Override
	    protected SimpleTransaction load() {
		SimpleTransaction set = null;
		for (SimpleTransaction obj : transactionEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<SimpleTransaction>(model);
    }

    public void loadCustomerTransactions(SearchTransactionCriteria criteria,

    Integer maxNumberToFetch, boolean forcedReload)
	    throws DataProviderLoadException {

	if (transactionEntries == null || forcedReload) {

	    List<SimpleTransaction> allEntries = findTransactions(criteria,
		    maxNumberToFetch);

	    if (PortalUtils.exists(allEntries)) {
		transactionEntries = allEntries;
	    }
	}
    }

    private List<SimpleTransaction> findTransactions(
	    SearchTransactionCriteria criteria, Integer maxNumberToFetch)
	    throws DataProviderLoadException {
	LOG.debug("# SimpleTransactionDataProvider.findTransactions()");
	List<SimpleTransaction> transactions = new ArrayList<SimpleTransaction>();
	if (PortalUtils.exists(criteria.getTxnID())) {

	} else {
	    try {
		FindTransactionsRequest request = mobBasePage
			.getNewMobiliserRequest(FindTransactionsRequest.class);

		if (PortalUtils.exists(criteria.getCustomerId()))
		    request.setCustomerId(criteria.getCustomerId().longValue());
		request.setFromDate(criteria.getFromDateXml());
		request.setToDate(criteria.getToDateXml());
		request.setMerchantOrderIdFilter(criteria.getOrderID());
		request.setShowFaulty(criteria.getShowFaulty());
		request.setStatusFilter(criteria.getTxnStatus());
		request.setMaxRecords(maxNumberToFetch.intValue());
		request.setJoinedCustomerId(criteria.getJoinedCustomerId());
		request.setCustomerIsPayer(criteria.getConsumerIsPayer());
		request.setShowInitial(criteria.getShowInitial());
		request.setCaller(criteria.getCallerId());
		FindTransactionsResponse response = mobBasePage.wsTransactionsClient
			.findTransactions(request);

		if (!mobBasePage.evaluateMobiliserResponse(response)) {
		    LOG.warn("# An error occurred while loading customer transactions");
		    throw new DataProviderLoadException();
		}
		transactions = response.getTransactions();

		if (!criteria.getShowAuthorizedCancel()) {
		    filterCancelledTxns(transactions);
		}

		if (transactions.size() == maxNumberToFetch.intValue()) {
		    LOG.debug("The maximum number of transactions were fetched.");
		}
	    } catch (Exception e) {
		LOG.error(
			"# An error occurred while loading customer transactions",
			e);
		throw new DataProviderLoadException();
	    }
	}
	return transactions;
    }

    protected List<SimpleTransaction> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<SimpleTransaction> sublist = getIndex(transactionEntries,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<SimpleTransaction> getIndex(
	    List<SimpleTransaction> transactionEntries, String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(transactionEntries, asc);
	} else {
	    return transactionEntries;
	}
    }

    private List<SimpleTransaction> sort(List<SimpleTransaction> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<SimpleTransaction>() {

		@Override
		public int compare(SimpleTransaction arg0,
			SimpleTransaction arg1) {
		    return (arg0).getCreationDate().toString()
			    .compareTo((arg1).getCreationDate().toString());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<SimpleTransaction>() {

		@Override
		public int compare(SimpleTransaction arg0,
			SimpleTransaction arg1) {
		    return (arg1).getCreationDate().toString()
			    .compareTo((arg0).getCreationDate().toString());
		}
	    });
	}
	return entries;
    }

    private void filterCancelledTxns(List<SimpleTransaction> transactions) {
	Iterator<SimpleTransaction> iter = transactions.iterator();
	while (iter.hasNext()) {
	    if (iter.next().getStatus() == Constants.TXN_STATUS_AUTHCANCEL)
		iter.remove();
	}
    }
}
