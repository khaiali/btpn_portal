package com.sybase365.mobiliser.web.dashboard.pages.servers.beans;

import java.util.ArrayList;
import java.util.List;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.CompositeAttributeValueListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.KeyValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeCompositeValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectInstanceBean;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.base.RemoteManagedValues;

public class AuditManagerValues extends RemoteManagedValues {

    public static final String GROUP_ID = "com.sybase365.mobiliser.framework.service.audit.jmx";
    public static final String AUDIT_MANAGER = "IAuditManager";

    String auditManagerObjectName;

    CompositeAttributeValueListBean requestTypeValue;

    public AuditManagerValues(MobiliserBasePage mobBasePage) {
	super(mobBasePage);

	List<ObjectInstanceBean> auditManagerBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + AUDIT_MANAGER);

	for (ObjectInstanceBean bean : auditManagerBeans) {
	    auditManagerObjectName = bean.getObjectName();
	    requestTypeValue = getCompositeValueByCompositeKey(
		    auditManagerObjectName, "TypeAuditStatistics",
		    "requestType");
	}

    }

    public List<String> getRequestTypes() {
	List<String> requestTypes = new ArrayList<String>();
	if (requestTypeValue != null) {
	    requestTypes = requestTypeValue.getValueList().getValue();

	}
	return requestTypes;
    }

    public AuditStatisticsBean getStatisticsByRequestType(
	    String requestTypeValue) {
	MBeanAttributeCompositeValueBean valueBean = getCompositeValues(
		auditManagerObjectName, "TypeAuditStatistics", "requestType",
		requestTypeValue);
	AuditStatisticsBean requestTypeBean = new AuditStatisticsBean();
	List<KeyValueBean> kvList = valueBean.getCompositeValues()
		.getResultList();

	for (KeyValueBean kv : kvList) {
	    if (kv.getKey().equals("averageTime")) {
		requestTypeBean.setAverageTime(kv.getValue());
	    }
	    if (kv.getKey().equals("failureCount")) {
		requestTypeBean.setFailureCount(kv.getValue());
	    }
	    if (kv.getKey().equals("requestCount")) {
		requestTypeBean.setRequestCount(kv.getValue());
	    }
	    if (kv.getKey().equals("requestType")) {
		requestTypeBean.setRequestType(kv.getValue());
	    }
	    if (kv.getKey().equals("successCount")) {
		requestTypeBean.setSuccessCount(kv.getValue());
	    }

	}
	return requestTypeBean;

    }
}
