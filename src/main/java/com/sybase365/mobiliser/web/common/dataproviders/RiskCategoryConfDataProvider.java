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

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RiskCategory;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class RiskCategoryConfDataProvider extends
	SortableDataProvider<RiskCategory> {
    private static final Logger LOG = LoggerFactory
	    .getLogger(RiskCategoryConfDataProvider.class);
    private transient List<RiskCategory> riskCatEntries = new ArrayList<RiskCategory>();
    private MobiliserBasePage mobBasePage;

    public RiskCategoryConfDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    /**
     * Returns RiskCategory starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<RiskCategory> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>RiskCategory</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (riskCatEntries == null) {
	    return count;
	}

	return riskCatEntries.size();
    }

    @Override
    public final IModel<RiskCategory> model(final RiskCategory object) {
	IModel<RiskCategory> model = new LoadableDetachableModel<RiskCategory>() {
	    @Override
	    protected RiskCategory load() {
		RiskCategory set = null;
		for (RiskCategory obj : riskCatEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<RiskCategory>(model);
    }

    public void loadRiskCatConfList(boolean forcedReload)
	    throws DataProviderLoadException {

	if (!PortalUtils.exists(riskCatEntries) || forcedReload) {

	    List<RiskCategory> allEntries = getMobiliserBasePage()
		    .getRiskCatConfList();

	    if (PortalUtils.exists(allEntries)) {
		riskCatEntries = allEntries;
	    }
	}

    }

    protected List<RiskCategory> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<RiskCategory> sublist = getIndex(riskCatEntries, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<RiskCategory> getIndex(
	    List<RiskCategory> riskCatEntries, String prop, boolean asc) {

	if (prop.equals("rcRiskCategory")) {
	    return sort(riskCatEntries, asc);
	} else {
	    return riskCatEntries;
	}
    }

    private List<RiskCategory> sort(List<RiskCategory> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<RiskCategory>() {

		@Override
		public int compare(RiskCategory arg0, RiskCategory arg1) {
		    return (arg0).getName().compareTo((arg1).getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<RiskCategory>() {

		@Override
		public int compare(RiskCategory arg0, RiskCategory arg1) {
		    return (arg1).getName().compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }

}
