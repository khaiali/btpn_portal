package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author Sushil.agrawala
 */
@SuppressWarnings("serial")
public class CustomerAlertsDataProvider extends
	SortableDataProvider<CustomerAlert> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CustomerAlertsDataProvider.class);

    private transient List<CustomerAlert> customerAlertEntries;
    private List<AlertType> alertTypes;

    private AlertsClientLogic alertsClientLogic;

    public CustomerAlertsDataProvider(final String defaultSortProperty,
	    final AlertsClientLogic alertsClientLogic,
	    final List<AlertType> alertTypes) {
	setSort(defaultSortProperty, true);
	this.alertsClientLogic = alertsClientLogic;
	this.alertTypes = alertTypes;
    }

    private AlertsClientLogic getAlertsClientLogic() {
	return this.alertsClientLogic;
    }

    private boolean alertTypesContains(long alertTypeId) {
	if (alertTypes != null) {
	    for (AlertType alertType : alertTypes) {
		if (alertType.getId().longValue() == alertTypeId) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Returns AlertEntry starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<CustomerAlert> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>AlertEntry</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (customerAlertEntries == null) {
	    return count;
	}

	return customerAlertEntries.size();
    }

    @Override
    public final IModel<CustomerAlert> model(final CustomerAlert object) {
	IModel<CustomerAlert> model = new LoadableDetachableModel<CustomerAlert>() {
	    @Override
	    protected CustomerAlert load() {
		CustomerAlert set = null;
		for (CustomerAlert obj : customerAlertEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};
	return new CompoundPropertyModel<CustomerAlert>(model);
    }

    public void loadAllAlertEntries(Long customerId, boolean forceReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(customerAlertEntries) || forceReload) {

	    List<CustomerAlert> customerAlerts = getAlertsClientLogic()
		    .findCustomerAlertByCustomer(customerId);

	    ListIterator<CustomerAlert> listIterator = customerAlerts
		    .listIterator();
	    while (listIterator.hasNext()) {
		CustomerAlert customerAlert = listIterator.next();
		if (!customerAlert.isActive()
			|| !alertTypesContains(customerAlert.getAlertTypeId())) {
		    listIterator.remove();
		}
	    }

	    setAllAlertEntries(customerAlerts);

	}
    }

    protected List<CustomerAlert> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<CustomerAlert> sublist = getIndex(customerAlertEntries,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<CustomerAlert> getIndex(
	    List<CustomerAlert> filteredWalletEntries, String prop, boolean asc) {
	if (prop.equals("calNickname")) {
	    return sort(filteredWalletEntries, asc);
	} else {
	    return filteredWalletEntries;
	}
    }

    private List<CustomerAlert> sort(List<CustomerAlert> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<CustomerAlert>() {

		@Override
		public int compare(CustomerAlert arg0, CustomerAlert arg1) {

		    return Long.valueOf((arg0).getAlertTypeId()).compareTo(
			    Long.valueOf((arg1).getAlertTypeId()));
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<CustomerAlert>() {

		@Override
		public int compare(CustomerAlert arg0, CustomerAlert arg1) {
		    return Long.valueOf((arg1).getAlertTypeId()).compareTo(
			    (arg0).getAlertTypeId());
		}
	    });
	}
	return entries;
    }

    public List<CustomerAlert> getAllAlertEntries() {
	return this.customerAlertEntries;
    }

    public void setAllAlertEntries(List<CustomerAlert> value) {
	this.customerAlertEntries = value;
    }

}