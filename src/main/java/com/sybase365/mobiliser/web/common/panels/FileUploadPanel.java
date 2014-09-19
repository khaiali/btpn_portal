package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.money.contract.v5_0.system.GetBulkFileTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetBulkFileTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UploadBulkProcessingFileRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UploadBulkProcessingFileResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.BulkFileType;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class FileUploadPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(FileUploadPanel.class);
    private MobiliserBasePage basePage;
    private Object content;
    private FileUploadField upload;
    private int bulkFileTypeId;

    public FileUploadPanel(String id, MobiliserBasePage basePage) {
	super(id);
	this.basePage = basePage;
	this.constructPanel();
    }

    private void constructPanel() {
	Form<?> uploadForm = new Form("uploadForm",
		new CompoundPropertyModel<FileUploadPanel>(this));
	uploadForm.add(new FeedbackPanel("errorMessages"));

	final KeyValueDropDownChoice<Integer, String> formatDropDown = new KeyValueDropDownChoice<Integer, String>(
		"bulkFileTypeId", getMimeTypeList());
	formatDropDown.setNullValid(true).setRequired(true).setEnabled(true);
	uploadForm.add(formatDropDown);
	uploadForm.add(upload = new FileUploadField("content"));

	uploadForm.add(new Button("Upload") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleSubmit();
	    }

	});

	add(uploadForm);
    }

    private void handleSubmit() {
	try {

	    if (!PortalUtils.exists(upload.getFileUpload())) {
		error(getLocalizer().getString("file.attachmentFile.required",
			this));
		return;
	    }

	    FileUpload data = upload.getFileUpload();
	    UploadBulkProcessingFileRequest request = basePage
		    .getNewMobiliserRequest(UploadBulkProcessingFileRequest.class);
	    request.setBulkFile(data.getBytes());
	    request.setBulkFileType(bulkFileTypeId);
	    request.setFileName(data.getClientFileName());
	    request.setMimeType(data.getContentType());
	    UploadBulkProcessingFileResponse response = basePage.wsBulkProcessingClient
		    .uploadBulkProcessingFile(request);

	    if (response.getStatus().getCode() == Constants.PENDING_APPROVAL_ERROR) {
		info(getLocalizer().getString("pendingApproval.msg", this));
		LOG.info("file uploaded successfull, but is pending approval");
		return;
	    }
	    if (!basePage.evaluateMobiliserResponse(response)) {
		LOG.warn("An error occurred while uploading file");
		return;
	    } else
		info(getLocalizer().getString("upload.file.bulk", this));

	} catch (Exception e) {
	    LOG.error("An error occurred while uploading file", e);
	    error(getLocalizer()
		    .getString("bulk.processing.upload.error", this));
	}

    }

    private List<KeyValue<Integer, String>> getMimeTypeList() {
	LOG.debug("# FileUploadPanel.getMimeTypeList()");
	List<KeyValue<Integer, String>> mimeTypes = new ArrayList<KeyValue<Integer, String>>();
	try {
	    GetBulkFileTypeRequest request = basePage
		    .getNewMobiliserRequest(GetBulkFileTypeRequest.class);
	    GetBulkFileTypeResponse response = basePage.wsBulkProcessingClient
		    .getBulkFileType(request);
	    if (basePage.evaluateMobiliserResponse(response)) {
		List<BulkFileType> typeList = response.getWrkBulkFileType();
		for (BulkFileType type : typeList) {

		    if (basePage.getMobiliserWebSession().hasPrivilege(
			    Constants.PRIV_CST_LOGIN)) {
			if (Arrays.asList(Constants.CST_ALLOWED_BULKFILE_TYPES)
				.contains(type.getBulkTypeId())) {
			    mimeTypes.add(new KeyValue<Integer, String>(type
				    .getBulkTypeId(), type.getFileTypeDesc()));

			}
		    } else if (basePage.getMobiliserWebSession().hasPrivilege(
			    Constants.PRIV_MERCHANT_LOGIN)) {
			if (Arrays.asList(Constants.DPP_ALLOWED_BULKFILE_TYPES)
				.contains(type.getBulkTypeId())) {
			    mimeTypes.add(new KeyValue<Integer, String>(type
				    .getBulkTypeId(), type.getFileTypeDesc()));

			}
		    }

		}
	    }
	} catch (Exception e) {
	    LOG.error("An error occurred in getting the mime type list", e);
	    error(getLocalizer().getString(
		    "bulk.processing.load.filetypes.error", this));
	}
	return mimeTypes;
    }

    public int getBulkFileTypeId() {
	return bulkFileTypeId;
    }

    public void setBulkFileTypeId(int bulkFileTypeId) {
	this.bulkFileTypeId = bulkFileTypeId;
    }

    public Object getContent() {
	return content;
    }

    public void setContent(Object content) {
	this.content = content;
    }

}
