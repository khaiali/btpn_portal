package com.sybase365.mobiliser.web.dashboard.pages.servers.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectInstanceBean;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.dashboard.base.GroupedRemoteManagedValues;

public class TaskValues extends GroupedRemoteManagedValues {

    public static final String GROUP_ID = "com.sybase365.mobiliser.framework.event";
    public static final String PRODUCT_SCHEDULER = "Scheduler";
    public static final String PRODUCT_TASK_HANDLER = "Handler.Task";

    public String schedulerObjectName;
    public List<String> taskHandlerNames;

    private AttributeListBean alb = new AttributeListBean();

    public TaskValues(MobiliserBasePage mobBasePage) {
	super(mobBasePage);

	// scheduled task
	List<ObjectInstanceBean> schedulerBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + PRODUCT_SCHEDULER);

	for (ObjectInstanceBean bean : schedulerBeans) {
	    schedulerObjectName = bean.getObjectName();

	    addAttributeBean(schedulerObjectName, "ScheduledTaskJobs");
	}

	// task handlers
	List<ObjectInstanceBean> taskHandlerBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + PRODUCT_TASK_HANDLER);

	taskHandlerNames = new ArrayList<String>();

	for (ObjectInstanceBean bean : taskHandlerBeans) {
	    String objectName = bean.getObjectName();
	    taskHandlerNames.add(bean.getObjectName());
	    addAttributeBean(objectName, "HandlerName");
	    addAttributeBean(objectName, "EventName");
	    addAttributeBean(objectName, "StatusDescription");
	    addAttributeBean(objectName, "AvgProcessTime");
	    addAttributeBean(objectName, "ConfiguredMaxActiveThreads");
	    addAttributeBean(objectName, "ConfiguredMaxIdleThreads");
	    addAttributeBean(objectName, "CurrentActiveThreads");
	    addAttributeBean(objectName, "CurrentIdleThreads");
	    addAttributeBean(objectName, "LastFail");
	    addAttributeBean(objectName, "LastRun");
	    addAttributeBean(objectName, "NoCatchup");
	    addAttributeBean(objectName, "NoExpire");
	    addAttributeBean(objectName, "NoFail");
	    addAttributeBean(objectName, "NoProcessed");
	    addAttributeBean(objectName, "NoRuns");
	    addAttributeBean(objectName, "NoSuccess");

	}

	refreshValues();
    }

    private void addAttributeBean(String objectName, String attributeName) {
	AttributeBean attrBean = new AttributeBean();
	attrBean.setObjectName(objectName);
	attrBean.setAttributeName(attributeName);
	alb.getAttributeBean().add(attrBean);
    }

    @Override
    protected AttributeListBean getAttributeNames() {
	return alb;
    }

    public Map<String, Map<String, String>> getScheduledTasks() {
	Map<String, Map<String, String>> taskMap = new HashMap<String, Map<String, String>>();
	List<String> taskNameList = new ArrayList<String>();
	String tasksStr = getAttributeValue(schedulerObjectName,
		"ScheduledTaskJobs");
	if (tasksStr != null && !tasksStr.equals("")) {
	    tasksStr = tasksStr.substring(1, tasksStr.length() - 1);
	    if (tasksStr.length() != 0)
		taskNameList = Arrays.asList(tasksStr.split("[,]"));
	}

	for (String taskName : taskNameList) {
	    taskName = taskName.trim();
	    taskMap.put(taskName, getTaskDetail(taskName));
	}
	return taskMap;
    }

    public Map<String, String> getTaskDetail(String taskName) {
	Map<String, String> taskDetail = new HashMap<String, String>();
	List<String> result = invokeRemoteManagedOperation(schedulerObjectName,
		"getTriggerDetails", new String[] { taskName });

	for (String details : result) {
	    details = details.substring(1, details.length() - 1);
	    if (details.length() != 0) {
		List<String> items = Arrays.asList(details.split("[,]"));
		for (String item : items) {
		    String[] parts = item.split("=");
		    String key = "";
		    String value = "";
		    if (parts.length > 1) {
			value = parts[1].trim();
		    }
		    if (parts.length > 0) {
			key = parts[0].trim();
		    }
		    taskDetail.put(key, value);
		}
	    }
	}
	return taskDetail;

    }

    public List<EventTaskHandlerBean> getTaskHandlers() {
	List<EventTaskHandlerBean> taskHandlers = new ArrayList<EventTaskHandlerBean>();
	for (String objectName : taskHandlerNames) {
	    EventTaskHandlerBean handler = new EventTaskHandlerBean();
	    handler.setEventName(getAttributeValue(objectName, "EventName"));
	    handler.setHandlerName(getAttributeValue(objectName, "HandlerName"));
	    handler.setStatusDescription(getAttributeValue(objectName,
		    "StatusDescription"));

	    handler.setAvgProcessTime(getAttributeValue(objectName,
		    "AvgProcessTime"));
	    handler.setConfiguredMaxActiveThreads(getAttributeValue(objectName,
		    "ConfiguredMaxActiveThreads"));
	    handler.setConfiguredMaxIdleThreads(getAttributeValue(objectName,
		    "ConfiguredMaxIdleThreads"));
	    handler.setCurrentActiveThreads(getAttributeValue(objectName,
		    "CurrentActiveThreads"));
	    handler.setCurrentIdleThreads(getAttributeValue(objectName,
		    "CurrentIdleThreads"));
	    handler.setLastFail(getAttributeValue(objectName, "LastFail"));
	    handler.setLastRun(getAttributeValue(objectName, "LastRun"));
	    handler.setNoCatchup(getAttributeValue(objectName, "NoCatchup"));
	    handler.setNoExpire(getAttributeValue(objectName, "NoExpire"));
	    handler.setNoFail(getAttributeValue(objectName, "NoFail"));
	    handler.setNoProcessed(getAttributeValue(objectName, "NoProcessed"));
	    handler.setNoRuns(getAttributeValue(objectName, "NoRuns"));
	    handler.setNoSuccess(getAttributeValue(objectName, "NoSuccess"));

	    taskHandlers.add(handler);
	}
	return taskHandlers;
    }

    private String getAttributeValue(String objectName, String attributesName) {
	return getValue(objectName, attributesName) == null ? "" : getValue(
		objectName, attributesName).getValue();
    }
}
