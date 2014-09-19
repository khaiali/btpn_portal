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

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitClass;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class LimitClassDataProvider extends SortableDataProvider<LimitClass> {

    private transient List<LimitClass> limitClassEntries = new ArrayList<LimitClass>();

    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(LimitClassDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public LimitClassDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends LimitClass> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<LimitClass> model(final LimitClass object) {
	IModel<LimitClass> model = new LoadableDetachableModel<LimitClass>() {
	    @Override
	    protected LimitClass load() {
		LimitClass set = null;
		for (LimitClass obj : limitClassEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<LimitClass>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (limitClassEntries == null) {
	    return count;
	}

	return limitClassEntries.size();
    }

    protected List<LimitClass> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<LimitClass> sublist = getIndex(limitClassEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<LimitClass> getIndex(List<LimitClass> limitSetEntries,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(limitSetEntries, asc);
	} else {
	    return limitSetEntries;
	}
    }

    private List<LimitClass> sort(List<LimitClass> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<LimitClass>() {

		@Override
		public int compare(LimitClass arg0, LimitClass arg1) {
		    return (arg0).getName().compareTo((arg1).getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<LimitClass>() {

		@Override
		public int compare(LimitClass arg0, LimitClass arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }

    public List<LimitClass> findLimitClass(boolean forcedReload,
	    Boolean individual, Long entity, Integer entityType)
	    throws DataProviderLoadException {
	try {
	    List<LimitSetClass> limitSetList = new ArrayList<LimitSetClass>();
	    limitClassEntries = new ArrayList<LimitClass>();
	    if (individual) {
		limitSetList = getMobiliserBasePage().getLimitSetClassList(
			entity, entityType);
		if (PortalUtils.exists(limitSetList)
			&& limitSetList.size() == 1) {
		    limitClassEntries.add(limitSetList.get(0).getLimitClass());
		}
	    } else {
		limitClassEntries = getMobiliserBasePage().findLimitClassList();
	    }
	} catch (Exception e) {
	    LOG.error("# Error finding agents", e);
	}
	return limitClassEntries;
    }

}
