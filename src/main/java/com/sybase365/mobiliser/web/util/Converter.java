package com.sybase365.mobiliser.web.util;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.RequestCycle;

import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Address;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Customer;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identification;
import com.sybase365.mobiliser.money.contract.v5_0.customer.beans.Identity;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.BankAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.CreditCard;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.ExternalAccount;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.SVA;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageAttachment;
import com.sybase365.mobiliser.util.contract.v5_0.messaging.beans.MessageTemplate;
import com.sybase365.mobiliser.util.tools.formatutils.FormatUtils;
import com.sybase365.mobiliser.web.beans.AddressBean;
import com.sybase365.mobiliser.web.beans.CustomerBean;
import com.sybase365.mobiliser.web.beans.RemittanceAccountBean;
import com.sybase365.mobiliser.web.util.notificationmgr.jaxb.MessageAttachments;
import com.sybase365.mobiliser.web.util.notificationmgr.jaxb.MessageContent;
import com.sybase365.mobiliser.web.util.notificationmgr.jaxb.NotificationMessage;

public class Converter {

    private static Converter _this;

    static {
	_this = new Converter();
    }

    public static Converter getInstance() {
	return _this;
    }

    public static Converter getInstance(Configuration configuration) {
	_this.configuration = configuration;
	return _this;
    }

    private Configuration configuration;

    public Customer getCustomerFromCustomerBean(CustomerBean customer) {
	Customer wsCustomer = new Customer();
	if (!PortalUtils.exists(customer.getCustomerTypeId())) {
	    if (((MobiliserWebSession) RequestCycle.get().getSession())
		    .getRoles().hasRole(Constants.PRIV_MERCHANT_LOGIN)) {
		wsCustomer.setCustomerTypeId(configuration
			.getDefaultTypeIdForNewCustomerDpp());
		customer.setCustomerTypeId(wsCustomer.getCustomerTypeId());
	    } else {
		wsCustomer.setCustomerTypeId(configuration
			.getDefaultTypeIdForNewCustomer());
		customer.setCustomerTypeId(wsCustomer.getCustomerTypeId());
	    }
	} else {
	    wsCustomer.setCustomerTypeId(customer.getCustomerTypeId());
	}
	wsCustomer.setId(customer.getId());
	int blrReason = 0;
	if (customer.getBlackListReason() != null) {
	    blrReason = customer.getBlackListReason().intValue();
	}
	wsCustomer.setBlackListReason(blrReason);
	wsCustomer.setCancellationReasonId(customer.getCancelationReason());
	wsCustomer.setCountry(customer.getKvCountry());
	wsCustomer.setDisplayName(customer.getDisplayName());

	if (PortalUtils.exists(customer.getFeeSetId())
		&& customer.getFeeSetId().longValue() != 0)
	    wsCustomer.setFeeSetId(customer.getFeeSetId());
	wsCustomer.setFeeVatPercentage(null);
	if (!PortalUtils.exists(customer.getLanguage())) {
	    wsCustomer.setLanguage(configuration.getLanguage());
	} else {
	    wsCustomer.setLanguage(customer.getLanguage());
	}
	if (PortalUtils.exists(customer.getLimitId())) {
	    if (customer.getLimitId().longValue() != 0) {
		wsCustomer.setLimitSetId(customer.getLimitId());
	    }
	}
	customer.setOrgUnitId(Constants.DEFAULT_ORGUNIT);
	wsCustomer.setOrgUnitId(Constants.DEFAULT_ORGUNIT);
	wsCustomer.setReferralCode(null);
	wsCustomer.setRiskCategoryId(customer.getRiskCategoryId());
	wsCustomer.setTimeZone(null);
	wsCustomer.setTxnText(null);
	wsCustomer.setActive(customer.isActive());
	wsCustomer.setParentId(customer.getParentId());
	wsCustomer.setTest(configuration.isTestConsumer());
	wsCustomer.setDateOfBirth(customer.getBirthDateAsXml());
	wsCustomer.setSecurityAnswer(customer.getSecQuesAns());
	wsCustomer.setSecurityQuestion(customer.getSecurityQuestion());
	wsCustomer.setTxnReceiptModeId(customer.getKvInfoMode());
	wsCustomer.setTimeZone(customer.getTimeZone());
	return wsCustomer;
    }

