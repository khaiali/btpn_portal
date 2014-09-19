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

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponBatchesByCouponTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponBatchesByCouponTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponBatch;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class CouponBatchesDataProvider extends
	SortableDataProvider<CouponBatch> {
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponBatchesDataProvider.class);
    List<CouponBatch> couponBatchList = new ArrayList<CouponBatch>();
    private MobiliserBasePage mobBasePage;

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public CouponBatchesDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends CouponBatch> iterator(int first, int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    protected List<CouponBatch> find(int first, int count, String sortProperty,
	    boolean sortAsc) {

	List<CouponBatch> sublist = getIndex(couponBatchList, sortProperty,
		sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<CouponBatch> getIndex(List<CouponBatch> couponBatchList,
	    String prop, boolean asc) {

	if (prop.equals("locale")) {
	    return sort(couponBatchList, asc);
	} else {
	    return couponBatchList;
	}
    }

    private List<CouponBatch> sort(List<CouponBatch> entries, boolean asc) {
	if (asc) {

	    Collections.sort(entries, new Comparator<CouponBatch>() {

		@Override
		public int compare(CouponBatch arg0, CouponBatch arg1) {
		    return String.valueOf((arg0).getId().intValue()).compareTo(
			    String.valueOf((arg1).getId().intValue()));
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<CouponBatch>() {

		@Override
		public int compare(CouponBatch arg0, CouponBatch arg1) {

		    return String.valueOf((arg1).getId().intValue()).compareTo(
			    String.valueOf((arg0).getId().intValue()));
		}
	    });
	}
	return entries;
    }

    @Override
    public int size() {
	int count = 0;

	if (couponBatchList == null) {
	    return count;
	}
	return couponBatchList.size();
    }

    @Override
    public IModel<CouponBatch> model(final CouponBatch object) {
	IModel<CouponBatch> model = new LoadableDetachableModel<CouponBatch>() {
	    @Override
	    protected CouponBatch load() {
		CouponBatch set = null;
		for (CouponBatch obj : couponBatchList) {

		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }

		}
		return set;
	    }
	};
	return new CompoundPropertyModel<CouponBatch>(model);
    }

    public void loadCouponBatches(boolean forcedReload)
	    throws DataProviderLoadException {
	if (couponBatchList == null || forcedReload) {
	    GetAllCouponBatchesByCouponTypeRequest request;
	    try {

		request = getMobiliserBasePage().getNewMobiliserRequest(
			GetAllCouponBatchesByCouponTypeRequest.class);
		request.setCouponType(getMobiliserBasePage()
			.getMobiliserWebSession().getCouponType().getId());
		GetAllCouponBatchesByCouponTypeResponse response = getMobiliserBasePage().wsCouponsClient
			.getAllCouponBatchesByCouponType(request);

		if (!getMobiliserBasePage().evaluateMobiliserResponse(response))
		    return;
		this.couponBatchList = response.getBatch();
	    } catch (Exception e) {
		LOG.error("# An error occurred while fetching Coupon batches",
			e);
		DataProviderLoadException dple = new DataProviderLoadException(
			e.getMessage());
		// throw dple;
	    }
	}

    }
}
