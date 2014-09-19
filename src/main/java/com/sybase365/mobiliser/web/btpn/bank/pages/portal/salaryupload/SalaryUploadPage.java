package com.sybase365.mobiliser.web.btpn.bank.pages.portal.salaryupload;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.bulk.facade.contract.UploadBulkFileWrkRequest;
import com.btpnwow.core.bulk.facade.contract.UploadBulkFileWrkResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.SearchSalaryDataBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Salary upload for Bank Portals.
 * 
 * @author Vikram Gunda
 */
public class SalaryUploadPage extends BtpnBaseBankPortalSelfCarePage {
	
	private static final Pattern USE_CASE_REGEX = Pattern.compile("^[0-9]{1,4}$");
	
	private static final Pattern AMOUNT_REGEX = Pattern.compile("^[0-9]{1,99}$");
	private Component typeComp;
	private SearchSalaryDataBean searchBean;
	
	private static final Set<String> USE_CASE_WHITELIST = new HashSet<String>(Arrays.asList(
		"212", "213", "214", "215",
		"0212", "0213", "0214", "0215",
		"225",
		"0225",
		"232", "233", "234", "242",
		"0232", "0233", "0234", "0242"
	));

	/**
	 * File upload page
	 */
	protected FileUpload fileUpload;

	private static final Logger LOG = LoggerFactory.getLogger(SalaryUploadPage.class);

	/**
	 * Constructor for this page.
	 */
	public SalaryUploadPage() {
		super();
		initThisPageComponents(false);
	}

	/**
	 * Constructor for this page.
	 */
	public SalaryUploadPage(boolean isRegUpload) {
		super();
		initThisPageComponents(isRegUpload);
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
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 * 
	 * @param isRegUpload true for registration upload, false for salary upload
	 */
	protected void initThisPageComponents(final boolean isRegUpload) {
		final FileUploadField fileUploadField = new FileUploadField("fileUpload");
		fileUploadField.setRequired(true).add(new ErrorIndicator());
		
		final Form<SalaryUploadPage> fileUploadForm = new Form<SalaryUploadPage>("fileuploadForm", new CompoundPropertyModel<SalaryUploadPage>(this));
		fileUploadForm.add(new FeedbackPanel("errorMessages"));
		fileUploadForm.add(fileUploadField);
		
		//bulkFileType
		final IChoiceRenderer<CodeValue> fileType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		fileUploadForm.add(typeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("searchBean.type",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_BULK_FILE_PROCESSING_TYPE, this, true, false)
				.setChoiceRenderer(fileType).setRequired(true).add(new ErrorIndicator()));
			typeComp.setOutputMarkupId(true);
		
		fileUploadForm.add(new Button("btnUpload") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				fileUpload = fileUploadField.getFileUpload();
				if (!PortalUtils.exists(searchBean)){
					searchBean = new SearchSalaryDataBean();
				}
			
				/*if (handleFileUploadValidations(fileUploadField)) {
					return;
				}*/
				handleFileUpload();
			}

		});
		
		fileUploadForm.setMultiPart(true);
		
