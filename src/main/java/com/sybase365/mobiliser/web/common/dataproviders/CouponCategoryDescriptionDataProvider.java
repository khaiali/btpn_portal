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

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponCategoryDescriptionsByCategoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponCategoryDescriptionsByCategoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategoryDescription;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public class CouponCategoryDescriptionDataProvider extends
	SortableDataProvider<CouponCategoryDescription> {

    List<CouponCategoryDescription> couponCategoryDescriptionList = new ArrayList<CouponCategoryDescription>();
    private MobiliserBasePage mobBasePage;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponCategoryDescriptionDataProvider.class);

    private MobiliserBasePage getMobiliserBasePage() {
	return mobBasePage;
    }

    public CouponCategoryDescriptionDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage, boolean ascending) {
	setSort(defaultSortProperty, ascending);
	this.mobBasePage = mobBasePage;
    }

    @Override
    public Iterator<? extends CouponCategoryDescription> iterator(int first,
	    int count) {
	SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    protected List<CouponCategoryDescription> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<CouponCategoryDescription> sublist = getIndex(
		couponCategoryDescriptionList, sortProperty, sortAsc).subList(
		first, first + count);

	return sublist;
    }

    protected List<CouponCategoryDescription> getIndex(
	    List<CouponCategoryDescription> couponCategoryDescriptionList,
	    String prop, boolean asc) {

	if (prop.equals("description")) {
	    return sort(couponCategoryDescriptionList, asc);
	} else {
	    return couponCategoryDescriptionList;
	}
    }

    private List<CouponCategoryDescription> sort(
	    List<CouponCategoryDescription> entries, boolean asc) {
	if (asc) {

	    Collections.sort(entries,
		    new Comparator<CouponCategoryDescription>() {

			@Override
			public int compare(CouponCategoryDescription arg0,
				CouponCategoryDescription arg1) {
			    return (arg0).getCaption().compareTo(
				    (arg1).getCaption());
			}
		    });

	} else {

	    Collections.sort(entries,
		    new Comparator<CouponCategoryDescription>() {

			@Override
			public int compare(CouponCategoryDescription arg0,
				CouponCategoryDescription arg1) {

			    return (arg1).getLocale().compareTo(
				    (arg0).getLocale());
			}
		    });
	}
	return entries;
    }

    @Override
    public int size() {
	int count = 0;

	if (couponCategoryDescriptionList == null) {
	    return count;
	}
	return couponCategoryDescriptionList.size();
    }

    @Override
    public IModel<CouponCategoryDescription> model(
	    final CouponCategoryDescription object) {
	IModel<CouponCategoryDescription> model = new LoadableDetachableModel<CouponCategoryDescription>() {
	    @Override
	    protected CouponCategoryDescription load() {
		CouponCategoryDescription set = null;
		for (CouponCategoryDescription obj : couponCategoryDescriptionList) {

		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }

		}
		return set;
	    }
	};
	return new CompoundPropertyModel<CouponCategoryDescription>(model);
    }

    public List<CouponCategoryDescription> loadCouponCategoryDescription(
	    boolean forcedReload) throws DataProviderLoadException {
	if (couponCategoryDescriptionList == null || forcedReload) {
	    GetAllCouponCategoryDescriptionsByCategoryRequest request;
	    try {

		request = getMobiliserBasePage()
			.getNewMobiliserRequest(
				GetAllCouponCategoryDescriptionsByCategoryRequest.class);
		request.setCategory(getMobiliserBasePage()
			.getMobiliserWebSession().getCouponCategory().getId());
		GetAllCouponCategoryDescriptionsByCategoryResponse response = getMobiliserBasePage().wsCouponsClient
			.getAllCouponCategoryDescriptionsByCategory(request);

		if (!getMobiliserBasePage().evaluateMobiliserResponse(response))
		    return null;
		this.couponCategoryDescriptionList.addAll(response
			.getDescription());
	    } catch (Exception e) {
		LOG.error(
			"# An error occurred while fetching Coupon category descriptions",
			e);
		throw new DataProviderLoadException();
	    }

	}
	return couponCategoryDescriptionList;
    }
}
