package com.sybase365.mobiliser.web.application.pages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.Component;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.Streams;
import org.springframework.aop.target.dynamic.Refreshable;

import com.btpnwow.core.security.facade.api.SecurityFacade;
import com.btpnwow.core.security.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.security.facade.contract.SendOtpRequest;
import com.btpnwow.core.security.facade.contract.SendOtpResponse;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserRequestType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MobiliserResponseType;
import com.sybase365.mobiliser.framework.contract.v5_0.base.MoneyAmount;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateAddressRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateAddressResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateAttachmentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateAttachmentResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateFullCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateFullCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateIdentificationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateIdentityRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateIdentityResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateNonPersistentOtpRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.CreateNonPersistentOtpResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.DeleteCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.FindHierarchicalCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAddressByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetAddressByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerByIdentificationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerHistoryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerHistoryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerNetworksRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerNetworksResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetIdentificationsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetIdentificationsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrCustomerPrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrCustomerPrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrCustomerRolesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrCustomerRolesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrPrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrPrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrRolesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.GetUmgrRolesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateAddressRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateAddressResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateIdentificationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.UpdateIdentificationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Attachment;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedAttachment;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedCredential;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerAttachedIdentification;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerNetwork;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerPrivilege;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerRole;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.CustomerType;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.HierarchicalCustomer;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.HistoryEntry;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrPrivilege;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.UmgrRole;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.CheckCredentialStrengthRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.CheckCredentialStrengthResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GenerateTemporaryCredentialRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GenerateTemporaryCredentialResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GetAllPrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GetAllPrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GetDefaultPrivilegesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.GetDefaultPrivilegesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.LogoutRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.LogoutResponse;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.SetCredentialRequest;
import com.sybase365.mobiliser.money.contract.v5_0.customer.security.SetCredentialResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.CreateInvoiceConfigurationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.CreateInvoiceConfigurationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.CreateInvoiceForInvoiceTypeRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.CreateInvoiceForInvoiceTypeResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.DeleteInvoiceConfigurationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.DeleteInvoiceConfigurationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetAllInvoiceTypesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetAllInvoiceTypesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetInvoiceConfigurationsByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetInvoiceConfigurationsByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetInvoiceRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetInvoiceResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetInvoiceTypesByGroupRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetInvoiceTypesByGroupResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetInvoicesByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.GetInvoicesByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.UpdateInvoiceConfigurationRequest;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.UpdateInvoiceConfigurationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.Invoice;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceConfiguration;
import com.sybase365.mobiliser.money.contract.v5_0.invoice.beans.InvoiceType;
import com.sybase365.mobiliser.money.contract.v5_0.ping.PingRequest;
import com.sybase365.mobiliser.money.contract.v5_0.ping.PingResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateLimitClassRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateLimitClassResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateLimitSetClassRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateLimitSetClassResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateLimitSetRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.CreateLimitSetResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteFeeSetRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteFeeSetResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteLimitClassRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteLimitClassResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteLimitSetClassRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteLimitSetClassResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteLimitSetRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.DeleteLimitSetResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetCustomerTypesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetCustomerTypesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetFeeSetsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetFeeSetsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetFeeTypesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetFeeTypesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLimitClassesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLimitClassesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLimitSetClassesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLimitSetClassesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLimitSetsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLimitSetsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLookupsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetLookupsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetOrgUnitsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetOrgUnitsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetRestrictionsGroupsRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetRestrictionsGroupsResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetRiskCategoriesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetRiskCategoriesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetScaleStepsByFeeTypeAndFeeSetRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetScaleStepsByFeeTypeAndFeeSetResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetUseCasesFeeTypesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.GetUseCasesFeeTypesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateLimitClassRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateLimitClassResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateLimitSetClassRequest;
import com.sybase365.mobiliser.money.contract.v5_0.system.UpdateLimitSetClassResponse;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.CurrencyScaleSteps;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeType;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.FeeTypeCurrencyScaleSteps;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitClass;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSet;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LimitSetClass;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.LookupEntity;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.OrgUnit;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RestrictionInfo;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RestrictionsGroup;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.RiskCategory;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.ScaleStep;
import com.sybase365.mobiliser.money.contract.v5_0.system.beans.UseCaseFeeType;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.Authorisation;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.AuthorisationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.CancelInvoiceRequest;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.CancelInvoiceResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.CheckPayInvoice;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.CheckPayInvoiceResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.ContinuePayInvoice;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.ContinuePayInvoiceResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PreAuthorisation;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PreAuthorisationContinue;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PreAuthorisationContinueResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.PreAuthorisationResponse;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.MoneyFeeType;
import com.sybase365.mobiliser.money.contract.v5_0.transaction.beans.SimpleTransaction;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.BalanceInquiryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.BalanceInquiryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.ContinuePendingWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.ContinuePendingWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreatePaymentInstrumentRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreatePaymentInstrumentResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetPaymentInstrumentsByCustomerRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetPaymentInstrumentsByCustomerResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetWalletEntriesRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetWalletEntriesResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.GetWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BankAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.CreditCard;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.ExternalAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PaymentInstrument;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.PendingWalletEntry;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.money.coupon.service.api.ICouponsEndpoint;
import com.sybase365.mobiliser.money.services.api.IAddressEndpoint;
import com.sybase365.mobiliser.money.services.api.IAttachmentEndpoint;
import com.sybase365.mobiliser.money.services.api.IAuthorisationCancelEndpoint;
import com.sybase365.mobiliser.money.services.api.IAuthorisationEndpoint;
import com.sybase365.mobiliser.money.services.api.IBalanceAlertEndpoint;
import com.sybase365.mobiliser.money.services.api.IBulkProcessingEndpoint;
import com.sybase365.mobiliser.money.services.api.ICancelInvoiceEndpoint;
import com.sybase365.mobiliser.money.services.api.ICaptureCancelEndpoint;
import com.sybase365.mobiliser.money.services.api.ICaptureEndpoint;
import com.sybase365.mobiliser.money.services.api.ICheckPayInvoiceEndpoint;
import com.sybase365.mobiliser.money.services.api.ICommissionClearingEndpoint;
import com.sybase365.mobiliser.money.services.api.ICommissionConfigurationEndpoint;
import com.sybase365.mobiliser.money.services.api.IConfirmVoucherEndpoint;
import com.sybase365.mobiliser.money.services.api.IContinuePayInvoiceEndpoint;
import com.sybase365.mobiliser.money.services.api.ICustomerEndpoint;
import com.sybase365.mobiliser.money.services.api.ICustomerNetworksEndpoint;
import com.sybase365.mobiliser.money.services.api.IDemandForPaymentEndpoint;
import com.sybase365.mobiliser.money.services.api.IFeeEndpoint;
import com.sybase365.mobiliser.money.services.api.IFindRemittanceVoucherEndpoint;
import com.sybase365.mobiliser.money.services.api.IIdentificationEndpoint;
import com.sybase365.mobiliser.money.services.api.IIdentityEndpoint;
import com.sybase365.mobiliser.money.services.api.IInvoiceEndpoint;
import com.sybase365.mobiliser.money.services.api.IJobEndpoint;
import com.sybase365.mobiliser.money.services.api.ILimitEndpoint;
import com.sybase365.mobiliser.money.services.api.INoteEndpoint;
import com.sybase365.mobiliser.money.services.api.IOtpEndpoint;
import com.sybase365.mobiliser.money.services.api.IPayInvoiceEndpoint;
import com.sybase365.mobiliser.money.services.api.IPingEndpoint;
import com.sybase365.mobiliser.money.services.api.IPreAuthContinueEndpoint;
import com.sybase365.mobiliser.money.services.api.IPreAuthorisationEndpoint;
import com.sybase365.mobiliser.money.services.api.IPrePickupMoneyEndpoint;
import com.sybase365.mobiliser.money.services.api.ISecurityEndpoint;
import com.sybase365.mobiliser.money.services.api.IStartVoucherEndpoint;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.money.services.api.ITransactionCancelEndpoint;
import com.sybase365.mobiliser.money.services.api.ITransactionEndpoint;
import com.sybase365.mobiliser.money.services.api.ITransactionRestrictionEndpoint;
import com.sybase365.mobiliser.money.services.api.IUmgrRolesPrivilegesEndpoint;
import com.sybase365.mobiliser.money.services.api.IWalletEndpoint;
import com.sybase365.mobiliser.money.services.api.IWebContinueEndpoint;
import com.sybase365.mobiliser.money.services.api.IWebStartEndpoint;
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
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerAlertRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.DeleteCustomerAlertResponse;
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
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetCustomerOtherIdentificationByCustomerRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.GetCustomerOtherIdentificationByCustomerResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerAlertRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerAlertResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerOtherIdentificationRequest;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.UpdateCustomerOtherIdentificationResponse;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.AlertType;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerAlert;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerBlackoutSchedule;
import com.sybase365.mobiliser.util.alerts.contract.v5_0.beans.CustomerOtherIdentification;
import com.sybase365.mobiliser.util.alerts.services.api.IAlertTypeManagementEndpoint;
import com.sybase365.mobiliser.util.alerts.services.api.ICustomerAlertManagementEndpoint;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeCompositeValueRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeCompositeValueResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeValueRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeValueResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeValuesByCompositeKeyRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeValuesByCompositeKeyResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeValuesRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanAttributeValuesResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanInfoRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanInfoResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanNotificationsRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.GetMBeanNotificationsResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.InvokeMBeanOperationRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.InvokeMBeanOperationResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.QueryMBeansRequest;
import com.sybase365.mobiliser.util.contract.v5_0.management.QueryMBeansResponse;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.AttributeListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.CompositeAttributeBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.CompositeAttributeValueListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeCompositeValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeValueBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanAttributeValueListBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanInfoBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanOperationInfoBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.MBeanParameterInfoBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.NotificationBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectInstanceBean;
import com.sybase365.mobiliser.util.contract.v5_0.management.beans.ObjectNameBean;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.GetDetailedTemplateRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.GetDetailedTemplateResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.SendTemplateRequest;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.SendTemplateResponse;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Map;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageAttachment;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageDetails;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageTemplate;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Receiver;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.TemplateMessage;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Map.Entry;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.CreatePreferencesApplicationRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.CreatePreferencesApplicationResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.DeletePreferencesApplicationRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.DeletePreferencesApplicationResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.GetDetailedPreferencesRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.GetDetailedPreferencesResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.GetPreferencesApplicationRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.GetPreferencesApplicationResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.GetPreferencesApplicationsRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.GetPreferencesApplicationsResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.GetPreferencesRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.GetPreferencesResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.RemovePreferencesNodeRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.RemovePreferencesNodeResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.RemovePreferencesValueRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.RemovePreferencesValueResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.SetPreferencesValueRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.SetPreferencesValueResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.UpdatePreferencesApplicationRequest;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.UpdatePreferencesApplicationResponse;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedMap;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedPreference;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.DetailedPreferencesTree;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.PreferencesApplication;
import com.sybase365.mobiliser.util.contract.v5_0.prefs.beans.PreferencesTree;
import com.sybase365.mobiliser.util.management.services.api.IManagementEndpoint;
import com.sybase365.mobiliser.util.messaging.service.api.IMessageLogService;
import com.sybase365.mobiliser.util.messaging.service.api.IMessagingService;
import com.sybase365.mobiliser.util.messaging.service.api.ITemplateService;
import com.sybase365.mobiliser.util.prefs.api.IPreferencesService;
import com.sybase365.mobiliser.util.prefs.encryption.api.IPreferencesEncryptionManager;
import com.sybase365.mobiliser.util.prefs.service.api.IPreferencesEndpoint;
import com.sybase365.mobiliser.util.report.service.api.IReportEndpoint;
import com.sybase365.mobiliser.util.tools.clientutils.api.IClientConfiguration;
import com.sybase365.mobiliser.util.tools.encryptionutils.AsymmetricKeyUtils;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.components.BasePage;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.LookupResourceLoader;
import com.sybase365.mobiliser.web.beans.AlertContactPointBean;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.PreferenceBean;
import com.sybase365.mobiliser.web.beans.RestrictionSetBean;
import com.sybase365.mobiliser.web.beans.ScaleStepConfBean;
import com.sybase365.mobiliser.web.beans.SelectBean;
import com.sybase365.mobiliser.web.beans.TransactionBean;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.common.components.KeyValue;
import com.sybase365.mobiliser.web.common.dataproviders.DataProviderLoadException;
import com.sybase365.mobiliser.web.cst.pages.customercare.BankAccountDataPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.CreditCardDataPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.ExternalAccountDataPage;
import com.sybase365.mobiliser.web.cst.pages.customercare.FindPendingWalletPage;
import com.sybase365.mobiliser.web.util.AuthorisationCallThread;
import com.sybase365.mobiliser.web.util.Configuration;
import com.sybase365.mobiliser.web.util.Constants;
import com.sybase365.mobiliser.web.util.Converter;
import com.sybase365.mobiliser.web.util.DynamicServiceConfiguration;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;
import com.sybase365.mobiliser.web.util.PhoneNumber;
import com.sybase365.mobiliser.web.util.PortalUtils;
//import com.sybase365.mobiliser.web.util.notificationmgr.jaxb.MessageTemplates;
import com.sybase365.mobiliser.web.util.notificationmgr.jaxb.MessageTemplates;

/**
 * The base class of each web page compiling useful methods which are most
 * likely needed by each web page implementation
 * 
 * @author sschweit
 * 
 */