    public AddressBean getAddressBeanFromAddress(Address wsAddress) {
	AddressBean address = new AddressBean();
	if (wsAddress != null) {
	    address.setId(wsAddress.getId());
	    address.setCity(wsAddress.getCity());
	    address.setFirstName(wsAddress.getFirstName());
	    address.setHouseNo(wsAddress.getHouseNumber());
	    address.setKvCountry(wsAddress.getCountry());
	    address.setLastName(wsAddress.getLastName());
	    address.setState(wsAddress.getState());
	    address.setStreet1(wsAddress.getStreet1());
	    address.setStreet2(wsAddress.getStreet2());
	    address.setZip(wsAddress.getZip());

	    address.setCompany(wsAddress.getCompany());
	    address.setPosition(wsAddress.getPosition());
	    address.setEmail(wsAddress.getEmail());
	}
	return address;
    }

    public CustomerBean getCustomerBeanFromCustomer(Customer wsCustomer) {
	CustomerBean customer = new CustomerBean();
	customer.setId(wsCustomer.getId());
	customer.setDisplayName(wsCustomer.getDisplayName());
	customer.setParentId(wsCustomer.getParentId());
	customer.setCustomerTypeId(wsCustomer.getCustomerTypeId());
	customer.setRiskCategoryId(!PortalUtils.exists(wsCustomer
		.getRiskCategoryId()) ? Integer.valueOf(0) : wsCustomer
		.getRiskCategoryId());
	customer.setKvCountry(wsCustomer.getCountry());
	customer.setLanguage(wsCustomer.getLanguage());
	if (PortalUtils.exists(wsCustomer.getRiskCategoryId()))
	    customer.setKvKycLevel(wsCustomer.getRiskCategoryId());
	customer.setBlackListReason(wsCustomer.getBlackListReason());
	customer.setCreated(wsCustomer.getCreated());
	customer.setTimeZone(wsCustomer.getTimeZone());
	if (PortalUtils.exists(wsCustomer.isActive()))
	    customer.setActive(wsCustomer.isActive());
	customer.setSecQuesAns(wsCustomer.getSecurityAnswer());
	customer.setSecurityQuestion(wsCustomer.getSecurityQuestion());
	customer.setCancelationReason(wsCustomer.getCancellationReasonId());
	customer.setOrgUnitId(wsCustomer.getOrgUnitId());

	customer.setBirthDateAsXml(wsCustomer.getDateOfBirth());

	if (wsCustomer.getDateOfBirth() == null) {
	    customer.setBirthDateString(null);
	} else {
	    // convert the xmlgregorian calendar into a unix timestamp
	    // at midnight in the server's timezone. that is what wicket will
	    // use when parsing the date
	    final XMLGregorianCalendar dateOfBirth = wsCustomer
		    .getDateOfBirth();

	    customer.setBirthDateString(dateOfBirth.toGregorianCalendar(
		    TimeZone.getDefault(), null, null).getTime());
	}
	if (PortalUtils.exists(wsCustomer.getFeeSetId())) {
	    customer.setFeeSetId(wsCustomer.getFeeSetId());
	    customer.setOriginalFeeSetId(customer.getFeeSetId());
	} else {
	    customer.setFeeSetId(new Long(0));
	    customer.setOriginalFeeSetId(new Long(0));
	}
	if (PortalUtils.exists(wsCustomer.getLimitSetId())) {
	    customer.setLimitId(wsCustomer.getLimitSetId());
	    customer.setOriginalLimitSetId(customer.getLimitId());
	} else {
	    customer.setLimitId(null);
	    customer.setOriginalLimitSetId(null);
	}

	return customer;
    }

