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

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSet;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class LimitSetDataProvider extends SortableDataProvider<LimitSet> {

    private transient List<LimitSet> limitSetEntries = new ArrayList<LimitSet>();

    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(LimitSetDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public LimitSetDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends LimitSet> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<LimitSet> model(final LimitSet object) {
	IModel<LimitSet> model = new LoadableDetachableModel<LimitSet>() {
	    @Override
	    protected LimitSet load() {
		LimitSet set = null;
		for (LimitSet obj : limitSetEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<LimitSet>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (limitSetEntries == null) {
	    return count;
	}

	return limitSetEntries.size();
    }

    protected List<LimitSet> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<LimitSet> sublist = getIndex(limitSetEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<LimitSet> getIndex(List<LimitSet> limitSetEntries,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(limitSetEntries, asc);
	} else {
	    return limitSetEntries;
	}
    }

    private List<LimitSet> sort(List<LimitSet> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<LimitSet>() {

		@Override
		public int compare(LimitSet arg0, LimitSet arg1) {
		    return (arg0).getName().compareTo((arg1).getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<LimitSet>() {

		@Override
		public int compare(LimitSet arg0, LimitSet arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }

    public List<LimitSet> findLimitSet(Long limitSetId, boolean forcedReload)
	    throws DataProviderLoadException {
	try {
	    limitSetEntries = getMobiliserBasePage().findLimitSet(limitSetId);

	} catch (Exception e) {
	    LOG.error("# Error finding agents", e);
	}
	return limitSetEntries;
    }

}
