package com.sybase365.mobiliser.web.dashboard.pages.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EventTaskHandlerBean;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.TaskValues;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class TasksPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TasksPage.class);

    public void TasksPage() {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initOwnPageComponents() {

	super.initOwnPageComponents();

	add(new FeedbackPanel("errorMessages"));

	TaskValues taskBean = new TaskValues(this);

	final Map<String, Map<String, String>> taskMap = taskBean
		.getScheduledTasks();

	List<String> taskList = new ArrayList<String>(taskMap.keySet());
	add(new ListView("taskListView", taskList) {

	    @Override
	    protected void populateItem(ListItem item) {
		final String taskName = (String) item.getModelObject();

		Link<String> taskLink = new Link<String>("taskLink",
			item.getModel()) {
		    @Override
		    public void onClick() {
			setResponsePage(new TaskDetailPage(TasksPage.this,
				taskMap.get(taskName)));
		    }

		    @Override
		    protected void onComponentTagBody(
			    MarkupStream markupStream, ComponentTag openTag) {
			replaceComponentTagBody(markupStream, openTag, taskName);
		    }

		};
		item.add(taskLink);

		item.add(new Label("cronExpresion", taskMap.get(taskName).get(
			"Cron Expression")));

	    }

	    @Override
	    public boolean isVisible() {
		if (taskMap.size() > 0) {
		    return true;
		} else {
		    return false;
		}
	    }

	});

	add(new MultiLineLabel("noTaskItemsMsg", getLocalizer().getString(
		"dashboard.tasks.scheduled.noItemsMsg", this)) {
	    @Override
	    public boolean isVisible() {
		if (taskMap.size() > 0) {
		    return false;
		} else {
		    return true;
		}
	    }
	});

	add(new ListView("taskHandlerListView", taskBean.getTaskHandlers()) {

	    @Override
	    protected void populateItem(ListItem item) {
		final EventTaskHandlerBean handler = (EventTaskHandlerBean) item
			.getModelObject();

		Link<EventTaskHandlerBean> handlerDetailLink = new Link<EventTaskHandlerBean>(
			"handlerLink", item.getModel()) {
		    @Override
		    public void onClick() {
			EventTaskHandlerBean entry = (EventTaskHandlerBean) getModelObject();
			setResponsePage(new TaskHandlerDetailPage(
				TasksPage.this, entry));
		    }

		    @Override
		    protected void onComponentTagBody(
			    MarkupStream markupStream, ComponentTag openTag) {
			String fullName = handler.getHandlerName();
			replaceComponentTagBody(
				markupStream,
				openTag,
				"..."
					+ fullName.substring(
						fullName.length() - 50,
						fullName.length()));
		    }

		};
		item.add(handlerDetailLink);

		item.add(new Label("statusDescription", handler
			.getStatusDescription()));
		item.add(new Label("eventName", handler.getEventName()));
	    }

	});
    }
}
