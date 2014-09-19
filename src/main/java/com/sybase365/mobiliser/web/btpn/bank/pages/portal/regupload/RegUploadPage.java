package com.sybase365.mobiliser.web.btpn.bank.pages.portal.regupload;

import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.bulkfileprocessing.BulkFileProcessRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.bulkfileprocessing.BulkFileProcessResponse;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload.SalaryUploadPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Page for uploading bulk registration data using a CSV file. It throws an error if other than CSV file is
 * uploaded.
 * 
 * @author Vikram Gunda
 */
public class RegUploadPage extends SalaryUploadPage {

	
	private static final Logger LOG = LoggerFactory.getLogger(RegUploadPage.class);
	
	/**
	 * Constructor for this page.
	 */
	public RegUploadPage() {
		super(true);
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
	}

	/**
	 * This method performs the validations for File upload.
	 * 
	 * @param fileUploadField File Upload field for File Upload
	 */
	@Override
	protected boolean handleFileUploadValidations(final FileUploadField fileUploadField) {
		if (!fileUpload.getContentType().equals(BtpnConstants.CSV_CONTENT_TYPE)) {
			fileUploadField.error(getLocalizer().getString("fileUpload.extension.csv", this));
			return true;
		}
		return false;
	}

	/**
	 * This method uploads the file to database and does the registration.
	 * 
	 * @param fileUploadField File Upload field for File Upload
	 */
	protected void handleFileUpload(final FileUploadField fileUploadField) {
		try {
			// Approve Fee Bean Fees Request
			final BulkFileProcessRequest request = this.getNewMobiliserRequest(BulkFileProcessRequest.class);
			request.setMakerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setFile(ConverterUtils.convertToFile(fileUpload, BtpnConstants.FILE_TYPE_REG_UPLOAD));
			final BulkFileProcessResponse response = this.getBulkFileProcesssingClient().bulkFileProcess(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				info(getLocalizer().getString("fileUpload.success", this));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while RegUploadPage ===> ", e);
		}
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("fileUpload.error", this);
		}
		error(message);
	}
}
