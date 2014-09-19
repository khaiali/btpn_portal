package com.sybase365.mobiliser.web.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.CreateAttachmentRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.CreateAttachmentResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.UpdateAttachmentRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.UpdateAttachmentResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageAttachment;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.cst.pages.notificationmgr.EditAttachmentPage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class MessageAttachmentPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(MessageAttachmentPanel.class);
    MessageAttachment attachment;
    MobiliserBasePage basePage;
    private FileUploadField upload;
    private Object uploadAttachmentFile;
    private boolean isCreateMode;
    private TextField<String> contentTypeTF;
    private TextField<String> charsetTF;

    public MessageAttachmentPanel(String id, MobiliserBasePage mobBasePage,
	    MessageAttachment attachment) {
	super(id);
	this.basePage = mobBasePage;
	this.attachment = attachment;
	if (attachment == null)
	    isCreateMode = true;
	else
	    isCreateMode = false;
	constructPanel();
    }

    private void constructPanel() {
	final Form form = new Form("attachmentForm",
		new CompoundPropertyModel<MessageAttachmentPanel>(this));

	form.setMultiPart(true);
	form.add(new FeedbackPanel("errorMessages"));
	form.add(upload = new FileUploadField("uploadAttachmentFile"));
	form.add(new RequiredTextField<String>("attachment.name")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.setEnabled(isCreateMode).add(new ErrorIndicator()));
	contentTypeTF = new TextField<String>("attachment.contentType");
	form.add(contentTypeTF.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.setEnabled(isCreateMode).add(new ErrorIndicator()));
	charsetTF = new TextField<String>("attachment.charset");
	form.add(charsetTF.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	form.add(new Button("addAttachment") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {

		if (isCreateMode
			&& (!PortalUtils.exists(upload) || !PortalUtils
				.exists(upload.getFileUpload()))) {
		    error(getLocalizer().getString(
			    "addAttachments.file.required", this));
		} else {
		    addAttachment();
		}
	    }
	});

	add(form);
    }

    protected void addAttachment() {
	final FileUpload fUpload = upload.getFileUpload();
	String contentType = attachment.getContentType();
	if (fUpload != null) {
	    contentType = fUpload.getContentType();
	    attachment.setContent(fUpload.getBytes());
	} else if (isCreateMode) {
	    error(getLocalizer().getString("upload.attachment.error", this));
	    return;
	}
	String charset = null;
	// content type can be in the form of text/plain; charset=utf-8
	// if charset info is present, extract it and set in attachment
	int charsetIndex = contentType.indexOf("; charset=");
	if (charsetIndex != -1) {
	    contentType = contentType.substring(0, charsetIndex);
	    charset = contentType.substring(charsetIndex
		    + "; charset=".length());
	}
	if (attachment.getId() == null) {
	    if (!PortalUtils.exists(attachment.getContentType())) {
		attachment.setContentType(contentType);
	    }
	    if (!PortalUtils.exists(attachment.getCharset())) {
		attachment.setCharset(charset);
	    }
	    try {
		CreateAttachmentRequest request = basePage
			.getNewMobiliserRequest(CreateAttachmentRequest.class);
		request.setAttachment(attachment);
		CreateAttachmentResponse response = basePage.wsTemplateClient
			.createAttachment(request);
		if (basePage.evaluateMobiliserResponse(response)) {
		    info(getLocalizer().getString("create.attachment.success",
			    this));
		    attachment.setId(response.getAttachmentId());
		    setResponsePage(new EditAttachmentPage(attachment));
		}
	    } catch (Exception e) {
		error(getLocalizer().getString("create.attachment.error", this));
		LOG.error("Error in create attachment", e);
	    }
	} else {
	    if (!contentType.equals(attachment.getContentType())) {
		error(basePage.getLocalizer().getString(
			"edit.wrong.type.error", contentTypeTF));
		return;
	    }
	    try {
		UpdateAttachmentRequest request = basePage
			.getNewMobiliserRequest(UpdateAttachmentRequest.class);
		request.setAttachment(attachment);
		UpdateAttachmentResponse response = basePage.wsTemplateClient
			.updateAttachment(request);
		if (basePage.evaluateMobiliserResponse(response)) {
		    info(getLocalizer().getString("update.attachment.success",
			    this));
		}
	    } catch (Exception e) {
		error(getLocalizer().getString("update.attachment.error", this));
		LOG.error("Error in update attachment", e);
	    }

	}
    }
}
