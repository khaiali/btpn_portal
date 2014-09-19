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

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrPrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrPrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrPrivilege;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class PrivilegeDataProvider extends SortableDataProvider<UmgrPrivilege> {
    private static final long serialVersionUID = 1L;
    private transient List<UmgrPrivilege> umgrPrivilegeEntries = new ArrayList<UmgrPrivilege>();
    private MobiliserBasePage mobBasePage;

    public PrivilegeDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean sortAsc) {
	setSort(defaultSortProperty, sortAsc);
	this.mobBasePage = mobBasePage;
    }

    /**
     * Returns MessageAttachment starting with index <code>first</code> and
     * ending with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<? extends UmgrPrivilege> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>MessageAttachment</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (umgrPrivilegeEntries == null) {
	    return count;
	}

	return umgrPrivilegeEntries.size();
    }

    @Override
    public final IModel<UmgrPrivilege> model(final UmgrPrivilege object) {
	IModel<UmgrPrivilege> model = new LoadableDetachableModel<UmgrPrivilege>() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected UmgrPrivilege load() {
		UmgrPrivilege set = null;
		for (UmgrPrivilege obj : umgrPrivilegeEntries) {
		    if (obj.getPrivilege() == object.getPrivilege()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<UmgrPrivilege>(model);
    }

    public void loadUmgrPrivilegesList(UmgrPrivilege privilegeCriteria,
	    boolean forcedReload) throws DataProviderLoadException {

	if (umgrPrivilegeEntries == null || forcedReload) {

	    List<UmgrPrivilege> allEntries = getUmgrPrivilegesList(privilegeCriteria);

	    if (PortalUtils.exists(allEntries)) {
		umgrPrivilegeEntries = allEntries;
	    }
	}

    }

    private List<UmgrPrivilege> getUmgrPrivilegesList(
	    UmgrPrivilege privilegeCriteria) throws DataProviderLoadException {
	List<UmgrPrivilege> umgrPrivilegeList = new ArrayList<UmgrPrivilege>();
	try {
	    GetUmgrPrivilegesRequest request = mobBasePage
		    .getNewMobiliserRequest(GetUmgrPrivilegesRequest.class);
	    request.setPrivilegeIdSearchParameter(privilegeCriteria
		    .getPrivilege());
	    request.setDescriptionSearchParameter(privilegeCriteria
		    .getDescription());
	    GetUmgrPrivilegesResponse response = mobBasePage.wsRolePrivilegeClient
		    .getUmgrPrivileges(request);
	    if (mobBasePage.evaluateMobiliserResponse(response)) {
		umgrPrivilegeEntries = response.getPrivileges();
		if (umgrPrivilegeEntries != null) {
		    Collections.sort(umgrPrivilegeEntries,
			    new Comparator<UmgrPrivilege>() {
				@Override
				public int compare(UmgrPrivilege arg0,
					UmgrPrivilege arg1) {

				    int result = new String(arg0.getPrivilege())
					    .compareTo(new String(arg1
						    .getPrivilege()));

				    return result;
				}
			    });
		}
	    }

	} catch (Exception e) {
	    DataProviderLoadException dple = new DataProviderLoadException(
		    e.getMessage());
	    dple.setStackTrace(e.getStackTrace());
	    throw dple;
	}

	return umgrPrivilegeEntries;
    }

    protected List<UmgrPrivilege> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<UmgrPrivilege> sublist = getIndex(umgrPrivilegeEntries,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<UmgrPrivilege> getIndex(
	    List<UmgrPrivilege> UmgrPrivilegeEntries, String prop, boolean asc) {

	if (prop.equals("privilege")) {
	    return sort(UmgrPrivilegeEntries, asc);
	} else {
	    return UmgrPrivilegeEntries;
	}
    }

    private List<UmgrPrivilege> sort(List<UmgrPrivilege> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<UmgrPrivilege>() {

		@Override
		public int compare(UmgrPrivilege arg0, UmgrPrivilege arg1) {
		    return (arg0).getPrivilege().compareTo(
			    (arg1).getPrivilege());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<UmgrPrivilege>() {

		@Override
		public int compare(UmgrPrivilege arg0, UmgrPrivilege arg1) {
		    return (arg1).getPrivilege().compareTo(
			    (arg0).getPrivilege());
		}
	    });
	}
	return entries;
    }
}
