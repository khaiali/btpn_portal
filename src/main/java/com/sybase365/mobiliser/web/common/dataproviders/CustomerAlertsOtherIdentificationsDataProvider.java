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

import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.web.application.clients.AlertsClientLogic;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author Sushil.agrawala
 */
@SuppressWarnings("serial")
public class CustomerAlertsOtherIdentificationsDataProvider extends
	SortableDataProvider<CustomerOtherIdentification> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CustomerAlertsDataProvider.class);

    private transient List<CustomerOtherIdentification> otherIdentList;

    private AlertsClientLogic alertsClientLogic;

    public CustomerAlertsOtherIdentificationsDataProvider(
	    final String defaultSortProperty,
	    final AlertsClientLogic alertsClientLogic) {
	setSort(defaultSortProperty, true);
	this.alertsClientLogic = alertsClientLogic;
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
    public Iterator<CustomerOtherIdentification> iterator(int first, int count) {
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

	if (otherIdentList == null) {
	    return count;
	}

	return otherIdentList.size();
    }

    @Override
    public final IModel<CustomerOtherIdentification> model(
	    final CustomerOtherIdentification object) {
	IModel<CustomerOtherIdentification> model = new LoadableDetachableModel<CustomerOtherIdentification>() {
	    @Override
	    protected CustomerOtherIdentification load() {
		CustomerOtherIdentification set = null;
		for (CustomerOtherIdentification obj : otherIdentList) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};
	return new CompoundPropertyModel<CustomerOtherIdentification>(model);
    }

    public void loadOtherIdentifications(Long customerId)
	    throws DataProviderLoadException {
	if (!PortalUtils.exists(getOtherIdentifications())) {
	    setOtherIdentifications(getAlertsClientLogic()
		    .findCustomerOtherIdentification(customerId));
	}
    }

    protected List<CustomerOtherIdentification> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<CustomerOtherIdentification> sublist = getIndex(otherIdentList,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<CustomerOtherIdentification> getIndex(
	    List<CustomerOtherIdentification> filteredContactPointsList,
	    String prop, boolean asc) {
	if (prop.equals("ocpNickname")) {
	    return sort(filteredContactPointsList, asc);
	} else {
	    return filteredContactPointsList;
	}
    }

    private List<CustomerOtherIdentification> sort(
	    List<CustomerOtherIdentification> entries, boolean asc) {

	if (asc) {
	    Collections.sort(entries,
		    new Comparator<CustomerOtherIdentification>() {

			@Override
			public int compare(CustomerOtherIdentification arg0,
				CustomerOtherIdentification arg1) {
			    return (arg0).getNickname().compareTo(
				    (arg1).getNickname());
			}
		    });

	} else {

	    Collections.sort(entries,
		    new Comparator<CustomerOtherIdentification>() {

			@Override
			public int compare(CustomerOtherIdentification arg0,
				CustomerOtherIdentification arg1) {
			    return (arg1).getNickname().compareTo(
				    (arg0).getNickname());
			}
		    });
	}
	return entries;
    }

    public void setOtherIdentifications(
	    List<CustomerOtherIdentification> otherIdentList) {
	this.otherIdentList = otherIdentList;
    }

    public List<CustomerOtherIdentification> getOtherIdentifications() {
	return otherIdentList;
    }

}