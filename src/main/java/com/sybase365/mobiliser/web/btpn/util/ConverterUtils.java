package com.sybase365.mobiliser.web.btpn.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.bulk.facade.contract.BulkFileType;
import com.btpnwow.core.bulk.facade.contract.BulkFileWrkType;
//import com.btpnwow.core.account.facade.contract.TransactionHistoryFindView;
import com.btpnwow.core.debit.services.contract.v1_0.DebitTransactionType;
import com.btpnwow.core.fee.facade.contract.UseCaseFeeFindViewType;
import com.btpnwow.core.fee.facade.contract.wrk.UseCaseFeeWrkFindViewType;
import com.btpnwow.core.gl.facade.contract.FoundGLItemType;
import com.btpnwow.core.gl.facade.contract.wrk.FoundGLWrkItemType;
import com.btpnwow.core.interest.facade.contract.InterestFindViewType;
import com.btpnwow.core.interest.facade.contract.InterestTaxFindViewType;
import com.btpnwow.core.interest.facade.contract.wrk.InterestTaxWrkFindViewType;
import com.btpnwow.core.interest.facade.contract.wrk.InterestWrkFindViewType;
import com.btpnwow.core.limitex.services.contract.v1_0.LimitExType;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.billpayment.AdviceBean;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.billpayment.BillPayment;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.bulkfileprocessing.File;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.changemsisdn.ChangeMsisdnDetails;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.changemsisdn.PendingApprovalChangeMsisdn;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.Address;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.Customer;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.CustomerAttachedAttachment;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.CustomerAttributes;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.CustomerIdentification;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.CustomerType;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.Identity; 
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Address;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Customer;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerAttachedAttachment;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerAttributes;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerIdentification;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerType;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Identity;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.FeeConfig;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.FeeConfigDetail;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.PendingApprovalFee;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.PendingApprovalFeeDetail;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.UseCaseFee;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.VendorFeeSharing;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.VendorFeeSummary;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fundtransfer.StandingInstructionResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.generalledger.GeneralLedger;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.generalledger.GeneralLedgerSummary;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.generalledger.PendingApprovalGeneralLedger;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.holidaycalendar.HolidayCalendar;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.holidaycalendar.PendingApprovalHolidayCalendar;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.limit.Limit;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.limit.LimitSummary;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.limit.PendingApprovalLimit;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.limit.PendingApprovalLimitDetail;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.product.InterestSlab;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.product.PendingApprovalProduct;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.product.Product;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.registration.BankStaff;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.registration.PendingApprovalUser;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.BankTransactionReportRequestBean;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.BankTransactionReportResponseBean;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.BankUserProfile;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.ConsumerPortalTransactionRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.ConsumerPortalTransactionResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.FileErrorData;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.SearchCustomerResult;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.SubAccountResponseBean;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.support.TransactionCustomer;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.topup.RequestData;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.transactiongl.PendingApprovalTransactionGL;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.transactiongl.TransactionGLDetail;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.transactionreversal.PendingApprovalTransactionReversal;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.transactionreversal.TransactionReversalDetail;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.ConfirmBillPayResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.billpayment.PerformBillPayResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.FindProductsResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.registration.CreateCustomerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.registration.FindPendingUsersResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.BankTransactionReportRequest;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashinBean;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveCustomerBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeConfirmBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveHolidayBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveMsisdnBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankTransactionDetailsReportRequestBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ConsumerTransactionBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ConsumerTransactionRequestBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.HolidayListBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsApproveBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsApproveRangeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsRangeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.NotificationAttachmentsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.SalaryDataBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.SalaryDataErrorBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionDetailsReportAgentBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionGeneralLedgerBean;
//import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionHistoryBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionReversalBean;
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirtimePerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.beans.ManualAdviceBean;
import com.sybase365.mobiliser.web.btpn.consumer.beans.StandingInstructionsBean;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.Address;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.Customer;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.CustomerAttachedAttachment;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.CustomerAttributes;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.CustomerIdentification;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.CustomerType;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.customer.Identity;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.registration.BankStaff;
//import com.btpnwow.core.registration.services.contract.v1_0.beans.registration.PendingApprovalUser;
//import com.btpnwow.core.registration.services.contract.v1_0.registration.CreateCustomerRequest;
//import com.btpnwow.core.registration.services.contract.v1_0.registration.FindPendingUsersResponse;

/**
 * This class consists of Converter utility methods for the btpn applications.
 * 
 * @author Vikram Gunda
 */
public class ConverterUtils {
	
	private static final Logger log = LoggerFactory.getLogger(ConverterUtils.class);

	/**
	 * Private Constructor for Converter Utils.
	 */
	private ConverterUtils() {

	}

	/**
	 * This method converts to create consumer request from
	 * CustomerRegistrationBean.
	 * 
	 * @param request
	 *            CreateCustomerRequest for Registering Consumer/Agent/Top
	 *            Agent/Sub Agent.
	 * @param customer
	 *            CustomerRegistrationBean from which request is populated.
	 * @param timeZone
	 *            Timezone to be used.
	 * @param countryCode
	 *            Country Code to be used.
	 */
	public static void convertToCreateCustomerRequest(
			final CreateCustomerRequest request,
			final CustomerRegistrationBean customer, final String timeZone,
			final String countryCode) {

		final Customer customerRequest = new Customer();

		final CustomerType customerType = new CustomerType();
		customerType.setId(Integer.valueOf(customer.getProductType().getId()));
		customerType.setName(customer.getProductType().getValue());

		customerRequest.setCustomerType(customerType);

		if (customer.getParentId() != 0) {
			customerRequest.setParentId(customer.getParentId());
		}

		// Setting the address
		final Address address = new Address();
		address.setEmail(customer.getEmailId());
		address.setEmployeeID(customer.getEmployeeId());
		address.setGender(Integer.valueOf(customer.getGender().getId()));
		address.setIncome(customer.getIncome().getId());
		address.setIndustrySectorOfEmployer(customer.getIndustryOfEmployee()
				.getId());
		address.setJob(customer.getJob().getId());
		address.setMaidenName(customer.getMothersMaidenName());
		address.setOccupation(customer.getOccupation().getId());
		address.setPurposeOfAccount(customer.getPurposeOfAccount().getId());
		address.setShortName(customer.getShortName());
		address.setSourceOfFund(customer.getSourceofFound().getId());
		address.setStreet1(customer.getStreet1());
		address.setStreet2(customer.getStreet2());
		customerRequest.setAddress(address);
		customerRequest.setCustomerNumber(customer.getCustomerNumber());

		customerRequest.setActive(true);
		customerRequest.setBlackListReason(BtpnConstants.BLACKLISTREASON_ZERO);

		// Add the identification
		final CustomerIdentification identification = new CustomerIdentification();
		identification.setType(BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO);
		final PhoneNumber phoneNumber = new PhoneNumber(
				customer.getMobileNumber(), countryCode);
		identification.setIdentification(phoneNumber.getInternationalFormat());
		identification.setStatus(BtpnConstants.REGISTRATION_INITIAL_STATUS);
		customerRequest.getIdentifications().add(identification);

		final CustomerIdentification identAtmCardNo = new CustomerIdentification();
		identAtmCardNo.setType(BtpnConstants.IDENTIFICATION_TYPE_ATM_CARD_NO);
		identAtmCardNo.setIdentification(customer.getAtmCardNo());
		identAtmCardNo.setStatus(BtpnConstants.REGISTRATION_INITIAL_STATUS);
		customerRequest.getIdentifications().add(identAtmCardNo);

		// Add attachements
		for (NotificationAttachmentsBean bean : customer.getAttachmentsList()) {
			final CustomerAttachedAttachment attachment = new CustomerAttachedAttachment();
			attachment.setName(bean.getFileName());
			attachment.setAttachmentType(Integer.valueOf(customer.getIdType()
					.getId()));
			attachment.setContent(bean.getFileContent());
			attachment.setContentType(bean.getContentType());
			customerRequest.getSign().add(attachment);
		}

		// Add the identities
		final Identity identity = new Identity();
		identity.setActive(true);
		identity.setIdentity(customer.getIdCardNo());
		identity.setIdentityType(Integer.valueOf(customer.getIdType().getId()));
		identity.setDateExpires(PortalUtils
				.getSaveXMLGregorianCalendarFromDate(
						customer.getExpireDateString(), null));
		identity.setStatus(BtpnConstants.REGISTRATION_INITIAL_STATUS);
		customerRequest.getIdentities().add(identity);

		// Set Other Details
		customerRequest.setDateOfBirth(PortalUtils
				.getSaveXMLGregorianCalendarFromDate(
						customer.getBirthDateString(), null));
		if (customer.getGlCodeId() != null) {
			customerRequest.setGlCode(Long.valueOf(customer.getGlCodeId()
					.getId()));
		}
		customerRequest.setHighRiskBusiness(customer.getHighRiskBusiness()
				.getId().equalsIgnoreCase(BtpnConstants.YES_ID));
		customerRequest.setHighRiskCustomer(customer.getHighRiskCustomer()
				.getId().equalsIgnoreCase(BtpnConstants.YES_ID));

		customerRequest.setIsOptimaActivated(customer.getOptimaActivated()
				.getId().equalsIgnoreCase(BtpnConstants.YES_ID));
		customerRequest.setIsTaxExempted(customer.getTaxExempted().getId()
				.equalsIgnoreCase(BtpnConstants.YES_ID));
		customerRequest.setLanguage(customer.getLanguage().getId());
		customerRequest.setMaritalStatus(customer.getMaritalStatus().getId());
		customerRequest.setName(customer.getName());
		customerRequest.setNationality(customer.getNationality().getId());
		customerRequest.setNotificationMode(Integer.valueOf(customer
				.getReceiptMode().getId()));
		customerRequest.setPlaceOfBirth(customer.getPlaceOfBirth());
		customerRequest.setPurposeOfTransaction(customer
				.getPurposeOfTransaction());
		request.setRegistrationType(customer.getRegistrationType().getValue());

		// setting customer attributes
		final CustomerAttributes custAttributes = new CustomerAttributes();
		custAttributes.setAgentId(customer.getAgentCode());
		custAttributes.setCity(customer.getCity() != null ? customer.getCity()
				.getId() : null);
		custAttributes
				.setForecastTransaction(customer.getForeCastTransaction() != null ? customer
						.getForeCastTransaction().getId() : null);
		custAttributes
				.setLastEducation(customer.getLastEducation() != null ? customer
						.getLastEducation().getId() : null);
		custAttributes
				.setMarketingsourceCode(customer.getMarketingSourceCode());
		custAttributes.setProvince(customer.getProvince() != null ? customer
				.getProvince().getId() : null);
		custAttributes.setReligion(customer.getReligion() != null ? customer
				.getReligion().getId() : null);
		custAttributes.setReferralNumber(customer.getReferralNumber());
		custAttributes.setTaxCardNumber(customer.getTaxCardNumber());
		custAttributes.setZipCode(customer.getZipCode());
		customerRequest.setAttributes(custAttributes);
		// set customer
		request.setCustomer(customerRequest);

	}

	/**
	 * This fetches the pending approval list.
	 * 
	 * @param response
	 *            FindPendingUsersResponse
	 * @return List<ApproveCustomerBean> list of approve beans
	 */
	public static List<ApproveCustomerBean> convertToApproveCustomerBean(
			final FindPendingUsersResponse response) {
		final List<ApproveCustomerBean> approveList = new ArrayList<ApproveCustomerBean>();
		for (final PendingApprovalUser user : response.getCustomers()) {
			final ApproveCustomerBean bean = new ApproveCustomerBean();
			bean.setConsumerType(user.getCustomerType().getName());
			bean.setCreatedBy(user.getCreatedBy());
			bean.setDate(PortalUtils.getSaveDate(user.getCreatedDate()));
			bean.setMobileNumber(user.getUsername());
			bean.setRequestType(user.getRequestType());
			bean.setStatus(user.getStatus());
			bean.setTaskId(user.getTaskId());
			approveList.add(bean);
		}
		return approveList;
	}

