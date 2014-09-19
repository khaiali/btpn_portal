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

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerType;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class CustomerTypeConfigDataProvider extends
	SortableDataProvider<CustomerType> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CustomerTypeConfigDataProvider.class);
    private transient List<CustomerType> custTypeEntries = new ArrayList<CustomerType>();
    private MobiliserBasePage mobBasePage;

    public CustomerTypeConfigDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns CustomerType starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<CustomerType> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>CustomerType</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (custTypeEntries == null) {
	    return count;
	}

	return custTypeEntries.size();
    }

    @Override
    public final IModel<CustomerType> model(final CustomerType object) {
	IModel<CustomerType> model = new LoadableDetachableModel<CustomerType>() {
	    @Override
	    protected CustomerType load() {
		CustomerType set = null;
		for (CustomerType obj : custTypeEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<CustomerType>(model);
    }

    public void loadCustTypeConfList(boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(custTypeEntries) || forcedReload) {

	    List<CustomerType> allEntries = getMobiliserBasePage()
		    .getCustTypeConfigList();

	    if (PortalUtils.exists(allEntries)) {
		custTypeEntries = allEntries;
	    }
	}

    }

    protected List<CustomerType> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<CustomerType> sublist = getIndex(custTypeEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<CustomerType> getIndex(
	    List<CustomerType> customerNetworkEntries, String prop, boolean asc) {

	if (prop.equals("ctCustType")) {
	    return sort(customerNetworkEntries, asc);
	} else {
	    return customerNetworkEntries;
	}
    }

    private List<CustomerType> sort(List<CustomerType> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<CustomerType>() {

		@Override
		public int compare(CustomerType arg0, CustomerType arg1) {
		    return (arg0).getName().compareTo((arg1).getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<CustomerType>() {

		@Override
		public int compare(CustomerType arg0, CustomerType arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }
}
