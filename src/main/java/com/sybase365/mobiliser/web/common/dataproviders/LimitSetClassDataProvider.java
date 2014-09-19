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

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class LimitSetClassDataProvider extends
	SortableDataProvider<LimitSetClass> {

    private transient List<LimitSetClass> limitSetClassEntries = new ArrayList<LimitSetClass>();

    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(LimitSetClassDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public LimitSetClassDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends LimitSetClass> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<LimitSetClass> model(final LimitSetClass object) {
	IModel<LimitSetClass> model = new LoadableDetachableModel<LimitSetClass>() {
	    @Override
	    protected LimitSetClass load() {
		LimitSetClass set = null;
		for (LimitSetClass obj : limitSetClassEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<LimitSetClass>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (limitSetClassEntries == null) {
	    return count;
	}

	return limitSetClassEntries.size();
    }

    protected List<LimitSetClass> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<LimitSetClass> sublist = getIndex(limitSetClassEntries,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<LimitSetClass> getIndex(List<LimitSetClass> limitSetEntries,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(limitSetEntries, asc);
	} else {
	    return limitSetEntries;
	}
    }

    private List<LimitSetClass> sort(List<LimitSetClass> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<LimitSetClass>() {

		@Override
		public int compare(LimitSetClass arg0, LimitSetClass arg1) {
		    return (arg0).getLimitSet().getName()
			    .compareTo((arg1).getLimitSet().getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<LimitSetClass>() {

		@Override
		public int compare(LimitSetClass arg0, LimitSetClass arg1) {
		    return (arg1).getLimitSet().getName()
			    .compareTo((arg0).getLimitSet().getName());
		}
	    });
	}
	return entries;
    }

    public List<LimitSetClass> findLimitSetClasses(long entityId, int type,
	    boolean forcedReload) throws DataProviderLoadException {
	try {
	    limitSetClassEntries = getMobiliserBasePage().getLimitSetClassList(
		    entityId, type);

	} catch (Exception e) {
	    LOG.error("# Error finding agents", e);
	}
	return limitSetClassEntries;
    }

}
