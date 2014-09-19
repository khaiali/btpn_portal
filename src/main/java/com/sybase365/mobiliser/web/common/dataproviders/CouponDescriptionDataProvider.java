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

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponTypeDescriptionsByCouponTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponTypeDescriptionsByCouponTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeDescription;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class CouponDescriptionDataProvider extends
	SortableDataProvider<CouponTypeDescription> {

    List<CouponTypeDescription> couponTypeDescriptionList = new ArrayList<CouponTypeDescription>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponDescriptionDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public CouponDescriptionDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends CouponTypeDescription> iterator(int first,
	    int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    protected List<CouponTypeDescription> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<CouponTypeDescription> sublist = getIndex(
		couponTypeDescriptionList, sortProperty, sortAsc).subList(
		first, first + count);

	return sublist;
    }

    protected List<CouponTypeDescription> getIndex(
	    List<CouponTypeDescription> couponTypeDescriptionList, String prop,
	    boolean asc) {

	if (prop.equals("mimeType")) {
	    return sort(couponTypeDescriptionList, asc);
	} else {
	    return couponTypeDescriptionList;
	}
    }

    private List<CouponTypeDescription> sort(
	    List<CouponTypeDescription> entries, boolean asc) {
	if (asc) {

	    Collections.sort(entries, new Comparator<CouponTypeDescription>() {

		@Override
		public int compare(CouponTypeDescription arg0,
			CouponTypeDescription arg1) {
		    return (arg0).getMimeType().compareTo((arg1).getMimeType());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<CouponTypeDescription>() {

		@Override
		public int compare(CouponTypeDescription arg0,
			CouponTypeDescription arg1) {

		    return (arg1).getMimeType().compareTo((arg0).getMimeType());
		}
	    });
	}
	return entries;
    }

    @Override
    public int size() {
	int count = 0;

	if (couponTypeDescriptionList == null) {
	    return count;
	}
	return couponTypeDescriptionList.size();
    }

    @Override
    public IModel<CouponTypeDescription> model(
	    final CouponTypeDescription object) {
	IModel<CouponTypeDescription> model = new LoadableDetachableModel<CouponTypeDescription>() {
	    @Override
	    protected CouponTypeDescription load() {
		CouponTypeDescription set = null;
		for (CouponTypeDescription obj : couponTypeDescriptionList) {

		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }

		}
		return set;
	    }
	};
	return new CompoundPropertyModel<CouponTypeDescription>(model);
    }

    public void loadCouponDescription(boolean forcedReload)
	    throws DataProviderLoadException {
	if (couponTypeDescriptionList == null || forcedReload) {
	    GetAllCouponTypeDescriptionsByCouponTypeRequest request;
	    try {

		request = getMobiliserBasePage().getNewMobiliserRequest(
			GetAllCouponTypeDescriptionsByCouponTypeRequest.class);
		request.setCouponType(getMobiliserBasePage()
			.getMobiliserWebSession().getCouponType().getId());
		GetAllCouponTypeDescriptionsByCouponTypeResponse response = getMobiliserBasePage().wsCouponsClient
			.getAllCouponTypeDescriptionsByCouponType(request);

		if (!getMobiliserBasePage().evaluateMobiliserResponse(response))
		    return;
		this.couponTypeDescriptionList = response.getDescription();
	    } catch (Exception e) {
		LOG.error("# An error occurred while fetching Coupon type", e);
		DataProviderLoadException dple = new DataProviderLoadException(
			e.getMessage());
		// throw dple;
	    }

	}

    }
}
