package com.sybase365.mobiliser.web.dashboard.pages.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EventQueueBean;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EventTaskHandlerBean;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EventValues;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class EventsPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EventsPage.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	EventValues eventBean = new EventValues(this);

	final Form<?> form = new Form("eventForm",
		new CompoundPropertyModel<EventValues>(eventBean));

	FeedbackPanel feedbackPanel = new FeedbackPanel("errorMessages");

	form.add(feedbackPanel);

	form.add(new Label("noRegular"));
	form.add(new Label("noRegenerated"));
	form.add(new Label("noDelayed"));
	form.add(new Label("noTransient"));
	form.add(new Label("noScheduled"));

	form.add(new ListView("physicalQueueListView", eventBean
		.getPhysicalEventQueues()) {

	    @Override
	    protected void populateItem(ListItem item) {
		EventQueueBean queue = (EventQueueBean) item.getModelObject();
		item.add(new Label("name", queue.getName()));
		item.add(new Label("size", queue.getSize()));
		item.add(new Label("maxSize", queue.getMaxSize()));
	    }

	});

	form.add(new ListView("virtualQueueListView", eventBean
		.getVirtualEventQueues()) {

	    @Override
	    protected void populateItem(ListItem item) {
		EventQueueBean queue = (EventQueueBean) item.getModelObject();
		item.add(new Label("name", queue.getName()));
		item.add(new Label("size", queue.getSize()));
		item.add(new Label("maxSize", queue.getMaxSize()));
	    }

	});

	final Map<String, Map<String, String>> eventMap = eventBean
		.getScheduledEvents();

	final List<String> eventList = new ArrayList<String>(eventMap.keySet());

	form.add(new ListView("eventListView", eventList) {

	    @Override
	    protected void populateItem(ListItem item) {
		final String eventName = (String) item.getModelObject();

		Link<String> taskLink = new Link<String>("eventLink", item
			.getModel()) {
		    @Override
		    public void onClick() {
			setResponsePage(new EventDetailPage(EventsPage.this,
				eventMap.get(eventName)));
		    }

		    @Override
		    protected void onComponentTagBody(
			    MarkupStream markupStream, ComponentTag openTag) {
			replaceComponentTagBody(markupStream, openTag,
				eventName);
		    }

		};
		item.add(taskLink);

		item.add(new Label("trigger", eventMap.get(eventName).get(
			"Trigger")));
	    }

	    @Override
	    public boolean isVisible() {
		if (eventList.size() > 0) {
		    return true;
		} else {
		    return false;
		}
	    }

	});

	form.add(new MultiLineLabel("noEventItemsMsg", getLocalizer()
		.getString("dashboard.events.scheduled.noItemsMsg", this)) {
	    @Override
	    public boolean isVisible() {
		if (eventList.size() > 0) {
		    return false;
		} else {
		    return true;
		}
	    }
	});

	form.add(new ListView("eventHandlerListView", eventBean
		.getEventHandlers()) {

	    @Override
	    protected void populateItem(ListItem item) {
		final EventTaskHandlerBean handler = (EventTaskHandlerBean) item
			.getModelObject();

		Link<EventTaskHandlerBean> handlerDetailLink = new Link<EventTaskHandlerBean>(
			"handlerLink", item.getModel()) {
		    @Override
		    public void onClick() {
			EventTaskHandlerBean entry = (EventTaskHandlerBean) getModelObject();
			setResponsePage(new EventHandlerDetailPage(
				EventsPage.this, entry));
		    }

		    @Override
		    protected void onComponentTagBody(
			    MarkupStream markupStream, ComponentTag openTag) {
			replaceComponentTagBody(markupStream, openTag,
				handler.getHandlerName());
		    }

		};
		item.add(handlerDetailLink);

		item.add(new Label("statusDescription", handler
			.getStatusDescription()));
		item.add(new Label("eventName", handler.getEventName()));
	    }

	});
	add(form);
    }
}
