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

import com.sybase365.mobiliser.money.contract.v5_0.system.beans.Restriction;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RestrictionRule;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * 
 * @author all
 */
@SuppressWarnings("serial")
public class RestrictionRulesDataProvider extends
	SortableDataProvider<RestrictionRule> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(RestrictionRulesDataProvider.class);
    private transient List<RestrictionRule> restrictionRules = new ArrayList<RestrictionRule>();
    private MobiliserBasePage mobBasePage;

    public RestrictionRulesDataProvider(String defaultSortProperty,
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
    public Iterator<RestrictionRule> iterator(int first, int count) {
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

	if (restrictionRules == null) {
	    return count;
	}

	return restrictionRules.size();
    }

    @Override
    public final IModel<RestrictionRule> model(final RestrictionRule object) {
	IModel<RestrictionRule> model = new LoadableDetachableModel<RestrictionRule>() {
	    @Override
	    protected RestrictionRule load() {
		RestrictionRule set = null;
		for (RestrictionRule obj : restrictionRules) {
		    if (obj.getEntityId() == object.getEntityId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<RestrictionRule>(model);
    }

    public void loadRestrictionRulesList(Restriction restriction)
	    throws DataProviderLoadException {

	// if (restrictionRules == null || forcedReload) {

	// List<RestrictionRule> allEntries = null;

	if (PortalUtils.exists(restriction)
		&& PortalUtils.exists(restriction.getRules())) {
	    restrictionRules = restriction.getRules();
	}
	// }
    }

    protected List<RestrictionRule> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<RestrictionRule> sublist = getIndex(restrictionRules,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<RestrictionRule> getIndex(
	    List<RestrictionRule> feeTypeEntries, String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(feeTypeEntries, asc);
	} else {
	    return feeTypeEntries;
	}
    }

    private List<RestrictionRule> sort(List<RestrictionRule> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<RestrictionRule>() {

		@Override
		public int compare(RestrictionRule arg0, RestrictionRule arg1) {
		    return (arg0).getMinTransactionAmount().compareTo(
			    (arg1).getMinTransactionAmount());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<RestrictionRule>() {

		@Override
		public int compare(RestrictionRule arg0, RestrictionRule arg1) {
		    return (arg1).getCurrency().compareTo((arg0).getCurrency());
		}
	    });
	}
	return entries;
    }
}
