package com.sybase365.mobiliser.web.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.dashboard.pages.servers.ServersMenuGroup;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EventTaskHandlerBean;

public abstract class EventTaskHandlerDetailPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EventTaskHandlerDetailPanel.class);

    EventTaskHandlerBean eventHandler;

    public EventTaskHandlerDetailPanel(String id,
	    EventTaskHandlerBean eventHandler) {
	super(id);
	this.eventHandler = eventHandler;
	constructPanel();
    }

    private void constructPanel() {
	final Form<?> form = new Form("eventHandlerForm",
		new CompoundPropertyModel<EventTaskHandlerBean>(eventHandler));

	FeedbackPanel feedbackPanel = new FeedbackPanel("errorMessages");

	form.add(feedbackPanel);

	form.add(new Label("handlerName"));
	form.add(new Label("eventName"));
	form.add(new Label("statusDescription"));

	form.add(new Label("avgProcessTime"));
	form.add(new Label("configuredMaxActiveThreads"));
	form.add(new Label("configuredMaxIdleThreads"));
	form.add(new Label("currentActiveThreads"));
	form.add(new Label("currentIdleThreads"));

	form.add(new Label("lastFail"));
	form.add(new Label("lastRun"));
	form.add(new Label("noCatchup"));
	form.add(new Label("noExpire"));
	form.add(new Label("noFail"));
	form.add(new Label("noProcessed"));
	form.add(new Label("noRuns"));
	form.add(new Label("noSuccess"));

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(getBackResponsePage());
	    }

	});

	add(form);
    }

    protected abstract ServersMenuGroup getBackResponsePage();
}
