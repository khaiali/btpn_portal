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

import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.ScaleStepConfBean;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ScaleStepsDataProvider extends
	SortableDataProvider<ScaleStepConfBean> {

    private static final Logger LOG = LoggerFactory
	    .getLogger(ScaleStepsDataProvider.class);
    private transient List<ScaleStepConfBean> scaleSteps = new ArrayList<ScaleStepConfBean>();
    private MobiliserBasePage mobBasePage;

    public ScaleStepsDataProvider(String defaultSortProperty,
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
    public Iterator<ScaleStepConfBean> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>ScaleSteps</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {
	int count = 0;

	if (scaleSteps == null) {
	    return count;
	}

	return scaleSteps.size();
    }

    @Override
    public final IModel<ScaleStepConfBean> model(final ScaleStepConfBean object) {
	IModel<ScaleStepConfBean> model = new LoadableDetachableModel<ScaleStepConfBean>() {
	    @Override
	    protected ScaleStepConfBean load() {
		long threshHold1 = object.getThresholdAmount() != null ? object
			.getThresholdAmount().longValue() : -1;
		String currency1 = object.getCurrency() != null ? object
			.getCurrency() : "";
		ScaleStepConfBean set = null;
		for (ScaleStepConfBean obj : scaleSteps) {
		    long threshHold2 = obj.getThresholdAmount() != null ? obj
			    .getThresholdAmount().longValue() : -1;
		    String currency2 = obj.getCurrency() != null ? obj
			    .getCurrency() : "";

		    if (obj.getFeeTypeName().equals(object.getFeeTypeName())
			    && currency1.equals(currency2)
			    && threshHold1 == threshHold2) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<ScaleStepConfBean>(model);
    }

    public void loadScaleStepsList(Long feeSetId, boolean forcedReload)
	    throws DataProviderLoadException {

	if (scaleSteps == null || forcedReload) {

	    List<ScaleStepConfBean> allEntries = getMobiliserBasePage()
		    .getScaleStepBeanList(feeSetId);

	    if (PortalUtils.exists(allEntries)) {
		scaleSteps = allEntries;
	    }
	}
    }

    protected List<ScaleStepConfBean> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<ScaleStepConfBean> sublist = getIndex(scaleSteps, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<ScaleStepConfBean> getIndex(
	    List<ScaleStepConfBean> scaleStepsEntries, String prop, boolean asc) {

	if (prop.equals("sStepFeeType")) {
	    return sort(scaleStepsEntries, asc);
	} else {
	    return scaleStepsEntries;
	}
    }

    private List<ScaleStepConfBean> sort(List<ScaleStepConfBean> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<ScaleStepConfBean>() {

		@Override
		public int compare(ScaleStepConfBean arg0,
			ScaleStepConfBean arg1) {

		    if (arg0.getFeeTypeName().compareTo(arg1.getFeeTypeName()) < 0)
			return -1;
		    else if (arg0.getFeeTypeName().compareTo(
			    arg1.getFeeTypeName()) > 0)
			return 1;
		    else {
			if ((arg0.getCurrency() != null && arg1.getCurrency() != null)
				&& (arg0.getCurrency().compareTo(
					arg1.getCurrency()) < 0))
			    return -1;
			else if ((arg0.getCurrency() != null && arg1
				.getCurrency() != null)
				&& (arg0.getCurrency().compareTo(
					arg1.getCurrency()) > 0))
			    return 1;
			else {

			    if (arg0.getThresholdAmount() == null
				    && arg1.getThresholdAmount() == null) {
				return -1;
			    } else if (arg0.getThresholdAmount() == null
				    && arg1.getThresholdAmount() != null)
				return -1;
			    else if (arg0.getThresholdAmount() != null
				    && arg1.getThresholdAmount() == null)
				return 1;
			    else {

				if (arg0.getThresholdAmount().longValue() < arg1
					.getThresholdAmount().longValue())
				    return -1;
				else if (arg0.getThresholdAmount().longValue() > arg1
					.getThresholdAmount().longValue())
				    return 1;
				else
				    return 0;
			    }
			}
		    }

		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<ScaleStepConfBean>() {

		@Override
		public int compare(ScaleStepConfBean arg0,
			ScaleStepConfBean arg1) {
		    if (arg1.getFeeTypeName().compareTo(arg0.getFeeTypeName()) < 0)
			return -1;
		    else if (arg1.getFeeTypeName().compareTo(
			    arg0.getFeeTypeName()) > 0)
			return 1;
		    else {
			if ((arg1.getCurrency() != null && arg0.getCurrency() != null)
				&& (arg1.getCurrency().compareTo(
					arg0.getCurrency()) < 0))
			    return -1;
			else if ((arg1.getCurrency() != null && arg0
				.getCurrency() != null)
				&& (arg1.getCurrency().compareTo(
					arg0.getCurrency()) > 0))

			    return 1;
			else {

			    if (arg1.getThresholdAmount() == null
				    && arg0.getThresholdAmount() == null) {
				return -1;
			    } else if (arg1.getThresholdAmount() == null
				    && arg0.getThresholdAmount() != null)
				return -1;
			    else if (arg1.getThresholdAmount() != null
				    && arg0.getThresholdAmount() == null)
				return 1;
			    else {

				if (arg1.getThresholdAmount().longValue() < arg0
					.getThresholdAmount().longValue())
				    return -1;
				else if (arg1.getThresholdAmount().longValue() > arg0
					.getThresholdAmount().longValue())
				    return 1;
				else
				    return 0;
			    }
			}
		    }

		}
	    });
	}
	return entries;
    }
}
