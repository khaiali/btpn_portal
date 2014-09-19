package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponCategoriesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllCouponCategoriesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponCategoryTreeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponCategoryTreeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponCategoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponCategoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategory;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategoryInformation;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.panels.CouponTypePanel;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class EditCouponCategoriesPage extends CouponCategoryMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(EditCouponCategoriesPage.class);

    private CouponCategory couponCategory;
    private Long parentCategory;
    private List<CouponCategory> couponCategories;
    private List<CouponCategoryInformation> couponChildCategories;

    public EditCouponCategoriesPage() {
	super();
	this.couponCategory = getMobiliserWebSession().getCouponCategory();
	initPageComponents();
    }

    public EditCouponCategoriesPage(CouponCategory couponCategory) {
	super();
	this.couponCategory = couponCategory;
	initPageComponents();
    }

    private void initPageComponents() {
	List<KeyValue<Long, String>> couponCategoriesList = new ArrayList<KeyValue<Long, String>>();
	try {
	    GetCouponCategoryTreeRequest request = getNewMobiliserRequest(GetCouponCategoryTreeRequest.class);
	    request.setCategoryGroup(1);
	    GetCouponCategoryTreeResponse response = wsCouponsClient
		    .getCouponCategoryTree(request);
	    List<CouponCategoryInformation> categoryTree = new ArrayList<CouponCategoryInformation>();
	    if (evaluateMobiliserResponse(response)) {
		categoryTree = response.getRootCategory();
	    }
	    couponCategories = new ArrayList<CouponCategory>();
	    couponChildCategories = new ArrayList<CouponCategoryInformation>();
	    GetAllCouponCategoriesRequest req = getNewMobiliserRequest(GetAllCouponCategoriesRequest.class);
	    GetAllCouponCategoriesResponse res = wsCouponsClient
		    .getAllCouponCategories(req);
	    if (evaluateMobiliserResponse(res)) {
		couponCategories = res.getCategory();
	    }

	    findChildCategory(categoryTree);
	    removeChildFromList();
	    for (CouponCategory category : couponCategories) {
		couponCategoriesList.add(new KeyValue<Long, String>(category
			.getId(), category.getName()));
	    }

	} catch (Exception e) {
	    LOG.error("An error occured in removing child categories ", e);
	    error(getLocalizer().getString("parent.category.load.error", this));
	}
	final Form<?> form = new Form("couponCategoryForm",
		new CompoundPropertyModel<CouponTypePanel>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new RequiredTextField<String>("couponCategory.name")
		.setRequired(true).add(new ErrorIndicator()));
	// final LocalizableLookupDropDownChoice<Long> parentCateories = new
	// LocalizableLookupDropDownChoice<Long>(
	// "parentCategory", Long.class,
	// Constants.RESOURCE_BUNDLE_PARENT_CATEGORIES, this);
	final KeyValueDropDownChoice<Long, String> parentCateories = new KeyValueDropDownChoice<Long, String>(
		"couponCategory.parent", couponCategoriesList);
	parentCateories.setNullValid(true).add(new ErrorIndicator());

	form.add(parentCateories);
	form.add(new CheckBox("couponCategory.isActive"));
	form.add(new Button("Save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		updateCouponCategory();
	    }
	});
	form.add(new Button("Cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(CouponCategoriesPage.class);
	    }
	}.setDefaultFormProcessing(false));

	add(form);
    }

    private void findChildCategory(List<CouponCategoryInformation> categoryTree) {
	if (PortalUtils.exists(categoryTree)) {
	    for (CouponCategoryInformation cats : categoryTree) {
		if (cats.getId() == couponCategory.getId()) {
		    couponChildCategories.add(cats);
		    removeChildCategory(cats.getChild());
		    return;
		} else {
		    findChildCategory(cats.getChild());
		}
	    }
	} else {
	    return;
	}
    }

    private void removeChildCategory(
	    List<CouponCategoryInformation> categoryTree) {
	if (PortalUtils.exists(categoryTree)) {
	    for (CouponCategoryInformation cats : categoryTree) {
		couponChildCategories.add(cats);
		removeChildCategory(cats.getChild());
	    }
	} else
	    return;
    }

    private void removeChildFromList() {
	for (Iterator<CouponCategory> it = couponCategories.iterator(); it
		.hasNext();) {
	    long catId = it.next().getId();
	    for (CouponCategoryInformation selectedCategory : couponChildCategories) {
		if (catId == selectedCategory.getId()) {
		    it.remove();
		}
	    }
	}
    }

    protected void updateCouponCategory() {

	try {
	    UpdateCouponCategoryRequest request = getNewMobiliserRequest(UpdateCouponCategoryRequest.class);
	    getCouponCategory().setParent(getCouponCategory().getParent());
	    request.setCategory(getCouponCategory());
	    UpdateCouponCategoryResponse response = wsCouponsClient
		    .updateCouponCategory(request);
	    if (evaluateMobiliserResponse(response)) {
		getMobiliserWebSession().info(
			getLocalizer().getString(
				"update.coupon.category.success", this));
		setResponsePage(EditCouponCategoriesPage.class);
	    }

	} catch (Exception e) {
	    LOG.error("An error occurred while updating coupon category", e);
	    error(getLocalizer()
		    .getString("update.coupon.category.error", this));

	}
    }

    @Override
    protected Class getActiveMenu() {
	return EditCouponCategoriesPage.class;
    }

    public CouponCategory getCouponCategory() {
	return couponCategory;
    }

    public void setCouponCategory(CouponCategory couponCategory) {
	this.couponCategory = couponCategory;
    }

    public Long getParentCategory() {
	return parentCategory;
    }

    public void setParentCategory(Long parentCategory) {
	this.parentCategory = parentCategory;
    }

}
