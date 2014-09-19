package com.sybase365.mobiliser.web.dashboard.pages.trackers.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserRequestType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeCompositeValueRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeCompositeValueResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeValueRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeValueResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.CompositeAttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.KeyValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeCompositeValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeValueBean;
import com.sybase365.mobiliser.util.management.services.api.IManagementEndpoint;
import com.sybase365.mobiliser.util.tools.clientutils.api.IServiceClientFactory;
import com.sybase365.mobiliser.web.dashboard.base.SpringAdapter;
import com.sybase365.mobiliser.web.dashboard.pages.trackers.dao.ITrackersDataSeriesDao;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.DynamicServiceConfiguration;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class TrackersDataSeriesJmxDaoImpl implements ITrackersDataSeriesDao,
	Serializable, InitializingBean {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(TrackersDataSeriesJmxDaoImpl.class);

    private Class<?>[] clientInterfaces = { com.sybase365.mobiliser.util.management.services.api.IManagementEndpoint.class };

    /**
     * @param clientInterface
     *            the clientInterface to set
     */
    public void setClientInterface(final Class<?> clientInterface) {
	setClientInterfaces(new Class[] { clientInterface });
    }

    /**
     * @param clientInterfaces
     *            the clientInterfaces to set
     */
    public void setClientInterfaces(final Class<?>[] clientInterfaces) {
	this.clientInterfaces = clientInterfaces;
    }

    private String endpointSuffix;

    /**
     * @param endpointSuffix
     *            the endpointSuffix to set
     */
    public void setEndpointSuffix(final String endpointSuffix) {
	this.endpointSuffix = endpointSuffix;
    }

    public TrackersDataSeriesJmxDaoImpl() {
    }

    @Override
    public void afterPropertiesSet() {
    }

    private IServiceClientFactory getServiceClientFactory() {
	return (IServiceClientFactory) SpringAdapter.getContext().getBean(
		"soapClientFactory", IServiceClientFactory.class);
    }

    private DynamicServiceConfiguration getDynamicServiceConfiguration() {
		return (DynamicServiceConfiguration) SpringAdapter.getContext().getBean("dynamicManagementClientConfiguration", DynamicServiceConfiguration.class);
    }

    private IManagementEndpoint createClient(IServiceClientFactory clientFactory) {
		DynamicServiceConfiguration configuration = getDynamicServiceConfiguration();
	
		return (IManagementEndpoint) clientFactory.createClient(clientInterfaces,
			configuration.getMobiliserEndpointUrl() + (endpointSuffix == null ? "" : endpointSuffix),
			configuration.getWsUserName(), configuration.getWsPassword());
    }

    private synchronized void destroyClient(IManagementEndpoint client) {
	try {
	    if (client instanceof DisposableBean) {
		LOG.debug("current target is destroyable...");
		((DisposableBean) client).destroy();
	    } else {
		LOG.debug("current target is not destroyable...");
	    }
	} catch (Exception e) {
	    LOG.error("Client disposable caught an exception", e);
	}
    }

    public synchronized String sample(final String server,
	    final String objectName, final String attributeName,
	    final String keyName, final String keyValue, final String valueName) {

		IServiceClientFactory clientFactory = getServiceClientFactory();
	
		IManagementEndpoint client = createClient(clientFactory);
	
		String result;
	
		if (PortalUtils.exists(keyName) && PortalUtils.exists(keyValue)
			&& PortalUtils.exists(valueName)) {
		    result = sampleCompositeType(client, objectName, attributeName,
			    keyName, keyValue, valueName);
		} else {
		    result = sampleSimpleType(client, objectName, attributeName);
		}
	
		destroyClient(client);
	
		clientFactory = null;
	
		return result;
    }

    private String sampleSimpleType(final IManagementEndpoint client,
	    final String objectName, final String attributeName) {

	LOG.debug("sampleSimpleType() -> {}/{}", new Object[] { objectName,
		attributeName });

	MBeanAttributeValueBean result;

	try {
	    AttributeBean attrBean = new AttributeBean();
	    attrBean.setObjectName(objectName);
	    attrBean.setAttributeName(attributeName);

	    GetMBeanAttributeValueRequest request = getNewMobiliserRequest(GetMBeanAttributeValueRequest.class);

	    request.setAttributeBean(attrBean);

	    GetMBeanAttributeValueResponse response = client
		    .getMBeanAttributeValue(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.debug("sample() - couldn't sample for data point because of mobiliser error response");
		return null;
	    }

	    result = response.getMBeanAttributeValueBean();

	} catch (Exception e) {
	    LOG.error(
		    "sampleSimpleType() - couldn't sample for data point because of exception",
		    e);
	    return null;
	}

	LOG.debug("sampleSimpleType() -> {} = {}", new Object[] {
		attributeName, result.getValue() });

	return result.getValue();
    }

    private synchronized String sampleCompositeType(
	    final IManagementEndpoint client, final String objectName,
	    final String attributeName, final String keyName,
	    final String keyValue, final String valueName) {

	LOG.debug("sampleCompositeType() -> {}/{}/{}/{}/{}", new Object[] {
		objectName, attributeName, keyName, keyValue, valueName });

	CompositeAttributeBean compAttrBean = new CompositeAttributeBean();
	compAttrBean.setObjectName(objectName);
	compAttrBean.setAttributeName(attributeName);
	compAttrBean.setKey(keyName);
	compAttrBean.setValue(keyValue);

	MBeanAttributeCompositeValueBean result;

	try {
	    GetMBeanAttributeCompositeValueRequest request = getNewMobiliserRequest(GetMBeanAttributeCompositeValueRequest.class);

	    request.setCompositeAttributeBean(compAttrBean);

	    GetMBeanAttributeCompositeValueResponse response = client
		    .getMBeanAttributeCompositeValue(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    result = response.getMBeanAttributeCompositeValueBean();

	} catch (Exception e) {
	    LOG.error(
		    "sampleCompositeType() - couldn't sample for data point because of exception",
		    e);
	    return null;
	}

	List<KeyValueBean> kvList = result.getCompositeValues().getResultList();

	for (KeyValueBean kv : kvList) {
	    if (kv.getKey().equals(valueName)) {
		LOG.debug("sampleCompositeType() -> {} = {}", new Object[] {
			valueName, kv.getValue() });
		return kv.getValue();
	    }
	}

	LOG.warn(
		"sampleCompositeType() - key: {}/{} and value: {} name not found",
		new Object[] { keyName, keyValue, valueName });

	return null;
    }

    private <Req extends MobiliserRequestType> Req getNewMobiliserRequest(
	    Class<Req> requestClass) throws Exception {

	Req req = requestClass.newInstance();
	req.setCallback(null);
	req.setConversationId(UUID.randomUUID().toString());
	req.setOrigin(Constants.MOBILISER_REQUEST_ORIGIN);
	req.setRepeat(Boolean.FALSE);
	req.setTraceNo(UUID.randomUUID().toString());

	return req;
    }

    private <Resp extends MobiliserResponseType> boolean evaluateMobiliserResponse(
	    Resp response) {

	if (response.getStatus().getCode() == 0) {
	    return true;
	}

	LOG.debug("Response returned status: {}-{}", response.getStatus()
		.getCode(), response.getStatus().getValue());

	// check for mobiliser session closed or expired
	if (response.getStatus().getCode() == 352
		|| response.getStatus().getCode() == 353) {

	    LOG.debug("Mobiliser session closed/expired error code {},",
		    response.getStatus().getCode());
	} else {
	    LOG.debug("Mobiliser error code: {}", response.getStatus()
		    .getCode());
	}

	return false;
    }
}