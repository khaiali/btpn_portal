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

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponTypesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponTypesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponType;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class CouponTypeDataProvider extends SortableDataProvider<CouponType> {

    List<CouponType> couponTypeList = new ArrayList<CouponType>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public CouponTypeDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends CouponType> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    protected List<CouponType> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<CouponType> sublist = getIndex(couponTypeList, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<CouponType> getIndex(List<CouponType> couponTypeList,
	    String prop, boolean asc) {

	if (prop.equals("name")) {
	    return sort(couponTypeList, asc);
	} else {
	    return couponTypeList;
	}
    }

    private List<CouponType> sort(List<CouponType> entries, boolean asc) {
	if (asc) {

	    Collections.sort(entries, new Comparator<CouponType>() {

		@Override
		public int compare(CouponType arg0, CouponType arg1) {
		    return (arg0.getName()).compareTo((arg1).getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<CouponType>() {

		@Override
		public int compare(CouponType arg0, CouponType arg1) {
		    return (arg1.getName()).compareTo((arg0).getName());
		}
	    });
	}
	return entries;
    }

    @Override
    public int size() {

	int count = 0;

	if (couponTypeList == null) {
	    return count;
	}

	return couponTypeList.size();
    }

    @Override
    public IModel<CouponType> model(final CouponType object) {
	IModel<CouponType> model = new LoadableDetachableModel<CouponType>() {
	    @Override
	    protected CouponType load() {
		CouponType set = null;
		for (CouponType obj : couponTypeList) {

		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }

		}
		return set;
	    }
	};

	return new CompoundPropertyModel<CouponType>(model);
    }

    public void loadCouponTypes(boolean forcedReload)
	    throws DataProviderLoadException {
	if (couponTypeList == null || forcedReload) {
	    GetAllCouponTypesRequest request;
	    try {
		request = getMobiliserBasePage().getNewMobiliserRequest(
			GetAllCouponTypesRequest.class);
		request.setActiveOnly(false);
		GetAllCouponTypesResponse response = getMobiliserBasePage().wsCouponsClient
			.getAllCouponTypes(request);
		if (!getMobiliserBasePage().evaluateMobiliserResponse(response))
		    return;
		this.couponTypeList = response.getCouponType();
	    } catch (Exception e) {
		LOG.error("# An error occurred while fetching Coupon type", e);
		DataProviderLoadException dple = new DataProviderLoadException(
			e.getMessage());
		throw dple;
	    }
	}

    }
}
