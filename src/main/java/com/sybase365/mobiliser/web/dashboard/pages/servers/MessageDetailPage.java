package com.sybase365.mobiliser.web.dashboard.pages.servers;

import java.text.DateFormat;
import java.util.Date;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.NotificationBean;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_DASHBOARD_SERVERS)
public class MessageDetailPage extends ServersMenuGroup {
    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(MessageDetailPage.class);

    ServersMenuGroup parentPage;
    NotificationBean notification;

    public MessageDetailPage(NotificationBean notification) {
	super();
	this.notification = notification;
	initPageComponents();
    }

    @Override
    protected Class getActiveMenu() {
	return ChannelsPage.class;
    }

    protected void initPageComponents() {
	final Form<?> form = new Form("messageDetailForm",
		new CompoundPropertyModel<NotificationBean>(notification));

	FeedbackPanel feedbackPanel = new FeedbackPanel("errorMessages");

	form.add(feedbackPanel);

	form.add(new Label("sequenceNumber"));

	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
		DateFormat.FULL, getMobiliserWebSession().getLocale());

	form.add(new Label("timestamp", df.format(new Date(notification
		.getTimestamp()))));
	form.add(new Label("type"));
	form.add(new Label("message"));

	form.add(new Button("back") {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onSubmit() {
		setResponsePage(ChannelsPage.class);
	    }

	});

	add(form);
    }

}
