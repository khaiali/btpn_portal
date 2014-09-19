package com.sybase365.mobiliser.web.cst.pages.notificationmgr;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageAttachment;
import com.sybase365.mobiliser.web.common.panels.MessageAttachmentPanel;

public class EditAttachmentPage extends BaseNotificationMgrPage {

    public EditAttachmentPage(MessageAttachment attachment) {
	super();
	add(new MessageAttachmentPanel("messageAttachmentPanel", this,
		attachment));
    }

}
