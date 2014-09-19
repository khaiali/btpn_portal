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

import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrRolesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrRolesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrRole;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class RoleDataProvider extends SortableDataProvider<UmgrRole> {
    private static final long serialVersionUID = 1L;
    private transient List<UmgrRole> umgrRoleEntries = new ArrayList<UmgrRole>();
    private MobiliserBasePage mobBasePage;

    public RoleDataProvider(String defaultSortProperty,
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
    public Iterator<? extends UmgrRole> iterator(int first, int count) {
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

	if (umgrRoleEntries == null) {
	    return count;
	}

	return umgrRoleEntries.size();
    }

    @Override
    public final IModel<UmgrRole> model(final UmgrRole object) {
	IModel<UmgrRole> model = new LoadableDetachableModel<UmgrRole>() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected UmgrRole load() {
		UmgrRole set = null;
		for (UmgrRole obj : umgrRoleEntries) {
		    if (obj.getRole() == object.getRole()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<UmgrRole>(model);
    }

    public void loadUmgrRolesList(UmgrRole roleCriteria, boolean forcedReload)
	    throws DataProviderLoadException {

	if (umgrRoleEntries == null || forcedReload) {

	    List<UmgrRole> allEntries = getUmgrRolesList(roleCriteria);

	    if (PortalUtils.exists(allEntries)) {
		umgrRoleEntries = allEntries;
	    }
	}

    }

    private List<UmgrRole> getUmgrRolesList(UmgrRole roleCriteria)
	    throws DataProviderLoadException {
	List<UmgrRole> umgrRoleList = new ArrayList<UmgrRole>();
	try {
	    GetUmgrRolesRequest request = mobBasePage
		    .getNewMobiliserRequest(GetUmgrRolesRequest.class);
	    request.setRoleIdSearchParameter(roleCriteria.getRole());
	    request.setDescriptionSearchParameter(roleCriteria.getDescription());
	    GetUmgrRolesResponse response = mobBasePage.wsRolePrivilegeClient
		    .getUmgrRoles(request);
	    if (mobBasePage.evaluateMobiliserResponse(response)) {
		umgrRoleEntries = response.getRoles();
		if (umgrRoleEntries != null) {
		    Collections.sort(umgrRoleEntries,
			    new Comparator<UmgrRole>() {
				@Override
				public int compare(UmgrRole arg0, UmgrRole arg1) {

				    int result = new String(arg0.getRole())
					    .compareTo(new String(arg1
						    .getRole()));

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

	return umgrRoleEntries;
    }

    protected List<UmgrRole> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<UmgrRole> sublist = getIndex(umgrRoleEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<UmgrRole> getIndex(List<UmgrRole> UmgrRoleEntries,
	    String prop, boolean asc) {

	if (prop.equals("role")) {
	    return sort(UmgrRoleEntries, asc);
	} else {
	    return UmgrRoleEntries;
	}
    }

    private List<UmgrRole> sort(List<UmgrRole> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<UmgrRole>() {

		@Override
		public int compare(UmgrRole arg0, UmgrRole arg1) {
		    return (arg0).getRole().compareTo((arg1).getRole());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<UmgrRole>() {

		@Override
		public int compare(UmgrRole arg0, UmgrRole arg1) {
		    return (arg1).getRole().compareTo((arg0).getRole());
		}
	    });
	}
	return entries;
    }

}
