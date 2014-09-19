package com.sybase365.mobiliser.web.common.panels;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.io.Streams;

import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteAttachmentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteAttachmentResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.AttachmentDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class AttachmentsPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AttachmentsPanel.class);

    private Long customerId;
    private MobiliserBasePage mobBasePage;
    private FeedbackPanel feedBackPanel;

    private FileUploadField upload;
    private Object uploadAttachmentFile;

    // Data Model for table list
    private AttachmentDataProvider dataProvider;
    private List<Attachment> attachments;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByName = "orderByName";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_name = "name";
    private static final String WICKET_ID_viewAction = "viewAction";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public AttachmentsPanel(String id, Long customerId,
	    MobiliserBasePage mobBasePage, FeedbackPanel feedBackPanel) {

	super(id);

	this.customerId = customerId;
	this.mobBasePage = mobBasePage;
	this.feedBackPanel = feedBackPanel;
	LOG.info("Created new AttachmentsPanel");

	constructPanel();
    }

    @SuppressWarnings("unchecked")
    private void constructPanel() {

	final Form form = new Form("attachmentForm",
		new CompoundPropertyModel<AttachmentsPanel>(this));

	form.setMultiPart(true);

	form.add(upload = new FileUploadField("uploadAttachmentFile"));
	final WebMarkupContainer dtaViewContainer = createAttachmentListDataView(form);

	dtaViewContainer.setOutputMarkupId(true);
	dtaViewContainer.setOutputMarkupPlaceholderTag(true);

	feedBackPanel.setOutputMarkupId(true);
	feedBackPanel.setOutputMarkupPlaceholderTag(true);
	form.add(new AjaxButton("addAttachment") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {

		addAttachment();
		target.addComponent(dtaViewContainer);
		target.addComponent(feedBackPanel);
	    }
	});

	add(form);
    }

    private void addAttachment() {

	if (upload != null) {

	    final FileUpload fUpload = upload.getFileUpload();

	    if (fUpload != null) {
		Attachment attachment = new Attachment();
		attachment.setContent(fUpload.getBytes());
		attachment.setContentType(fUpload.getContentType());
		attachment.setName(fUpload.getClientFileName());
		attachment
			.setAttachmentType(Constants.ATT_TYPE_SCANNED_REGISTRATION);

		// create attachment for customer if id is available
		if (customerId != null) {
		    try {
			attachment = mobBasePage.createAttachmentService(
				attachment, customerId);
			getAttachments().add(attachment);
			info(getLocalizer().getString(
				"attachments.add.success", this));
		    } catch (Exception e) {
			LOG.error("An error occurred for upload attachment", e);
			error(getLocalizer().getString("attachments.add.error",
				this));
		    }
		}
		// if no customer associated with attachment (yet) then park
		// the new attachment into the list
		else {
		    getAttachments().add(attachment);
		    info(getLocalizer().getString("attachments.add.success",
			    this));
		}
		forceReload = true;
	    } else {
		error(getLocalizer().getString("attachments.file.required",
			this));
	    }
	} else {
	    error(getLocalizer().getString("attachments.file.required", this));
	}
    }

    private void downloadAttachment(final Attachment attachment) {
	try {
	    getRequestCycle().setRequestTarget(new IRequestTarget() {

		@Override
		public void respond(RequestCycle requestCycle) {
		    try {
			InputStream attStream = new ByteArrayInputStream(
				attachment.getContent());

			WebResponse webResponse = (WebResponse) getResponse();
			webResponse.setAttachmentHeader(attachment.getName());
			webResponse.setContentType(attachment.getContentType());

			Streams.copy(attStream, webResponse.getOutputStream());
		    } catch (Exception e) {
			LOG.warn("Error in download attachment ", e);
		    }
		}

		@Override
		public void detach(RequestCycle arg0) {
		    // nothing to do here
		}
	    });
	} catch (Exception e) {
	    LOG.warn("Error in download attachment ", e);
	    error(getLocalizer().getString("attachments.download.error", this));
	}
    }

    private void deleteAttachment(final Attachment attachment) {
	try {
	    if (PortalUtils.exists(attachment.getId())) {
		DeleteAttachmentRequest delAttReq = mobBasePage
			.getNewMobiliserRequest(DeleteAttachmentRequest.class);

		delAttReq.setAttachmentId(attachment.getId());

		DeleteAttachmentResponse delAttResp = mobBasePage.wsAttachmentClient
			.deleteAttachment(delAttReq);

		if (!mobBasePage.evaluateMobiliserResponse(delAttResp)) {
		    error(getLocalizer().getString("attachments.delete.error",
			    this));
		    return;
		}
	    }
	    getAttachments().remove(attachment);
	    forceReload = true;
	} catch (Exception e) {
	    LOG.warn("An error occurred for delete attachment", e);
	    error(getLocalizer().getString("attachments.delete.error", this));
	}
    }

    private WebMarkupContainer createAttachmentListDataView(
	    final WebMarkupContainer parent) {
	dataProvider = new AttachmentDataProvider(WICKET_ID_name, mobBasePage);

	final DataView<Attachment> dataView = new DataView<Attachment>(
		WICKET_ID_pageable, dataProvider) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadCustomerAttachments(customerId,
			    forceReload);

		    refreshTotalItemCount();
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "An error occured while loading customer attachments list",
			    dple);
		    error(getLocalizer().getString("attachments.load.error",
			    this));
		}

		refreshTotalItemCount();
		if (dataProvider.size() > 0) {
		    setVisible(true);
		} else {
		    setVisible(false);
		}
		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<Attachment> item) {

		final Attachment entry = item.getModelObject();

		item.add(new Label(WICKET_ID_name, entry.getName()));

		// Edit Action
		Link<Attachment> editLink = new Link<Attachment>(
			WICKET_ID_viewAction, item.getModel()) {

		    @Override
		    public void onClick() {
			Attachment entry = (Attachment) getModelObject();
			downloadAttachment(entry);
		    }
		};
		item.add(editLink);

		// Remove Action
		Link removeLink = new Link<Attachment>(WICKET_ID_removeAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			Attachment entry = (Attachment) getModelObject();
			deleteAttachment(entry);
		    }
		};

		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"attachments.remove.confirm", this)
				+ "');"));
		boolean createMode;
		if (customerId == null) {
		    // Customer not yet created
		    createMode = true;
		} else {
		    // Customer already exist in DB
		    createMode = false;
		}

		item.add(removeLink.setVisible(createMode));

		// set items in even/odd rows to different css style classes
		item.add(new AttributeModifier(Constants.CSS_KEYWARD_CLASS,
			true, new AbstractReadOnlyModel<String>() {

			    @Override
			    public String getObject() {
				return (item.getIndex() % 2 == 1) ? Constants.CSS_STYLE_ODD
					: Constants.CSS_STYLE_EVEN;
			    }
			}));
	    }

	    @Override
	    public boolean isVisible() {
		if (getItemCount() > 0) {
		    return true;
		} else {
		    return super.isVisible();
		}
	    }

	    private void refreshTotalItemCount() {
		totalItemString = new Integer(dataProvider.size()).toString();
		int total = dataProvider.size();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total) {
			endIndex = total;
		    }
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }
	};

	dataView.setOutputMarkupPlaceholderTag(true);

	dataView.setItemsPerPage(5);

	parent.add(dataView);

	parent.add(new OrderByBorder(WICKET_ID_orderByName, WICKET_ID_name,
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

	parent.add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer()
		.getString("attachments.table.noItemsMsg", this)
		+ "\n"
		+ getLocalizer().getString(
			"attachments.table.addAttachmentHelp", this)) {

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
	parent.add(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	parent.add(new Label(WICKET_ID_totalItems, new PropertyModel<String>(
		this, "totalItemString")));

	parent.add(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	parent.add(new Label(WICKET_ID_endIndex, new PropertyModel(this,
		"endIndex")));
	return parent;
    }

    public void setUploadAttachmentFile(Object uploadAttachmentFile) {
	this.uploadAttachmentFile = uploadAttachmentFile;
    }

    public Object getUploadAttachmentFile() {
	return uploadAttachmentFile;
    }

    public List<Attachment> getAttachments() {
	if (PortalUtils.exists(dataProvider)) {
	    return dataProvider.getAttachments();
	} else {
	    return new ArrayList<Attachment>();
	}
    }

}
