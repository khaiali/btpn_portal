package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.ArrayList;
import java.util.Collections;
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
import com.sybase365.mobiliser.web.beans.PreferenceBean;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class PreferencesDataProvider extends SortableDataProvider<PreferenceBean> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(PreferencesDataProvider.class);

    private transient List<PreferenceBean> allPrefs = new ArrayList<PreferenceBean>();

    private MobiliserBasePage mobBasePage;

    public PreferencesDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns PreferenceBean starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<PreferenceBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>PreferenceBean</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (allPrefs == null) {
	    return count;
	}

	return allPrefs.size();
    }

    @Override
    public IModel<PreferenceBean> model(final PreferenceBean object) {
	IModel<PreferenceBean> model = new LoadableDetachableModel<PreferenceBean>() {
	    @Override
	    protected PreferenceBean load() {
		PreferenceBean set = null;
		for (PreferenceBean obj : allPrefs) {
		    if ((obj.toString().equals(object.toString()))) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<PreferenceBean>(model);
    }

    protected List<PreferenceBean> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<PreferenceBean> sublist = getIndex(allPrefs,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<PreferenceBean> getIndex(
	    List<PreferenceBean> allPrefs, String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(allPrefs, asc);
	} else {
	    return allPrefs;
	}
    }

    private List<PreferenceBean> sort(List<PreferenceBean> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries);

	}

	return entries;
    }

    public List<PreferenceBean> getAllPreferences() {
	return this.allPrefs;
    }

    public void setAllPreferences(List<PreferenceBean> value) {
	this.allPrefs = value;
    }

}