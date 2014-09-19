package com.sybase365.mobiliser.web.dashboard.pages.servers;

import java.util.Map;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.EventTaskDetailPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class TaskDetailPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TaskDetailPage.class);

    ServersMenuGroup parentPage;
    Map<String, String> detail;

    public TaskDetailPage(ServersMenuGroup parentPage,
	    Map<String, String> taskDetail) {
	super();
	this.parentPage = parentPage;
	this.detail = taskDetail;
	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	return TasksPage.class;
    }

    protected void initPageComponents() {
	add(new EventTaskDetailPanel("taskDetailPanel", detail) {

	    @Override
	    protected ServersMenuGroup getBackResponsePage() {
		return parentPage;
	    }

	});
    }

}