    public CustomerBean getCustomerBeanFromCustomer(
	    com.sybase365.mobiliser.util.tools.wicketutils.security.Customer wsCustomer) {
	CustomerBean customer = new CustomerBean();
	customer.setId(wsCustomer.getCustomerId());
	customer.setKvCountry(wsCustomer.getCountry());
	customer.setDisplayName(wsCustomer.getDisplayName());
	customer.setParentId(wsCustomer.getParentId());
	customer.setTimeZone(wsCustomer.getTimeZone());
	return customer;
    }

    public Identification getMsisdnIdFromCustomerBean(CustomerBean customer) {
	return getIdentification(Constants.IDENT_TYPE_MSISDN,
		customer.getMsisdn(), customer.getNetworkProvider(),
		PortalUtils.exists(customer.getId()) ? customer.getId()
			.longValue() : 0);
    }

    public Identification getUsernameIdFromCustomerBean(CustomerBean customer) {
	return getIdentification(Constants.IDENT_TYPE_USERNAME,
		customer.getUserName(), customer.getNetworkProvider(),
		PortalUtils.exists(customer.getId()) ? customer.getId()
			.longValue() : 0);
    }

    public Identification getCustIdFromCustomerBean(CustomerBean customer) {
	return getIdentification(Constants.IDENT_TYPE_CUST_ID,
		Long.toString(customer.getId().longValue()),
		customer.getNetworkProvider(), customer.getId().longValue());
    }

    public Address getAddressFromAddressBean(AddressBean address) {
	Address wsAddress = new Address();

	wsAddress.setAddressSince(FormatUtils
		.getSaveXMLGregorianCalendar(new Date()));
	wsAddress.setAddressStatus(0);
	wsAddress.setAddressType(Constants.ADDRESS_TYPE_POSTAL_DELIVERY);
	wsAddress.setAlias(null);
	wsAddress.setBusinessPhone(null);
	wsAddress.setCity(address.getCity());
	wsAddress.setCompany(address.getCompany());
	wsAddress.setCompany2(null);
	wsAddress.setCompanyShortName(null);
	if (address.getKvCountry() != null)
	    wsAddress.setCountry(address.getKvCountry());
	// wsAddress.setCustomerId(address.getId());
	wsAddress.setEmail(address.getEmail());
	wsAddress.setFax(null);
	wsAddress.setFirstName(address.getFirstName());
	wsAddress.setGender(null);
	wsAddress.setHouseNumber(address.getHouseNo());
	wsAddress.setLastName(address.getLastName());
	wsAddress.setMiddleName(null);
	wsAddress.setPosition(address.getPosition());
	wsAddress.setPrivatePhone(null);
	wsAddress.setState(address.getState());
	wsAddress.setStreet1(address.getStreet1());
	wsAddress.setStreet2(address.getStreet2());
	wsAddress.setTitle(null);
	wsAddress.setUrl(null);
	wsAddress.setZip(address.getZip());
	wsAddress.setId(address.getId());
	return wsAddress;
    }

    public static SVA getSva(CustomerBean customer, String currency) {
	SVA sva = new SVA();
	// sva.set

	sva.setActive(true);
	sva.setCurrency(currency);
	sva.setCustomerId(customer.getId());
	sva.setIssuerId(null);
	sva.setLimitSetId(null);
	sva.setMultiCurrency(false);
	sva.setStatus(0);
	sva.setType(Constants.PI_TYPE_DEFAULT_SVA);
	return sva;
    }

    public WalletEntry getWalletEntry(long piId) {
	WalletEntry wsWe = new WalletEntry();

	wsWe.setAlias(null);
	wsWe.setCreditPriority(0);
	wsWe.setDebitPriority(0);
	wsWe.setLimitSetId(null);
	wsWe.setPaymentInstrumentId(piId);

	return wsWe;
    }

