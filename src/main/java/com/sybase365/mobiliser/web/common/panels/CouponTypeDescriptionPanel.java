package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponTypeDescriptionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.CreateCouponTypeDescriptionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllMimeTypesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetAllMimeTypesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetMimeTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.GetMimeTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponTypeDescriptionRequest;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.UpdateCouponTypeDescriptionResponse;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponMimeType;
import com.sybase365.mobiliser.money.contract.v5_0.coupon.beans.CouponTypeDescription;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.CouponTypeDescriptionPage;
import com.sybase365.mobiliser.web.cst.pages.couponadmin.EditCouponTypeDescriptionPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class CouponTypeDescriptionPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
	    .getLogger(CouponTypeDescriptionPanel.class);
    private FileUploadField upload;
    private Object content;
    private CouponTypeDescription couponTypeDescription;
    private MobiliserBasePage basePage;
    private boolean isCreateMode;
    boolean isFile = false;
    private TextArea<String> desc;
    private String descriptionText;

    public CouponTypeDescriptionPanel(String id, MobiliserBasePage basePage,
	    CouponTypeDescription couponTypeDescription) {
	super(id);
	this.basePage = basePage;
	this.couponTypeDescription = couponTypeDescription;
	if (PortalUtils.exists(couponTypeDescription)
		&& PortalUtils.exists(couponTypeDescription.getId())) {
	    isCreateMode = false;
	    if (isBinary(couponTypeDescription.getMimeType())) {
		isFile = true;
	    } else {
		if (PortalUtils.exists(couponTypeDescription.getContent()))
		    descriptionText = new String(
			    couponTypeDescription.getContent());
		else
		    descriptionText = new String("");
	    }
	} else {
	    isCreateMode = true;
	}
	constructPanel();
    }

    private void constructPanel() {
	final Form<?> form = new Form("couponTypeDescriptionForm",
		new CompoundPropertyModel<CouponTypeDescriptionPanel>(this));

	final WebMarkupContainer descriptionContainer = new WebMarkupContainer(
		"descriptionContainer");
	final WebMarkupContainer fileContainer = new WebMarkupContainer(
		"fileContainer");
	form.add(new FeedbackPanel("errorMessages"));
	form.setMultiPart(true);
	final LocalizableLookupDropDownChoice<String> languageDropDown = new LocalizableLookupDropDownChoice<String>(
		"couponTypeDescription.locale", String.class,
		Constants.RESOURCE_BUNDLE_SUPPORTED_LOCALE, basePage);
	languageDropDown.setNullValid(true).setRequired(true)
		.setEnabled(isCreateMode);
	form.add(languageDropDown);
	final KeyValueDropDownChoice<String, String> formatDropDown = new KeyValueDropDownChoice<String, String>(
		"couponTypeDescription.mimeType", getMimeTypeList());
	formatDropDown.setNullValid(true).setRequired(true)
		.setEnabled(isCreateMode);
	formatDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		String newSelection = formatDropDown.getModelObject();
		if (isBinary((newSelection))) {
		    fileContainer.setVisible(true);
		    desc.clearInput();
		    descriptionContainer.setVisible(false);
		} else {
		    descriptionContainer.setVisible(true);
		    fileContainer.setVisible(false);
		}
		target.addComponent(descriptionContainer);
		target.addComponent(fileContainer);
	    }
	});
	form.add(formatDropDown);
	form.add(new RequiredTextField<String>("couponTypeDescription.caption")
		.add(new ErrorIndicator()));
	desc = new TextArea<String>("descriptionText");
	desc.setRequired(true).add(Constants.largeStringValidator)
		.add(Constants.largeSimpleAttributeModifier)
		.add(new ErrorIndicator());
	descriptionContainer.add(desc);
	descriptionContainer.setOutputMarkupPlaceholderTag(true).setVisible(
		!isFile);

	fileContainer.add(upload = new FileUploadField("content"));

	fileContainer.setOutputMarkupPlaceholderTag(true).setVisible(isFile);
	form.add(new TextField<String>("couponTypeDescription.thumbnailUrl")
		.add(new ErrorIndicator()));
	form.add(new TextField<String>("couponTypeDescription.imageUrl")
		.add(new ErrorIndicator()));
	form.add(descriptionContainer);
	form.add(fileContainer);
	form.add(new Button("Save") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		if (isFile || isBinary(couponTypeDescription.getMimeType())) {
		    if (isCreateMode
			    && !PortalUtils.exists(upload.getFileUpload())) {
			error(getLocalizer().getString(
				"description.attachmentFile.required", this));
			return;
		    }

		    if (PortalUtils.exists(upload)
			    && PortalUtils.exists(upload.getFileUpload())) {
			FileUpload data = upload.getFileUpload();
			if (!couponTypeDescription.getMimeType()
				.equalsIgnoreCase(data.getContentType())) {
			    error(getLocalizer().getString(
				    "coupon.description.extension.error", this));
			    return;
			}

			couponTypeDescription.setContent(upload.getFileUpload()
				.getBytes());
		    }

		} else {
		    if (descriptionText.length() > 2000) {
			error(getLocalizer().getString(
				"descriptionText.length.exceeded", this));
			return;
		    }
		    couponTypeDescription.setContent(descriptionText.getBytes());
		}

		if (!PortalUtils.exists(couponTypeDescription.getId())) {
		    try {
			CreateCouponTypeDescriptionRequest request = basePage
				.getNewMobiliserRequest(CreateCouponTypeDescriptionRequest.class);
			couponTypeDescription.setCouponType(basePage
				.getMobiliserWebSession().getCouponType()
				.getId());
			request.setDescription(couponTypeDescription);
			CreateCouponTypeDescriptionResponse response = basePage.wsCouponsClient
				.createCouponTypeDescription(request);
			if (basePage.evaluateMobiliserResponse(response)) {
			    info(getLocalizer().getString(
				    "create.coupon.description.success", this));
			    couponTypeDescription.setId(response.getId());
			    setResponsePage(new EditCouponTypeDescriptionPage(
				    couponTypeDescription));
			}
		    } catch (Exception e) {
			LOG.error("An error occurred in creating description",
				e);
			error(getLocalizer().getString(
				"create.coupon.description.error", this));
		    }
		} else {
		    try {
			UpdateCouponTypeDescriptionRequest request = basePage
				.getNewMobiliserRequest(UpdateCouponTypeDescriptionRequest.class);
			request.setDescription(couponTypeDescription);
			UpdateCouponTypeDescriptionResponse response = basePage.wsCouponsClient
				.updateCouponTypeDescription(request);
			if (basePage.evaluateMobiliserResponse(response)) {
			    info(getLocalizer().getString(
				    "update.coupon.description.success", this));
			}
		    } catch (Exception e) {
			LOG.error("An error occurred in updating description",
				e);
			error(getLocalizer().getString(
				"update.coupon.description.error", this));
		    }

		}

	    }
	});
	form.add(new Button("Cancel") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(CouponTypeDescriptionPage.class);
	    }
	}.setDefaultFormProcessing(false));

	add(form);
    }

    protected boolean isBinary(String mimeType) {
	LOG.debug("# CouponTypeDescriptionPanel.isBinary()");
	try {
	    GetMimeTypeRequest req = basePage
		    .getNewMobiliserRequest(GetMimeTypeRequest.class);
	    req.setType(mimeType);
	    GetMimeTypeResponse response = basePage.wsCouponsClient
		    .getMimeType(req);
	    if (basePage.evaluateMobiliserResponse(response)) {
		CouponMimeType type = response.getMimeType();
		if (PortalUtils.exists(type))
		    return type.isIsBinary();
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in getting the mime type", e);
	    error(getLocalizer().getString("load.mime.type.error", this));
	}
	return false;
    }

    private List<KeyValue<String, String>> getMimeTypeList() {
	LOG.debug("# CouponTypeDescriptionPanel.getMimeTypeList()");
	List<KeyValue<String, String>> mimeTypes = new ArrayList<KeyValue<String, String>>();
	try {
	    GetAllMimeTypesRequest request = basePage
		    .getNewMobiliserRequest(GetAllMimeTypesRequest.class);
	    GetAllMimeTypesResponse response = basePage.wsCouponsClient
		    .getAllCouponMimeTypes(request);
	    if (basePage.evaluateMobiliserResponse(response)) {
		List<CouponMimeType> typeList = response.getMimeType();
		for (CouponMimeType type : typeList)
		    mimeTypes.add(new KeyValue<String, String>(type
			    .getMimeType(), type.getMimeType()));
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in getting the mime type list", e);
	    error(getLocalizer().getString("load.mime.type.list.error", this));
	}
	return mimeTypes;
    }

    public void setCouponType(CouponTypeDescription couponTypeDescription) {
	this.couponTypeDescription = couponTypeDescription;
    }

    public CouponTypeDescription getCouponType() {
	return couponTypeDescription;
    }

    public Object getContent() {
	return content;
    }

    public void setContent(Object content) {
	this.content = content;
    }

    public void setDescriptionText(String descriptionText) {
	this.descriptionText = descriptionText;
    }

    public String getDescriptionText() {
	return descriptionText;
    }

}