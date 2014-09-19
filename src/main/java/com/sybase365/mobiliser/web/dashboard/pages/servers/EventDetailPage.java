package com.sybase365.mobiliser.web.dashboard.pages.servers;

import java.util.Map;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.EventTaskDetailPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class EventDetailPage extends ServersMenuGroup {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(EventDetailPage.class);

    ServersMenuGroup parentPage;
    Map<String, String> detail;

    public EventDetailPage(ServersMenuGroup parentPage,
	    Map<String, String> eventDetail) {
	super();
	this.parentPage = parentPage;
	this.detail = eventDetail;
	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	return EventsPage.class;
    }

    protected void initPageComponents() {
	add(new EventTaskDetailPanel("eventDetailPanel", detail) {

	    @Override
	    protected ServersMenuGroup getBackResponsePage() {
		return parentPage;
	    }

	});
    }

}