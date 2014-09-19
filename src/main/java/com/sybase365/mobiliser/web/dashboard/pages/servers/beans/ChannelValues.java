package com.sybase365.mobiliser.web.dashboard.pages.servers.beans;

import java.util.List;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.NotificationBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectInstanceBean;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.base.GroupedRemoteManagedValues;

public class ChannelValues extends GroupedRemoteManagedValues {

    public static final String GROUP_ID = "com.sybase365.mobiliser.util.messaging.channelmanager";
    public static final String CHANNEL_MANAGER = "ChannelManager";

    String channelObjectName;

    private AttributeListBean alb = new AttributeListBean();

    public ChannelValues(MobiliserBasePage mobBasePage) {
	super(mobBasePage);

	List<ObjectInstanceBean> channelsSummaryBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + CHANNEL_MANAGER);

	for (ObjectInstanceBean bean : channelsSummaryBeans) {
	    channelObjectName = bean.getObjectName();

	    addAttributeBean(channelObjectName, "AvailableChannels");
	    addAttributeBean(channelObjectName, "ReceivedMessages");
	    addAttributeBean(channelObjectName, "SentMessages");
	    addAttributeBean(channelObjectName, "FailedMessages");

	}

	refreshValues();
    }

    @Override
    protected AttributeListBean getAttributeNames() {
	return alb;
    }

    private void addAttributeBean(String objectName, String attributeName) {
	AttributeBean attrBean = new AttributeBean();
	attrBean.setObjectName(objectName);
	attrBean.setAttributeName(attributeName);
	alb.getAttributeBean().add(attrBean);
    }

    private String getAttributeValue(String objectName, String attributesName) {
	return getValue(objectName, attributesName) == null ? "" : getValue(
		objectName, attributesName).getValue();
    }

    public String getAvailableChannels() {
	return getAttributeValue(channelObjectName, "AvailableChannels");
    }

    public String getReceivedMessages() {
	return getAttributeValue(channelObjectName, "ReceivedMessages");
    }

    public String getSentMessages() {
	return getAttributeValue(channelObjectName, "SentMessages");
    }

    public String getFailedMessages() {
	return getAttributeValue(channelObjectName, "FailedMessages");
    }

    public List<NotificationBean> getNotificationList() {
	return getNotifications(channelObjectName);
    }

}
