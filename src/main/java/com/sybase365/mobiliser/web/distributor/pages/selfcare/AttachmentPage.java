package com.sybase365.mobiliser.web.distributor.pages.selfcare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.io.Streams;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.web.common.components.CustomPagingNavigator;
import com.sybase365.mobiliser.web.common.dataproviders.AttachmentDataProvider;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.PortalUtils;

@AuthorizeInstantiation(Constants.PRIV_MERCHANT_LOGIN)
public class AttachmentPage extends SelfCareMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AttachmentPage.class);

    // Data Model for table list
    private AttachmentDataProvider dataProvider;
    private List<Attachment> attachments;

    // Table Items
    private String totalItemString = null;
    private int startIndex = 0;
    private int endIndex = 0;

    private boolean forceReload = true;

    private static final String WICKET_ID_navigator = "navigator";
    private static final String WICKET_ID_totalItems = "totalItems";
    private static final String WICKET_ID_startIndex = "startIndex";
    private static final String WICKET_ID_endIndex = "endIndex";
    private static final String WICKET_ID_pageable = "pageable";
    private static final String WICKET_ID_id = "id";
    private static final String WICKET_ID_date = "date";
    private static final String WICKET_ID_type = "type";
    private static final String WICKET_ID_name = "name";
    private static final String WICKET_ID_contentType = "contentType";
    private static final String WICKET_ID_viewAction = "viewAction";
    private static final String WICKET_ID_noItemsMsg = "noItemsMsg";

    public AttachmentPage() {
	super();
    }

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @param parameters
     *            Page parameters
     */
    public AttachmentPage(final PageParameters parameters) {
	super(parameters);
    }

    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	add(new FeedbackPanel("errorMessages"));

	createAttachmentListDataView();
    }

    private void downloadAttachment(final Attachment attachment) {
	try {
	    getRequestCycle().setRequestTarget(new IRequestTarget() {

		public void respond(RequestCycle requestCycle) {
		    try {
			InputStream attStream = new ByteArrayInputStream(
				attachment.getContent());
			WebResponse webResponse = (WebResponse) getResponse();
			webResponse.setAttachmentHeader(attachment.getName());
			webResponse.setContentType(attachment.getContentType());
			Streams.copy(attStream, webResponse.getOutputStream());

		    } catch (Exception e) {
			LOG.error("# Error in download attachment ", e);
			error(getLocalizer().getString(
				"attachment.download.error",
				AttachmentPage.this));
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

    private void createAttachmentListDataView() {

	dataProvider = new AttachmentDataProvider(WICKET_ID_name, this);

	attachments = new ArrayList<Attachment>();

	final Long customerId = getWebSession().getLoggedInCustomer()
		.getCustomerId();

	final DataView<Attachment> dataView = new DataView<Attachment>(
		WICKET_ID_pageable, dataProvider) {

	    @Override
	    protected void onBeforeRender() {

		try {
		    dataProvider.loadCustomerAttachments(customerId,
			    forceReload);

		    refreshTotalItemCount();

		    if (dataProvider.size() > 0) {
			setVisible(true);
		    } else {
			setVisible(false);
		    }
		} catch (DataProviderLoadException dple) {
		    LOG.error(
			    "# An error occurred while loading customer attachments list",
			    dple);
		    error(getLocalizer().getString("attachment.load.error",
			    this));
		}

		refreshTotalItemCount();

		super.onBeforeRender();
	    }

	    @Override
	    protected void populateItem(final Item<Attachment> item) {

		final Attachment entry = item.getModelObject();

		attachments.add(entry);

		item.add(new Label(WICKET_ID_id, entry.getId().toString()));

		item.add(new Label(WICKET_ID_date, PortalUtils
			.getFormattedDateTime(entry.getCreated(),
				getMobiliserWebSession().getLocale(),
				getMobiliserWebSession().getTimeZone())));

		item.add(new Label(WICKET_ID_type, Integer.toString(entry
			.getAttachmentType())));

		item.add(new Label(WICKET_ID_name, entry.getName()));

		item.add(new Label(WICKET_ID_contentType, entry
			.getContentType()));

		// View Action
		Link<Attachment> viewLink = new Link<Attachment>(
			WICKET_ID_viewAction, item.getModel()) {

		    @Override
		    public void onClick() {
			Attachment entry = (Attachment) getModelObject();
			downloadAttachment(entry);
		    }
		};
		item.add(viewLink);

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
		totalItemString = Integer.toString(dataProvider.size());
		int total = dataProvider.size();
		if (total > 0) {
		    startIndex = getCurrentPage() * getItemsPerPage() + 1;
		    endIndex = startIndex + getItemsPerPage() - 1;
		    if (endIndex > total) {
			endIndex = total;
		    }
		} else {
		    startIndex = 0;
		    endIndex = 0;
		}
	    }
	};

	dataView.setItemsPerPage(5);

	add(dataView);

	add(new MultiLineLabel(WICKET_ID_noItemsMsg, getLocalizer().getString(
		"attachment.table.noItemsMsg", this)) {

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
	add(new CustomPagingNavigator(WICKET_ID_navigator, dataView));

	add(new Label(WICKET_ID_totalItems, new PropertyModel<String>(this,
		"totalItemString")));

	add(new Label(WICKET_ID_startIndex, new PropertyModel(this,
		"startIndex")));

	add(new Label(WICKET_ID_endIndex, new PropertyModel(this, "endIndex")));
    }

}
