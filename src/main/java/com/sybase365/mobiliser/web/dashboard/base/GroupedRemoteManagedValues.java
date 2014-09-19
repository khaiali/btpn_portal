package com.sybase365.mobiliser.web.dashboard.base;

import java.util.List;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeValueListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanInfoBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.NotificationBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectInstanceBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectNameBean;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.util.PortalUtils;

public abstract class GroupedRemoteManagedValues implements
	java.io.Serializable {

    private MBeanAttributeValueListBean values;

    protected MobiliserBasePage mobBasePage;

    public GroupedRemoteManagedValues(MobiliserBasePage mobBasePage) {
	this.mobBasePage = mobBasePage;
    }

    protected abstract AttributeListBean getAttributeNames();

    protected void refreshValues() {
	if (PortalUtils.exists(mobBasePage)) {
	    this.values = mobBasePage
		    .getRemoteManagedAttributes(getAttributeNames());
	}
    }

    protected MBeanAttributeValueBean getValue(String objectName,
	    String attributeName) {

	if (PortalUtils.exists(values)) {
	    for (MBeanAttributeValueBean avb : values
		    .getMBeanAttributeValueBean()) {
		if (avb.getObjectName().equals(objectName)
			&& avb.getName().equals(attributeName)) {
		    return avb;
		}
	    }
	}

	return null;
    }

    protected List<ObjectInstanceBean> getRemoteManagedObjects(String objectName) {
	ObjectNameBean searchBean = new ObjectNameBean();
	searchBean.setObjectName(objectName);
	return mobBasePage.getRemoteManagedObjects(searchBean);
    }

    protected MBeanInfoBean getRemoteManagedObject(String objectName) {
	ObjectNameBean objectNameBean = new ObjectNameBean();
	objectNameBean.setObjectName(objectName);
	return mobBasePage.getRemoteManagedObject(objectNameBean);
    }

    protected List<String> invokeRemoteManagedOperation(String objectName,
	    String operationName, String[] params) {
	return mobBasePage.invokeRemoteManagedOperation(objectName,
		operationName, params);
    }

    protected List<NotificationBean> getNotifications(String objectName) {
	ObjectNameBean objectNameBean = new ObjectNameBean();
	objectNameBean.setObjectName(objectName);
	return mobBasePage.getNotifications(objectNameBean);
    }

}
