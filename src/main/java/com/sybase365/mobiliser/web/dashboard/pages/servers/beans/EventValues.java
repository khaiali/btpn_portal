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

public class EventValues extends GroupedRemoteManagedValues {

    public static final String GROUP_ID = "com.sybase365.mobiliser.framework.event";
    public static final String PRODUCT_GENERATOR = "Generator";
    public static final String PRODUCT_PHYSICAL_QUEUE = "Dispatcher,instance=PhysicalQueue*";
    public static final String PRODUCT_VIRTUAL_QUEUE = "Dispatcher,instance=VirtualQueue*";
    public static final String PRODUCT_SCHEDULER = "Scheduler";
    public static final String PRODUCT_EVENT_HANDLER = "Handler.Event";

    public String geneartorObjectName;
    public List<String> eventHandlerNames;
    public List<String> physicalQueueNames;
    public List<String> virtualQueueNames;
    public String schedulerObjectName;

    private AttributeListBean alb = new AttributeListBean();

    public EventValues(MobiliserBasePage mobBasePage) {
	super(mobBasePage);

	// generator
	List<ObjectInstanceBean> generatorBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + PRODUCT_GENERATOR);

	for (ObjectInstanceBean bean : generatorBeans) {
	    geneartorObjectName = bean.getObjectName();

	    addAttributeBean(geneartorObjectName, "NoRegular");
	    addAttributeBean(geneartorObjectName, "NoRegenerated");
	    addAttributeBean(geneartorObjectName, "NoDelayed");
	    addAttributeBean(geneartorObjectName, "NoTransient");
	    addAttributeBean(geneartorObjectName, "NoScheduled");
	}

	// physical queues

	List<ObjectInstanceBean> physicalQueueBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + PRODUCT_PHYSICAL_QUEUE);

	physicalQueueNames = new ArrayList<String>();
	for (ObjectInstanceBean bean : physicalQueueBeans) {
	    String objectName = bean.getObjectName();
	    physicalQueueNames.add(objectName);

	    addAttributeBean(objectName, "Name");
	    addAttributeBean(objectName, "Size");
	    addAttributeBean(objectName, "MaxSize");

	}

	// virtual queues
	List<ObjectInstanceBean> virtualQueueBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + PRODUCT_VIRTUAL_QUEUE);

	virtualQueueNames = new ArrayList<String>();

	for (ObjectInstanceBean bean : virtualQueueBeans) {
	    String objectName = bean.getObjectName();
	    virtualQueueNames.add(objectName);

	    addAttributeBean(objectName, "Name");
	    addAttributeBean(objectName, "Size");
	    addAttributeBean(objectName, "MaxSize");

	}

	// scheduled event
	List<ObjectInstanceBean> schedulerBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + PRODUCT_SCHEDULER);

	for (ObjectInstanceBean bean : schedulerBeans) {
	    schedulerObjectName = bean.getObjectName();

	    addAttributeBean(schedulerObjectName, "ScheduledEventJobs");
	}

	// event handler
	List<ObjectInstanceBean> eventHandlerBeans = getRemoteManagedObjects(GROUP_ID
		+ ":*,product=" + PRODUCT_EVENT_HANDLER);

	eventHandlerNames = new ArrayList<String>();
	for (ObjectInstanceBean bean : eventHandlerBeans) {
	    String objectName = bean.getObjectName();
	    eventHandlerNames.add(bean.getObjectName());
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

    public String getNoRegular() {
	return getAttributeValue(geneartorObjectName, "NoRegular");
    }

    public String getNoRegenerated() {
	return getAttributeValue(geneartorObjectName, "NoRegenerated");
    }

    public String getNoDelayed() {
	return getAttributeValue(geneartorObjectName, "NoDelayed");
    }

    public String getNoTransient() {
	return getAttributeValue(geneartorObjectName, "NoTransient");
    }

    public String getNoScheduled() {
	return getAttributeValue(geneartorObjectName, "NoScheduled");
    }

    public List<EventQueueBean> getPhysicalEventQueues() {
	return getEventQueues(physicalQueueNames);
    }

    public List<EventQueueBean> getVirtualEventQueues() {
	return getEventQueues(virtualQueueNames);
    }

    private List<EventQueueBean> getEventQueues(List<String> queueNames) {
	List<EventQueueBean> eventQueues = new ArrayList<EventQueueBean>();
	for (String objectName : queueNames) {
	    EventQueueBean queue = new EventQueueBean();

	    queue.setName(getAttributeValue(objectName, "Name"));
	    queue.setSize(getAttributeValue(objectName, "Size"));
	    queue.setMaxSize(getAttributeValue(objectName, "MaxSize"));
	    eventQueues.add(queue);
	}
	return eventQueues;
    }

    public List<EventTaskHandlerBean> getEventHandlers() {
	List<EventTaskHandlerBean> eventHandlers = new ArrayList<EventTaskHandlerBean>();
	for (String objectName : eventHandlerNames) {
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

	    eventHandlers.add(handler);
	}
	return eventHandlers;
    }

    public Map<String, Map<String, String>> getScheduledEvents() {
	Map<String, Map<String, String>> eventMap = new HashMap<String, Map<String, String>>();
	List<String> eventNameList = new ArrayList<String>();
	String eventsStr = getAttributeValue(schedulerObjectName,
		"ScheduledEventJobs");
	if (eventsStr != null && !eventsStr.equals("")) {
	    eventsStr = eventsStr.substring(1, eventsStr.length() - 1);
	    if (eventsStr.length() != 0)
		eventNameList = Arrays.asList(eventsStr.split("[,]"));
	}

	for (String eventName : eventNameList) {
	    eventName = eventName.trim();
	    eventMap.put(eventName, getEventDetail(eventName));
	}
	return eventMap;
    }

    public Map<String, String> getEventDetail(String eventName) {
	Map<String, String> eventDetail = new HashMap<String, String>();
	List<String> result = invokeRemoteManagedOperation(schedulerObjectName,
		"getTriggerDetails", new String[] { eventName });

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
		    eventDetail.put(key, value);
		}
	    }
	}
	return eventDetail;

    }

}
