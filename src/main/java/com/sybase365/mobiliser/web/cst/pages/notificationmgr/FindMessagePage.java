package com.sybase365.mobiliser.web.cst.pages.notificationmgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.DeleteTemplateRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.DeleteTemplateResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindTemplatesRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.FindTemplatesResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageTemplate;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.TemplateSearchCriteria;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.util.tools.wicketutils.security.PrivilegedBehavior;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.components.LocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_NMGR_EDIT)
public class FindMessagePage extends BaseNotificationMgrPage {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(FindMessagePage.class);
    private String name;
    private String templateType;
    private String localeStr;

    private List<MessageTemplate> messageList;
    List<MessageTemplate> selectedMessages = new ArrayList<MessageTemplate>();
    private MessageTemplateDataProvider dataProvider;
    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;
    private int rowIndex = 1;
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByType = "orderByType";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_Ltype = "Ltype";
    private static final String WICKET_ID_Lname = "Lname";
    private static final String WICKET_ID_Llocale = "Llocale";
    private static final String WICKET_ID_Lsubject = "Lsubject";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_editAction = "editAction";
    private static final String WICKET_ID_removeAction = "removeAction";
    private static final String WICKET_ID_testAction = "testAction";
    private static final String WICKET_ID_selected = "selected";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final Form<?> form = new Form("findMessageForm",
		new CompoundPropertyModel<FindMessagePage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new TextField<String>("name").add(new ErrorIndicator()));
	form.add(new LocalizableLookupDropDownChoice<String>("templateType",
		String.class, Constants.RESOURCE_BUNDLE_TEMPLATE_TYPE, this,
		Boolean.FALSE, true).setNullValid(true).add(
		new ErrorIndicator()));
	form.add(new TextField<String>("localeStr").add(
		new PatternValidator(Constants.REGEX_LOCALE_VARIANT)).add(
		new ErrorIndicator()));
	final WebMarkupContainer dataViewContainer = new WebMarkupContainer(
		"dataViewContainer");
	form.add(dataViewContainer);
	form.add(new Button("findMessage") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createFindMessageDataView(dataViewContainer, true);
	    }
	});
	add(form);
	createFindMessageDataView(dataViewContainer, false);
    }

    protected void createFindMessageDataView(
	    final WebMarkupContainer dataViewContainer, final boolean isVisible) {
	dataProvider = new MessageTemplateDataProvider(WICKET_ID_pageable, this);
	messageList = new ArrayList<MessageTemplate>();
	final DataView<MessageTemplate> dataView = new DataView<MessageTemplate>(
		WICKET_ID_pageable, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    TemplateSearchCriteria criteria = new TemplateSearchCriteria();
		    criteria.setLocale(convertLocale(localeStr));
		    if (PortalUtils.exists(getName()))
			criteria.setName(getName().replaceAll("\\*", "%"));
		    criteria.setType(templateType);
		    dataProvider.loadMsgAttachmentsList(criteria, true);
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error("An error occured while loading Message list",
			    dple);
		    error(getLocalizer().getString("message.find.error", this));
		}

		refreshTotalItemCount();
		// reset rowIndex
		rowIndex = 1;

		super.onBeforeRender();
	    }

	    @SuppressWarnings("rawtypes")
	    @Override
	    protected void populateItem(final Item<MessageTemplate> item) {
		final MessageTemplate entry = item.getModelObject();

		messageList.add(entry);
		// Select box
		AjaxCheckBox selectedCheckBox = new AjaxCheckBoxImpl(
			WICKET_ID_selected, new Model(isSelected(entry)), entry);
		selectedCheckBox.setOutputMarkupId(true);
		selectedCheckBox.setMarkupId(WICKET_ID_selected + rowIndex++);
		item.add(selectedCheckBox);
		item.add(new Label(WICKET_ID_Ltype, getDisplayValue(
			entry.getTemplateType(),
			Constants.RESOURCE_BUNDLE_TEMPLATE_TYPE)));
		item.add(new Label(WICKET_ID_Lsubject, entry.getSubject()));
		item.add(new Label(WICKET_ID_Lname, entry.getName()));
		item.add(new Label(WICKET_ID_Llocale, convertLocale(entry
			.getLocale())));

		// Edit Action
		Link<MessageTemplate> editLink = new Link<MessageTemplate>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			MessageTemplate entry = (MessageTemplate) getModelObject();
			setResponsePage(new EditMessagePage(entry));
		    }
		};
		item.add(editLink);

		// Remove Action
		Link removeLink = new Link<MessageTemplate>(
			WICKET_ID_removeAction, item.getModel()) {
		    @Override
		    public void onClick() {
			MessageTemplate entry = (MessageTemplate) getModelObject();
			if (removeMessage(entry))
			    createFindMessageDataView(dataViewContainer, true);
		    }

		};

		removeLink.add(new SimpleAttributeModifier("onclick",
			"return confirm('"
				+ getLocalizer().getString(
					"findMessage.remove.confirm", this)
				+ "');"));

		item.add(removeLink.add(new PrivilegedBehavior(
			FindMessagePage.this, Constants.PRIV_NMGR_DELETE)));
		Link testLink = new Link<MessageTemplate>(WICKET_ID_testAction,
			item.getModel()) {
		    @Override
		    public void onClick() {
			MessageTemplate entry = (MessageTemplate) getModelObject();
			setResponsePage(new TestMessagePage(entry));
		    }

		};
		item.add(testLink);

	    }

	    private void refreshTotalItemCount() {
		totalItemString = Integer.toString(dataProvider.size());
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
		private static final long serialVersionUID = 1L;
		private final MessageTemplate entry;

		public AjaxCheckBoxImpl(final String id,
			final IModel<Boolean> model, final MessageTemplate entry) {
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
		protected void onUpdate(final AjaxRequestTarget target) {
		    boolean checkBoxSelected = getModelObject();
		    if (checkBoxSelected) {
			LOG.info("Added {} to deletion list", entry.getId());
			selectedMessages.add(entry);
		    } else {
			LOG.info("Removed {} from deletion list", entry.getId());
			selectedMessages.remove(entry);
		    }
		}
	    }
	};
	dataView.setItemsPerPage(10);
	dataViewContainer.addOrReplace(dataView);

	dataViewContainer.addOrReplace(new OrderByBorder(WICKET_ID_orderByType,
		WICKET_ID_Lname, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onSortChanged() {
		// For some reasons the dataView can be null when the
		// page is
		// loading
		// and the sort is clicked (clicking the name header),
		// so handle
		// it
		if (dataView != null) {
		    dataView.setCurrentPage(0);
		}
	    }
	});

	final Button exportButton = new Button("exportMessages") {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		handleExport();
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

	dataViewContainer.addOrReplace(exportButton);

	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("messageList.noItemsMsg", this)) {
	    private static final long serialVersionUID = 1L;

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
	dataViewContainer.addOrReplace(new CustomPagingNavigator(
		WICKET_ID_navigator, dataView));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_totalItems,
		new PropertyModel<String>(this, "totalItemString")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_startIndex,
		new PropertyModel(this, "startIndex")));

	dataViewContainer.addOrReplace(new Label(WICKET_ID_endIndex,
		new PropertyModel(this, "endIndex")));
	dataViewContainer.setVisible(isVisible);

    }

    protected void handleExport() {
	if (!PortalUtils.exists(getSelectedMessages())) {
	    error(getLocalizer().getString(
		    "find.message.nothing.selected.export", this));
	    return;
	}
	doMessageExport(getSelectedMessages());
    }

    protected boolean isSelected(MessageTemplate entry) {
	return selectedMessages.contains(entry);
    }

    private boolean removeMessage(MessageTemplate entry) {
	try {
	    final DeleteTemplateRequest request = getNewMobiliserRequest(DeleteTemplateRequest.class);
	    request.setTemplateId(entry.getId());
	    final DeleteTemplateResponse response = wsTemplateClient
		    .deleteTemplate(request);
	    if (evaluateMobiliserResponse(response)) {
		info(getLocalizer().getString("remove.message.success", this));
		return true;
	    }
	} catch (Exception e) {
	    error(getLocalizer().getString("remove.message.error", this));
	    LOG.error("Error in removing message", e);
	}
	return false;

    }

    public void setName(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public void setTemplateType(String templateType) {
	this.templateType = templateType;
    }

    public String getTemplateType() {
	return templateType;
    }

    public void setLocaleStr(String localeStr) {
	this.localeStr = localeStr;
    }

    public String getLocaleStr() {
	return localeStr;
    }

    public List<MessageTemplate> getSelectedMessages() {
	return selectedMessages;
    }

    public void setSelectedMessages(List<MessageTemplate> selectedMessages) {
	this.selectedMessages = selectedMessages;
    }
}

