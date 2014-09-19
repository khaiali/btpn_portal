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

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeSet;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class FeeSetDataProvider extends SortableDataProvider<FeeSet> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(FeeTypeConfDataProvider.class);
    private List<FeeSet> feeSets = new ArrayList<FeeSet>();
    private MobiliserBasePage mobBasePage;

    public FeeSetDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns Fee Sets starting with index <code>first</code> and ending with
     * <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<FeeSet> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>FeeSets</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (feeSets == null) {
	    return count;
	}

	return feeSets.size();
    }

    @Override
    public final IModel<FeeSet> model(final FeeSet object) {
	IModel<FeeSet> model = new LoadableDetachableModel<FeeSet>() {
	    @Override
	    protected FeeSet load() {
		FeeSet set = null;
		for (FeeSet obj : feeSets) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<FeeSet>(model);
    }

    public void loadFeeSetsList(boolean forcedReload, Long feeSetId,
	    Boolean fetchIndividual) throws DataProviderLoadException {

	if (feeSets == null || forcedReload) {

	    List<FeeSet> allEntries = getMobiliserBasePage().getFeeSetsList(
		    fetchIndividual);

	    if (PortalUtils.exists(allEntries)) {

		if (PortalUtils.exists(feeSetId)) {
		    for (FeeSet feeSet : allEntries) {
			if (feeSet.getId().longValue() == feeSetId.longValue()) {
			    allEntries.clear();
			    if (feeSet.isIndividual())
				allEntries.add(feeSet);
			    feeSets = allEntries;
			    break;
			}
		    }
		} else {
		    if (fetchIndividual) {
			allEntries.clear();
			feeSets = allEntries;
		    } else {
			feeSets = allEntries;
		    }
		}
	    }
	}
    }

    public List<FeeSet> getFeeSets() {
	FeeSet feeSet = new FeeSet();
	feeSet.setId(new Long(1));
	feeSet.setName("Default Fee Set");
	feeSets.add(feeSet);

	feeSet = new FeeSet();
	feeSet.setId(new Long(2));
	feeSet.setName("New Fee Set");
	feeSets.add(feeSet);
	return feeSets;
    }

    protected List<FeeSet> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<FeeSet> sublist = getIndex(feeSets, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<FeeSet> getIndex(List<FeeSet> feeSetEntries, String prop,
	    boolean asc) {

	if (prop.equals("name")) {
	    return sort(feeSetEntries, asc);
	} else {
	    return feeSetEntries;
	}
    }

    private List<FeeSet> sort(List<FeeSet> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<FeeSet>() {

		@Override
		public int compare(FeeSet arg0, FeeSet arg1) {
		    return (arg0).getId().compareTo((arg1).getId());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<FeeSet>() {

		@Override
		public int compare(FeeSet arg0, FeeSet arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }
}
