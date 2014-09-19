package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Alignment;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Unit;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.PropertyTreeColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponCategoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponCategoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponCategoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponCategoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponCategoryTreeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetCouponCategoryTreeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategory;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponCategoryInformation;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.util.tools.wicketutils.treetable.utils.TreeTableUtils;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponCategoriesPage extends BaseCouponAdminPage {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponCategoriesPage.class);
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;
    private String categoryName;
    private Long parentCategory;
    private boolean forceReload = true;
    private int rowIndex = 1;
    private static final String WICKET_ID_editAction = "editLink";
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByName = "orderByName";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_active = "active";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));
	final WebMarkupContainer addCouponCategoryFormDiv = new WebMarkupContainer(
		"addCouponCategoryFormDiv");
	createAddCouponCategoryForm(addCouponCategoryFormDiv);
	createCouponCategoriesDataView();
    }

    private void createCouponCategoriesDataView() {
	WebMarkupContainer categoryTreeDiv = new WebMarkupContainer(
		"categoryTreeDiv");
	categoryTreeDiv.setVisible(false);
	try {
	    // TODO change the default group
	    GetCouponCategoryTreeRequest request = getNewMobiliserRequest(GetCouponCategoryTreeRequest.class);
	    request.setCategoryGroup(1);
	    GetCouponCategoryTreeResponse response = wsCouponsClient
		    .getCouponCategoryTree(request);
	    List<CouponCategoryInformation> categoryTree = new ArrayList<CouponCategoryInformation>();
	    if (evaluateMobiliserResponse(response)) {
		categoryTree = response.getRootCategory();
	    }
	    if (!PortalUtils.exists(categoryTree)) {
		info(getLocalizer().getString("category.not.found", this));
	    } else {
		TreeModel treeModel = buildModel(categoryTree);
		List<IColumn> columns = new ArrayList<IColumn>();
		columns.add(new PropertyTreeColumn(new ColumnLocation(
			Alignment.LEFT, 722, Unit.PX), "root",
			"userObject.name"));
		TreeTable categoryTreeTable = new TreeTable("categoryTree",
			treeModel, columns.toArray(new IColumn[columns.size()])) {
		    private static final long serialVersionUID = 1L;

		    @Override
		    protected void onNodeLinkClicked(AjaxRequestTarget target,
			    TreeNode node) {
			super.onNodeLinkClicked(target, node);

			// access the category behind the node
			CouponCategoryInformation selectedCategory = (CouponCategoryInformation) ((DefaultMutableTreeNode) node)
				.getUserObject();
			try {
			    GetCouponCategoryRequest req = getNewMobiliserRequest(GetCouponCategoryRequest.class);
			    req.setId(selectedCategory.getId());
			    GetCouponCategoryResponse response = wsCouponsClient
				    .getCouponCategory(req);
			    if (evaluateMobiliserResponse(response)
				    && PortalUtils.exists(response
					    .getCategory())) {
				getMobiliserWebSession().setCouponCategory(
					response.getCategory());
				setResponsePage(new EditCouponCategoriesPage(
					response.getCategory()));
				return;
			    }
			} catch (Exception e) {
			    LOG.error(
				    "Error occurred in loading category details",
				    e);
			    error(getLocalizer().getString(
				    "load.category.detail.error", this));
			}
			// see that only one branch is expanded at all times
			TreeTableUtils.expandPath(getTreeState(), node);
			updateTree(target);
		    }

		};
		// in this case we do not want the root node to be shown
		// since
		// it is
		// just a pseude elemnt anyway.
		categoryTreeTable.setRootLess(true);
		categoryTreeTable.getTreeState().setAllowSelectMultiple(false);
		categoryTreeTable.getTreeState().collapseAll();

		// add the tree table component to the page's component tree
		categoryTreeDiv.setVisible(true);
		categoryTreeDiv.addOrReplace(categoryTreeTable);
	    }

	} catch (Exception e) {
	    LOG.error("# An error occuurred while loading categories", e);
	    error(getLocalizer().getString("load.category.all.error", this));
	}
	add(categoryTreeDiv);

    }

    public TreeModel buildModel(List<CouponCategoryInformation> nodeList) {

	// since no root node is provided, we create one
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
	addNode(root, nodeList);

	return new DefaultTreeModel(root);
    }

    public void addNode(DefaultMutableTreeNode parentNode,
	    List<CouponCategoryInformation> children) {

	if (children == null || children.size() == 0)
	    return;

	for (CouponCategoryInformation child : children) {
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
	    parentNode.add(node);
	    addNode(node, child.getChild());
	}
    }

    private void createAddCouponCategoryForm(
	    final WebMarkupContainer addCouponCategoryFormDiv) {

	addCouponCategoryFormDiv.setOutputMarkupId(true);
	addCouponCategoryFormDiv.setOutputMarkupPlaceholderTag(true);
	final Form<?> addCouponCategoryForm = new Form("addCouponCategoryForm",
		new CompoundPropertyModel<CouponCategoriesPage>(this));

	addCouponCategoryForm.add(new RequiredTextField<String>("categoryName")
		.setRequired(true).add(new ErrorIndicator()));

	final KeyValueDropDownChoice<Long, String> categoriesDropDown = new KeyValueDropDownChoice<Long, String>(
		"parentCategory", getAllCouponCategories());
	categoriesDropDown.setNullValid(true).setRequired(false);

	addCouponCategoryForm.add(categoriesDropDown);
	addCouponCategoryForm.add(new Button("save") {
	    @Override
	    public void onSubmit() {
		addCouponCategory();
	    }

	});

	addCouponCategoryForm.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addCouponCategoryFormDiv.setVisible(false);
		addCouponCategoryForm.clearInput();
		target.addComponent(addCouponCategoryFormDiv);

	    }
	});

	addCouponCategoryFormDiv.add(addCouponCategoryForm);
	addOrReplace(addCouponCategoryFormDiv.setVisible(false));

	// Adding Link

	add(new AjaxLink("couponCategoryAddLink") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addCouponCategoryForm.clearInput();
		addCouponCategoryFormDiv.setVisible(true);
		target.addComponent(addCouponCategoryFormDiv);
	    }
	}.add(new PrivilegedBehavior(this, Constants.PRIV_CST_LOGIN)));

    }

    protected void addCouponCategory() {

	try {
	    CreateCouponCategoryRequest request = getNewMobiliserRequest(CreateCouponCategoryRequest.class);
	    CouponCategory category = new CouponCategory();
	    category.setName(getCategoryName());
	    category.setParent(getParentCategory());
	    category.setCategoryGroup(1);
	    category.setIsActive(false);
	    request.setCategory(category);
	    CreateCouponCategoryResponse response = wsCouponsClient
		    .createCouponCategory(request);
	    if (evaluateMobiliserResponse(response)) {
		info(getLocalizer().getString("create.coupon.category.success",
			this));
		setResponsePage(CouponCategoriesPage.class);
	    }

	} catch (Exception e) {
	    LOG.error("An error occurred while adding coupon category", e);
	    error(getLocalizer()
		    .getString("create.coupon.category.error", this));

	}

    }

    public Long getParentCategory() {
	return parentCategory;
    }

    public void setParentCategory(Long parentCategory) {
	this.parentCategory = parentCategory;
    }

    public String getCategoryName() {
	return categoryName;
    }

    public void setCategoryName(String categoryName) {
	this.categoryName = categoryName;
    }
}