package com.sybase365.mobiliser.web.cst.pages.notificationmgr;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.web.common.panels.MessageAttachmentPanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_NMGR_CREATE)
public class CreateAttachmentPage extends BaseNotificationMgrPage {

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new MessageAttachmentPanel("messageAttachmentPanel", this, null));
    }
}
