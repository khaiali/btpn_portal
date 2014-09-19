package com.sybase365.mobiliser.web.cst.pages.notificationmgr;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
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
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.AttachmentSearchCriteria;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageAttachment;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.common.dataproviders.MessageAttachmentDataProvider;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_NMGR_EDIT)
public class FindAttachmentPage extends BaseNotificationMgrPage {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(FindAttachmentPage.class);
    private String name;
    private String contentType;
    private List<MessageAttachment> attachmentList;
    private MessageAttachmentDataProvider dataProvider;
    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;
    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_orderByName = "orderByName";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_Ltype = "Ltype";
    private static final String WICKET_ID_Lname = "Lname";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";
    private static final String WICKET_ID_editAction = "editAction";

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	final Form<?> form = new Form("findAttachmentForm",
		new CompoundPropertyModel<FindAttachmentPage>(this));
	form.add(new FeedbackPanel("errorMessages"));
	form.add(new TextField<String>("name").add(new ErrorIndicator()));
	form.add(new TextField<String>("contentType").add(new ErrorIndicator()));
	final WebMarkupContainer dataViewContainer = new WebMarkupContainer(
		"dataViewContainer");
	form.add(dataViewContainer);
	form.add(new Button("findAttachment") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		createFindAttachmentDataView(dataViewContainer, true);
	    }
	});
	add(form);
	createFindAttachmentDataView(dataViewContainer, false);

    }

    protected void createFindAttachmentDataView(
	    final WebMarkupContainer dataViewContainer, final boolean isVisible) {
	dataProvider = new MessageAttachmentDataProvider(WICKET_ID_pageable,
		this);
	attachmentList = new ArrayList<MessageAttachment>();
	final DataView<MessageAttachment> dataView = new DataView<MessageAttachment>(
		WICKET_ID_pageable, dataProvider) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onBeforeRender() {

		try {
		    AttachmentSearchCriteria criteria = new AttachmentSearchCriteria();
		    if (PortalUtils.exists(getName()))
			criteria.setName(getName().replaceAll("\\*", "%"));
		    if (PortalUtils.exists(getContentType()))
			criteria.setContentType(getContentType().replaceAll(
				"\\*", "%"));
		    dataProvider.findAttachmentsList(criteria, true);
		    refreshTotalItemCount();
		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error("An error occured while loading Attachment list",
			    dple);
		    error(getLocalizer().getString("attachment.find.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @SuppressWarnings("rawtypes")
	    @Override
	    protected void populateItem(final Item<MessageAttachment> item) {
		final MessageAttachment entry = item.getModelObject();

		attachmentList.add(entry);
		item.add(new Label(WICKET_ID_Lname, entry.getName()));
		item.add(new Label(WICKET_ID_Ltype, String.valueOf(entry
			.getContentType())));

		// Edit Action
		Link<MessageAttachment> editLink = new Link<MessageAttachment>(
			WICKET_ID_editAction, item.getModel()) {
		    @Override
		    public void onClick() {
			MessageAttachment entry = (MessageAttachment) getModelObject();
			setResponsePage(new EditAttachmentPage(entry));
		    }
		};
		item.add(editLink);

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

	};
	dataView.setItemsPerPage(10);
	dataViewContainer.addOrReplace(dataView);

	dataViewContainer.addOrReplace(new OrderByBorder(WICKET_ID_orderByName,
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
	dataViewContainer.addOrReplace(new MultiLineLabel(WICKET_ID_noItemsMsg,
		getLocalizer().getString("attachmentList.noItemsMsg", this)) {
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

    public void setName(final String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public void setContentType(final String contentType) {
	this.contentType = contentType;
    }

    public String getContentType() {
	return contentType;
    }

}
