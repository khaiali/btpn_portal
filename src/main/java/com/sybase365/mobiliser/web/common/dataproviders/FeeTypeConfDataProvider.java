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

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.UseCaseFeeType;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class FeeTypeConfDataProvider extends
	SortableDataProvider<UseCaseFeeType> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(FeeTypeConfDataProvider.class);
    private transient List<UseCaseFeeType> useCaseFeeTypes = new ArrayList<UseCaseFeeType>();
    private MobiliserBasePage mobBasePage;

    public FeeTypeConfDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns Fee Types starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<UseCaseFeeType> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>FeeTypes</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (useCaseFeeTypes == null) {
	    return count;
	}

	return useCaseFeeTypes.size();
    }

    @Override
    public final IModel<UseCaseFeeType> model(final UseCaseFeeType object) {
	IModel<UseCaseFeeType> model = new LoadableDetachableModel<UseCaseFeeType>() {
	    @Override
	    protected UseCaseFeeType load() {
		UseCaseFeeType set = null;
		for (UseCaseFeeType obj : useCaseFeeTypes) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<UseCaseFeeType>(model);
    }

    public void loadUseCaseFeeTypesList(boolean forcedReload)
	    throws DataProviderLoadException {

	if (useCaseFeeTypes == null || forcedReload) {

	    List<UseCaseFeeType> allEntries = getMobiliserBasePage()
		    .getUseCaseFeeTypes();

	    if (PortalUtils.exists(allEntries)) {
		useCaseFeeTypes = allEntries;
	    }
	}
    }

    protected List<UseCaseFeeType> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<UseCaseFeeType> sublist = getIndex(useCaseFeeTypes, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<UseCaseFeeType> getIndex(
	    List<UseCaseFeeType> feeTypeEntries, String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(feeTypeEntries, asc);
	} else {
	    return feeTypeEntries;
	}
    }

    private List<UseCaseFeeType> sort(List<UseCaseFeeType> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<UseCaseFeeType>() {

		@Override
		public int compare(UseCaseFeeType arg0, UseCaseFeeType arg1) {
		    return (arg0).getId().compareTo((arg1).getId());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<UseCaseFeeType>() {

		@Override
		public int compare(UseCaseFeeType arg0, UseCaseFeeType arg1) {
		    return (arg1).getId().compareTo((arg0).getId());
		}
	    });
	}
	return entries;
    }
}