public abstract class MobiliserBasePage extends BasePage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(MobiliserBasePage.class);

    private static List<KeyValue<Integer, String>> FEE_TYPES;
    private static List<KeyValue<Boolean, String>> YES_NO_OPTIONS;
    private static List<KeyValue<String, String>> BANK_LIST;
    private static List<KeyValue<Long, String>> INVOICE_TYPE_CUSTOMER_ADDS_LIST;
    private static List<KeyValue<Long, String>> INVOICE_TYPE_CURRENCY_LIST;
    private static List<KeyValue<Long, String>> INVOICE_TYPE_CONFIG_REF_PATTERN_LIST;
    private static List<KeyValue<Long, String>> INVOICE_TYPE_INVOICE_REF_PATTERN_LIST;
    private static List<KeyValue<Long, String>> INVOICE_TYPE_FULL_LIST;
    private static List<KeyValue<Long, String>> INVOICE_TYPE_OPERATOR_LIST;

    List<FeeSet> feeSetList = null;
    List<InvoiceType> invoiceTypes = null;
    private boolean createStatus = false;
    List<UmgrRole> roleList;
    List<UmgrPrivilege> privilegeList;

    @SpringBean(name = "configuration")
    private Configuration configuration;

    @SpringBean(name = "serviceConfiguration")
    private IClientConfiguration clientConfiguration;

    // initialise the web service clients
    @SpringBean(name = "smartAuthAlertTypeManagementClient")
    public IAlertTypeManagementEndpoint wsAlertTypesClient;
    @SpringBean(name = "smartAuthCustomerAlertsClient")
    public ICustomerAlertManagementEndpoint wsCustomerAlertsClient;
    @SpringBean(name = "smartAuthCustomerClient")
    public ICustomerEndpoint wsCustomerClient;
    @SpringBean(name = "smartAuthIdentificationClient")
    public IIdentificationEndpoint wsIdentClient;
    @SpringBean(name = "smartAuthAddressClient")
    public IAddressEndpoint wsAddressClient;
    @SpringBean(name = "smartAuthWalletClient")
    public IWalletEndpoint wsWalletClient;
    @SpringBean(name = "systemAuthWalletClient")
    public IWalletEndpoint wsSystemAuthWalletClient;
    @SpringBean(name = "smartAuthSecurityClient")
    public ISecurityEndpoint wsSecurityClient;
    @SpringBean(name = "smartAuthOtpClient")
    public IOtpEndpoint wsOtpClient;
    @SpringBean(name = "smartAuthMessagingClient")
    public IMessagingService wsMsgClient;
    @SpringBean(name = "smartAuthTemplateClient")
    public ITemplateService wsTemplateClient;
    @SpringBean(name = "smartAuthPreAuthorisationClient")
    public IPreAuthorisationEndpoint wsPreAuthClient;
    @SpringBean(name = "smartAuthAuthorisationClient")
    public IAuthorisationEndpoint wsAuthClient;
    @SpringBean(name = "smartAuthCustomerNetworksClient")
    public ICustomerNetworksEndpoint wsCustNetworkClient;
    @SpringBean(name = "smartAuthRolesPrivilegesClient")
    public IUmgrRolesPrivilegesEndpoint wsRolePrivilegeClient;
    @SpringBean(name = "smartAuthPreAuthContinueClient")
    public IPreAuthContinueEndpoint wsPreAuthContinueClient;
    @SpringBean(name = "smartAuthAttachmentClient")
    public IAttachmentEndpoint wsAttachmentClient;
    @SpringBean(name = "smartAuthIdentityClient")
    public IIdentityEndpoint wsIdentityClient;
    @SpringBean(name = "smartAuthCommissionConfigurationClient")
    public ICommissionConfigurationEndpoint wsCommissionConfigClient;
    @SpringBean(name = "smartAuthBalanceAlertClient")
    public IBalanceAlertEndpoint wsBalanceAlertClient;
    @SpringBean(name = "smartAuthLimitClient")
    public ILimitEndpoint wsLimitClient;
    @SpringBean(name = "smartAuthCommissionClearingClient")
    public ICommissionClearingEndpoint wsCommissionClearingClient;
    @SpringBean(name = "smartAuthTransactionClient")
    public ITransactionEndpoint wsTransactionsClient;
    @SpringBean(name = "smartAuthCaptureCancelClient")
    public ICaptureCancelEndpoint wsCaptureCancelClient;
    @SpringBean(name = "smartAuthAuthorisationCancelClient")
    public IAuthorisationCancelEndpoint wsAuthorisationCancelClient;
    @SpringBean(name = "smartAuthCaptureClient")
    public ICaptureEndpoint wsCaptureClient;
    @SpringBean(name = "smartAuthReportClient")
    public IReportEndpoint wsReportClient;
    @SpringBean(name = "smartAuthFindRemittanceVoucherClient")
    public IFindRemittanceVoucherEndpoint wsFindRemittanceVoucherClient;
    @SpringBean(name = "smartAuthPrePickupMoneyClient")
    public IPrePickupMoneyEndpoint wsPrePickupMoneyClient;
    @SpringBean(name = "smartAuthSystemClient")
    public ISystemEndpoint wsSystemConfClient;
    @SpringBean(name = "smartAuthNoteClient")
    public INoteEndpoint wsNoteClient;
    @SpringBean(name = "smartAuthJobClient")
    public IJobEndpoint wsJobClient;
    @SpringBean(name = "smartAuthFeeClient")
    public IFeeEndpoint wsFeeConfClient;
    @SpringBean(name = "smartAuthInvoiceClient")
    public IInvoiceEndpoint wsInvoiceClient;
    @SpringBean(name = "smartAuthWebStartClient")
    public IWebStartEndpoint wsWebStartClient;
    @SpringBean(name = "smartAuthWebContinueClient")
    public IWebContinueEndpoint wsWebContinueClient;
    @SpringBean(name = "smartAuthPayInvoiceClient")
    public IPayInvoiceEndpoint wsPayInvoiceClient;
    @SpringBean(name = "smartAuthCheckPayInvoiceClient")
    public ICheckPayInvoiceEndpoint wsCheckPayInvoiceClient;
    @SpringBean(name = "smartAuthContinuePayInvoiceClient")
    public IContinuePayInvoiceEndpoint wsContinuePayInvoiceClient;
    @SpringBean(name = "smartAuthCancelInvoiceClient")
    public ICancelInvoiceEndpoint wsCancelInvoiceClient;
    @SpringBean(name = "smartAuthDemandForPaymentClient")
    public IDemandForPaymentEndpoint wsDemandForPaymentClient;
    @SpringBean(name = "smartAuthPreferencesClient")
    public IPreferencesEndpoint wsPrefsClient;
    @SpringBean(name = "smartAuthTransactionRestrictionClient")
    public ITransactionRestrictionEndpoint wsTxnRestrictionClient;
    @SpringBean(name = "smartAuthStartVoucherClient")
    public IStartVoucherEndpoint wsStartVoucherClient;
    @SpringBean(name = "smartAuthConfirmVoucherClient")
    public IConfirmVoucherEndpoint wsConfirmVoucherClient;
    @SpringBean(name = "smartAuthTransactionCancelClient")
    public ITransactionCancelEndpoint wsTxnCancelClient;
    @SpringBean(name = "smartAuthCouponClient")
    public ICouponsEndpoint wsCouponsClient;
    @SpringBean(name = "smartAuthMessageLogClient")
    public IMessageLogService wsMessageLogClient;
    @SpringBean(name = "smartAuthBulkProcessingClient")
    public IBulkProcessingEndpoint wsBulkProcessingClient;

    // Beans for remote management - different from other service beans above
    // as they use a dynamic/refreshable client configuration
    @SpringBean(name = "dynamicManagementClientConfiguration")
    public DynamicServiceConfiguration wsDynamicManagementClientConfiguration;
    @SpringBean(name = "dynamicManagementClientSource")
    public Refreshable wsDynamicManagementClientSource;
    @SpringBean(name = "dynamicManagementClient")
    public IManagementEndpoint wsDynamicManagementClient;
    @SpringBean(name = "dynamicPingClientConfiguration")
    public DynamicServiceConfiguration wsDynamicPingClientConfiguration;
    @SpringBean(name = "dynamicPingClientSource")
    public Refreshable wsDynamicPingClientSource;
    @SpringBean(name = "dynamicPingClient")
    public IPingEndpoint wsDynamicPingClient;

    @SpringBean(name = "encryptionManager")
    public IPreferencesEncryptionManager preferencesEncryptionManager;

    @SpringBean(name = "prefsService")
    public IPreferencesService preferencesService;

    // system auth web service clients
    @SpringBean(name = "systemAuthCustomerContextClient")
    private ISecurityEndpoint wsSystemAuthSecurityClient;
    
    
    @SpringBean(name="securitysClient")
	private SecurityFacade securitysClient;

    private String svaBalance;
    public String loginDetails;
    private XMLGregorianCalendar timestamp;

    // TODO get bundle based on locale
    private static ResourceBundle ERROR_BUNDLE;

    public MobiliserBasePage() {
	super();
    }

    public MobiliserBasePage(final PageParameters parameters) {
	super(parameters);
    }

    public MobiliserWebSession getMobiliserWebSession() {
	return (MobiliserWebSession) super.getSession();
    }

    public void cleanupSession() {

	LOG.debug("# MobiliserBasePage.cleanupSession()");

	Locale locale = (Locale) getWebSession().getLocale();
	getWebSession().invalidate();
	getWebSession().setLocale(locale);
    }

    public Configuration getConfiguration() {
	return configuration;
    }

    public IClientConfiguration getClientConfiguration() {
	return clientConfiguration;
    }

    public List<KeyValue<Boolean, String>> getYesNoOptions() {
	LOG.debug("# MobiliserBasePage.getYesNoOptions()");
	// setup the YES_NO_OPTIONS
	if (YES_NO_OPTIONS == null) {
	    YES_NO_OPTIONS = new ArrayList<KeyValue<Boolean, String>>();
	    YES_NO_OPTIONS.add(new KeyValue<Boolean, String>(new Boolean(true),
		    getLocalizer().getString("feeType.useCase.yes", this)));
	    YES_NO_OPTIONS.add(new KeyValue<Boolean, String>(
		    new Boolean(false), getLocalizer().getString(
			    "feeType.useCase.no", this)));
	}

	return YES_NO_OPTIONS;
    }

    public List<FeeSet> getFeeSetsList(Boolean fetchIndividual)
	    throws DataProviderLoadException {
	LOG.debug("# MobiliserBasePage.getFeeSetsList()");
	List<FeeSet> feeSetList = null;
	GetFeeSetsRequest request;
	try {
	    request = getNewMobiliserRequest(GetFeeSetsRequest.class);
	    request.setFetchIndividual(fetchIndividual);
	    GetFeeSetsResponse response = wsFeeConfClient.getFeeSets(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while fetching fee sets");
		throw new DataProviderLoadException();

	    }
	    feeSetList = response.getFeeSets();
	} catch (Exception e) {
	    throw new DataProviderLoadException();
	}
	return feeSetList;
    }

    public List<KeyValue<Long, String>> getFeeSets(Long feeSetId) {
	LOG.debug("# MobiliserBasePage.getFeeSets()");
	List<KeyValue<Long, String>> feeSetKeyValueList = new ArrayList<KeyValue<Long, String>>();

	try {

	    if (!PortalUtils.exists(feeSetList))
		feeSetList = getFeeSetsList(Boolean.FALSE);
	} catch (DataProviderLoadException e) {
	    LOG.error("#An error occurred while fetching fee sets");
	    return feeSetKeyValueList;

	}

	boolean isGlobalFeeSet = false;
	KeyValue<Long, String> keyValue;
	for (FeeSet feeSet : feeSetList) {
	    keyValue = new KeyValue<Long, String>(feeSet.getId(), feeSet
		    .getName());
	    feeSetKeyValueList.add(keyValue);
	    if (PortalUtils.exists(feeSetId)
		    && (feeSet.getId().longValue() == feeSetId.longValue()))
		isGlobalFeeSet = true;
	}

	if (PortalUtils.exists(feeSetId) && feeSetId.longValue() != 0
		&& !isGlobalFeeSet) {
	    keyValue = new KeyValue<Long, String>(feeSetId, getLocalizer()
		    .getString("cst.customercare.feeset.individual", this));
	    feeSetKeyValueList.add(keyValue);
	}
	keyValue = new KeyValue<Long, String>(new Long(0), getLocalizer()
		.getString("cst.customercare.feeset.none", this));
	feeSetKeyValueList.add(keyValue);
	return feeSetKeyValueList;
    }

    public List<KeyValue<Integer, String>> getFeeTypesList() {
	LOG.debug("# MobiliserBasePage.getFeeTypesList()");
	List<FeeType> feeTypes = getFeeTypes();
	FEE_TYPES = new ArrayList<KeyValue<Integer, String>>(feeTypes.size());
	for (FeeType feeType : feeTypes) {
	    FEE_TYPES.add(new KeyValue<Integer, String>(feeType.getId(),
		    feeType.getName()));
	}

	return FEE_TYPES;
    }

    public String getFeeType(int feeTypeId) {
	LOG.debug("# MobiliserBasePage.getFeeType()");
	if (!PortalUtils.exists(FEE_TYPES))
	    getFeeTypesList();
	for (KeyValue<Integer, String> feeType : FEE_TYPES) {
	    if (feeType.getKey().intValue() == feeTypeId)
		return feeType.getValue();
	}

	return "";
    }

    protected List<KeyValue<String, String>> getBankList(boolean forceLoad) {
	LOG.debug("# MobiliserBasePage.getBankList()");
	if (forceLoad || BANK_LIST == null) {
	    BANK_LIST = new ArrayList<KeyValue<String, String>>();
	    String gcashBankArray[] = Constants.BANK_LIST.split(";");

	    for (String bank : gcashBankArray) {
		String bankValues[] = bank.split("=");
		String id = bankValues[0].trim();
		String name = bankValues[1].trim();
		BANK_LIST.add(new KeyValue<String, String>(id, name));
	    }
	}

	return BANK_LIST;

    }

    protected String getBank(String bankCode) {
	LOG.debug("# MobiliserBasePage.getBank()");
	String bankName = "";
	if (BANK_LIST == null) {
	    getBankList(false);
	}
	if (PortalUtils.exists(BANK_LIST) && PortalUtils.exists(bankCode)) {
	    for (KeyValue<String, String> bankObj : BANK_LIST) {
		if (bankCode.equals(bankObj.getKey())) {
		    bankName = bankObj.getValue();
		    break;
		}
	    }
	}

	return bankName;
    }

    @Override
    public void prepareMobiliserRequest(MobiliserRequestType req) {
	req.setOrigin(Constants.MOBILISER_REQUEST_ORIGIN);
    }

    public List<KeyValue<String, String>> getSelectableMonth() {

	List<KeyValue<String, String>> list = new ArrayList<KeyValue<String, String>>();

	// current date
	GregorianCalendar systemRefDate = new GregorianCalendar();

	GregorianCalendar greg = new GregorianCalendar(TimeZone.getDefault());

	SelectBean sb = new SelectBean(String
		.valueOf(greg.get(Calendar.MONTH) + 1), String.valueOf(greg
		.get(Calendar.YEAR)));

	list.add(new KeyValue<String, String>(sb.getId() + "-" + sb.getName(),
		getLocalizer().getString("calendar.months." + sb.getId(), this)
			+ "-" + sb.getName()));

	// current month
	while (!(systemRefDate.get(Calendar.MONTH) + 1 == 1)
		|| !(systemRefDate.get(Calendar.YEAR) == 2009)) {

	    systemRefDate.add(Calendar.MONTH, -1);

	    greg.add(Calendar.MONTH, -1);

	    sb = new SelectBean(String.valueOf(greg.get(Calendar.MONTH) + 1),
		    String.valueOf(greg.get(Calendar.YEAR)));

	    list.add(new KeyValue<String, String>(sb.getId() + "-"
		    + sb.getName(), getLocalizer().getString(
		    "calendar.months." + sb.getId(), this)
		    + "-" + sb.getName()));
	}

	return list;
    }

    public boolean handleTransaction(TransactionBean tab) throws Exception {
	LOG.debug("# MobiliserBasePage.handleTransaction()");

	if (!tab.isPreAuthFinished())
	    return handlePreAuthorisation(tab);
	else
	    return handlePreAuthContinue(tab);
    }

    private boolean handlePreAuthorisation(TransactionBean tab)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.handlePreAuthorisation(..)");
	PreAuthorisation request = getNewMobiliserRequest(PreAuthorisation.class);
	request.setAmount(tab.getAmount());
	request.setAutoCapture(tab.isAutoCapture());
	request.setUsecase(tab.getUsecase());
	request.setText(tab.getText());
	request.setPayer(tab.getPayer());
	request.setPayee(tab.getPayee());
	request.setOrderChannel(tab.getOrderChannel());
	request.setOrderID(tab.getOrderId());
	PreAuthorisationResponse preAuthResp = wsPreAuthClient
		.preAuthorisation(request);

	if (!evaluateMobiliserResponse(preAuthResp)) {
	    LOG.warn("# Error during preauthorization");
	    return false;
	}

	LOG.info("# PreAuthorizaion transaction[{}] successfully finished", tab
		.getModule());

	// Calculate fees and amounts
	long payeeFee = 0;
	long payerFee = 0;
	for (MoneyFeeType mft : preAuthResp.getMoneyFee()) {
	    if (mft.isPayee()) {
		payeeFee += mft.getValue();
		payeeFee += mft.getVat();
	    } else {
		// isPayer
		payerFee += mft.getValue();
		payerFee += mft.getVat();
	    }
	}

	tab.setFeeAmount(payerFee + payeeFee);
	tab.setDebitAmount(tab.getAmount().getValue() + payerFee);
	tab.setCreditAmount(tab.getAmount().getValue() - payeeFee);
	if (PortalUtils.exists(preAuthResp.getAuthenticationMethods())) {
	    if (PortalUtils.exists(preAuthResp.getAuthenticationMethods()
		    .getAuthMethodPayee()))
		tab.setAuthenticationMethodPayee(Integer.valueOf(preAuthResp
			.getAuthenticationMethods().getAuthMethodPayee()
			.getId()));
	    if (PortalUtils.exists(preAuthResp.getAuthenticationMethods()
		    .getAuthMethodPayer()))
		tab.setAuthenticationMethodPayer(Integer.valueOf(preAuthResp
			.getAuthenticationMethods().getAuthMethodPayer()
			.getId()));
	}
	tab.setPreAuthFinished(true);
	tab.setRefTransaction(preAuthResp.getTransaction());
	return true;
    }

    private boolean handlePreAuthContinue(TransactionBean tab) throws Exception {
	LOG.debug("# MobiliserBasePage.handlePreAuthContinue(...)");

	try {
	    AuthorisationCallThread thread = getMobiliserWebSession()
		    .getAuthorisationCallThread();

	    if (thread == null) {
		PreAuthorisationContinue request = getNewMobiliserRequest(PreAuthorisationContinue.class);
		request.setReferenceTransaction(tab.getRefTransaction());
		thread = new AuthorisationCallThread(request,
			wsPreAuthContinueClient);
		getMobiliserWebSession().setAuthorisationCallThread(thread);
		thread.start();

	    }
	    if ((tab.getAuthenticationMethodPayee() == null || tab
		    .getAuthenticationMethodPayee() == 0)
		    && (tab.getAuthenticationMethodPayee() == null || tab
			    .getAuthenticationMethodPayee() == 0)) {
		try {
		    thread.join();
		} catch (InterruptedException e) {
		    LOG
			    .error(
				    "# Interrupted exception when rejoining authorisation call thread",
				    e);
		}
	    }
	    if (!thread.isActive()) {
		PreAuthorisationContinueResponse authResp = thread
			.getResponse();

		if (authResp.getStatus().getCode() == Constants.PENDING_APPROVAL_ERROR) {
		    LOG
			    .warn("#  Your transaction request has been processed successfully, but is pending approval");
		    tab.setStatusCode(Constants.TXN_STATUS_PENDING_APPROVAL);
		    getMobiliserWebSession().setAuthorisationCallThread(null);
		    return false;
		}

		if (!evaluateMobiliserResponse(authResp)) {
		    LOG.warn("# Error during PreAuthorisation continue");
		    getMobiliserWebSession().setAuthorisationCallThread(null);
		    return false;
		}
		LOG.info("PreAuthorisation continue success");
		tab.setTxnId(authResp.getTransaction().getSystemId());
		tab.setAuthCode("");
		getMobiliserWebSession().setAuthorisationCallThread(null);
		setTimestamp(authResp.getTimestamp());
	    }
	} catch (Exception e) {
	    getMobiliserWebSession().setAuthorisationCallThread(null);
	    throw e;
	}
	// TODO handle the condition when AuthenticationMethod for payee or
	// payer is not NO_AUTH
	return true;

    }

    protected boolean handleAuthorisation(TransactionBean tab) throws Exception {
	LOG.debug("# MobiliserBasePage.handleAuthorisation()");
	Authorisation request = getNewMobiliserRequest(Authorisation.class);
	request.setAmount(tab.getAmount());
	request.setAutoCapture(tab.isAutoCapture());
	request.setOrderID(tab.getOrderId());
	request.setUsecase(tab.getUsecase());
	request.setText(tab.getText());
	request.setPayer(tab.getPayer());
	request.setPayee(tab.getPayee());
	request.setOrderChannel(tab.getOrderChannel());
	AuthorisationResponse authResp = wsAuthClient.authorisation(request);

	if (authResp.getStatus().getCode() == Constants.PENDING_APPROVAL_ERROR) {
	    LOG
		    .warn("#  Your transaction request has been processed successfully, but is pending approval");
	    tab.setStatusCode(Constants.TXN_STATUS_PENDING_APPROVAL);
	    return false;
	}

	if (!evaluateMobiliserResponse(authResp)) {
	    LOG.warn("# Error during preauthorization");
	    return false;
	}

	LOG.info("# preauthorise transaction[{}] money successfully finished",
		tab.getModule());

	tab.setDebitAmount(tab.getAmount().getValue());
	tab.setCreditAmount(tab.getAmount().getValue());
	if (PortalUtils.exists(authResp.getAuthenticationMethods())) {
	    if (PortalUtils.exists(authResp.getAuthenticationMethods()
		    .getAuthMethodPayee()))
		tab.setAuthenticationMethodPayee(Integer.valueOf(authResp
			.getAuthenticationMethods().getAuthMethodPayee()
			.getId()));
	    if (PortalUtils.exists(authResp.getAuthenticationMethods()
		    .getAuthMethodPayer()))
		tab.setAuthenticationMethodPayer(Integer.valueOf(authResp
			.getAuthenticationMethods().getAuthMethodPayer()
			.getId()));
	}
	tab.setRefTransaction(authResp.getTransaction());
	tab.setTxnId(authResp.getTransaction().getSystemId());
	tab.setAuthCode("");
	return true;
    }

    public WalletEntry getSvaPI(long customerId,
	    final IWalletEndpoint walletEndpoint) throws Exception {
	LOG.debug("# MobiliserBasePage.getSvaPiId()");
	GetWalletEntriesRequest walletRequest = getNewMobiliserRequest(GetWalletEntriesRequest.class);
	walletRequest.setCustomerId(customerId);
	walletRequest.setPaymentInstrumentTypeFilter(new Integer(
		Constants.PIS_TYPE_FILTER_SVA));

	GetWalletEntriesResponse walletResponse = walletEndpoint
		.getWalletEntriesByCustomer(walletRequest);

	if (!evaluateMobiliserResponse(walletResponse)) {
	    LOG.warn("# Error while fetching PI of customer's SVA");
	    return null;
	}

	List<WalletEntry> walletEntries = walletResponse.getWalletEntries();
	LOG.debug("# Successfully fetched PI  for customer's SVA");
	if (PortalUtils.exists(walletEntries))
	    return walletEntries.get(0);
	else
	    return null;

    }

    public WalletEntry getSvaPI(long customerId) throws Exception {
	return this.getSvaPI(customerId, this.wsWalletClient);
    }

    public String getBalance(final IWalletEndpoint walletClient) {
	LOG.debug("# MobiliserBasePage.getBalance()");
	this.svaBalance = "";
	if (getMobiliserWebSession().isSignedIn()) {
	    try {
		WalletEntry svaPI = getSvaPI(getMobiliserWebSession()
			.getLoggedInCustomer().getCustomerId(), walletClient);
		if (PortalUtils.exists(svaPI)) {
		    BalanceInquiryRequest inquiryRequest = getNewMobiliserRequest(BalanceInquiryRequest.class);
		    inquiryRequest.setPaymentInstrumentId(svaPI
			    .getPaymentInstrumentId());
		    BalanceInquiryResponse inquiryResponse = walletClient
			    .getPaymentInstrumentBalance(inquiryRequest);

		    if (inquiryResponse.getStatus().getCode() == 0) {
			this.svaBalance = getLocalizer().getString(
				"sva.balance.heading", this)
				+ convertAmountToStringWithCurrency(
					inquiryResponse.getBalance()
						.longValue(), inquiryResponse
						.getCurrency());
		    } else {
			LOG.warn("# Error while fetching SVA balance");
		    }
		}

	    } catch (Exception e) {
		LOG.error("# Error while getting Sva Balance", e);
	    }
	}
	return this.svaBalance;
    }

    public String getBalance() {
	return this.getBalance(wsWalletClient);
    }

    /**
     * Checks if an external account exists for this customer
     * 
     * @param customerId
     * @param piClassFilter
     * @param piTypeFilter
     * @param accountJax
     * @return Account if found
     * @throws Exception
     */
    protected ExternalAccount externalAccountExist(long customerId,
	    Integer piTypeFilter, ExternalAccount accountJax) throws Exception {
	LOG.debug("# MobiliserBasePage.externalAccountExist()");
	// Check if PI already exist
	GetWalletEntriesRequest walletRequest = getNewMobiliserRequest(GetWalletEntriesRequest.class);
	walletRequest.setCustomerId(customerId);
	walletRequest.setPaymentInstrumentTypeFilter(piTypeFilter);

	GetWalletEntriesResponse walletResponse = wsWalletClient
		.getWalletEntriesByCustomer(walletRequest);

	if (!evaluateMobiliserResponse(walletResponse)) {
	    LOG
		    .warn("# Error while checking whether external account already exists");
	    return null;
	}

	List<WalletEntry> walletEntries = walletResponse.getWalletEntries();
	for (WalletEntry externalWalletEntry : walletEntries) {
	    ExternalAccount account = externalWalletEntry.getExternalAccount();
	    if (account != null) {
		if (eq(account.getId1(), accountJax.getId1())) {
		    return account;
		}
	    }
	}
	return null;
    }

    protected String getMSISDN(long custId) {
	LOG.debug("# MobiliserBasePage.getMSISDN()");
	List<Identification> identifcations;
	int idType;
	String msisdn = null;
	GetIdentificationsRequest req;
	try {
	    req = getNewMobiliserRequest(GetIdentificationsRequest.class);
	    req.setCustomerId(custId);
	    GetIdentificationsResponse response = wsIdentClient
		    .getIdentifications(req);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error while retrieving customer's MSISDN {}",
			custId);
		setResponsePage(this);
	    }
	    identifcations = response.getIdentifications();
	    for (Identification idnt : identifcations) {
		idType = idnt.getType();
		if (idType == Constants.IDENT_TYPE_MSISDN) {
		    msisdn = idnt.getIdentification();
		    break;
		}
	    }
	} catch (Exception e) {
	    LOG.error("# Error while fetching the customer's MSISDN", e);
	    error(getLocalizer().getString(
		    "retrieve.customer.ident.technical.error", this));

	    setResponsePage(this);
	}

	return msisdn;

    }

    public Customer getCustomerByIdentification(int identityType,
	    String identification) {
	LOG.debug("# MobiliserBasePage.getCustomerByIdentification()");
	GetCustomerByIdentificationRequest request = null;
	GetCustomerByIdentificationResponse response = null;
	try {
	    request = getNewMobiliserRequest(GetCustomerByIdentificationRequest.class);
	    request.setIdentificationType(identityType);
	    request.setIdentification(identification);
	    response = wsCustomerClient.getCustomerByIdentification(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	} catch (Exception e) {
	    LOG
		    .error("#An error occurred while getting customer by identification["
			    + identification + "]");
	    error(getLocalizer().getString(
		    "get.customerBy.identification.error", this));
	}

	return response.getCustomer();

    }

    public Address getAddressByCustomer(long customerId) {
	LOG.debug("# MobiliserBasePage.getAddressByCustomer()");
	GetAddressByCustomerRequest addrByCustReq;
	GetAddressByCustomerResponse response = null;
	try {
	    addrByCustReq = getNewMobiliserRequest(GetAddressByCustomerRequest.class);
	    addrByCustReq.setCustomerId(customerId);
	    response = wsAddressClient.getAddressByCustomer(addrByCustReq);
	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }
	} catch (Exception e) {
	    LOG
		    .error("#An error occurred while getting customer's address by customerid for customer["
			    + customerId + "]");
	    error(getLocalizer().getString("get.customer.address.error", this));
	}

	return response.getAddress();

    }

    public Identification getIdentificationByCustomer(long customerId,
	    int identificationType) {
	LOG.debug("# MobiliserBasePage.getIdentificationByCustomer()");
	Identification identification = null;
	try {
	    GetIdentificationsRequest identReq = getNewMobiliserRequest(GetIdentificationsRequest.class);
	    identReq.setCustomerId(customerId);
	    Customer wsCustomer = getCustomerByCustomerId(customerId);
	    if (wsCustomer.getCustomerTypeId() == Constants.MBANKING_CUSTOMER_TYPE
		    && identificationType == Constants.IDENT_TYPE_MSISDN) {
		identReq.setOrgUnit(wsCustomer.getOrgUnitId());
	    }
	    GetIdentificationsResponse identResp = wsIdentClient
		    .getIdentifications(identReq);
	    List<Identification> identifications = identResp
		    .getIdentifications();
	    for (Identification ident : identifications) {
		if (ident.getType() == identificationType) {
		    identification = ident;
		    break;
		}

	    }
	    if (!evaluateMobiliserResponse(identResp)) {
		return null;
	    }
	} catch (Exception e) {
	    LOG.error(
		    "# No Identification found for the customer" + customerId,
		    e);
	}
	return identification;
    }

    public List<Identification> getIdentificationsByCustomer(long customerId) {
	LOG.debug("# MobiliserBasePage.getIdentificationByCustomer()");
	List<Identification> identifications = null;
	try {
	    GetIdentificationsRequest identReq = getNewMobiliserRequest(GetIdentificationsRequest.class);
	    identReq.setCustomerId(customerId);
	    identReq.setIdentificationTypeId(Constants.IDENT_TYPE_MSISDN);
	    GetIdentificationsResponse identResp = wsIdentClient
		    .getIdentifications(identReq);

	    identifications = identResp.getIdentifications();

	    if (!evaluateMobiliserResponse(identResp)) {
		return null;
	    }
	} catch (Exception e) {
	    LOG.error(
		    "# No Identification found for the customer" + customerId,
		    e);
	}
	return identifications;
    }

    /**
     * Compares two variables and checks for NULL
     * 
     * @param val1
     * @param val2
     * @return if equal
     */
    private boolean eq(String val1, String val2) {
	if (val1 == null && val2 == null)
	    return true;
	return (val1 != null && val1.equals(val2));

    }

    public void setLoginDetails(String loginDetails) {
	this.loginDetails = loginDetails;
    }

    public XMLGregorianCalendar getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(XMLGregorianCalendar timestamp) {
	this.timestamp = timestamp;
    }

    public <Resp extends MobiliserResponseType> boolean evaluateMobiliserResponse(
	    Resp response) {

	LOG.debug("# Response returned status: {}-{}", response.getStatus()
		.getCode(), response.getStatus().getValue());

	if (response.getStatus().getCode() == 0) {
	    return true;
	}

	// check for mobiliser session closed or expired
	if (response.getStatus().getCode() == 352
		|| response.getStatus().getCode() == 353) {

	    LOG
		    .debug("# Mobiliser session closed/expired, redirect to sign in page");

	    // if mobiliser session gone, then can't continue with web session
	    // so invalidate session and redirect to home, which will go to
	    getMobiliserWebSession().invalidate();
	    getRequestCycle().setRedirect(true);
	    setResponsePage(getComponent(ApplicationLoginPage.class));
	}

	String errorMessage = null;

	errorMessage = getDisplayValue(String.valueOf(response.getStatus()
		.getCode()), Constants.RESOURCE_BUNDLE_ERROR_CODES);

	if (PortalUtils.exists(errorMessage)) {
	    getMobiliserWebSession().error(errorMessage);
	} else {
	    getMobiliserWebSession().error(
		    getLocalizer().getString("portal.genericError", this));
	}

	return false;
    }

    /**
     * Method used to create Customer's basic data
     * 
     * @param customer
     * @return
     * @throws Exception
     */
    public CustomerBean createCustomer(CustomerBean customer) throws Exception {
	LOG.debug("# MobiliserBasePage.createCustomer()");
	customer.setDisplayName(createDisplayName(customer.getAddress()
		.getFirstName(), customer.getAddress().getLastName()));
	CreateCustomerRequest crCustReq = getNewMobiliserRequest(CreateCustomerRequest.class);

	crCustReq.setCustomer(Converter.getInstance(getConfiguration())
		.getCustomerFromCustomerBean(customer));
	CreateCustomerResponse crCustResp = wsCustomerClient
		.createCustomer(crCustReq);

	if (!evaluateMobiliserResponse(crCustResp)) {
	    createStatus = false;
	    return null;
	}
	createStatus = true;
	customer.setId(crCustResp.getCustomerId());
	customer.setBlackListReason(0);
	LOG.debug("# Successfully created Customer data");
	return customer;
    }

    /**
     * Method used to create Customer's basic data
     * 
     * @param customer
     * @return
     * @throws Exception
     */

    public CustomerBean createFullCustomer(CustomerBean customer,
	    List<Attachment> attachmentsList) throws Exception {
	LOG.debug("# MobiliserBasePage.createFullCustomer()");
	try {
	    CreateFullCustomerRequest crCustReq = getNewMobiliserRequest(CreateFullCustomerRequest.class);
	    customer.setDisplayName(createDisplayName(customer.getAddress()
		    .getFirstName(), customer.getAddress().getLastName()));

	    crCustReq.setCustomer(Converter.getInstance(getConfiguration())
		    .getCustomerFromCustomerBean(customer));

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
		identification = Converter.getInstance(getConfiguration())
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
		identification = Converter.getInstance(getConfiguration())
			.getMsisdnIdFromCustomerBean(customer);
		custIdentification = new CustomerAttachedIdentification();
		custIdentification.setIdentification(identification
			.getIdentification());
		custIdentification.setProvider(identification.getProvider());
		custIdentification.setStatus(identification.getStatus());
		custIdentification.setType(identification.getType());
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

	    CreateFullCustomerResponse response = wsCustomerClient
		    .createFullCustomer(crCustReq);

	    if (response.getStatus().getCode() == Constants.PENDING_APPROVAL_ERROR) {
		customer.setPendingApproval(true);
		createStatus = false;
		return customer;
	    }

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while creating pending customer");
		createStatus = false;
		return null;
	    }
	    Address createdaddress = getCustomerAddress(response
		    .getCustomerId());
	    createStatus = true;
	    customer.getAddress().setId(createdaddress.getId());
	    customer.setId(response.getCustomerId());
	    customer.setBlackListReason(0);
	    customer.setFeeSetId(new Long(0));
	    customer.setOriginalFeeSetId(new Long(0));
	    return customer;

	} catch (Exception e) {
	    throw e;
	}

    }

    /**
     * Method used to create Customer's address
     * 
     * @throws Exception
     */
    public CustomerBean createCustomerAddress(CustomerBean customer)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.createCustomerAddress()");
	CreateAddressRequest crAddrReq = getNewMobiliserRequest(CreateAddressRequest.class);
	Address address = Converter.getInstance().getAddressFromAddressBean(
		customer.getAddress());
	address.setCustomerId(customer.getId());
	crAddrReq.setAddress(address);
	CreateAddressResponse crAddrResp = wsAddressClient
		.createAddress(crAddrReq);

	if (!evaluateMobiliserResponse(crAddrResp)) {
	    createStatus = false;
	    return null;
	}
	createStatus = true;
	customer.getAddress().setId(crAddrResp.getAddressId());
	LOG.debug("# Successfully created Address data");
	return customer;

    }

    public Address getCustomerAddress(long customerId) {
	LOG.debug("# MobiliserBasePage.getCustomerAddress()");
	Address address = null;
	try {
	    GetAddressByCustomerRequest addressReq = getNewMobiliserRequest(GetAddressByCustomerRequest.class);
	    addressReq.setCustomerId(customerId);
	    addressReq.setAddressType(Constants.ADDRESS_TYPE_POSTAL_DELIVERY);

	    GetAddressByCustomerResponse addressResp = wsAddressClient
		    .getAddressByCustomer(addressReq);
	    if (evaluateMobiliserResponse(addressResp)) {
		address = addressResp.getAddress();
	    }
	} catch (Exception e) {
	    error(getLocalizer().getString("agentData.getAddress.error", this));
	    LOG.error("# Error in getAddressByCustomerReq() ", e);
	}

	return address;
    }

    /**
     * Method used to create Customer's login data
     * 
     * @throws Exception
     */
    public void createCustomerCredential(CustomerBean customer,
	    int credentialType) throws Exception {
	LOG.debug("# MobiliserBasePage.createCustomerCredential()");
	// create his credential (password)
	SetCredentialRequest credentialRequest = getNewMobiliserRequest(SetCredentialRequest.class);
	credentialRequest.setCustomerId(customer.getId());
	if (credentialType == 1) {
	    credentialRequest.setCredential(customer.getPassword());
	    credentialRequest
		    .setCredentialType(Constants.CREDENTIAL_TYPE_PASSWORD);
	} else if (credentialType == 0) {
	    credentialRequest.setCredential(customer.getPin());
	    credentialRequest.setCredentialType(Constants.CREDENTIAL_TYPE_PIN);
	}
	credentialRequest.setCredentialStatus(Constants.CREDENTIAL_STATUS);

	SetCredentialResponse credentialResponse = wsSecurityClient
		.setCredential(credentialRequest);

	if (!evaluateMobiliserResponse(credentialResponse)) {
	    createStatus = false;
	    return;
	}
	createStatus = true;
	LOG.debug("# Successfully created password Credential");

    }

    public void createCustomerMsisdn(CustomerBean customer) throws Exception {
	LOG.debug("# MobiliserBasePage.createCustomerMsisdn()");
	// create his identifications (MSISDN )
	CreateIdentificationRequest crIdReqMsisdn = getNewMobiliserRequest(CreateIdentificationRequest.class);
	crIdReqMsisdn.setIdentification(Converter.getInstance(
		getConfiguration()).getMsisdnIdFromCustomerBean(customer));

	CreateIdentificationResponse crIdRespMsisdn = wsIdentClient
		.createIdentification(crIdReqMsisdn);

	if (!evaluateMobiliserResponse(crIdRespMsisdn)) {
	    createStatus = false;
	    return;
	}
	createStatus = true;
	LOG.debug("# Successfully created MSISDN data");
    }

    public CustomerBean createCustomerIdentification(CustomerBean customer)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.createCustomerIdentification()");
	// create his identifications (ID)
	CreateIdentificationRequest crIdReqCustId = getNewMobiliserRequest(CreateIdentificationRequest.class);
	crIdReqCustId.setIdentification(Converter.getInstance(
		getConfiguration()).getCustIdFromCustomerBean(customer));
	CreateIdentificationResponse crIdRespCustId = wsIdentClient
		.createIdentification(crIdReqCustId);

	if (!evaluateMobiliserResponse(crIdRespCustId)) {
	    createStatus = false;
	    return null;
	}
	createStatus = true;
	LOG.debug("# Successfully created MSISDN data");
	return customer;
    }

    public Identification getCustomerIdentification(long customerId,
	    int identificationType) throws Exception {
	LOG.debug("# MobiliserBasePage.getCustomerIdentification()");
	// create his identifications (ID)
	GetIdentificationsRequest crIdReqCustId = getNewMobiliserRequest(GetIdentificationsRequest.class);
	crIdReqCustId.setCustomerId(customerId);
	crIdReqCustId.setIdentificationTypeId(Integer
		.valueOf(identificationType));
	Customer wsCustomer = getCustomerByCustomerId(customerId);

	if (wsCustomer.getCustomerTypeId() == Constants.MBANKING_CUSTOMER_TYPE
		&& identificationType == Constants.IDENT_TYPE_MSISDN) {
	    crIdReqCustId.setOrgUnit(wsCustomer.getOrgUnitId());
	}
	GetIdentificationsResponse crIdRespCustId = wsIdentClient
		.getIdentifications(crIdReqCustId);

	if (evaluateMobiliserResponse(crIdRespCustId)
		&& PortalUtils.exists(crIdRespCustId.getIdentifications())) {
	    LOG.warn("# Successfully getCustomerIdentification");
	    return crIdRespCustId.getIdentifications().get(0);
	}
	return null;
    }

    public CustomerBean createCustomerIdentity(CustomerBean customer)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.createCustomerIdentity()");
	// Save identity
	CreateIdentityRequest cdIdentityReq = getNewMobiliserRequest(CreateIdentityRequest.class);
	cdIdentityReq
		.setIdentity(Converter.getInstance().getIdentity(customer));
	CreateIdentityResponse cdIdentityResp = wsIdentityClient
		.createIdenity(cdIdentityReq);

	if (!evaluateMobiliserResponse(cdIdentityResp)) {
	    createStatus = false;
	    return null;
	}
	customer.setIdentityId(String.valueOf(cdIdentityResp.getIdentityId()));
	createStatus = true;
	LOG.debug("# Successfully created Identity");
	return customer;
    }

    /**
     * @param customer
     * @throws Exception
     */
    public boolean updateCustomerDetail(CustomerBean customer) throws Exception {
	LOG.debug("# MobiliserBasePage.updateCustomerDetail()");
	customer.setDisplayName(createDisplayName(customer.getAddress()
		.getFirstName(), customer.getAddress().getLastName()));
	UpdateCustomerRequest updateCustReq = getNewMobiliserRequest(UpdateCustomerRequest.class);
	updateCustReq.setCustomer(Converter.getInstance(getConfiguration())
		.getCustomerFromCustomerBean(customer));
	UpdateCustomerResponse updateCustResp = wsCustomerClient
		.updateCustomer(updateCustReq);

	if (!evaluateMobiliserResponse(updateCustResp)) {
	    return false;
	}

	LOG.debug("# Successfully updated customer data");
	return true;
    }

    public boolean deleteCustomer(Long customerID) throws Exception {
	LOG.debug("# MobiliserBasePage.deleteCustomer()");
	DeleteCustomerRequest deleteCustReq = getNewMobiliserRequest(DeleteCustomerRequest.class);
	deleteCustReq.setCustomerId(customerID);
	DeleteCustomerResponse deleteCustResp = wsCustomerClient
		.deleteCustomer(deleteCustReq);

	if (!evaluateMobiliserResponse(deleteCustResp)) {
	    return false;
	}

	LOG.debug("# Successfully deleted customer data");
	return true;
    }

    /**
     * @param customer
     * @throws Exception
     */
    public boolean updateCustomerAddress(CustomerBean customer)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.updateCustomerAddress()");
	UpdateAddressRequest updateAddReq;
	updateAddReq = getNewMobiliserRequest(UpdateAddressRequest.class);
	Address address = Converter.getInstance().getAddressFromAddressBean(
		customer.getAddress());
	address.setCustomerId(customer.getId());
	updateAddReq.setAddress(address);
	UpdateAddressResponse updateAddResp = wsAddressClient
		.updateAddress(updateAddReq);

	if (!evaluateMobiliserResponse(updateAddResp)) {
	    LOG
		    .warn(
			    "# An error occurred whilte saving Personal data, with agent Id {}",
			    customer.getId());
	    return false;
	}

	LOG.debug("# Successfully updated Address data");
	return true;
    }

    protected boolean setCustomerCredential(Long customerID, String credential)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.setCustomerCredential()");
	SetCredentialRequest setCredentialReq = getNewMobiliserRequest(SetCredentialRequest.class);
	setCredentialReq.setCustomerId(customerID);
	setCredentialReq.setCredential(credential);
	setCredentialReq.setCredentialType(Constants.CREDENTIAL_TYPE_PASSWORD);
	SetCredentialResponse setCredentialResp = wsSecurityClient
		.setCredential(setCredentialReq);

	if (!evaluateMobiliserResponse(setCredentialResp)) {
	    LOG.warn("# An error occurred while updating password credential");

	    return false;
	}

	LOG.debug("# Successfully updated new password Credential");
	return true;
    }

    protected boolean valedatePasswordStrength(String password,
	    int credentialType) {
	try {
	    LOG.debug("# MobiliserBasePage.valedatePasswordStrength()");
	    CheckCredentialStrengthRequest credentialStrengthRequest = getNewMobiliserRequest(CheckCredentialStrengthRequest.class);
	    credentialStrengthRequest.setCredential(password);
	    credentialStrengthRequest.setCredentialType(credentialType);
	    credentialStrengthRequest
		    .setCustomerTypeId(Constants.CONSUMER_IDTYPE);
	    CheckCredentialStrengthResponse strengthResponse = wsSecurityClient
		    .checkCredentialStrength(credentialStrengthRequest);

	    if (!evaluateMobiliserResponse(strengthResponse)) {
		return false;
	    }

	} catch (Exception e) {
	    LOG
		    .error(
			    "# Error while checking credential strength during password vaildation.",
			    e);

	    error(getLocalizer().getString("ERROR.PLATFORM", this));
	    return false;
	}
	return true;
    }

    /**
     * @param customer
     * @return
     * @throws Exception
     */
    public void createCustomerUserName(CustomerBean customer) throws Exception {
	LOG.debug("# MobiliserBasePage.createCustomerNameIdentification()");
	CreateIdentificationRequest crIdReqMsisdn = getNewMobiliserRequest(CreateIdentificationRequest.class);
	crIdReqMsisdn.setIdentification(Converter.getInstance(
		getConfiguration()).getUsernameIdFromCustomerBean(customer));
	CreateIdentificationResponse crIdRespMsisdn = wsIdentClient
		.createIdentification(crIdReqMsisdn);
	if (!evaluateMobiliserResponse(crIdRespMsisdn)) {
	    createStatus = false;
	    return;
	}
	createStatus = true;
	LOG.debug("# Successfully created Username data");
    }

    protected List<Integer> getAllowedChildRoles(Long customerID) {
	LOG.debug("# MobiliserBasePage.getAllowedChildRoles()");
	List<String> availablePrevList = new ArrayList<String>();
	List<Integer> allowedRoleTypeIdList = new ArrayList<Integer>();
	try {
	    availablePrevList = getAllCustomerPrivileges(customerID);
	    for (String priv : availablePrevList) {

		// get Allowed Roles
		Matcher m = Pattern.compile(Constants.PRIV_REGEX_ALLOWED_ROLES)
			.matcher(priv);
		if (m.matches() && m.groupCount() == 1) {
		    int roleType = Integer.valueOf(m.group(1)).intValue();
		    allowedRoleTypeIdList.add(roleType);
		}
	    }

	} catch (Exception e) {
	    LOG.error("# Error while getting allowed child roles", e);
	}
	return allowedRoleTypeIdList;
    }

    public void createAgentWalletEntry(Long custId, long paymentInstrumentId,
	    String alias) throws Exception {
	LOG.debug("# MobiliserBasePage.createWalletEntry()");
	CreateWalletEntryRequest crWalletReq = getNewMobiliserRequest(CreateWalletEntryRequest.class);

	crWalletReq.setPrimaryCredit(true);
	crWalletReq.setPrimaryDebit(true);

	WalletEntry wallet = new WalletEntry();
	wallet.setCreditPriority(0);
	wallet.setDebitPriority(0);
	wallet.setLimitSetId(null);
	wallet.setPaymentInstrumentId(paymentInstrumentId);
	wallet.setAlias(alias);
	wallet.setCustomerId(custId);

	crWalletReq.setWalletEntry(wallet);
	CreateWalletEntryResponse crWalletResp = wsWalletClient
		.createWalletEntry(crWalletReq);

	if (!evaluateMobiliserResponse(crWalletResp)) {
	    LOG
		    .warn(
			    "# Error while creating wallet entry for customerId [{}] paymentInstId [{}] ",
			    new Object[] { custId,
				    new Long(paymentInstrumentId) });
	    createStatus = false;
	}
	createStatus = true;
	LOG.debug("# Successfully created WalletEntry");

    }

    /*
     * Pass null for riskCategoryId if privileges are required on based on
     * customer type
     */
    public List<String> getDefaultPrivileges(int typeId, Integer riskCategoryId)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.getDefaultPrivileges()");
	GetDefaultPrivilegesRequest privReq = getNewMobiliserRequest(GetDefaultPrivilegesRequest.class);
	privReq.setCustomerTypeId(typeId);
	privReq.setRiskCategoryId(riskCategoryId);
	GetDefaultPrivilegesResponse privResp = wsSecurityClient
		.getDefaultPrivileges(privReq);
	if (privResp.getStatus().getCode() == 0) {
	    return privResp.getPrivileges();
	}
	return null;
    }

    public boolean createSvaWalletWithPI(CustomerBean customer)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.createSvaPaymentInstrument()");
	createStatus = false;
	List<String> privileges = getDefaultPrivileges(customer
		.getCustomerTypeId(), customer.getRiskCategoryId());
	boolean hasSvaPrivilege = false;
	if (PortalUtils.exists(privileges)) {
	    if (privileges.contains(Constants.PRIV_AUTO_CREATE_SVA))
		hasSvaPrivilege = true;
	}

	if (hasSvaPrivilege) {
	    CreateWalletEntryRequest crWalletReq = getNewMobiliserRequest(CreateWalletEntryRequest.class);

	    crWalletReq.setPrimaryCredit(true);
	    crWalletReq.setPrimaryDebit(true);

	    WalletEntry wallet = new WalletEntry();
	    wallet.setCreditPriority(0);
	    wallet.setDebitPriority(0);
	    wallet.setLimitSetId(null);
	    wallet.setSva(Converter.getSva(customer, getConfiguration()
		    .getSvaCurrency()));
	    wallet.setAlias(getConfiguration().getSVAAlias());
	    wallet.setCustomerId(customer.getId());

	    crWalletReq.setWalletEntry(wallet);
	    CreateWalletEntryResponse crWalletResp = wsWalletClient
		    .createWalletEntry(crWalletReq);

	    if (!evaluateMobiliserResponse(crWalletResp)) {
		LOG
			.warn(
				"# Error while creating wallet entry for customerId [{}] ",
				new Object[] { customer.getId() });
		createStatus = false;
		return createStatus;
	    }
	    createStatus = true;

	} else {
	    createStatus = true;
	}
	return createStatus;
    }

    public CustomerBean createAgentLimitSetting(CustomerBean customerBean)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.createAgentLimitSetting()");
	CreateLimitClassRequest addLimitReq = getNewMobiliserRequest(CreateLimitClassRequest.class);
	addLimitReq.setEntityId(customerBean.getId());
	addLimitReq.setEntityType(Constants.LIMIT_CUSTOMER_TYPE);
	LimitClass limit = customerBean.getLimitClass();
	limit.setCurrency(getConfiguration().getCurrency());
	addLimitReq.setLimitClass(limit);
	// addLimitReq.setUseCaseId(value)
	CreateLimitClassResponse addLimitResp = wsLimitClient
		.createLimitClass(addLimitReq);

	if (!evaluateMobiliserResponse(addLimitResp)) {
	    createStatus = false;
	    return null;
	}
	createStatus = true;
	LOG.debug("# Successfully created Agent setting Limit data");
	List<LimitSetClass> limitSetList = getLimitSetClassList(customerBean
		.getId(), Constants.LIMIT_CUSTOMER_TYPE);
	if (PortalUtils.exists(limitSetList)) {
	    customerBean.setLimitId(limitSetList.get(0).getId());
	    customerBean.setLimitClass(limitSetList.get(0).getLimitClass());
	}

	super.info(getLocalizer().getString(
		"MESSAGE.EDIT_AGENTSETTING_UPDATED", this));
	return customerBean;
    }

    protected void updateAgentLimitSetting(CustomerBean customerBean)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.updateAgentLimitSetting()");
	UpdateLimitClassRequest updateLimitReq = null;
	updateLimitReq = getNewMobiliserRequest(UpdateLimitClassRequest.class);

	updateLimitReq.setLimitClass(customerBean.getLimitClass());
	UpdateLimitClassResponse updateLimitResp = wsLimitClient
		.updateLimitClass(updateLimitReq);

	if (!evaluateMobiliserResponse(updateLimitResp)) {
	    return;
	}

	LOG.debug("# Successfully updated Agent setting limit data");

	super.info(getLocalizer().getString(
		"MESSAGE.EDIT_AGENTSETTING_UPDATED", this));
	getMobiliserWebSession().setCustomer(customerBean);

    }

    public Attachment createAttachmentService(Attachment attachment, Long custId)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.createAttachmentService()");
	CreateAttachmentRequest crAttachmentReq = getNewMobiliserRequest(CreateAttachmentRequest.class);
	attachment.setCustomerId(custId);
	crAttachmentReq.setAttachment(attachment);
	CreateAttachmentResponse crAttachmentResp = wsAttachmentClient
		.createAttachment(crAttachmentReq);

	if (!evaluateMobiliserResponse(crAttachmentResp)) {
	    createStatus = false;
	    return null;
	}

	attachment.setId(crAttachmentResp.getAttachmentId());

	LOG.info("# Successfully created Attachment");
	createStatus = true;
	return attachment;
    }

    public List<KeyValue<String, String>> fetchLookupEntries(String lookupName,
	    String errorKey) {
	List<KeyValue<String, String>> keyValueList = new ArrayList<KeyValue<String, String>>();
	try {
	    GetLookupsRequest request = getNewMobiliserRequest(GetLookupsRequest.class);
	    request.setEntityName(lookupName);
	    GetLookupsResponse response = wsSystemConfClient
		    .getLookups(request);
	    if (!evaluateMobiliserResponse(response))
		return null;
	    keyValueList = convertLookupToIntKeyValueList(response
		    .getLookupEntities());
	} catch (Exception e) {
	    error(getLocalizer().getString(errorKey, this));
	    LOG.error("# Error occurred while fetching lookup entries["
		    + lookupName + "]");
	}

	return keyValueList;

    }

    public MoneyAmount getSVABalanceAmount(long paymentInstrumentId) {
	LOG.debug("# MobiliserBasePage.getSVABalanceList()");

	try {
	    BalanceInquiryRequest svaReq = getNewMobiliserRequest(BalanceInquiryRequest.class);
	    svaReq.setPaymentInstrumentId(paymentInstrumentId);
	    BalanceInquiryResponse svaResponse = wsWalletClient
		    .getPaymentInstrumentBalance(svaReq);

	    if (!evaluateMobiliserResponse(svaResponse)) {
		LOG
			.warn(
				"Error while fetching SVA balance for paymentInstrumentId [{}]",
				new Long(paymentInstrumentId));
		return null;
	    }

	    MoneyAmount svaBalAmt = new MoneyAmount();
	    svaBalAmt.setCurrency(svaResponse.getCurrency());
	    if (svaResponse.getBalance() != null)
		svaBalAmt.setValue(svaResponse.getBalance().longValue());
	    return svaBalAmt;

	} catch (Exception e) {
	    LOG
		    .error(
			    "# Error while fetching SVA balance for paymentInstrumentId [{}]",
			    new Long(paymentInstrumentId), e);
	}

	return null;

    }

    public List<WalletEntry> getWalletEntryList(Long customerId,
	    Integer classFilter, Integer typeFilter) {
	LOG.debug("# MobiliserBasePage.getWalletEntryList()");
	List<WalletEntry> walletEntryList = null;
	try {
	    GetWalletEntriesRequest walletReq = getNewMobiliserRequest(GetWalletEntriesRequest.class);
	    walletReq.setCustomerId(customerId);
	    if (classFilter != null) {
		walletReq.setPaymentInstrumentClassFilter(classFilter);
	    }
	    if (typeFilter != null) {
		walletReq.setPaymentInstrumentTypeFilter(typeFilter);
	    }
	    GetWalletEntriesResponse walletResp = wsWalletClient
		    .getWalletEntriesByCustomer(walletReq);

	    if (!evaluateMobiliserResponse(walletResp)) {
		LOG
			.warn(
				"Error while fetching wallet entries for customerId [{}] classFilter [{}] typeFilter [{}]",
				new Object[] { customerId, classFilter,
					typeFilter });

		return null;
	    }

	    walletEntryList = walletResp.getWalletEntries();
	} catch (Exception e) {
	    LOG
		    .error(
			    "# Error while fetching wallet entries for customerId [{}] classFilter [{}] typeFilter [{}]",
			    new Object[] { customerId, classFilter, typeFilter,
				    e });
	}
	return walletEntryList;
    }

    public WalletEntry getWalletEntry(Long walletId) {
	LOG.debug("# MobiliserBasePage.getWalletEntry()");
	try {
	    GetWalletEntryRequest walletReq = getNewMobiliserRequest(GetWalletEntryRequest.class);
	    walletReq.setWalletEntryId(walletId);
	    GetWalletEntryResponse walletResp = wsWalletClient
		    .getWalletEntry(walletReq);

	    if (!evaluateMobiliserResponse(walletResp)) {
		LOG
			.warn(
				"Error while fetching wallet entry for walletEntryId [{}]",
				walletId);
		return null;
	    }

	    return walletResp.getWalletEntry();
	} catch (Exception e) {
	    LOG.error("# Error while fetching wallet entry for walletId [{}]",
		    walletId, e);
	}
	return null;
    }

    public List<CustomerNetwork> getCustomerNetworkList(Long customerId,
	    int type, int status) {
	LOG.debug("# MobiliserBasePage.getCustomerNetworkList()");
	List<CustomerNetwork> customerNetworkList = null;
	try {
	    GetCustomerNetworksRequest request = getNewMobiliserRequest(GetCustomerNetworksRequest.class);

	    request.setCustomerId(getMobiliserWebSession()
		    .getLoggedInCustomer().getCustomerId());

	    request.setType(new Integer(type));

	    request.setIncludeInactive(false);
	    request.setStatus(new Integer(status));

	    GetCustomerNetworksResponse response = wsCustNetworkClient
		    .getCustomerNetworks(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    customerNetworkList = response.getCustomerNetworks();
	} catch (Exception e) {
	    LOG
		    .error(
			    "# Error occurred in getCustomerNetworkList for customerId [{}] type [{}]",
			    new Object[] { customerId, type, e });
	}
	return customerNetworkList;
    }

    public List<HistoryEntry> getCustomerHistoryList(
	    GetCustomerHistoryRequest customerHistoryRequest) {
	LOG.debug("# MobiliserBasePage.getCustomerHistoryList()");
	List<HistoryEntry> historyList = null;
	try {

	    GetCustomerHistoryResponse customerHistoryResponse = wsCustomerClient
		    .getCustomerHistory(customerHistoryRequest);

	    if (!evaluateMobiliserResponse(customerHistoryResponse)) {
		return null;
	    }
	    historyList = customerHistoryResponse.getHistory();

	} catch (Exception e) {
	    LOG.error("# Error while getting customer History ", e);
	}
	return historyList;
    }

    public Customer getCustomerByCustomerId(Long customerId) {
	LOG.debug("# MobiliserBasePage.getCustomerByCustomerId()");
	Customer customer = null;
	try {
	    GetCustomerRequest request = getNewMobiliserRequest(GetCustomerRequest.class);
	    request.setCustomerId(customerId);
	    GetCustomerResponse getCustomerResponse = wsCustomerClient
		    .getCustomer(request);

	    if (!evaluateMobiliserResponse(getCustomerResponse)) {
		return null;
	    }
	    customer = getCustomerResponse.getCustomer();

	} catch (Exception e) {
	    LOG.error("# Error while getting customer  ", e);
	}
	return customer;
    }

    public List<LimitSetClass> getLimitSetClassList(Long entityId,
	    Integer entityType) throws Exception {
	LOG.debug("# MobiliserBasePage.getLimitSet()");
	GetLimitSetClassesRequest limitReq = getNewMobiliserRequest(GetLimitSetClassesRequest.class);
	limitReq.setEntityId(entityId);
	limitReq.setEntityType(entityType);
	GetLimitSetClassesResponse limitResp = wsLimitClient
		.getLimitSetClasses(limitReq);

	if (evaluateMobiliserResponse(limitResp)) {
	    LOG.warn("Successfully fetched limit set");
	    return limitResp.getLimitSetClasses();
	}

	return null;
    }

    public List<CustomerBean> findCustomer(
	    FindHierarchicalCustomerRequest request) {
	LOG
		.debug("# MobiliserBasePage.findCustomer FindHierarchicalCustomerRequest()");
	List<CustomerBean> customers = new ArrayList<CustomerBean>();
	if (request.getAgentId() == 0)
	    request.setAgentId(getMobiliserWebSession().getLoggedInCustomer()
		    .getCustomerId());
	request.setMaxRecords(500);
	if (getMobiliserWebSession().getMaxViewLevel() != -1)
	    request.setMaxHierarchyLevel(getMobiliserWebSession()
		    .getMaxViewLevel());
	request.setMinHierarchyLevel(1);
	FindHierarchicalCustomerResponse resp = wsCustomerClient
		.findHierarchicalCustomer(request);
	List<HierarchicalCustomer> agents = resp.getCustomers();
	for (HierarchicalCustomer c : agents) {
	    if (!c.getId().equals(
		    getMobiliserWebSession().getLoggedInCustomer()
			    .getCustomerId())) {
		CustomerBean cb = Converter.getInstance()
			.getCustomerBeanFromCustomer(c);
		cb.setHierarchyLevel(c.getHierarchylevel());
		customers.add(cb);
	    }
	}
	return customers;
    }

    public List<LimitSet> findLimitSet(Long limitSetId) throws Exception {
	List<LimitSet> limitSetList = new ArrayList<LimitSet>();
	GetLimitSetsRequest limitSetReq = getNewMobiliserRequest(GetLimitSetsRequest.class);
	limitSetReq.setFetchIndividual(Boolean.FALSE);
	GetLimitSetsResponse limitSetResp = wsLimitClient
		.getLimitSets(limitSetReq);
	if (!evaluateMobiliserResponse(limitSetResp))
	    return null;
	LOG.info("# GetLimitSets Success.");
	limitSetList = limitSetResp.getLimitSets();

	if (PortalUtils.exists(limitSetList)) {

	    if (PortalUtils.exists(limitSetId)) {
		for (LimitSet limitSet : limitSetList) {
		    if (limitSet.getId().longValue() == limitSetId.longValue()) {
			limitSetList.clear();
			if (limitSet.isIndividual())
			    limitSetList.add(limitSet);
			break;
		    }
		}
	    }

	}

	return limitSetList;
    }

    public List<LimitClass> findLimitClassList() throws Exception {
	List<LimitClass> limiClassList = new ArrayList<LimitClass>();
	GetLimitClassesRequest limitClassReq = getNewMobiliserRequest(GetLimitClassesRequest.class);
	GetLimitClassesResponse limitClassResp = wsLimitClient
		.getLimitClasses(limitClassReq);
	if (!evaluateMobiliserResponse(limitClassResp))
	    return null;
	LOG.info("# GetLimitClass Success.");
	limiClassList = limitClassResp.getLimitClasses();
	return limiClassList;
    }

    public LimitSet addLimitSet(LimitSet limitSet) throws Exception {
	CreateLimitSetRequest createLimitSetReq = getNewMobiliserRequest(CreateLimitSetRequest.class);
	createLimitSetReq.setLimitSet(limitSet);
	CreateLimitSetResponse createLimitSetResp = wsLimitClient
		.createLimitSet(createLimitSetReq);
	if (!evaluateMobiliserResponse(createLimitSetResp))
	    return null;
	if (limitSet.isIndividual()) {
	    super.info(getLocalizer().getString(
		    "MESSAGE.INDIVIDUAL_LIMITSET_ADDED", this));
	} else {
	    super
		    .info(getLocalizer().getString("MESSAGE.LIMITSET_ADDED",
			    this));
	}

	LOG.info("# CreateLimitSets Success.");
	limitSet.setId(createLimitSetResp.getLimitSetId());
	return limitSet;
    }

    public LimitSet CreateIndividualLimitSet(LimitSet limitSet,
	    CustomerBean customer) {
	return null;
    }

    public LimitClass addLimitClass(LimitClass limitClass, Long entityId,
	    Integer type) throws Exception {

	LOG.debug("# MobiliserBasePage.createAgentLimitSetting()");
	CreateLimitClassRequest addLimitReq = getNewMobiliserRequest(CreateLimitClassRequest.class);
	if (PortalUtils.exists(entityId) && PortalUtils.exists(type)) {
	    addLimitReq.setEntityId(entityId);
	    addLimitReq.setEntityType(type);
	}

	String currency;
	if (PortalUtils.exists(limitClass.getCurrency())) {
	    currency = limitClass.getCurrency();
	} else {
	    currency = getConfiguration().getCurrency();
	}
	limitClass.setCurrency(currency);
	addLimitReq.setLimitClass(limitClass);
	CreateLimitClassResponse addLimitResp = wsLimitClient
		.createLimitClass(addLimitReq);

	if (!evaluateMobiliserResponse(addLimitResp)) {
	    return null;
	}
	super.info(getLocalizer().getString("MESSAGE.LIMITCLASS_ADDED", this));
	return limitClass;

    }

    public LimitClass updateLimitClass(LimitClass limitClass) throws Exception {
	LOG.debug("# MobiliserBasePage.updateLimitClass");
	UpdateLimitClassRequest updateLimitClassReq = getNewMobiliserRequest(UpdateLimitClassRequest.class);
	updateLimitClassReq.setLimitClass(limitClass);
	UpdateLimitClassResponse updateLimitClassResp = wsLimitClient
		.updateLimitClass(updateLimitClassReq);
	if (!evaluateMobiliserResponse(updateLimitClassResp)) {
	    return null;
	}
	super
		.info(getLocalizer().getString("MESSAGE.LIMITCLASS_UPDATED",
			this));
	return limitClass;
    };

    public boolean removeLimitSet(long limitSetId) throws Exception {

	DeleteLimitSetRequest delLimitSetReq = getNewMobiliserRequest(DeleteLimitSetRequest.class);
	delLimitSetReq.setLimitSetId(limitSetId);
	DeleteLimitSetResponse delLimitSetResp = wsLimitClient
		.deleteLimitSet(delLimitSetReq);
	if (!evaluateMobiliserResponse(delLimitSetResp))
	    return false;
	LOG.info("# DeleteLimitSets Success.");
	return true;
    }

    public boolean removeLimitClass(Long limitClassId, Long entity,
	    Integer entityType) throws Exception {
	List<LimitSetClass> limitSetClassList = getLimitSetClassList(entity,
		entityType);
	if (PortalUtils.exists(limitSetClassList)
		&& PortalUtils.exists(limitClassId)) {
	    for (LimitSetClass limitSetClass : limitSetClassList) {
		if (limitSetClass.getLimitClass().getId().longValue() == limitClassId
			.longValue()) {
		    if (removeLimitSetClass(limitSetClass.getId())) {
			if (deleteLimitClass(limitClassId)) {
			    return true;
			} else {
			    return false;
			}
		    }
		    break;
		}
	    }
	} else {
	    if (deleteLimitClass(limitClassId)) {
		return true;
	    } else {
		return false;
	    }
	}
	return false;
    }

    public boolean deleteLimitClass(Long limitClassId) throws Exception {
	DeleteLimitClassRequest deleteLimitClassReq = getNewMobiliserRequest(DeleteLimitClassRequest.class);
	deleteLimitClassReq.setLimitClassId(limitClassId);
	DeleteLimitClassResponse deleteLimitClassResp = wsLimitClient
		.deleteLimitClass(deleteLimitClassReq);
	if (!evaluateMobiliserResponse(deleteLimitClassResp))
	    return false;
	LOG.info("# Delete Limit Class Success.");
	return true;
    }

    public LimitSetClass addLimitSetClass(Long limitSetId,
	    LimitSetClass limitSetClass) throws Exception {
	CreateLimitSetClassRequest createLimitSetClassReq = getNewMobiliserRequest(CreateLimitSetClassRequest.class);
	createLimitSetClassReq.setLimitSetId(limitSetId);
	createLimitSetClassReq.setLimitClassId(limitSetClass.getLimitClass()
		.getId());
	createLimitSetClassReq.setUseCaseId(limitSetClass.getUseCaseId());

	CreateLimitSetClassResponse createLimitSetClassResp = wsLimitClient
		.createLimitSetClass(createLimitSetClassReq);
	if (!evaluateMobiliserResponse(createLimitSetClassResp))
	    return null;
	LOG.info("# Add Limit Set Class Success.");
	super.info(getLocalizer().getString("MESSAGE.LIMITSET_CLASS.SAVED",
		this));
	limitSetClass.setId(createLimitSetClassResp.getLimitSetClassId());
	return limitSetClass;
    }

    public void updateLimitSetClass(Long limitSetId, LimitSetClass limitSetClass)
	    throws Exception {
	UpdateLimitSetClassRequest updateLimitSetClassReq = getNewMobiliserRequest(UpdateLimitSetClassRequest.class);
	updateLimitSetClassReq.setLimitClassId(limitSetClass.getLimitClass()
		.getId());
	updateLimitSetClassReq.setLimitSetClassId(limitSetClass.getId());
	updateLimitSetClassReq.setLimitSetId(limitSetId);
	updateLimitSetClassReq.setUseCaseId(limitSetClass.getUseCaseId());
	UpdateLimitSetClassResponse updateLimitSetClassResp = wsLimitClient
		.updateLimitSetClass(updateLimitSetClassReq);
	if (!evaluateMobiliserResponse(updateLimitSetClassResp))
	    return;
	LOG.info("# Update Limit Set Class Success.");
	super.info(getLocalizer().getString("MESSAGE.LIMITSET_CLASS.UPDATED",
		this));
    }

    public boolean removeLimitSetClass(long limitSetClassId) throws Exception {
	DeleteLimitSetClassRequest removeLimitSetClassReq = getNewMobiliserRequest(DeleteLimitSetClassRequest.class);
	removeLimitSetClassReq.setLimitSetClassId(limitSetClassId);
	DeleteLimitSetClassResponse removeLimitSetClassResp = wsLimitClient
		.deleteLimitSetClass(removeLimitSetClassReq);

	if (!evaluateMobiliserResponse(removeLimitSetClassResp))
	    return false;
	LOG.info("# DeleteLimitSetClass Success.");
	return true;
    }

    public List<CustomerBean> findCustomer(FindCustomerRequest request) {
	LOG.debug("# MobiliserBasePage.findCustomer()");
	List<CustomerBean> customers = new ArrayList<CustomerBean>();
	request.setMaxRecords(500);
	FindCustomerResponse resp = wsCustomerClient.findCustomer(request);
	List<Customer> agents = resp.getCustomers();
	for (Customer c : agents) {
	    CustomerBean cb = Converter.getInstance()
		    .getCustomerBeanFromCustomer(c);
	    customers.add(cb);
	}
	return customers;
    }

    public long convertAmountToLong(String strAmount) throws ParseException {
	LOG.debug("# MobiliserBasePage.convertAmountToLong()");
	long amount = FormatUtils.toAmount(strAmount, Currency
		.getInstance(getConfiguration().getCurrency()),
		getMobiliserWebSession().getLocale());
	return amount;
    }

    public String convertAmountToString(long amount) {
	LOG.debug("# MobiliserBasePage.convertAmountToString(long)");
	String amountStr = FormatUtils.formatCurrencyAmount(amount, Currency
		.getInstance(getConfiguration().getCurrency()),
		getMobiliserWebSession().getLocale());
	return amountStr;
    }

    public String convertAmountToString(MoneyAmount amount) {
	LOG.debug("# MobiliserBasePage.convertAmountToString(MoneyAmount)");
	String amountStr = FormatUtils.formatCurrencyAmount(amount.getValue(),
		Currency.getInstance(amount.getCurrency()),
		getMobiliserWebSession().getLocale());
	return amountStr;
    }

    public String convertAmountToStringWithCurrency(long amount) {
	LOG
		.debug("# MobiliserBasePage.convertAmountToStringWithCurrency(long)");
	String amountStr = FormatUtils.formatCurrencyAmount(amount, Currency
		.getInstance(getConfiguration().getCurrency()),
		getMobiliserWebSession().getLocale())
		+ Currency.getInstance(getConfiguration().getCurrency())
			.getSymbol(getMobiliserWebSession().getLocale());
	return amountStr;
    }

    public String convertAmountToStringWithCurrency(long amount, String currency) {
	LOG
		.debug("# MobiliserBasePage.convertAmountToStringWithCurrency(long,String)");
	String amountStr = FormatUtils.formatCurrencyAmount(amount, Currency
		.getInstance(currency), getMobiliserWebSession().getLocale())
		+ Currency.getInstance(currency).getSymbol(
			getMobiliserWebSession().getLocale());
	return amountStr;
    }

    public String convertAmountToStringWithCurrency(MoneyAmount amount) {
	LOG
		.debug("# MobiliserBasePage.convertAmountToStringWithCurrency(MoneyAmount)");
	if (!PortalUtils.exists(amount)
		|| !PortalUtils.exists(amount.getCurrency())) {
	    return "";
	}
	String amountStr = FormatUtils.formatCurrencyAmount(amount.getValue(),
		Currency.getInstance(amount.getCurrency()),
		getMobiliserWebSession().getLocale())
		+ Currency.getInstance(amount.getCurrency()).getSymbol(
			getMobiliserWebSession().getLocale());
	return amountStr;
    }

    public String getCurrencySymbol() {
	return Currency.getInstance(getConfiguration().getCurrency())
		.getSymbol(getMobiliserWebSession().getLocale());
    }

    public void setCreateStatus(boolean createStatus) {
	this.createStatus = createStatus;
    }

    public boolean isCreateStatus() {
	return createStatus;
    }

    public static class KeyValueComparatorId implements
	    Comparator<KeyValue<Integer, String>> {
	public int compare(KeyValue<Integer, String> o1,
		KeyValue<Integer, String> o2) {
	    return (o1.getKey() < o2.getKey()) ? -1 : 1;
	}
    }

    private static class KeyValueComparatorString implements
	    Comparator<KeyValue<String, String>> {
	public int compare(KeyValue<String, String> o1,
		KeyValue<String, String> o2) {
	    return o1.getKey().compareTo(o2.getKey());
	}
    }

    public String getTransactionAmount(SimpleTransaction txn, long customerId) {
	LOG.debug("# MobiliserBasePage.getTransactionAmount(...)");
	MoneyAmount amt = new MoneyAmount();
	String prefix = "";
	if (customerId == txn.getPayerId() && customerId != txn.getPayeeId()) {
	    amt = txn.getPayerAmount();
	    prefix = "-";
	} else if (customerId == txn.getPayeeId()
		&& customerId != txn.getPayerId()) {
	    amt = txn.getPayeeAmount();
	} else if (customerId == txn.getPayeeId()
		&& customerId == txn.getPayerId() && txn.getPayerPiType() == 0
		&& txn.getPayeePiType() != 0) {
	    amt = txn.getPayerAmount();
	    prefix = "-";
	} else if (customerId == txn.getPayeeId()
		&& customerId == txn.getPayerId() && txn.getPayeePiType() == 0
		&& txn.getPayerPiType() != 0) {
	    amt = txn.getPayeeAmount();
	} else if (customerId == txn.getPayeeId()
		&& customerId == txn.getPayerId() && txn.getPayeePiType() != 0
		&& txn.getPayerPiType() != 0) {
	    amt = txn.getPayeeAmount();
	}

	return prefix + convertAmountToStringWithCurrency(amt);
    }

    public PaymentInstrument addToWallet(Customer payeeCustomer,
	    PaymentInstrument pInstrument) throws Exception {
	LOG.debug("MobiliserBasePage.addToWallet()");

	CreatePaymentInstrumentRequest createPiReq = getNewMobiliserRequest(CreatePaymentInstrumentRequest.class);
	createPiReq.setPaymentInstrument(pInstrument);
	CreatePaymentInstrumentResponse createPiResp = wsWalletClient
		.createPaymentInstrument(createPiReq);

	if (!evaluateMobiliserResponse(createPiResp))
	    return null;

	WalletEntry wallet = new WalletEntry();
	wallet.setCustomerId(payeeCustomer.getId());
	wallet.setLimitSetId(null);
	wallet.setPaymentInstrumentId(createPiResp.getPaymentInstrumentId());
	CreateWalletEntryRequest walletEntry = getNewMobiliserRequest(CreateWalletEntryRequest.class);
	walletEntry.setWalletEntry(wallet);
	CreateWalletEntryResponse walletEntryResp = wsWalletClient
		.createWalletEntry(walletEntry);
	if (evaluateMobiliserResponse(walletEntryResp)) {
	    pInstrument.setId(createPiResp.getPaymentInstrumentId());
	    return pInstrument;
	}

	return null;
    }

    public List<CustomerPrivilege> getCustomerPrivilegesList(Long customerID) {
	LOG.debug("MobiliserBasePage.getCustomerPrivilegesList()");
	List<CustomerPrivilege> privList = new ArrayList<CustomerPrivilege>();
	try {
	    GetUmgrCustomerPrivilegesRequest umgrReq = getNewMobiliserRequest(GetUmgrCustomerPrivilegesRequest.class);
	    umgrReq.setCustomerId(customerID);
	    GetUmgrCustomerPrivilegesResponse umgrResp = wsRolePrivilegeClient
		    .getUmgrCustomerPrivileges(umgrReq);
	    if (!evaluateMobiliserResponse(umgrResp))
		return null;
	    privList = umgrResp.getUmgrPrivileges();

	} catch (Exception e) {
	    LOG.error("# Error while getting available privileges.", e);
	}

	return privList;

    }

    protected List<String> getAllCustomerPrivileges(Long customerId)
	    throws Exception {
	LOG.debug("MobiliserBasePage.getAllCustomerPrivileges()");
	List<String> privList = new ArrayList<String>();
	GetAllPrivilegesRequest getAllPrivReq = getNewMobiliserRequest(GetAllPrivilegesRequest.class);
	getAllPrivReq.setCustomerId(customerId);
	GetAllPrivilegesResponse getAllPrivResp = wsSecurityClient
		.getPrivileges(getAllPrivReq);
	if (!evaluateMobiliserResponse(getAllPrivResp))
	    return null;
	privList = getAllPrivResp.getPrivileges();
	return privList;
    }

    // returns all the assigned roles of the customer
    protected List<CustomerRole> getRoles(Long cutomerId) throws Exception {

	LOG.debug("MobiliserBasePage.getRoles()");
	GetUmgrCustomerRolesRequest umgrReq = getNewMobiliserRequest(GetUmgrCustomerRolesRequest.class);
	umgrReq.setCustomerId(cutomerId);
	GetUmgrCustomerRolesResponse umgrResp = wsRolePrivilegeClient
		.getUmgrCustomerRoles(umgrReq);
	if (!evaluateMobiliserResponse(umgrResp))
	    return null;
	List<CustomerRole> roles = umgrResp.getUmgrRoles();
	return roles;

    }

    public List<FeeType> getFeeTypes() {
	LOG.debug("# MobiliserBasePage.getFeeTypes()");
	GetFeeTypesResponse response = null;
	try {
	    GetFeeTypesRequest request = getNewMobiliserRequest(GetFeeTypesRequest.class);

	    response = wsFeeConfClient.getFeeTypes(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }
	} catch (Exception e) {
	    LOG.error("# Error while getting fee types.", e);
	}
	return response != null ? response.getFeeTypes() : null;
    }

    public List<UseCaseFeeType> getUseCaseFeeTypes()
	    throws DataProviderLoadException {

	LOG.debug("# MobiliserBasePage.getUseCaseFeeTypes()");
	GetUseCasesFeeTypesResponse response = null;
	try {
	    GetUseCasesFeeTypesRequest request = getNewMobiliserRequest(GetUseCasesFeeTypesRequest.class);

	    response = wsFeeConfClient.getUseCasesFeeTypes(request);

	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error while getting use case configurations");
		return null;
	    }
	} catch (Exception e) {
	    LOG.error("# Error while getting use case configurations", e);
	    throw new DataProviderLoadException();

	}
	return response.getUseCaseFeeTypes();
    }

    public void removeFeeSet(FeeSet feeSet) throws Exception {
	LOG.debug("# MobiliserBasePage.removeFeeSet()");
	DeleteFeeSetResponse response;

	DeleteFeeSetRequest request = getNewMobiliserRequest(DeleteFeeSetRequest.class);
	request.setFeeSetId(feeSet.getId());
	response = wsFeeConfClient.deleteFeeSet(request);
	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error occurred while deleting fee set");
	    return;
	}
    }

    public List<ScaleStepConfBean> getScaleStepBeanList(Long feeSetId)
	    throws DataProviderLoadException {
	List<ScaleStepConfBean> scaleStepsBeanList = new ArrayList<ScaleStepConfBean>();

	if (!PortalUtils.exists(feeSetId)) {
	    return scaleStepsBeanList;
	}

	List<FeeTypeCurrencyScaleSteps> scaleSteps = getScaleSteps(feeSetId,
		null);

	ScaleStepConfBean scaleStepBean;
	for (FeeTypeCurrencyScaleSteps feeType : scaleSteps) {
	    scaleStepBean = new ScaleStepConfBean();
	    scaleStepBean.setFeeTypeId(feeType.getFeeTypeId());
	    scaleStepBean.setFeeTypeName(feeType.getFeeTypeName());
	    scaleStepBean.setCssStyle(Constants.CSS_STYLE_FEE_TYPE);
	    scaleStepsBeanList.add(scaleStepBean);

	    for (CurrencyScaleSteps feeTypeCurrency : feeType
		    .getCurrenciesScaleSteps()) {
		scaleStepBean = new ScaleStepConfBean();
		scaleStepBean.setFeeTypeId(feeType.getFeeTypeId());
		scaleStepBean.setFeeTypeName(feeType.getFeeTypeName());
		scaleStepBean.setCurrency(feeTypeCurrency.getCurrency());
		scaleStepBean.setCssStyle(Constants.CSS_STYLE_ODD);
		scaleStepsBeanList.add(scaleStepBean);
		for (ScaleStep scaleStep : feeTypeCurrency.getScaleSteps()) {
		    scaleStepBean = new ScaleStepConfBean();
		    scaleStepBean
			    .setScalePeriodId(scaleStep.getScalePeriodId());
		    scaleStepBean.setFeeTypeId(feeType.getFeeTypeId());
		    scaleStepBean.setFeeTypeName(feeType.getFeeTypeName());
		    scaleStepBean.setCurrency(feeTypeCurrency.getCurrency());
		    scaleStepBean.setMaxAmount(scaleStep.getMaximumAmount());
		    scaleStepBean.setMinAmount(scaleStep.getMinimumAmount());
		    scaleStepBean.setOnTop(scaleStep.getOnTopAmount());
		    scaleStepBean.setPercentage(scaleStep.getPercentage());
		    scaleStepBean.setThresholdAmount(scaleStep
			    .getThresholdAmount());
		    scaleStepsBeanList.add(scaleStepBean);
		    scaleStepBean.setCssStyle(Constants.CSS_STYLE_EVEN);
		}

	    }

	}
	return scaleStepsBeanList;
    }

    public List<FeeTypeCurrencyScaleSteps> getScaleSteps(Long feeSetId,
	    Integer feeTypeId) throws DataProviderLoadException {
	LOG.debug("# MobiliserBasePage.getScaleSteps()");

	GetScaleStepsByFeeTypeAndFeeSetResponse response = null;
	try {

	    GetScaleStepsByFeeTypeAndFeeSetRequest request = getNewMobiliserRequest(GetScaleStepsByFeeTypeAndFeeSetRequest.class);
	    request.setFeeSetId(feeSetId);
	    request.setFeeTypeId(feeTypeId);
	    response = wsFeeConfClient.getScaleStepsByFeeTypeAndFeeSet(request);

	    if (!evaluateMobiliserResponse(response)) {
		throw new DataProviderLoadException();
	    }
	} catch (Exception e) {
	    throw new DataProviderLoadException();
	}
	return response.getFeeTypesCurrenciesScaleSteps();
    }

    public List<OrgUnit> getOrgUnitConfigurationsList()
	    throws DataProviderLoadException {
	LOG.debug("# MobiliserBasePage.getOrgUnitConfigurationsList()");
	GetOrgUnitsResponse response = null;
	List<OrgUnit> orgUnitsList = null;
	try {
	    GetOrgUnitsRequest request = getNewMobiliserRequest(GetOrgUnitsRequest.class);
	    response = wsSystemConfClient.getOrgUnits(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while getting org units");
		throw new DataProviderLoadException();
	    }
	    orgUnitsList = response.getOrgUnits();
	} catch (Exception e) {
	    throw new DataProviderLoadException();
	}
	return orgUnitsList;
    }

    public List<CustomerType> getCustTypeConfigList()
	    throws DataProviderLoadException {
	LOG.debug("# MobiliserBasePage.getCustTypeConfigList()");
	GetCustomerTypesResponse response = null;
	List<CustomerType> customerTypesList = null;
	try {
	    GetCustomerTypesRequest request = getNewMobiliserRequest(GetCustomerTypesRequest.class);
	    response = wsSystemConfClient.getCustomerTypes(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while getting customer types");
		throw new DataProviderLoadException();
	    }
	    customerTypesList = response.getCustomerTypes();
	} catch (Exception e) {
	    throw new DataProviderLoadException();
	}
	return customerTypesList;
    }

    public String getRiskCatName(int riskCatId) {
	LOG.debug("# MobiliserBasePage.getRiskCatName()");
	for (KeyValue<Integer, String> riskCat : getRiskCategories()) {
	    if (riskCat.getKey().intValue() == riskCatId)
		return riskCat.getValue();
	}

	return "";
    }

    public List<KeyValue<Integer, String>> getRiskCategories() {
	List<RiskCategory> riskCategories = null;
	List<KeyValue<Integer, String>> riskCatList = new ArrayList<KeyValue<Integer, String>>();
	try {
	    riskCategories = getRiskCatConfList();
	    KeyValue<Integer, String> keyValue;
	    for (RiskCategory riskCat : riskCategories) {
		keyValue = new KeyValue<Integer, String>(new Integer(riskCat
			.getId()), riskCat.getName());
		riskCatList.add(keyValue);
	    }
	} catch (DataProviderLoadException dpe) {
	    LOG.error("#An error occurred while fetching risk categories", dpe);
	}

	return riskCatList;

    }

    public List<KeyValue<Long, String>> getLimitSets() {
	List<KeyValue<Long, String>> limtSetList = new ArrayList<KeyValue<Long, String>>();
	List<LimitSet> limitSets = null;
	try {
	    limitSets = findLimitSet(null);
	    KeyValue<Long, String> keyValue;
	    for (LimitSet limitSet : limitSets) {
		keyValue = new KeyValue<Long, String>(limitSet.getId(),
			limitSet.getName());
		limtSetList.add(keyValue);
	    }
	} catch (Exception e) {
	    LOG.error("#An error occurred while fetching limit sets");
	}

	return limtSetList;

    }

    public List<RiskCategory> getRiskCatConfList()
	    throws DataProviderLoadException {
	LOG.debug("# MobiliserBasePage.getRiskCatConfList()");
	GetRiskCategoriesResponse response = null;
	List<RiskCategory> riskcategoriesList = null;
	try {
	    GetRiskCategoriesRequest request = getNewMobiliserRequest(GetRiskCategoriesRequest.class);
	    response = wsSystemConfClient.getRiskCategories(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# An error occurred while getting risk categories");
		throw new DataProviderLoadException();
	    }
	    riskcategoriesList = response.getRiskCategories();
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting risk categories");
	    throw new DataProviderLoadException();
	}
	return riskcategoriesList;
    }

    public List<KeyValue<String, String>> getCancelationReasons() {
	List<KeyValue<String, String>> cancellationReasons = new ArrayList<KeyValue<String, String>>();
	try {
	    GetLookupsRequest request = getNewMobiliserRequest(GetLookupsRequest.class);
	    request.setEntityName("cancellationReasons");
	    GetLookupsResponse response = wsSystemConfClient
		    .getLookups(request);
	    if (!evaluateMobiliserResponse(response))
		return null;
	    cancellationReasons = convertLookupToIntKeyValueList(response
		    .getLookupEntities());
	} catch (Exception e) {
	    error(getLocalizer().getString("cancellationReasons.lookUp.error",
		    this));
	    LOG.error("# Error occurred while getting cancellationReasons");
	}
	return cancellationReasons;
    }

    private List<KeyValue<String, String>> convertLookupToIntKeyValueList(
	    List<LookupEntity> lookUpEntityList) {
	List<KeyValue<String, String>> keyValueList = new ArrayList<KeyValue<String, String>>();
	for (LookupEntity lookupEntity : lookUpEntityList) {
	    keyValueList.add(new KeyValue<String, String>(lookupEntity.getId(),
		    lookupEntity.getName()));
	}
	return keyValueList;
    }

    /**
     * 
     * @param identification
     * @param identificationType
     * @param customerId
     * @return true if identification is unique
     * @throws Exception
     */
    public boolean uniqueIdentificationCheck(String identification,
	    int identificationType, Long customerId) throws Exception {
	GetCustomerByIdentificationRequest req = getNewMobiliserRequest(GetCustomerByIdentificationRequest.class);
	if (identificationType == Constants.IDENT_TYPE_MSISDN) {
	    PhoneNumber pn = new PhoneNumber(identification, getConfiguration()
		    .getCountryCode());
	    identification = pn.getInternationalFormat();
	}
	req.setIdentification(identification);
	req.setIdentificationType(identificationType);
	GetCustomerByIdentificationResponse res = wsCustomerClient
		.getCustomerByIdentification(req);
	if (!evaluateMobiliserResponse(res)) {
	    LOG.warn("# Get customer by identification request failed");
	    return false;
	}

	if (res.getCustomer() != null) {
	    if (PortalUtils.exists(customerId)
		    && customerId.equals(res.getCustomer().getId()))
		return true;

	    LOG
		    .debug(
			    "# The entered The entered identification [{}] has already been registered",
			    customerId);
	    if (identificationType == Constants.IDENT_TYPE_MSISDN)
		error(getLocalizer().getString("msisdn.inuse.error", this));
	    else
		error(getLocalizer().getString("username.inuse.error", this));
	    return false;
	}
	return true;

    }

    public boolean checkPinStrength(CustomerBean customer) throws Exception {

	CheckCredentialStrengthRequest credentialStrengthRequest = getNewMobiliserRequest(CheckCredentialStrengthRequest.class);
	credentialStrengthRequest.setCredential(String.valueOf(customer
		.getPin()));
	credentialStrengthRequest
		.setCredentialType(Constants.CREDENTIAL_TYPE_PIN);
	credentialStrengthRequest.setCustomerTypeId(Constants.CONSUMER_IDTYPE);
	CheckCredentialStrengthResponse strengthResponse = wsSecurityClient
		.checkCredentialStrength(credentialStrengthRequest);

	if (!evaluateMobiliserResponse(strengthResponse)) {
	    LOG.warn("# Entered pin is weak");
	    return false;
	}

	return true;
    }

    public boolean checkPasswordStrength(CustomerBean customer)
	    throws Exception {
	CheckCredentialStrengthRequest credentialStrengthRequest = getNewMobiliserRequest(CheckCredentialStrengthRequest.class);
	credentialStrengthRequest.setCredential(customer.getPassword());
	credentialStrengthRequest
		.setCredentialType(Constants.CREDENTIAL_TYPE_PASSWORD);
	credentialStrengthRequest.setCustomerTypeId(Constants.CONSUMER_IDTYPE);
	CheckCredentialStrengthResponse strengthResponse = wsSecurityClient
		.checkCredentialStrength(credentialStrengthRequest);

	if (!evaluateMobiliserResponse(strengthResponse)) {
	    LOG.warn("# Entered password is weak");
	    return false;

	}
	return true;
    }

    public List<KeyValue<Integer, String>> getSelectableTimeFrame() {
	List<KeyValue<Integer, String>> selectableTimeFrame = new ArrayList<KeyValue<Integer, String>>();
	selectableTimeFrame.add(new KeyValue<Integer, String>(
		Constants.TXN_TIMEFRAME_ALL, getLocalizer().getString(
			"timeframe." + Constants.TXN_TIMEFRAME_ALL, this)));
	selectableTimeFrame
		.add(new KeyValue<Integer, String>(
			Constants.TXN_TIMEFRAME_LAST_THREE_MONTH,
			getLocalizer()
				.getString(
					"timeframe."
						+ Constants.TXN_TIMEFRAME_LAST_THREE_MONTH,
					this)));
	selectableTimeFrame.add(new KeyValue<Integer, String>(
		Constants.TXN_TIMEFRAME_LAST_MONTH, getLocalizer()
			.getString(
				"timeframe."
					+ Constants.TXN_TIMEFRAME_LAST_MONTH,
				this)));
	selectableTimeFrame.add(new KeyValue<Integer, String>(
		Constants.TXN_TIMEFRAME_LAST_TEN_DAYS,
		getLocalizer().getString(
			"timeframe." + Constants.TXN_TIMEFRAME_LAST_TEN_DAYS,
			this)));
	selectableTimeFrame
		.add(new KeyValue<Integer, String>(
			Constants.TXN_TIMEFRAME_LAST_WEEK,
			getLocalizer().getString(
				"timeframe."
					+ Constants.TXN_TIMEFRAME_LAST_WEEK,
				this)));
	selectableTimeFrame.add(new KeyValue<Integer, String>(
		Constants.TXN_TIMEFRAME_LAST_TWO_DAYS,
		getLocalizer().getString(
			"timeframe." + Constants.TXN_TIMEFRAME_LAST_TWO_DAYS,
			this)));

	return selectableTimeFrame;

    }

    public String encrypt(String text, String keyAlias)
	    throws KeyStoreException {
	String encrypted = text;

	PublicKey publicKey;
	publicKey = AsymmetricKeyUtils.getPublicKey(getConfiguration()
		.getPublicKeyStore(), getConfiguration().getKeyStorePw(),
		keyAlias);
	encrypted = AsymmetricKeyUtils.encrypt(text, publicKey);

	return encrypted;
    }

    public List<KeyValue<Integer, String>> getCustomerStatus() {
	List<KeyValue<Integer, String>> customerStatus = new ArrayList<KeyValue<Integer, String>>();
	customerStatus.add(new KeyValue<Integer, String>(1, getLocalizer()
		.getString("consumer.status_true", this)));
	customerStatus.add(new KeyValue<Integer, String>(0, getLocalizer()
		.getString("consumer.status_false", this)));
	return customerStatus;
    }

    public void setPingClientConfiguration(
	    DynamicServiceConfiguration clientConfig) {

	LOG.debug("# MobiliserBasePage.setPingClientConfiguration({})",
		clientConfig.getMobiliserEndpointUrl());

	wsDynamicPingClientConfiguration = clientConfig;

	LOG.debug("# Refresh ping client source...");
	wsDynamicPingClientSource.refresh();
    }

    public DynamicServiceConfiguration getPingClientConfiguration() {
	return wsDynamicPingClientConfiguration;
    }

    public boolean pingServer(String hostname, int port) {

	LOG.debug("# MobiliserBasePage.pingServer({}:{})", hostname, port);

	LOG.debug("Updating client configuration of ping service...");

	DynamicServiceConfiguration clientConfiguration = this
		.getPingClientConfiguration();

	clientConfiguration.setHostname(hostname);
	clientConfiguration.setPort(String.valueOf(port));

	this.setPingClientConfiguration(clientConfiguration);

	try {
	    PingRequest request = getNewMobiliserRequest(PingRequest.class);

	    PingResponse response = wsDynamicPingClient.ping(request);

	    if (!evaluateMobiliserResponse(response)) {
		return false;
	    }

	} catch (Exception e) {
	    // turn down ping exception logging to debug as fail is not an error
	    LOG.debug("# MobiliserBasePage.pingServer() - exception", e);
	    return false;
	}

	return true;
    }

    public void setManagementClientConfiguration(
	    DynamicServiceConfiguration clientConfig) {

	LOG.debug("# MobiliserBasePage.setManagementClientConfiguration({})",
		clientConfig.getMobiliserEndpointUrl());

	wsDynamicManagementClientConfiguration = clientConfig;

	LOG.debug("# Refresh management client source...");
	wsDynamicManagementClientSource.refresh();
    }

    public DynamicServiceConfiguration getManagementClientConfiguration() {
	return wsDynamicManagementClientConfiguration;
    }

    public List<ObjectInstanceBean> getRemoteManagedObjects(
	    ObjectNameBean searchBean) {

	LOG.debug("# MobiliserBasePage.getRemoteManagedObjects({})", searchBean
		.getObjectName());

	List<ObjectInstanceBean> value;

	try {
	    QueryMBeansRequest request = getNewMobiliserRequest(QueryMBeansRequest.class);

	    request.setSearchCriteriaBean(searchBean);

	    QueryMBeansResponse response = wsDynamicManagementClient
		    .queryMBeans(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    value = response.getObjectInstanceList().getResultList();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# MobiliserBasePage.getRemoteManagedObjects() - exception",
			    e);
	    return null;
	}

	return value;
    }

    public MBeanInfoBean getRemoteManagedObject(ObjectNameBean objectName) {

	LOG.debug("# MobiliserBasePage.getRemoteManagedObject({})", objectName
		.getObjectName());

	MBeanInfoBean value;

	try {
	    GetMBeanInfoRequest request = getNewMobiliserRequest(GetMBeanInfoRequest.class);

	    request.setObjectNameBean(objectName);

	    GetMBeanInfoResponse response = wsDynamicManagementClient
		    .getMBeanInfo(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    value = response.getMBeanInfoBean();

	} catch (Exception e) {
	    LOG.error(
		    "# MobiliserBasePage.getRemoteManagedObject() - exception",
		    e);
	    return null;
	}

	return value;
    }

    public MBeanAttributeValueBean getRemoteManagedAttribute(
	    AttributeBean attrBean) {

	LOG.debug("# MobiliserBasePage.getRemoteManagedAttribute({})", attrBean
		.getAttributeName());

	MBeanAttributeValueBean value;

	try {
	    GetMBeanAttributeValueRequest request = getNewMobiliserRequest(GetMBeanAttributeValueRequest.class);

	    request.setAttributeBean(attrBean);

	    GetMBeanAttributeValueResponse response = wsDynamicManagementClient
		    .getMBeanAttributeValue(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    value = response.getMBeanAttributeValueBean();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# MobiliserBasePage.getRemoteManagedAttribute() - exception",
			    e);
	    return null;
	}

	return value;
    }

    public MBeanAttributeValueListBean getRemoteManagedAttributes(
	    AttributeListBean attrListBean) {

	LOG.debug("# MobiliserBasePage.getRemoteManagedAttributes({})",
		attrListBean.getAttributeBean());

	MBeanAttributeValueListBean value;

	try {
	    GetMBeanAttributeValuesRequest request = getNewMobiliserRequest(GetMBeanAttributeValuesRequest.class);

	    request.setAttributeListBean(attrListBean);

	    GetMBeanAttributeValuesResponse response = wsDynamicManagementClient
		    .getMBeanAttributeValues(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    value = response.getMBeanAttributeValueListBean();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# MobiliserBasePage.getRemoteManagedAttributes() - exception",
			    e);
	    return null;
	}

	return value;
    }

    public CompositeAttributeValueListBean getMBeanAttributeValuesByCompositeKey(
	    CompositeAttributeBean compositeAttrBean) {

	LOG.debug("# MobiliserBasePage.getRemoteManagedAttribute({})",
		compositeAttrBean.getAttributeName());

	CompositeAttributeValueListBean value;

	try {
	    GetMBeanAttributeValuesByCompositeKeyRequest request = getNewMobiliserRequest(GetMBeanAttributeValuesByCompositeKeyRequest.class);

	    request.setCompositeAttributeBean(compositeAttrBean);

	    GetMBeanAttributeValuesByCompositeKeyResponse response = wsDynamicManagementClient
		    .getMBeanAttributeValuesByCompositeKey(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    value = response.getValueListBean();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# MobiliserBasePage.getRemoteManagedAttribute() - exception",
			    e);
	    return null;
	}

	return value;
    }

    public MBeanAttributeCompositeValueBean getRemoteManagedAttributeComposite(
	    CompositeAttributeBean compositeAttrBean) {

	LOG
		.debug(
			"# MobiliserBasePage.getRemoteManagedAttributeComposite({}, {})",
			new Object[] { compositeAttrBean.getAttributeName(),
				compositeAttrBean.getKey() });

	MBeanAttributeCompositeValueBean value;

	try {
	    GetMBeanAttributeCompositeValueRequest request = getNewMobiliserRequest(GetMBeanAttributeCompositeValueRequest.class);

	    request.setCompositeAttributeBean(compositeAttrBean);

	    GetMBeanAttributeCompositeValueResponse response = wsDynamicManagementClient
		    .getMBeanAttributeCompositeValue(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    value = response.getMBeanAttributeCompositeValueBean();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# MobiliserBasePage.getRemoteManagedAttributeComposite() - exception",
			    e);
	    return null;
	}

	return value;
    }

    public List<String> invokeRemoteManagedOperation(String objectName,
	    String operationName, String[] params) {

	LOG.debug("# MobiliserBasePage.invokeRemoteManagedOperation({})",
		operationName);

	List<String> result = Collections.EMPTY_LIST;

	// look for the mbean to find and populate the MBeanOperationInfo
	// object that is used for the remote operation call
	ObjectNameBean onb = new ObjectNameBean();
	onb.setObjectName(objectName);

	MBeanInfoBean mBeanInfo = this.getRemoteManagedObject(onb);

	if (null == mBeanInfo) {
	    LOG.debug("# No MBean found for the object name: {}", objectName);
	    return null;
	}

	MBeanOperationInfoBean operationToInvoke = null;

	// loop through each known operation for this mbean
	for (MBeanOperationInfoBean oi : mBeanInfo.getOperationInfo()) {

	    // at the moment don't complicate the operation call by checking
	    // the signature - just find the matching operation by name and
	    // and matching count of parameters
	    if (oi.getName().equals(operationName)) {

		int paramIdx = 0;

		if (oi.getSignature().size() == params.length) {
		    for (MBeanParameterInfoBean pi : oi.getSignature()) {
			pi.setValue(params[paramIdx++]);
		    }
		    operationToInvoke = oi;
		    break;
		}
	    }
	}

	if (null == operationToInvoke) {
	    LOG
		    .debug(
			    "# No MBean operation found for the object name/operation/no.params: {}/{}/{}",
			    new Object[] { objectName, operationName,
				    params.length });
	    return null;
	}

	try {
	    LOG.debug("# Found operation to invoke, now do it...");

	    InvokeMBeanOperationRequest request = getNewMobiliserRequest(InvokeMBeanOperationRequest.class);

	    request.setOperationInfoBean(operationToInvoke);

	    InvokeMBeanOperationResponse response = wsDynamicManagementClient
		    .invokeMBeanOperation(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    result = response.getOperationResult().getValue();

	    LOG.debug("# Operation returned result: {}", result);

	} catch (Exception e) {
	    LOG
		    .error(
			    "# MobiliserBasePage.invokeRemoteManagedOperation() - exception",
			    e);
	    return null;
	}

	return result;
    }

    public List<NotificationBean> getNotifications(ObjectNameBean objectName) {

	LOG.debug("# MobiliserBasePage.getNotifications({})", objectName);

	List<NotificationBean> results;

	try {
	    GetMBeanNotificationsRequest request = getNewMobiliserRequest(GetMBeanNotificationsRequest.class);

	    request.setObjectNameBean(objectName);

	    GetMBeanNotificationsResponse response = wsDynamicManagementClient
		    .getMBeanNotifications(request);

	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }

	    results = response.getNotificationList().getResultList();

	} catch (Exception e) {
	    LOG.error("# MobiliserBasePage.getNotifications() - exception", e);
	    return null;
	}

	return results;
    }

    public void refreshPreferencesClientSource() {
	preferencesService.getSystemPreferences().refresh();
    }

    public List<PreferencesApplication> getAllPreferencesApplicationNames()
	    throws Exception {

	LOG.debug("# MobiliserBasePage.getAllPreferencesApplicationNames()");

	GetPreferencesApplicationsRequest request = getNewMobiliserRequest(GetPreferencesApplicationsRequest.class);

	GetPreferencesApplicationsResponse response = wsPrefsClient
		.getAllApplicationNames(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while getting preferences applications");
	    return null;
	}

	return response.getApplication();
    }

    public PreferencesApplication getPreferencesApplication(String name)
	    throws Exception {

	LOG.debug("# MobiliserBasePage.getPreferencesApplication()");

	GetPreferencesApplicationRequest request = getNewMobiliserRequest(GetPreferencesApplicationRequest.class);

	request.setApplicationName(name);

	GetPreferencesApplicationResponse response = wsPrefsClient
		.getPreferencesApplication(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while getting preference application");
	    return null;
	}

	return response.getApplicationName();
    }

    public boolean createPreferencesApplication(String name, String desc,
	    String readPrivilege, String writePrivilege) throws Exception {

	LOG.debug("# MobiliserBasePage.createPreferencesApplication()");

	CreatePreferencesApplicationRequest request = getNewMobiliserRequest(CreatePreferencesApplicationRequest.class);

	PreferencesApplication appl = new PreferencesApplication();
	appl.setApplicationName(name);
	appl.setDescription(desc);
	appl.setReadPrivilege(readPrivilege);
	appl.setWritePrivilege(writePrivilege);

	request.setApplication(appl);

	CreatePreferencesApplicationResponse response = wsPrefsClient
		.createPreferencesApplication(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while creating preferences application");
	    return false;
	}

	return true;
    }

    public boolean deletePreferencesApplication(String name) throws Exception {

	LOG.debug("# MobiliserBasePage.deletePreferencesApplication()");

	DeletePreferencesApplicationRequest request = getNewMobiliserRequest(DeletePreferencesApplicationRequest.class);

	request.setApplicationName(name);

	DeletePreferencesApplicationResponse response = wsPrefsClient
		.deletePreferencesApplication(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while deleting preferences application");
	    return false;
	}

	return true;
    }

    public boolean updatePreferencesApplication(String name, String desc,
	    String readPrivilege, String writePrivilege) throws Exception {

	LOG.debug("# MobiliserBasePage.updatePreferencesApplication()");

	UpdatePreferencesApplicationRequest request = getNewMobiliserRequest(UpdatePreferencesApplicationRequest.class);

	PreferencesApplication appl = new PreferencesApplication();
	appl.setApplicationName(name);
	appl.setDescription(desc);
	appl.setReadPrivilege(readPrivilege);
	appl.setWritePrivilege(writePrivilege);

	request.setApplication(appl);

	UpdatePreferencesApplicationResponse response = wsPrefsClient
		.updatePreferencesApplication(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while updating preferences application");
	    return false;
	}

	return true;
    }

    public DetailedPreferencesTree getDetailedPreferences(String name,
	    String path) throws Exception {

	LOG.debug("# MobiliserBasePage.getDetailedPreferences()");

	GetDetailedPreferencesRequest request = getNewMobiliserRequest(GetDetailedPreferencesRequest.class);

	request.setApplicationName(name);
	request.setPath(path);

	GetDetailedPreferencesResponse response = wsPrefsClient
		.getDetailedPreferences(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while getting detailed preferences tree");
	    return null;
	}

	return response.getPreferences();
    }

    public PreferencesTree getPreferences(String name, String path)
	    throws Exception {

	LOG.debug("# MobiliserBasePage.getPreferences()");

	GetPreferencesRequest request = getNewMobiliserRequest(GetPreferencesRequest.class);

	request.setApplicationName(name);
	request.setPath(path);

	GetPreferencesResponse response = wsPrefsClient.getPreferences(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while getting preferences tree");
	    return null;
	}

	return response.getPreferences();
    }

    public PreferenceBean getPreferenceBean(String name, String path, String key) {

	PreferenceBean result = null;

	try {
	    LOG.debug("Loading preferences node: {} -> {}", name, path);

	    // returns a tree pointing to a list
	    DetailedPreferencesTree prefsTree = this.getDetailedPreferences(
		    name, path);

	    List<DetailedPreference> prefsNodes = prefsTree
		    .getDetailedPreference();

	    // but we still interate through list
	    for (DetailedPreference prefNode : prefsNodes) {

		if (prefNode.getPath().equals(path)) {

		    LOG.debug("Found node: {}", prefNode.getPath());

		    LOG.debug("Looking for key: {}", key);

		    for (DetailedMap.Entry entry : prefNode.getEntry()) {

			String prefKey = entry.getKey();

			if (prefKey.equals(key)) {
			    PreferenceBean bean = new PreferenceBean();
			    bean.setKey(key);
			    bean.setValue(entry.getValue());
			    bean.setType(entry.getType());
			    bean.setDescription(entry.getDescription());

			    result = bean;

			    LOG.debug("Got preference value: {}={}", bean
				    .getKey(), bean.getValue());

			    break;
			}
		    }
		}
	    }
	} catch (Exception e) {
	    LOG.error("Problem finding preference value: {}/{}/{}",
		    new Object[] { name, path, key, e });
	}

	return result;
    }

    public boolean removePreferencesNode(String appName, String path)
	    throws Exception {

	LOG.debug("# MobiliserBasePage.removePreferencesNode()");

	RemovePreferencesNodeRequest request = getNewMobiliserRequest(RemovePreferencesNodeRequest.class);

	request.setApplicationName(appName);
	request.setPath(path);

	RemovePreferencesNodeResponse response = wsPrefsClient
		.removePreferencesNode(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while deleting preferences node");
	    return false;
	}

	return true;
    }

    public boolean removePreferencesValue(String appName, String path,
	    String name) throws Exception {

	LOG.debug("# MobiliserBasePage.removePreferencesValue()");

	RemovePreferencesValueRequest request = getNewMobiliserRequest(RemovePreferencesValueRequest.class);

	request.setApplicationName(appName);
	request.setPath(path);
	request.setName(name);

	RemovePreferencesValueResponse response = wsPrefsClient
		.removePreferencesValue(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while deleting preferences node value");
	    return false;
	}

	return true;
    }

    public boolean setPreferencesValue(String appName, String path,
	    String name, String value, String type, String desc)
	    throws Exception {

	return this.setPreferencesValue(appName, path, name, value, type, desc,
		false, null);
    }

    public boolean setPreferencesValue(final String appName, final String path,
	    final String name, final String value, final String type,
	    final String desc, final boolean encrypt, final String passphrase)
	    throws Exception {

	LOG.debug("# MobiliserBasePage.setPreferencesValue()");

	SetPreferencesValueRequest request = getNewMobiliserRequest(SetPreferencesValueRequest.class);

	request.setApplicationName(appName);
	request.setPath(path);
	request.setName(name);

	if (encrypt) {
	    request.setValue(this.preferencesEncryptionManager.encryptValue(
		    value, "default", passphrase));
	} else {
	    request.setValue(value);
	}

	request.setType(type);
	request.setDescription(desc);

	SetPreferencesValueResponse response = wsPrefsClient
		.setPreferencesValue(request);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.warn("# Error while deleting preferences node value");
	    return false;
	}

	return true;
    }

	public String generateOTP() throws Exception {
		
		String otp = null;
		CreateNonPersistentOtpRequest nonPersistentOtpRequest = getNewMobiliserRequest(CreateNonPersistentOtpRequest.class);
		nonPersistentOtpRequest.setOtpType(Constants.OTP_TYPE);

		CreateNonPersistentOtpResponse nonPersistentOtpResponse = wsOtpClient
				.createNonPersistentOtp(nonPersistentOtpRequest);
		if (!evaluateMobiliserResponse(nonPersistentOtpResponse)) {
			LOG.debug("# Failed to create non persistent OTP");
			return null;
		}

		// TODO - remove this log
		LOG.debug("###### OTP generated is--> {}",  nonPersistentOtpResponse.getOpt());
		otp = nonPersistentOtpResponse.getOpt();
		return otp;

	}
    

    public boolean sendOTP(String msisdn, String templateName,
	    String templateType, Long customerId,
	    List<KeyValue<String, String>> paramsList) throws Exception {
	com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Locale userLocale = null;
	if (PortalUtils.exists(getMobiliserWebSession().getLocale()
		.getLanguage())) {
	    userLocale = new com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Locale();
	    userLocale.setLanguage(getMobiliserWebSession().getLocale()
		    .getLanguage());
	    userLocale.setCountry(getMobiliserWebSession().getLocale()
		    .getCountry());
	    userLocale.setVariant(getMobiliserWebSession().getLocale()
		    .getVariant());
	}

	SendTemplateRequest request = getNewMobiliserRequest(SendTemplateRequest.class);
	TemplateMessage template = new TemplateMessage();
	if (PortalUtils.exists(paramsList)) {
	    Map paramMap = new Map();
	    for (KeyValue<String, String> kvParam : paramsList) {
		Entry entry = new Entry();
		entry.setKey(kvParam.getKey());
		entry.setValue(kvParam.getValue());
		paramMap.getEntry().add(entry);
	    }
	    template.setParameters(paramMap);
	}
	template.setChannel(getConfiguration().getChannelForTestMessage());
	template.setType(templateType);
	Receiver rcv = new Receiver();
	rcv.setValue(msisdn);
	rcv.setReferencedEntity(customerId);
	template.getReceiver().add(rcv);
	MessageDetails detail = new MessageDetails();
	detail.setLocale(userLocale);
	detail.setTemplate(templateName);
	template.setDetails(detail);
	request.setMessage(template);
	SendTemplateResponse response = wsMsgClient.sendTemplate(request);

	// PlainTextMessage plainTextMessage = new PlainTextMessage();
	// plainTextMessage.setChannel("SMS");
	// plainTextMessage.setSender("1234567896");
	// plainTextMessage.setSubject("OTP");
	// plainTextMessage.setText(nonPersistentOtpResponse.getOpt());
	// plainTextMessage.setType(Constants.OTP_MSG_TYPE);
	//
	// Receiver rcv = new Receiver();
	// // rcv.setType("0");
	// rcv.setValue(msisdn);
	//
	// plainTextMessage.getReceiver().add(rcv);
	//
	// SendPlainTextRequest textRequest =
	// getNewMobiliserRequest(SendPlainTextRequest.class);
	// textRequest.setMessage(plainTextMessage);
	//
	// SendPlainTextResponse plainTextResponse = wsMsgClient
	// .sendPlainText(textRequest);

	if (!evaluateMobiliserResponse(response)) {
	    LOG.debug("# Failed to send OTP plain text");
	    return false;
	}
	return true;
    }

    public boolean updateIdentificationByCustomer(Long customerId,
	    int identificationType, Long identificationId,
	    String newIdentification, String provider) throws Exception {
	UpdateIdentificationRequest request;
	request = getNewMobiliserRequest(UpdateIdentificationRequest.class);
	Identification identification = new Identification();
	identification.setActive(true);
	identification.setCustomerId(customerId);
	identification.setType(identificationType);
	identification.setId(identificationId);
	identification.setIdentification(newIdentification);
	identification.setProvider(provider);
	request.setIdentification(identification);
	UpdateIdentificationResponse response = wsIdentClient
		.updateIdentification(request);
	if (!evaluateMobiliserResponse(response)) {
	    return false;
	}
	LOG.debug("#Identification[" + identificationId + ":"
		+ newIdentification + "] has been updated");

	return true;
    }

    public boolean generateTempCredential(Long customerId, int credentialType,
	    String orderChannel) throws Exception {
	GenerateTemporaryCredentialRequest request = getNewMobiliserRequest(GenerateTemporaryCredentialRequest.class);
	request.setCredentialType(credentialType);
	request.setCustomerId(customerId);
	request.setNotificationType(orderChannel);
	GenerateTemporaryCredentialResponse response = wsSystemAuthSecurityClient
		.generateTemporaryCredential(request);
	if (!evaluateMobiliserResponse(response)) {
	    return false;
	}

	return true;
    }

    public boolean logoutCustomer(String sessionId) {

	try {
	    LogoutRequest request = getNewMobiliserRequest(LogoutRequest.class);

	    LogoutResponse response = wsSystemAuthSecurityClient
		    .logout(request);

	    if (!evaluateMobiliserResponse(response)) {
		return false;
	    }
	} catch (Exception e) {
	    LOG.error("# Error while logging out customer", e);
	}

	return true;
    }

    public class DropDownListComparator implements
	    Comparator<KeyValue<String, String>> {
	@Override
	public int compare(KeyValue<String, String> o1,
		KeyValue<String, String> o2) {
	    return o1.getValue().compareTo(o2.getValue());
	}
    }

    public List<KeyValue<Long, String>> getCustomerAddsInvoiceTypes(
	    boolean forceLoad) {
	LOG.debug("# MobiliserBasePage.getCustomerAddsInvoiceTypes()");
	if (forceLoad || INVOICE_TYPE_CUSTOMER_ADDS_LIST == null) {
	    List<InvoiceType> invoiceTypeList = getAllInvoiceTypes();

	    INVOICE_TYPE_CUSTOMER_ADDS_LIST = new ArrayList<KeyValue<Long, String>>();
	    for (InvoiceType it : invoiceTypeList) {
		if (it.getGroupId() == Constants.STANDARD_INV_GRP_TYPE
			&& it.isCustomerAddsInvoiceConfiguration()
			&& it.isCustomerAddsInvoice()) {
		    INVOICE_TYPE_CUSTOMER_ADDS_LIST.add(new KeyValue(
			    it.getId(), it.getName()));
		}
	    }
	}
	return INVOICE_TYPE_CUSTOMER_ADDS_LIST;
    }

    public List<KeyValue<Long, String>> getInvoiceTypesFullList(
	    boolean forceLoad) {
	LOG.debug("# MobiliserBasePage.getCurrencyFromInvoiceTypes()");
	if (forceLoad || INVOICE_TYPE_FULL_LIST == null) {
	    loadInvoiceTypePropertyLists(true);
	}

	Collections.sort(INVOICE_TYPE_FULL_LIST,
		new Comparator<KeyValue<Long, String>>() {
		    @Override
		    public int compare(KeyValue<Long, String> arg0,
			    KeyValue<Long, String> arg1) {
			return arg0.getValue().compareTo(arg1.getValue());
		    }
		});
	return INVOICE_TYPE_FULL_LIST;
    }

    public List<KeyValue<Long, String>> getInvoiceTypesCurrencyList(
	    boolean forceLoad) {
	LOG.debug("# MobiliserBasePage.getCurrencyFromInvoiceTypes()");
	if (forceLoad || INVOICE_TYPE_CURRENCY_LIST == null) {
	    loadInvoiceTypePropertyLists(true);
	}
	return INVOICE_TYPE_CURRENCY_LIST;
    }

    public List<KeyValue<Long, String>> getInvoiceTypesConfigRefPatternList(
	    boolean forceLoad) {
	LOG.debug("# MobiliserBasePage.getInvoiceTypesConfigPatternList()");
	if (forceLoad || INVOICE_TYPE_CONFIG_REF_PATTERN_LIST == null) {
	    loadInvoiceTypePropertyLists(true);
	}
	return INVOICE_TYPE_CONFIG_REF_PATTERN_LIST;
    }

    public List<KeyValue<Long, String>> getInvoiceTypesInvoiceRefPatternList(
	    boolean forceLoad) {
	LOG.debug("# MobiliserBasePage.getInvoiceTypesInvoicePatternList()");
	if (forceLoad || INVOICE_TYPE_INVOICE_REF_PATTERN_LIST == null) {
	    loadInvoiceTypePropertyLists(true);
	}
	return INVOICE_TYPE_INVOICE_REF_PATTERN_LIST;
    }

    private void loadInvoiceTypePropertyLists(boolean forceLoad) {
	LOG.debug("# MobiliserBasePage.getInvoiceTypesPatternList()");
	if (forceLoad) {
	    List<InvoiceType> invoiceTypeList = getAllInvoiceTypes();

	    INVOICE_TYPE_FULL_LIST = new ArrayList<KeyValue<Long, String>>();
	    INVOICE_TYPE_CURRENCY_LIST = new ArrayList<KeyValue<Long, String>>();
	    INVOICE_TYPE_CONFIG_REF_PATTERN_LIST = new ArrayList<KeyValue<Long, String>>();
	    INVOICE_TYPE_INVOICE_REF_PATTERN_LIST = new ArrayList<KeyValue<Long, String>>();

	    for (InvoiceType it : invoiceTypeList) {
		INVOICE_TYPE_FULL_LIST.add(new KeyValue(it.getId(), it
			.getName()));
		INVOICE_TYPE_CONFIG_REF_PATTERN_LIST
			.add(new KeyValue(it.getId(), it
				.getInvoiceConfigurationReferencePattern()));

		INVOICE_TYPE_INVOICE_REF_PATTERN_LIST.add(new KeyValue(it
			.getId(), it.getInvoiceReferencePattern()));

		INVOICE_TYPE_CURRENCY_LIST.add(new KeyValue(it.getId(), it
			.getCurrency()));

	    }
	}
	return;
    }

    public List<InvoiceType> getAllInvoiceTypes() {
	LOG.debug("# MobiliserBasePage.getAllInvoiceTypes()");
	if (!PortalUtils.exists(invoiceTypes)) {

	    GetAllInvoiceTypesResponse response = null;
	    try {
		GetAllInvoiceTypesRequest request = getNewMobiliserRequest(GetAllInvoiceTypesRequest.class);
		request.setOnlyActive(true);
		response = wsInvoiceClient.getAllInvoiceTypes(request);

		if (!evaluateMobiliserResponse(response)) {
		    return null;
		}
		invoiceTypes = response.getInvoiceType();
	    } catch (Exception e) {
		LOG.error("# Error while getting invoice types.", e);
	    }
	}

	if (invoiceTypes != null) {
	    Collections.sort(invoiceTypes, new Comparator<InvoiceType>() {
		@Override
		public int compare(InvoiceType arg0, InvoiceType arg1) {

		    int result = new Integer(arg0.getGroupId())
			    .compareTo(new Integer(arg1.getGroupId()));

		    if (result == 0) {
			result = arg0.getName().compareTo(arg1.getName());
		    }

		    return result;
		}
	    });
	}
	return invoiceTypes;
    }

    public boolean isInLongStringList(Long key,
	    List<KeyValue<Long, String>> aList) {
	if (key == null)
	    return false;
	for (KeyValue<Long, String> keyValue : aList) {
	    if (key.equals(keyValue.getKey())) {
		return true;
	    }
	}
	return false;
    }

    public String getStringFromLongStringList(Long key,
	    List<KeyValue<Long, String>> aList) {
	if (key == null)
	    return null;
	for (KeyValue<Long, String> keyValue : aList) {
	    if (key.equals(keyValue.getKey())) {
		return keyValue.getValue();
	    }
	}
	return null;
    }

    public List<PaymentInstrument> getPaymentInstrumentsByCustomer(
	    Long customerId) {
	LOG.debug("# MobiliserBasePage.getPaymentInstrumentsByCustomer()");
	List<PaymentInstrument> piEntries = null;
	try {
	    GetPaymentInstrumentsByCustomerRequest piRequest = getNewMobiliserRequest(GetPaymentInstrumentsByCustomerRequest.class);
	    piRequest.setCustomerId(customerId);

	    GetPaymentInstrumentsByCustomerResponse piResponse = wsWalletClient
		    .getPaymentInstrumentsByCustomer(piRequest);

	    if (!evaluateMobiliserResponse(piResponse)) {
		LOG.warn("# Error while fetching PI");
		return null;
	    }

	    piEntries = piResponse.getPaymentInstruments();
	    LOG.debug("# Successfully fetched PI");
	} catch (Exception e) {
	    LOG.error("# Error while getting PI.", e);
	}

	return piEntries;
    }

    public Long createInvoiceConfiguration(InvoiceConfiguration ic)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.createInvoiceConfiguration()");

	CreateInvoiceConfigurationRequest icRequest = getNewMobiliserRequest(CreateInvoiceConfigurationRequest.class);
	icRequest.setInvoiceConfiguration(ic);

	CreateInvoiceConfigurationResponse icResponse = wsInvoiceClient
		.createInvoiceConfiguration(icRequest);

	if (!evaluateMobiliserResponse(icResponse)) {
	    LOG.warn("# Error while creating invoice configuration");
	    return null;
	}

	LOG.debug("# Successfully created invoice configuration");

	return icResponse.getInvoiceConfigurationId();

    }

    public boolean updateInvoiceConfiguration(InvoiceConfiguration ic)
	    throws Exception {
	LOG.debug("# MobiliserBasePage.updateInvoiceConfiguration()");

	UpdateInvoiceConfigurationRequest icRequest = getNewMobiliserRequest(UpdateInvoiceConfigurationRequest.class);
	icRequest.setInvoiceConfiguration(ic);

	UpdateInvoiceConfigurationResponse icResponse = wsInvoiceClient
		.updateInvoiceConfiguration(icRequest);

	if (!evaluateMobiliserResponse(icResponse)) {
	    LOG.warn("# Error while updating invoice configuration");
	    return false;
	}

	LOG.debug("# Successfully updated invoice configuration");

	return true;

    }

    public List<InvoiceConfiguration> getInvoiceConfigurationList(
	    Long customerId) {
	LOG.debug("# MobiliserBasePage.getWalletEntryList()");
	List<InvoiceConfiguration> icList = null;
	try {
	    GetInvoiceConfigurationsByCustomerRequest icRequest = getNewMobiliserRequest(GetInvoiceConfigurationsByCustomerRequest.class);
	    icRequest.setCustomerId(customerId);
	    icRequest.setOnlyActive(true);
	    GetInvoiceConfigurationsByCustomerResponse icResponse = wsInvoiceClient
		    .getInvoiceConfigurationsByCustomer(icRequest);

	    if (!evaluateMobiliserResponse(icResponse)) {
		LOG
			.warn(
				"Error while fetching invoice configuration for customerId [{}] ",
				new Object[] { customerId });

		return null;
	    }

	    icList = icResponse.getInvoiceConfiguration();

	} catch (Exception e) {
	    LOG
		    .error(
			    "Error while fetching invoice configuration for customerId [{}] ",
			    new Object[] { customerId });
	}
	return icList;
    }

    public List<InvoiceConfiguration> getInvoiceConfigurationCustomerAllowedList(
	    Long customerId) {
	LOG.debug("# MobiliserBasePage.getWalletEntryList()");
	List<InvoiceConfiguration> icList = getInvoiceConfigurationList(customerId);
	List<KeyValue<Long, String>> itList = getCustomerAddsInvoiceTypes(false);

	List<InvoiceConfiguration> icCustomerAllowedList = new ArrayList<InvoiceConfiguration>();
	for (InvoiceConfiguration ic : icList) {
	    if (isInLongStringList(ic.getInvoiceTypeId(), itList)) {
		icCustomerAllowedList.add(ic);
	    }
	}
	return icCustomerAllowedList;
    }

    public boolean deleteInvoiceConfiguration(Long icId) throws Exception {
	LOG.debug("# MobiliserBasePage.deleteInvoiceConfiguration()");

	DeleteInvoiceConfigurationRequest request = getNewMobiliserRequest(DeleteInvoiceConfigurationRequest.class);
	request.setInvoiceConfigurationId(icId);
	DeleteInvoiceConfigurationResponse response = wsInvoiceClient
		.deleteInvoiceConfiguration(request);
	if (!evaluateMobiliserResponse(response)) {
	    LOG
		    .warn("# An error occurred while deleting from invoice configuration");
	    return false;
	}
	LOG.debug("# Successfully deleted invoice configuration");
	return true;
    }

    public List<Invoice> getInvoiceList(Long customerId, Integer status) {
	LOG.debug("# MobiliserBasePage.getInvoiceList()");
	List<Invoice> invoiceList = null;
	try {
	    GetInvoicesByCustomerRequest invoiceRequest = getNewMobiliserRequest(GetInvoicesByCustomerRequest.class);
	    invoiceRequest.setCustomerId(customerId);

	    invoiceRequest.setInvoiceStatus(status);
	    // invoiceRequest.setInvoiceStatus(Constants.INVOICE_STATUS_OPEN);

	    GetInvoicesByCustomerResponse invoiceResponse = wsInvoiceClient
		    .getInvoicesByCustomer(invoiceRequest);

	    if (!evaluateMobiliserResponse(invoiceResponse)) {
		LOG
			.warn(
				"Error while fetching invoice configuration for customerId [{}] ",
				new Object[] { customerId });

		return null;
	    }

	    invoiceList = invoiceResponse.getInvoice();
	} catch (Exception e) {
	    LOG
		    .error(
			    "Error while fetching invoice configuration for customerId [{}] ",
			    new Object[] { customerId });
	}
	return invoiceList;
    }

    public com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Locale convertLocale(
	    String locale) {
	com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Locale msgLocale = null;
	if (PortalUtils.exists(locale)) {
	    String[] result = locale.split("_");
	    if (PortalUtils.exists(result)) {
		msgLocale = new com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Locale();
		if (result.length >= 1 && PortalUtils.exists(result[0]))
		    msgLocale.setLanguage(result[0]);
		if (result.length >= 2 && PortalUtils.exists(result[1]))
		    msgLocale.setCountry(result[1]);
		if (result.length >= 3 && PortalUtils.exists(result[2]))
		    msgLocale.setVariant(result[2]);
	    }
	}
	return msgLocale;

    }

    public String convertLocale(
	    com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.Locale locale) {
	StringBuilder localeBuilder = new StringBuilder();
	if (locale != null) {
	    localeBuilder.append(locale.getLanguage());
	    if (PortalUtils.exists(locale.getCountry())) {
		localeBuilder.append('_');
		localeBuilder.append(locale.getCountry());

	    }
	    if (PortalUtils.exists(locale.getVariant())) {
		localeBuilder.append('_');
		localeBuilder.append(locale.getVariant());

	    }
	}
	return localeBuilder.toString();
    }

    public void doMessageExport(final List<MessageTemplate> messageTemplates) {
	getRequestCycle().setRequestTarget(new IRequestTarget() {

	    @Override
	    public void respond(RequestCycle requestCycle) {
		try {
		    final MessageTemplates templateList = new MessageTemplates();
		    List<MessageAttachment> msgAttachments = null;
		    for (MessageTemplate message : messageTemplates) {
			try {
			    GetDetailedTemplateRequest request = getNewMobiliserRequest(GetDetailedTemplateRequest.class);
			    request.setTemplateId(message.getId());
			    GetDetailedTemplateResponse response = wsTemplateClient
				    .getDetailedTemplate(request);
			    if (response.getStatus().getCode() == 0) {
				msgAttachments = response.getTemplate()
					.getAttachments();
			    }
			} catch (Exception e) {
			    LOG
				    .debug("Error in getting detailed template. Message "
					    + message.getId()
					    + " will be exported without attachment "
					    + e);
			}
			templateList.getTemplates().add(
				Converter.getInstance().convertMessage(message,
					convertLocale(message.getLocale()),
					msgAttachments));
		    }
		    ByteArrayOutputStream outStream = getMessageOutputStream(templateList);
		    WebResponse webResponse = (WebResponse) getResponse();
		    webResponse.setAttachmentHeader("export_message.xml");
		    webResponse.setContentType("text/xml");
		    InputStream inStream = new ByteArrayInputStream(outStream
			    .toByteArray());
		    Streams.copy(inStream, webResponse.getOutputStream());
		} catch (JAXBException e) {
		    LOG.error("Error in Export Message ", e);
		    error(getLocalizer().getString("export.message.error",
			    MobiliserBasePage.this));
		} catch (Exception e) {
		    LOG.error("Error in Export Message ", e);
		    error(getLocalizer().getString("export.message.error",
			    MobiliserBasePage.this));
		}
	    }

	    @Override
	    public void detach(RequestCycle arg0) {
		// nothing to do here
	    }
	});

    }

    private ByteArrayOutputStream getMessageOutputStream(
	    MessageTemplates templateList) throws JAXBException, IOException {
	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	JAXBContext jc = JAXBContext.newInstance(templateList.getClass()
		.getPackage().getName());
	Marshaller m = jc.createMarshaller();
	m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	m.marshal(templateList, outStream);
	outStream.close();
	return outStream;
    }

    public Customer getCustomerDetails(Long customerId) throws Exception {

	GetCustomerRequest customerRequest = getNewMobiliserRequest(GetCustomerRequest.class);
	customerRequest.setCustomerId(customerId);
	GetCustomerResponse response = wsCustomerClient
		.getCustomer(customerRequest);

	if (!evaluateMobiliserResponse(response))
	    return null;

	Customer customer = response.getCustomer();

	return customer;
    }

    public Long createInvoice(long opertorId, long payeeId, String reference,
	    long txnAmount) {
	CreateInvoiceForInvoiceTypeResponse response = null;
	try {
	    CreateInvoiceForInvoiceTypeRequest request = getNewMobiliserRequest(CreateInvoiceForInvoiceTypeRequest.class);
	    request.setInvoiceTypeId(opertorId);
	    request.setCustomerId(payeeId);
	    request.setReference(reference);
	    request.setAmount(txnAmount);
	    response = wsInvoiceClient.createInvoiceForInvoiceType(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# Error occurred while creating invoice for airtime topup");
		return null;
	    }
	    LOG.info("# Successfully created invoice for airtime topup");
	    return response.getInvoiceId();

	} catch (Exception e) {
	    LOG
		    .error("# Error occurred while creating invoice for airtime topup");
	    error(getLocalizer().getString("airtimeTopup.create.invoice.error",
		    this));
	    return null;
	}
    }

    public Invoice getInvoice(long invoiceId) {

	GetInvoiceResponse response = null;
	try {
	    GetInvoiceRequest request = getNewMobiliserRequest(GetInvoiceRequest.class);
	    request.setInvoiceId(invoiceId);
	    response = wsInvoiceClient.getInvoice(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG.warn("# Error occurred while getting invoice [{}] ",
			new Object[] { invoiceId });
		return null;
	    }
	    LOG.info("# Successfully got invoice");
	    return response.getInvoice();

	} catch (Exception e) {
	    LOG.error("# Error occurred while while getting invoice");
	    return null;
	}
    }

    public boolean checkPayInvoice(long invoiceId, TransactionBean tab)
	    throws Exception {
	CheckPayInvoiceResponse response;

	CheckPayInvoice invoice = getNewMobiliserRequest(CheckPayInvoice.class);
	invoice.setInvoiceId(invoiceId);
	response = wsCheckPayInvoiceClient.checkInvoice(invoice);
	if (!evaluateMobiliserResponse(response)) {
	    return Boolean.FALSE;
	}
	// Calculate fees and amounts
	long payeeFee = 0;
	long payerFee = 0;
	for (MoneyFeeType mft : response.getMoneyFee()) {
	    if (mft.isPayee()) {
		payeeFee += mft.getValue();
		payeeFee += mft.getVat();
	    } else {
		// isPayer
		payerFee += mft.getValue();
		payerFee += mft.getVat();
	    }
	}
	tab.setPayeeFee(payeeFee);
	tab.setPayerFee(payerFee);
	tab.setFeeAmount(payerFee + payeeFee);
	tab
		.setDebitAmount(payerFee
			+ ((tab.getAmount() == null) ? 0L : tab.getAmount()
				.getValue()));
	tab.setCreditAmount(((tab.getAmount() == null) ? 0L : tab.getAmount()
		.getValue())
		- payeeFee);
	tab.setRefTransaction(response.getTransaction());
	tab.setStatusString(response.getStatus().getValue());
	return Boolean.TRUE;
    }

    public boolean continuePayInvoice(TransactionBean txnBean) throws Exception {
	ContinuePayInvoiceResponse response;
	ContinuePayInvoice request = getNewMobiliserRequest(ContinuePayInvoice.class);
	request.setReferenceTransaction(txnBean.getRefTransaction());
	response = wsContinuePayInvoiceClient.continueInvoice(request);
	txnBean.setTxnId(response.getTransaction().getSystemId());
	txnBean.setAuthCode("");
	txnBean.setStatusCode(response.getStatus().getCode());
	txnBean.setStatusString(response.getStatus().getValue());
	if (response.getStatus().getCode() == Constants.PENDING_APPROVAL_ERROR) {
	    LOG
		    .warn("#  Your transaction request has been processed successfully, but is pending approval");
	    txnBean.setStatusCode(Constants.TXN_STATUS_PENDING_APPROVAL);
	    return false;
	}
	if (!evaluateMobiliserResponse(response)) {
	    return Boolean.FALSE;
	}

	return Boolean.TRUE;
    }

    public boolean cancelPayInvoice(long invoiceId) throws Exception {
	LOG.debug("# MobiliserBasePage.cancelPayInvoice()");

	CancelInvoiceRequest cancelRequest = getNewMobiliserRequest(CancelInvoiceRequest.class);
	cancelRequest.setInvoiceId(invoiceId);
	cancelRequest.setCancelByCustomer(true);
	CancelInvoiceResponse cancelResponse = wsCancelInvoiceClient
		.cancelInvoice(cancelRequest);
	if (!evaluateMobiliserResponse(cancelResponse)) {
	    LOG.warn("# An error occurred while canceling invoice");
	    return false;
	}

	LOG.debug("# Successfully canceled invoice");
	return true;
    }

    public List<KeyValue<Long, String>> getInvoiceTypesList() {
	LOG.debug("# MobiliserBasePage.getInvoiceTypesList()");
	List<InvoiceType> invoiceTypes = getInvoiceTypes();
	if (PortalUtils.exists(invoiceTypes))
	    INVOICE_TYPE_OPERATOR_LIST = new ArrayList<KeyValue<Long, String>>(
		    invoiceTypes.size());
	for (InvoiceType invoiceType : invoiceTypes) {
	    INVOICE_TYPE_OPERATOR_LIST.add(new KeyValue<Long, String>(
		    invoiceType.getId(), invoiceType.getName()));
	}

	return INVOICE_TYPE_OPERATOR_LIST;
    }

    private List<InvoiceType> getInvoiceTypes() {
	GetInvoiceTypesByGroupResponse response = null;
	try {
	    GetInvoiceTypesByGroupRequest request = getNewMobiliserRequest(GetInvoiceTypesByGroupRequest.class);
	    request.setOnlyActive(Boolean.TRUE);
	    request.setGroupId(Constants.TOPUP_INV_GRP_TYPE);
	    response = wsInvoiceClient.getInvoiceTypesByGroup(request);
	    return response.getInvoiceType();

	} catch (Exception e) {
	    LOG
		    .error(
			    "# Error while getting invoice types by group for airtime topup",
			    e);
	    return null;
	}
    }

    public List<RestrictionSetBean> getRestrictionSetBeanList()
	    throws DataProviderLoadException {
	LOG.debug("# MobiliserBasePage.getRestrictionSetBeanList()");
	List<RestrictionsGroup> restGrpList = new ArrayList<RestrictionsGroup>();
	List<RestrictionSetBean> restBeanList = new ArrayList<RestrictionSetBean>();
	RestrictionSetBean restSetBean;
	GetRestrictionsGroupsResponse response = null;
	try {
	    GetRestrictionsGroupsRequest request = getNewMobiliserRequest(GetRestrictionsGroupsRequest.class);
	    response = wsTxnRestrictionClient.getRestrictionsGroups(request);
	    if (!evaluateMobiliserResponse(response)) {
		return null;
	    }
	    restGrpList = response.getRestrictionGroup();
	} catch (Exception e) {
	    throw new DataProviderLoadException();
	}
	int iCount;
	for (RestrictionsGroup restGrp : restGrpList) {
	    restSetBean = new RestrictionSetBean();
	    restSetBean.setRestrictionSet(restGrp);
	    restSetBean.setCssStyle(Constants.CSS_STYLE_FEE_TYPE);
	    restBeanList.add(restSetBean);
	    iCount = 1;

	    for (RestrictionInfo restriction : restGrp.getRestriction()) {
		restSetBean = new RestrictionSetBean();
		restSetBean.setRestrictionSet(restGrp);
		restSetBean.setRestriction(restriction);
		if (iCount % 2 == 1)
		    restSetBean.setCssStyle(Constants.CSS_STYLE_ODD);
		else
		    restSetBean.setCssStyle(Constants.CSS_STYLE_EVEN);

		if (iCount == 1)
		    restSetBean.setShowMoveUpLink(false);
		if (iCount == restGrp.getRestriction().size())
		    restSetBean.setShowMoveDownLink(false);

		restBeanList.add(restSetBean);
		iCount++;
	    }

	}

	return restBeanList;
    }

    @Deprecated
    public int createCustomerBlackoutSchedule(
	    CustomerBlackoutSchedule customerBlackoutSchedule, long alertId) {
	int status = -1;
	try {

	    CreateCustomerBlackoutScheduleRequest request = getNewMobiliserRequest(CreateCustomerBlackoutScheduleRequest.class);
	    request.setBlackoutSchedule(customerBlackoutSchedule);
	    CreateCustomerBlackoutScheduleResponse createCustomerBlackoutScheduleResponse = wsCustomerAlertsClient
		    .createCustomerBlackoutSchedule(request);

	    if (!evaluateMobiliserResponse(createCustomerBlackoutScheduleResponse)) {
		LOG.warn("# An error occurred while adding alert blackOut");
		return -1;
	    }
	    status = createCustomerBlackoutScheduleResponse.getStatus()
		    .getCode();
	    if (status == 0) {
		AddBlackoutToCustomerAlertRequest addBlackoutToCustomerAlertRequest = getNewMobiliserRequest(AddBlackoutToCustomerAlertRequest.class);
		addBlackoutToCustomerAlertRequest
			.setBlackoutScheduleId(createCustomerBlackoutScheduleResponse
				.getBlackoutScheduleId());
		addBlackoutToCustomerAlertRequest.setCustomerAlertId(alertId);

		AddBlackoutToCustomerAlertResponse addBlackoutToCustomerAlertResponse = wsCustomerAlertsClient
			.addBlackoutToCustomerAlert(addBlackoutToCustomerAlertRequest);

		if (!evaluateMobiliserResponse(createCustomerBlackoutScheduleResponse)) {
		    LOG.warn("# An error occurred while adding alert blackOut");
		    return -1;
		}
		status = addBlackoutToCustomerAlertResponse.getStatus()
			.getCode();
	    }
	} catch (Exception e) {
	    LOG.error("# An error occurred while adding alert blackOut", e);
	    return -1;

	}
	return status;
    }

    @Deprecated
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

    @Deprecated
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
	} catch (Exception e) {
	    LOG
		    .error(
			    "# An error occurred while creating  Customer OtherIdentification",
			    e);

	}
	return status;
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public int updateCustomerAlert(CustomerAlert customerAlert) {
	try {
	    int status = -1;
	    UpdateCustomerAlertRequest request = getNewMobiliserRequest(UpdateCustomerAlertRequest.class);
	    request.setCustomerAlert(customerAlert);
	    UpdateCustomerAlertResponse updateCustomerAlertResponse = wsCustomerAlertsClient
		    .updateCustomerAlert(request);

	    status = updateCustomerAlertResponse.getStatus().getCode();
	    if (!evaluateMobiliserResponse(updateCustomerAlertResponse)) {
		LOG.warn("# An error occurred while updating customer alert");
		return -1;
	    }

	    return status;

	} catch (Exception e) {
	    LOG.error("Error occured when updating  a the customer Alert ", e);
	    return -1;
	}
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    public List<KeyValue<String, String>> getOrgUnits() {
	List<OrgUnit> orgUnits = null;
	List<KeyValue<String, String>> orgUnitsList = new ArrayList<KeyValue<String, String>>();
	try {
	    orgUnits = getOrgUnitConfigurationsList();
	    KeyValue<String, String> keyValue;
	    for (OrgUnit orgUnit : orgUnits) {
		keyValue = new KeyValue<String, String>(orgUnit.getId(),
			orgUnit.getName());
		orgUnitsList.add(keyValue);
	    }
	} catch (DataProviderLoadException dpe) {
	    LOG.error("#An error occurred while fetching org units");
	}

	return orgUnitsList;

    }

    public String getDisplayValue(String key, String bundleName) {

	if (PortalUtils.exists(key))
	    try {
		return getLocalizer().getString(
			LookupResourceLoader.LOOKUP_INDICATOR + bundleName
				+ "." + key, this);
	    } catch (MissingResourceException me) {
		return "";
	    }
	return "";
    }

    public boolean validateAmount(String amount, Locale locale, String regex) {
	try {
	    String tempAmount = amount;
	    DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
	    char groupingSeparator = dfs.getGroupingSeparator();
	    char decimalSeparator = dfs.getDecimalSeparator();
	    // index of decimal separator
	    int index = amount.indexOf(decimalSeparator);
	    int indexGroupSeparator = amount.indexOf(groupingSeparator);
	    // remove all grouping separator appearing before decimal separator
	    // This is done so that we can validate the length of input <= 18
	    if (Character.isSpaceChar(groupingSeparator)) {
		amount = amount.replace(" ", String.valueOf(groupingSeparator));
	    }
	    // The group separator will be removed only when it is not next to
	    // a decimal point. Else an invalid entry error will be thrown
	    if (!(index > 0 && indexGroupSeparator > 0 && (index
		    - indexGroupSeparator == 1 || indexGroupSeparator - index == 1))) {
		tempAmount = amount.substring(0,
			index > 0 ? index : amount.length()).replace(
			String.valueOf(groupingSeparator), "");
		if (index > 0) {
		    tempAmount += amount.substring(index);
		    tempAmount = tempAmount.replace(decimalSeparator, '.');
		}
	    }
	    Matcher m = Pattern.compile(regex).matcher(tempAmount);
	    return m.matches();
	} catch (Exception e) {
	    LOG.debug("Error in validating amount: ", e);
	}
	return false;
    }

    public <K> List<K> getKeysFromPreferences(String prefsKey, Class<K> keyClass)
	    throws Exception {
	List<K> keyList = null;
	String keys = getConfiguration().getLookupKeys(prefsKey);
	if (PortalUtils.exists(keys)) {
	    String[] strKeys = keys.split(",");
	    if (PortalUtils.exists(strKeys)) {
		keyList = new ArrayList<K>();
		for (String key : strKeys) {
		    keyList.add(keyClass.getConstructor(String.class)
			    .newInstance(key));
		}
	    }
	}

	return keyList;
    }

    public Long createWallet(String alias, PaymentInstrument pi, Long customerId)
	    throws Exception {
	WalletEntry we = new WalletEntry();
	we.setAlias(alias);
	we.setCreditPriority(null);
	we.setDebitPriority(null);
	we.setLimitSetId(null);
	we.setCustomerId(customerId);
	if (pi instanceof CreditCard)
	    we.setCreditCard((CreditCard) pi);
	else if (pi instanceof BankAccount)
	    we.setBankAccount((BankAccount) pi);
	else
	    we.setExternalAccount((ExternalAccount) pi);

	CreateWalletEntryRequest crWalletReq = getNewMobiliserRequest(CreateWalletEntryRequest.class);

	crWalletReq.setPrimaryCredit(null);
	crWalletReq.setPrimaryDebit(null);
	crWalletReq.setWalletEntry(we);
	CreateWalletEntryResponse crWalletResp = wsWalletClient
		.createWalletEntry(crWalletReq);

	if (crWalletResp.getStatus().getCode() == Constants.PENDING_APPROVAL_ERROR) {
	    return -1L;
	} else if (!evaluateMobiliserResponse(crWalletResp))
	    return null;
	return crWalletResp.getWalletEntryId();
    }

    public String createDisplayName(String firstName, String lastName) {
	String displayName = "";

	if (PortalUtils.exists(firstName) && PortalUtils.exists(lastName)) {

	    if (firstName.length() <= 39 && lastName.length() <= 40) {
		displayName = firstName + " " + lastName;
	    } else if (firstName.length() > 39 && lastName.length() > 40) {
		displayName = firstName.substring(0, 39) + " "
			+ lastName.substring(0, 40);
	    } else if (firstName.length() <= 39 && lastName.length() > 40) {
		displayName = firstName
			+ " "
			+ lastName.substring(0, Math.min(40 + (39 - firstName
				.length()), lastName.length()));
	    } else if (firstName.length() > 39 && lastName.length() <= 40) {
		displayName = firstName.substring(0, Math.min(
			39 + (40 - lastName.length()), firstName.length()))
			+ " " + lastName;

	    }

	}
	return displayName;
    }

    public void approve(WalletEntry walletEntry, boolean bApprove) {
	LOG.debug("# MobiliserBasePage.approve(...)");
	ContinuePendingWalletEntryResponse response;
	try {
	    ContinuePendingWalletEntryRequest request = getNewMobiliserRequest(ContinuePendingWalletEntryRequest.class);
	    request.setApprove(bApprove);
	    request.setTaskId(((PendingWalletEntry) walletEntry).getTaskId());
	    response = wsWalletClient.continuePendingWalletEntry(request);
	    if (!evaluateMobiliserResponse(response)) {
		LOG
			.warn("# An error occurred during approval/rejection process");
		return;
	    }
	    if (bApprove)
		getMobiliserWebSession().info(
			getLocalizer().getString("wallet.approved.successfull",
				this));
	    else
		getMobiliserWebSession().info(
			getLocalizer().getString("wallet.rejected.successfull",
				this));

	    setResponsePage(FindPendingWalletPage.class);

	} catch (Exception e) {
	    LOG.error("# An error occurred while approving/rejecting wallet");
	    if (bApprove)
		error(getLocalizer().getString("wallet.approve.error", this));
	    else
		error(getLocalizer().getString("wallet.reject.error", this));

	}
    }

    public List<AlertContactPointBean> getAllContactPointsList(
	    final long customerId) {

	List<AlertContactPointBean> result = new ArrayList<AlertContactPointBean>();

	List<Identification> identList = getPrimaryContactPointsList(customerId);

	for (Identification ident : identList) {
	    AlertContactPointBean cpBean = new AlertContactPointBean();
	    cpBean.setPrimaryIdentification(ident);
	    result.add(cpBean);
	}

	List<CustomerOtherIdentification> otherIdentList = findCustomerOtherIdentification(customerId);

	for (CustomerOtherIdentification otherIdent : otherIdentList) {
	    if (otherIdent.getType() == Constants.IDENT_TYPE_MSISDN
		    || otherIdent.getType() == Constants.IDENT_TYPE_EMAIL) {
		AlertContactPointBean cpBean = new AlertContactPointBean();
		cpBean.setOtherIdentification(otherIdent);
		result.add(cpBean);
	    }
	}

	return result;
    }

    public List<Identification> getPrimaryContactPointsList(
	    final long customerId) {

	List<Identification> identList = new ArrayList<Identification>();

	try {
	    Identification msisdnIdent = getCustomerIdentification(customerId,
		    Constants.IDENT_TYPE_MSISDN);
	    if (PortalUtils.exists(msisdnIdent)) {
		identList.add(msisdnIdent);
	    }
	} catch (Exception e) {
	    LOG.error("Problem loading msisdn identification for customer {}",
		    customerId);
	}

	try {
	    Identification emailIdent = getCustomerIdentification(customerId,
		    Constants.IDENT_TYPE_EMAIL);
	    if (PortalUtils.exists(emailIdent)) {
		identList.add(emailIdent);
	    }
	} catch (Exception e) {
	    LOG.error("Problem loading email identification for customer {}",
		    customerId);
	}

	return identList;
    }

    /*
     * --------------------------------------------------------------------------
     * -------------------------------------------------------- Start of
     * application customisation/factory methods
     */

    // private access to restrict access to only getXYZ() methods in this class
    @SpringBean(name = "customComponentConfiguration")
    private java.util.Map<String, String> customComponentConfiguration;

    /**
     * Return a registered custom version of a base Wicket Component class
     * 
     * @param c
     *            The original Component class
     * @return If a custom class is registered in the custom components map for
     *         the original class then the custom class is returned, otherwise
     *         the original class is returned.
     */
    public Class getComponent(Class<? extends Component> c, Object... args) {
	if (customComponentConfiguration.containsKey(c.getCanonicalName())) {
	    if (LOG.isTraceEnabled()) {
		LOG.trace("Custom component of '{}' configured for '{}'",
			customComponentConfiguration.get(c.getCanonicalName()),
			c.getCanonicalName());
	    }
	    try {
		return Class.forName(customComponentConfiguration.get(c
			.getCanonicalName()));
	    } catch (ClassNotFoundException ex) {
		LOG
			.error(
				"Custom component of '{}' was not found [ClassNotFoundException] using original instead '{}'",
				customComponentConfiguration.get(c
					.getCanonicalName()), c
					.getCanonicalName());
	    }
	} else {
	    LOG.debug("No custom class configured for '{}'", c
		    .getCanonicalName());
	}
	return c;
    }

    /**
     * Return an instance of a registered custom version of a base Wicket
     * Component class.
     * <p>
     * Note: this method uses reflection on the class to match the types of the
     * variable arguments to a known constructor. It is not possible to specify
     * a null value in any variable argument as the type will not be derivable
     * from it. Therefore, only use this method to create instances through
     * constructors whose arguments are known to be non-null.
     * 
     * @param c
     *            The original Component class
     * @param args
     *            The variable arguments to be passed to the matching
     *            constructor
     * @return If a custom class is registered in the custom components map for
     *         the original class and there is a matching constructor with the
     *         same argument types, then a new instance of the custom class is
     *         returned, otherwise a new instance of the original class is
     *         returned.
     */
    public Component getComponentInstance(Class<? extends Component> c,
	    Object... args) {
	Component result = null;
	// get the configured instance of this component
	c = this.getComponent(c);
	try {
	    // if no args constructor is wanted, then we can just serve up
	    // instance using newInstance()
	    if (args.length == 0) {
		result = c.newInstance();
		LOG
			.debug(
				"Returning new instance of '{}' from zero-argument constructor",
				c.getCanonicalName());
	    } else {
		LOG.debug("Looking for typed constructor in {}: ", c
			.getCanonicalName());
		// otherwise we have to find the constructor matching the
		// variable arguments passed
		Class[] argsClass = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
		    if (args[i] == null) {
			// best we can do if we have a null argument is to guess
			// at an Object class
			argsClass[i] = new Object().getClass();
		    } else {
			argsClass[i] = args[i].getClass();
		    }
		    LOG.trace("Argument[{}]: {}", i, argsClass[i]);
		}
		Constructor ctor = getCompatibleConstructor(c, argsClass);
		// once constructor is found, get a new instance using the
		// specified variable args
		result = (Component) ctor.newInstance(args);
		LOG.debug(
			"Returning new instance of '{}' from constructor: {}",
			c.getCanonicalName(), ctor);
	    }
	} catch (SecurityException ex) {
	    LOG
		    .error(
			    "Couldn't get new instance of class '{}' due to SecurityException: {}",
			    c.getCanonicalName(), ex);
	} catch (InstantiationException ex) {
	    LOG
		    .error(
			    "Couldn't get new instance of class '{}' due to InstantiationException: {}",
			    c.getCanonicalName(), ex);
	} catch (IllegalAccessException ex) {
	    LOG
		    .error(
			    "Couldn't get new instance of class '{}' due to IllegalAccessException: {}",
			    c.getCanonicalName(), ex);
	} catch (InvocationTargetException ex) {
	    LOG
		    .error(
			    "Couldn't get new instance of class '{}' due to InvocationTargetException: {}",
			    c.getCanonicalName(), ex);
	}
	return result;
    }

    /**
     * Convenience method specifically for returning Panel customisation Class
     * objects
     * 
     * @see MobiliserBasePage#getComponent(java.lang.Class<? extends Component>)
     */
    public Class getPanel(Class<? extends Panel> c) {
	return getComponent(c);
    }

    /**
     * Convenience method specifically for returning Panel customisation object
     * instances
     * 
     * @see MobiliserBasePage#getComponentInstance(java.lang.Class<? extends
     *      Component>, Object... args)
     */
    public Panel getPanelInstance(Class<? extends Panel> c, Object... args) {
	return (Panel) getComponentInstance(c, args);
    }

    /**
     * Convenience method specifically for returning Page customisations
     * 
     * @see MobiliserBasePage#getComponent(java.lang.Class<? extends Component>)
     */
    public Class getPage(Class<? extends Page> c) {
	return getComponent(c);
    }

    /**
     * Convenience method specifically for returning Page customisation object
     * instances
     * 
     * @see MobiliserBasePage#getComponentInstance(java.lang.Class<? extends
     *      Component>, Object... args)
     */
    public Page getPageInstance(Class<? extends Page> c, Object... args) {
	return (Page) getComponentInstance(c, args);
    }

    /**
     * Get a compatible constructor to the supplied parameter types.
     * 
     * @param c
     *            the class which we want to construct
     * @param parameterTypes
     *            the types required of the constructor
     * 
     * @return a compatible constructor or null if none exists
     */
    public static Constructor<?> getCompatibleConstructor(Class<?> c,
	    Class<?>[] parameterTypes) {
	Constructor<?>[] constructors = c.getConstructors();
	for (int i = 0; i < constructors.length; i++) {
	    if (constructors[i].getParameterTypes().length == (parameterTypes != null ? parameterTypes.length
		    : 0)) {
		Class<?>[] constructorTypes = constructors[i]
			.getParameterTypes();
		boolean isCompatible = true;
		for (int j = 0; j < (parameterTypes != null ? parameterTypes.length
			: 0); j++) {
		    if (!constructorTypes[j]
			    .isAssignableFrom(parameterTypes[j])
			    || (constructorTypes[j].isPrimitive() && !isAssignablePrimitiveToBoxed(
				    constructorTypes[j], parameterTypes[j]))) {
			isCompatible = false;
			break;
		    }
		}
		if (isCompatible) {
		    return constructors[i];
		}
	    }
	}
	LOG
		.trace("No suitable constructors found on: {}", c
			.getCanonicalName());
	if (c != java.lang.Object.class) {
	    return getCompatibleConstructor(c.getSuperclass(), parameterTypes);
	}
	return null;
    }

    /**
     * Checks if a primitive type is assignable with a boxed type.
     * 
     * @param p
     *            A primitive class type
     * @param b
     *            A boxed class type
     * 
     * @return true if p and b are assignment compatible
     */
    private static boolean isAssignablePrimitiveToBoxed(Class<?> p, Class<?> b) {
	if (p.equals(java.lang.Boolean.TYPE)) {
	    if (b.equals(java.lang.Boolean.class))
		return true;
	    else
		return false;
	}
	if (p.equals(java.lang.Byte.TYPE)) {
	    if (b.equals(java.lang.Byte.class))
		return true;
	    else
		return false;
	}
	if (p.equals(java.lang.Character.TYPE)) {
	    if (b.equals(java.lang.Character.class))
		return true;
	    else
		return false;
	}
	if (p.equals(java.lang.Double.TYPE)) {
	    if (b.equals(java.lang.Double.class))
		return true;
	    else
		return false;
	}
	if (p.equals(java.lang.Float.TYPE)) {
	    if (b.equals(java.lang.Float.class))
		return true;
	    else
		return false;
	}
	if (p.equals(java.lang.Integer.TYPE)) {
	    if (b.equals(java.lang.Integer.class))
		return true;
	    else
		return false;
	}
	if (p.equals(java.lang.Long.TYPE)) {
	    if (b.equals(java.lang.Long.class))
		return true;
	    else
		return false;
	}
	if (p.equals(java.lang.Short.TYPE)) {
	    if (b.equals(java.lang.Short.class))
		return true;
	    else
		return false;
	}
	return false;
    }

    /**
     * Simple helper that looks for a specific class dynamically from the
     * specified name by adding prefix 'Edit' and suffix 'Page'. The package
     * name is taken from the package name of the passed in page.
     * 
     * @param pageClass
     *            Supplies the base class from which the package will be used to
     *            lookup the dynamic class name
     * @param name
     *            The dynamic name part of the class
     * 
     * @throws ClassNotFoundException
     *             if nothing can be loaded for the dynamically named class
     */
    // TODO - add some fallback processing for package name flexibility
    @SuppressWarnings("rawtypes")
    public Class getEditPageClass(Class pageClass, String name)
	    throws ClassNotFoundException {
	return Class.forName(pageClass.getPackage().getName() + ".Edit" + name
		+ "Page");
    }

    /**
     * Simple helper that looks for a specific class dynamically from the
     * specified name by adding prefix 'Edit' and suffix 'Page'. The package
     * name is taken from the package name of the passed in page.
     * 
     * @param pageClass
     *            Supplies the base class from which the package will be used to
     *            lookup the dynamic class name
     * @param name
     *            The dynamic name part of the class
     * 
     * @throws ClassNotFoundException
     *             if nothing can be loaded for the dynamically named class
     */
    // TODO - add some fallback processing for package name flexibility
    @SuppressWarnings("rawtypes")
    public Class getAddPageClass(Class pageClass, String name)
	    throws ClassNotFoundException {
	return Class.forName(pageClass.getPackage().getName() + ".Add" + name
		+ "Page");
    }

    public List<UmgrRole> getAvailRolesList() {
	LOG.debug("# MobiliserBasePage.getAvailRolesList()");
	try {
	    GetUmgrRolesRequest umgrReq = getNewMobiliserRequest(GetUmgrRolesRequest.class);
	    GetUmgrRolesResponse umgrResp = wsRolePrivilegeClient
		    .getUmgrRoles(umgrReq);

	    if (!evaluateMobiliserResponse(umgrResp)) {
		LOG.warn("#An error occurred while getting available roles");
		return null;
	    }
	    roleList = umgrResp.getRoles();
	    return roleList;
	} catch (Exception e) {
	    LOG.error("# An error occurred while getting available roles.", e);
	    return null;
	}
    }

    public List<UmgrPrivilege> getAvailPrivilegesList() {
	LOG.debug("# MobiliserBasePage.getAvailPrivilegesList()");
	try {
	    GetUmgrPrivilegesRequest umgrReq = getNewMobiliserRequest(GetUmgrPrivilegesRequest.class);
	    GetUmgrPrivilegesResponse umgrResp = wsRolePrivilegeClient
		    .getUmgrPrivileges(umgrReq);

	    if (!evaluateMobiliserResponse(umgrResp)) {
		LOG
			.warn("#An error occurred while getting available privileges");
		return null;
	    }
	    privilegeList = umgrResp.getPrivileges();
	    return privilegeList;
	} catch (Exception e) {
	    LOG.error(
		    "# An error occurred while getting available privileges.",
		    e);
	    return null;
	}
    }

    public String getRoleDescription(String roleId) {
	LOG.debug("# MobiliserBasePage.getRoleDescription(...)");
	if (!PortalUtils.exists(this.roleList)) {
	    getAvailRolesList();
	}
	Iterator<UmgrRole> itr = this.roleList.iterator();
	UmgrRole role;
	for (int i = 0; itr.hasNext(); i++) {
	    role = itr.next();
	    if (role.getRole().equals(roleId))
		return role.getDescription();

	}
	return "";
    }

    public String getPrivilegeDescription(String privilegeId) {
	LOG.debug("# MobiliserBasePage.getPrivilegeDescription(...)");

	if (!PortalUtils.exists(this.privilegeList)) {
	    getAvailPrivilegesList();
	}
	Iterator<UmgrPrivilege> itr = this.privilegeList.iterator();
	UmgrPrivilege privilege;
	for (int i = 0; itr.hasNext(); i++) {
	    privilege = itr.next();
	    if (privilege.getPrivilege().equals(privilegeId))
		return privilege.getDescription();

	}
	return "";
    }

    public Page getResponsePage(WalletEntry wallet) {
	Page responsePage = null;
	if (PortalUtils.exists(wallet.getBankAccount())) {

	    responsePage = new BankAccountDataPage(wallet,
		    FindPendingWalletPage.class);

	} else if (PortalUtils.exists(wallet.getCreditCard())) {
	    responsePage = new CreditCardDataPage(wallet,
		    FindPendingWalletPage.class);
	} else if (PortalUtils.exists(wallet.getExternalAccount())) {
	    responsePage = new ExternalAccountDataPage(wallet,
		    FindPendingWalletPage.class);
	}

		return responsePage;
    }

	/**
	 * @return the securitysClient
	 */
	public SecurityFacade getSecuritysClient() {
		return securitysClient;
	}

	/**
	 * @param securitysClient the securitysClient to set
	 */
	public void setSecuritysClient(SecurityFacade securitysClient) {
		this.securitysClient = securitysClient;
	}

}
