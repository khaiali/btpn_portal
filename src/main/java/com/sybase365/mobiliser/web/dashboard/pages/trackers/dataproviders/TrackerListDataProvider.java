package com.sybase365.mobiliser.web.dashboard.pages.trackers.dataproviders;

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

import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.dashboard.pages.BaseDashboardPage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerBean;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class TrackerListDataProvider extends SortableDataProvider<TrackerBean> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(TrackerListDataProvider.class);

    private transient List<TrackerBean> trackers = new ArrayList<TrackerBean>();

    private BaseDashboardPage basePage;

    public TrackerListDataProvider(String defaultSortProperty,
	    final BaseDashboardPage basePage) {
	setSort(defaultSortProperty, true);
	this.basePage = basePage;
    }

    /**
     * Returns TrackerBean starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<TrackerBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>TrackerBean</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (trackers == null) {
	    return count;
	}

	return trackers.size();
    }

    @Override
    public final IModel<TrackerBean> model(final TrackerBean object) {
	IModel<TrackerBean> model = new LoadableDetachableModel<TrackerBean>() {

	    @Override
	    protected TrackerBean load() {
		TrackerBean set = null;
		for (TrackerBean obj : trackers) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<TrackerBean>(model);
    }

    public void loadTrackers(boolean forcedReload)
	    throws DataProviderLoadException {

	if (trackers == null || forcedReload) {

	    try {
		trackers = basePage.trackersDao.getTrackers();
	    } catch (Exception e) {
		LOG.error("# Error loading trackers", e);
	    }
	}
    }

    public List<TrackerBean> getTrackers() {
	return trackers;
    }

    protected List<TrackerBean> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<TrackerBean> sublist = getIndex(trackers, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<TrackerBean> getIndex(List<TrackerBean> trackers,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(trackers, asc);
	} else {
	    return trackers;
	}
    }

    private List<TrackerBean> sort(List<TrackerBean> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<TrackerBean>() {

		@Override
		public int compare(TrackerBean arg0, TrackerBean arg1) {
		    return (arg0).getName().toString().compareTo(
			    (arg1).getName().toString());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<TrackerBean>() {

		@Override
		public int compare(TrackerBean arg0, TrackerBean arg1) {
		    return (arg1).getName().toString().compareTo(
			    (arg0).getName().toString());
		}
	    });
	}
	return entries;
    }
}
