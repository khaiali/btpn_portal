package com.sybase365.mobiliser.web.cst.pages.notificationmgr;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageTemplate;
import com.sybase365.mobiliser.web.common.panels.NotificationMessagePanel;
import com.sybase365.mobiliser.web.util.Constants;

@AuthorizeInstantiation(Constants.PRIV_NMGR_CREATE)
public class CreateMessagePage extends BaseNotificationMgrPage {

    MessageTemplate message;
    String localeStr;

    @Override
    protected void initOwnPageComponents() {
	super.initOwnPageComponents();
	add(new NotificationMessagePanel("notificationMessagePanel", this, null));
    }

}
