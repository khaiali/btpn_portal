package com.sybase365.mobiliser.web.dashboard.pages.servers;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.EventTaskHandlerDetailPanel;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EventTaskHandlerBean;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class TaskHandlerDetailPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TaskHandlerDetailPage.class);

    ServersMenuGroup parentPage;
    EventTaskHandlerBean taskHandler;

    public TaskHandlerDetailPage(ServersMenuGroup parentPage,
	    EventTaskHandlerBean taskHandler) {
	super();
	this.parentPage = parentPage;
	this.taskHandler = taskHandler;
	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	return TasksPage.class;
    }

    protected void initPageComponents() {
	add(new EventTaskHandlerDetailPanel("taskHandlerDetailPanel",
		taskHandler) {

	    @Override
	    protected ServersMenuGroup getBackResponsePage() {
		return parentPage;
	    }

	});
    }

}
