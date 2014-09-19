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

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerNetwork;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class CustomerNetworkDataProvider extends
	SortableDataProvider<CustomerNetwork> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CustomerNetworkDataProvider.class);
    private transient List<CustomerNetwork> customerNetworkEntries = new ArrayList<CustomerNetwork>();
    private MobiliserBasePage mobBasePage;

    public CustomerNetworkDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns CustomerNetwork starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<CustomerNetwork> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>CustomerNetwork</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (customerNetworkEntries == null) {
	    return count;
	}

	return customerNetworkEntries.size();
    }

    @Override
    public final IModel<CustomerNetwork> model(final CustomerNetwork object) {
	IModel<CustomerNetwork> model = new LoadableDetachableModel<CustomerNetwork>() {
	    @Override
	    protected CustomerNetwork load() {
		CustomerNetwork set = null;
		for (CustomerNetwork obj : customerNetworkEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<CustomerNetwork>(model);
    }

    public void loadFriendsAndFamilyList(Long customerId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (customerNetworkEntries == null || forcedReload) {

	    List<CustomerNetwork> allEntries = getMobiliserBasePage()
		    .getCustomerNetworkList(customerId,
			    Constants.CUSTOMER_NETWORK_TYPE_F_AND_F,
			    Constants.CUSTOMER_NETWORK_STATUS_APPROVED);

	    if (PortalUtils.exists(allEntries)) {
		customerNetworkEntries = allEntries;
	    }
	}

    }

    protected List<CustomerNetwork> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<CustomerNetwork> sublist = getIndex(customerNetworkEntries,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<CustomerNetwork> getIndex(
	    List<CustomerNetwork> customerNetworkEntries, String prop,
	    boolean asc) {

	if (prop.equals("name")) {
	    return sort(customerNetworkEntries, asc);
	} else {
	    return customerNetworkEntries;
	}
    }

    private List<CustomerNetwork> sort(List<CustomerNetwork> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<CustomerNetwork>() {

		@Override
		public int compare(CustomerNetwork arg0, CustomerNetwork arg1) {
		    return (arg0).getNickname().compareTo((arg1).getNickname());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<CustomerNetwork>() {

		@Override
		public int compare(CustomerNetwork arg0, CustomerNetwork arg1) {
		    return (arg1).getNickname().compareTo((arg0).getNickname());
		}
	    });
	}
	return entries;
    }
}
