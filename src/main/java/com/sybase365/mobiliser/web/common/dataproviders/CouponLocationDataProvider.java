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

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponTypeLocationsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponTypeLocationsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeLocation;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class CouponLocationDataProvider extends
	SortableDataProvider<CouponTypeLocation> {

    List<CouponTypeLocation> couponTypeLocationList = new ArrayList<CouponTypeLocation>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponLocationDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public CouponLocationDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends CouponTypeLocation> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    protected List<CouponTypeLocation> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<CouponTypeLocation> sublist = getIndex(couponTypeLocationList,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<CouponTypeLocation> getIndex(
	    List<CouponTypeLocation> couponTypeLocationList, String prop,
	    boolean asc) {

	if (prop.equals("locale")) {
	    return sort(couponTypeLocationList, asc);
	} else {
	    return couponTypeLocationList;
	}
    }

    private List<CouponTypeLocation> sort(List<CouponTypeLocation> entries,
	    boolean asc) {
	if (asc) {

	    Collections.sort(entries, new Comparator<CouponTypeLocation>() {

		@Override
		public int compare(CouponTypeLocation arg0,
			CouponTypeLocation arg1) {
		    return (arg0).getLongitude().compareTo(
			    (arg1).getLongitude());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<CouponTypeLocation>() {

		@Override
		public int compare(CouponTypeLocation arg0,
			CouponTypeLocation arg1) {

		    return (arg1).getLongitude().compareTo(
			    (arg0).getLongitude());
		}
	    });
	}
	return entries;
    }

    @Override
    public int size() {
	int count = 0;

	if (couponTypeLocationList == null) {
	    return count;
	}
	return couponTypeLocationList.size();
    }

    @Override
    public IModel<CouponTypeLocation> model(final CouponTypeLocation object) {
	IModel<CouponTypeLocation> model = new LoadableDetachableModel<CouponTypeLocation>() {
	    @Override
	    protected CouponTypeLocation load() {
		CouponTypeLocation set = null;
		for (CouponTypeLocation obj : couponTypeLocationList) {

		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }

		}
		return set;
	    }
	};
	return new CompoundPropertyModel<CouponTypeLocation>(model);
    }

    public void loadCouponLocation(boolean forcedReload)
	    throws DataProviderLoadException {
	if (couponTypeLocationList == null || forcedReload) {
	    GetAllCouponTypeLocationsRequest request;
	    try {

		request = getMobiliserBasePage().getNewMobiliserRequest(
			GetAllCouponTypeLocationsRequest.class);
		request.setType(getMobiliserBasePage().getMobiliserWebSession()
			.getCouponType().getId());
		GetAllCouponTypeLocationsResponse response = getMobiliserBasePage().wsCouponsClient
			.getAllCouponTypeLocations(request);

		if (!getMobiliserBasePage().evaluateMobiliserResponse(response))
		    return;
		this.couponTypeLocationList = response.getLocation();
	    } catch (Exception e) {
		LOG.error("# An error occurred while fetching Coupon type", e);
		DataProviderLoadException dple = new DataProviderLoadException(
			e.getMessage());
		throw dple;
	    }

	}

    }
}