    public Identification getIdentification(int type, String identifier,
	    String provider, long customerId) {

	Identification wsIdent = new Identification();

	wsIdent.setActive(true);
	wsIdent.setCustomerId(customerId);
	if (type == Constants.IDENT_TYPE_MSISDN) {
	    PhoneNumber pn = new PhoneNumber(identifier,
		    configuration.getCountryCode());
	    wsIdent.setIdentification(pn.getInternationalFormat());
	    wsIdent.setProvider(provider);
	} else {
	    wsIdent.setIdentification(identifier);
	    wsIdent.setProvider(null);
	}
	wsIdent.setStatus(0);
	wsIdent.setType(type);

	return wsIdent;
    }

    public Identity getIdentity(CustomerBean customer) {
	Identity identity = new Identity();
	identity.setActive(Boolean.TRUE);
	identity.setCustomerId(customer.getId().longValue());
	identity.setIdentity(customer.getIdentityValue());
	identity.setIdentityType(customer.getKvIdentityType().intValue());
	return identity;
    }

    public ExternalAccount convertToExternalAccount(RemittanceAccountBean rAcc) {
	ExternalAccount extAcc = new ExternalAccount();
	if (PortalUtils.exists(rAcc)) {
	    extAcc.setId1(rAcc.getMsisdn());
	    extAcc.setId2(rAcc.getAccountHolder());
	    extAcc.setId3(rAcc.getAccountHolderAddress());
	    extAcc.setId4(rAcc.getAccountNo());
	    extAcc.setId5(rAcc.getInstitutionCode() == null ? null : rAcc
		    .getInstitutionCode());
	    extAcc.setId6(rAcc.getCity());
	    extAcc.setId7(rAcc.getZip());
	    extAcc.setId8(rAcc.getCountry() == null ? null : rAcc.getCountry());
	    extAcc.setType(Integer.valueOf(rAcc.getType()));
	    extAcc.setId(rAcc.getpId() == null ? null : Long.valueOf(rAcc
		    .getpId()));
	    extAcc.setCurrency(Constants.DEFAULT_CURRENCY);
	    extAcc.setStatus(0);
	}
	return extAcc;
    }

    public BankAccount getBankAccount(WalletEntry walletEntry) {
	String accNumber = "";
	BankAccount wsBa = new BankAccount();
	wsBa.setAccountHolderName(walletEntry.getBankAccount()
		.getAccountHolderName());
	wsBa.setAccountNumber(walletEntry.getBankAccount().getAccountNumber());
	wsBa.setBankCode(walletEntry.getBankAccount().getBankCode());
	wsBa.setCustomerId(walletEntry.getBankAccount().getCustomerId());
	wsBa.setBankCountry(walletEntry.getBankAccount().getBankCountry());
	wsBa.setCurrency(walletEntry.getBankAccount().getCurrency());
	wsBa.setActive(true);
	wsBa.setBankCity(walletEntry.getBankAccount().getBankCity());
	wsBa.setBranchCode(walletEntry.getBankAccount().getBranchCode());
	wsBa.setIssuerId(walletEntry.getBankAccount().getIssuerId());
	wsBa.setIssuerName(walletEntry.getBankAccount().getIssuerName());
	wsBa.setLimitSetId(walletEntry.getBankAccount().getLimitSetId());
	wsBa.setMultiCurrency(false);
	wsBa.setId(walletEntry.getBankAccount().getId());
	wsBa.setStatus(walletEntry.getBankAccount().getStatus());
	wsBa.setType(walletEntry.getBankAccount().getType());
	accNumber = wsBa.getAccountNumber().length() < 4 ? wsBa
		.getAccountNumber() : wsBa.getAccountNumber().substring(
		wsBa.getAccountNumber().length() - 4);
	wsBa.setDisplayNumber("xxxxxx" + accNumber);
	return wsBa;
    }

