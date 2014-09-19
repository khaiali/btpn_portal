/**
 * 
 */
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

import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.ServicePackage;
import com.sybase365.mobiliser.web.application.clients.MBankingClientLogic;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * @author Pavan Raya
 * 
 */
public class ServicePackagesDataProvider extends
	SortableDataProvider<ServicePackage> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5036856638912973735L;

    private MBankingClientLogic mbankingClientLogic;
    private transient List<ServicePackage> bankServicePackageEntries;

    public ServicePackagesDataProvider(final String defaultSortProperty,
	    final MBankingClientLogic mbankingClientLogic) {
	setSort(defaultSortProperty, true);
	this.mbankingClientLogic = mbankingClientLogic;
    }

    private MBankingClientLogic getMBankingClientLogic() {
	return this.mbankingClientLogic;
    }

    /**
     * Returns ServicePackage starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<ServicePackage> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>ServicePackage</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;
	if (bankServicePackageEntries == null) {
	    return count;
	}
	return bankServicePackageEntries.size();
    }

    @Override
    public final IModel<ServicePackage> model(final ServicePackage object) {
	IModel<ServicePackage> model = new LoadableDetachableModel<ServicePackage>() {
	    /**
	     * 
	     */
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected ServicePackage load() {
		ServicePackage set = null;
		for (ServicePackage obj : bankServicePackageEntries) {
		    if (obj.getServicePackageId() == object
			    .getServicePackageId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};
	return new CompoundPropertyModel<ServicePackage>(model);
    }

    public void loadAllServicePackages(String orgUnitId)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(bankServicePackageEntries)) {
	    List<ServicePackage> bankServicePackages = getMBankingClientLogic()
		    .getBankServicePackages(orgUnitId);
	    setAllBankServicePackageEntries(bankServicePackages);
	}
    }

    protected List<ServicePackage> find(int first, int count,
	    String sortProperty, boolean sortAsc) {
	List<ServicePackage> sublist = getIndex(bankServicePackageEntries,
		sortProperty, sortAsc).subList(first, first + count);
	return sublist;
    }

    protected List<ServicePackage> getIndex(
	    List<ServicePackage> servicePackageEntries, String prop, boolean asc) {
	if (prop.equals("spName")) {
	    return sort(servicePackageEntries, asc);
	} else {
	    return servicePackageEntries;
	}
    }

    private List<ServicePackage> sort(List<ServicePackage> entries, boolean asc) {

	if (asc) {
	    Collections.sort(entries, new Comparator<ServicePackage>() {
		@Override
		public int compare(ServicePackage arg0, ServicePackage arg1) {
		    return (arg0).getName().compareTo((arg1).getName());
		}
	    });
	} else {
	    Collections.sort(entries, new Comparator<ServicePackage>() {
		@Override
		public int compare(ServicePackage arg0, ServicePackage arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }

    public List<ServicePackage> getAllBankServicePackageEntries() {
	return this.bankServicePackageEntries;
    }

    public void setAllBankServicePackageEntries(List<ServicePackage> value) {
	this.bankServicePackageEntries = value;
    }
}
