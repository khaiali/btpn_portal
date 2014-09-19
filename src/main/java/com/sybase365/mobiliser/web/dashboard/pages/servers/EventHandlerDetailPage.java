package com.sybase365.mobiliser.web.dashboard.pages.servers;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.EventTaskHandlerDetailPanel;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.EventTaskHandlerBean;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class EventHandlerDetailPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EventHandlerDetailPage.class);

    ServersMenuGroup parentPage;
    EventTaskHandlerBean eventHandler;

    public EventHandlerDetailPage(ServersMenuGroup parentPage,
	    EventTaskHandlerBean eventHandler) {
	super();
	this.parentPage = parentPage;
	this.eventHandler = eventHandler;
	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	return EventsPage.class;
    }

    protected void initPageComponents() {
	add(new EventTaskHandlerDetailPanel("eventHandlerDetailPanel",
		eventHandler) {

	    @Override
	    protected ServersMenuGroup getBackResponsePage() {
		return parentPage;
	    }

	});
    }

}
