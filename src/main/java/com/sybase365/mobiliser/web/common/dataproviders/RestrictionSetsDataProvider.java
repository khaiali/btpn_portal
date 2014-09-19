package com.sybase365.mobiliser.web.common.dataproviders;

import java.util.ArrayList;
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
import com.sybase365.mobiliser.web.beans.RestrictionSetBean;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class RestrictionSetsDataProvider extends
	SortableDataProvider<RestrictionSetBean> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(RestrictionSetsDataProvider.class);
    private transient List<RestrictionSetBean> restrictionSets = new ArrayList<RestrictionSetBean>();
    private MobiliserBasePage mobBasePage;

    public RestrictionSetsDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns Fee Types starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<RestrictionSetBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>FeeTypes</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (restrictionSets == null) {
	    return count;
	}

	return restrictionSets.size();
    }

    @Override
    public final IModel<RestrictionSetBean> model(
	    final RestrictionSetBean object) {
	IModel<RestrictionSetBean> model = new LoadableDetachableModel<RestrictionSetBean>() {
	    @Override
	    protected RestrictionSetBean load() {
		RestrictionSetBean set = null;
		for (RestrictionSetBean obj : restrictionSets) {
		    if (obj.getRestrictionSet().getGroupId() == object
			    .getRestrictionSet().getGroupId()) {

			if (object.getRestriction() == null) {
			    set = obj;
			    break;
			} else {
			    if (obj.getRestriction() != null
				    && (obj.getRestriction().getRestrictionID()
					    .longValue() == object
					    .getRestriction()
					    .getRestrictionID().longValue())) {
				set = obj;
				break;
			    }

			}

		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<RestrictionSetBean>(model);
    }

    public void loadRestrictionSetBeansList(boolean forcedReload)
	    throws DataProviderLoadException {

	if (restrictionSets == null || forcedReload) {

	    List<RestrictionSetBean> allEntries = getMobiliserBasePage()
		    .getRestrictionSetBeanList();

	    if (PortalUtils.exists(allEntries)) {
		restrictionSets = allEntries;
	    }
	}
    }

    protected List<RestrictionSetBean> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	return restrictionSets;
    }

}
