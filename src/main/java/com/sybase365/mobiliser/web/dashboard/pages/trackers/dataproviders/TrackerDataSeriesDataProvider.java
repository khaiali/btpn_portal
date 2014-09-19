package com.sybase365.mobiliser.web.dashboard.pages.trackers.dataproviders;

import com.sybase365.mobiliser.web.common.dataproviders.*;
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

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.beans.TrackerDataSeriesBean;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class TrackerDataSeriesDataProvider extends SortableDataProvider<TrackerDataSeriesBean> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(TrackerDataSeriesDataProvider.class);
    private transient List<TrackerDataSeriesBean> dataSeriesList = new ArrayList<TrackerDataSeriesBean>();
    private MobiliserBasePage mobBasePage;

    public TrackerDataSeriesDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    /**
     * Returns TrackerDataSeriesBean starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<TrackerDataSeriesBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>TrackerDataSeriesBean</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (dataSeriesList == null) {
	    return count;
	}

	return dataSeriesList.size();
    }

    @Override
    public final IModel<TrackerDataSeriesBean> model(final TrackerDataSeriesBean object) {
	IModel<TrackerDataSeriesBean> model = new LoadableDetachableModel<TrackerDataSeriesBean>() {

	    @Override
	    protected TrackerDataSeriesBean load() {
		TrackerDataSeriesBean set = null;
		for (TrackerDataSeriesBean obj : dataSeriesList) {
		    if (obj.toString().equals(object.toString())) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<TrackerDataSeriesBean>(model);
    }

    public void setTrackerDataSeries(List<TrackerDataSeriesBean> listValue) {
	this.dataSeriesList = listValue;
    }

    public List<TrackerDataSeriesBean> getTrackerDataSeriesBeans() {
	return dataSeriesList;
    }

    protected List<TrackerDataSeriesBean> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<TrackerDataSeriesBean> sublist = getIndex(dataSeriesList, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<TrackerDataSeriesBean> getIndex(List<TrackerDataSeriesBean> dataSeriesList,
	    String prop, boolean asc) {

	if (prop.equals("objectName")) {
	    return sort(dataSeriesList, asc);
	} else {
	    return dataSeriesList;
	}
    }

    private List<TrackerDataSeriesBean> sort(List<TrackerDataSeriesBean> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<TrackerDataSeriesBean>() {

		@Override
		public int compare(TrackerDataSeriesBean arg0, TrackerDataSeriesBean arg1) {
		    return (arg0).getObjectName().toString().compareTo(
			    (arg1).getObjectName().toString());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<TrackerDataSeriesBean>() {

		@Override
		public int compare(TrackerDataSeriesBean arg0, TrackerDataSeriesBean arg1) {
		    return (arg1).getObjectName().toString().compareTo(
			    (arg0).getObjectName().toString());
		}
	    });
	}
	return entries;
    }
}
