package com.sybase365.mobiliser.web.application.clients;

import java.util.ArrayList;
import java.util.List;

import com.sybase365.mobiliser.framework.contract.v5_0.base.Locale;
import com.sybase365.mobiliser.mbanking.contract.v5_0.AddBankServicePackagesRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.AddBankServicePackagesResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.AddCustomerServicePackageRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.AddCustomerServicePackageResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.AddNumberRestrictionsRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.AddNumberRestrictionsResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.GetBankServicePackagesRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.GetBankServicePackagesResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.GetCustomerServicePackageRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.GetCustomerServicePackageResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.GetNumberRestrictionByOrgUnitRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.GetNumberRestrictionByOrgUnitResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.GetOptInSettingsRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.GetOptInSettingsResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.RemoveBankServicePackageRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.RemoveBankServicePackageResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.RemoveNumberRestrictionsRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.RemoveNumberRestrictionsResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.SendTestNotificationRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.SendTestNotificationResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.UpdateBankServicePackagesRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.UpdateBankServicePackagesResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.UpdateCustomerServicePackageRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.UpdateCustomerServicePackageResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.UpdateOptInSettingsRequest;
import com.sybase365.mobiliser.mbanking.contract.v5_0.UpdateOptInSettingsResponse;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.OptInSetting;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.RestrictedNumber;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.RestrictedNumberList;
import com.sybase365.mobiliser.mbanking.contract.v5_0.beans.ServicePackage;
import com.sybase365.mobiliser.mbanking.services.api.ICustomerEndpoint;
import com.sybase365.mobiliser.mbanking.services.api.INumberRestrictionsManagementEndpoint;
import com.sybase365.mobiliser.mbanking.services.api.IOptInManagementEndpoint;
import com.sybase365.mobiliser.mbanking.services.api.IServicePackageManagementEndpoint;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateFullCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateFullCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedAttachment;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedCredential;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedIdentification;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.web.application.model.IMobiliserServiceClientLogic;
import com.sybase365.mobiliser.web.application.pages.MobiliserBasePage;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * Client logic for Mobiliser mBanking web services handling
 * <p>
 * &copy; 2012 by Sybase, Inc.
 * </p>
 * 
 * @author <a href='mailto:msw@sybase.com'>Mark White</a>
 */