    public CreditCard getCreditCard(long customerId, WalletEntry walletEntry) {

	CreditCard wsCr = new CreditCard();
	wsCr.setCardHolderName(walletEntry.getCreditCard().getCardHolderName());
	String cardNumber = walletEntry.getCreditCard().getCardNumber()
		.replaceAll(" ", "");
	wsCr.setCardNumber(cardNumber);
	wsCr.setCardIssueNumber(walletEntry.getCreditCard()
		.getCardIssueNumber());
	wsCr.setMonthExpiry(walletEntry.getCreditCard().getMonthExpiry());
	wsCr.setYearExpiry(walletEntry.getCreditCard().getYearExpiry());
	wsCr.setCardType(walletEntry.getCreditCard().getCardType());
	wsCr.setSecurityNumber(walletEntry.getCreditCard().getSecurityNumber());
	wsCr.setCurrency(walletEntry.getCreditCard().getCurrency());
	wsCr.setCustomerId(customerId);
	cardNumber = wsCr.getCardNumber().length() < 4 ? wsCr.getCardNumber()
		: wsCr.getCardNumber().substring(
			wsCr.getCardNumber().length() - 4);

	wsCr.setDisplayNumber("xxxxxxxxxxxx" + cardNumber);

	wsCr.setIssuerId(walletEntry.getCreditCard().getIssuerId());
	wsCr.setIssuerName(walletEntry.getCreditCard().getIssuerName());
	wsCr.setLimitSetId(walletEntry.getCreditCard().getLimitSetId());
	wsCr.setActive(true);
	wsCr.setMultiCurrency(false);
	wsCr.setId(walletEntry.getCreditCard().getId());
	wsCr.setStatus(Integer.valueOf(0));
	wsCr.setType(Constants.PI_TYPE_DEFAULT_CR);
	return wsCr;

    }

    public com.sybase365.mobiliser.web.beans.AddressBean convertFromWSaddressToAddress(
	    Address wsAddress) {
	com.sybase365.mobiliser.web.beans.AddressBean address = new com.sybase365.mobiliser.web.beans.AddressBean();
	if (PortalUtils.exists(wsAddress)) {
	    address.setFirstName(wsAddress.getFirstName());
	    address.setLastName(wsAddress.getLastName());
	    address.setCity(wsAddress.getCity());
	    address.setState(wsAddress.getState());
	    address.setStreet1(wsAddress.getStreet1());
	    address.setStreet2(wsAddress.getStreet2());
	    address.setEmail(wsAddress.getEmail());
	    address.setKvCountry(wsAddress.getCountry());
	    address.setZip(wsAddress.getZip());
	    address.setHouseNo(wsAddress.getHouseNumber());
	}
	return address;
    }

    public NotificationMessage convertMessage(MessageTemplate message,
	    String locale, List<MessageAttachment> msgAttachments) {
	NotificationMessage nMessage = new NotificationMessage();
	nMessage.setName(message.getName());
	nMessage.setSubject(message.getSubject());
	nMessage.setSender(message.getSender());
	nMessage.setLocale(locale);
	nMessage.setTemplateType(message.getTemplateType());
	MessageContent content = new MessageContent();
	content.setCharset(message.getContent().getCharset());
	content.setContent(message.getContent().getContent());
	content.setContentType(message.getContent().getContentType());
	nMessage.setContent(content);
	if (PortalUtils.exists(message.getAlternativeContent())) {
	    MessageContent altContent = new MessageContent();
	    altContent.setCharset(message.getAlternativeContent().getCharset());
	    altContent.setContent(message.getAlternativeContent().getContent());
	    altContent.setContentType(message.getAlternativeContent()
		    .getContentType());
	    nMessage.setAlternativeContent(altContent);
	}
	if (PortalUtils.exists(msgAttachments)) {
	    for (MessageAttachment att : msgAttachments) {
		MessageAttachments nAtt = new MessageAttachments();
		nAtt.setContentType(att.getContentType());
		nAtt.setName(att.getName());
		nMessage.getAttachments().add(nAtt);
	    }
	}
	return nMessage;
    }

}
