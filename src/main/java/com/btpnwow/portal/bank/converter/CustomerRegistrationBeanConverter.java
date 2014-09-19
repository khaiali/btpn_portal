package com.btpnwow.portal.bank.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;

import com.btpnwow.core.customer.facade.contract.CustomerAttachmentType;
import com.btpnwow.core.customer.facade.contract.CustomerAttachmentWrkType;
import com.btpnwow.core.customer.facade.contract.CustomerFindViewType;
import com.btpnwow.core.customer.facade.contract.CustomerInformationType;
import com.btpnwow.core.customer.facade.contract.CustomerInformationWrkType;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.NotificationAttachmentsBean;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;

public abstract class CustomerRegistrationBeanConverter {
	
	public static CustomerInformationWrkType toContractWrk(CustomerRegistrationBean customer, String countryCode, String orgUnitId, Long parentId) {
		
		CustomerInformationWrkType ccust = new CustomerInformationWrkType();
		ccust.setAgentId(customer.getAgentCode());
		ccust.setAtmCardNo(customer.getAtmCardNo());
		ccust.setCity(customer.getCity().getId());
		ccust.setCustomerType(PortalUtils.exists(customer.getProductType()) ? Integer.valueOf(customer.getProductType().getId()) : null);
		ccust.setDateOfBirth(FormatUtils.getSaveXMLGregorianCalendar(customer.getBirthDateString()));
		ccust.setEmail(customer.getEmailId());
		ccust.setEmployeeId(customer.getEmployeeId());
		ccust.setForecastTransaction(customer.getForeCastTransaction() == null ? null : customer.getForeCastTransaction().getId());
		ccust.setGender(Integer.valueOf(customer.getGender().getId()));
		ccust.setHighRiskBusiness(Boolean.valueOf(BtpnConstants.YES_ID.equalsIgnoreCase(customer.getHighRiskBusiness().getId())));
		ccust.setHighRiskCustomer(Boolean.valueOf(BtpnConstants.YES_ID.equalsIgnoreCase(customer.getHighRiskCustomer().getId())));
		ccust.setId(customer.getCustomerId() == null ? null : Long.valueOf(customer.getCustomerId()));
		ccust.setIdCardExpirationDate(FormatUtils.getSaveXMLGregorianCalendar(customer.getExpireDateString()));
		ccust.setIdCardNo(customer.getIdCardNo());
		ccust.setIdCardType(Integer.valueOf(customer.getIdType().getId()));
		ccust.setIncome(customer.getIncome().getId());
		ccust.setIndustrySectorOfEmployer(customer.getIndustryOfEmployee().getId());
		ccust.setJob(customer.getJob().getId());
		ccust.setLanguage(customer.getLanguage().getId());
		ccust.setLastEducation(customer.getLastEducation() == null ? null : customer.getLastEducation().getId());
		ccust.setMaritalStatus(customer.getMaritalStatus().getId());
		ccust.setMarketingSourceCode(customer.getMarketingSourceCode());
		ccust.setMobileNumber(new PhoneNumber(customer.getMobileNumber(), countryCode).getInternationalFormat());
		ccust.setMothersMaidenName(customer.getMothersMaidenName());
		ccust.setName(customer.getName());
		ccust.setNationality(customer.getNationality().getId());
		ccust.setNotificationMode(Integer.valueOf(customer.getReceiptMode().getId()));
		ccust.setOccupation(customer.getOccupation().getId());
		ccust.setOptimaActivated(Boolean.valueOf(BtpnConstants.YES_ID.equalsIgnoreCase(customer.getOptimaActivated().getId())));
		ccust.setOrgUnitId(orgUnitId);
		ccust.setParentId(parentId);
		ccust.setPlaceOfBirth(customer.getPlaceOfBirth());
		ccust.setProvince(customer.getProvince() == null ? null : customer.getProvince().getId());
		ccust.setPurposeOfAccount(customer.getPurposeOfAccount().getId());
		ccust.setPurposeOfTransaction(customer.getPurposeOfTransaction());
		ccust.setReferralNumber(customer.getReferralNumber());
		ccust.setReligion(customer.getReligion() == null ? null : customer.getReligion().getId());
		ccust.setShortName(customer.getShortName());
		ccust.setSourceOfFund(customer.getSourceofFound() == null ? null : customer.getSourceofFound().getId());
		ccust.setStatus(customer.getBlackListReason() == null ? null : Integer.valueOf(customer.getBlackListReason().getId()));
		ccust.setStreet1(customer.getStreet1());
		ccust.setStreet2(customer.getStreet2());
		ccust.setTaxCardNumber(customer.getTaxCardNumber());
		ccust.setTaxExempted(Boolean.valueOf(BtpnConstants.YES_ID.equalsIgnoreCase(customer.getTaxExempted().getId())));
		ccust.setTerritoryCode(customer.getTerritoryCode());
		ccust.setZipCode(customer.getZipCode());
		
		List<NotificationAttachmentsBean> attachments = customer.getAttachmentsList();
		
		if ((attachments != null) && !attachments.isEmpty()) {
			for (NotificationAttachmentsBean e : attachments) {
				CustomerAttachmentWrkType cattach = new CustomerAttachmentWrkType();
				
				cattach.setContent(e.getFileContent());
				cattach.setContentType(e.getContentType());
				cattach.setName(e.getFileName());
				
				ccust.getAttachment().add(cattach);
			}
		}
		
		return ccust;
	}
	
