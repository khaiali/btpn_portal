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

import com.sybase365.mobiliser.money.contract.v5_0.coupon.SearchCouponsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.SearchCouponsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.SearchCouponsResponseType.Result;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponDataProvider extends SortableDataProvider<Result> {

    private transient List<Result> couponsList = new ArrayList<Result>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CustomerBeanDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public CouponDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends Result> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    @Override
    public IModel<Result> model(final Result object) {
	IModel<Result> model = new LoadableDetachableModel<Result>() {
	    @Override
	    protected Result load() {
		Result set = null;
		for (Result obj : couponsList) {
		    if (obj.getCoupon().getId() == object.getCoupon().getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<Result>(model);
    }

    @Override
    public int size() {
	int count = 0;

	if (couponsList == null) {
	    return count;
	}

	return couponsList.size();
    }

    protected List<Result> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<Result> sublist = getIndex(couponsList, sortProperty, sortAsc)
		.subList(first, first + count);

	return sublist;
    }

    protected List<Result> getIndex(List<Result> agentListEntries, String prop,
	    boolean asc) {

	if (prop.equals("Lid")) {
	    return sort(agentListEntries, asc);
	} else {
	    return agentListEntries;
	}
    }

    private List<Result> sort(List<Result> entries, boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<Result>() {

		@Override
		public int compare(Result arg0, Result arg1) {
		    return String
			    .valueOf((arg0).getCoupon().getId().intValue())
			    .compareTo(
				    String.valueOf((arg1).getCoupon().getId()
					    .intValue()));
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<Result>() {

		@Override
		public int compare(Result arg0, Result arg1) {
		    return String
			    .valueOf((arg1).getCoupon().getId().intValue())
			    .compareTo(
				    String.valueOf((arg0).getCoupon().getId()
					    .intValue()));
		}
	    });
	}
	return entries;
    }

    public List<Result> searchCoupons(SearchCouponsRequest req,
	    boolean forcedReload) throws DataProviderLoadException {

	if (!PortalUtils.exists(couponsList) || forcedReload) {
	    try {
		SearchCouponsResponse response = getMobiliserBasePage().wsCouponsClient
			.searchCoupons(req);
		if (getMobiliserBasePage().evaluateMobiliserResponse(response))
		    this.couponsList = response.getResult();

	    } catch (Exception e) {
		LOG.error("# Error finding agents", e);
		DataProviderLoadException dple = new DataProviderLoadException(
			e.getMessage());
		throw dple;
	    }

	}
	return couponsList;
    }
}