		add(fileUploadForm);
	}
	
	/**
	 * This method performs the validations for File upload.
	 * 
	 * @param fileUploadField File Upload field for File Upload
	 */
	protected boolean handleFileUploadValidations(final FileUploadField fileUploadField) {
		if (!fileUpload.getContentType().equals(BtpnConstants.CSV_CONTENT_TYPE)) {
			fileUploadField.error(getLocalizer().getString("fileUpload.extension.csv", this));
			return true;
		}
		
		if (!validateFileUploadFormat(fileUploadField)) {
			return true;
		}
		
		return false;
	}
	
	protected String validateFileUploadLineFormat(final String line) {
		// check empty line
		if (line.isEmpty()) {
			return "line is empty";
		}
		
		final String[] tokens = line.split(",");
		
		// check for tokens count
		final int ntokens = tokens.length;
		if (ntokens != 7) {
			return "columns length is not 7";
		}
		
		// check for empty token
		for (int i = 0, n = tokens.length; i < n; ++i) {
			final String token = tokens[i];
			
			if (token == null) {
				return "column #".concat(String.valueOf(i + 1)).concat(" is empty");
			}
		}
		
		// check use case
		String usecase = tokens[0];
		if ((usecase.length() > 4) || !USE_CASE_REGEX.matcher(usecase.trim()).matches()) {
			return "column #1 (use case) must be 1-4 digits length";
		}
		
		if (!USE_CASE_WHITELIST.contains(usecase.trim())) {
			return "column #1 (use case) is not supported";
		}
		
		// check debit account type 1
		tokens[1] = tokens[1].trim();
		
		if (!"S".equalsIgnoreCase(tokens[1]) && !"G".equalsIgnoreCase(tokens[1])) {
			return "column #2 (GL or SVA Debit/Credit Account Type) must be 'S' or 'G'";
		}
		
		// check debit account number 1
		if (tokens[2].trim().length() > 20) {
			return "column #3 (GL or SVA Debit/Credit Account Number) length must not exceed 20 digits length";
		}
		
		// check debit account type 2
		tokens[3] = tokens[3].trim();
		
		if (!"S".equalsIgnoreCase(tokens[3]) && !"G".equalsIgnoreCase(tokens[3])) {
			return "column #4 (GL or SVA Debit/Credit Account Type) must be 'S' or 'G'";
		}
		
		// check debit account number 4
		if (tokens[4].trim().length() > 20) {
			return "column #5 (GL or SVA Debit/Credit Account Number) length must not exceed 20 digits length";
		}
		
		// check amount value
		String amount = tokens[5];
		
		if (amount.length() > 12) {
			return "column #6 (amount) must not exceed 12 digits length";
		}
		
		amount = amount.trim();
		
		if (amount.contains(".")) {
			return "column #6 (amount) must not contain any decimal point";
		}
		if (!AMOUNT_REGEX.matcher(amount).matches()) {
			return "column #6 (amount) must not contain any non number character";
		}
		if (Long.parseLong(amount) <= 0) {
			return "column #6 (amount) must greater than zero";
		}
		
		// check remarks
		String remarks = tokens[6].trim();
		if (remarks.length() > 100) {
			return "column #7 (remarks) must not exceed 100 characters length";
		}
		
		return null;
	}
	
	protected boolean validateFileUploadFormat(final FileUploadField field) {
		final FileUpload f = field.getFileUpload();
		
		ByteArrayInputStream in = null;
		BufferedReader reader = null;
		
		int lineNo = 1;
		String error = null;
		
		try {
			in = new ByteArrayInputStream(f.getBytes());
			reader = new BufferedReader(new InputStreamReader(in));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				if ((line.trim().length() == 0) && (reader.read() == -1)) {
					break; // END OF LINE
				}
				
				if ((error = validateFileUploadLineFormat(line)) != null) {
					break;
				}
				
				++lineNo;
			}
		} catch (IOException e) {
			error = "IO Error: " + e.getMessage();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable t) {
					// do nothing
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
					// do nothing
				}
			}
		}
		
		if (error == null) {
			return true;
		}
		
		Map<String, Object> vm = new HashMap<String, Object>();
		vm.put("lineNo", String.valueOf(lineNo));
		vm.put("msg", error);
		
		field.error(getLocalizer().getString("fileUpload.validation.failed", this, new PropertyModel<String>(vm, null), null));
		return false;
	}
	
	
	/**
	 * This method uploads the file to database and does the registration.
	 * 
	 * @param fileUploadField File Upload field for File Upload
	 */
	protected void handleFileUpload() {
		try {
			// Approve Fee Bean Fees Request
			final UploadBulkFileWrkRequest request = this.getNewMobiliserRequest(UploadBulkFileWrkRequest.class);
			
			LOG.info("FileName:"+fileUpload.getClientFileName());
			request.setName(fileUpload.getClientFileName());
			request.setRequest(fileUpload.getBytes());
			request.setType(Integer.parseInt(searchBean.getType().getId()) );
			request.setCallerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			
			final UploadBulkFileWrkResponse response = this.getBulkFileWrkClient().uploadWrk(request);
			LOG.info("status responsecode : "+response.getStatus());
			if (evaluateBankPortalMobiliserResponse(response)) {
				info(getLocalizer().getString("fileUpload.success", this));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while SalaryUploadPage  ===> ", e);
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
