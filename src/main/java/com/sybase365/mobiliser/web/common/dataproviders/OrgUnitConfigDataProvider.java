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

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.OrgUnit;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class OrgUnitConfigDataProvider extends SortableDataProvider<OrgUnit> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(OrgUnitConfigDataProvider.class);
    private transient List<OrgUnit> orgUnitEntries = new ArrayList<OrgUnit>();
    private MobiliserBasePage mobBasePage;

    public OrgUnitConfigDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns OrgUnit starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<OrgUnit> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>OrgUnit</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (orgUnitEntries == null) {
	    return count;
	}

	return orgUnitEntries.size();
    }

    @Override
    public final IModel<OrgUnit> model(final OrgUnit object) {
	IModel<OrgUnit> model = new LoadableDetachableModel<OrgUnit>() {
	    @Override
	    protected OrgUnit load() {
		OrgUnit set = null;
		for (OrgUnit obj : orgUnitEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<OrgUnit>(model);
    }

    public void loadOrgUnitConfList(boolean forcedReload)
	    throws DataProviderLoadException {

	if (orgUnitEntries == null || forcedReload) {

	    List<OrgUnit> allEntries = getMobiliserBasePage()
		    .getOrgUnitConfigurationsList();

	    if (PortalUtils.exists(allEntries)) {
		orgUnitEntries = allEntries;
	    }
	}

    }

    protected List<OrgUnit> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<OrgUnit> sublist = getIndex(orgUnitEntries, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<OrgUnit> getIndex(List<OrgUnit> customerNetworkEntries,
	    String prop, boolean asc) {

	if (prop.equals("ouOrgUnit")) {
	    return sort(customerNetworkEntries, asc);
	} else {
	    return customerNetworkEntries;
	}
    }

    private List<OrgUnit> sort(List<OrgUnit> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<OrgUnit>() {

		@Override
		public int compare(OrgUnit arg0, OrgUnit arg1) {
		    return (arg0).getName().compareTo((arg1).getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<OrgUnit>() {

		@Override
		public int compare(OrgUnit arg0, OrgUnit arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }
}
