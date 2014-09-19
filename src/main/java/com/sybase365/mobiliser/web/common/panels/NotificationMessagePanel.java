package com.sybase365.mobiliser.web.common.panels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.CreateTemplateRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.CreateTemplateResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindAttachmentsRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindAttachmentsResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.UpdateTemplateRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.UpdateTemplateResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.AttachmentSearchCriteria;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageAttachment;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageStringContent;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageTemplate;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.components.KeyValueDropDownChoice;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.MessageAttachmentDataProvider;
import com.sybase365.mobiliser.web.cst.pages.notificationmgr.EditMessagePage;
import com.sybase365.mobiliser.web.cst.pages.notificationmgr.TestMessagePage;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class NotificationMessagePanel extends Panel {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(NotificationMessagePanel.class);
    MobiliserBasePage basePage;
    MessageTemplate message;
    String localeStr;
    String smsText;
    String plainText;
    String htmlText;
    String logText;
    Long attachmentAdd;

    // Data Model for table list
    private MessageAttachmentDataProvider dataProvider;

    List<MessageAttachment> selectedAttachments = new ArrayList<MessageAttachment>();
    List<MessageAttachment> msgAttachmentList;
    List<KeyValue<Long, String>> attachmentsList;
    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;
    boolean isEmail = false;
    private boolean forceReload = true;

    private int rowIndex = 1;
    private boolean isCreateMode;
    // Attachment container is visible only for email messages in edit mode
    // if there is an attachment available to be associated with message
    // or if there is any attachment already associated with message
    private boolean isAttachmentContainerVisible = true;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByName = "orderByName";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_selected = "selected";
    private static final String WICKET_ID_name = "name";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public NotificationMessagePanel(String id, MobiliserBasePage mobBasePage,
	    MessageTemplate message) {
	super(id);
	this.basePage = mobBasePage;
	this.message = message;
	if (message == null) {
	    isAttachmentContainerVisible = false;
	    isCreateMode = true;
	} else {
	    getAttachments();
	    isCreateMode = false;
	    localeStr = basePage.convertLocale(message.getLocale());
	    if (PortalUtils.exists(message)
		    && Constants.TEMPLATE_TYPE_EMAIL_KEY.equals(message
			    .getTemplateType())) {
		isEmail = true;
		isAttachmentContainerVisible = true;
		if (PortalUtils.exists(message.getContent()))
		    setPlainText(message.getContent().getContent());
		if (PortalUtils.exists(message.getAlternativeContent()))
		    setHtmlText(message.getAlternativeContent().getContent());
	    } else {
		if (PortalUtils.exists(message.getContent()))
		    setSmsText(message.getContent().getContent());
		isEmail = false;
		isAttachmentContainerVisible = false;
	    }
	}
	constructPanel();
    }

    private void constructPanel() {
	final Form<?> form = new Form("messageForm",
		new CompoundPropertyModel<NotificationMessagePanel>(this));
	final WebMarkupContainer emailTextContainer = new WebMarkupContainer(
		"emailTextContainer");
	final WebMarkupContainer smsTextContainer = new WebMarkupContainer(
		"smsTextContainer");
	final WebMarkupContainer confidentialTextContainer = new WebMarkupContainer(
		"confidentialTextContainer");
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new RequiredTextField<String>("message.name")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()).setEnabled(isCreateMode));
	final LocalizableLookupDropDownChoice<String> templateTypeDropDown = new LocalizableLookupDropDownChoice<String>(
		"message.templateType", String.class,
		Constants.RESOURCE_BUNDLE_TEMPLATE_TYPE, basePage,
		Boolean.FALSE, true);
	templateTypeDropDown.setNullValid(false).setRequired(true)
		.setEnabled(isCreateMode);

	templateTypeDropDown.add(new AjaxFormComponentUpdatingBehavior(
		"onchange") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		String newSelection = templateTypeDropDown.getModelObject();
		if (Constants.TEMPLATE_TYPE_EMAIL_KEY.equals(newSelection)) {
		    emailTextContainer.setVisible(true);
		    smsTextContainer.setVisible(false);
		} else {
		    emailTextContainer.setVisible(false);
		    smsTextContainer.setVisible(true);
		}
		target.addComponent(emailTextContainer);
		target.addComponent(smsTextContainer);
	    }
	});
	form.add(templateTypeDropDown);
	form.add(new TextField<String>("localeStr")
		.add(new PatternValidator(Constants.REGEX_LOCALE_VARIANT))
		.add(new ErrorIndicator()).setEnabled(isCreateMode));
	form.add(new RequiredTextField<String>("message.sender")
		.add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	smsTextContainer.add(new TextArea<String>("smsText").setRequired(true)
		.add(new ErrorIndicator()));
	smsTextContainer.setOutputMarkupPlaceholderTag(true).setVisible(
		!isEmail);
	form.add(smsTextContainer);
	emailTextContainer.add(new TextField<String>("message.subject")
		.setRequired(true).add(Constants.mediumStringValidator)
		.add(Constants.mediumSimpleAttributeModifier)
		.add(new ErrorIndicator()));
	emailTextContainer.add(new TextArea<String>("plainText").setRequired(
		true).add(new ErrorIndicator()));
	emailTextContainer.add(new TextArea<String>("htmlText")
		.add(new ErrorIndicator()));
	emailTextContainer.setOutputMarkupPlaceholderTag(true).setVisible(
		isEmail);
	form.add(emailTextContainer);
	final AjaxCheckBox confidentialCheckBox = new AjaxCheckBox(
		"message.confidential") {

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		boolean selected = getModelObject();
		if (selected)
		    confidentialTextContainer.setVisible(true);
		else
		    confidentialTextContainer.setVisible(false);
		target.addComponent(confidentialTextContainer);
	    }
	};
	final TextArea confidentialTextArea = new TextArea<String>(
		"message.logText");
	form.add(confidentialCheckBox.add(new ErrorIndicator()));
	confidentialTextContainer.add(confidentialTextArea
		.add(new ErrorIndicator()));
	form.add(confidentialTextContainer
		.setOutputMarkupPlaceholderTag(true)
		.setVisible(
			(message != null && message.isConfidential() != null) ? message
				.isConfidential() : false));
	createAttachmentContainer(form);

	form.add(new Button("submit") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createMessage();
	    }
	});
	form.add(new Button("export") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		exportMessage();
	    }
	}.setDefaultFormProcessing(false).setVisible(!isCreateMode));
	form.add(new Button("test") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		sendTestMessageRequest();
	    }
	}.setDefaultFormProcessing(false).setVisible(!isCreateMode));
	add(form);

    }

    public void sendTestMessageRequest() {
	setResponsePage(new TestMessagePage(message));
    }

    private void exportMessage() {
	List<MessageTemplate> messageTemplates = new ArrayList<MessageTemplate>();
	messageTemplates.add(message);
	basePage.doMessageExport(messageTemplates);
    }

    private void createAttachmentContainer(final Form form) {
	final WebMarkupContainer attachmentContainer = new WebMarkupContainer(
		"attachmentContainer");
	attachmentContainer.setOutputMarkupPlaceholderTag(true);
	forceReload = true;
	isAttachmentContainerVisible = isAttachmentContainerVisible
		&& (message.getAttachments().size() > 0 || PortalUtils
			.exists(attachmentsList));
	createAttachmentsListDataView(attachmentContainer, form);
	final WebMarkupContainer addAttachmentContainer = new WebMarkupContainer(
		"addAttachmentContainer");
	final Form<?> addAttachmentForm = new Form("addAttachmentForm",
		new CompoundPropertyModel<NotificationMessagePanel>(this));

	addAttachmentForm.add(new KeyValueDropDownChoice<Long, String>(
		"attachmentAdd", attachmentsList).setVisible(PortalUtils
		.exists(attachmentsList)));
	addAttachmentForm.setOutputMarkupPlaceholderTag(true);
	addAttachmentForm.add(new Button("add") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		message.getAttachments().add(attachmentAdd);
		try {
		    if (updateMessage()) {
			info(getLocalizer().getString("add.attachment.success",
				this));
			forceReload = true;
			getAttachments();
			createAttachmentContainer(form);
		    }
		} catch (Exception e) {
		    error(getLocalizer()
			    .getString("add.attachment.error", this));
		    LOG.error("Error in attaching attachment with message", e);
		}
	    }
	});

	addAttachmentForm.add(new AjaxLink("cancel") {

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		addAttachmentContainer.setVisible(false);
		target.addComponent(addAttachmentContainer);

	    }
	});
	addAttachmentContainer.setVisible(false);
	addAttachmentContainer.add(addAttachmentForm);
	addAttachmentContainer.setOutputMarkupPlaceholderTag(true);
	attachmentContainer.addOrReplace(addAttachmentContainer);
	attachmentContainer.addOrReplace(new AjaxLink("addAttachment") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		addAttachmentContainer.setVisible(true);
		target.addComponent(addAttachmentContainer);
	    }
	}.setVisible(PortalUtils.exists(attachmentsList)));
	attachmentContainer.setVisible(isAttachmentContainerVisible);
	form.addOrReplace(attachmentContainer);

    }

    private void createAttachmentsListDataView(
	    WebMarkupContainer attachmentContainer, final Form form) {
	WebMarkupContainer msgAttachmentsContainer = new WebMarkupContainer(
		"msgAttachmentsContainer");

	dataProvider = new MessageAttachmentDataProvider(WICKET_ID_name,
		basePage);

	msgAttachmentList = new ArrayList<MessageAttachment>();

	final DataView<MessageAttachment> dataView = new DataView<MessageAttachment>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    if (!isCreateMode)
			dataProvider.loadMsgAttachmentsList(message.getId(),
				forceReload);
		    forceReload = false;
		    refreshTotalItemCount();
		    // reset rowIndex
		    rowIndex = 1;
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading friends and family list",
			    dple);
		    error(getLocalizer().getString("msgAttachments.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<MessageAttachment> item) {

		final MessageAttachment entry = item.getModelObject();

		msgAttachmentList.add(entry);

		// Select box
		AjaxCheckBox selectedCheckBox = new AjaxCheckBoxImpl(
			WICKET_ID_selected, new Model(isSelected(entry)), entry);

		selectedCheckBox.setOutputMarkupId(true);
		selectedCheckBox.setMarkupId(WICKET_ID_selected + rowIndex++);
		item.add(selectedCheckBox);

		item.add(new Label(WICKET_ID_name, entry.getName()));

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

	    private void refreshTotalItemCount() {
		totalItemString = new Integer(dataProvider.size()).toString();
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

	    class AjaxCheckBoxImpl extends AjaxCheckBox {

		private final MessageAttachment entry;

		public AjaxCheckBoxImpl(String id, IModel<Boolean> model,
			MessageAttachment entry) {
		    super(id, model);
		    this.entry = entry;
		}

		@Override
		public boolean isEnabled() {
		    return true;
		}

		@Override
		protected void onComponentTag(final ComponentTag tag) {
		    super.onComponentTag(tag);
		    if (getModelObject()) {
			tag.put("checked", "checked");
		    }
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
		    boolean checkBoxSelected = getModelObject();
		    if (checkBoxSelected) {
			LOG.info("Added {} to deletion list", entry.getId());
			selectedAttachments.add(entry);
		    } else {
			LOG.info("Removed {} from deletion list", entry.getId());
			selectedAttachments.remove(entry);
		    }
		}
	    }

	};

	dataView.setItemsPerPage(10);
	msgAttachmentsContainer.add(dataView);

	msgAttachmentsContainer.add(new OrderByBorder(WICKET_ID_orderByName,
		WICKET_ID_name, dataProvider) {
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

	Button removeButton = new Button("removeAttachment") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleRemove();
		createAttachmentContainer(form);
	    };

	    @Override
	    public boolean isVisible() {
		if (dataView.getItemCount() > 0) {
		    return super.isVisible();
		} else {
		    return false;
		}
	    }

	}.setDefaultFormProcessing(false);

	removeButton
		.add(new SimpleAttributeModifier("onclick", "return confirm('"
			+ getLocalizer().getString(
				"msgAttachments.remove.confirm", this) + "');"));

	msgAttachmentsContainer.add(removeButton);

	msgAttachmentsContainer.add(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("msgAttachments.noItemsMsg", this)
			+ "\n"
			+ getLocalizer().getString(
				"msgAttachments.addMsgAttachmentsHelp", this)) {
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
	msgAttachmentsContainer.add(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	msgAttachmentsContainer.add(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	msgAttachmentsContainer.add(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	msgAttachmentsContainer.add(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	attachmentContainer.addOrReplace(msgAttachmentsContainer);
    }

    protected boolean updateMessage() throws Exception {
	UpdateTemplateRequest request = basePage
		.getNewMobiliserRequest(UpdateTemplateRequest.class);
	request.setTemplate(message);
	UpdateTemplateResponse response = basePage.wsTemplateClient
		.updateTemplate(request);
	if (basePage.evaluateMobiliserResponse(response)) {
	    return true;
	}
	return false;
    }

    private void handleRemove() {
	if (!PortalUtils.exists(getSelectedAttachments())) {
	    error(getLocalizer().getString(
		    "msgAttachment.nothing.selected.remove", this));
	    return;
	}

	try {
	    Iterator<Long> iterator = message.getAttachments().iterator();

	    while (iterator.hasNext()) {
		Long attId = iterator.next();
		for (MessageAttachment removeId : getSelectedAttachments()) {
		    if (attId.equals(removeId.getId())) {
			iterator.remove();
		    }
		}
	    }
	    if (updateMessage()) {
		info(getLocalizer()
			.getString("remove.attachment.success", this));
		getAttachments();
	    }
	} catch (Exception e) {
	    error(getLocalizer().getString("remove.attachment.error", this));
	    LOG.error("Error in removing attachment", e);
	}

    };

    public boolean isSelected(MessageAttachment entry) {
	return selectedAttachments.contains(entry);
    }

    private List<KeyValue<Long, String>> getAttachments() {
	attachmentsList = new ArrayList<KeyValue<Long, String>>();
	if (!isCreateMode) {
	    try {
		FindAttachmentsRequest request = basePage
			.getNewMobiliserRequest(FindAttachmentsRequest.class);
		AttachmentSearchCriteria criteria = new AttachmentSearchCriteria();
		criteria.getTemplateExclusion().add(message.getId());
		request.setCriteria(criteria);
		FindAttachmentsResponse response = basePage.wsTemplateClient
			.findAttachments(request);
		if (basePage.evaluateMobiliserResponse(response)) {
		    List<MessageAttachment> attachments = response
			    .getAttachments();
		    for (MessageAttachment att : attachments) {
			attachmentsList.add(new KeyValue<Long, String>(att
				.getId(), att.getName()));
		    }
		}
	    } catch (Exception e) {
		error(getLocalizer().getString("load.attachment.error", this));
		LOG.error("Error in getAttachments", e);
	    }
	}
	return attachmentsList;
    }

    protected void createMessage() {
	message.setLocale(basePage.convertLocale(localeStr));
	if (Constants.TEMPLATE_TYPE_SMS_KEY.equals(message.getTemplateType())) {
	    MessageStringContent content = new MessageStringContent();
	    content.setContentType("text/plain");
	    content.setContent(getSmsText());
	    message.setContent(content);
	} else {
	    MessageStringContent content = new MessageStringContent();
	    content.setContentType("text/plain");
	    content.setContent(getPlainText());
	    message.setContent(content);
	    if (PortalUtils.exists(getHtmlText())) {
		MessageStringContent altContent = new MessageStringContent();
		altContent.setContentType("text/html");
		altContent.setContent(getHtmlText());
		message.setAlternativeContent(altContent);
	    }
	}
	if (message.getId() == null) {
	    try {
		CreateTemplateRequest request = basePage
			.getNewMobiliserRequest(CreateTemplateRequest.class);
		request.setTemplate(message);
		CreateTemplateResponse response = basePage.wsTemplateClient
			.createTemplate(request);
		if (!basePage.evaluateMobiliserResponse(response)) {
		    return;
		}
		LOG.debug("message created successfully");
		message.setId(response.getTemplateId());
		info(getLocalizer().getString("create.message.success", this));

		setResponsePage(new EditMessagePage(message));
	    } catch (Exception e) {
		LOG.error("Error in creating message. ", e);
		error(getLocalizer().getString("create.message.error", this));
	    }
	} else {
	    try {
		if (updateMessage()) {
		    info(getLocalizer().getString("update.message.success",
			    this));
		}
	    } catch (Exception e) {
		error(getLocalizer().getString("update.message.error", this));
		LOG.error("Error in updating message", e);
	    }

	}
    }

    private List<MessageAttachment> getSelectedAttachments() {
	return selectedAttachments;
    }

    public MessageTemplate getMessage() {
	return message;
    }

    public void setMessage(MessageTemplate message) {
	this.message = message;
    }

    public String getLocaleStr() {
	return localeStr;
    }

    public void setLocaleStr(String localeStr) {
	this.localeStr = localeStr;
    }

    public String getSmsText() {
	return smsText;
    }

    public void setSmsText(String smsText) {
	this.smsText = smsText;
    }

    public String getPlainText() {
	return plainText;
    }

    public void setPlainText(String plainText) {
	this.plainText = plainText;
    }

    public String getHtmlText() {
	return htmlText;
    }

    public void setHtmlText(String htmlText) {
	this.htmlText = htmlText;
    }

    public String getPrincipleText() {
	return logText;
    }

    public void setPrincipleText(String principleText) {
	this.logText = principleText;
    }

    public Long getAttachmentAdd() {
	return attachmentAdd;
    }

    public void setAttachmentAdd(Long attachmentAdd) {
	this.attachmentAdd = attachmentAdd;
    }

}
