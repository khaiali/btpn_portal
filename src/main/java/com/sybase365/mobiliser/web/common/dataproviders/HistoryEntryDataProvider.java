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

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerHistoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.HistoryEntry;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class HistoryEntryDataProvider extends
	SortableDataProvider<HistoryEntry> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(HistoryEntryDataProvider.class);
    private transient List<HistoryEntry> historyEntries = new ArrayList<HistoryEntry>();
    private MobiliserBasePage mobBasePage;

    public HistoryEntryDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns HistoryEntry starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<HistoryEntry> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>HistoryEntry</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (historyEntries == null) {
	    return count;
	}

	return historyEntries.size();
    }

    @Override
    public final IModel<HistoryEntry> model(final HistoryEntry object) {
	IModel<HistoryEntry> model = new LoadableDetachableModel<HistoryEntry>() {
	    @Override
	    protected HistoryEntry load() {
		HistoryEntry set = null;
		for (HistoryEntry obj : historyEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<HistoryEntry>(model);
    }

    public void loadHistory(GetCustomerHistoryRequest customerHistoryRequest,
	    boolean forcedReload) throws DataProviderLoadException {

	if (historyEntries == null || forcedReload) {

	    List<HistoryEntry> allEntries = getMobiliserBasePage()
		    .getCustomerHistoryList(customerHistoryRequest);

	    if (PortalUtils.exists(allEntries)) {
		historyEntries = allEntries;
	    }
	}
    }

    protected List<HistoryEntry> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<HistoryEntry> sublist = getIndex(historyEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<HistoryEntry> getIndex(List<HistoryEntry> historyEntries,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(historyEntries, asc);
	} else {
	    return historyEntries;
	}
    }

    private List<HistoryEntry> sort(List<HistoryEntry> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<HistoryEntry>() {

		@Override
		public int compare(HistoryEntry arg0, HistoryEntry arg1) {
		    return (arg0).getId().compareTo((arg1).getId());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<HistoryEntry>() {

		@Override
		public int compare(HistoryEntry arg0, HistoryEntry arg1) {
		    return (arg1).getId().compareTo((arg0).getId());
		}
	    });
	}
	return entries;
    }
}
