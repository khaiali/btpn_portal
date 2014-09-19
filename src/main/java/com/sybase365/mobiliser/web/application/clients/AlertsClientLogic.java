package com.sybase365.mobiliser.web.application.clients;

import java.util.Collections;
import java.util.List;

import com.sybase365.mobiliser.util.alerts.contract.v5_0.AddBlackoutToCustomerAlertRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.AddBlackoutToCustomerAlertResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.CreateCustomerAlertRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.CreateCustomerAlertResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.CreateCustomerBlackoutScheduleRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.CreateCustomerBlackoutScheduleResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.CreateCustomerContactPointRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.CreateCustomerContactPointResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.CreateCustomerOtherIdentificationRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.CreateCustomerOtherIdentificationResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerAlertByCustomerAndDataRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerAlertByCustomerAndDataResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerAlertRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerAlertResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerBlackoutScheduleRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerBlackoutScheduleResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerContactPointRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerContactPointResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerOtherIdentificationRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerOtherIdentificationResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.FindCustomerAlertByCustomerRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.FindCustomerAlertByCustomerResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetActiveAlertTypesRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetActiveAlertTypesResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetAlertTypeRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetAlertTypeResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetCustomerAlertRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetCustomerAlertResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetCustomerBlackoutScheduleByCustomerRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetCustomerBlackoutScheduleByCustomerResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetCustomerOtherIdentificationByCustomerRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetCustomerOtherIdentificationByCustomerResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.RemoveBlackoutFromCustomerAlertRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.RemoveBlackoutFromCustomerAlertResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerAlertRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerAlertResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerBlackoutScheduleRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerBlackoutScheduleResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerOtherIdentificationRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerOtherIdentificationResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutSchedule;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.util.alerts.services.api.IAlertTypeManagementEndpoint;
import com.sybase365.mobiliser.util.alerts.services.api.ICustomerAlertManagementEndpoint;
import com.sybase365.mobiliser.web.application.model.IMobiliserServiceClientLogic;

/**
 * Client logic for Mobiliser alerts web services handling
 * <p>
 * &copy; 2012 by Sybase, Inc.
 * </p>
 * 
 * @author <a href='mailto:msw@sybase.com'>Mark White</a>
 */
