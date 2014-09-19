package com.sybase365.mobiliser.web.util;

import java.util.List;

import com.btpnwow.core.customer.facade.contract.GetCustomerExResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Address;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerAttributes;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerIdentification;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Identity;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.ViewCustomer;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.ViewProfileResponse;


public class ConsumerPortalConverterUtils {

	public static CustomerRegistrationBean convertViewCustomertoCustomer(ViewProfileResponse response,
		ILookupMapUtility lookupMapUtility, BtpnMobiliserBasePage component) {

		ViewCustomer viewCustomer = response.getCoustomer();

		Address address = response.getAddress();

		List<CustomerIdentification> listIdentifications = response.getIdentification();
		List<Identity> listIdentity = response.getIdentity();

		final CustomerRegistrationBean customerBean = new CustomerRegistrationBean();

		customerBean.setActive(viewCustomer.isActive());
		customerBean.setCustomerNumber(viewCustomer.getCustomerNumber());
		customerBean.setBirthDateString(PortalUtils.getSaveDate(viewCustomer.getDateOfBirth()));

		// set the GL Code
		if (viewCustomer.getGlCode() != null) {
			String glCode = String.valueOf(viewCustomer.getGlCode());
			customerBean.setGlCodeId(new CodeValue(glCode, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_BTPN_GL_CODES, component),
				BtpnConstants.RESOURCE_BUNDLE_BTPN_GL_CODES + "." + glCode)));
		}

		// set the Language
		final String language = viewCustomer.getLanguage();
		customerBean.setLanguage(new CodeValue(language, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, component),
			BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG + "." + language)));

		// set the marital status
		final String maritalStatus = viewCustomer.getMaritalStatus();
		customerBean.setMaritalStatus(new CodeValue(maritalStatus, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS, component),
			BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS + "." + maritalStatus)));
		customerBean.setName(viewCustomer.getName());

		// set the nationality
		final String nationality = viewCustomer.getNationality();
		customerBean.setNationality(new CodeValue(nationality, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_NATIONALITY, component),
			BtpnConstants.RESOURCE_BUBDLE_NATIONALITY + "." + nationality)));

		// set the reciept mode
		final String recieptMode = String.valueOf(viewCustomer.getNotificationMode());
		customerBean.setReceiptMode(new CodeValue(recieptMode, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, component),
			BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE + "." + recieptMode)));

		customerBean.setPlaceOfBirth(viewCustomer.getPlaceOfBirth());
		customerBean.setHighRiskBusiness(setBooleanValues(viewCustomer.isHighRiskBusiness()));
		customerBean.setHighRiskCustomer(setBooleanValues(viewCustomer.isHighRiskCustomer()));
		customerBean.setOptimaActivated(setBooleanValues(viewCustomer.isIsOptimaActivated()));
		customerBean.setTaxExempted(setBooleanValues(viewCustomer.isIsTaxExempted()));
		customerBean.setPurposeOfTransaction(viewCustomer.getPurposeOfTransaction());

		// Setting the address
		customerBean.setEmailId(address.getEmail());
		customerBean.setEmployeeId(address.getEmployeeID());

		// Set the gender
		final String gender = String.valueOf(address.getGender());
		customerBean.setGender(new CodeValue(gender, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_GENDERS, component),
			BtpnConstants.RESOURCE_BUNDLE_GENDERS + "." + gender)));

		// Set the income
		final String income = address.getIncome();
		customerBean.setIncome(new CodeValue(income, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_INCOME, component),
			BtpnConstants.RESOURCE_BUBDLE_INCOME + "." + income)));

		// set the Industry sector
		final String employer = address.getIndustrySectorOfEmployer();
		customerBean.setIndustryOfEmployee(new CodeValue(employer, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE, component),
			BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE + "." + employer)));

		// Set the job
		final String job = address.getJob();
		customerBean.setJob(new CodeValue(job, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_JOB, component),
			BtpnConstants.RESOURCE_BUBDLE_JOB + "." + job)));
		customerBean.setMothersMaidenName(address.getMaidenName());

		// set the Occupation
		final String occupation = address.getOccupation();
		customerBean.setOccupation(new CodeValue(occupation, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_OCCUPATION, component),
			BtpnConstants.RESOURCE_BUNDLE_OCCUPATION + "." + occupation)));

		// set the purpose of account
		final String purposeOfAccount = address.getPurposeOfAccount();
		customerBean.setPurposeOfAccount(new CodeValue(purposeOfAccount, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT, component),
			BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT + "." + purposeOfAccount)));

		customerBean.setShortName(address.getShortName());

		// set the Source of Fund
		final String sourceOfFund = address.getSourceOfFund();
		customerBean.setSourceofFound(new CodeValue(sourceOfFund, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND, component),
			BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND + "." + sourceOfFund)));

		customerBean.setStreet1(address.getStreet1());
		customerBean.setStreet2(address.getStreet2());

		// Set the identification List
		final List<CustomerIdentification> identificationsList = listIdentifications;
		for (CustomerIdentification identification : identificationsList) {
			final int type = identification.getType();
			if (type == BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO) {
				customerBean.setMobileNumber(identification.getIdentification());
			} else if (type == BtpnConstants.IDENTIFICATION_TYPE_ATM_CARD_NO) {
				customerBean.setAtmCardNo(identification.getIdentification());
				customerBean.setAtmCardId(identification.getId());
			}
		}

		// set the identities
		final List<Identity> identitiesList = listIdentity;
		for (Identity identity : identitiesList) {
			customerBean.setCustomerId(String.valueOf(identity.getCustomerId()));
			customerBean.setExpireDateString(PortalUtils.getSaveDate(identity.getDateExpires()));
			customerBean.setIdentityId(identity.getId());
			customerBean.setIdCardNo(identity.getIdentity());
			customerBean.setIdentitiStatus(identity.getStatus());
			final String idType = String.valueOf(identity.getIdentityType());
			customerBean.setIdType(new CodeValue(idType, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_ID_TYPE, component),
				BtpnConstants.RESOURCE_BUBDLE_ID_TYPE + "." + idType)));
			break;
		}

		// Set Customer Attributes
		final CustomerAttributes attributes = response.getAttributes();
		if (attributes != null) {
			customerBean.setAgentCode(attributes.getAgentId());
			// Set Province
			final String province = attributes.getProvince();
			if (PortalUtils.exists(province)) {
				customerBean.setProvince(new CodeValue(province, BtpnUtils.getDropdownValueFromId(
					lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, component),
					BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE + "." + province)));
			}
			// Set City Attribute
			final String city = attributes.getCity();
			if (PortalUtils.exists(city)) {
				customerBean.setCity(new CodeValue(city, BtpnUtils.getDropdownValueFromId(
					lookupMapUtility.getLookupNamesMap(province, component), province + "." + city)));
			}
			// set Forecast
			final String forecast = attributes.getForecastTransaction();
			if (PortalUtils.exists(forecast)) {
				customerBean.setForeCastTransaction(new CodeValue(forecast, BtpnUtils.getDropdownValueFromId(
					lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS, component),
					BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS + "." + forecast)));
			}
			// Last Education
			final String lastEducation = attributes.getLastEducation();
			if (PortalUtils.exists(lastEducation)) {
				customerBean.setLastEducation(new CodeValue(lastEducation, BtpnUtils.getDropdownValueFromId(
					lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, component),
					BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS + "." + lastEducation)));
			}
			// Religion
			final String religion = attributes.getReligion();
			if (PortalUtils.exists(religion)) {
				customerBean.setReligion(new CodeValue(religion, BtpnUtils.getDropdownValueFromId(
					lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_RELIGION, component),
					BtpnConstants.RESOURCE_BUNDLE_RELIGION + "." + religion)));
			}
			customerBean.setMarketingSourceCode(attributes.getMarketingsourceCode());
			customerBean.setReferralNumber(attributes.getReferralNumber());
			customerBean.setTaxCardNumber(attributes.getTaxCardNumber());
			customerBean.setZipCode(attributes.getZipCode());
		}
		return customerBean;

	}
	
	
	public static CustomerRegistrationBean convertViewCustomertoCustomer(GetCustomerExResponse response,
			ILookupMapUtility lookupMapUtility, BtpnMobiliserBasePage component) {
			
			final CustomerRegistrationBean customerBean = new CustomerRegistrationBean();

			customerBean.setActive(true);
			customerBean.setName(response.getInformation().getName());
			customerBean.setCustomerNumber(response.getInformation().getCustomerNumber());
			customerBean.setBirthDateString(PortalUtils.getSaveDate(response.getInformation().getDateOfBirth()));

			// set the Language
			final String language = response.getInformation().getLanguage();
			customerBean.setLanguage(new CodeValue(language, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG, component),
				BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG + "." + language)));

			// set the marital status
			final String maritalStatus = response.getInformation().getMaritalStatus();
			customerBean.setMaritalStatus(new CodeValue(maritalStatus, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS, component),
				BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS + "." + maritalStatus)));

			// set the nationality
			final String nationality = response.getInformation().getNationality();;
			customerBean.setNationality(new CodeValue(nationality, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_NATIONALITY, component),
				BtpnConstants.RESOURCE_BUBDLE_NATIONALITY + "." + nationality)));

			// set the reciept mode
			final String recieptMode = String.valueOf(response.getInformation().getNotificationMode());
			customerBean.setReceiptMode(new CodeValue(recieptMode, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, component),
				BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE + "." + recieptMode)));

			customerBean.setPlaceOfBirth(response.getInformation().getPlaceOfBirth());
			customerBean.setHighRiskBusiness(setBooleanValues(response.getInformation().getHighRiskBusiness()));
			customerBean.setHighRiskCustomer(setBooleanValues(response.getInformation().getHighRiskCustomer()));
			customerBean.setOptimaActivated(setBooleanValues(response.getInformation().getOptimaActivated()));
			customerBean.setTaxExempted(setBooleanValues(response.getInformation().getTaxExempted()));
			customerBean.setPurposeOfTransaction(response.getInformation().getPurposeOfTransaction());

			// Setting the address
			customerBean.setEmailId(response.getInformation().getEmail());
			customerBean.setEmployeeId(response.getInformation().getEmployeeId());

			// Set the gender
			final String gender = String.valueOf(response.getInformation().getGender());
			customerBean.setGender(new CodeValue(gender, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_GENDERS, component),
				BtpnConstants.RESOURCE_BUNDLE_GENDERS + "." + gender)));

			// Set the income
			final String income = response.getInformation().getIncome();
			customerBean.setIncome(new CodeValue(income, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_INCOME, component),
				BtpnConstants.RESOURCE_BUBDLE_INCOME + "." + income)));

			// set the Industry sector
			final String employer = response.getInformation().getIndustrySectorOfEmployer();
			customerBean.setIndustryOfEmployee(new CodeValue(employer, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE, component),
				BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE + "." + employer)));

			// Set the job
			final String job = response.getInformation().getJob();
			customerBean.setJob(new CodeValue(job, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_JOB, component),
				BtpnConstants.RESOURCE_BUBDLE_JOB + "." + job)));
			customerBean.setMothersMaidenName(response.getInformation().getMothersMaidenName());

			// set the Occupation
			final String occupation = response.getInformation().getOccupation();
			customerBean.setOccupation(new CodeValue(occupation, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_OCCUPATION, component),
				BtpnConstants.RESOURCE_BUNDLE_OCCUPATION + "." + occupation)));

			// set the purpose of account
			final String purposeOfAccount = response.getInformation().getPurposeOfAccount();
			customerBean.setPurposeOfAccount(new CodeValue(purposeOfAccount, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT, component),
				BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT + "." + purposeOfAccount)));

			customerBean.setShortName(response.getInformation().getShortName());

			// set the Source of Fund
			final String sourceOfFund = response.getInformation().getSourceOfFund();
			customerBean.setSourceofFound(new CodeValue(sourceOfFund, BtpnUtils.getDropdownValueFromId(
				lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND, component),
				BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND + "." + sourceOfFund)));

			customerBean.setStreet1(response.getInformation().getStreet1());
			customerBean.setStreet2(response.getInformation().getStreet2());
			
			customerBean.setMobileNumber(response.getInformation().getMobileNumber());
			customerBean.setAtmCardNo(response.getInformation().getAtmCardNo());
			customerBean.setAtmCardId(response.getInformation().getId());
			
			customerBean.setIdCardNo(response.getInformation().getIdCardNo());
			// set ID Cart Type
			final String idCardType = String.valueOf(response.getInformation().getIdCardType());
			if (PortalUtils.exists(idCardType)) {
				customerBean.setIdType(new CodeValue(idCardType, BtpnUtils.getDropdownValueFromId(
						lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_ID_TYPE, component),
						BtpnConstants.RESOURCE_BUBDLE_ID_TYPE + "." + idCardType)));
			}

			// Set Agent Code Attributes
			customerBean.setAgentCode(response.getInformation().getAgentId());

			// Set Province
			final String province = response.getInformation().getProvince();
			if (PortalUtils.exists(province)) {
				customerBean.setProvince(new CodeValue(province, BtpnUtils.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
								BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, component),
								BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE + "." + province)));
			}
	
			// Set City Attribute
			final String city = response.getInformation().getCity();
			if (PortalUtils.exists(city)) {
				customerBean.setCity(new CodeValue(city, BtpnUtils.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
								province, component), province + "." + city)));
			}
	
			// set Forecast
			final String forecast = response.getInformation()
					.getForecastTransaction();
			if (PortalUtils.exists(forecast)) {
				customerBean.setForeCastTransaction(new CodeValue(forecast, BtpnUtils.getDropdownValueFromId(lookupMapUtility
					.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS, component),
					BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS + "." + forecast)));
			}
	
			// Last Education
			final String lastEducation = response.getInformation().getLastEducation();
			if (PortalUtils.exists(lastEducation)) {
				customerBean.setLastEducation(new CodeValue(lastEducation, BtpnUtils.getDropdownValueFromId(lookupMapUtility
					.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, component),
					BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS + "." + lastEducation)));
			}
	
			// Religion
			final String religion = response.getInformation().getReligion();
			if (PortalUtils.exists(religion)) {
				customerBean.setReligion(new CodeValue(religion, BtpnUtils
						.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
								BtpnConstants.RESOURCE_BUNDLE_RELIGION, component),
								BtpnConstants.RESOURCE_BUNDLE_RELIGION + "." + religion)));
			}
	
			customerBean.setMarketingSourceCode(response.getInformation().getMarketingSourceCode());
			customerBean.setReferralNumber(response.getInformation().getReferralNumber());
			customerBean.setTaxCardNumber(response.getInformation().getTaxCardNumber());
			customerBean.setZipCode(response.getInformation().getZipCode());
	
			return customerBean;

		}

	/**
	 * This method converts to CustomerRegistrationBean
	 */
	private static CodeValue setBooleanValues(final boolean yesOrNo) {
		if (yesOrNo) {
			return new CodeValue(BtpnConstants.YES_ID, BtpnConstants.YES_VALUE);
		}
		return new CodeValue(BtpnConstants.NO_ID, BtpnConstants.NO_VALUE);
	}

}
