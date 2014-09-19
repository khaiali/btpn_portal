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
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerGroupBean;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class TrackerGroupDataProvider extends SortableDataProvider<TrackerGroupBean> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(TrackerGroupDataProvider.class);

    private transient List<TrackerGroupBean> trackerGroups = new ArrayList<TrackerGroupBean>();

    private int groupSize;
    private BaseDashboardPage basePage;

    public TrackerGroupDataProvider(int trackerGroupSize,
	    String defaultSortProperty, final BaseDashboardPage basePage) {
	setSort(defaultSortProperty, true);
	this.groupSize = trackerGroupSize;
	this.basePage = basePage;
    }

    /**
     * Returns TrackerGroupBean starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<TrackerGroupBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>TrackerGroupBean</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (trackerGroups == null) {
	    return count;
	}

	return trackerGroups.size();
    }

    @Override
    public final IModel<TrackerGroupBean> model(final TrackerGroupBean object) {
	IModel<TrackerGroupBean> model = new LoadableDetachableModel<TrackerGroupBean>() {

	    @Override
	    protected TrackerGroupBean load() {
		TrackerGroupBean set = null;
		for (TrackerGroupBean obj : trackerGroups) {
		    if (obj.getId().equals(object.getId())) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<TrackerGroupBean>(model);
    }

    public void loadTrackerGroups(boolean forcedReload)
	    throws DataProviderLoadException {

	if (trackerGroups == null || forcedReload) {

	    try {
		trackerGroups = new ArrayList<TrackerGroupBean>();

		List<TrackerBean> allTrackers =
			basePage.trackersDao.getTrackers();

		int index = Integer.MAX_VALUE-1;
		int groupId = 0;

		TrackerGroupBean trackerGroup = 
			new TrackerGroupBean(String.valueOf(groupId++), 
				this.groupSize);

		for (TrackerBean trackerBean : allTrackers) {

		    index++;

		    if (index >= this.groupSize) {
			index=0;
			trackerGroup = 
				new TrackerGroupBean(String.valueOf(groupId++),
					this.groupSize);
			trackerGroups.add(trackerGroup);
		    }

		    trackerGroup.addTrackerBeanToGroup(index, trackerBean);
		}

	    } catch (Exception e) {
		LOG.error("# Error loading trackers", e);
	    }
	}
    }

    public List<TrackerGroupBean> getTrackerGroups() {
	return trackerGroups;
    }

    protected List<TrackerGroupBean> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<TrackerGroupBean> sublist = getIndex(trackerGroups, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<TrackerGroupBean> getIndex(List<TrackerGroupBean> trackers,
	    String prop, boolean asc) {

	if (prop.equals("id")) {
	    return sort(trackerGroups, asc);
	} else {
	    return trackers;
	}
    }

    private List<TrackerGroupBean> sort(List<TrackerGroupBean> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<TrackerGroupBean>() {

		@Override
		public int compare(TrackerGroupBean arg0, TrackerGroupBean arg1) {
		    return (arg0).getId().toString().compareTo(
			    (arg1).getId().toString());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<TrackerGroupBean>() {

		@Override
		public int compare(TrackerGroupBean arg0, TrackerGroupBean arg1) {
		    return (arg1).getId().toString().compareTo(
			    (arg0).getId().toString());
		}
	    });
	}
	return entries;
    }
}