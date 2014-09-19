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

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author Sushil.agrawala
 */
@SuppressWarnings("serial")
public class CustomerIdentificationsDataProvider extends
	SortableDataProvider<Identification> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CustomerAlertsDataProvider.class);

    private transient List<Identification> identList;

    private MobiliserBasePage mobBasePage;

    public CustomerIdentificationsDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns AlertEntry starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<Identification> iterator(int first, int count) {
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

	if (identList == null) {
	    return count;
	}

	return identList.size();
    }

    @Override
    public final IModel<Identification> model(final Identification object) {
	IModel<Identification> model = new LoadableDetachableModel<Identification>() {
	    @Override
	    protected Identification load() {
		Identification set = null;
		for (Identification obj : identList) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};
	return new CompoundPropertyModel<Identification>(model);
    }

    public void loadContactableIdentifications(long customerId)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(getIdentifications())) {

	    identList = new ArrayList<Identification>();

	    try {
		Identification msisdnIdent = getMobiliserBasePage()
			.getCustomerIdentification(customerId,
				Constants.IDENT_TYPE_MSISDN);
		if (PortalUtils.exists(msisdnIdent)) {
		    identList.add(msisdnIdent);
		}
	    } catch (Exception e) {
		LOG.error(
			"Problem loading msisdn identification for customer {}",
			customerId);
	    }

	    try {
		Identification emailIdent = getMobiliserBasePage()
			.getCustomerIdentification(customerId,
				Constants.IDENT_TYPE_EMAIL);
		if (PortalUtils.exists(emailIdent)) {
		    identList.add(emailIdent);
		}
	    } catch (Exception e) {
		LOG.error(
			"Problem loading email identification for customer {}",
			customerId);
	    }
	}
    }

    protected List<Identification> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<Identification> sublist = getIndex(identList, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<Identification> getIndex(
	    List<Identification> filteredContactPointsEntries, String prop,
	    boolean asc) {

	if (prop.equals("name")) {
	    return sort(filteredContactPointsEntries, asc);
	} else {
	    return filteredContactPointsEntries;
	}
    }

    private List<Identification> sort(List<Identification> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<Identification>() {

		@Override
		public int compare(Identification arg0, Identification arg1) {
		    return (arg0).getIdentification().compareTo(
			    (arg1).getIdentification());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<Identification>() {

		@Override
		public int compare(Identification arg0, Identification arg1) {
		    return (arg1).getIdentification().compareTo(
			    (arg0).getIdentification());
		}
	    });
	}
	return entries;
    }

    public void setOtherIdentificatinEntries(List<Identification> identList) {
	this.identList = identList;
    }

    public List<Identification> getIdentifications() {
	return identList;
    }

}