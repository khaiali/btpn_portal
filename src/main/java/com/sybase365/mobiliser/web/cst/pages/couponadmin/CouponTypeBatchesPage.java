package com.sybase365.mobiliser.web.cst.pages.couponadmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.UploadCouponBatchRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UploadCouponBatchResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponBatch;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.CouponBatchesDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypeBatchesPage extends CouponTypeMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeBatchesPage.class);
    private CouponBatchesDataProvider dataProvider;
    private List<CouponBatch> couponBatchList;
    private FileUploadField upload;
    private Object content;
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;
    private int rowIndex = 1;
    private static final String WICKET_ID_editAction = "editLink";
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderById = "orderById";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_id = "id";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new FeedbackPanel("errorMessages"));
	final WebMarkupContainer addCouponBatchContainer = new WebMarkupContainer(
		"addCouponBatchContainer");
	addCouponBatchContainer.setOutputMarkupPlaceholderTag(true);
	createAddCouponBatchForm(addCouponBatchContainer);
	add(addCouponBatchContainer.setVisible(false));

	add(new AjaxLink("couponBatchAddLink") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addCouponBatchContainer.setVisible(true);
		target.addComponent(addCouponBatchContainer);

	    }
	}.add(new PrivilegedBehavior(this, Constants.PRIV_UPLOAD_CPN_BATCH)));

	createCouponTypesBatchesDataView();
    }

    private void createAddCouponBatchForm(
	    final WebMarkupContainer addCouponBatchContainer) {

	// Adding Add Form

	final Form<?> addCouponBatchForm = new Form("addCouponBatchForm",
		new CompoundPropertyModel<CouponTypeBatchesPage>(this));
	addCouponBatchForm.setMultiPart(true);

	addCouponBatchForm.add(upload = new FileUploadField("content"));
	addCouponBatchContainer.add(addCouponBatchForm);
	addCouponBatchContainer.setOutputMarkupPlaceholderTag(true).setVisible(
		false);
	addCouponBatchForm.add(new Button("save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		uploadBatch();
	    }

	    private void uploadBatch() {
		LOG.debug("# CouponTypeBatchesPage.uploadBatch()");
		if (!PortalUtils.exists(upload)
			|| !PortalUtils.exists(upload.getFileUpload())) {
		    error(getLocalizer().getString(
			    "couponTypeBatch.attachmentFile.required", this));
		    return;
		}

		try {
		    UploadCouponBatchRequest request = getNewMobiliserRequest(UploadCouponBatchRequest.class);
		    CouponBatch batch = new CouponBatch();
		    batch.setCouponType(getMobiliserWebSession()
			    .getCouponType().getId());
		    batch.setIsActive(false);
		    request.setBinaryContent(upload.getFileUpload().getBytes());
		    request.setCouponType(getMobiliserWebSession()
			    .getCouponType().getId());
		    UploadCouponBatchResponse response = wsCouponsClient
			    .uploadCouponBatch(request);
		    if (evaluateMobiliserResponse(response)) {
			info(getLocalizer().getString(
				"create.coupon.batch.upload.success", this));
			setResponsePage(CouponTypeBatchesPage.class);
		    }
		} catch (Exception e) {
		    LOG.error("An error occurred in uploading coupon batch", e);
		    error(getLocalizer().getString(
			    "create.coupon.batch.upload.error", this));
		}

	    }
	});

	addCouponBatchForm.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(final AjaxRequestTarget target) {
		addCouponBatchForm.clearInput();
		addCouponBatchContainer.setVisible(false);
		target.addComponent(addCouponBatchContainer);

	    }
	});

    }

    private void createCouponTypesBatchesDataView() {
	dataProvider = new CouponBatchesDataProvider("id", this, true);
	couponBatchList = new ArrayList<CouponBatch>();

	final DataView<CouponBatch> dataView = new DataView<CouponBatch>(
		WICKET_ID_pageable, dataProvider) {
	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadCouponBatches(forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading Coupon batches",
			    dple);
		    error(getLocalizer().getString("couponbatch.load.error",
			    this));
		}
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}
		refreshTotalItemCount();
		super.onBeforeRender();
	    }

	    private void refreshTotalItemCount() {
		totalItemString = Integer.toString(dataProvider.size());
		int total = getItemCount();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total)
			endIndex = total;
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }

	    @Override
	    protected void populateItem(final Item<CouponBatch> item) {
		final CouponBatch entry = item.getModelObject();
		couponBatchList.add(entry);
		item.add(new Label("id", String.valueOf(entry.getId())));
		item.add(new Label("creationDate", PortalUtils
			.getFormattedDate(entry.getCreationDate(),
				Locale.getDefault())));
		item.add(new Label("status", getDisplayValue(
			String.valueOf(entry.getStatus()),
			Constants.RESOURCE_BUNDLE_COUPON_STATUS)));
		item.add(new Label("intQuantity", String.valueOf(entry
			.getQuantity())));
		item.add(new Label("leftCount", getLeftCount(entry)));
		Link<CouponBatch> editLink = new Link<CouponBatch>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			CouponBatch entry = (CouponBatch) getModelObject();
			setResponsePage(new EditCouponTypeBatchPage(entry));
		    }
		};
		item.add(editLink);
	    }

	};
	dataView.setItemsPerPage(10);
	addOrReplace(dataView);

	addOrReplace(new OrderByBorder(WICKET_ID_orderById, WICKET_ID_id,
		dataProvider) {
	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the page is
		// loading
		// and the sort is clicked (clicking the name header), so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("couponTypeBatch.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"couponTypeBatch.addCouponBatchHelp", this)) {
	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return false;
		} else {
		    return super.isVisible();
		}
	    }
	});

	// Navigator example: << < 1 2 > >>
	addOrReplace(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	addOrReplace(new Label(WICKET_ID_totalItems, new PropertyModel<String>(
		this, "totalItemString")));

	addOrReplace(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	addOrReplace(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));

	// End of Pagenation
    }

    protected String getLeftCount(CouponBatch entry) {
	String leftCount = "";
	if (PortalUtils.exists(entry.getQuantity())) {
	    long left = 0;
	    if (PortalUtils.exists(entry.getUsed()))
		left = entry.getQuantity() - entry.getUsed();
	    else
		left = entry.getQuantity();
	    leftCount = String.valueOf(left);
	}
	return leftCount;
    }

    public Object getContent() {
	return content;
    }

    public void setContent(Object content) {
	this.content = content;
    }
}
