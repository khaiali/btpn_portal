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

import com.sybase365.mobiliser.money.contract.v5_0.system.ContinuePendingBulkProcessingFileRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.ContinuePendingBulkProcessingFileResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetPendingBulkProcessingFileDetailsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetPendingBulkProcessingFileDetailsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFile;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ConfirmBulkFileDetailsPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
	    .getLogger(ConfirmBulkFileDetailsPanel.class);
    private MobiliserBasePage basePage;
    private BulkFile bulkFile;
    private String date;
    private String fileType;

    public ConfirmBulkFileDetailsPanel(String id, MobiliserBasePage basePage,
	    BulkFile bulkFile) {
	super(id);
	this.basePage = basePage;
	this.bulkFile = bulkFile;
	setDate(bulkFile.getUploadDate());
	setFileType(bulkFile.getBulkFileType());
	constructPanel();
    }

    private void constructPanel() {
	Form<?> form = new Form("confirmDetailsForm",
		new CompoundPropertyModel<ConfirmBulkFileDetailsPanel>(this));
	form.add(new FeedbackPanel("errorMessages"));

	form.add(new TextField<String>("bulkFile.bulkFileName")
		.setEnabled(false)
		.add(new SimpleAttributeModifier("readonly", "readonly"))
		.add(new SimpleAttributeModifier("style",
			"background-color: #E6E6E6;")));
	form.add(new TextField<String>("fileType")
		.setEnabled(false)
		.add(new SimpleAttributeModifier("readonly", "readonly"))
		.add(new SimpleAttributeModifier("style",
			"background-color: #E6E6E6;")));
	form.add(new TextField<String>("date")
		.setEnabled(false)
		.add(new SimpleAttributeModifier("readonly", "readonly"))
		.add(new SimpleAttributeModifier("style",
			"background-color: #E6E6E6;")));

	form.add(new Button("approve") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleApprove(Boolean.valueOf(true));
	    }
	});
	form.add(new Button("reject") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleApprove(Boolean.valueOf(false));
	    }
	});
	form.add(new Button("bulkFileDownolad") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleDownload();
	    }

	});
	add(form);

    }

    private void handleDownload() {
	LOG.debug("# ConfirmBulkFileDetailsPanel.handleDownload()");
	try {
	    GetPendingBulkProcessingFileDetailsRequest request = basePage
		    .getNewMobiliserRequest(GetPendingBulkProcessingFileDetailsRequest.class);
	    request.setTaskId(bulkFile.getTaskId());
	    GetPendingBulkProcessingFileDetailsResponse response = basePage.wsBulkProcessingClient
		    .getPendingBulkProcessingFileDetails(request);
	    downloadAttachment(response);
	} catch (Exception e) {
	    LOG.error("# Error in download attachment ", e);
	    error(getLocalizer().getString("bulkFile.download.error", this));
	}

    }

    private void handleApprove(Boolean isApprove) {
	LOG.debug("# ConfirmBulkFileDetailsPanel.handleApprove()");
	try {
	    ContinuePendingBulkProcessingFileRequest request = basePage
		    .getNewMobiliserRequest(ContinuePendingBulkProcessingFileRequest.class);
	    request.setTaskId(bulkFile.getTaskId());
	    request.setApprove(isApprove);
	    ContinuePendingBulkProcessingFileResponse response = basePage.wsBulkProcessingClient
		    .continueBulkProcessingFile(request);
	    if (basePage.evaluateMobiliserResponse(response)) {
		info(getLocalizer().getString("bulkFile.approve.success", this));
	    }
	} catch (Exception e) {
	    LOG.error(
		    "An error occurred while approving the pending bulk file",
		    e);
	    error(getLocalizer().getString("bulkFile.approve.error", this));
	}

    }

    public String getDate() {
	return date;
    }

    public void setDate(XMLGregorianCalendar date) {
	this.date = PortalUtils.getFormattedDate(date, Locale.getDefault());
    }

    private void downloadAttachment(
	    final GetPendingBulkProcessingFileDetailsResponse file) {
	LOG.info("# downloadAttachment");
	try {
	    getRequestCycle().setRequestTarget(new IRequestTarget() {

		public void respond(RequestCycle requestCycle) {
		    try {
			InputStream attStream = new ByteArrayInputStream(file
				.getBulkFile());
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

}
