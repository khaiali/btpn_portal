package com.sybase365.mobiliser.web.dashboard.pages.servers;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.AuditManagerValues;
import com.sybase365.mobiliser.web.dashboard.pages.servers.beans.AuditStatisticsBean;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class ServiceDetailPage extends ServersMenuGroup {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(ServicesPage.class);

    ServersMenuGroup parentPage;
    String requestType;

    AuditStatisticsBean statisticsBean;

    public ServiceDetailPage(ServersMenuGroup parentPage, String requestType,
	    AuditManagerValues auditManagerValues) {
	super();
	this.parentPage = parentPage;
	this.requestType = requestType;

	statisticsBean = auditManagerValues
		.getStatisticsByRequestType(requestType);

	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	return ServicesPage.class;
    }

    protected void initPageComponents() {
	final Form<?> form = new Form("serviceDetailForm",
		new CompoundPropertyModel<AuditStatisticsBean>(statisticsBean));

	FeedbackPanel feedbackPanel = new FeedbackPanel("errorMessages");

	form.add(feedbackPanel);

	form.add(new Label("requestType"));
	form.add(new Label("requestCount"));
	form.add(new Label("successCount"));
	form.add(new Label("failureCount"));
	form.add(new Label("averageTime"));

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(parentPage);
	    }

	});

	add(form);
    }

}
