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

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.common.model.IAlertFilter;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author msw
 */
@SuppressWarnings("serial")
public class AlertsDataProvider extends SortableDataProvider<AlertType> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(AlertsDataProvider.class);

    private transient List<AlertType> alertList;
    private AlertsClientLogic alertsClientLogic;
    private IAlertFilter alertFilter;

    public AlertsDataProvider(final String defaultSortProperty,
	    final AlertsClientLogic alertsClientLogic,
	    final IAlertFilter alertFilter) {
	setSort(defaultSortProperty, true);
	this.alertsClientLogic = alertsClientLogic;
	this.alertFilter = alertFilter;
    }

    private AlertsClientLogic getAlertsClientLogic() {
	return this.alertsClientLogic;
    }

    /**
     * Returns AlertEntry starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<AlertType> iterator(int first, int count) {
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

	if (alertList == null) {
	    return count;
	}

	return alertList.size();
    }

    @Override
    public final IModel<AlertType> model(final AlertType object) {
	IModel<AlertType> model = new LoadableDetachableModel<AlertType>() {
	    @Override
	    protected AlertType load() {
		AlertType set = null;
		for (AlertType obj : alertList) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};
	return new CompoundPropertyModel<AlertType>(model);
    }

    public void loadAllAlertEntries(Long customerId)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(alertList)) {
	    List<AlertType> customerAlerts = getAlertsClientLogic()
		    .findAlertTypes();

	    // apply the specific alert filter to restrict the list
	    // of alerts that are available
	    if (alertFilter != null) {
		alertFilter
			.filterAlerts(customerAlerts, customerId.longValue());
	    }

	    setAllAlertEntries(customerAlerts);
	}
    }

    protected List<AlertType> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<AlertType> sublist = getIndex(alertList, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<AlertType> getIndex(List<AlertType> filteredList,
	    String prop, boolean asc) {
	if (prop.equals("alName")) {
	    return sort(filteredList, asc);
	} else {
	    return filteredList;
	}
    }

    private List<AlertType> sort(List<AlertType> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<AlertType>() {

		@Override
		public int compare(AlertType arg0, AlertType arg1) {
		    return (arg0).getName().compareTo((arg1).getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<AlertType>() {

		@Override
		public int compare(AlertType arg0, AlertType arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }

    public List<AlertType> getAllAlertEntries() {
	return this.alertList;
    }

    public void setAllAlertEntries(List<AlertType> value) {
	this.alertList = value;
    }

}