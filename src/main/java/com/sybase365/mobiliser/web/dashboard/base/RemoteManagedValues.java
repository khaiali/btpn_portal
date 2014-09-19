package com.sybase365.mobiliser.web.dashboard.base;

import java.util.List;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.CompositeAttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.CompositeAttributeValueListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeCompositeValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectInstanceBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectNameBean;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;

public abstract class RemoteManagedValues implements java.io.Serializable {

    protected MobiliserBasePage mobBasePage;

    public RemoteManagedValues(MobiliserBasePage mobBasePage) {
	this.mobBasePage = mobBasePage;
    }

    protected MBeanAttributeValueBean getValue(String objectName,
	    String attributeName) {

	AttributeBean attrBean = new AttributeBean();
	attrBean.setObjectName(objectName);
	attrBean.setAttributeName(attributeName);

	return mobBasePage.getRemoteManagedAttribute(attrBean);
    }

    protected List<ObjectInstanceBean> getRemoteManagedObjects(String objectName) {
	ObjectNameBean searchBean = new ObjectNameBean();
	searchBean.setObjectName(objectName);
	return mobBasePage.getRemoteManagedObjects(searchBean);
    }

    protected CompositeAttributeValueListBean getCompositeValueByCompositeKey(
	    String objectName, String attributeName, String compositeKey) {

	CompositeAttributeBean compAttrBean = new CompositeAttributeBean();
	compAttrBean.setObjectName(objectName);
	compAttrBean.setAttributeName(attributeName);
	compAttrBean.setKey(compositeKey);

	return mobBasePage.getMBeanAttributeValuesByCompositeKey(compAttrBean);
    }

    protected MBeanAttributeCompositeValueBean getCompositeValues(
	    String objectName, String attributeName, String keyToMatch,
	    String valueToMatch) {

	CompositeAttributeBean compAttrBean = new CompositeAttributeBean();
	compAttrBean.setObjectName(objectName);
	compAttrBean.setAttributeName(attributeName);
	compAttrBean.setKey(keyToMatch);
	compAttrBean.setValue(valueToMatch);

	return mobBasePage.getRemoteManagedAttributeComposite(compAttrBean);
    }

}
