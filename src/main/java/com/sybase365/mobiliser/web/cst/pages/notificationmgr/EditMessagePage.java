package com.sybase365.mobiliser.web.cst.pages.notificationmgr;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageTemplate;
import com.sybase365.mobiliser.web.common.panels.NotificationMessagePanel;

public class EditMessagePage extends BaseNotificationMgrPage {
    MessageTemplate message;

    public EditMessagePage(MessageTemplate message) {
	super();
	this.message = message;
	initPage();
    }

    private void initPage() {
	add(new NotificationMessagePanel("notificationMessagePanel", this,
		message));
    }

}