	/**
	 * This method converts to CustomerRegistrationBean from Customer Object.
	 * 
	 * @param customer
	 *            Customer Object for Customer.
	 * @param lookupMapUtility
	 *            Lookupmap utility for lookups.
	 * @param component
	 *            component.
	 * @return CustomerRegistrationBean Customer Registration Bean for Customer.
	 */
	public static CustomerRegistrationBean convertToCustomerRegistrationBean(
			final Customer customer, final ILookupMapUtility lookupMapUtility,
			final Component component) {
		final CustomerRegistrationBean customerBean = new CustomerRegistrationBean();

		// Setting the address
		final Address address = customer.getAddress();
		customerBean.setCustomerId(String.valueOf(address.getCustomerId()));
		customerBean.setEmailId(address.getEmail());
		customerBean.setEmployeeId(address.getEmployeeID());

		// set the product type
		customerBean.setProductType(new CodeValue(String.valueOf(customer
				.getCustomerType().getId()), customer.getCustomerType()
				.getName()));

		// Set the gender
		final String gender = String.valueOf(address.getGender());
		customerBean.setGender(new CodeValue(gender, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUNDLE_GENDERS, component),
						BtpnConstants.RESOURCE_BUNDLE_GENDERS + "." + gender)));

		// Set the income
		final String income = address.getIncome();
		customerBean.setIncome(new CodeValue(income, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUBDLE_INCOME, component),
						BtpnConstants.RESOURCE_BUBDLE_INCOME + "." + income)));

		// set the Industry sector
		final String employer = address.getIndustrySectorOfEmployer();
		customerBean.setIndustryOfEmployee(new CodeValue(employer, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE,
						component),
						BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE
								+ "." + employer)));

		// Set the job
		final String job = address.getJob();
		customerBean.setJob(new CodeValue(job, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUBDLE_JOB, component),
						BtpnConstants.RESOURCE_BUBDLE_JOB + "." + job)));
		customerBean.setMothersMaidenName(address.getMaidenName());

		// set the Occupation
		final String occupation = address.getOccupation();
		customerBean.setOccupation(new CodeValue(occupation, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUNDLE_OCCUPATION, component),
						BtpnConstants.RESOURCE_BUNDLE_OCCUPATION + "."
								+ occupation)));

		// set the purpose of account
		final String purposeOfAccount = address.getPurposeOfAccount();
		customerBean
				.setPurposeOfAccount(new CodeValue(
						purposeOfAccount,
						BtpnUtils.getDropdownValueFromId(
								lookupMapUtility
										.getLookupNamesMap(
												BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT,
												component),
								BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT
										+ "." + purposeOfAccount)));

		customerBean.setShortName(address.getShortName());

		// set the Source of Fund
		final String sourceOfFund = address.getSourceOfFund();
		customerBean.setSourceofFound(new CodeValue(sourceOfFund, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND,
						component),
						BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND + "."
								+ sourceOfFund)));

		customerBean.setStreet1(address.getStreet1());
		customerBean.setStreet2(address.getStreet2());

		// Set the identification List
		final List<CustomerIdentification> identificationsList = customer
				.getIdentifications();
		for (CustomerIdentification identification : identificationsList) {
			final int type = identification.getType();
			if (type == BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO) {
				customerBean
						.setMobileNumber(identification.getIdentification());
			} else if (type == BtpnConstants.IDENTIFICATION_TYPE_ATM_CARD_NO) {
				customerBean.setAtmCardNo(identification.getIdentification());
			}
		}

		// set the sign
		final List<CustomerAttachedAttachment> getSignList = customer.getSign();
		for (CustomerAttachedAttachment attachment : getSignList) {
			final NotificationAttachmentsBean bean = new NotificationAttachmentsBean();
			bean.setContentType(attachment.getContentType());
			bean.setFileName(attachment.getName());
			customerBean.getAttachmentsList().add(bean);
		}

		// set the identities
		final List<Identity> identitiesList = customer.getIdentities();
		for (Identity identity : identitiesList) {
			customerBean.setIdCardNo(identity.getIdentity());
			final String idType = String.valueOf(identity.getIdentityType());
			customerBean.setIdType(new CodeValue(idType, BtpnUtils
					.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
							BtpnConstants.RESOURCE_BUBDLE_ID_TYPE, component),
							BtpnConstants.RESOURCE_BUBDLE_ID_TYPE + "."
									+ idType)));
			customerBean.setExpireDateString(PortalUtils.getSaveDate(identity
					.getDateExpires()));
			break;
		}

		customerBean.setCustomerNumber(customer.getCustomerNumber());
		customerBean.setBirthDateString(PortalUtils.getSaveDate(customer
				.getDateOfBirth()));

		// set the GL Code
		final String glCode = String.valueOf(customer.getGlCode());
		if (null != glCode) {
			customerBean.setGlCodeId(new CodeValue(glCode, BtpnUtils
					.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
							BtpnConstants.RESOURCE_BUNDLE_BTPN_GL_CODES,
							component),
							BtpnConstants.RESOURCE_BUNDLE_BTPN_GL_CODES + "."
									+ glCode)));
		}

		// set the Language
		final String language = customer.getLanguage();
		customerBean.setLanguage(new CodeValue(language, BtpnUtils
				.getDropdownValueFromId(
						lookupMapUtility.getLookupNamesMap(
								BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG,
								component),
						BtpnConstants.RESOURCE_BUNDLE_BTPN_REG_LANG + "."
								+ language)));

		// set the marital status
		final String maritalStatus = customer.getMaritalStatus();
		customerBean.setMaritalStatus(new CodeValue(maritalStatus, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility
						.getLookupNamesMap(
								BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS,
								component),
						BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS + "."
								+ maritalStatus)));
		customerBean.setName(customer.getName());

		// set the nationality
		final String nationality = customer.getNationality();
		customerBean.setNationality(new CodeValue(nationality, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUBDLE_NATIONALITY, component),
						BtpnConstants.RESOURCE_BUBDLE_NATIONALITY + "."
								+ nationality)));

		// set the reciept mode
		final String recieptMode = String.valueOf(customer
				.getNotificationMode());
		customerBean.setReceiptMode(new CodeValue(recieptMode, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, component),
						BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE + "."
								+ recieptMode)));

		customerBean.setPlaceOfBirth(customer.getPlaceOfBirth());
		customerBean.setHighRiskBusiness(setBooleanValues(customer
				.isHighRiskBusiness()));
		customerBean.setHighRiskCustomer(setBooleanValues(customer
				.isHighRiskCustomer()));
		customerBean.setOptimaActivated(setBooleanValues(customer
				.isIsOptimaActivated()));
		customerBean
				.setTaxExempted(setBooleanValues(customer.isIsTaxExempted()));
		customerBean
				.setPurposeOfTransaction(customer.getPurposeOfTransaction());
		// Set Customer Attributes
		final CustomerAttributes attributes = customer.getAttributes();
		customerBean.setAgentCode(attributes.getAgentId());
		// Set Province
		final String province = attributes.getProvince();
		if (PortalUtils.exists(province)) {
			customerBean.setProvince(new CodeValue(province, BtpnUtils
					.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
							BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE,
							component),
							BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE
									+ "." + province)));
		}
		// Set City Attribute
		final String city = attributes.getCity();
		if (PortalUtils.exists(city)) {
			customerBean.setCity(new CodeValue(city, BtpnUtils
					.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
							province, component), province + "." + city)));
		}
		// set Forecast
		final String forecast = attributes.getForecastTransaction();
		if (PortalUtils.exists(forecast)) {
			customerBean
					.setForeCastTransaction(new CodeValue(
							forecast,
							BtpnUtils.getDropdownValueFromId(
									lookupMapUtility
											.getLookupNamesMap(
													BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS,
													component),
									BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS
											+ "." + forecast)));
		}
		// Last Education
		final String lastEducation = attributes.getLastEducation();
		if (PortalUtils.exists(lastEducation)) {
			customerBean
					.setLastEducation(new CodeValue(
							lastEducation,
							BtpnUtils.getDropdownValueFromId(
									lookupMapUtility
											.getLookupNamesMap(
													BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS,
													component),
									BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS
											+ "." + lastEducation)));
		}
		// Religion
		final String religion = attributes.getReligion();
		if (PortalUtils.exists(religion)) {
			customerBean.setReligion(new CodeValue(religion, BtpnUtils
					.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
							BtpnConstants.RESOURCE_BUNDLE_RELIGION, component),
							BtpnConstants.RESOURCE_BUNDLE_RELIGION + "."
									+ religion)));
		}
		customerBean
				.setMarketingSourceCode(attributes.getMarketingsourceCode());
		customerBean.setReferralNumber(attributes.getReferralNumber());
		customerBean.setTaxCardNumber(attributes.getTaxCardNumber());
		customerBean.setZipCode(attributes.getZipCode());
		// set customer
		return customerBean;

	}

	/**
	 * This method converts to convertToBankStaffBean.
	 * 
	 * @param bankStaff
	 *            Bank Staff for Bank Staff.
	 * @return BankStaffBean Front End Back Staff Bean.
	 */
	public static BankStaffBean convertToBankStaffBean(final BankStaff bankStaff) {
		final BankStaffBean bankStaffBean = new BankStaffBean();
		bankStaffBean.setDesignation(bankStaff.getDesignation());
		bankStaffBean.setEmail(bankStaff.getEmail());
		bankStaffBean.setName(bankStaff.getName());
		bankStaffBean.setTerritoryCode(bankStaff.getTerritoryCode());
		bankStaffBean.setType(bankStaff.getCustomerType().getRole());
		return bankStaffBean;
	}

	/**
	 * This method converts to convertToBankStaffBean.
	 * 
	 * @param customer
	 *            Customer from Login Service
	 * @return BtpnCustomer BtpnCustomer for BTPN.
	 */
	public static BtpnCustomer convertToBtpnCustomer(
			final com.sybase365.mobiliser.util.tools.wicketutils.security.Customer customer) {
		
		final BtpnCustomer btpnCustomer = new BtpnCustomer();
		btpnCustomer.setCustomerId(customer.getCustomerId());
		btpnCustomer.setCountry(customer.getCountry());
		btpnCustomer.setCustomerTypeId(customer.getCustomerTypeId());
		btpnCustomer.setDisplayName(customer.getDisplayName());
		btpnCustomer.setLanguage(customer.getLanguage());
		btpnCustomer.setParentId(customer.getParentId());
		btpnCustomer.setOrgUnitId(customer.getOrgUnitId());
		btpnCustomer.setTimeZone(customer.getTimeZone());
		btpnCustomer.setCreatedDate(new Date());
		btpnCustomer.setUsername(customer.getUsername());
		btpnCustomer.setSessionId(customer.getSessionId());
		btpnCustomer.setSessionTimeout(customer.getSessionTimeout());
		return btpnCustomer;
	}
	
	/**
	 * Add By Andi S
	 */
	public static BtpnCustomer convToBtpnCustomer(
			final com.sybase365.mobiliser.util.tools.wicketutils.security.Customer customer) {
		
		final BtpnCustomer btpnCustomer = new BtpnCustomer();
		
		btpnCustomer.setCountry(customer.getCountry());
		btpnCustomer.setCustomerId(customer.getCustomerId());
		btpnCustomer.setCustomerTypeId(customer.getCustomerTypeId());
		btpnCustomer.setDisplayName(customer.getDisplayName());
		btpnCustomer.setLanguage(customer.getLanguage());
		btpnCustomer.setParentId(customer.getParentId());
		btpnCustomer.setOrgUnitId(customer.getOrgUnitId());
		btpnCustomer.setTimeZone(customer.getTimeZone());
		btpnCustomer.setCreatedDate(new Date());
		btpnCustomer.setUsername(customer.getUsername());
		btpnCustomer.setSessionId(customer.getSessionId());
		btpnCustomer.setSessionTimeout(customer.getSessionTimeout());
		
		return btpnCustomer;
	}
	

	/**
	 * This method converts to CodeValue from Boolean Values.
	 * 
	 * @param yesOrNo
	 *            boolean value for Yes Or No.
	 * @return CodeValue CodeValue for Yes Or No.
	 */
	public static CodeValue setBooleanValues(final boolean yesOrNo) {
		if (yesOrNo) {
			return new CodeValue(BtpnConstants.YES_ID, BtpnConstants.YES_VALUE);
		}
		return new CodeValue(BtpnConstants.NO_ID, BtpnConstants.NO_VALUE);
	}

	/**
	 * This method converts to Products Bean List.
	 * 
	 * @param response
	 *            FindProductsResponse for products.
	 * @param lookupMapUtility
	 *            LookupMapUtility for lookups.
	 * @param component
	 *            Component for lookup.
	 * @return List<ManageProductsBean> ManageProductsBean for products.
	 */
	public static List<ManageProductsBean> convertToManageProductsBeanList(
			final FindProductsResponse response,
			final ILookupMapUtility lookupMapUtility, final Component component) {
		final List<Product> productsList = response.getProduct();
		final List<ManageProductsBean> productsBeanList = new ArrayList<ManageProductsBean>();
		for (Product product : productsList) {
			final ManageProductsBean productsBean = convertToManageProductsBean(
					product, lookupMapUtility, component);
			productsBeanList.add(productsBean);
		}
		return productsBeanList;
	}

	/**
	 * This method converts to Products Bean List.
	 * 
	 * @param product
	 *            Product for Manage Products
	 * @param lookupMapUtility
	 *            LookupMapUtility for lookups.
	 * @param component
	 *            Component for lookup.
	 * @return ManageProductsBean ManageProductsBean for Front End.
	 */
	public static ManageProductsBean convertToManageProductsBean(
			final Product product, final ILookupMapUtility lookupMapUtility,
			final Component component) {

		final ManageProductsBean bean = new ManageProductsBean();
		final Map<String, String> productGLMap = lookupMapUtility
				.getLookupNamesMap(BtpnConstants.PRODUCT_GL_CODES, component);
		final Map<String, String> productCategory = lookupMapUtility
				.getLookupNamesMap(BtpnConstants.PRODUCT_TYPES, component);

		bean.setAdminFee(product.getAdminFeeAmount());
		// set the fee GL Code and set the product GL Code
		final Long feeGLCodeLong = product.getAdminFeeGLCode();
		if (PortalUtils.exists(feeGLCodeLong)) {
			final String feeGLCode = feeGLCodeLong.toString();
			bean.setFeeGLCode(new CodeValue(feeGLCode, BtpnUtils
					.getDropdownValueFromId(productGLMap,
							BtpnConstants.PRODUCT_GL_CODES + "." + feeGLCode)));
		}
		bean.setInitialDeposit(product.getInitialDepositAmount());
		bean.setMinBalance(product.getMinimumBalanceAmount());
		// set Product GL Code

		final Long productGLCodeLong = product.getProductGLCode();
		if (PortalUtils.exists(productGLCodeLong)) {
			final String productGLCode = productGLCodeLong.toString();
			bean.setProductGLCode(new CodeValue(productGLCode, BtpnUtils
					.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
							BtpnConstants.PRODUCT_GL_CODES, component),
							BtpnConstants.PRODUCT_GL_CODES + "."
									+ productGLCode)));
		}
		final String productType = String.valueOf(product.getProductCategory());
		bean.setProductType(new CodeValue(productType, BtpnUtils
				.getDropdownValueFromId(productCategory,
						BtpnConstants.PRODUCT_TYPES + "." + productType)));
		bean.setProductId(product.getCustomerTypeId());
		bean.setProductName(String.valueOf(product.getProductName()));
		bean.setRoleName(product.getRoleName());
		bean.setRoleDescription(product.getRoleDescription());

		final List<InterestSlab> interestSlabsList = product.getInterestSlab();

		for (InterestSlab interestSlab : interestSlabsList) {
			final ManageProductsRangeBean rangeBean = new ManageProductsRangeBean();
			rangeBean.setId(interestSlab.getId());
			rangeBean.setMinRange(interestSlab.getMinRangeAmount());
			rangeBean.setMaxRange(interestSlab.getMaxRangeAmount());
			rangeBean
					.setInterest(interestSlab.getPercentage() != null ? BtpnConstants.PERCENT_INTEREST_RADIO
							: BtpnConstants.FIXED_INTEREST_RADIO);
			rangeBean
					.setValue(interestSlab.getInterestAmount() == null ? String
							.valueOf(interestSlab.getPercentage()) : String
							.valueOf(interestSlab.getInterestAmount() / 100));
			bean.getRangeList().add(rangeBean);
		}
		return bean;

	}

	/**
	 * This method converts to Products Bean.
	 * 
	 * @param productBean
	 *            ManageProductsBean for products.
	 * @return Product Products from service.
	 */
	public static Product convertToProduct(final ManageProductsBean productBean) {
		final Product product = new Product();
		product.setAdminFeeAmount(productBean.getAdminFee());
		product.setAdminFeeGLCode(Long.parseLong(productBean.getFeeGLCode()
				.getId()));
		product.setInitialDepositAmount(productBean.getInitialDeposit());
		product.setMinimumBalanceAmount(productBean.getMinBalance());
		product.setProductGLCode(Long.parseLong(productBean.getProductGLCode()
				.getId()));
		product.setCustomerTypeId(productBean.getProductId() != null ? Integer
				.valueOf(productBean.getProductId()) : null);
		product.setProductName(productBean.getProductName());
		product.setProductCategory(Integer.valueOf(productBean.getProductType()
				.getId()));
		product.setRoleName(productBean.getRoleName());
		product.setRoleDescription(productBean.getRoleDescription());
		final List<ManageProductsRangeBean> rangeList = productBean
				.getRangeList();
		for (ManageProductsRangeBean rangeBean : rangeList) {
			final InterestSlab interestSlab = new InterestSlab();
			interestSlab.setId(rangeBean.getId());
			interestSlab.setMaxRangeAmount(rangeBean.getMaxRange());
			interestSlab.setMinRangeAmount(rangeBean.getMinRange());
			if (rangeBean.getInterest().equals(
					BtpnConstants.FIXED_INTEREST_RADIO)) {
				interestSlab.setInterestAmount(Long.valueOf(rangeBean
						.getValue()) * 100);
				interestSlab.setPercentage(null);
			} else {
				interestSlab.setInterestAmount(null);
				interestSlab
						.setPercentage(new BigDecimal(rangeBean.getValue()));
			}
			product.getInterestSlab().add(interestSlab);
		}
		return product;
	}

	/**
	 * This method converts to Manage Products Approve Bean List.
	 * 
	 * @param products
	 *            Products from service
	 * @param lookupMapUtility
	 *            LookupMapUtility for lookups.
	 * @param component
	 *            Component for lookup.
	 * @return List<ManageProductsApproveBean> ManageProductsApproveBean from
	 *         approve.
	 */
	public static List<ManageProductsApproveBean> convertToManageProductsApproveBeanList(
			final List<PendingApprovalProduct> products,
			final ILookupMapUtility lookupMapUtility, final Component component) {
		final List<ManageProductsApproveBean> approveList = new ArrayList<ManageProductsApproveBean>();
		for (PendingApprovalProduct pendingProduct : products) {
			final ManageProductsApproveBean approveBean = convertToManageProductsApproveBean(
					pendingProduct.getCurrentProduct(),
					pendingProduct.getNewProduct(), lookupMapUtility, component);
			approveBean.setStatus(pendingProduct.getStatus());
			approveBean.setMaker(pendingProduct.getCreatedBy());
			approveBean.setAction(pendingProduct.getRequestType());
			approveBean.setTaskId(pendingProduct.getTaskId());
			approveList.add(approveBean);
		}
		return approveList;
	}

	/**
	 * This method converts to Manage Products Approve Bean.
	 * 
	 * @param currentProduct
	 *            Current Product from service.
	 * @param newProduct
	 *            New Product from service
	 * @param lookupMapUtility
	 *            LookupMapUtility for lookups.
	 * @param component
	 *            Component for lookup.
	 * @return ManageProductsApproveBean ManageProductsApproveBean to approve.
	 */
	public static ManageProductsApproveBean convertToManageProductsApproveBean(
			final Product currentProduct, final Product newProduct,
			final ILookupMapUtility lookupMapUtility, final Component component) {
		final ManageProductsApproveBean approveBean = new ManageProductsApproveBean();
		List<InterestSlab> currentSlabList = new ArrayList<InterestSlab>();
		final Map<String, String> productGLMap = lookupMapUtility
				.getLookupNamesMap(BtpnConstants.PRODUCT_GL_CODES, component);
		final Map<String, String> productCategory = lookupMapUtility
				.getLookupNamesMap(BtpnConstants.PRODUCT_TYPES, component);

		if (currentProduct != null) {
			// Set the Current Products
			approveBean.setProductId(currentProduct.getCustomerTypeId());
			approveBean.setProductName(String.valueOf(currentProduct
					.getProductName()));
			approveBean.setAdminFee(currentProduct.getAdminFeeAmount());

			// set the fee GL Code
			final String feeGLCode = String.valueOf(currentProduct
					.getAdminFeeGLCode());
			approveBean.setFeeGLCode(new CodeValue(feeGLCode, BtpnUtils
					.getDropdownValueFromId(productGLMap,
							BtpnConstants.PRODUCT_GL_CODES + "." + feeGLCode)));
			final String productGLCode = String.valueOf(currentProduct
					.getProductGLCode());
			approveBean.setProductGLCode(new CodeValue(productGLCode, BtpnUtils
					.getDropdownValueFromId(productGLMap,
							BtpnConstants.PRODUCT_GL_CODES + "."
									+ productGLCode)));
			final String productType = String.valueOf(currentProduct
					.getProductCategory());
			approveBean.setProductType(new CodeValue(productType, BtpnUtils
					.getDropdownValueFromId(productCategory,
							BtpnConstants.PRODUCT_TYPES + "." + productType)));
			approveBean.setInitialDeposit(currentProduct
					.getInitialDepositAmount());
			approveBean.setMinBalance(currentProduct.getMinimumBalanceAmount());
			approveBean.setRoleName(currentProduct.getRoleName());
			approveBean.setRoleDescription(currentProduct.getRoleDescription());
			currentSlabList = currentProduct.getInterestSlab();
		}

		// Set the New Products
		approveBean.setNewAdminFee(newProduct.getAdminFeeAmount());
		final String newFeeGLCode = String.valueOf(newProduct
				.getAdminFeeGLCode());
		approveBean.setNewFeeGLCode(new CodeValue(newFeeGLCode, BtpnUtils
				.getDropdownValueFromId(productGLMap,
						BtpnConstants.PRODUCT_GL_CODES + "." + newFeeGLCode)));
		final String newProductGLCode = String.valueOf(newProduct
				.getProductGLCode());
		approveBean.setNewProductGLCode(new CodeValue(newProductGLCode,
				BtpnUtils
						.getDropdownValueFromId(productGLMap,
								BtpnConstants.PRODUCT_GL_CODES + "."
										+ newProductGLCode)));
		final String newProductType = String.valueOf(newProduct
				.getProductCategory());
		approveBean.setNewProductType(new CodeValue(newProductType, BtpnUtils
				.getDropdownValueFromId(productCategory,
						BtpnConstants.PRODUCT_TYPES + "." + newProductType)));
		approveBean.setNewProductId(newProduct.getCustomerTypeId());
		approveBean.setNewProductName(String.valueOf(newProduct
				.getProductName()));
		approveBean.setNewInitialDeposit(newProduct.getInitialDepositAmount());
		approveBean.setNewMinBalance(newProduct.getMinimumBalanceAmount());
		approveBean.setNewRoleName(newProduct.getRoleName());
		approveBean.setNewRoleDescription(newProduct.getRoleDescription());

		convertToManageProductsApproveRangeBeanList(currentSlabList,
				newProduct.getInterestSlab(), approveBean);

		return approveBean;
	}

	/**
	 * This method converts to Manage Products Range Bean
	 */
	public static ManageProductsApproveBean convertToManageProductsApproveRangeBeanList(
			final List<InterestSlab> currentSlabList,
			final List<InterestSlab> newSlabList,
			final ManageProductsApproveBean approveBean) {
		final List<ManageProductsApproveRangeBean> approveBeanRangeList = approveBean
				.getApproveRangeList();
		final Iterator<InterestSlab> currentSlabItr = currentSlabList
				.iterator();
		final Iterator<InterestSlab> newSlabItr = newSlabList.iterator();
		while (currentSlabItr.hasNext() || newSlabItr.hasNext()) {
			final ManageProductsApproveRangeBean approveRangeBean = new ManageProductsApproveRangeBean();
			if (currentSlabItr.hasNext()) {
				final InterestSlab currentSlab = currentSlabItr.next();
				approveRangeBean.setMinRange(currentSlab.getMinRangeAmount());
				approveRangeBean.setMaxRange(currentSlab.getMaxRangeAmount());
				approveRangeBean
						.setFixedAmount(currentSlab.getInterestAmount() == null ? 0
								: currentSlab.getInterestAmount());
				approveRangeBean.setPercentageAmount(currentSlab
						.getPercentage() == null ? "0.0" : String
						.valueOf(currentSlab.getPercentage()));
			}
			if (newSlabItr.hasNext()) {
				final InterestSlab newSlab = newSlabItr.next();
				approveRangeBean.setNewMinRange(newSlab.getMinRangeAmount());
				approveRangeBean.setNewMaxRange(newSlab.getMaxRangeAmount());
				approveRangeBean
						.setNewFixedAmount(newSlab.getInterestAmount() == null ? 0
								: newSlab.getInterestAmount());
				approveRangeBean
						.setNewPercentageAmount(newSlab.getPercentage() == null ? "0.0"
								: String.valueOf(newSlab.getPercentage()));
			}
			approveBeanRangeList.add(approveRangeBean);
		}
		approveBean.setApproveRangeList(approveBeanRangeList);
		return approveBean;
	}

	/**
	 * This method converts to Manage General Ledger Bean.
	 * 
	 * @param generalLedger
	 *            List<GeneralLedgerSummary> list
	 */
	public static List<ManageGeneralLedgerBean> convertToManageGeneralLedgerBean(
			final List<GeneralLedgerSummary> generalLedger) {
		final List<ManageGeneralLedgerBean> generalLedgerBeanList = new ArrayList<ManageGeneralLedgerBean>();
		for (GeneralLedgerSummary glSummary : generalLedger) {
			final ManageGeneralLedgerBean glBean = new ManageGeneralLedgerBean();
			glBean.setGlCode(String.valueOf(glSummary.getIdGlCode()));
			glBean.setGlDescription(glSummary.getDescription());
			generalLedgerBeanList.add(glBean);
		}
		return generalLedgerBeanList;
	}

	/**
	 * This method converts to Manage General Ledger details Bean.
	 * 
	 * @param generalLedger
	 *            List<GeneralLedgerSummary> list
	 */
	public static ManageGeneralLedgerBean convertToManageGeneralLedgerDetailsBean(
			final GeneralLedger generalLedger,
			final ManageGeneralLedgerBean ledgerBean,
			final ILookupMapUtility lookupMapUtility, final Component component) {
		final String isLeaf = generalLedger.getIsLeaf();
		ledgerBean.setIsLeaf(isLeaf != null
				&& isLeaf.equalsIgnoreCase(BtpnConstants.YES_ID));
		final String isRoot = generalLedger.getIsRoot();
		ledgerBean.setIsRoot(isRoot != null
				&& isRoot.equalsIgnoreCase(BtpnConstants.YES_ID));
		if (generalLedger.getParentGlCode() != null) {
			final String parentGLCode = String.valueOf(generalLedger
					.getParentGlCode());
			ledgerBean
					.setParentGlCode(new CodeValue(
							parentGLCode,
							BtpnUtils.getDropdownValueFromId(
									lookupMapUtility
											.getLookupNamesMap(
													BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES,
													component),
									BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES
											+ "." + parentGLCode)));
		}
		final String glType = generalLedger.getGlType();
		ledgerBean.setType(new CodeValue(glType, BtpnUtils
				.getDropdownValueFromId(lookupMapUtility.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES,
						component),
						BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES
								+ "." + glType)));
		return ledgerBean;
	}
	
	/**
	 * This method converts to Manage General Ledger details Bean.
	 * 
	 * @param generalLedger
	 *            List<GeneralLedgerSummary> list
	 */
	public static GeneralLedger convertToGeneralLedger(
			final ManageGeneralLedgerBean ledgerBean) {
		final GeneralLedger generalLedger = new GeneralLedger();
		generalLedger.setDescription(ledgerBean.getGlDescription());
		generalLedger.setGlType(ledgerBean.getType().getId());
		generalLedger
				.setIsLeaf(ledgerBean.getIsLeaf() == true ? BtpnConstants.YES_ID
						: BtpnConstants.NO_ID);
		generalLedger
				.setIsRoot(ledgerBean.getIsRoot() == true ? BtpnConstants.YES_ID
						: BtpnConstants.NO_ID);
		final CodeValue parentGLCode = ledgerBean.getParentGlCode();
		if (parentGLCode != null && PortalUtils.exists(parentGLCode.getId())
				&& !parentGLCode.getId().equals("null")) {
			generalLedger.setParentGlCode((Long.valueOf(parentGLCode.getId())));
		} else {
			generalLedger.setParentGlCode(null);
		}
		generalLedger.setIdGlCode(Long.valueOf(ledgerBean.getGlCode()));
		return generalLedger;
	}
	
	/**
	 * This method converts to Bank Portal Manage General Ledger Bean
	 * 
	 * @param List<FoundGLItemType>
	 * @return ManageCustomGeneralLedgerBean bean
	 */
	
	public static List<ManageCustomGeneralLedgerBean> convertToCustomGeneralLedger(List<FoundGLItemType> txnList, final ILookupMapUtility lookupMapUtility, final Component component) {
		
		log.info(" ### (ConverterUtils) convertToCustomerGeneralLedger ### "); 
		final List<ManageCustomGeneralLedgerBean> glBeanList = new ArrayList<ManageCustomGeneralLedgerBean>();
		
		for (FoundGLItemType txnBean : txnList) {
			final ManageCustomGeneralLedgerBean glBean = new ManageCustomGeneralLedgerBean();
			glBean.setGlCode(Long.toString(txnBean.getCode()));
			log.info(" ### (ConverterUtils) convertToCustomGeneralLedger CODE ### " + Long.toString(txnBean.getCode()));

			if (txnBean.getParent() != null){
				glBean.setSelectedParentGlCode(new CodeValue(
					txnBean.getParent().toString(),
					BtpnUtils.getDropdownValueFromId(
							lookupMapUtility
									.getLookupNamesMap(
											BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES,
											component),
							BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES
									+ "." + txnBean.getParent().toString())));
			}else{
				glBean.setSelectedParentGlCode(null);
			}
			
			glBean.setGlDescription(txnBean.getDescription());
			log.info(" ### (ConverterUtils) convertToCustomGeneralLedger DESC ### " + txnBean.getDescription());
			glBeanList.add(glBean);
		}
		return glBeanList;
	}
	
	/**
	 * This method converts to Bank Portal Manage General Ledger Bean
	 * 
	 * @param List<FoundGLItemType>
	 * @return ManageCustomGeneralLedgerBean bean
	 */
	public static List<ManageCustomGeneralLedgerBean> convertToCustomGLWrk(List<FoundGLWrkItemType> txnList, final ILookupMapUtility lookupMapUtility, final Component component) {
		
		log.info(" ### (ConverterUtils) convertToCustomGLWrk ### "+txnList.size());
		
		final List<ManageCustomGeneralLedgerBean> glBeanList = new ArrayList<ManageCustomGeneralLedgerBean>();
		
		for (FoundGLWrkItemType txnBean : txnList) {
			final ManageCustomGeneralLedgerBean glBean = new ManageCustomGeneralLedgerBean();
			glBean.setGlCode(Long.toString(txnBean.getCode()));
			log.info(" ### (ConverterUtils) convertToCustomGeneralLedger CODE ### " + Long.toString(txnBean.getCode()));

			if (txnBean.getParent() != null){
				glBean.setSelectedParentGlCode(new CodeValue(
					txnBean.getParent().toString(),
					BtpnUtils.getDropdownValueFromId(
							lookupMapUtility
									.getLookupNamesMap(
											BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES,
											component),
							BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES
									+ "." + txnBean.getParent().toString())));
			}else{
				glBean.setSelectedParentGlCode(null);
			}
			
			glBean.setGlDescription(txnBean.getDescription());
			log.info(" ### (ConverterUtils) convertToCustomGLWrk DESC ### " + txnBean.getDescription());
			glBean.setWorkFlowId(txnBean.getWorkflowId());
			log.info(" ### (ConverterUtils) convertToCustomGLWrk WORK FLOW ID ### " + txnBean.getWorkflowId());
			glBeanList.add(glBean);
		}
		return glBeanList;
	}
	

	/**
	 * This method converts to Approval General Ledger details Bean.
	 * 
	 * @param generalLedger
	 *            List<GeneralLedgerSummary> list
	 */
	public static List<ApproveGeneralLedgerBean> convertToApproveGeneralLedgerBeanList(
			final List<PendingApprovalGeneralLedger> pendingApprovalList,
			final ILookupMapUtility lookupMapUtility, final Component component) {
		final List<ApproveGeneralLedgerBean> approvalBeanList = new ArrayList<ApproveGeneralLedgerBean>();
		for (PendingApprovalGeneralLedger ledger : pendingApprovalList) {
			final ApproveGeneralLedgerBean approveBean = convertToApproveGeneralLedgerBean(
					ledger.getNewGL(), ledger.getCurrentGL(), lookupMapUtility,
					component);
			approveBean.setStatus(ledger.getStatus());
			approveBean.setCreatedBy(ledger.getCreatedBy());
			approveBean.setTaskId(ledger.getTaskId());
			approvalBeanList.add(approveBean);
		}
		return approvalBeanList;
	}

	/**
	 * This method converts to Approval General Ledger Bean.
	 * 
	 * @param newGL
	 *            New General Ledger
	 * @param currentGL
	 *            Current General Ledger
	 */
	public static ApproveGeneralLedgerBean convertToApproveGeneralLedgerBean(
			final GeneralLedger newGL, final GeneralLedger currentGL,
			final ILookupMapUtility lookupMapUtility, final Component component) {
		final ApproveGeneralLedgerBean approveBean = new ApproveGeneralLedgerBean();
		final Map<String, String> lookupMap = lookupMapUtility
				.getLookupNamesMap(
						BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES,
						component);
		// Set the current bean details.
		if (currentGL != null) {
			approveBean.setGlCode(String.valueOf(currentGL.getIdGlCode()));
			approveBean.setGlDescription(currentGL.getDescription());
			final String currentIsLeaf = currentGL.getIsLeaf();
			approveBean.setIsLeaf(currentIsLeaf != null
					&& currentIsLeaf.equalsIgnoreCase(BtpnConstants.YES_ID));
			final String currentIsRoot = currentGL.getIsRoot();
			approveBean.setIsRoot(currentIsRoot != null
					&& currentIsRoot.equalsIgnoreCase(BtpnConstants.YES_ID));
			approveBean.setParentGlCode(new CodeValue(currentGL
					.getParentGlCode() != null ? String.valueOf(currentGL
					.getParentGlCode()) : null, null));
			approveBean.setType(new CodeValue(currentGL.getGlType(), BtpnUtils
					.getDropdownValueFromId(lookupMap,
							BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES
									+ "." + currentGL.getGlType())));
		}

		// set the new bean details
		approveBean.setNewGlCode(String.valueOf(newGL.getIdGlCode()));
		approveBean.setNewGlDescription(newGL.getDescription());
		final String newIsLeaf = newGL.getIsLeaf();
		approveBean.setNewIsLeaf(newIsLeaf != null
				&& newIsLeaf.equalsIgnoreCase(BtpnConstants.YES_ID));
		final String newIsRoot = newGL.getIsRoot();
		approveBean.setNewIsRoot(newIsRoot != null
				&& newIsRoot.equalsIgnoreCase(BtpnConstants.YES_ID));
		approveBean.setNewParentGlCode(new CodeValue(
				newGL.getParentGlCode() != null ? String.valueOf(newGL
						.getParentGlCode()) : null, null));
		approveBean.setNewType(new CodeValue(newGL.getGlType(), BtpnUtils
				.getDropdownValueFromId(lookupMap,
						BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES
								+ "." + newGL.getGlType())));
		return approveBean;
	}

	/**
	 * This method converts to Manage General Ledger Bean.
	 * 
	 * @param generalLedger
	 *            List<GeneralLedgerSummary> list
	 */
	public static List<ManageLimitBean> convertToManageLimitBeanList(
			final List<LimitSummary> limitSummaryList) {
		final List<ManageLimitBean> limitBeanList = new ArrayList<ManageLimitBean>();
		for (LimitSummary limitSummary : limitSummaryList) {
			final ManageLimitBean limitBean = new ManageLimitBean();
			limitBean.setUseCaseId(new CodeValue(String.valueOf(limitSummary
					.getUseCaseId()), limitSummary.getUseCaseName()));
			if (limitSummary.getCustomerTypeId() != null) {
				limitBean.setProductId(new CodeValue(String
						.valueOf(limitSummary.getCustomerTypeId()),
						limitSummary.getProductName()));
			}
			limitBean.setLimitClassId(limitSummary.getLimitClassId());
			limitBeanList.add(limitBean);
		}
		return limitBeanList;
	}

	/**
	 * This method converts to Manage General Ledger Bean.
	 * 
	 * @param generalLedger
	 *            List<GeneralLedgerSummary> list
	 */
	public static ManageLimitBean convertToManageLimitBean(
			final Limit limitDetail) {
		final ManageLimitBean limitBean = new ManageLimitBean();
		final LimitSummary limitSummary = limitDetail.getSummary();
		limitBean.setUseCaseId(new CodeValue(String.valueOf(limitSummary
				.getUseCaseId()), limitSummary.getUseCaseName()));
		if (PortalUtils.exists(limitSummary.getCustomerTypeId())) {
			limitBean.setProductId(new CodeValue(String.valueOf(limitSummary
					.getCustomerTypeId()), limitSummary.getProductName()));
		}
		limitBean
				.setIsApplyToPayee(limitDetail.isIsApplyToPayee() != null ? limitDetail
						.isIsApplyToPayee() : false);
		limitBean.setLimitClassId(limitSummary.getLimitClassId());
		limitBean.setMonthlyLimit(limitDetail.getMonthlyLimit());
		limitBean.setWeeklyLimit(limitDetail.getWeeklyLimit());
		limitBean.setDailyLimit(limitDetail.getDailyLimit());
		limitBean.setLimitClassId(limitSummary.getLimitClassId());
		limitBean.setLimitSetId(limitSummary.getLimitSetId());
		return limitBean;

	}

	/**
	 * This method converts to Limit Service bean
	 * 
	 * @param ManageLimitBean
	 *            ManageLimitBean beans
	 */
	public static Limit convertToLimit(final ManageLimitBean limitBean) {
		final Limit limit = new Limit();
		final LimitSummary limitSummary = new LimitSummary();
		if (limitBean.getProductId() != null) {
			limitSummary.setCustomerTypeId(Integer.valueOf(limitBean
					.getProductId().getId()));
			limitSummary.setProductName(limitBean.getProductId().getValue());
		}
		limitSummary.setUseCaseId(Integer.valueOf(limitBean.getUseCaseId()
				.getId()));
		limitSummary.setUseCaseName(limitBean.getUseCaseId().getValue());
		limitSummary.setLimitClassId(limitBean.getLimitClassId());
		limitSummary.setLimitSetId(limitBean.getLimitSetId());
		limit.setSummary(limitSummary);
		limit.setIsApplyToPayee(limitBean.getIsApplyToPayee());
		limit.setMonthlyLimit(limitBean.getMonthlyLimit());
		limit.setWeeklyLimit(limitBean.getWeeklyLimit());
		limit.setDailyLimit(limitBean.getDailyLimit());
		return limit;
	}

	/**
	 * This method converts to Limit Service bean
	 * 
	 * @param List
	 *            <SalaryDataErrorBean> SalaryDataErrorBean beans
	 */
	public static List<SalaryDataErrorBean> convertToSalaryDataErrorBeanList(
			final List<FileErrorData> fileErrorDataList) {
		final List<SalaryDataErrorBean> errorList = new ArrayList<SalaryDataErrorBean>();
		for (FileErrorData errorData : fileErrorDataList) {
			final SalaryDataErrorBean salaryErrorBean = new SalaryDataErrorBean();
			salaryErrorBean.setLineNo(errorData.getErrorLineNumber());
			salaryErrorBean.setErrorRecord(errorData.getErrorRecord());
			salaryErrorBean.setErrorDescription(errorData
					.getErrorDesccription());
			errorList.add(salaryErrorBean);
		}
		return errorList;
	}

	/**
	 * This method converts to Transaction General Ledger Bean
	 * 
	 * @param List
	 *            <TransactionGeneralLedgerBean> TransactionGeneralLedgerBean
	 *            beans
	 */
	public static List<TransactionGeneralLedgerBean> convertToTransactionGeneralLedgerApproveBeanList(
			final List<PendingApprovalTransactionGL> approveList) {
		// Transaction General Ledger Bean
		final List<TransactionGeneralLedgerBean> detailList = new ArrayList<TransactionGeneralLedgerBean>();
		// Transaction GL Detail
		for (final PendingApprovalTransactionGL approveBean : approveList) {
			final TransactionGLDetail glDetail = approveBean.getTransactiongl();
			final TransactionGeneralLedgerBean glBean = new TransactionGeneralLedgerBean();
			glBean.setCreatedBy(approveBean.getCreatedBy());
			glBean.setStatus(approveBean.getStatus());
			glBean.setTaskId(approveBean.getTaskId());
			final CodeValue currentGL = new CodeValue(String.valueOf(glDetail
					.getCurrentGlCode()), glDetail.getCurrentGlDescription());
			glBean.setCurrentGL(currentGL);
			final CodeValue newGL = new CodeValue(String.valueOf(glDetail
					.getNewGlCode()), glDetail.getNewGlDescription());
			glBean.setNewGL(newGL);
			glBean.setUseCaseId(glDetail.getUseCaseId());
			glBean.setUseCaseName(glDetail.getUseCaseName());
			glBean.setIdPi(glDetail.getIdPi());
			detailList.add(glBean);
		}
		return detailList;
	}

	/**
	 * This method converts to Transaction General Ledger Bean
	 * 
	 * @param List
	 *            <TransactionGeneralLedgerBean> TransactionGeneralLedgerBean
	 *            beans
	 */
	public static List<TransactionGeneralLedgerBean> convertToTransactionGeneralLedgerBeanList(
			final List<TransactionGLDetail> transactionGLList) {
		// Transaction General Ledger Bean
		final List<TransactionGeneralLedgerBean> detailList = new ArrayList<TransactionGeneralLedgerBean>();
		// Transaction GL Detail
		for (final TransactionGLDetail glDetail : transactionGLList) {
			final TransactionGeneralLedgerBean glBean = new TransactionGeneralLedgerBean();
			glBean.setCreatedBy(glBean.getCreatedBy());
			final CodeValue currentGL = new CodeValue(String.valueOf(glDetail
					.getCurrentGlCode()), glDetail.getCurrentGlDescription());
			glBean.setCurrentGL(currentGL);
			final CodeValue newGL = new CodeValue(String.valueOf(glDetail
					.getNewGlCode()), glDetail.getNewGlDescription());
			if (PortalUtils.exists(glDetail.getNewGlCode())) {
				glBean.setNewGL(newGL);
			}
			glBean.setUseCaseId(glDetail.getUseCaseId());
			glBean.setUseCaseName(glDetail.getUseCaseName());
			glBean.setIdPi(glDetail.getIdPi());
			detailList.add(glBean);
		}
		return detailList;
	}

	/**
	 * This method converts to Transaction General Ledger Detail Bean
	 * 
	 * @param List
	 *            <TransactionGLDetail> TransactionGLDetail bean
	 */
	public static TransactionGLDetail convertToTransactionGeneralLedgerBean(
			final TransactionGeneralLedgerBean glBean) {
		final TransactionGLDetail transGlDetail = new TransactionGLDetail();
		transGlDetail.setUseCaseId(transGlDetail.getUseCaseId());
		transGlDetail.setUseCaseName(glBean.getUseCaseName());
		transGlDetail.setCurrentGlCode(Long.valueOf(glBean.getCurrentGL()
				.getId()));
		transGlDetail.setCurrentGlDescription(glBean.getCurrentGL().getValue());
		transGlDetail.setNewGlCode(Long.valueOf(glBean.getNewGL().getId()));
		transGlDetail.setNewGlDescription(glBean.getNewGL().getValue());
		transGlDetail.setIdPi(glBean.getIdPi());
		return transGlDetail;
	}

	/**
	 * This method converts to Bank Portal Cash in Bean
	 * 
	 * @param List
	 *            <BankCashinBean> BankCashinBean bean
	 * @return
	 */
	public static List<BankCashinBean> convertToBankCashinBean(
			List<TransactionCustomer> txnCustomersList) {
		final List<BankCashinBean> cashInList = new ArrayList<BankCashinBean>();
		for (TransactionCustomer txnBean : txnCustomersList) {
			final BankCashinBean cashInBean = new BankCashinBean();
			cashInBean.setCustomerId(String.valueOf(txnBean.getCustomerId()));
			cashInBean.setMsisdn(txnBean.getMsisdn());
			cashInBean.setDisplayName(txnBean.getCustomerName());
			cashInBean.setAccountBalance(txnBean.getAccountBalance());
			cashInBean.setAccountName(txnBean.getAccountName());
			cashInBean.setAccountNumber(txnBean.getAccountNumber());
			cashInBean.setAccountType(txnBean.getAccountType());
			cashInBean.setTotalSVABalance(txnBean.getAccountBalance());
			cashInList.add(cashInBean);
		}
		return cashInList;
	}
	
	/**
	 * This method converts to Bank Portal Cash in Bean
	 * 
	 * @param List<DebitTransactionType>
	 * @return BankCustomCashInBean bean
	 */
	
	public static BankCustomCashInBean convertToBankCustomCashInBean(List<DebitTransactionType> txnList) {
		
		final BankCustomCashInBean cashInBean = new BankCustomCashInBean();
		
		for (DebitTransactionType txnBean : txnList) {
			cashInBean.setAccountName(txnBean.getCreditAccount().getHolderName());
			log.info(" ### (ConverterUtils) convertToBankCustomCashInBean HOLDER NAME ### " + txnBean.getCreditAccount().getHolderName());	
			cashInBean.setFee(txnBean.getFee().getValue());
		}
		
		return cashInBean;
	}
	

	/**
	 * This method converts to Bank Portal Cash out Bean
	 * 
	 * @param List
	 *            <BankCashOutBean> BankCashOutBean bean
	 * @return
	 */
	public static List<BankCashOutBean> convertToBankCashOutBean(
			List<TransactionCustomer> txnCustomersList) {
		final List<BankCashOutBean> cashOutList = new ArrayList<BankCashOutBean>();
		for (TransactionCustomer txnBean : txnCustomersList) {
			final BankCashOutBean cashOutBean = new BankCashOutBean();
			cashOutBean.setCustomerId(String.valueOf(txnBean.getCustomerId()));
			cashOutBean.setMobileNumber((txnBean.getMsisdn()));
			cashOutBean.setDisplayName(txnBean.getCustomerName());
			cashOutBean.setAccountBalance(txnBean.getAccountBalance());
			cashOutBean.setTotalSVABalance(txnBean.getAccountBalance());
			cashOutBean.setAccountName(txnBean.getAccountName());
			cashOutBean.setAccountNumber(txnBean.getAccountNumber());
			cashOutBean.setAccountType(txnBean.getAccountType());
			cashOutList.add(cashOutBean);
		}
		return cashOutList;
	}

	/**
	 * This method converts to Agent Portal Cash in Bean
	 * 
	 * @param AgentCashinBean
	 * @return
	 */
	public static AgentCashinBean convertToAgentCashinBean(
			List<TransactionCustomer> txnCustomersList) {
		final AgentCashinBean agentCashInBean = new AgentCashinBean();
		for (TransactionCustomer txnBean : txnCustomersList) {
			agentCashInBean.setCustomerId(String.valueOf(txnBean
					.getCustomerId()));
			agentCashInBean.setPayeeMsisdn(txnBean.getMsisdn());
			agentCashInBean.setDisplayName(txnBean.getCustomerName());
			agentCashInBean.setAccountBalance(txnBean.getAccountBalance());
			agentCashInBean.setAccountName(txnBean.getCustomerName());
			agentCashInBean.setAccountNumber(txnBean.getAccountNumber());
			agentCashInBean.setAccountType(txnBean.getAccountType());
		}
		return agentCashInBean;
	}
	
	/**
	 * This method converts to Agent Portal Cash in Bean
	 * 
	 * @param AgentCustomCashinBean
	 * @return
	 */
	public static AgentCustomCashInBean convertToAgentCustomCashInBean(List<TransactionCustomer> txnCustomersList) {
		final AgentCustomCashInBean agentCashInBean = new AgentCustomCashInBean();
		for (TransactionCustomer txnBean : txnCustomersList) {
			agentCashInBean.setCustomerId(String.valueOf(txnBean.getCustomerId()));
			agentCashInBean.setPayeeMsisdn(txnBean.getMsisdn());
			agentCashInBean.setDisplayName(txnBean.getCustomerName());
			agentCashInBean.setAccountBalance(txnBean.getAccountBalance());
			agentCashInBean.setAccountName(txnBean.getCustomerName());
			agentCashInBean.setAccountNumber(txnBean.getAccountNumber());
			agentCashInBean.setAccountType(txnBean.getAccountType());
		}
		return agentCashInBean;
	}

	/**
	 * This method converts to Agent Portal Cash Out Bean
	 * 
	 * @param AgentCashOutBean
	 * @return
	 */
	public static AgentCashOutBean convertToAgentCashOutBean(
			List<TransactionCustomer> txnCustomersList) {
		final AgentCashOutBean agentCashOutBean = new AgentCashOutBean();
		for (TransactionCustomer txnBean : txnCustomersList) {
			agentCashOutBean.setCustomerId(String.valueOf(txnBean
					.getCustomerId()));
			agentCashOutBean.setPayeeMsisdn(txnBean.getMsisdn());
			agentCashOutBean.setDisplayName(txnBean.getCustomerName());
			agentCashOutBean.setAccountBalance(txnBean.getAccountBalance());
			agentCashOutBean.setAccountName(txnBean.getCustomerName());
			agentCashOutBean.setAccountNumber(txnBean.getAccountNumber());
			agentCashOutBean.setAccountType(txnBean.getAccountType());
		}
		return agentCashOutBean;
	}
	
	public static AgentCustomCashOutBean convertToAgentCustomCashOutBean(List<TransactionCustomer> txnCustomersList) {
		final AgentCustomCashOutBean agentCashOutBean = new AgentCustomCashOutBean();
		for (TransactionCustomer txnBean : txnCustomersList) {
			agentCashOutBean.setCustomerId(String.valueOf(txnBean.getCustomerId()));
			agentCashOutBean.setPayeeMsisdn(txnBean.getMsisdn());
			agentCashOutBean.setDisplayName(txnBean.getCustomerName());
			agentCashOutBean.setAccountBalance(txnBean.getAccountBalance());
			agentCashOutBean.setAccountName(txnBean.getCustomerName());
			agentCashOutBean.setAccountNumber(txnBean.getAccountNumber());
			agentCashOutBean.setAccountType(txnBean.getAccountType());
		}
		return agentCashOutBean;
	}

	/**
	 * This method converts to Transaction Reversal Bean
	 * 
	 * @param approvalList
	 * @param pendingTxnList
	 */
	public static List<TransactionReversalBean> convertToTransactionReversalBean(
			List<PendingApprovalTransactionReversal> pendingTxnList) {
		final List<TransactionReversalBean> approvalList = new ArrayList<TransactionReversalBean>();

		for (PendingApprovalTransactionReversal pendingTxnbean : pendingTxnList) {

			final TransactionReversalBean newTxnBean = new TransactionReversalBean();
			final TransactionReversalDetail newTxn = pendingTxnbean.getNewTr();
			newTxnBean.setTransactionAmount(String.valueOf(newTxn
					.getTransactionAmount()));
			newTxnBean.setMobileNumber(newTxn.getMsisdn());
			newTxnBean.setTransactionID(String.valueOf(newTxn
					.getTransactionId()));
			newTxnBean.setTransactionName(newTxn.getTransactionName());
			newTxnBean.setUseCase(String.valueOf(newTxn.getUseCaseId()));
			newTxnBean.setTransactionDate(String.valueOf(newTxn
					.getTransactionDate()));
			newTxnBean.setStatus(pendingTxnbean.getStatus());

			final TransactionReversalBean currentTxnBean = new TransactionReversalBean();
			final TransactionReversalDetail currentTxn = pendingTxnbean
					.getCurrentTr();
			currentTxnBean.setTransactionAmount(String.valueOf(currentTxn
					.getTransactionAmount()));
			currentTxnBean.setMobileNumber(currentTxn.getMsisdn());
			currentTxnBean.setTransactionID(String.valueOf(currentTxn
					.getTransactionId()));
			currentTxnBean.setTransactionName(currentTxn.getTransactionName());
			currentTxnBean
					.setUseCase(String.valueOf(currentTxn.getUseCaseId()));
			currentTxnBean.setTransactionDate(String.valueOf(currentTxn
					.getTransactionDate()));
			currentTxnBean.setStatus(pendingTxnbean.getStatus());

			final TransactionReversalBean txnBean = new TransactionReversalBean();
			txnBean.setMaker(pendingTxnbean.getCreatedBy());
			txnBean.setTaskId(pendingTxnbean.getTaskId());
			txnBean.setNewValue(newTxnBean);
			txnBean.setCurrentValue(currentTxnBean);
			approvalList.add(txnBean);
		}
		return approvalList;
	}

	/**
	 * This method converts to Sub Accounts Bean
	 * 
	 * @param List
	 *            <SubAccountsBean> SubAccountsBean
	 * @return
	 */
	public static List<SubAccountsBean> convertToSubAccountsBean(
			List<SubAccountResponseBean> accountList) {
		final List<SubAccountsBean> beanList = new ArrayList<SubAccountsBean>();
		for (SubAccountResponseBean responseBean : accountList) {
			final SubAccountsBean accountBean = new SubAccountsBean();
			accountBean.setName(responseBean.getSubAccountName());
			accountBean.setAccountId(String.valueOf(responseBean
					.getSubAccountId()));
			accountBean.setBalance(responseBean.getSubAccountBalance());
			accountBean.setDescription(responseBean.getDescription());
			beanList.add(accountBean);
		}
		return beanList;
	}

	/**
	 * This method converts to Standing Instructions Bean
	 * 
	 * @param List
	 *            <StandingInstructionsBean> StandingInstructionsBean
	 * @return
	 */
	public static List<StandingInstructionsBean> convertToStandingInstructionsBean(
			List<StandingInstructionResponse> result) {
		final List<StandingInstructionsBean> beanList = new ArrayList<StandingInstructionsBean>();
		for (StandingInstructionResponse siBean : result) {
			final StandingInstructionsBean instructionsBean = new StandingInstructionsBean();
			instructionsBean.setName(siBean.getSiName());
			instructionsBean.setPayer(siBean.getPayerValue());
			instructionsBean.setPayee(siBean.getPayeeValue());
			instructionsBean.setType(siBean.getUsecaseType());
			instructionsBean.setStartDate(siBean.getPreSetDate());
			instructionsBean.setExpiryDate(siBean.getExpiryDate());
			instructionsBean.setFrequency(siBean.getFrequency());
			instructionsBean.setWeekDay(siBean.getWeekDay());
			instructionsBean.setAmount(String.valueOf(siBean.getAmount()));
			beanList.add(instructionsBean);
		}
		return beanList;
	}

	/**
	 * This method converts to Approve Msisdn Bean
	 * 
	 * @param List
	 *            <ApproveMsisdnBean> ApproveMsisdnBean
	 * @return
	 */
	public static List<ApproveMsisdnBean> convertToApproveMsisdnBean(
			List<PendingApprovalChangeMsisdn> customersList) {
		final List<ApproveMsisdnBean> approvalList = new ArrayList<ApproveMsisdnBean>();
		for (PendingApprovalChangeMsisdn bean : customersList) {
			final ChangeMsisdnDetails msisdnDetail = bean
					.getChangeMsisdnDetail();
			final ApproveMsisdnBean msisdnBean = new ApproveMsisdnBean();
			msisdnBean.setCreatedBy(bean.getCreatedBy());
			msisdnBean.setMobileNumber(msisdnDetail.getCurrentMsisdn());
			msisdnBean.setOldMobile(msisdnDetail.getCurrentMsisdn());
			msisdnBean.setNewMobile(msisdnDetail.getNewMsisdn());
			msisdnBean.setTaskId(bean.getTaskId());
			msisdnBean.setChangeRequest("MSISDN");
			msisdnBean.setStatus("PENDING APPROVAL");
			approvalList.add(msisdnBean);
		}
		return approvalList;
	}

	/**
	 * This method converts to Bill Payment bean.
	 * 
	 * @param BillPaymentPerformBean
	 *            billPayBean
	 * @return
	 */
	/*public static BillPayment convertToBillPay(
			final BillPaymentPerformBean billPayBean) {
		final BillPayment billPayment = new BillPayment();
		billPayment.setBillerCode(billPayBean.getBillerType().getId());
		billPayment.setBillerName(billPayBean.getBillerType().getValue());
		billPayment.setBillerType(billPayBean.getBillerType().getId());
		billPayment.setInstitutionCode(billPayBean.getSubBillerType().getId());
		Long billAmount = billPayBean.getBillAmount();
		billPayment.setAmount(billAmount == null ? 0 : billAmount);
		billPayment.setCurrencyCode(BtpnConstants.BILLPAYMENT_CURRENCY);
		String additionalData = billPayBean.getAdditionalData();
		if (additionalData == null) {
			billPayment.setAdditionalData(billPayBean.getSelectedBillerId()
					.getId());
		} else {
			billPayment.setAdditionalData(billPayBean.getAdditionalData());
		}
		billPayment.setReferenceNumber(billPayBean.getReferenceNumber());
		return billPayment;
	}*/

	/**
	 * This method performs BillPayment.
	 * 
	 * @param response
	 *            PerformBillPayResponse PerformBillPayResponse for the
	 *            response.
	 * @param bean
	 *            bean is the BillPaymentPerformBean.
	 * @return
	 */
	public static void convertToBillPaymentPerformBean(
			final PerformBillPayResponse response,
			final BillPaymentPerformBean bean) {
		bean.setBillAmount(response.getBillAmount());
		bean.setFeeAmount(response.getFeeAmount());
		bean.setReferenceNumber(response.getReferenceNumber());
		bean.setAccountNumber(response.getAccountNumber());
		bean.setPayDate(response.getPayDate());
		bean.setTxnId(response.getTxnID());
		bean.setAdditionalData(response.getAdditional());
		bean.setCustomerName(response.getCustomerName());
	}

	/**
	 * This method performs BillPayment.
	 * 
	 * @param response
	 *            ConfirmBillPayResponse ConfirmBillPayResponse for the
	 *            response.
	 * @param bean
	 *            bean is the BillPaymentPerformBean.
	 * @return
	 */
	public static void convertToBillPaymentPerformBean(
			final ConfirmBillPayResponse response,
			final BillPaymentPerformBean bean) {
	}

	/**
	 * Get Request data for Airtime Topup.
	 * 
	 * @param airtimeBean
	 *            AirtimePerformBean
	 * @return RequestData RequestData
	 */
	public static RequestData getRequestData(
			final AirtimePerformBean airtimeBean) {
		final RequestData data = new RequestData();
		data.setDestinationMsisdn(airtimeBean.getSelectedMsisdn().getId());
		data.setSourceMsisdn(airtimeBean.getSourceMsisdn());
		data.setTelcoId(airtimeBean.getTelco().getId());
		data.setTopUpAmount(Long.valueOf(airtimeBean.getDenomination()
				.getValue()) * 100);
		data.setDenomination(airtimeBean.getDenomination().getId());
		return data;
	}

	/**
	 * This method converts to Holiday ListBean
	 * 
	 * @param List
	 *            <HolidayListBean> HolidayListBean bean
	 * @return
	 */
	public static List<HolidayListBean> convertToHolidayListBean(
			final List<HolidayCalendar> calendarList, TimeZone tz) {
		final List<HolidayListBean> holidayList = new ArrayList<HolidayListBean>();
		for (HolidayCalendar calendarBean : calendarList) {
			final HolidayListBean holidayBean = new HolidayListBean();
			holidayBean.setFromDate(PortalUtils.getMMDDYYYYDate(
					calendarBean.getFromDate(), null));
			holidayBean.setToDate(PortalUtils.getMMDDYYYYDate(
					calendarBean.getToDate(), null));
			holidayBean.setDescription(calendarBean.getDescription());
			holidayBean.setHolidayId(calendarBean.getHolidayId());
			holidayList.add(holidayBean);
		}
		return holidayList;
	}

	/**
	 * This method converts to Approve Holiday List Bean
	 * 
	 * @param List
	 *            <ApproveHolidayBean> ApproveHolidayBean bean
	 * @return
	 */
	public static List<ApproveHolidayBean> convertToApproveHolidayBean(
			List<PendingApprovalHolidayCalendar> customers, TimeZone tz) {
		final List<ApproveHolidayBean> approvalList = new ArrayList<ApproveHolidayBean>();
		for (PendingApprovalHolidayCalendar bean : customers) {
			final ApproveHolidayBean approveBean = new ApproveHolidayBean();
			final HolidayCalendar calendar = bean.getCalendar();
			approveBean.setCreatedBy(bean.getCreatedBy());
			approveBean.setDescription(calendar.getDescription());
			approveBean.setStatus(BtpnConstants.STATUS_PENDING_APPROVAL);
			approveBean.setFromDate(PortalUtils.getMMDDYYYYDate(
					calendar.getFromDate(), null));
			approveBean.setToDate(PortalUtils.getMMDDYYYYDate(
					calendar.getToDate(), null));
			approveBean.setTaskId(bean.getTaskId());
			approveBean.setAction(bean.getRequestType());
			approvalList.add(approveBean);
		}
		return approvalList;
	}

	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param List
	 *            <UseCaseFee> List of use case fees to be converted
	 * @return List<ManageFeeBean> Manage Fee bean list
	 */
	/*public static List<ManageBillPaymentFeeBean> convertToManageBillPaymentFeeBean(
			final List<VendorFeeSummary> vendorFeeSummaryList) {
		final List<ManageBillPaymentFeeBean> billPayFeeList = new ArrayList<ManageBillPaymentFeeBean>();
		for (final VendorFeeSummary feeSummary : vendorFeeSummaryList) {
			final ManageBillPaymentFeeBean feeBean = new ManageBillPaymentFeeBean();
			feeBean.setBiller(new CodeValue(feeSummary.getVendorId(),
					feeSummary.getVendorDescription()));
			feeBean.setBillerCode(new CodeValue(feeSummary.getVendorCode(),
					feeSummary.getVendorCodeDescription()));
			feeBean.setUseCaseFeeId(feeSummary.getUseCaseFeeId());
			billPayFeeList.add(feeBean);
		}
		return billPayFeeList;
	}*/
	
	
	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param List
	 *            <UseCaseFee> List of use case fees to be converted
	 * @return List<ManageCustomUseCaseFeeBean> ManageCustomUseCaseFeeBean bean list
	 */
	public static List<ManageCustomUseCaseFeeBean> convertToManageCustomUseCaseFeeBean(
			final List<UseCaseFeeFindViewType> txnList) {
		
		final List<ManageCustomUseCaseFeeBean> ucFeeList = new ArrayList<ManageCustomUseCaseFeeBean>();
		for (final UseCaseFeeFindViewType uc : txnList) {
			final ManageCustomUseCaseFeeBean ucBean = new ManageCustomUseCaseFeeBean();
			log.info("### (ConverterUtils) convertToManageCustomUseCaseFeeBean ID ### "+uc.getId());
			ucBean.setId(uc.getId());
			log.info("### (ConverterUtils) convertToManageCustomUseCaseFeeBean DESC ### "+uc.getDescription());
			ucBean.setDescription(uc.getDescription());
			ucFeeList.add(ucBean);
		}
		return ucFeeList;
	}
	
	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param List
	 *            <UseCaseFee> List of use case fees to be converted
	 * @return List<ManageCustomUseCaseFeeBean> ManageCustomUseCaseFeeBean bean list
	 */
	public static List<ManageCustomUseCaseFeeBean> convertToManageCustomUseCaseFeeWrkBean(
			final List<UseCaseFeeWrkFindViewType> txnList) {   
		
		final List<ManageCustomUseCaseFeeBean> ucFeeList = new ArrayList<ManageCustomUseCaseFeeBean>();
		for (final UseCaseFeeWrkFindViewType uc : txnList) {
			final ManageCustomUseCaseFeeBean ucBean = new ManageCustomUseCaseFeeBean();
			ucBean.setWorkFlowId(uc.getWorkflowId());
			ucBean.setDescription(uc.getDescription());
			ucFeeList.add(ucBean);
		}
		return ucFeeList;
	}

	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param List
	 *            <UseCaseFee> List of use case fees to be converted
	 * @return List<ManageFeeBean> Manage Fee bean list
	 */
	public static List<ManageAirtimeTopupFeeBean> convertToManageAiritmeTopupFeeBean(
			final List<VendorFeeSummary> vendorFeeSummaryList) {
		final List<ManageAirtimeTopupFeeBean> airtimeFeeList = new ArrayList<ManageAirtimeTopupFeeBean>();
		for (final VendorFeeSummary feeSummary : vendorFeeSummaryList) {
			final ManageAirtimeTopupFeeBean feeBean = new ManageAirtimeTopupFeeBean();
			feeBean.setDenomination(new CodeValue(feeSummary.getVendorCode(),
					feeSummary.getVendorCodeDescription()));
			feeBean.setTelco(new CodeValue(feeSummary.getVendorId(), feeSummary
					.getVendorDescription()));
			//feeBean.setUseCaseFeeId(feeSummary.getUseCaseFeeId());
			airtimeFeeList.add(feeBean);
		}
		return airtimeFeeList;
	}

	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param ManageBillPaymentFeeBean
	 *            ManageBillPaymentFeeBean
	 * @return VendorFeeSharing VendorFeeSharing
	 */
	/*public static VendorFeeSharing convertToAirtimeVendorFeeSharing(
			final ManageAirtimeTopupFeeBean feeBean) {
		final VendorFeeSharing feeSharing = new VendorFeeSharing();
		feeSharing.setVendorId(feeBean.getTelco().getId());
		feeSharing.setVendorDescription(feeBean.getTelco().getValue());
		feeSharing.setVendorCode(feeBean.getDenomination().getId());
		feeSharing.setVendorCodeDescription(feeBean.getDenomination()
				.getValue());
		feeSharing.setUseCaseFee(ConverterUtils.convertToUseCaseFee(feeBean));
		feeSharing.getFees().addAll(
				ConverterUtils.convertToAddFeeConfig(feeBean));
		return feeSharing;
	}*/

	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param ManageBillPaymentFeeBean
	 *            ManageBillPaymentFeeBean
	 * @return VendorFeeSharing VendorFeeSharing
	 */
	/*public static VendorFeeSharing convertToVendorFeeSharing(
			final ManageBillPaymentFeeBean feeBean) {
		final VendorFeeSharing feeSharing = new VendorFeeSharing();
		feeSharing.setVendorId(feeBean.getBiller().getId());
		feeSharing.setVendorDescription(feeBean.getBiller().getValue());
		feeSharing.setVendorCode(feeBean.getBillerCode().getId());
		feeSharing.setVendorCodeDescription(feeBean.getBillerCode().getValue());
		feeSharing.setUseCaseFee(ConverterUtils.convertToUseCaseFee(feeBean));
		feeSharing.getFees().addAll(
				ConverterUtils.convertToAddFeeConfig(feeBean));
		return feeSharing;
	}*/

	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param List
	 *            <UseCaseFee> List of use case fees to be converted
	 * @return List<ManageFeeBean> Manage Fee bean list
	 */
	/*public static List<ManageBillPaymentFeeBean> convertToManageFeeBeanList(
			final List<UseCaseFee> useCaseFeesList) {
		final List<ManageBillPaymentFeeBean> feeList = new ArrayList<ManageBillPaymentFeeBean>();
		for (final UseCaseFee useCaseFee : useCaseFeesList) {
			feeList.add(convertToManageFeeBean(useCaseFee));
		}
		return feeList;
	}*/

	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param UseCaseFee
	 *            List of use case fees to be converted
	 * @return List<ManageFeeBean> Manage Fee bean list
	 */
	/*public static ManageBillPaymentFeeBean convertToManageFeeBean(
			final UseCaseFee useCaseFee) {
		final ManageBillPaymentFeeBean feeBean = new ManageBillPaymentFeeBean();
		feeBean.setFeeType(useCaseFee.getRequestType());
		if (PortalUtils.exists(useCaseFee.getProductId())) {
			feeBean.setProductName(new CodeValue(String.valueOf(useCaseFee
					.getProductId()), useCaseFee.getProductName()));
		}
		feeBean.setUseCaseName(new CodeValue(String.valueOf(useCaseFee
				.getUseCaseId()), useCaseFee.getUseCaseName()));
		feeBean.setApplyToPayee(useCaseFee.isIsApplyToPayee() == null ? false
				: useCaseFee.isIsApplyToPayee());
		feeBean.setUseCaseFeeId(useCaseFee.getUseCasefeeId());
		feeBean.setTransactionAmount(useCaseFee.getTransactionAmount());
		return feeBean;
	}*/

	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param UseCaseFee
	 *            List of use case fees to be converted
	 * @return List<ManageFeeBean> Manage Fee bean list
	 */
	/*public static ManageBillPaymentFeeBean convertToManageBillPayFeeBean(
			final UseCaseFee useCaseFee, final ManageBillPaymentFeeBean feeBean) {
		feeBean.setFeeType(useCaseFee.getRequestType());
		if (PortalUtils.exists(useCaseFee.getProductId())) {
			feeBean.setProductName(new CodeValue(String.valueOf(useCaseFee
					.getProductId()), useCaseFee.getProductName()));
		}
		feeBean.setUseCaseName(new CodeValue(String.valueOf(useCaseFee
				.getUseCaseId()), useCaseFee.getUseCaseName()));
		feeBean.setApplyToPayee(useCaseFee.isIsApplyToPayee() == null ? false
				: useCaseFee.isIsApplyToPayee());
		feeBean.setUseCaseFeeId(useCaseFee.getUseCasefeeId());
		feeBean.setTransactionAmount(useCaseFee.getTransactionAmount());
		return feeBean;
	}*/

	/**
	 * This method performs the conversion for Manage Fee Bean.
	 * 
	 * @param UseCaseFee
	 *            List of use case fees to be converted
	 * @return List<ManageFeeBean> Manage Fee bean list
	 */
	/*public static ManageBillPaymentFeeBean convertToManageAirtimeFeeBean(
			final UseCaseFee useCaseFee, final ManageAirtimeTopupFeeBean feeBean) {
		feeBean.setFeeType(useCaseFee.getRequestType());
		if (PortalUtils.exists(useCaseFee.getProductId())) {
			feeBean.setProductName(new CodeValue(String.valueOf(useCaseFee
					.getProductId()), useCaseFee.getProductName()));
		}
		feeBean.setUseCaseName(new CodeValue(String.valueOf(useCaseFee
				.getUseCaseId()), useCaseFee.getUseCaseName()));
		feeBean.setApplyToPayee(useCaseFee.isIsApplyToPayee() == null ? false
				: useCaseFee.isIsApplyToPayee());
		feeBean.setUseCaseFeeId(useCaseFee.getUseCasefeeId());
		feeBean.setTransactionAmount(useCaseFee.getTransactionAmount());
		return feeBean;
	}*/

	/**
	 * This method performs the conversion for Manage Fee Details Bean.
	 * 
	 * @param List
	 *            <UseCaseFee> List of use case fees to be converted
	 * @return List<ManageFeeDetailsBean> Manage Fee bean list
	 */
	public static List<ManageFeeDetailsBean> convertToManageFeeDetailsBean(
			final List<FeeConfigDetail> feeConfigDetailList) {
		final List<ManageFeeDetailsBean> feeDetailsBeanList = new ArrayList<ManageFeeDetailsBean>();
		for (FeeConfigDetail feeConfigDetail : feeConfigDetailList) {
			final ManageFeeDetailsBean feeDetailsBean = new ManageFeeDetailsBean();
			final Long fixedFeeAmount = feeConfigDetail.getFixedFeeAmount();
			final BigDecimal percentageFee = feeConfigDetail.getPercentage();
			feeDetailsBean.setFixedFee(feeConfigDetail.getFixedFeeAmount());
			feeDetailsBean.setPercentageFee(feeConfigDetail.getPercentage());
			if (PortalUtils.exists(fixedFeeAmount)
					&& !fixedFeeAmount.toString().equals("0")) {
				feeDetailsBean
						.setAmountType(BtpnConstants.FIXED_INTEREST_RADIO);
				feeDetailsBean.setAmount(String.valueOf(fixedFeeAmount / 100));
			} else if (PortalUtils.exists(percentageFee)
					&& !percentageFee.toString().equals("0")
					&& !percentageFee.toString().equals("0.0")) {
				feeDetailsBean
						.setAmountType(BtpnConstants.PERCENT_INTEREST_RADIO);
				feeDetailsBean.setAmount(String.valueOf(percentageFee));
			}
			feeDetailsBean.setGlCode(new CodeValue(String
					.valueOf(feeConfigDetail.getFeeGlCode()), feeConfigDetail
					.getFeeGlCodeName()));
			feeDetailsBean.setMaxValue(feeConfigDetail.getMaxRangeAmount());
			feeDetailsBean.setMinValue(feeConfigDetail.getMinRangeAmount());
			feeDetailsBean.setFeeName(feeConfigDetail.getFeeName());
			feeDetailsBean.setFeeTypeId(feeConfigDetail.getFeeTypeId());
			feeDetailsBean.setFeeScalePeriodId(feeConfigDetail
					.getFeeScalePeriodId());
			feeDetailsBean.setFeeScaleId(feeConfigDetail.getFeeScaleId());
			feeDetailsBeanList.add(feeDetailsBean);
		}
		return feeDetailsBeanList;
	}

	/**
	 * This method returns the FeeConfig from ManageFeeDetailsBean
	 * 
	 * @param ManageFeeDetailsBean
	 *            feeDetailsBean for Manage Fee
	 * @return FeeConfig FeeConfig for fee
	 */
	/*public static FeeConfig convertToFee(final ManageBillPaymentFeeBean feeBean,
			final ManageFeeDetailsBean feeDetailsBean) {
		final FeeConfig feeConfig = new FeeConfig();
		feeConfig.setUseCaseFee(convertToUseCaseFee(feeBean));
		feeConfig.getFees().add(convertToFeeConfig(feeDetailsBean));
		return feeConfig;
	}*/

	/**
	 * This method returns the FeeConfig from ManageFeeDetailsBean
	 * 
	 * @param ManageFeeDetailsBean
	 *            feeDetailsBean for Manage Fee
	 * @return FeeConfig FeeConfig for fee
	 */
	/*public static UseCaseFee convertToUseCaseFee(final ManageBillPaymentFeeBean feeBean) {
		final UseCaseFee useCaseFee = new UseCaseFee();
		final CodeValue useCaseName = feeBean.getUseCaseName();
		if (useCaseName != null) {
			useCaseFee.setUseCaseId(Long.valueOf(feeBean.getUseCaseName()
					.getId()));
			useCaseFee.setUseCaseName(feeBean.getUseCaseName().getValue());
		}
		final CodeValue productId = feeBean.getProductName();
		if (productId != null) {
			useCaseFee.setProductId(Integer.valueOf(productId.getId()));
			useCaseFee.setProductName(productId.getValue());
		}
		useCaseFee.setRequestType(feeBean.getFeeType());
		useCaseFee.setIsApplyToPayee(feeBean.getApplyToPayee());
		useCaseFee.getUseCasefeeId().addAll(feeBean.getUseCaseFeeId());
		useCaseFee.setTransactionAmount(feeBean.getTransactionAmount());
		return useCaseFee;
	}*/

	/**
	 * This method returns the FeeConfig from ManageFeeDetailsBean
	 * 
	 * @param ManageFeeDetailsBean
	 *            feeDetailsBean for ManageFeeDetailsBean
	 * @return List<FeeConfigDetail> Fee Config Detail for list
	 */
	public static FeeConfigDetail convertToFeeConfig(
			final ManageFeeDetailsBean feeDetailsBean) {
		final FeeConfigDetail configDetail = new FeeConfigDetail();
		configDetail.setFeeGlCode(Long.valueOf(feeDetailsBean.getGlCode()
				.getId()));
		configDetail.setFeeGlCodeName(feeDetailsBean.getGlCode().getValue());
		configDetail.setFeeName(feeDetailsBean.getFeeName());
		configDetail.setFeeScaleId(feeDetailsBean.getFeeScaleId());
		configDetail.setFeeTypeId(feeDetailsBean.getFeeTypeId());
		configDetail.setFeeScalePeriodId(feeDetailsBean.getFeeScalePeriodId());
		configDetail.setFeeTypeId(feeDetailsBean.getFeeTypeId());
		configDetail.setFixedFeeAmount(feeDetailsBean.getFixedFee());
		configDetail.setMaxRangeAmount(feeDetailsBean.getMaxValue());
		configDetail.setMinRangeAmount(feeDetailsBean.getMinValue());
		configDetail.setPercentage(feeDetailsBean.getPercentageFee());
		return configDetail;
	}

	/**
	 * This method returns UseCaseFee for the use case
	 * 
	 * @param ManageBillPaymentFeeBean
	 *            feeDetailsBean for ManageFeeBean
	 * @return UseCaseFee UseCaseFee for the use case.
	 */
	/*public static List<FeeConfigDetail> convertToAddFeeConfig(
			final ManageBillPaymentFeeBean feeBean) {
		final List<FeeConfigDetail> feeConfigDetailList = new ArrayList<FeeConfigDetail>();
		for (ManageFeeDetailsBean feeDetailsBean : feeBean
				.getManageDetailsList()) {
			feeConfigDetailList.add(convertToFeeConfig(feeDetailsBean));
		}
		return feeConfigDetailList;
	}*/

	/**
	 * This method returns UseCaseFee for the use case
	 * 
	 * @param ManageBillPaymentFeeBean
	 *            feeDetailsBean for ManageFeeBean
	 * @return UseCaseFee UseCaseFee for the use case.
	 */
	public static List<ApproveFeeBean> convertToApproveFeeBeanList(
			final List<PendingApprovalFee> approveList) {
		final List<ApproveFeeBean> approveBeanList = new ArrayList<ApproveFeeBean>();
		for (PendingApprovalFee pendingFee : approveList) {
			final ApproveFeeBean approveFeeBean = new ApproveFeeBean();
			approveFeeBean.setFeeType(pendingFee.getRequestType());
			if (pendingFee.getProductId() != null) {
				approveFeeBean.setProductName(new CodeValue(String
						.valueOf(pendingFee.getProductId()), pendingFee
						.getProductName()));
			}
			approveFeeBean.setUseCaseName(new CodeValue(String
					.valueOf(pendingFee.getUseCaseId()), pendingFee
					.getUseCaseName()));
			approveFeeBean.setRequestDate(PortalUtils.getSaveDate(pendingFee
					.getRequestDate()));
			approveFeeBean.setTaskId(pendingFee.getTaskId());
			approveBeanList.add(approveFeeBean);
		}
		return approveBeanList;

	}

	/**
	 * This method converts to Confirm Approve Bean.
	 * 
	 * @param feeDetail
	 *            PendingApprovalFeeDetail for feeDetail
	 * @return feeBean ApproveFeeBean for fees
	 */
	/*public static void convertToApproveFeeBean(
			final PendingApprovalFeeDetail feeDetail,
			final ApproveFeeConfirmBean feeBean2) {
		final FeeConfig currentConfig = feeDetail.getCurrentFee();
		final FeeConfig newConfig = feeDetail.getNewFee();

		final UseCaseFee newUseCase = newConfig.getUseCaseFee();
		feeBean2.setNewApplyToPayee(newUseCase.isIsApplyToPayee() == null ? false
				: newUseCase.isIsApplyToPayee());
		feeBean2.setNewFeeType(newUseCase.getRequestType());
		final Integer productId = newUseCase.getProductId();
		final String productName = newUseCase.getProductName();
		if (PortalUtils.exists(productId)) {
			feeBean2.setNewProductName(new CodeValue(String.valueOf(productId),
					productName));
		}
		feeBean2.setNewTransactionAmount(newUseCase.getTransactionAmount());
		final Long useCaseId = newUseCase.getUseCaseId();
		if (PortalUtils.exists(useCaseId)) {
			feeBean2.setNewUseCaseName(new CodeValue(String.valueOf(useCaseId),
					null));
		}
		feeBean2.setNewUseCaseFeeId(newUseCase.getUseCasefeeId());
		feeBean2.setNewAction(feeDetail.getFeeAction());

		if (PortalUtils.exists(currentConfig)) {
			// Setting the current object
			final UseCaseFee currentUseCase = currentConfig.getUseCaseFee();
			feeBean2.setAction(feeDetail.getFeeAction());
			feeBean2.setApplyToPayee(currentUseCase.isIsApplyToPayee() == null ? false
					: currentUseCase.isIsApplyToPayee());
			feeBean2.setFeeType(currentUseCase.getRequestType());
			final Long useCaseIdCurrent = currentUseCase.getUseCaseId();
			if (PortalUtils.exists(useCaseIdCurrent)) {
				feeBean2.setUseCaseName(new CodeValue(String
						.valueOf(useCaseIdCurrent), null));
			}
			final Integer productIdCurrent = currentUseCase.getProductId();
			final String productNameCurrent = currentUseCase.getProductName();
			if (PortalUtils.exists(productIdCurrent)) {
				feeBean2.setProductName(new CodeValue(String
						.valueOf(productIdCurrent), productNameCurrent));
			}
			feeBean2.setTransactionAmount(currentUseCase.getTransactionAmount());
			feeBean2.setUseCaseFeeId(currentUseCase.getUseCasefeeId());
		}

		convertToApproveBeanFeeList(
				currentConfig != null ? currentConfig.getFees()
						: new ArrayList<FeeConfigDetail>(),
				newConfig.getFees(), feeBean2);
	}*/

	/**
	 * This method converts to Confirm Approve Bean.
	 * 
	 * @param feeDetail
	 *            PendingApprovalFeeDetail for feeDetail
	 * @return feeBean ApproveFeeBean for fees
	 */
	public static void convertToApproveBeanFeeList(
			final List<FeeConfigDetail> currentFees,
			final List<FeeConfigDetail> newFees,
			final ApproveFeeConfirmBean feeBean2) {
		final List<ApproveFeeDetailsBean> approveBeanRangeList = feeBean2
				.getFeeDetailsBean();
		final Iterator<FeeConfigDetail> currentFeeConfigItr = currentFees
				.iterator();
		final Iterator<FeeConfigDetail> newFeeConfigItr = newFees.iterator();
		while (currentFeeConfigItr.hasNext() || newFeeConfigItr.hasNext()) {
			final ApproveFeeDetailsBean approveRangeBean = new ApproveFeeDetailsBean();
			if (currentFeeConfigItr.hasNext()) {
				final FeeConfigDetail currentConfig = currentFeeConfigItr
						.next();
				approveRangeBean.setMinValue(currentConfig.getMinRangeAmount());
				approveRangeBean.setMaxValue(currentConfig.getMaxRangeAmount());
				approveRangeBean.setFixedFee(currentConfig.getFixedFeeAmount());
				approveRangeBean
						.setPercentageFee(currentConfig.getPercentage());
				approveRangeBean.setFeeName(currentConfig.getFeeName());
				final Long currentFeeGlCode = currentConfig.getFeeGlCode();
				final String currentGlName = currentConfig.getFeeGlCodeName();
				if (PortalUtils.exists(currentFeeGlCode)) {
					approveRangeBean.setGlCode(new CodeValue(String
							.valueOf(currentFeeGlCode), currentGlName));
				}
			}
			if (newFeeConfigItr.hasNext()) {
				final FeeConfigDetail newConfig = newFeeConfigItr.next();
				approveRangeBean.setNewMinValue(newConfig.getMinRangeAmount());
				approveRangeBean.setNewMaxValue(newConfig.getMaxRangeAmount());
				approveRangeBean.setNewFixedFee(newConfig.getFixedFeeAmount());
				approveRangeBean.setNewPercentageFee(newConfig.getPercentage());
				approveRangeBean.setNewFeeName(newConfig.getFeeName());
				final Long newFeeGlCode = newConfig.getFeeGlCode();
				final String newGlName = newConfig.getFeeGlCodeName();
				if (PortalUtils.exists(newFeeGlCode)) {
					approveRangeBean.setNewGlCode(new CodeValue(String
							.valueOf(newFeeGlCode), newGlName));
				}
			}
			approveBeanRangeList.add(approveRangeBean);
		}
		feeBean2.setFeeDetailsBean(approveBeanRangeList);
	}

	/**
	 * This method converts to Limit Bean
	 */
	public static void convertToApproveLimit(
			final List<PendingApprovalLimit> limitApprovalList,
			final List<ApproveFeeBean> limitList,
			final ILookupMapUtility lookupMapUtility, final Component component) {
		for (final PendingApprovalLimit approvalLimit : limitApprovalList) {
			final ApproveFeeBean feeBean = new ApproveFeeBean();
			final Map<String, String> productCategory = lookupMapUtility
					.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_PRODUCT,
							component);
			final Map<String, String> useCases = lookupMapUtility
					.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_USECASE,
							component);
			final String productName = approvalLimit.getProductName();
			if (PortalUtils.exists(productName)) {
				final String productId = BtpnUtils.getDropdownIdFromValue(
						productCategory, productName);
				feeBean.setProductName(new CodeValue(
						productId != null ? productId
								.replace(BtpnConstants.RESOURCE_BUNDLE_PRODUCT
										+ ".", "") : "", productName));
			}
			feeBean.setFeeType(approvalLimit.getRequestType());
			final String useCaseName = approvalLimit.getUseCaseName();
			final String useCaseId = BtpnUtils.getDropdownIdFromValue(useCases,
					useCaseName);
			feeBean.setUseCaseName(new CodeValue(useCaseId != null ? useCaseId
					.replace(BtpnConstants.RESOURCE_BUNDLE_USECASE + ".", "")
					: useCaseName, useCaseName));
			feeBean.setRequestDate(PortalUtils.getSaveDate(approvalLimit
					.getRequestDate()));
			feeBean.setTaskId(approvalLimit.getTaskId());
			feeBean.setLimitClassId(approvalLimit.getLimitClassId());
			limitList.add(feeBean);
		}
	}

	/**
	 * This method converts to Limit Bean
	 */
	public static void convertToLimitBean(
			final PendingApprovalLimitDetail limit,
			final ApproveLimitBean limitBean) {
		// Current Limit and New Limit
		final Limit currentLimit = limit.getCurrentLimit();
		final Limit newLimit = limit.getNewLimit();
		if (currentLimit != null) {
			// Current Limit for the Manage Limits.
			limitBean.setAction(limit.getLimitAction());
			limitBean.setDailyLimit(currentLimit.getDailyLimit());
			limitBean.setWeeklyLimit(currentLimit.getWeeklyLimit());
			limitBean.setMonthlyLimit(currentLimit.getMonthlyLimit());
			limitBean
					.setIsApplyToPayee(currentLimit.isIsApplyToPayee() == null ? false
							: currentLimit.isIsApplyToPayee());
			limitBean.setIsPerCustomer(true);
			final LimitSummary limitSummary = currentLimit.getSummary();
			limitBean.setLimitClassId(limitSummary.getLimitClassId());
			final Integer productId = limitSummary.getCustomerTypeId();
			final String productName = limitSummary.getProductName();
			if (PortalUtils.exists(productId)) {
				limitBean.setProductId(new CodeValue(String.valueOf(productId),
						productName));
			}
			final int useCaseId = limitSummary.getUseCaseId();
			final String useCaseName = limitSummary.getUseCaseName();
			if (PortalUtils.exists(useCaseId)) {
				limitBean.setUseCaseId(new CodeValue(String.valueOf(useCaseId),
						useCaseName));
			}
		}

		// New Limit for the Manage Limits
		limitBean.setNewAction(limit.getLimitAction());
		limitBean.setNewDailyLimit(newLimit.getDailyLimit());
		limitBean.setNewWeeklyLimit(newLimit.getWeeklyLimit());
		limitBean.setNewMonthlyLimit(newLimit.getMonthlyLimit());
		limitBean
				.setNewIsApplyToPayee(newLimit.isIsApplyToPayee() == null ? false
						: newLimit.isIsApplyToPayee());
		limitBean.setNewIsPerCustomer(true);
		final LimitSummary newLimitSummary = newLimit.getSummary();
		limitBean.setLimitClassId(newLimitSummary.getLimitClassId());
		final Integer productIdNew = newLimitSummary.getCustomerTypeId();
		final String productNameNew = newLimitSummary.getProductName();
		if (PortalUtils.exists(productIdNew)) {
			limitBean.setNewProductId(new CodeValue(String
					.valueOf(productIdNew), productNameNew));
		}
		final int useCaseIdNew = newLimitSummary.getUseCaseId();
		final String useCaseNameNew = newLimitSummary.getUseCaseName();
		if (PortalUtils.exists(useCaseIdNew)) {
			limitBean.setNewUseCaseId(new CodeValue(String
					.valueOf(useCaseIdNew), useCaseNameNew));
		}
	}

	/**
	 * This method converts to Customer Registration Bean
	 */
	public static List<CustomerRegistrationBean> convertToCustomerRegistrationBean(
			List<SearchCustomerResult> customersList) {
		final List<CustomerRegistrationBean> customerList = new ArrayList<CustomerRegistrationBean>();
		for (SearchCustomerResult customer : customersList) {
			final CustomerRegistrationBean customerBean = new CustomerRegistrationBean();
			customerBean.setMobileNumber(customer.getMsisdn());
			customerBean.setName(customer.getName());
			customerBean.setCustomerType(customer.getCustomerType());
			customerBean
					.setCustomerId(String.valueOf(customer.getCustomerId()));
			customerList.add(customerBean);
		}
		return customerList;
	}

	/**
	 * This method converts to File Object.
	 */
	public static File convertToFile(FileUpload fileUpload, int fileTypeId) {
		final File file = new File();
		file.setFileContent(fileUpload.getBytes());
		file.setFileExtension(BtpnConstants.FILE_CSV_EXTENSION);
		file.setFileName(fileUpload.getClientFileName());
		file.setFileTypeId(fileTypeId);
		return file;
	}

	/**
	 * This method converts to Salary Bean List
	 */
	public static List<SalaryDataBean> convertToSalaryDataBean(
			final List<BulkFileType> txnList, final ILookupMapUtility lookupMapUtility, BtpnMobiliserBasePage basePage, Component component) {
		
		final List<SalaryDataBean> salaryDataBeanList = new ArrayList<SalaryDataBean>();
		for (final BulkFileType bulk : txnList) {
			final SalaryDataBean salaryDataBean = new SalaryDataBean();
			
			salaryDataBean.setId(bulk.getId());
			salaryDataBean.setName(bulk.getName());
			salaryDataBean.setType(bulk.getType());
		
			String status = String.valueOf( bulk.getStatus() );
			log.info("getStatus:"+status);
		
			salaryDataBean.setStatus( new CodeValue(status, BtpnUtils.getDropdownValueFromId(
					lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_BULK_FILE_PROCESSING_STATUS,
							component), BtpnConstants.RESOURCE_BUNDLE_BULK_FILE_PROCESSING_STATUS + "." + status)));
			log.info("value:"+ salaryDataBean.getStatus().getIdAndValue());
	
			salaryDataBeanList.add(salaryDataBean);
		}
		return salaryDataBeanList;
	}
	
	/**
	 * This method converts to Salary Bean List
	 */
	public static List<SalaryDataBean> convertToSalaryWrkDataBean(
		final List<BulkFileWrkType> txnList, final ILookupMapUtility lookupMapUtility, Component component) {
		
		final List<SalaryDataBean> salaryDataBeanList = new ArrayList<SalaryDataBean>();
		for (final BulkFileWrkType bulk : txnList) {
			final SalaryDataBean salaryDataBean = new SalaryDataBean();
			
			salaryDataBean.setId(bulk.getId());
			salaryDataBean.setWorkFlowId(bulk.getWorkflowId());
			salaryDataBean.setName(bulk.getName());
			salaryDataBean.setType(bulk.getType());
		
			String status = String.valueOf( bulk.getStatus() );
			log.info("getStatus:"+status);
			
			salaryDataBean.setStatus( new CodeValue(status, BtpnUtils.getDropdownValueFromId(
					lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_BULK_FILE_PROCESSING_STATUS,
							component), BtpnConstants.RESOURCE_BUNDLE_BULK_FILE_PROCESSING_STATUS + "." + status)));
			log.info("value:"+ salaryDataBean.getStatus().getIdAndValue());
	
			salaryDataBeanList.add(salaryDataBean);
		}
		return salaryDataBeanList;
	}

	/**
	 * This method converts to Salary Bean List
	 */
	public static ConsumerPortalTransactionRequest convertToCreateConsumerTransactionHistoryRequest(
			final String tz, final ConsumerTransactionRequestBean searchData,
			final long customerId) {
		final ConsumerPortalTransactionRequest request = new ConsumerPortalTransactionRequest();
		if (searchData.getFilterType().equals(
				BtpnConstants.FREQUENCY_TYPE_DAILY)) {
			request.setFromDate(PortalUtils
					.getSaveXMLGregorianCalendarFromDate(
							searchData.getFromDate(), null));
			request.setToDate(PortalUtils.getSaveXMLGregorianCalendarToDate(
					searchData.getToDate(), null));
		} else {
			final String[] monthYear = searchData.getMonth().getId().split("-");
			request.setFromDate(PortalUtils.getXmlFromDateOfMonth(null,
					monthYear[0], monthYear[1]));
			request.setToDate(PortalUtils.getXmlToDateOfMonth(null,
					monthYear[0], monthYear[1]));
		}
		request.setSubAccountId(searchData.getSubAccount() != null ? Long
				.valueOf(searchData.getSubAccount().getId()) : null);
		request.setUseCaseId(searchData.getTxnType() != null ? Integer
				.valueOf(searchData.getTxnType().getId()) : null);
		request.setCustomerId(customerId);
		return request;

	}

	public static List<ConsumerTransactionBean> convertToCreateConsumerTransactionBeanList(
			final List<ConsumerPortalTransactionResponse> responseList) {
		final List<ConsumerTransactionBean> beanList = new ArrayList<ConsumerTransactionBean>();

		for (ConsumerPortalTransactionResponse response : responseList) {
			final ConsumerTransactionBean bean = new ConsumerTransactionBean();
			final Long transactionAmount = response.getTransactionAmount();
			final Long feeAmount = response.getFeeAmount();
			if (response.isReversed()) {
				bean.setAmount(-transactionAmount);
				bean.setFee(-feeAmount);
			} else {
				bean.setAmount(transactionAmount);
				bean.setFee(feeAmount);
			}
			bean.setDetails(response.getDetails() != null ? response
					.getDetails() : "");
			bean.setDate(PortalUtils.getSaveDate(response.getTransactionDate()));
			bean.setErrorCode(response.getTransactionStatus());
			bean.setName(response.getParticipantName());
			bean.setTxnId(String.valueOf(response.getTransactionId()));
			bean.setType(response.getTransactionType());
			beanList.add(bean);
		}
		return beanList;
	}
	
	/*
	public static List<ConsumerTransactionBean> convertToConsumerTransactionHistoryBeanList(
			final List<TransactionHistoryFindView> responseList) {
		
		final List<ConsumerTransactionBean> beanList = new ArrayList<ConsumerTransactionBean>();

		for (TransactionHistoryFindView response : responseList) {
			final ConsumerTransactionBean bean = new ConsumerTransactionBean();
			bean.setId(response.getId());
			bean.setDate(PortalUtils.getSaveDate(response.getTransactionDateTime()));	
			bean.setUseCaseId(response.getUseCaseId());
			bean.setReserved1(response.getReserved1());
			bean.setAmount(response.getAmount());
			bean.setFee(response.getFee());
			bean.setBalance(response.getBalance());
			response.getDescription();
			beanList.add(bean);
		}
		return beanList;
	}*/
	

	public static BankStaffBean convertFromBankStaffProfile(
			BankUserProfile profile) {
		final BankStaffBean bankStaff = new BankStaffBean();
		bankStaff.setCustomerId(profile.getCustomerId());
		bankStaff.setDesignation(profile.getDesignation());
		bankStaff.setEmail(profile.getEmail());
		bankStaff.setTerritoryCode(profile.getTerritoryCode());
		bankStaff.setName(profile.getName());
		return bankStaff;
	}

	public static BankUserProfile convertToBankStaffProfile(
			final BankStaffBean bankStaff) {
		final BankUserProfile profile = new BankUserProfile();
		profile.setCustomerId(bankStaff.getCustomerId());
		profile.setDesignation(bankStaff.getDesignation());
		profile.setEmail(bankStaff.getEmail());
		profile.setName(bankStaff.getName());
		profile.setTerritoryCode(bankStaff.getTerritoryCode());
		return profile;
	}

	public static BankTransactionReportRequest convertToBankTransactionReportRequest(
			final BankTransactionDetailsReportRequestBean requestBean,
			BankTransactionReportRequest request) {
		final BankTransactionReportRequestBean reportRequest = new BankTransactionReportRequestBean();
		reportRequest.setAgentMsisdn(requestBean.getAgentMsisdn());
		reportRequest.setFromDate(PortalUtils
				.getSaveXMLGregorianCalendarFromDate(requestBean.getFromDate(),
						null));
		reportRequest.setToDate(PortalUtils.getSaveXMLGregorianCalendarToDate(
				requestBean.getToDate(), null));
		reportRequest
				.setReportScope(requestBean.getReportScope() != null ? Integer
						.valueOf(requestBean.getReportScope().getId()) : 0);
		reportRequest.setTxnType(requestBean.getTxnType() != null ? Integer
				.valueOf(requestBean.getTxnType().getId()) : null);
		request.setReportRequest(reportRequest);
		return request;
	}

	public static List<TransactionDetailsReportAgentBean> convertToTransactionDetailsReportAgentBeanList(
			List<BankTransactionReportResponseBean> reportList,
			final String agentType) {
		List<TransactionDetailsReportAgentBean> transactionList = new ArrayList<TransactionDetailsReportAgentBean>();
		for (BankTransactionReportResponseBean bean : reportList) {
			final TransactionDetailsReportAgentBean detailBean = new TransactionDetailsReportAgentBean();
			detailBean.setAgentId(bean.getAgentId());
			detailBean.setAgentType(agentType);
			detailBean.setAmount(bean.getTransactionAmount());
			detailBean.setBeneificary(bean.getBeneficiary());
			detailBean.setBiller(bean.getBill());
			detailBean.setCustomerAccount(bean.getConsumerMobile());
			detailBean.setDate(PortalUtils.getSaveDate(bean
					.getTransactionDate()));
			detailBean.setTransactionType(bean.getTransactionType());
			transactionList.add(detailBean);
		}
		return transactionList;
	}

	/**
	 * This is the manual advice bean list for Manual Advices.
	 * 
	 * @param adviceBeansList
	 * @return List<ManualAdviceBean>
	 */
	public static List<ManualAdviceBean> convertToManualAdviceList(
			final List<AdviceBean> adviceBeansList) {
		final List<ManualAdviceBean> manualAdviceList = new ArrayList<ManualAdviceBean>();
		for (final AdviceBean adviceBean : adviceBeansList) {
			final ManualAdviceBean manualAdviceBean = new ManualAdviceBean();
			manualAdviceBean.setAmount(adviceBean.getFeeAmount());
			manualAdviceBean.setBillerId(manualAdviceBean.getBillerId());
			manualAdviceBean.setFeeAmount(manualAdviceBean.getFeeAmount());
			manualAdviceBean.setTransactionDate(manualAdviceBean
					.getTransactionDate());
			manualAdviceBean.setTransactionId(manualAdviceBean
					.getTransactionId());
			manualAdviceList.add(manualAdviceBean);
		}
		return manualAdviceList;
	}

	public static LimitExType convertToLimitExType(final ElimitBean bean) {
		LimitExType object = new LimitExType();

		GregorianCalendar cal = new GregorianCalendar();
		GregorianCalendar calFrom = new GregorianCalendar();
		GregorianCalendar calTo = new GregorianCalendar();
		if(bean.getCreationDate()!=null){
			cal.setTime(bean.getCreationDate());
			XMLGregorianCalendar date=null;
			try {
				date = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}		
//			object.setCreationDate(date);
		}
		
		if(bean.getDateFrom()!=null){
			calFrom.setTime(bean.getDateFrom());
			XMLGregorianCalendar date = null;
			try {
				date = DatatypeFactory.newInstance().newXMLGregorianCalendar(calFrom);
			} catch (Exception e) {
				e.printStackTrace();
			}
//			object.setCreationDateFrom(date);
		}
		
		if(bean.getDateTo()!=null){
			calTo.setTime(bean.getDateTo());
			XMLGregorianCalendar date = null;
			try {
				date = DatatypeFactory.newInstance().newXMLGregorianCalendar(calTo);
			} catch (Exception e) {
				e.printStackTrace();
			}
//			object.setCreationDateTo(date);
		}

		object.setCreator(bean.getCreator());
		object.setId(bean.getId());
		object.setDescription(bean.getDescription());
		object.setCustomerId(bean.getCustomer());
		if(bean.getSelectedCustomerType()!=null){
			CodeValue customerType = bean.getSelectedCustomerType();
			object.setCustomerType(Long.valueOf(customerType.getId()));
		}else{
			object.setCustomerType(bean.getCustomerType());
		}
		if(bean.getMaximumBalance()!=null)
			object.setMaxBalance(bean.getMaximumBalance());
		if(bean.getMinimumBalance()!=null)
			object.setMinBalance(bean.getMinimumBalance());
		object.setPiId(bean.getPi());
		if(bean.getSelectedPiType()!=null){
			CodeValue piType = bean.getSelectedPiType();
			object.setPiType(Long.valueOf(piType.getId()));
		}else{
			object.setPiType(bean.getPiType());
		}
		
		if(bean.getSingleCreditMaxAmount()!=null)
			object.setSingleCreditMaxAmount(bean.getSingleCreditMaxAmount());
		if(bean.getSingleCreditMinAmount()!=null)
			object.setSingleCreditMinAmount(bean.getSingleCreditMinAmount());
		if(bean.getSingleDebitMaxAmount()!=null)
			object.setSingleDebitMaxAmount(bean.getSingleDebitMaxAmount());
		if(bean.getSingleDebitMinAmount()!=null)
			object.setSingleDebitMinAmount(bean.getSingleDebitMinAmount());
		
		if(bean.getSelectedUseCases()!=null){
			CodeValue useCase = bean.getSelectedUseCases();
			object.setUseCase(Long.valueOf(useCase.getId()));
		}else{
			object.setUseCase(bean.getUseCase());
		}
		
		if(bean.getDailyCreditMaxAmount()!=null)
			object.setDailyCreditMaxAmount(bean.getDailyCreditMaxAmount());
		if(bean.getDailyDebitMaxAmount()!=null)
			object.setDailyDebitMaxAmount(bean.getDailyDebitMaxAmount());
		if(bean.getWeeklyCreditMaxAmount()!=null)
			object.setWeeklyCreditMaxAmount(bean.getWeeklyCreditMaxAmount());
		if(bean.getWeeklyDebitMaxAmount()!=null)
			object.setWeeklyDebitMaxAmount(bean.getWeeklyDebitMaxAmount());
		if(bean.getMonthlyCreditMaxAmount()!=null)
			object.setMonthlyCreditMaxAmount(bean.getMonthlyCreditMaxAmount());
		if(bean.getMonthlyDebitMaxAmount()!=null)
			object.setMonthlyDebitMaxAmount(bean.getMonthlyDebitMaxAmount());
		
		object.setDailyCreditMaxCount(bean.getDailyCreditMaxCount());
		object.setDailyDebitMaxCount(bean.getDailyDebitMaxCount());
		object.setWeeklyCreditMaxCount(bean.getWeeklyCreditMaxCount());
		object.setWeeklyDebitMaxCount(bean.getWeeklyDebitMaxCount());
		object.setMonthlyCreditMaxCount(bean.getMonthlyCreditMaxCount());
		object.setMonthlyDebitMaxCount(bean.getMonthlyDebitMaxCount());

		
		LimitExMultiply100(object);
		
		return object;
	}

	public static ElimitBean convertToLimitBean(LimitExType bean, final Component component, 
					BtpnMobiliserBasePage basePage) {
		
		ElimitBean object = new ElimitBean();
		object.setId(bean.getId());
		
		object.setCustomer(bean.getCustomerId());
		object.setDescription(bean.getDescription());
		if(bean.getCreationDate()!=null){
			object.setCreationDate(bean.getCreationDate().toGregorianCalendar().getTime());
		}
		object.setCreator(bean.getCreator());
		object.setDescription(bean.getDescription());
//		if(bean.getCreationDate()!=null){
//			object.setCreationDate(bean.getCreationDate().toGregorianCalendar().getTime());
//		}

		
		if (bean.getCustomerType() != null) {
			object.setSelectedCustomerType(new CodeValue(bean.getCustomerType().toString(), 
					BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
							BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, component), 
							BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE + "." +bean.getCustomerType().toString())
					));
		}
		
		if (bean.getPiType() != null) {
			object.setSelectedPiType(new CodeValue(bean.getPiType().toString(), 
				BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
						BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, component), 
						BtpnConstants.RESOURCE_BUNDLE_PI_TYPE + "." +bean.getPiType().toString())
				));
		}
		
		if (bean.getUseCase() != null) {
		
			object.setSelectedUseCases(new CodeValue(bean.getUseCase().toString(), 
				BtpnUtils.getDropdownValueFromId(basePage.getLookupMapUtility().getLookupNamesMap(
						BtpnConstants.RESOURCE_BUNDLE_USECASE, component), 
						BtpnConstants.RESOURCE_BUNDLE_USECASE + "." +bean.getUseCase().toString())
				));
		}
		
		object.setPi(bean.getPiId());
		object.setMaximumBalance(bean.getMaxBalance());
		object.setMinimumBalance(bean.getMinBalance());
		object.setSingleCreditMaxAmount(bean.getSingleCreditMaxAmount());
		object.setSingleCreditMinAmount(bean.getSingleCreditMinAmount());
		object.setSingleDebitMaxAmount(bean.getSingleDebitMaxAmount());
		object.setSingleDebitMinAmount(bean.getSingleDebitMinAmount());
		object.setDailyCreditMaxAmount(bean.getDailyCreditMaxAmount());
		object.setDailyCreditMaxCount(bean.getDailyCreditMaxCount());
		object.setDailyDebitMaxAmount(bean.getDailyDebitMaxAmount());
		object.setDailyDebitMaxCount(bean.getDailyDebitMaxCount());
		object.setWeeklyCreditMaxAmount(bean.getWeeklyCreditMaxAmount());
		object.setWeeklyCreditMaxCount(bean.getWeeklyCreditMaxCount());
		object.setWeeklyDebitMaxAmount(bean.getWeeklyDebitMaxAmount());
		object.setWeeklyDebitMaxCount(bean.getWeeklyDebitMaxCount());
		object.setMonthlyCreditMaxAmount(bean.getMonthlyCreditMaxAmount());
		object.setMonthlyCreditMaxCount(bean.getMonthlyCreditMaxCount());
		object.setMonthlyDebitMaxAmount(bean.getMonthlyDebitMaxAmount());
		object.setMonthlyDebitMaxCount(bean.getMonthlyDebitMaxCount());
		
				
		LimitExNormalize100(object);
		
		return object;
	}

	public static List<ElimitBean> convertToListLimitBean(List<LimitExType> listExTypes, 
			final Component component, 
			BtpnMobiliserBasePage basePage ) {
		
		List<ElimitBean> listBean = new ArrayList<ElimitBean>();
		for (LimitExType type : listExTypes) {
			try {
				ElimitBean bean = new ElimitBean();
				bean = convertToLimitBean(type, component, basePage);
				listBean.add(bean);
				
			} catch (Exception e) {
				e.printStackTrace();	
			}

		}
		return listBean;

  }
	
	public static LimitExType LimitExMultiply100(LimitExType object){
		
		
		if(object.getMaxBalance()!=null){
			object.setMaxBalance(object.getMaxBalance()*100);
		}
		if(object.getMinBalance()!=null){
			object.setMinBalance(object.getMinBalance()*100);
		}
		if(object.getSingleCreditMaxAmount()!=null){
			object.setSingleCreditMaxAmount(object.getSingleCreditMaxAmount()*100);
		}
		if(object.getSingleCreditMinAmount()!=null){
			object.setSingleCreditMinAmount(object.getSingleCreditMinAmount()*100);
		}
		if(object.getSingleDebitMaxAmount()!=null){
			object.setSingleDebitMaxAmount(object.getSingleDebitMaxAmount()*100);
		}
		if(object.getSingleDebitMinAmount()!=null){
			object.setSingleDebitMinAmount(object.getSingleDebitMinAmount()*100);
		}
		if(object.getDailyCreditMaxAmount()!=null){
			object.setDailyCreditMaxAmount(object.getDailyCreditMaxAmount()*100);
		}
		if(object.getDailyDebitMaxAmount()!=null){
			object.setDailyDebitMaxAmount(object.getDailyDebitMaxAmount()*100);
		}
		if(object.getWeeklyCreditMaxAmount()!=null){
			object.setWeeklyCreditMaxAmount(object.getWeeklyCreditMaxAmount()*100);
		}
		if(object.getWeeklyDebitMaxAmount()!=null){
			object.setWeeklyDebitMaxAmount(object.getWeeklyDebitMaxAmount()*100);
		}
		if(object.getMonthlyCreditMaxAmount()!=null){
			object.setMonthlyCreditMaxAmount(object.getMonthlyCreditMaxAmount()*100);
		}
		if(object.getMonthlyDebitMaxAmount()!=null){
			object.setMonthlyDebitMaxAmount(object.getMonthlyDebitMaxAmount()*100);
		}
		
		
		return object;
	}
	
	
	
	public static ElimitBean LimitExNormalize100( ElimitBean object){
		
		if(object.getMaximumBalance()!=null){
			object.setMaximumBalance(object.getMaximumBalance()/100);
		}
		if(object.getMinimumBalance()!=null){
			object.setMinimumBalance(object.getMinimumBalance()/100);
		}
		if(object.getSingleCreditMaxAmount()!=null){
			object.setSingleCreditMaxAmount(object.getSingleCreditMaxAmount()/100);
		}
		if(object.getSingleCreditMinAmount()!=null){
			object.setSingleCreditMinAmount(object.getSingleCreditMinAmount()/100);
		}
		if(object.getSingleDebitMaxAmount()!=null){
			object.setSingleDebitMaxAmount(object.getSingleDebitMaxAmount()/100);
		}
		if(object.getSingleDebitMinAmount()!=null){
			object.setSingleDebitMinAmount(object.getSingleDebitMinAmount()/100);
		}
		if(object.getDailyCreditMaxAmount()!=null){
			object.setDailyCreditMaxAmount(object.getDailyCreditMaxAmount()/100);
		}
		if(object.getDailyDebitMaxAmount()!=null){
			object.setDailyDebitMaxAmount(object.getDailyDebitMaxAmount()/100);
		}
		if(object.getWeeklyCreditMaxAmount()!=null){
			object.setWeeklyCreditMaxAmount(object.getWeeklyCreditMaxAmount()/100);
		}
		if(object.getWeeklyDebitMaxAmount()!=null){
			object.setWeeklyDebitMaxAmount(object.getWeeklyDebitMaxAmount()/100);
		}
		if(object.getMonthlyCreditMaxAmount()!=null){
			object.setMonthlyCreditMaxAmount(object.getMonthlyCreditMaxAmount()/100);
		}
		if(object.getMonthlyDebitMaxAmount()!=null){
			object.setMonthlyDebitMaxAmount(object.getMonthlyDebitMaxAmount()/100);
		}

		
		return object;
	}
	
	
	/**
	 * This method performs the conversion for Manage Interest Bean.
	 * 
	 * @param List<ManageInterestBean> List of interest to be converted
	 * @return List<ManageInterestBean> ManageInterestBean bean list
	 */
	public static List<ManageInterestBean> convertToManageInterestBean(
			final List<InterestFindViewType> txnList) {
		
		final List<ManageInterestBean> interestList = new ArrayList<ManageInterestBean>();
		for (final InterestFindViewType uc : txnList) {
			final ManageInterestBean interestBean = new ManageInterestBean();
			log.info("### (ConverterUtils) convertToManageInterestBean ID ### "+uc.getId());
			interestBean.setId(uc.getId());
			log.info("### (ConverterUtils) convertToManageInterestBean DESC ### "+uc.getDescription());
			interestBean.setDescription(uc.getDescription());
			interestList.add(interestBean);
		}
		return interestList;
	}

	
	/**
	 * This method performs the conversion for Manage Interest Bean.
	 * 
	 * @param List<ManageInterestApproveBean> List of interest approval to be converted
	 * @return List<ManageInterestApproveBean> ManageInterestApproveBean bean list
	 */
	public static List<ManageInterestApproveBean> convertToManageInterestWrkBean(
			final List<InterestWrkFindViewType> txnList) {   
		
		final List<ManageInterestApproveBean> interestList = new ArrayList<ManageInterestApproveBean>();
		for (final InterestWrkFindViewType uc : txnList) {
			final ManageInterestApproveBean interestBean = new ManageInterestApproveBean();
			log.info("### (ConverterUtils) convertToManageInterestWrkBean WF ID ### "+uc.getWorkflowId());
			interestBean.setWorkFlowId(uc.getWorkflowId());
			log.info("### (ConverterUtils) convertToManageInterestWrkBean DESC ### "+uc.getDescription());
			interestBean.setDescription(uc.getDescription());
			interestBean.setLastModifiedById(uc.getLastModifiedById());
			interestBean.setLastModifiedByName(uc.getLastModifiedByName());
			interestList.add(interestBean);
		}
		return interestList;
	}

	
	/**
	 * This method performs the conversion for Manage Interest Bean.
	 * 
	 * @param List<ManageInterestTaxBean> List of interest to be converted
	 * @return List<ManageInterestTaxBean> ManageInterestTaxBean bean list
	 */
	public static List<ManageInterestTaxBean> convertToManageInterestTaxBean(
			final List<InterestTaxFindViewType> txnList) {
		
		final List<ManageInterestTaxBean> interestList = new ArrayList<ManageInterestTaxBean>();
		for (final InterestTaxFindViewType uc : txnList) {
			final ManageInterestTaxBean interestTaxBean = new ManageInterestTaxBean();
			log.info("### (ConverterUtils) convertToManageInterestBean ID ### "+uc.getId());
			interestTaxBean.setId(uc.getId());
			interestTaxBean.setDescription(uc.getDescription());
			log.info("### (ConverterUtils) convertToManageInterestBean DESC ### "+uc.getDescription());
			interestList.add(interestTaxBean);
		}
		return interestList;
	}

	
	/**
	 * This method performs the conversion for Manage Interest Bean.
	 * 
	 * @param List<ManageInterestTaxApproveBean> List of interest approval to be converted
	 * @return List<ManageInterestTaxApproveBean> ManageInterestApproveBean bean list
	 */
	public static List<ManageInterestTaxApproveBean> convertToManageInterestTaxWrkBean(
			final List<InterestTaxWrkFindViewType> txnList) {   
		
		final List<ManageInterestTaxApproveBean> interestTaxList = new ArrayList<ManageInterestTaxApproveBean>();
		for (final InterestTaxWrkFindViewType uc : txnList) {
			final ManageInterestTaxApproveBean interestTaxBean = new ManageInterestTaxApproveBean();
			log.info("### (ConverterUtils) convertToManageInterestTaxWrkBean WF ID ### "+uc.getWorkflowId());
			interestTaxBean.setWorkFlowId(uc.getWorkflowId());
			log.info("### (ConverterUtils) convertToManageInterestTaxWrkBean DESC ### "+uc.getDescription());
			interestTaxBean.setDescription(uc.getDescription());
			interestTaxBean.setLastModifiedById(uc.getLastModifiedById());
			interestTaxBean.setLastModifiedByName(uc.getLastModifiedByName());
			interestTaxList.add(interestTaxBean);
		}
		return interestTaxList;
	}
}
