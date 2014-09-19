package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponTypeCategoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponTypeCategoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.DeleteCouponTypeCategoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.DeleteCouponTypeCategoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.FindAllCategoriesForCouponTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.FindAllCategoriesForCouponTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategory;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeCategory;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownMultiChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypeCategoriesPage extends CouponTypeMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeCategoriesPage.class);

    List<KeyValue<Long, String>> availableCategoriesList;
    List<KeyValue<Long, String>> assignedCategoriesList;
    private List<Long> availableCategories;
    private List<Long> assignedCategories;

    public CouponTypeCategoriesPage() {
	super();

	availableCategoriesList = getAllCouponCategories();
	assignedCategoriesList = getAllCategoriesByCoupontype();
	availableCategoriesList.removeAll(assignedCategoriesList);
	initPageComponents();
    }

    private List<KeyValue<Long, String>> getAllCategoriesByCoupontype() {
	List<KeyValue<Long, String>> categories = new ArrayList<KeyValue<Long, String>>();
	try {
	    FindAllCategoriesForCouponTypeRequest req = getNewMobiliserRequest(FindAllCategoriesForCouponTypeRequest.class);
	    req.setCouponType(getMobiliserWebSession().getCouponType().getId());
	    FindAllCategoriesForCouponTypeResponse response = wsCouponsClient
		    .findAllCategoriesForCouponType(req);
	    if (evaluateMobiliserResponse(response)) {
		if (PortalUtils.exists(response.getCategory())) {
		    List<CouponCategory> couponCategories = response
			    .getCategory();
		    for (CouponCategory category : couponCategories) {
			categories.add(new KeyValue<Long, String>(category
				.getId(), category.getName()));
		    }
		}
	    }
	} catch (Exception e) {
	    LOG.error(
		    "An error occurred in loading categories of coupon type ",
		    e);
	    error(getLocalizer().getString("load.coupon.assigned.error", this));
	}
	return categories;
    }

    protected void initPageComponents() {
	Form<?> form = new Form("categoriesForm",
		new CompoundPropertyModel<CouponTypeCategoriesPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	final KeyValueDropDownMultiChoice<Long, String> availableChoice = new KeyValueDropDownMultiChoice<Long, String>(
		"availableCategories", availableCategoriesList);
	form.add(availableChoice);

	form.add(new Button("addCategory") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		boolean isSuccess = true;
		if (getAvailableCategories() == null
			|| getAvailableCategories().isEmpty()) {
		    error(getLocalizer().getString(
			    "availableCategories.Required", this));
		    return;
		} else {
		    for (Long category : getAvailableCategories()) {
			if (!addCategory(category)) {
			    isSuccess = false;
			    break;
			}
		    }
		    if (isSuccess) {
			info(getLocalizer().getString(
				"assign.category.success", this));
		    }

		    setResponsePage(new CouponTypeCategoriesPage());
		}

	    }

	});

	final KeyValueDropDownMultiChoice<Long, String> assigedChoice = new KeyValueDropDownMultiChoice<Long, String>(
		"assignedCategories", assignedCategoriesList);
	form.add(assigedChoice);

	form.add(new Button("removeCategory") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		boolean isSuccess = true;
		if (getAssignedCategories() == null
			|| getAssignedCategories().isEmpty()) {
		    error(getLocalizer().getString(
			    "assignedCategories.Required", this));
		    return;
		} else {
		    for (Long category : getAssignedCategories()) {

			if (!removeCategory(category)) {
			    isSuccess = false;
			    break;
			}

		    }
		    if (isSuccess) {
			info(getLocalizer().getString(
				"remove.category.success", this));
		    }

		    setResponsePage(new CouponTypeCategoriesPage());
		}

	    }

	});
	add(form);

    }

    private boolean addCategory(Long categoryIdx) {

	try {

	    CreateCouponTypeCategoryRequest request = getNewMobiliserRequest(CreateCouponTypeCategoryRequest.class);

	    CouponTypeCategory ctCategory = new CouponTypeCategory();

	    ctCategory.setCategory(categoryIdx);
	    ctCategory.setCouponType(getMobiliserWebSession().getCouponType()
		    .getId());
	    request.setLink(ctCategory);
	    CreateCouponTypeCategoryResponse response = wsCouponsClient
		    .createCouponTypeCategory(request);
	    if (evaluateMobiliserResponse(response)) {
		return true;
	    }

	} catch (Exception e) {
	    LOG.error("Error in assigning categories to coupon type ", e);
	    error(getLocalizer().getString("assign.category.error", this));
	}
	return false;

    }

    private boolean removeCategory(Long category) {
	try {
	    DeleteCouponTypeCategoryRequest request = getNewMobiliserRequest(DeleteCouponTypeCategoryRequest.class);
	    CouponTypeCategory ctCategory = new CouponTypeCategory();
	    ctCategory.setCategory(category);
	    ctCategory.setCouponType(getMobiliserWebSession().getCouponType()
		    .getId());
	    request.setLink(ctCategory);
	    DeleteCouponTypeCategoryResponse response = wsCouponsClient
		    .deleteCouponTypeCategory(request);
	    if (evaluateMobiliserResponse(response)) {
		return true;
	    }

	} catch (Exception e) {
	    LOG.error("Error in removing categories to coupon type ", e);
	    error(getLocalizer().getString("remove.category.error", this));
	}
	return false;
    }

    public void setAvailableCategories(List<Long> availableCategories) {
	this.availableCategories = availableCategories;
    }

    public List<Long> getAvailableCategories() {
	return availableCategories;
    }

    public void setAssignedCategories(List<Long> assignedCategories) {
	this.assignedCategories = assignedCategories;
    }

    public List<Long> getAssignedCategories() {
	return assignedCategories;
    }
}