	public static CustomerRegistrationBean fromContract(CustomerFindViewType contract, ILookupMapUtility lookupMapUtility, Component component) {
		
		CustomerRegistrationBean bean = new CustomerRegistrationBean();
		
		bean.setCustomerTypeId(contract.getCustomerType());
		bean.setCustomerType(MobiliserUtils.getValue(
				BtpnConstants.RESOURCE_BUNDLE_CUSTOMER_TYPE, Integer.toString(contract.getCustomerType()), lookupMapUtility, component));
		bean.setCustomerId(Long.toString(contract.getId()));
		bean.setMobileNumber(contract.getMobileNumber());
		bean.setName(contract.getName());
		
		return bean;
	}
	
	public static CustomerRegistrationBean fromContractWrk(CustomerInformationWrkType customer, ILookupMapUtility lookupMapUtility, Component component) {
		CustomerRegistrationBean bean = new CustomerRegistrationBean();
		
		bean.setActive(true);
		bean.setAgentCode(customer.getAgentId());
		bean.setAtmCardNo(customer.getAtmCardNo());
		bean.setBirthDateString(PortalUtils.getSaveDate(customer.getDateOfBirth()));
		bean.setDateOfBirth(customer.getDateOfBirth() == null ? null : PortalUtils.getFormattedDate(customer.getDateOfBirth(), component.getLocale()));
		if (customer.getProvince() != null) {
			bean.setCity(MobiliserUtils.getCodeValue(customer.getProvince(), customer.getCity(), lookupMapUtility, component));
		}
		bean.setBlackListReson(customer.getStatus() == null ? -1 : customer.getStatus().intValue());
		bean.setBlackListReason(MobiliserUtils.getCodeValue("blackListReasons", customer.getStatus(), lookupMapUtility, component));
		bean.setCustomerId(customer.getId().toString());
		bean.setCustomerNumber(customer.getCustomerNumber());
		bean.setCustomerStatus(bean.getBlackListReason());
		bean.setEmailId(customer.getEmail());
		bean.setEmployeeId(customer.getEmployeeId());
		bean.setExpireDateString(PortalUtils.getSaveDate(customer.getIdCardExpirationDate()));
		bean.setForeCastTransaction(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS, customer.getForecastTransaction(), lookupMapUtility, component));
		bean.setGender(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_GENDERS, customer.getGender(), lookupMapUtility, component));
		bean.setHighRiskBusiness(MobiliserUtils.getCodeValue(customer.getHighRiskBusiness()));
		bean.setHighRiskCustomer(MobiliserUtils.getCodeValue(customer.getHighRiskCustomer()));
		bean.setIdCardNo(customer.getIdCardNo());
		bean.setIdType(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_ID_TYPE, customer.getIdCardType(), lookupMapUtility, component));
		bean.setIdExpirationDate(customer.getIdCardExpirationDate() == null ? null : PortalUtils.getFormattedDate(customer.getIdCardExpirationDate(), component.getLocale()));
		bean.setIncome(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_INCOME, customer.getIncome(), lookupMapUtility, component));
		bean.setIndustryOfEmployee(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE, customer.getIndustrySectorOfEmployer(), lookupMapUtility, component));
		bean.setJob(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_JOB, customer.getJob(), lookupMapUtility, component));
		bean.setLanguage(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, customer.getLanguage(), lookupMapUtility, component));
		bean.setLastEducation(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, customer.getLastEducation(), lookupMapUtility, component));
		bean.setMaritalStatus(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS, customer.getMaritalStatus(), lookupMapUtility, component));
		bean.setMarketingSourceCode(customer.getMarketingSourceCode());
		bean.setMobileNumber(customer.getMobileNumber());
		bean.setMothersMaidenName(customer.getMothersMaidenName());
		bean.setName(customer.getName());
		bean.setNationality(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_NATIONALITY, customer.getNationality(), lookupMapUtility, component));
		bean.setOccupation(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_OCCUPATION, customer.getOccupation(), lookupMapUtility, component));
		bean.setOptimaActivated(MobiliserUtils.getCodeValue(customer.getOptimaActivated()));
		bean.setParentId(customer.getParentId() == null ? -1L : customer.getParentId().longValue());
		bean.setPlaceOfBirth(customer.getPlaceOfBirth());
		bean.setProductCategory(customer.getCustomerTypeCategory() == null ? -1 : customer.getCustomerTypeCategory().intValue());
		bean.setProductType(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_PRODUCTS_TYPES, customer.getCustomerType(), lookupMapUtility, component));
		bean.setProduct(bean.getProductType() == null ? null : bean.getProductType().getIdAndValue());
		bean.setProvince(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, customer.getProvince(), lookupMapUtility, component));
		bean.setPurposeOfAccount(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT, customer.getPurposeOfAccount(), lookupMapUtility, component));
		bean.setPurposeOfTransaction(customer.getPurposeOfTransaction());
		bean.setReceiptMode(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, customer.getNotificationMode(), lookupMapUtility, component));
		bean.setReferralNumber(customer.getReferralNumber());
		bean.setReligion(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_RELIGION, customer.getReligion(), lookupMapUtility, component));
		bean.setShortName(customer.getShortName());
		bean.setSourceofFound(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND, customer.getSourceOfFund(), lookupMapUtility, component));
		bean.setStatus(bean.getBlackListReason() == null ? null : bean.getBlackListReason().getValue());
		bean.setStreet1(customer.getStreet1());
		bean.setStreet2(customer.getStreet2());
		bean.setSvaNumber(customer.getMobileNumber());
		bean.setTaxCardNumber(customer.getTaxCardNumber());
		bean.setTaxExempted(MobiliserUtils.getCodeValue(customer.getTaxExempted()));
		bean.setTerritoryCode(customer.getTerritoryCode());
		bean.setZipCode(customer.getZipCode());

		List<CustomerAttachmentWrkType> attachment = customer.getAttachment();
		
		if ((attachment != null) && !attachment.isEmpty()) {
			List<NotificationAttachmentsBean> battachment = new ArrayList<NotificationAttachmentsBean>();
			
			for (CustomerAttachmentWrkType e : attachment) {
				NotificationAttachmentsBean be = new NotificationAttachmentsBean();
				be.setContentType(e.getContentType());
				be.setFileContent(e.getContent());
				be.setFileName(e.getName());
				
				battachment.add(be);
			}
			
			bean.setAttachmentsList(battachment);
		}
		
		return bean;
	}
	
	public static CustomerRegistrationBean fromContract(CustomerInformationType customer, ILookupMapUtility lookupMapUtility, Component component) {
		CustomerRegistrationBean bean = new CustomerRegistrationBean();

		bean.setActive(true);
		bean.setAgentCode(customer.getAgentId());
		bean.setAtmCardNo(customer.getAtmCardNo());
		bean.setBirthDateString(PortalUtils.getSaveDate(customer.getDateOfBirth()));
		bean.setDateOfBirth(customer.getDateOfBirth() == null ? null : PortalUtils.getFormattedDate(customer.getDateOfBirth(), component.getLocale()));
		bean.setBlackListReson(customer.getStatus().intValue());
		bean.setBlackListReason(MobiliserUtils.getCodeValue("blackListReasons", customer.getStatus(), lookupMapUtility, component));
		if (customer.getProvince() != null) {
			bean.setCity(MobiliserUtils.getCodeValue(customer.getProvince(), customer.getCity(), lookupMapUtility, component));
		}
		bean.setCustomerId(customer.getId().toString());
		bean.setCustomerNumber(customer.getCustomerNumber());
		bean.setCustomerStatus(bean.getBlackListReason());
		bean.setEmailId(customer.getEmail());
		bean.setEmployeeId(customer.getEmployeeId());
		bean.setExpireDateString(PortalUtils.getSaveDate(customer.getIdCardExpirationDate()));
		bean.setForeCastTransaction(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS, customer.getForecastTransaction(), lookupMapUtility, component));
		bean.setGender(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_GENDERS, customer.getGender(), lookupMapUtility, component));
		bean.setHighRiskBusiness(MobiliserUtils.getCodeValue(customer.getHighRiskBusiness()));
		bean.setHighRiskCustomer(MobiliserUtils.getCodeValue(customer.getHighRiskCustomer()));
		bean.setIdCardNo(customer.getIdCardNo());
		bean.setIdType(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_ID_TYPE, customer.getIdCardType(), lookupMapUtility, component));
		bean.setIdExpirationDate(customer.getIdCardExpirationDate() == null ? null : PortalUtils.getFormattedDate(customer.getIdCardExpirationDate(), component.getLocale()));
		bean.setIncome(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_INCOME, customer.getIncome(), lookupMapUtility, component));
		bean.setIndustryOfEmployee(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE, customer.getIndustrySectorOfEmployer(), lookupMapUtility, component));
		bean.setJob(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_JOB, customer.getJob(), lookupMapUtility, component));
		bean.setLanguage(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, customer.getLanguage(), lookupMapUtility, component));
		bean.setLastEducation(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, customer.getLastEducation(), lookupMapUtility, component));
		bean.setMaritalStatus(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS, customer.getMaritalStatus(), lookupMapUtility, component));
		bean.setMarketingSourceCode(customer.getMarketingSourceCode());
		bean.setMobileNumber(customer.getMobileNumber());
		bean.setMothersMaidenName(customer.getMothersMaidenName());
		bean.setName(customer.getName());
		bean.setNationality(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_NATIONALITY, customer.getNationality(), lookupMapUtility, component));
		bean.setOccupation(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_OCCUPATION, customer.getOccupation(), lookupMapUtility, component));
		bean.setOptimaActivated(MobiliserUtils.getCodeValue(customer.getOptimaActivated()));
		bean.setParentId(customer.getParentId() == null ? -1L : customer.getParentId().longValue());
		bean.setPlaceOfBirth(customer.getPlaceOfBirth());
		bean.setProductCategory(customer.getCustomerTypeCategory().intValue());
		bean.setProductType(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_PRODUCTS_TYPES, customer.getCustomerType(), lookupMapUtility, component));
		bean.setProduct(bean.getProductType() == null ? null : bean.getProductType().getIdAndValue());
		bean.setProvince(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, customer.getProvince(), lookupMapUtility, component));
		bean.setPurposeOfAccount(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT, customer.getPurposeOfAccount(), lookupMapUtility, component));
		bean.setPurposeOfTransaction(customer.getPurposeOfTransaction());
		bean.setReceiptMode(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, customer.getNotificationMode(), lookupMapUtility, component));
		bean.setReferralNumber(customer.getReferralNumber());
		bean.setReligion(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_RELIGION, customer.getReligion(), lookupMapUtility, component));
		bean.setShortName(customer.getShortName());
		bean.setSourceofFound(MobiliserUtils.getCodeValue(
				BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND, customer.getSourceOfFund(), lookupMapUtility, component));
		bean.setStatus(bean.getBlackListReason() == null ? null : bean.getBlackListReason().getValue());
		bean.setStreet1(customer.getStreet1());
		bean.setStreet2(customer.getStreet2());
		bean.setSvaNumber(customer.getMobileNumber());
		bean.setTaxCardNumber(customer.getTaxCardNumber());
		bean.setTaxExempted(MobiliserUtils.getCodeValue(customer.getTaxExempted()));
		bean.setTerritoryCode(customer.getTerritoryCode());
		bean.setZipCode(customer.getZipCode());

		List<CustomerAttachmentType> attachment = customer.getAttachment();
		
		if ((attachment != null) && !attachment.isEmpty()) {
			List<NotificationAttachmentsBean> battachment = new ArrayList<NotificationAttachmentsBean>();
			
			for (CustomerAttachmentType e : attachment) {
				NotificationAttachmentsBean be = new NotificationAttachmentsBean();
				be.setContentType(e.getContentType());
				be.setFileContent(e.getContent());
				be.setFileName(e.getName());
				
				battachment.add(be);
			}
			
			bean.setAttachmentsList(battachment);
		}
		
		return bean;
	}
}