public class AlertsClientLogic extends BaseClientLogic implements
	IMobiliserServiceClientLogic {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(AlertsClientLogic.class);

    // initialise the web service clients for this client logic hander
    private IAlertTypeManagementEndpoint wsAlertTypesClient;

    private ICustomerAlertManagementEndpoint wsCustomerAlertsClient;

    public void setWsAlertTypesClient(IAlertTypeManagementEndpoint value) {
	this.wsAlertTypesClient = value;
    }

    public void setWsCustomerAlertsClient(ICustomerAlertManagementEndpoint value) {
	this.wsCustomerAlertsClient = value;
    }

    /**
     * Wraps a CreateCustomerBlackScheduleRequest WS call
     * 
     * @param customerBlackoutSchedule
     * @param alertId
     * @return WS Response
     */
    public int createCustomerBlackoutSchedule(
	    CustomerBlackoutSchedule customerBlackoutSchedule, long alertId) {

	int status = -1;

	try {
	    CreateCustomerBlackoutScheduleRequest request = getNewMobiliserRequest(CreateCustomerBlackoutScheduleRequest.class);

	    request.setBlackoutSchedule(customerBlackoutSchedule);

	    CreateCustomerBlackoutScheduleResponse createCustomerBlackoutScheduleResponse = wsCustomerAlertsClient
		    .createCustomerBlackoutSchedule(request);

	    if (!evaluateMobiliserResponse(createCustomerBlackoutScheduleResponse)) {
		LOG
			.warn("# An error occurred while creating customer blackout schedule");
		return -1;
	    }

	    status = createCustomerBlackoutScheduleResponse.getStatus()
		    .getCode();

	    if (status == 0) {

		customerBlackoutSchedule
			.setId(createCustomerBlackoutScheduleResponse
				.getBlackoutScheduleId());

		status = addBlackoutToCustomerAlert(
			createCustomerBlackoutScheduleResponse
				.getBlackoutScheduleId().longValue(), alertId);
	    }

	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while adding creating customer blackout",
			    e);
	    return -1;

	}
	return status;
    }

    /**
     * Wraps an UpdateCustomerBlackScheduleRequest WS call
     * 
     * @param customerBlackoutSchedule
     * @return WS Response
     */
    public int updateCustomerBlackoutSchedule(
	    CustomerBlackoutSchedule customerBlackoutSchedule) {

	int status = -1;

	try {
	    UpdateCustomerBlackoutScheduleRequest request = getNewMobiliserRequest(UpdateCustomerBlackoutScheduleRequest.class);

	    request.setBlackoutSchedule(customerBlackoutSchedule);

	    UpdateCustomerBlackoutScheduleResponse response = wsCustomerAlertsClient
		    .updateCustomerBlackoutSchedule(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while updateing customer blackout schedule");
		return -1;
	    }

	    status = response.getStatus().getCode();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while adding creating customer blackout",
			    e);
	    return -1;

	}
	return status;
    }

    /**
     * Wraps an deleteCustomerAlertByCustomerAndData WS call
     * 
     * @param customerId
     * @return WS Response
     */
    public int deleteCustomerAlertByCustomerAndData(long customerId,
	    String key, long value) {

	int status = -1;

	try {
	    DeleteCustomerAlertByCustomerAndDataRequest request = getNewMobiliserRequest(DeleteCustomerAlertByCustomerAndDataRequest.class);
	    request.setCustomerId(customerId);
	    request.setKey(key);
	    request.setData(String.valueOf(value));
	    DeleteCustomerAlertByCustomerAndDataResponse response = wsCustomerAlertsClient
		    .deleteCustomerAlertByCustomerAndData(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while deleting  customer alert by account data");
		return -1;
	    }

	    status = response.getStatus().getCode();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while deleting  customer alert by account data",
			    e);
	    return -1;

	}
	return status;
    }

    /**
     * Wraps a GetCustomerBlackoutScheduleByCustomerRequest WS call
     * 
     * @param customerId
     * @return WS Response
     */
    public List<CustomerBlackoutSchedule> getCustomerBlackoutScheduleByCustomer(
	    long customerId) {

	GetCustomerBlackoutScheduleByCustomerResponse response = null;

	try {
	    GetCustomerBlackoutScheduleByCustomerRequest request = getNewMobiliserRequest(GetCustomerBlackoutScheduleByCustomerRequest.class);

	    request.setCustomerId(customerId);

	    response = wsCustomerAlertsClient
		    .getCustomerBlackoutScheduleByCustomer(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while fetching customer blackout schedules");
	    }

	    return response.getBlackoutSchedule();

	} catch (Exception e) {
	    LOG
		    .error(
			    "Error occured when fetching the customer blackout schedules ",
			    e);
	}
	return Collections.emptyList();
    }

    /**
     * Wraps a DeleteCustomerBlackoutScheduleRequest WS call
     * 
     * @param customerId
     * @return WS Response
     */
    public int deleteCustomerBlackoutSchedule(CustomerBlackoutSchedule cbs) {

	int status = -1;

	DeleteCustomerBlackoutScheduleResponse response = null;

	try {
	    DeleteCustomerBlackoutScheduleRequest request = getNewMobiliserRequest(DeleteCustomerBlackoutScheduleRequest.class);

	    request.setBlackoutScheduleId(cbs.getId());

	    response = wsCustomerAlertsClient
		    .deleteCustomerBlackoutSchedule(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while fetching customer blackout schedules");
		return -1;
	    }

	    status = response.getStatus().getCode();

	} catch (Exception e) {
	    LOG
		    .error(
			    "Error occured when fetching the customer blackout schedules ",
			    e);
	    return -1;
	}
	return status;
    }

    /**
     * Wraps a AddBlackoutToCustomerAlertRequest WS call
     * 
     * @param customerBlackoutSchedule
     * @param alertId
     * @return WS Response
     */
    public int addBlackoutToCustomerAlert(long blackoutScheduleId, long alertId) {

	int status = -1;

	try {
	    AddBlackoutToCustomerAlertRequest addBlackoutToCustomerAlertRequest = getNewMobiliserRequest(AddBlackoutToCustomerAlertRequest.class);

	    addBlackoutToCustomerAlertRequest
		    .setBlackoutScheduleId(blackoutScheduleId);
	    addBlackoutToCustomerAlertRequest.setCustomerAlertId(alertId);

	    AddBlackoutToCustomerAlertResponse response = wsCustomerAlertsClient
		    .addBlackoutToCustomerAlert(addBlackoutToCustomerAlertRequest);

	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while adding customer blackout to alert");
		return -1;
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while adding creating customer blackout",
			    e);
	    return -1;

	}
	return status;
    }

    /**
     * Wraps a RemoveBlackoutFromCustomerAlertRequest WS call
     * 
     * @param customerBlackoutSchedule
     * @param alertId
     * @return WS Response
     */
    public int removeBlackoutToCustomerAlert(long blackoutScheduleId,
	    long alertId) {

	int status = -1;

	try {
	    RemoveBlackoutFromCustomerAlertRequest removeBlackoutFromCustomerAlertRequest = getNewMobiliserRequest(RemoveBlackoutFromCustomerAlertRequest.class);

	    removeBlackoutFromCustomerAlertRequest
		    .setBlackoutScheduleId(blackoutScheduleId);
	    removeBlackoutFromCustomerAlertRequest.setCustomerAlertId(alertId);

	    RemoveBlackoutFromCustomerAlertResponse response = wsCustomerAlertsClient
		    .removeBlackoutFromCustomerAlert(removeBlackoutFromCustomerAlertRequest);

	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while removing a customer blackout from alert");
		return -1;
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while removing customer blackout from alert",
			    e);
	    return -1;

	}
	return status;
    }

    /**
     * Wraps an UpdateCustomerOtherIdentificationRequest WS call
     * 
     * @param customerOtherIdentification
     * @return WS Response
     */
    public int updateCustomerOtherIdentification(
	    CustomerOtherIdentification customerOtherIdentification) {

	int status = -1;

	try {
	    UpdateCustomerOtherIdentificationRequest request = getNewMobiliserRequest(UpdateCustomerOtherIdentificationRequest.class);

	    request.setOtherIdentification(customerOtherIdentification);

	    UpdateCustomerOtherIdentificationResponse response = wsCustomerAlertsClient
		    .updateCustomerOtherIdentification(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while updating  Customer OtherIdentification");
		return -1;
	    }

	    status = response.getStatus().getCode();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while updating  Customer OtherIdentification",
			    e);
	    return -1;

	}
	return status;
    }

    /**
     * Wraps a CreateCustomerOtherIdentificationRequest WS call
     * 
     * @param customerOtherIdentification
     * @return WS Response
     */
    public int createCustomerOtherIdentification(
	    CustomerOtherIdentification customerOtherIdentification) {

	int status = -1;

	try {
	    CreateCustomerOtherIdentificationRequest request = getNewMobiliserRequest(CreateCustomerOtherIdentificationRequest.class);

	    request.setOtherIdentification(customerOtherIdentification);

	    CreateCustomerOtherIdentificationResponse createCustomerOtherIdentificationResponse = wsCustomerAlertsClient
		    .createCustomerOtherIdentification(request);

	    status = createCustomerOtherIdentificationResponse.getStatus()
		    .getCode();

	    if (!evaluateMobiliserResponse(createCustomerOtherIdentificationResponse)) {
		LOG
			.warn("# An error occurred while creating  Customer OtherIdentification");
		return -1;
	    }

	    status = createCustomerOtherIdentificationResponse.getStatus()
		    .getCode();

	    if (status == 0) {
		customerOtherIdentification
			.setId(createCustomerOtherIdentificationResponse
				.getOtherIdentificationId());
	    }
	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while creating  Customer OtherIdentification",
			    e);

	}
	return status;
    }

    /**
     * Wraps a DeleteCustomerOtherIdentificationRequest call
     * 
     * @param OtherIdentificationId
     * @return WS Response
     */
    public int deleteCustomerOtherIdentification(Long OtherIdentificationId) {

	int status = -1;

	try {
	    DeleteCustomerOtherIdentificationRequest request = getNewMobiliserRequest(DeleteCustomerOtherIdentificationRequest.class);

	    request.setOtherIdentificationId(OtherIdentificationId);

	    DeleteCustomerOtherIdentificationResponse deleteCustomerOtherIdentificationResponse = wsCustomerAlertsClient
		    .deleteCustomerOtherIdentification(request);

	    if (!evaluateMobiliserResponse(deleteCustomerOtherIdentificationResponse)) {
		LOG
			.warn("# An error occurred while deleting OtherIdentification ");
		return -1;
	    }

	    status = deleteCustomerOtherIdentificationResponse.getStatus()
		    .getCode();
	} catch (Exception e) {
	    LOG
		    .error(
			    "Error occured while deleting CustomerOtherIdentification ",
			    e);

	}
	return status;
    }

    /**
     * Wraps a FindCustomerOtherIdentificationRequest WS call
     * 
     * @param customerId
     * @return WS Response
     */
    public List<CustomerOtherIdentification> findCustomerOtherIdentification(
	    long customerId) {

	List<CustomerOtherIdentification> listCustomerOtherIdentification = null;
	GetCustomerOtherIdentificationByCustomerResponse getCustomerOtherIdentificationByCustomerResponse = null;

	try {
	    GetCustomerOtherIdentificationByCustomerRequest request = getNewMobiliserRequest(GetCustomerOtherIdentificationByCustomerRequest.class);

	    request.setCustomerId(customerId);

	    getCustomerOtherIdentificationByCustomerResponse = wsCustomerAlertsClient
		    .getCustomerOtherIdentificationByCustomer(request);

	    if (!evaluateMobiliserResponse(getCustomerOtherIdentificationByCustomerResponse)) {
		LOG
			.warn("# An error occurred while fetching OtherIdentification by customer Id");
		return null;
	    }

	    listCustomerOtherIdentification = getCustomerOtherIdentificationByCustomerResponse
		    .getOtherIdentification();
	} catch (Exception e) {
	    LOG
		    .error(
			    "Error occured while find customer OtherIdentification by customer Id ",
			    e);
	    return null;
	}
	return listCustomerOtherIdentification;
    }

    /**
     * Wraps a FindCustomerAlertByCustomerRequest WS call
     * 
     * @param customerId
     * @return WS Response
     */
    public List<CustomerAlert> findCustomerAlertByCustomer(Long customerId) {

	FindCustomerAlertByCustomerResponse findCustomerAlertsResponse = null;

	try {
	    FindCustomerAlertByCustomerRequest request = getNewMobiliserRequest(FindCustomerAlertByCustomerRequest.class);

	    request.setCustomerId(customerId);

	    findCustomerAlertsResponse = wsCustomerAlertsClient
		    .findCustomerAlertByCustomer(request);

	    if (!evaluateMobiliserResponse(findCustomerAlertsResponse)) {
		LOG.warn("# An error occurred while fetching customer Alert");
	    }

	    return findCustomerAlertsResponse.getCustomerAlert();

	} catch (Exception e) {
	    LOG.error("Error occured when fetching the customer Alerts ", e);
	    return null;
	}
    }

    /**
     * Wraps a GetCustomerAlertRequest WS call
     * 
     * @param alertId
     * @return WS Response
     */
    public CustomerAlert findCustomerAlert(Long alertId) {

	GetCustomerAlertResponse getCustomerAlertResponse = null;

	try {
	    GetCustomerAlertRequest request = getNewMobiliserRequest(GetCustomerAlertRequest.class);

	    request.setCustomerAlertId(alertId);

	    getCustomerAlertResponse = wsCustomerAlertsClient
		    .getCustomerAlert(request);

	    if (!evaluateMobiliserResponse(getCustomerAlertResponse)) {
		LOG.warn("# An error occurred while fetching customer Alert");
	    }

	    return getCustomerAlertResponse.getCustomerAlert();

	} catch (Exception e) {
	    LOG.error("Error occured when fetching the customer Alerts ", e);
	    return null;
	}
    }

    /**
     * Wraps a CreateCustomerAlertRequest WS call
     * 
     * @param customerAlert
     * @return WS Response
     */
    public Long createCustomerAlert(CustomerAlert customerAlert) {
	try {
	    CreateCustomerAlertRequest request = getNewMobiliserRequest(CreateCustomerAlertRequest.class);

	    request.setCustomerAlert(customerAlert);

	    CreateCustomerAlertResponse createCustomerAlertResponse = wsCustomerAlertsClient
		    .createCustomerAlert(request);
	    if (!evaluateMobiliserResponse(createCustomerAlertResponse)) {
		LOG.warn("# An error occurred while creating customer Alert");
		return null;
	    }

	    return createCustomerAlertResponse.getCustomerAlertId();

	} catch (Exception e) {
	    LOG.error("Error occured when creating the customer Alerts ", e);
	    return null;
	}
    }

    /**
     * Wraps a CreateCustomerContactPointRequest WS call
     * 
     * @param customerAlertId
     * @param customerIdentificationId
     * @param customerOtherIdentificationId
     * @return WS Response
     */
    public Long createCustomerContactPoint(long customerAlertId,
	    Long customerIdentificationId, Long customerOtherIdentificationId) {
	try {
	    CreateCustomerContactPointRequest request = getNewMobiliserRequest(CreateCustomerContactPointRequest.class);

	    request.setCustomerAlertId(customerAlertId);

	    if (customerIdentificationId == null) {
		request.setIdentificationId(null);
		request.setOtherIdentificationId(customerOtherIdentificationId);
	    } else {
		request.setIdentificationId(customerIdentificationId);
		request.setOtherIdentificationId(null);
	    }

	    CreateCustomerContactPointResponse createCustomerContactPointResponse = wsCustomerAlertsClient
		    .createCustomerContactPoint(request);

	    if (!evaluateMobiliserResponse(createCustomerContactPointResponse)) {
		LOG
			.warn("# An error occurred while creating customer Contact Points");
		return null;
	    }

	    return createCustomerContactPointResponse.getContactPointId();

	} catch (Exception e) {
	    LOG.error(
		    "Error occured when creating the customer Contact Points ",
		    e);
	    return null;
	}
    }

    /**
     * Wraps a DeleteCustomerContactPointRequest WS call
     * 
     * @param contactPointId
     * @return WS Response
     */
    public int deleteCustomerContactPoint(long contactPointId) {
	try {
	    DeleteCustomerContactPointRequest request = getNewMobiliserRequest(DeleteCustomerContactPointRequest.class);

	    request.setContactPointId(contactPointId);

	    DeleteCustomerContactPointResponse deleteCustomerContactPointResponse = wsCustomerAlertsClient
		    .deleteCustomerContactPoint(request);

	    if (!evaluateMobiliserResponse(deleteCustomerContactPointResponse)) {
		LOG
			.warn("# An error occurred while deleting customer Contact Points");
		return -1;
	    }

	    return deleteCustomerContactPointResponse.getStatus().getCode();

	} catch (Exception e) {
	    LOG
		    .error(
			    "Error occured when deleting  the customer Contact Points ",
			    e);
	    return -1;
	}
    }

    /**
     * Wraps an UpdateCustomerAlertRequest WS call
     * 
     * @param customerAlert
     * @return WS Response
     */
    public int updateCustomerAlert(CustomerAlert customerAlert) {

	int status = -1;

	try {
	    UpdateCustomerAlertRequest request = getNewMobiliserRequest(UpdateCustomerAlertRequest.class);

	    request.setCustomerAlert(customerAlert);

	    UpdateCustomerAlertResponse updateCustomerAlertResponse = wsCustomerAlertsClient
		    .updateCustomerAlert(request);

	    status = updateCustomerAlertResponse.getStatus().getCode();

	    if (!evaluateMobiliserResponse(updateCustomerAlertResponse)) {
		LOG.warn("# An error occurred while updating customer alert");
		return -1;
	    }
	} catch (Exception e) {
	    LOG.error("Error occured when updating  a the customer Alert ", e);
	    return -1;
	}

	return status;

    }

    /**
     * Wraps a DeleteCustomerAlertRequest WS call
     * 
     * @param customerAlertId
     * @return WS Response
     */
    public boolean deleteCustomerAlert(Long customerAlertId) {
	try {
	    DeleteCustomerAlertRequest request = getNewMobiliserRequest(DeleteCustomerAlertRequest.class);

	    request.setCustomerAlertId(customerAlertId);

	    DeleteCustomerAlertResponse deleteCustomerAlertRes = wsCustomerAlertsClient
		    .deleteCustomerAlert(request);

	    if (!evaluateMobiliserResponse(deleteCustomerAlertRes)) {
		LOG.warn("# An error occurred while deleting customer alert");
		return false;
	    }

	    LOG.debug("# Successfully deleted customer alert");

	} catch (Exception e) {
	    LOG.error("Error occured when deleting a the customer Alert ", e);
	}
	return true;
    }

    /**
     * Wraps a GetActiveAlertTypesRequest WS call
     * 
     * @return WS Response
     */
    public List<AlertType> findAlertTypes() {

	List<AlertType> result = null;

	try {
	    GetActiveAlertTypesRequest request = getNewMobiliserRequest(GetActiveAlertTypesRequest.class);

	    GetActiveAlertTypesResponse response = wsAlertTypesClient
		    .getActiveAlertTypes(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while finding alert types");
		return result;
	    }

	    result = response.getAlertType();

	} catch (Exception e) {
	    LOG.error("Error occured when finding all alert types", e);
	}
	return result;
    }

    /**
     * Wraps a GetAlertTypeRequest WS call
     * 
     * @param alertTypeId
     * @return WS Response
     */
    public AlertType getAlertType(long alertTypeId) {

	AlertType result = null;

	try {
	    GetAlertTypeRequest request = getNewMobiliserRequest(GetAlertTypeRequest.class);

	    request.setAlertTypeId(alertTypeId);

	    GetAlertTypeResponse response = wsAlertTypesClient
		    .getAlertType(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while getting alert type");
		return result;
	    }

	    result = response.getAlertType();

	} catch (Exception e) {
	    LOG.error("Error occured when find an alert type", e);
	}
	return result;
    }
}
