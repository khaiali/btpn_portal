package com.sybase365.mobiliser.web.btpn.bank.common.panels;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.DateValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration.AgentConfirmRegistrationPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentPortalHomePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.NotificationAttachmentsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.AttachmentsDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.ConsumerTopAgentConfirmRegistrationPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnOrderByOrder;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;


public class RegistrationPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(RegistrationPanel.class);

	private FeedbackPanel feedBack;
	protected BtpnMobiliserBasePage basePage;
	protected CustomerRegistrationBean customer;
	private WebMarkupContainer attachmentsContainer;

	private FileUploadField fileUploadField;
	private FileUpload fileUpload;

	private AttachmentsDataProvider attachmentsDataProvider;
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_LINK = "deleteLink";
	private static final String WICKET_ID_LINK_NAME = "linkName";
	private static final String WICKET_ID_ATTACHMENTNAVIGATOR = "attachmentNavigator";
	private static final String WICKET_ID_ATTACHMENTTOTALITEMS = "attachmentHeader";

	private String attachmentTotalItemString;
	private int attachmentStartIndex = 0;
	private int attachmentEndIndex = 0;
	private Label attachmentHeader;

	private BtpnCustomPagingNavigator navigator;
	private RequiredTextField<String> idCardNo;
	private BtpnLocalizableLookupDropDownChoice<CodeValue> idCardType;
	private Form<RegistrationPanel> form;
	private RequiredTextField<String> territoryCode;

	/**
	 * Constructor for this page.
	 * 
	 * @param id id Of the Panel
	 * @param basePage basePage of the Panel
	 * @param customer customer Object
	 */
	public RegistrationPanel(final String id, final BtpnMobiliserBasePage basePage, final CustomerRegistrationBean customer) {
		super(id);
		
		this.basePage = basePage;
		this.customer = customer;
		
		// Adds the header contributor
		addDateHeaderContributor();
		
		// Adds the panel
		constructPanel();
	}

	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.basePage.getLocalizer().getString("datepicker.chooseDate", basePage);
		final String locale = this.basePage.getLocale().getLanguage();
		
		add(new HeaderContributor(new DateHeaderContributor(locale, BtpnConstants.DATE_FORMAT_PATTERN_PICKER, chooseDtTxt)));
	}
	
	protected BtpnLocalizableLookupDropDownChoice<CodeValue> newCodeValueDropDown(String id, String lookupName, boolean required, IChoiceRenderer<CodeValue> renderer) {
		BtpnLocalizableLookupDropDownChoice<CodeValue> c =
				new BtpnLocalizableLookupDropDownChoice<CodeValue>(id, CodeValue.class, lookupName, this, Boolean.FALSE, true);
		
		c.setNullValid(!required);
		c.setChoiceRenderer(renderer);
		c.setRequired(required);
		c.add(new ErrorIndicator());
		
		return c;
	}

	/**
	 * Constructs the required components for the panel
	 */
	protected void constructPanel() {
		
		final String customerTypeValue = customer.getCustomerType();
		final boolean agentPortal = BtpnConstants.REG_CHILD_AGENT.equals(customerTypeValue) || BtpnConstants.REG_SUB_AGENT.equals(customerTypeValue);
		
		form = new Form<RegistrationPanel>("consumerRegistrationForm", new CompoundPropertyModel<RegistrationPanel>(this)) {
			private static final long serialVersionUID = 1L;
			@Override
			public void onError() {
				if (!PortalUtils.exists(customer.getAttachmentsList())) {
					fileUploadField.error(RegistrationPanel.this.getLocalizer().getString("file.attachmentFile.Required", RegistrationPanel.this));
				}
			}
		};
		
		// Add feedback panel for Error Messages
		form.add((feedBack = new FeedbackPanel("errorMessages"))
				.setOutputMarkupId(true));

		form.add(new Label("header.registrationType", getLocalizer().getString(customerTypeValue + ".header.registrationType", this)));
		
		final BtpnCustomer btpnCustomer = this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();

		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		
		// Add the Product Type Dropdown choice
		form.add(newCodeValueDropDown("customer.productType", BtpnUtils.fetchProductType(customerTypeValue), true, choiceRenderer));
		
		// Add the Customer Info Head Line
		form.add(new Label("customerInfo.headLine", getLocalizer().getString(customerTypeValue + ".customerInfo.headLine", this)));
		
		// Add the Customer Name
		form.add(new RequiredTextField<String>("customer.name")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_DISPLAY_NAME_REGEX))
				.add(BtpnConstants.REGISTRATION_DISPLAY_NAME_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.shortName")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_SHORT_NAME_REGEX))
				.add(BtpnConstants.REGISTRATION_SHORT_NAME_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new TextField<String>("customer.employeeId")
				.setRequired(false)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_EMPLOYEE_ID_REGEX))
				.add(BtpnConstants.REGISTRATION_EMPLOYEE_ID_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new RequiredTextField<String>("customer.mothersMaidenName")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_MOTHER_MAIDEN_NAME_REGEX))
				.add(BtpnConstants.REGISTRATION_MOTHER_MAIDEN_NAME_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.maritalStatus", BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS, true, choiceRenderer));

		form.add(new RequiredTextField<String>("customer.placeOfBirth")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_PLACE_OF_BIRTH_REGEX))
				.add(BtpnConstants.REGISTRATION_PLACE_OF_BIRTH_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.nationality", BtpnConstants.RESOURCE_BUBDLE_NATIONALITY, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.language", BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.receiptMode", BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.occupation", BtpnConstants.RESOURCE_BUNDLE_OCCUPATION, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.job", BtpnConstants.RESOURCE_BUBDLE_JOB, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.industryOfEmployee", BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.purposeOfAccount", BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.sourceofFound", BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.income", BtpnConstants.RESOURCE_BUBDLE_INCOME, true, choiceRenderer));

		form.add((idCardNo = new RequiredTextField<String>("customer.idCardNo"))
				.add(BtpnConstants.REGISTRATION_ID_CARD_NUMBER_LENGTH)
				.add(new ErrorIndicator())
				.setOutputMarkupId(true));

		form.add((idCardType = newCodeValueDropDown("customer.idType", BtpnConstants.RESOURCE_BUBDLE_ID_TYPE, true, choiceRenderer))
				.setOutputMarkupId(true));

		form.add(DateTextField.forDatePattern("customer.expireDateString", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.add(DateValidator.minimum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN))
				.setRequired(true)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.highRiskCustomer", BtpnConstants.RESOURCE_BUBDLE_HIGH_RISK_CUSTOMER, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.highRiskBusiness", BtpnConstants.RESOURCE_BUNDLE_HIGH_RISK_BUSINESS, true, choiceRenderer));

		form.add(new TextField<String>("customer.atmCardNo")
				.setRequired(false)
				.add(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_VALIDATOR)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_REGEX))
				.add(BtpnConstants.REGISTRATION_ATM_CARD_NUMBER_LENGTH)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.gender", BtpnConstants.RESOURCE_BUNDLE_GENDERS, true, choiceRenderer));

		form.add(new RequiredTextField<String>("customer.emailId")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.EMAIL_ID_REGEX))
				.add(BtpnConstants.EMAIL_ID_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new WebMarkupContainer("customerNumberInfo")
				.add(new RequiredTextField<String>("customer.customerNumber")
						.setEnabled(false))
				.setVisible(!agentPortal));
		
		form.add(createPurposeOfTransactionContainer());

		form.add(DateTextField.forDatePattern("customer.birthDateString", BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.add(DateValidator.maximum(new Date(), BtpnConstants.ID_EXPIRY_DATE_PATTERN))
				.setRequired(true)
				.add(new ErrorIndicator()));

		form.add(newCodeValueDropDown("customer.taxExempted", BtpnConstants.RESOURCE_BUBDLE_TAX_EXEMPTED, true, choiceRenderer));
		form.add(newCodeValueDropDown("customer.optimaActivated", BtpnConstants.RESOURCE_BUNDLE_IS_OPTIMA_ACTIVATED, true, choiceRenderer)
				.setVisible(customerTypeValue.equals(BtpnConstants.REG_CONSUMER)));

		form.add(newCodeValueDropDown("customer.religion", BtpnConstants.RESOURCE_BUNDLE_RELIGION, false, choiceRenderer));
		form.add(newCodeValueDropDown("customer.lastEducation", BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, false, choiceRenderer));
		form.add(newCodeValueDropDown("customer.foreCastTransaction", BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS, false, choiceRenderer));

		form.add(new TextField<String>("customer.taxCardNumber")
				.add(new PatternValidator("^[0-9]*$"))
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));


		// Not visible for consumers
		form.add(new RequiredTextField<String>(
				"customer.optimaActivated.text",
				new PropertyModel<String>(this, "customer.optimaActivated.id"))
				.setEnabled(false)
				.setVisible(!customerTypeValue.equals(BtpnConstants.REG_CONSUMER)));

		// Add the file upload fields in a form
		final Form<RegistrationPanel> fileUploadForm = new Form<RegistrationPanel>("fileuploadForm");
		
		fileUploadForm.add((fileUploadField = new FileUploadField("fileUpload"))
				.setOutputMarkupId(true)
				.add(new ErrorIndicator()));
		fileUploadForm.add(new FileUploadAjaxButton("uploadButton"));
		fileUploadForm.setMultiPart(true);
		
		form.add(fileUploadForm);
		
		idCardType.add(new AjaxFormComponentUpdatingBehavior("onblur") {

			private static final long serialVersionUID = 1L;

			protected void onUpdate(AjaxRequestTarget target) {
				// Nothing to do.
			}
		});
		idCardType.setOutputMarkupId(true);
		
		idCardNo.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			protected void onUpdate(AjaxRequestTarget target) {
				// Nothing to do.
			}
		});
		idCardNo.setOutputMarkupId(true);
		
		// Add the File attachments container
		attachmentsContainer = new WebMarkupContainer("attachmentsContainer");
		attachmentsContainer.setMarkupId("attachmentId");
		notificationAttachmentsDataView(attachmentsContainer);
		attachmentsContainer.setOutputMarkupId(true);
		
		form.add(attachmentsContainer);

		// Contact And Information.
		form.add(new Label("contactAddressInfo.headLine", getLocalizer().getString(customerTypeValue + ".contactAddressInfo.headLine", this)));

		form.add(new TextField<String>("customer.mobileNumber")
				.setRequired(true)
				.setEnabled(false));

		form.add(new RequiredTextField<String>("customer.street1")
				.setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_STREET1_REGEX))
				.add(BtpnConstants.REGISTRATION_STREET1_MAX_LENGTH)
				.add(new ErrorIndicator()));

		form.add(new TextField<String>("customer.street2")
				.setRequired(false)
				.add(new PatternValidator(BtpnConstants.REGISTRATION_STREET1_REGEX))
				.add(BtpnConstants.REGISTRATION_STREET1_MAX_LENGTH)
				.add(new ErrorIndicator()));

		final BtpnLocalizableLookupDropDownChoice<CodeValue> cityDropdown =
				newCodeValueDropDown("customer.city", BtpnConstants.RESOURCE_BUNDLE_CITIES, true, choiceRenderer);
		
		cityDropdown.setLookupName(customer.getProvince() != null ? customer.getProvince().getId() : null);
		cityDropdown.setOutputMarkupId(true);

		form.add(cityDropdown);
		
		
		form.add(newCodeValueDropDown("customer.province", BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, true, choiceRenderer)
				.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					
					private static final long serialVersionUID = 1L;

					protected void onUpdate(AjaxRequestTarget target) {
						cityDropdown.setLookupName(customer.getProvince().getId());
						target.addComponent(cityDropdown);
					}
				}));
		
		form.add(new TextField<String>("customer.zipCode")
				.add(new SimpleAttributeModifier("maxlength", "6"))
				.add(new ErrorIndicator()));
		

		form.add(new RequiredTextField<String>("officerId", Model.of(btpnCustomer.getUsername()))
				.setEnabled(false));

		final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

		form.add(new RequiredTextField<String>("datecreated", Model.of(sdf.format(new Date())))
				.setEnabled(false));

		String defaultTerritoryCode = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getTerritoryCode();
		
		if (agentPortal || (defaultTerritoryCode != null)) {
			customer.setTerritoryCode(defaultTerritoryCode);
			
			form.add((territoryCode = new RequiredTextField<String>("customer.territoryCode", Model.of(defaultTerritoryCode)))
					.add(new ErrorIndicator())
					.add(new SimpleAttributeModifier("maxlength", "20"))
					.setEnabled(false));
		} else {
			if (customer.getTerritoryCode() == null) {
				customer.setTerritoryCode(defaultTerritoryCode);
			}
			
			form.add((territoryCode = new RequiredTextField<String>("customer.territoryCode"))
					.add(new ErrorIndicator())
					.add(new SimpleAttributeModifier("maxlength", "20"))
					.setEnabled(true));
		}

		form.add(new RequiredTextField<String>("customer.marketingSourceCode")
				.setRequired(true)
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));
		
		form.add(new TextField<String>("customer.referralNumber")
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));
		
		form.add(new RequiredTextField<String>("customer.agentCode")
				.setRequired(true)
				.add(new ErrorIndicator())
				.add(new SimpleAttributeModifier("maxlength", "30")));

		Button button = addSubmitButton();
		form.add(new SimpleAttributeModifier("onkeydown", "keyEnter(" + button.getMarkupId(true) + ")"));

		form.add(button);
		form.add(addCancelButton(agentPortal));

		add(form);
	}


	/**
	 * This method adds the Purpose of Transaction Container only for all Agent registrations
	 */
	private WebMarkupContainer createPurposeOfTransactionContainer() {
		
		WebMarkupContainer purpOfTrans = new WebMarkupContainer("purposeOfTransactionBlock");
		
		purpOfTrans.add(new RequiredTextField<String>("customer.purposeOfTransaction")
				.add(BtpnConstants.REGISTRATION_PURPOSE_OF_TRANSACTION_LENGTH)
				.add(new ErrorIndicator()));
		
		purpOfTrans.setVisible(!customer.getCustomerType().equals(BtpnConstants.REG_CONSUMER));
		
		return purpOfTrans;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void notificationAttachmentsDataView(final WebMarkupContainer dataViewContainer) {
		// Create the Attachment View
		attachmentsDataProvider = new AttachmentsDataProvider("fileName", customer.getAttachmentsList());

		final DataView<NotificationAttachmentsBean> dataView = new AttachmentsDataView(WICKET_ID_PAGEABLE,
			attachmentsDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_ATTACHMENTNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return customer.getAttachmentsList().size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;
			public String getObject() {
				return String.format(
						RegistrationPanel.this.getLocalizer().getString("attachment.totalitems.header", RegistrationPanel.this),
						attachmentTotalItemString,
						Integer.valueOf(attachmentStartIndex),
						Integer.valueOf(attachmentEndIndex));
			}
		};
		
		attachmentHeader = new Label(WICKET_ID_ATTACHMENTTOTALITEMS, headerDisplayModel) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isVisible() {
				return customer.getAttachmentsList().size() != 0;
			}
		};
		
		dataViewContainer.add(attachmentHeader);
		attachmentHeader.setOutputMarkupId(true);
		attachmentHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new BtpnOrderByOrder("orderByFileName", "fileName", attachmentsDataProvider, dataView));
		dataViewContainer.add(new BtpnOrderByOrder("orderByUploadedDate", "uploadedDate", attachmentsDataProvider, dataView));

		dataViewContainer.addOrReplace(dataView);
	}

	
	/**
	 * This method adds the submit button for the Registration Panel
	 */
	protected Button addSubmitButton() {
		
		Button submitButton = new Button("submitButton") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				final String customerTypeValue = customer.getCustomerType();
				final String defaultTC = RegistrationPanel.this.basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getTerritoryCode();
				
				if (!PortalUtils.exists(customer.getAttachmentsList())) {
					fileUploadField.error(RegistrationPanel.this.getLocalizer().getString("file.attachmentFile.Required", RegistrationPanel.this));
					return;
				}

				if (customerTypeValue.equals(BtpnConstants.REG_CONSUMER) || customerTypeValue.equals(BtpnConstants.REG_TOPUP_AGENT)) {
					if (customer.getTerritoryCode() == null) {
						if (defaultTC == null) {
							territoryCode.error(RegistrationPanel.this.getLocalizer().getString("territoryCode.Required", RegistrationPanel.this));
							return;
						}
						customer.setTerritoryCode(defaultTC);
					}
					
					setResponsePage(new ConsumerTopAgentConfirmRegistrationPage(customer));
				
				} else {
					if (defaultTC == null) {
						territoryCode.error(RegistrationPanel.this.getLocalizer().getString("territoryCode.Required", RegistrationPanel.this));
						return;
					}
					
					customer.setTerritoryCode(defaultTC);
					
					setResponsePage(new AgentConfirmRegistrationPage(customer));
				}
			}
		};
		
		return submitButton;
	}

	/**
	 * This method adds the cancel button for the Registration Panel
	 */
	protected Button addCancelButton(final boolean agentPortal) {
		
		Button cancelButton = new Button("cancelButton") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(agentPortal == true ? AgentPortalHomePage.class : BankPortalHomePage.class);
			};
		};
		
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This is the FileUploadAjaxButton for registering Bank consumer.
	 * 
	 * @author Vikram Gunda
	 */
	private class FileUploadAjaxButton extends AjaxButton {

		private static final long serialVersionUID = 1L;

		public FileUploadAjaxButton(String id) {
			super(id);
		}

		protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
			
			try {
				
				boolean errorExists = false;

				// Get the file upload data
				fileUpload = fileUploadField.getFileUpload();

				// Check for file exists or not
				if (!PortalUtils.exists(fileUpload)) {
					fileUploadField.error(RegistrationPanel.this.getLocalizer().getString("file.attachmentFile.Required", RegistrationPanel.this));
					
					target.addComponent(feedBack);
					target.addComponent(fileUploadField);
					
					errorExists = true;
				}
				
				if (PortalUtils.exists(fileUpload) && (fileUpload.getSize() > BtpnConstants.REGISTRATION_FILE_MAX_SIZE.longValue())) {
					fileUploadField.error(RegistrationPanel.this.getLocalizer().getString("attachment.size.error", RegistrationPanel.this));
					
					errorExists = true;
				}
				
				target.addComponent(idCardType);
				target.addComponent(idCardNo);
				target.addComponent(feedBack);
				target.addComponent(fileUploadField);
				
				if (errorExists) {
					return;
				}

				// Get the uploaded file name and Date
				final String uploadedFileName = fileUpload.getClientFileName();
				final String contentType = fileUpload.getContentType();
				final Date date = new Date();
				
				LOG.info("Uploaded File Name : " + uploadedFileName);
				LOG.info("File Uploaded Date : " + date);

				// Add the Notification Attachment view for listing the file
				// uploads
				final NotificationAttachmentsBean attachmentsBean = new NotificationAttachmentsBean();
				attachmentsBean.setFileName(uploadedFileName);
				attachmentsBean.setUploadedDate(date);
				attachmentsBean.setContentType(contentType);
				attachmentsBean.setFileContent(fileUpload.getBytes());
				
				customer.addAttachmentBean(attachmentsBean);

				// Add the feedback panel and container to render the view again
				target.addComponent(attachmentsContainer);
				target.addComponent(fileUploadField);
				target.addComponent(navigator);
				target.addComponent(attachmentHeader);
			
			} catch (Throwable ex) {
				LOG.error("An error occurred while uploading file", ex);
				fileUploadField.error(RegistrationPanel.this.getLocalizer().getString("file.upload.error", RegistrationPanel.this));
				target.addComponent(feedBack);
				target.addComponent(fileUploadField);
			}
		}
	}

	/**
	 * This is the AttachmentsDataView for registering Bank consumer.
	 * 
	 * @author Vikram Gunda
	 */
	private class AttachmentsDataView extends DataView<NotificationAttachmentsBean> {

		private static final long serialVersionUID = 1L;

		protected AttachmentsDataView(String id, IDataProvider<NotificationAttachmentsBean> dataProvider) {
			super(id, dataProvider);
			
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
		}

		@Override
		protected void onBeforeRender() {
			final AttachmentsDataProvider dataProvider = (AttachmentsDataProvider) internalGetDataProvider();
			dataProvider.setAttachmentsList(customer.getAttachmentsList());
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		protected void populateItem(final Item<NotificationAttachmentsBean> item) {
			
			final NotificationAttachmentsBean entry = item.getModelObject();

			// Add the File name
			item.add(new Label("fileType", entry.getFileName()));
			
			// Add the uploaded date
			final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			
			item.add(new Label("uploadedDate", dateFormat.format(entry.getUploadedDate())));
			
			// Add the delete Link
			final AjaxLink<NotificationAttachmentsBean> deleteLink = new AjaxLink<NotificationAttachmentsBean>(
				WICKET_ID_LINK, item.getModel()) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final NotificationAttachmentsBean attachmentsBean = (NotificationAttachmentsBean) item.getModelObject();
					customer.getAttachmentsList().remove(attachmentsBean);
					
					target.addComponent(attachmentsContainer);
					target.addComponent(navigator);
					target.addComponent(attachmentHeader);
				}
			};
			
			deleteLink.add(new Label(WICKET_ID_LINK_NAME, "Delete"));
			item.add(deleteLink);
			
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return customer.getAttachmentsList().size() != 0;
		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			
			attachmentTotalItemString = new Integer(size).toString();
			
			if (size > 0) {
				attachmentStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				attachmentEndIndex = attachmentStartIndex + getItemsPerPage() - 1;
				
				if (attachmentEndIndex > size) {
					attachmentEndIndex = size;
				}
			} else {
				attachmentStartIndex = 0;
				attachmentEndIndex = 0;
			}
		}
	}
}
