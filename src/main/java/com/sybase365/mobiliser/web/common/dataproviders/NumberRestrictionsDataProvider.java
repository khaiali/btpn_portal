package com.sybase365.mobiliser.web.common.dataproviders;

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

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.RestrictedNumber;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author Sushil.agrawala
 */
@SuppressWarnings("serial")
public class NumberRestrictionsDataProvider extends
	SortableDataProvider<RestrictedNumber> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(NumberRestrictionsDataProvider.class);

    private transient List<RestrictedNumber> restrictedNumberList;
    protected MBankingClientLogic mBankingClientLogic;

    public NumberRestrictionsDataProvider(String defaultSortProperty,
	    final MBankingClientLogic mBankingClientLogic) {
	setSort(defaultSortProperty, true);
	this.mBankingClientLogic = mBankingClientLogic;
    }

    /**
     * Returns RestrictedNumber starting with index <code>first</code> and
     * ending with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<RestrictedNumber> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>RestrictedNumber</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (restrictedNumberList == null) {
	    return count;
	}

	return restrictedNumberList.size();
    }

    @Override
    public final IModel<RestrictedNumber> model(final RestrictedNumber object) {
	IModel<RestrictedNumber> model = new LoadableDetachableModel<RestrictedNumber>() {
	    @Override
	    protected RestrictedNumber load() {
		RestrictedNumber set = null;
		for (RestrictedNumber obj : restrictedNumberList) {
		    if (obj.getMsisdn() == object.getMsisdn()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};
	return new CompoundPropertyModel<RestrictedNumber>(model);
    }

    public void loadRestrictionsNumber(String orgUnit)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(getRestrictedNumberList())) {
	    setRestrictedNumberList(mBankingClientLogic
		    .getRestrictedNumbersByOrgUnit(Constants.DEFAULT_ORGUNIT));
	}
    }

    protected List<RestrictedNumber> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<RestrictedNumber> sublist = getIndex(restrictedNumberList,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<RestrictedNumber> getIndex(
	    List<RestrictedNumber> filteredContactPointsEntries, String prop,
	    boolean asc) {

	if (prop.equals("mrnNumber")) {
	    return sort(filteredContactPointsEntries, asc);
	} else {
	    return filteredContactPointsEntries;
	}
    }

    private List<RestrictedNumber> sort(List<RestrictedNumber> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<RestrictedNumber>() {

		@Override
		public int compare(RestrictedNumber arg0, RestrictedNumber arg1) {
		    return Double.valueOf((arg0).getMsisdn()).compareTo(
			    Double.valueOf((arg1).getMsisdn()));
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<RestrictedNumber>() {

		@Override
		public int compare(RestrictedNumber arg0, RestrictedNumber arg1) {
		    return Double.valueOf((arg1).getMsisdn()).compareTo(
			    Double.valueOf((arg0).getMsisdn()));
		}
	    });
	}
	return entries;
    }

    public void setRestrictedNumberList(
	    List<RestrictedNumber> restrictedNumberList) {
	this.restrictedNumberList = restrictedNumberList;
    }

    public List<RestrictedNumber> getRestrictedNumberList() {
	return restrictedNumberList;
    }

}