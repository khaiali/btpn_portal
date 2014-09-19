package com.sybase365.mobiliser.web.common.panels;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.io.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.money.contract.v5_0.system.GetFinishedBulkProcessingFileRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetFinishedBulkProcessingFileResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFileHistoryBean;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFileHistoryListItem;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class BulkHistoryFileDetailsPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
	    .getLogger(BulkHistoryFileDetailsPanel.class);
    private MobiliserBasePage basePage;
    private BulkFileHistoryListItem bulkFileHistoryListItem;
    private String date;
    private String fileType;
    private String status;

    public BulkHistoryFileDetailsPanel(String id, MobiliserBasePage basePage,
	    BulkFileHistoryListItem bulkFileHistoryListItem) {
	super(id);
	this.basePage = basePage;
	this.bulkFileHistoryListItem = bulkFileHistoryListItem;
	setDate(bulkFileHistoryListItem.getUploadDate());
	setFileType(bulkFileHistoryListItem.getBulkFileType());
	setStatus(bulkFileHistoryListItem.getBulkFileStatus());
	constructPanel();
    }

    private void constructPanel() {
	Form<?> form = new Form("historyDetailsForm",
		new CompoundPropertyModel<BulkHistoryFileDetailsPanel>(this));
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new TextField<String>("fileType")
		.setEnabled(false)
		.add(new SimpleAttributeModifier("readonly", "readonly"))
		.add(new SimpleAttributeModifier("style",
			"background-color: #E6E6E6;")));
	form.add(new TextField<String>("bulkFileHistoryListItem.bulkFileName")
		.setEnabled(false)
		.add(new SimpleAttributeModifier("readonly", "readonly"))
		.add(new SimpleAttributeModifier("style",
			"background-color: #E6E6E6;")));
	form.add(new TextField<String>("date")
		.setEnabled(false)
		.add(new SimpleAttributeModifier("readonly", "readonly"))
		.add(new SimpleAttributeModifier("style",
			"background-color: #E6E6E6;")));
	form.add(new TextField<String>("status")
		.setEnabled(false)
		.add(new SimpleAttributeModifier("readonly", "readonly"))
		.add(new SimpleAttributeModifier("style",
			"background-color: #E6E6E6;")));

	form.add(new Button("historyFileDownolad") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {

		try {
		    GetFinishedBulkProcessingFileRequest request = basePage
			    .getNewMobiliserRequest(GetFinishedBulkProcessingFileRequest.class);
		    request.setEntityId(bulkFileHistoryListItem.getEntityId());
		    GetFinishedBulkProcessingFileResponse response = basePage.wsBulkProcessingClient
			    .getFinishedBulkProcessingFile(request);
		    BulkFileHistoryBean bulkFileHistoryBean = response
			    .getHistoryItem();
		    downloadAttachment(bulkFileHistoryBean);
		} catch (Exception e) {
		    LOG.error("# Error in download attachment ", e);
		    error(getLocalizer().getString("bulkFile.download.error",
			    this));
		}
	    }
	});
	add(form);

    }

    public String getDate() {
	return date;
    }

    public void setDate(XMLGregorianCalendar date) {
	this.date = PortalUtils.getFormattedDate(date, Locale.getDefault());
    }

    private void downloadAttachment(final BulkFileHistoryBean file) {
	try {
	    getRequestCycle().setRequestTarget(new IRequestTarget() {

		public void respond(RequestCycle requestCycle) {
		    try {
			InputStream attStream = new ByteArrayInputStream(file
				.getBulkFileOut());
			WebResponse webResponse = (WebResponse) getResponse();
			webResponse.setAttachmentHeader(file.getFileName());
			Streams.copy(attStream, webResponse.getOutputStream());

		    } catch (Exception e) {
			LOG.error("# Error in download attachment ", e);
			error(getLocalizer().getString(
				"attachment.download.error", basePage));
		    }
		}

		@Override
		public void detach(RequestCycle arg0) {
		    // nothing to do here
		}
	    });
	} catch (Exception e) {
	    LOG.error("# Error in download attachment ", e);
	    error(getLocalizer().getString("attachment.download.error", this));
	}

    }

    public String getFileType() {
	return fileType;
    }

    public void setFileType(int fileType) {
	this.fileType = basePage.getDisplayValue(String.valueOf(fileType),
		"bulkFileType");
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(long status) {
	this.status = basePage.getDisplayValue(String.valueOf(status),
		"bulkStatus");
    }

}
