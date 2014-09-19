package com.sybase365.mobiliser.web.dashboard.pages.servers;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.NotificationBean;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.ChannelValues;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EventValues;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class ChannelsPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ChannelsPage.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();
	ChannelValues channelBean = new ChannelValues(this);

	final Form<?> form = new Form("channelsForm",
		new CompoundPropertyModel<EventValues>(channelBean));

	FeedbackPanel feedbackPanel = new FeedbackPanel("errorMessages");

	form.add(feedbackPanel);

	form.add(new Label("availableChannels"));
	form.add(new Label("receivedMessages"));
	form.add(new Label("sentMessages"));
	form.add(new Label("failedMessages"));

	List<NotificationBean> notificationList = channelBean
		.getNotificationList();
	List<String> messageList = new ArrayList<String>();
	form.add(new ListView("messageListView", notificationList) {

	    @Override
	    protected void populateItem(ListItem item) {
		final NotificationBean notification = (NotificationBean) item
			.getModelObject();

		Link<String> msgDetailLink = new Link<String>("messageLink",
			item.getModel()) {
		    @Override
		    public void onClick() {
			setResponsePage(new MessageDetailPage(notification));
		    }

		    @Override
		    protected void onComponentTagBody(
			    MarkupStream markupStream, ComponentTag openTag) {
			replaceComponentTagBody(markupStream, openTag,
				notification.getMessage());
		    }

		};
		item.add(msgDetailLink);

	    }

	});

	form.add(new Button("reloadMessages") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(ChannelsPage.class);
	    }

	});

	add(form);
    }
}
