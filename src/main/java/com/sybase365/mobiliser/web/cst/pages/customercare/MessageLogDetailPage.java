package com.sybase365.mobiliser.web.cst.pages.customercare;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageLog;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class MessageLogDetailPage extends CustomerCareMenuGroup {

    private static final Logger LOG = LoggerFactory
	    .getLogger(MessageLogDetailPage.class);

    private MessageLog messageLog;
    private WebPage backPage;

    public MessageLogDetailPage(MessageLog messageLog, WebPage backPage) {
	super();
	this.messageLog = messageLog;
	this.backPage = backPage;
	initPageComponent();
    }

    private void initPageComponent() {
	Form<?> form = new Form("messageLogDetailsForm",
		new CompoundPropertyModel<MessageLogDetailPage>(this));

	form.add(new Label("messageLogId", String.valueOf(getMessageLog()
		.getId())));

	form.add(new Label("id", String.valueOf(getMessageLog().getId())));

	form.add(new Label("messageText", getMessageLog().getMessageText()));

	form.add(new Label("templateName", getMessageLog().getTemplateName()));

	form.add(new Label("sendDate", PortalUtils.getFormattedDateTime(
		getMessageLog().getSentDate(), getMobiliserWebSession()
			.getLocale(), getMobiliserWebSession().getTimeZone())));

	form.add(new Button("back") {
	    @Override
	    public void onSubmit() {
		setResponsePage(backPage);
	    }
	});
	add(form);

    }

    public MessageLog getMessageLog() {
	return messageLog;
    }

}