public class MBankingClientLogic extends BaseClientLogic implements
	IMobiliserServiceClientLogic {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(MBankingClientLogic.class);

    // Initialize the web service clients for this client logic hander
    private IServicePackageManagementEndpoint wsServicePackageMgmtEndpoint;
    private INumberRestrictionsManagementEndpoint wsNumberRestrictionsMgmtEndpoint;
    private IOptInManagementEndpoint wsOptInManagementEndpoint;
    private ICustomerEndpoint wsCustomerEndpoint;
    private boolean createStatus = false;

    /**
     * Wraps a GetCustomerServicePackageRequest WS call
     * 
     * @param alertTypeId
     * @return WS Response
     */
    public ServicePackage getCustomerServicePackage(long customerId) {
	ServicePackage result = null;
	try {
	    GetCustomerServicePackageRequest request = getNewMobiliserRequest(GetCustomerServicePackageRequest.class);
	    request.setCustomerId(Long.valueOf(customerId));
	    GetCustomerServicePackageResponse response = wsServicePackageMgmtEndpoint
		    .getCustomerServicePackage(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while getting customer service package");
		return result;
	    }
	    result = response.getServicePackage();
	} catch (Exception e) {
	    LOG.error(
		    "Error occured when find getting customer service package",
		    e);
	}
	return result;
    }

    /**
     * Wraps a CreateFullCustomerRequest WS call
     * 
     * @param customer
     * @return WS Response
     */
    public CustomerBean createFullCustomer(CustomerBean customer,
	    List<Attachment> attachmentsList, MobiliserBasePage basePage)
	    throws Exception {

	try {
	    LOG.debug("# MobiliserBasePage.createFullCustomer()");
	    CreateFullCustomerRequest crCustReq = getNewMobiliserRequest(CreateFullCustomerRequest.class);
	    customer.setDisplayName(basePage.createDisplayName(customer
		    .getAddress().getFirstName(), customer.getAddress()
		    .getLastName()));

	    crCustReq.setCustomer(Converter.getInstance(
		    basePage.getConfiguration()).getCustomerFromCustomerBean(
		    customer));

	    Address address = Converter.getInstance()
		    .getAddressFromAddressBean(customer.getAddress());

	    crCustReq.getAddresses().add(address);
	    CustomerAttachedCredential credential = new CustomerAttachedCredential();

	    if (PortalUtils.exists(customer.getPin())) {
		credential.setCredential(customer.getPin());
		credential.setType(Constants.CREDENTIAL_TYPE_PIN);
		credential.setStatus(Constants.CREDENTIAL_STATUS);
		crCustReq.getCredentials().add(credential);
	    }

	    if (PortalUtils.exists(customer.getPassword())) {
		credential = new CustomerAttachedCredential();
		credential.setCredential(customer.getPassword());
		credential.setType(Constants.CREDENTIAL_TYPE_PASSWORD);
		credential.setStatus(Constants.CREDENTIAL_STATUS);
		crCustReq.getCredentials().add(credential);
	    }

	    CustomerAttachedIdentification custIdentification = null;
	    Identification identification = null;
	    if (PortalUtils.exists(customer.getUserName())) {
		identification = Converter.getInstance(
			basePage.getConfiguration())
			.getUsernameIdFromCustomerBean(customer);
		custIdentification = new CustomerAttachedIdentification();
		custIdentification.setIdentification(identification
			.getIdentification());
		custIdentification.setProvider(identification.getProvider());
		custIdentification.setStatus(identification.getStatus());
		custIdentification.setType(identification.getType());
		crCustReq.getIdentifications().add(custIdentification);
	    }

	    if (PortalUtils.exists(customer.getMsisdn())) {
		identification = Converter.getInstance(
			basePage.getConfiguration())
			.getMsisdnIdFromCustomerBean(customer);
		custIdentification = new CustomerAttachedIdentification();
		custIdentification.setIdentification(identification
			.getIdentification());
		custIdentification.setProvider(identification.getProvider());
		custIdentification.setStatus(identification.getStatus());
		custIdentification.setType(identification.getType());
		custIdentification.setOrgUnit(Constants.DEFAULT_ORGUNIT);
		crCustReq.getIdentifications().add(custIdentification);
	    }

	    List<CustomerAttachedAttachment> atmtList = new ArrayList<CustomerAttachedAttachment>();
	    CustomerAttachedAttachment attachment;

	    if (PortalUtils.exists(attachmentsList)) {
		for (Attachment att : attachmentsList) {
		    attachment = new CustomerAttachedAttachment();
		    attachment.setAttachmentType(att.getAttachmentType());
		    attachment.setContent(att.getContent());
		    attachment.setContentType(att.getContentType());
		    attachment.setName(att.getName());
		    attachment.setStatus(att.getStatus());
		    atmtList.add(attachment);
		}
		crCustReq.getAttachments().addAll(atmtList);
	    }

	    CreateFullCustomerResponse response = wsCustomerEndpoint
		    .createFullCustomer(crCustReq);

	    if (response.getStatus().getCode() == Constants.PENDING_APPROVAL_ERROR) {
		customer.setPendingApproval(true);
		basePage.setCreateStatus(false);
		return customer;
	    }

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while creating pending customer");
		basePage.setCreateStatus(false);
		return null;
	    }
	    Address createdaddress = basePage.getCustomerAddress(response
		    .getCustomerId());
	    basePage.setCreateStatus(true);
	    customer.getAddress().setId(createdaddress.getId());
	    customer.setId(response.getCustomerId());
	    customer.setBlackListReason(0);
	    return customer;

	} catch (Exception e) {
	    throw e;
	}

    }

    /**
     * Wraps a AddCustomerServicePackageRequest WS call
     * 
     * @param customerId
     * @param servicePackageId
     */
    public int addCustomerServicePackage(long customerId,
	    String servicePackageId) {
	int status = -1;
	try {
	    AddCustomerServicePackageRequest request = getNewMobiliserRequest(AddCustomerServicePackageRequest.class);
	    request.setServicePackageId(servicePackageId);
	    request.setCustomerId(customerId);
	    AddCustomerServicePackageResponse response = wsServicePackageMgmtEndpoint
		    .addCustomerServicePackage(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# Error occured when adding customer service package");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("Error occured when adding customer service package", e);
	}
	return status;
    }

    /**
     * Wraps a UpdateCustomerServicePackageRequest WS call
     * 
     * @param customerId
     * @param oldServicePackageId
     * @param newServicePackageId
     */
    public int updateCustomerServicePackage(long customerId,
	    String oldServicePackageId, String newServicePackageId) {
	int status = -1;
	try {
	    UpdateCustomerServicePackageRequest request = getNewMobiliserRequest(UpdateCustomerServicePackageRequest.class);
	    request.setCustomerId(customerId);
	    request.setOldServicePackageId(oldServicePackageId);
	    request.setNewServicePackageId(newServicePackageId);
	    UpdateCustomerServicePackageResponse response = wsServicePackageMgmtEndpoint
		    .updateCustomerServicePackage(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# Error occured when updating customer service package");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("# Error occured when updating customer service package",
		    e);
	}
	return status;
    }

    /**
     * Wraps a GetBankServicePackagesRequest WS call
     * 
     * @param orgUnitId
     */
    public List<ServicePackage> getBankServicePackages(String orgUnitId) {
	List<ServicePackage> result = null;
	try {
	    GetBankServicePackagesRequest request = getNewMobiliserRequest(GetBankServicePackagesRequest.class);
	    request.setOrgUnit(orgUnitId);
	    GetBankServicePackagesResponse response = wsServicePackageMgmtEndpoint
		    .getBankServicePackage(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while getting bank service packages");
		return result;
	    }
	    result = response.getServicePackageList();
	} catch (Exception e) {
	    LOG.error("Error occured when find getting bank service packages",
		    e);
	}
	return result;
    }

    /**
     * Wraps a UpdateBankServicePackagesRequest WS call
     * 
     * @param servicePackage
     */
    public int updateBankServicePackage(ServicePackage servicePackage) {
	int status = -1;
	try {
	    UpdateBankServicePackagesRequest request = getNewMobiliserRequest(UpdateBankServicePackagesRequest.class);
	    request.setServicePackage(servicePackage);
	    UpdateBankServicePackagesResponse response = wsServicePackageMgmtEndpoint
		    .updateBankServicePackage(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error occured when updating bank service packages");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("Error occured when updating bank service packages", e);
	}
	return status;
    }

    /**
     * Wraps a AddBankServicePackagesRequest WS call
     * 
     * @param servicePackage
     */
    public int addBankServicePackage(ServicePackage servicePackage) {
	int status = -1;
	try {
	    AddBankServicePackagesRequest request = getNewMobiliserRequest(AddBankServicePackagesRequest.class);
	    request.setServicePackage(servicePackage);
	    servicePackage.setOrgUnitId(Constants.DEFAULT_ORGUNIT);
	    AddBankServicePackagesResponse response = wsServicePackageMgmtEndpoint
		    .addBankServicePackage(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error occured when adding bank service packages");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("Error occured when adding bank service packages", e);
	}
	return status;
    }

    /**
     * Wraps a RemoveBankServicePackageRequest WS call
     * 
     * @param servicePackage
     */
    public int removeBankServicePackage(ServicePackage servicePackage) {
	int status = -1;
	try {
	    RemoveBankServicePackageRequest request = getNewMobiliserRequest(RemoveBankServicePackageRequest.class);
	    request.setServicePackageId(servicePackage.getServicePackageId());
	    request.setOrgUnitId(servicePackage.getOrgUnitId());
	    RemoveBankServicePackageResponse response = wsServicePackageMgmtEndpoint
		    .deleteBankServicePackage(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error occured when deleting bank service packages");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("Error occured when deleting bank service packages", e);
	}
	return status;
    }

    /**
     * Wraps a SendTestNotificationRequest WS call
     * 
     * @param messageId
     * @param phoneNumber
     * @param customerId
     */
    public int sendTestNotification(String messageId, Long customerId,
	    String identification, int identificationType, String orgUnitId,
	    Locale locale) {
	int status = -1;
	try {
	    SendTestNotificationRequest request = getNewMobiliserRequest(SendTestNotificationRequest.class);
	    request.setMessageTemplate(messageId);
	    request.setCustomerIdSender(customerId);
	    request.setIdentification(identification);
	    request.setIdentificationType(identificationType);
	    request.setBankId(orgUnitId);

	    // country code is not there while register through SPM
	    if (locale.getCountry() == null) {
		locale.setCountry(Constants.DEFAULT_COUNTRY_ID);
	    }
	    // language code is not mandatory while CST register for a consumer
	    if (locale.getLanguage() == null) {
		locale.setLanguage(Constants.DEFAULT_LANGUAGE);
	    }

	    request.setLocale(locale);
	    SendTestNotificationResponse response = wsNumberRestrictionsMgmtEndpoint
		    .sendTestNotification(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while sending test notification");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("An error occurred while sending test notification", e);
	    status = -1;
	}
	return status;
    }

    /**
     * Wraps a GetNumberRestrictionByOrgUnitRequest WS call
     * 
     * @param orgUnitId
     */
    public List<RestrictedNumber> getRestrictedNumbersByOrgUnit(String orgUnitId) {
	List<RestrictedNumber> result = null;
	try {
	    GetNumberRestrictionByOrgUnitRequest request = getNewMobiliserRequest(GetNumberRestrictionByOrgUnitRequest.class);
	    request.setOrgUnit(orgUnitId);
	    GetNumberRestrictionByOrgUnitResponse response = wsNumberRestrictionsMgmtEndpoint
		    .getNumberRestrictionByOrgUnit(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while fetching restricted numbers");
	    }
	    result = response.getRestrictedNumbers().getRestrictedNumber();
	} catch (Exception e) {
	    LOG.error("An error occurred while fetching restricted numbers", e);
	}
	return result;
    }

    public int addRestrictedNumbers(RestrictedNumberList restrictedNumbers) {
	int status = -1;
	try {
	    AddNumberRestrictionsRequest request = getNewMobiliserRequest(AddNumberRestrictionsRequest.class);
	    request.setRestrictedNumbers(restrictedNumbers);
	    request.setOrgUnit(Constants.DEFAULT_ORGUNIT);
	    AddNumberRestrictionsResponse response = wsNumberRestrictionsMgmtEndpoint
		    .addNumberRestrictions(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while adding restricted numbers");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("An error occurred while adding restricted numbers", e);
	}
	return status;
    }

    public int removeRestrictedNumbers(RestrictedNumberList restrictedNumbers) {
	int status = -1;
	try {
	    RemoveNumberRestrictionsRequest request = getNewMobiliserRequest(RemoveNumberRestrictionsRequest.class);
	    request.setRestrictedNumbers(restrictedNumbers);
	    request.setOrgUnit(Constants.DEFAULT_ORGUNIT);
	    RemoveNumberRestrictionsResponse response = wsNumberRestrictionsMgmtEndpoint
		    .removeNumberRestrictions(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred while removing restricted numbers");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("An error occurred while removing restricted numbers", e);
	}
	return status;
    }

    /**
     * Wraps a GetOptInSettingsRequest WS call
     * 
     * @param origin
     * @return WS Response
     */

    public List<OptInSetting> getOptInSettings() {
	List<OptInSetting> optInsettingList = null;
	try {
	    GetOptInSettingsRequest request = getNewMobiliserRequest(GetOptInSettingsRequest.class);
	    GetOptInSettingsResponse response = wsOptInManagementEndpoint
		    .getOptInSettings(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while getting OptInSettings");
	    }

	    if (response != null && response.getOptInSettingsList() != null) {
		optInsettingList = response.getOptInSettingsList();

	    }

	} catch (Exception e) {
	    LOG.error("An error occurred while getting OptInSettings", e);
	}
	return optInsettingList;
    }

    /**
     * Wraps a UpdateOptInSettingsRequest WS call
     * 
     * @param UpdateOptInSettingsRequest
     */

    public int updateOptInSettings(OptInSetting optInSettings) {
	int status = -1;
	try {
	    UpdateOptInSettingsRequest updateOptInSettingsRequest = getNewMobiliserRequest(UpdateOptInSettingsRequest.class);
	    updateOptInSettingsRequest.setSetting(optInSettings);
	    UpdateOptInSettingsResponse response = wsOptInManagementEndpoint
		    .updateOptInSettings(updateOptInSettingsRequest);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while updating OptInSettings");
	    }
	    status = response.getStatus().getCode();
	} catch (Exception e) {
	    LOG.error("An error occurred while updating OptInSettings", e);
	}
	return status;
    }

    public void setWsServicePackageMgmtEndpoint(
	    IServicePackageManagementEndpoint value) {
	this.wsServicePackageMgmtEndpoint = value;
    }

    public void setWsNumberRestrictionsMgmtEndpoint(
	    INumberRestrictionsManagementEndpoint wsNumberRestrictionsMgmtEndpoint) {
	this.wsNumberRestrictionsMgmtEndpoint = wsNumberRestrictionsMgmtEndpoint;
    }

    public void setWsOptInManagementEndpoint(
	    IOptInManagementEndpoint wsOptInManagementEndpoint) {
	this.wsOptInManagementEndpoint = wsOptInManagementEndpoint;
    }

    public IOptInManagementEndpoint getWsOptInManagementEndpoint() {
	return wsOptInManagementEndpoint;
    }

    public void setWsCustomerEndpoint(ICustomerEndpoint wsCustomerEndpoint) {
	this.wsCustomerEndpoint = wsCustomerEndpoint;
    }

    public ICustomerEndpoint getWsCustomerEndpoint() {
	return wsCustomerEndpoint;
    }

    public void setCreateStatus(boolean createStatus) {
	this.createStatus = createStatus;
    }

    public boolean isCreateStatus() {
	return createStatus;
    }
}