class MessageTemplateDataProvider extends SortableDataProvider<MessageTemplate> {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory
	    .getLogger(MessageTemplateDataProvider.class);

    private transient List<MessageTemplate> msgTemplateEntries = new ArrayList<MessageTemplate>();
    private MobiliserBasePage mobBasePage;

    public MessageTemplateDataProvider(String defaultSortProperty,
	    final MobiliserBasePage mobBasePage) {
	setSort(defaultSortProperty, true);
	this.mobBasePage = mobBasePage;
    }

    /**
     * Returns MessageTemplate starting with index <code>first</code> and ending
     * with <code>first+count</code>
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int,
     *      int)
     */
    @Override
    public Iterator<MessageTemplate> iterator(int first, int count) {
	final SortParam sp = getSort();
	return find(first, count, sp.getProperty(), sp.isAscending())
		.iterator();
    }

    /**
     * Returns total number of <code>MessageTemplate</code> in the list
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public int size() {

	if (msgTemplateEntries == null) {
	    return 0;
	}

	return msgTemplateEntries.size();
    }

    @Override
    public final IModel<MessageTemplate> model(final MessageTemplate object) {
	IModel<MessageTemplate> model = new LoadableDetachableModel<MessageTemplate>() {
	    @Override
	    protected MessageTemplate load() {
		MessageTemplate set = null;
		for (MessageTemplate obj : msgTemplateEntries) {
		    if (obj.getId() == object.getId()) {
			set = obj;
			break;
		    }
		}
		return set;
	    }
	};

	return new CompoundPropertyModel<MessageTemplate>(model);
    }

    public void loadMsgAttachmentsList(TemplateSearchCriteria criteria,
	    boolean forcedReload) throws DataProviderLoadException {

	if (msgTemplateEntries == null || forcedReload) {

	    List<MessageTemplate> allEntries = getMessageTemplateList(criteria);

	    if (PortalUtils.exists(allEntries)) {
		msgTemplateEntries = allEntries;
	    }
	}
    }

    private List<MessageTemplate> getMessageTemplateList(
	    TemplateSearchCriteria criteria) throws DataProviderLoadException {
	List<MessageTemplate> msgAttachmentList = new ArrayList<MessageTemplate>();

	try {
	    FindTemplatesRequest request = mobBasePage
		    .getNewMobiliserRequest(FindTemplatesRequest.class);

	    request.setCriteria(criteria);
	    FindTemplatesResponse response = mobBasePage.wsTemplateClient
		    .findTemplates(request);
	    if (mobBasePage.evaluateMobiliserResponse(response)) {
		msgAttachmentList = response.getTemplates();
	    }
	} catch (Exception e) {
	    LOG.error("error in loading message list", e);
	    DataProviderLoadException dple = new DataProviderLoadException(
		    e.getMessage());
	    throw dple;
	}
	return msgAttachmentList;
    }

    protected List<MessageTemplate> find(int first, int count,
	    String sortProperty, boolean sortAsc) {

	List<MessageTemplate> sublist = getIndex(msgTemplateEntries,
		sortProperty, sortAsc).subList(first, first + count);

	return sublist;
    }

    protected List<MessageTemplate> getIndex(
	    List<MessageTemplate> MessageTemplateEntries, String prop,
	    boolean asc) {

	if ("name".equals(prop)) {
	    return sort(MessageTemplateEntries, asc);
	} else {
	    return MessageTemplateEntries;
	}
    }

    private List<MessageTemplate> sort(List<MessageTemplate> entries,
	    boolean asc) {

	if (asc) {

	    Collections.sort(entries, new Comparator<MessageTemplate>() {

		@Override
		public int compare(MessageTemplate arg0, MessageTemplate arg1) {
		    return arg0.getName().compareTo(arg1.getName());
		}
	    });

	} else {

	    Collections.sort(entries, new Comparator<MessageTemplate>() {

		@Override
		public int compare(MessageTemplate arg0, MessageTemplate arg1) {
		    return arg1.getName().compareTo(arg0.getName());
		}
	    });
	}
	return entries;
    }

}