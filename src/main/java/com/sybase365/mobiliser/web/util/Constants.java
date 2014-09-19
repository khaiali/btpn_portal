package com.sybase365.mobiliser.web.util;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.validation.validator.StringValidator;

public class Constants {

    public static final int LENGTH_TEMP_PASSWORD = 10;
    public static final int MIN_LENGTH_AGENT_ID = 0;
    public static final int MAX_LENGTH_AGENT_ID = 100;
    public static final int MIN_LENGTH_MERCHANT_ID = 0;
    public static final int MAX_LENGTH_MERCHANT_ID = 88;
    public static final int MIN_LENGTH_PASSWORD = 5;
    public static final int MAX_LENGTH_PASSWORD = 32;
    public static final int MIN_LENGTH_MSISDN = 0;
    public static final int MAX_LENGTH_MSISDN = 100;
    public static final int MIN_LENGTH_AMOUNT = 0;
    public static final int MAX_LENGTH_AMOUNT = 12;
    public static final int MIN_LENGTH_EMAIL = 0;
    public static final int MAX_LENGTH_EMAIL = 50;
    public static final int MIN_LENGTH_AGENT_FIRST_NAME = 0;
    public static final int MAX_LENGTH_AGENT_FIRST_NAME = 40;
    public static final int MIN_LENGTH_AGENT_TEMPLATE_NAME = 0;
    public static final int MAX_LENGTH_AGENT_TEMPLATE_NAME = 40;
    public static final int MIN_LENGTH_AGENT_LAST_NAME = 0;
    public static final int MAX_LENGTH_AGENT_LAST_NAME = 40;
    public static final int MIN_LENGTH_AGENT_COMPANY = 0;
    public static final int MAX_LENGTH_AGENT_COMPANY = 40;
    public static final int MIN_LENGTH_AGENT_POSITION = 0;
    public static final int MAX_LENGTH_AGENT_POSITION = 30;
    public static final int MIN_LENGTH_AGENT_USERNAME = 0;
    public static final int MAX_LENGTH_AGENT_USERNAME = 88;
    public static final int MIN_LENGTH_ORDER_ID = 0;
    public static final int MAX_LENGTH_ORDER_ID = 30;
    public static final int MIN_LENGTH_ZIP = 1;
    public static final int MAX_LENGTH_ZIP = 10;
    public static final int MIN_LENGTH_STREET1 = 0;
    public static final int MAX_LENGTH_STREET1 = 60;
    public static final int MIN_LENGTH_HOUSE_NUMBER = 0;
    public static final int MAX_LENGTH_HOUSE_NUMBER = 20;
    public static final int MIN_LENGTH_CITY = 0;
    public static final int MAX_LENGTH_CITY = 40;
    public static final int MIN_LENGTH_STATE = 0;
    public static final int MAX_LENGTH_STATE = 30;
    public static final int MIN_LENGTH_SEC_ANSWER = 0;
    public static final int MAX_LENGTH_SEC_ANSWER = 60;

    public static final String REQUEST_ORIGIN = "DIST-WEB";

    // regular expressions
    public static final String REGEX_MSISDN = "^(\\+){0,1}[0-9]+$";
    public static final String REGEX_AMOUNT = "^(([1-9][0-9]{0,2},([0-9]{3},)*[0-9]{3})([\\.][0-9]{0,2})?)|(0|[1-9][0-9]*([\\.][0-9]{0,2})?)|(0[\\.][0-9][1-9])|(0[\\.][0-9]0?)$";
    public static final String REGEX_FEE = "^([0-9]{1,2}([\\.][0-9]{0,2})?)$";
    public static final String REGEX_LOCALE = "^[A-Za-z][A-Za-z0-9_,+@\\-\\.=]*$";
    public static final String REGEX_LOCALE_VARIANT = "[a-z]{2}(?:_[A-Z]{2}(?:_[0-9]{4})?)?";
    public static final String REGEX_COUNTRY = "^[A-Za-z]{2}$";
    public static final String REGEX_LANGUAGE = "^[A-Za-z]{2}$";
    public static final String REGEX_PHONE_NUMBER = "^(\\+){0,1}[0-9]+$";
    public static final String REGEX_FILTER_MONTH = "^([1-9]|(1[0-2]))(\\-[1-2][0-9]{3})$";
    public static final String REGEX_DATE = "^[0-1]?[0-9]/[0-3]?[0-9]/([1-2][0-9]{3}|[0-9]{2})$";
    public static final String REGEX_EMAIL = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
    public static final String REGEX_EMAILS = "^([a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]){0,1}(,[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z])*$";
    public static final String REGEX_PHONE_NUMBERS = "^((\\+){0,1}[0-9]+){0,1}(,(\\+){0,1}[0-9]+)*$";
    public static final String REGEX_STREET1 = "^[-0-9a-zA-ZÀ-ÿ .']*[-a-zA-ZÀ-ÿ .]+[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_CITY = "^[-0-9a-zA-ZÀ-ÿ .']*[-a-zA-ZÀ-ÿ .]+[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_FIRSTNAME = "^[-0-9a-zA-ZÀ-ÿ .']*[-a-zA-ZÀ-ÿ .]+[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_AMOUNT_12_0 = "^[0-9]{1,12}$";
    public static final String REGEX_AMOUNT_12_6 = "^[0-9]{0,12}+([\\.][0-9][0-9]?[0-9]?[0-9]?[0-9]?[0-9]?)?$";
    public static final String REGEX_AMOUNT_16_2 = "^[0-9]{0,16}+([\\.][0-9][0-9]?)?$";
    public static final String REGEX_AMOUNT_16_2_NEG = "^(-)?[0-9]{0,16}+([\\.][0-9][0-9]?)?$";
    public static final String REGEX_AMOUNT_18_0 = "^[0-9]{0,18}$";
    public static final String REGEX_ACC_NUMBER = "^[a-zA-Z0-9]*$";

    public static final String REGEX_STATE = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_ZIP = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_USERNAME = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_SECURITY_ANSWER = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_TXN_TEXT = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_ORDERID = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_FRIEND_NICKNAME = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_STREET2 = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_CARD_NICKNAME = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_CARD_HOLDERNAME = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_BANK_NICKNAME = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_BANK_CODE = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_BRANCH_CODE = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_BANK_HOLDERNAME = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_BILL_NAME = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_TEMPLATE = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_COMPANY = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_POSITION = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_HOUSENO = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_TITLE = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";
    public static final String REGEX_ID_NO = "^$|^[-0-9a-zA-ZÀ-ÿ .']*$";

    public static final String DEFAULT_SERVICE_PACKAGE_ID = "444";
    public static final String DEFAULT_COUNTRY_CODE = "49";
    public static final String DEFAULT_COUNTRY_ID = "DE";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_ORGUNIT = "0000";
    public static final String DEFAULT_CURRENCY = "EUR";
    public static final int SIMILAR_NAMES_MAX_ERRORS = 2;
    public static final float SIMILAR_NAMES_MIN_PERCENTAGE = 0.75f;
    public static final int TXN_MAX_TXN_TO_FETCH = 1000;

    public static final int IDENT_TYPE_MSISDN = 0;
    public static final int IDENT_TYPE_USERNAME = 5;
    public static final int IDENT_TYPE_CUST_ID = 1;
    public static final int IDENT_TYPE_EMAIL = 7;
    public static final int IDENT_TYPE_FAX = 8;

    public static final int PI_TYPE_DEFAULT_SVA = 0;
    public static final int PI_CLASS_EXTERNAL_ACCOUNT = 0;
    public static final int PI_TYPE_INTERNAL_VOUCHER = 60;
    public static final long PI_TYPE_SYSTEM_SVA = 0;
    public static final int PI_TYPE_BANK_ACCOUNT = 40;

    public static final int CREDENTIAL_TYPE_PASSWORD = 1;
    public static final int CREDENTIAL_STATUS = 0;
    public static final int ROLE_MONEY_MERCHANT_AGENT = 8;
    public static final int CONSUMER_IDTYPE = 2;
    public static final int IDTYPE_ADMINISTRATOR = 12;
    public static final int IDTYPE_AGENT = 0;
    public static final int IDTYPE_MERCHANT_AGENT = 8;
    public static final int IDTYPE_MERCHANT_DEALER = 11;

    public static final int ADDRESS_TYPE_POSTAL_DELIVERY = 0;

    public static final int ATT_TYPE_SCANNED_REGISTRATION = 2;

    public static final int CUSTOMER_ROLE_HEADQUARTER = 10;
    public static final int CUSTOMER_ROLE_MONEY_MERCHANT = 3;
    public static final int CUSTOMER_ROLE_MONEY_MERCHANT_AGENT = 8;
    public static final int CUSTOMER_ROLE_MONEY_MERCHANT_DEALER = 11;

    public static final int ORDER_CHANNEL_WEB = 0;
    public static final String ORDER_CHANNEL_SMS = "sms";
    public static final String ORDER_CHANNEL_WEB_STR = "email";

    public static final int USE_CASE_CASH_IN = 171;
    public static final int USE_CASE_CASH_OUT = 172;
    public static final int USE_CASE_AIRTIME_TOPUP = 173;
    public static final int USE_CASE_MONEY_TRANSFER = 179;
    public static final int USE_CASE_PICK_UP = 175;
    public static final int MAX_RECORD_FETCH_REPORT = 500;
    public static final int USE_CASE_TOPUP = 181;
    public static final int USE_CASE_SENDMONEY_BANK = 161;

    public static final int KYC_LEVEL_0 = 0;
    public static final int KYC_LEVEL_1 = 1;
    public static final int KYC_LEVEL_2 = 2;
    public static final int KYC_LEVEL_3 = 3;
    public static final int KYC_LEVEL_4 = 4;
    public static final int KYC_LEVEL_5 = 5;
    public static final int KYC_LEVEL_6 = 6;
    public static final int KYC_LEVEL_7 = 7;

    // bank list
    // TODO move the GCash bank list to preferences
    public static final String BANK_LIST = "13=Allied Bank Corporation;14=Asia United Bank;15=Banco Filipino;16=Banco San Juan;17=Centennial Savings Bank;18=China Banking Corporation;19=Chinatrust Bank;20=Citibank;21=Citibank Savings;22=Citystate Savings Bank;23=East West Bank;24=Export Industry;25=Malayan Bank;28=Metropolitan Bank and Trust Company;29=Opportunity Microfinance Bank;30=PBCom;52=Philtrust Bank;33=Postal Bank;34=Philippine Savings Bank;35=Rizal Commercial Banking Corporation;36=RCBC Savings Bank;37=Real Bank;39=Standard Chartered Bank;41=World Partners Bank;45=Quezon Capital Rural Bank;46=Sterling Bank;47=Allied Savings Bank;48=Green Bank;49=Philippine Business Bank;50=Robinsons Bank;51=Tong Yang Bank;55=Security Bank;56=Metro Bank;90=Philippine National Bank";
    public static final String MODULE_REMIT_MONEY = "REMIT MONEY";
    public static final String MODULE_AIRTIME_TOPUP = "airTimeTopup";
    public static final long PI_TYPE_GCASH_WALLET = 76;
    public static final long PI_TYPE_GCASH_BANK_ACCOUNT = 79;
    public static final int USE_CASE_REMIT_MONEY_TO_WALLET = 7001;
    public static final int USE_CASE_REMIT_MONEY_TO_BANK = 7002;
    public static final int EXTERNAL_ACCOUNT_CLASS_FILTER = 7;

    // Privileges associated with merchant portal
    public static final String PRIV_MERCHANT_LOGIN = "UI_DPP_LOGIN";
    public static final String PRIV_WALLET_SERVICES = "UI_DPP_WALLET_SERVICES";
    public static final String PRIV_MERCHANT_TXN_HISTORY = "UI_DPP_TXN_HISTORY";
    public static final String PRIV_DPP_REPORTS = "UI_DPP_REPORTS";
    public static final String PRIV_TXN_CANCEL = "UI_DPP_TXN_CANCEL";
    public static final String PRIV_MANAGE_AGENTS = "UI_DPP_MANAGE_AGENTS";
    public static final String PRIV_VIEW_PAYMENT_INSTRUMENTS = "UI_DPP_VIEW_PIS";
    public static final String PRIV_PASSWORD_RECOVERY = "UI_DPP_PW_RECOVERY";
    public static final String PRIV_ATTACHMENT = "UI_DPP_ATTACHMENT";

    // system level privileges
    public static final String PRIV_VIEW_DESCENDANTS = "VIEW_DESCENDANTS";
    public static final String PRIV_CREATE_DESCENDANTS = "CREATE_DESCENDANTS";
    public static final String PRIV_MAINTAIN_DESCENDANTS = "MAINTAIN_DESCENDANTS";

    // Privileges associated with merchant portal
    public static final String PRIV_ACTIVATE_DESCENDANTS = "UI_DPP_ACTIVATE_L2PLUS_DESCENDANTS";
    public static final String PRIV_VIEW_COMMISSION_MGMT = "UI_DPP_VIEW_COMMISSION_MGMT";
    public static final String PRIV_EDIT_COMMISSION_MGMT = "UI_DPP_EDIT_COMMISSION_MGMT";
    public static final String PRIV_SETTLE_COMMISSION = "UI_DPP_SETTLE_COMMISSIONS";

    // Privileges associated with consumer portal
    public static final String PRIV_CONSUMER_LOGIN = "UI_SELFCARE_LOGIN";
    public static final String PRIV_CONSUMER_TXN_HISTORY = "UI_SELFCARE_TXN_HISTORY";
    public static final String PRIV_MANAGE_ACCOUNTS = "UI_SELFCARE_MANAGE_ACCOUNTS";
    public static final String PRIV_SEND_MONEY = "UI_SELFCARE_SEND_MONEY";
    public static final String PRIV_SEND_MONEY_BANK = "UI_SELFCARE_SEND_MONEY_BANK";
    public static final String PRIV_REQUEST_MONEY = "UI_SELFCARE_REQUEST_MONEY";
    public static final String PRIV_AIRTIME_TOPUP = "UI_SELFCARE_AIRTIME_TOPUP";
    public static final String PRIV_FRIENDS_LIST = "UI_SELFCARE_FRIENDS_LIST";
    public static final String PRIV_BANK_ACCOUNT_LIST = "UI_SELFCARE_BANK_ACCOUNT_LIST";
    public static final String PRIV_CHANGE_PASSWORD = "UI_SELFCARE_CHANGE_PASSWORD";
    public static final String PRIV_CHANGE_ADDRESS = "UI_SELFCARE_CHANGE_ADDRESS";
    public static final String PRIV_CHANGE_PREFERENCES = "UI_SELFCARE_CHANGE_PREFERENCES";
    public static final String PRIV_CHANGE_SECQANDA = "UI_SELFCARE_CHANGE_SECQANDA";
    public static final String PRIV_CHANGE_PIN = "UI_SELFCARE_CHANGE_PIN";
    public static final String PRIV_SHOW_HISTORY = "UI_SELFCARE_SHOW_HISTORY";
    public static final String PRIV_MERCHANT_TRANSACTION = "UI_DPP_MERCHANT_TXNS";
    public static final String PRIV_BILL_PAYMENT = "UI_SELFCARE_BILL_PAYMENT";
    public static final String PRIV_CONTACT_POINT = "UI_SELFCARE_CONTACT_POINT";
    public static final String PRIV_SHOW_ALERT = "UI_SELFCARE_SHOW_ALERT";
    public static final String PRIV_SHOW_MBANKING_ALERT = "UI_SELFCARE_MBANKING_ALERT";

    // Privileges associated with CST
    public static final String PRIV_CST_LOGIN = "UI_CST_LOGIN";
    public static final String PRIV_CRED_CHANGE = "UI_CST_CRED_CHANGE";
    public static final String PRIV_WILDCARDS = "CUSTOMER_WILDCARD_SEARCH";
    public static final String PRIV_CUST_READ = "UI_CST_CUSTOMER_READ";
    public static final String PRIV_CUST_WRITE = "UI_CST_CUSTOMER_WRITE";
    public static final String PRIV_CUST_PINCALL = "UI_CST_CUSTOMER_PINCALL";
    public static final String PRIV_CUST_PASSWORD = "UI_CST_CUSTOMER_RESETPASSWORD";
    public static final String PRIV_CUST_BLACKLIST = "UI_CST_CUSTOMER_BLACKLIST";
    public static final String PRIV_CUST_CANCEL = "UI_CST_CUSTOMER_CANCEL";
    public static final String PRIV_TXN_READ = "UI_CST_TRANSACTION_READ";
    public static final String PRIV_AGENT_TXN_READ = "UI_CST_AGENT_TXN_READ";
    public static final String PRIV_NOTE_READ = "UI_CST_NOTE_READ";
    public static final String PRIV_SMS_TRAFFIC = "UI_CST_SMS_TRAFFIC";
    public static final String PRIV_SVA_CREDIT = "UI_CST_CREDIT_CUSTOMER_SVA";
    public static final String PRIV_SVA_DEBIT = "UI_CST_DEBIT_CUSTOMER_SVA";
    public static final String PRIV_FOREX_READ = "UI_CST_FOREX_READ";
    public static final String PRIV_FOREX_WRITE = "UI_CST_FOREX_WRITE";
    public static final String PRIV_FEE_CONFIGURATION = "UI_CST_FEE_CONFIGURATION";
    public static final String PRIV_EDIT_RESTRICTIONS = "UI_CST_EDIT_RESTRICTIONS";
    public static final String PRIV_READ_INVOICE = "UI_CST_READ_INVOICE";
    public static final String PRIV_WRITE_INVOICE = "UI_CST_WRITE_INVOICE";
    public static final String PRIV_CREATE_CUSTOMER = "UI_CST_CREATE_CUSTOMER";
    public static final String PRIV_BALANCE_ALERT = "UI_CST_CUSTOMER_BALANCE_ALERT";
    public static final String PRIV_CST_REPORTS = "UI_CST_REPORTS";
    public static final String PRIV_CST_LIMITS = "UI_CST_LIMITS";
    public static final String PRIV_CST_MBANKING = "CST_MBANKING";
    public static final String PRIV_FIND_PENDING_APPROVALS = "UI_CST_FIND_PENDING_APPROVALS";
    public static final String PRIV_APPROVE_PENDING_CUSTOMER = "UI_CST_APPROVE_PENDING_CUSTOMER";
    public static final String PRIV_APPROVE_PENDING_WALLET = "UI_CST_APPROVE_PENDING_WALLET";
    public static final String PRIV_APPROVE_PENDING_TRANSACTION = "UI_CST_APPROVE_PENDING_TRANSACTION";
    public static final String PRIV_CST_GLOBAL_CONFIG = "UI_CST_GLOBAL_CONFIG";
    public static final String PRIV_CST_BULK_PROCESSING = "UI_CST_BULK_PROCESSING";
    public static final String PRIV_CST_BULK_UPLOAD = "UI_CST_BULK_UPLOAD";
    public static final String PRIV_CST_BULK_CONFIRM = "UI_CST_BULK_CONFIRM";
    public static final String PRIV_CST_BULK_HISTORY = "UI_CST_BULK_HISTORY";
    public static final String PRIV_CST_UMGR = "UI_CST_UMGR";
    public static final String PRIV_CST_UMGR_CREATE_ROLE = "UI_CST_UMGR_CREATE_ROLE";
    public static final String PRIV_CST_UMGR_EDIT_ROLE = "UI_CST_UMGR_EDIT_ROLE";
    public static final String PRIV_CST_UMGR_CREATE_PRIVILEGE = "UI_CST_UMGR_CREATE_PRIVILEGE";
    public static final String PRIV_CST_UMGR_EDIT_PRIVILEGE = "UI_CST_UMGR_EDIT_PRIVILEGE";
    public static final String PRIV_CST_PRIVILEGES = "UI_CST_PRIVILEGES";
    public static final String PRIV_CST_PRIVILEGES_CUSTOMER = "UI_CST_PRIVILEGES_CUSTOMER";
    public static final String PRIV_CST_PRIVILEGES_TXN = "UI_CST_PRIVILEGES_TXN";
    public static final String PRIV_CST_PRIVILEGES_WALLET = "UI_CST_PRIVILEGES_WALLET";
    public static final String PRIV_CST_PRIVILEGES_FILETYPE = "UI_CST_PRIVILEGES_FILETYPE";

    // Privileges associted with coupon administration
    public static final String PRIV_VIEW_COUPON_TYPES = "UI_CST_VIEW_COUPON_TYPES";
    public static final String PRIV_SEARCH_COUPON = "UI_CST_SEARCH_COUPON";
    public static final String PRIV_VIEW_CPN_CATEGORY = "UI_CST_VIEW_CPN_CATEGORY";
    public static final String PRIV_CPN_CTAEGORY_DESCRIPTION = "UI_CPN_CTAEGORY_DESCRIPTION";
    public static final String PRIV_CPN_DESCRIPTION = "UI_CST_CPN_DESCRIPTION";
    public static final String PRIV_CPN_TAG = "UI_CST_CPN_TAG";
    public static final String PRIV_CPN_CATEGORY = "UI_CST_CPN_CATEGORY";
    public static final String PRIV_CPN_GENERATE_BATCH = "UI_CST_CPN_GENERATE_BATCH";
    public static final String PRIV_CPN_BATCH = "UI_CST_CPN_BATCH";
    public static final String PRIV_CPN_LOCATION = "UI_CST_CPN_LOCATION";
    public static final String PRIV_CPN_ASSIGN = "UI_CST_CPN_ASSIGN";
    public static final String PRIV_CREATE_CPN_TYPE = "UI_CST_CREATE_CPN_TYPE";
    public static final String PRIV_EDIT_CPN_TYPE = "UI_CST_EDIT_CPN_TYPE";
    public static final String PRIV_UPLOAD_CPN_BATCH = "UI_CST_UPLOAD_CPN_BATCH";
    public static final String PRIV_EDIT_COUPON = "UI_CST_EDIT_COUPON";
    public static final String PRIV_ACTIVATE_CPN_BATCH = "UI_CST_ACTIVATE_CPN_BATCH";

    // Privileges associated with Configuration Manager
    public static final String PRIV_UMGR_FIND_AGENT = "UI_CST_FIND_AGENT";
    public static final String PRIV_UMGR_EDIT_AGENT = "UI_CST_EDIT_AGENT";
    public static final String PRIV_UMGR_CREATE_AGENT = "UI_CST_CREATE_AGENT";
    public static final String PRIV_NMGR_CREATE = "UI_CST_NOTIFMGR_CREATE";
    public static final String PRIV_NMGR_EDIT = "UI_CST_NOTIFMGR_EDIT";
    public static final String PRIV_NMGR_DELETE = "UI_CST_NOTIFMGR_DELETE";

    // Privileges associated with dashboard
    public static final String PRIV_DASHBOARD_LOGIN = "UI_DASHBOARD_LOGIN";
    public static final String PRIV_DASHBOARD_PREFS = "UI_DASHBOARD_PREFS";
    public static final String PRIV_DASHBOARD_JOBS = "UI_DASHBOARD_JOBS";
    public static final String PRIV_DASHBOARD_SERVERS = "UI_DASHBOARD_SERVERS";
    public static final String PRIV_DASHBOARD_TRACKERS = "UI_DASHBOARD_TRACKERS";

    public static final int PENDING_REG_MERCHANT_BLACKLSTREASON = 5;
    public static final int DEFAULT_MERCHANT_BLACKLSTREASON = 0;

    public static final String PRIV_REGEX_VIEW_CHILDREN_LEVEL = "VIEW_CHILDREN_L(\\d*)";
    public static final String PRIV_REGEX_CREATE_CHILDREN_LEVEL = "CREATE_CHILDREN_L(\\d*)";
    public static final String PRIV_REGEX_MAINTAIN_CHILDREN_LEVEL = "MAINTAIN_CHILDREN_L(\\d*)";
    public static final String PRIV_REGEX_ALLOWED_ROLES = "CHILD_CT_(\\d*)";
    public static final String PRIV_CUSTOMER_BLACKLIST = "UI_DPP_BLACKLIST_AGENT";
    public static final String PRIV_CREATE_ACTIVE_DESCENDANTS = "UI_DPP_CREATE_ACTIVE_DESCENDANTS";

    private static final String PRE_PRIV_ROLE = "ROLE_";
    public static final String PRIV_ROLE_AGENT = PRE_PRIV_ROLE + "AGENT";
    public static final String PRIV_ROLE_TOPUP_MERCHANT = PRE_PRIV_ROLE
	    + "TOPUP_MERCHANT";
    public static final String PRIV_ROLE_MONEY_CONSUMER = PRE_PRIV_ROLE
	    + "MONEY_CONSUMER";
    public static final String PRIV_ROLE_MONEY_MERCHANT = PRE_PRIV_ROLE
	    + "MONEY_MERCHANT";
    public static final String PRIV_ROLE_TOPUP_AGENT = PRE_PRIV_ROLE
	    + "TOPUP_AGENT";
    public static final String PRIV_ROLE_TOPUP_ISSUER = PRE_PRIV_ROLE
	    + "TOPUP_ISSUER";
    public static final String PRIV_ROLE_MONEY_VOUCHER_ISSUER = PRE_PRIV_ROLE
	    + "MONEY_VOUCHER_ISSUER";
    public static final String PRIV_ROLE_MONEY_MERCHANT_AGENT = PRE_PRIV_ROLE
	    + "MONEY_MERCHANT_AGENT";
    public static final String PRIV_ROLE_MONEY_BENEFICIARY = PRE_PRIV_ROLE
	    + "MONEY_BENEFICIARY";
    public static final String PRIV_ROLE_MONEY_HEADQUARTER = PRE_PRIV_ROLE
	    + "MONEY_HEADQUARTER";
    public static final String PRIV_ROLE_MONEY_DEALER = PRE_PRIV_ROLE
	    + "MONEY_DEALER";
    public static final String CAN_HAVE_BANK_ACCOUNT_PRIV = "UI_CST_CAN_HAVE_BANK_ACCOUNT";
    public static final String CAN_HAVE_CREDIT_CARD_PRIV = "UI_CST_CAN_HAVE_CREDIT_CARD";
    public static final String PRIV_KYC_LEVEL_UPGRADE_PREFIX = "UPGRADE_KYC_LEVEL_";
    public static final String PRIV_AUTO_CREATE_SVA = "AUTO_CREATE_SVA";
    public static final String PRIV_AUTO_INHERIT_PIS = "UI_DPP_AUTO_INHERIT_PIS";

    public static final String PASSWORD_RECOVERY_PRIV = "UI_SELFCARE_PASSWORD_RECOVERY";
    public static final String NOTIFICATION_CHANNEL_EMAIL = "email";

    public static final String RESOURCE_BUNDLE_COUNTIRES = "countries";
    public static final String RESOURCE_BUNDLE_LANGUAGES = "languages";
    public static final String RESOURCE_BUNDLE_TIMEZONES = "timezones";
    public static final String RESOURCE_BUNDLE_NETWORK_PROVIDERS = "networkproviders";
    public static final String RESOURCE_BUNDLE_REMITTANCE_ACC = "remittanceAccountTypes";
    public static final String RESOURCE_BUNDLE_REMITTANCE_PURPOSE = "remittancePurposeTypes";
    public static final String RESOURCE_BUNDLE_ORG_UNITS = "orgUnits";
    public static final String RESOURCE_BUNDLE_PI_TYPES = "pitypes";
    public static final String RESOURCE_BUNDLE_USE_CASES = "usecases";
    public static final String RESOURCE_BUNDLE_FILE_TYPES = "filetypes";
    public static final String RESOURCE_BUNDLE_ = "usecases";
    public static final String RESOURCE_BUNDLE_FEE_SETS = "feeSets";
    public static final String RESOURCE_BUNDLE_EXPIRY_MONTH = "monthExpiry";
    public static final String RESOURCE_BUNDLE_CURRENCIES = "currencies";
    public static final String RESOURCE_BUNDLE_IDENTIFICATION_TYPE = "identificationType";

    public static final String RESOURCE_BUNDLE_SUB_TXN_TYPES = "subTxnTypes";
    public static final String RESOURCE_BUNDLE_ERROR_CODES = "errorCode";
    public static final String RESOURCE_BUNDLE_KYCLEVELS = "kyclevel";
    public static final String RESOURCE_BUNDLE_BLOCKED_STATUS = "blockedStatus";
    public static final String RESOURCE_BUNDLE_CUSTOMER_HISTORY = "historyList";
    public static final String RESOURCE_BUNDLE_SEC_QUESTIONS = "securityQuestions";
    public static final String RESOURCE_BUNDLE_SUPPORTED_MIME_TYPES = "format";
    public static final String RESOURCE_BUNDLE_SUPPORTED_LOCALE = "locale";
    public static final String RESOURCE_BUNDLE_CODE_TYPE = "codeType";
    public static final String RESOURCE_BUNDLE_PARENT_CATEGORIES = "parentcategories";
    public static final String RESOURCE_BUNDLE_SUPPORTED_ACTION = "action";
    public static final String RESOURCE_BUNDLE_INFO_MODE = "sendModes";
    public static final String RESOURCE_BUNDLE_RISK_CATEGORIES = "riskcategories";
    public static final String RESOURCE_BUNDLE_TEMPLATE_TYPE = "templateType";
    public static final String RESOURCE_BUNDLE_IDENTITYTYPES = "identityTypes";
    public static final String RESOURCE_BUNDLE_COUPON_STATUS = "couponStatus";
    public static final String RESOURCE_BUNDLE_UMGR_PRIV = "umgrPrivileges";

    public static final int ERR_UNKNOWN_PAYEE = 2501;
    public static final int ERR_NO_PAYEE_PI = 2503;
    public static final String RESOURCE_BUNDLE_CUSTOMER_TYPE = "customertypes";
    public static final String RESOURCE_BUNDLE_GENDER = "gender";
    public static final int LIMIT_CUSTOMER_TYPE = 1;
    public static final int LIMIT_LIMITSET_TYPE = 4;
    public static final String AIRTIME_TOPUP_SERVICE_REMITTANCE = "remittance";
    public static final String AIRTIME_TOPUP_SERVICE_AUTHORISE = "authorise";
    public static final String RESOURCE_BUNDLE_HISTORY_ENTRIES = "historyEntry";
    public static final String RESOURCE_BUNDLE_TRANSACTION_STATUS = "txnStatus";
    public static final String RESOURCE_BUNDLE_INVOICE_STATUS = "invoicestatus";
    public static final String RESOURCE_BUNDLE_NOTE_STATUS = "notestatus";
    public static final String RESOURCE_BUNDLE_NOTE_CATEGORY = "notecategories";

    public static final int MBANKING_CUSTOMER_TYPE = 102;

    // regular expressions
    public static final String REGEX_PIN = "^[0-9]\\d*$";

    // Payment instrument types
    public static final int PI_TYPE_DEFAULT_BA = 40;
    public static final int PI_TYPE_EXTERNAL_BA = 45;
    public static final int PI_TYPE_CST_EXTERNAL_ACC = 70;
    public static final int PI_TYPE_EXTERNAL_SEND_MONEYBANK = 46;
    public static final int PI_TYPE_DEFAULT_CR = 20;
    public static final int PI_TYPE_EXTERNAL_AIRTIME = 71;
    // some demo privileges
    public static final String PRIV_DEMO_1 = "DEMO 1";
    public static final String PRIV_DEMO_2 = "DEMO 2";

    public static final int CREDENTIAL_TYPE_PIN = 0;

    public static final int OTP_TYPE = 10;

    public static final int USE_CASE_ADD_FUNDS = 160;
    public static final int USE_CASE_WITHDRAW_FUNDS = 161;

    public static final String MODULE_ADD_FUNDS = "addFunds";
    public static final String MODULE_WITHDRAW_FUNDS = "withdrawFunds";
    public static final String MODULE_SEND_MONEY = "sendMoney";
    public static final String MODULE_SEND_MONEY_BANK = "sendMoneyBank";
    public static final String MODULE_REQUEST_MONEY = "requestMoney";
    public static final String MODULE_TOPUP = "topup";
    public static final List<Integer> CUSTOMER_ROLES_M2M = Arrays.asList(
	    CUSTOMER_ROLE_MONEY_MERCHANT, CUSTOMER_ROLE_HEADQUARTER);
    public static final int INFO_MODE_NONE = 0;
    public static final int INFO_MODE_SMS = 1;
    public static final int INFO_MODE_EMAIL = 2;
    public static final int INFO_MODE_SMS_AND_EMAIL = 3;
    public static final String OTP_MSG_TYPE = "SMS";

    public static final int USE_CASE_SEND_MONEY = 193;
    public static final int USE_CASE_SEND_VOUCHER_UNKNOWN = 174;
    public static final int USE_CASE_REQUEST_MONEY = 194;
    public static final int PIS_TYPE_SVA = 0;
    public static final int PIS_CLASS_FILTER_SVA = 0;
    public static final int PIS_CLASS_FILTER_BANK_ACCOUNT = 4;
    public static final int PIS_CLASS_FILTER_CREDIT_CARD = 2;
    public static final int PIS_TYPE_FILTER_SVA = 0;

    public static final int CUSTOMER_NETWORK_TYPE_F_AND_F = 1;

    public static final int CUSTOMER_NETWORK_STATUS_APPROVED = 1;
    public static final String OPERATION_ADD = "add";
    public static final String OPERATION_EDIT = "edit";
    public static final String OPERATION = "operation";

    public static final String MAX_LENGTH_SEND_MONEY_TXN_TEXT = "80";
    public static final String TXN_FILTERTYPE_MONTH = "month";
    public static final String TXN_FILTERTYPE_TIMEFRAME = "timeframe";
    // Value for below constant has to be fetched from preference
    public static final String txnMaxNumberToFetch = "1000";
    public static final long txnCancelTimeBuffer = 600L;

    public static final String CSS_KEYWARD_CLASS = "class";

    public static final String CSS_STYLE_ALT = "alt";
    public static final String CSS_STYLE_ODD = "odd";
    public static final String CSS_STYLE_EVEN = "even";

    public static final String CSS_STYLE_FEE_TYPE = "feetype";

    public static final int DEFAULT_TYPE_ID_FOR_NEW_CUSTOMER = CONSUMER_IDTYPE;
    public static final int DEFAULT_TYPE_ID_FOR_NEW_CUSTOMER_DPP = 2;
    public static final int DEFAULT_TYPE_ID_FOR_INTERNAL_CUSTOMER = 0;
    public static final int DEFAULT_RISK_CAT_FOR_NEW_CUSTOMER = 0;
    public static final String SSO_SECRET = "NIy5TxKQBj4=";

    // Make best guest default here - really should be set in preferences
    public static final String DEFAULT_REPORT_SERVER_URL = "ReportViewer";
    public static final String DEFAULT_REPORT_PROXY_SERVER_URL = "http://localhost:8080/crystalrpt";
    public static final String DEFAULT_REPORT_PROXY_SERVER_PATH = "/ReportViewer";

    public static final int TXN_TIMEFRAME_ALL = 1;
    public static final int TXN_TIMEFRAME_LAST_THREE_MONTH = 2;
    public static final int TXN_TIMEFRAME_LAST_MONTH = 3;
    public static final int TXN_TIMEFRAME_LAST_TEN_DAYS = 4;
    public static final int TXN_TIMEFRAME_LAST_WEEK = 5;
    public static final int TXN_TIMEFRAME_LAST_TWO_DAYS = 6;

    public static final int TXN_STATUS_AUTHORISED = 20;
    public static final int TXN_STATUS_CAPTURED = 30;
    public static final int TXN_STATUS_AUTHCANCEL = 60;
    public static final int TXN_STATUS_CAPTURECANCEL = 70;
    public static final int TXN_STATUS_PENDING_APPROVAL = 15;

    public static final Boolean TXN_OPTION_YES = true;
    public static final Boolean TXN_OPTION_NO = false;
    public static final int MAX_ALLOWED_OTP_COUNT = 2;
    public static final int MAX_ALLOWED_OTP_RESEND_COUNT = 2;
    public static final int CANCELATION_REASON_OK = 0;
    public static final String TEMPLATE_TYPE_EMAIL_KEY = "email";
    public static final String TEMPLATE_TYPE_SMS_KEY = "sms";

    public static final String LESS_THAN_OPERATOR = "<";
    public static final String MORE_THAN_OPERATOR = ">";
    public static final String LESS_THAN_OPERATOR_STRING = "less";
    public static final String MORE_THAN_OPERATOR_STRING = "more";
    public static final String ANY_OPERATOR = "any";
    // invoice status
    public static final int INVOICE_STATUS_NEW = 0;
    public static final int INVOICE_STATUS_ACTIVE = 1;

    public static final String SERVICE_RETURNED_STATUS_SUCCESS = "Success";

    public static final StringValidator smallStringValidator = StringValidator
	    .lengthBetween(0, 6);
    public static final StringValidator mediumStringValidator = StringValidator
	    .lengthBetween(0, 80);
    public static final StringValidator largeStringValidator = StringValidator
	    .lengthBetween(0, 200);
    public static final StringValidator hugeStringValidator = StringValidator
	    .lengthBetween(0, 2048);

    public static final StringValidator fromThreeToMediumStringValidator = StringValidator
	    .lengthBetween(3, 80);

    public static final SimpleAttributeModifier smallSimpleAttributeModifier = new SimpleAttributeModifier(
	    "maxlength", "6");
    public static final SimpleAttributeModifier mediumSimpleAttributeModifier = new SimpleAttributeModifier(
	    "maxlength", "80");
    public static final SimpleAttributeModifier largeSimpleAttributeModifier = new SimpleAttributeModifier(
	    "maxlength", "200");
    public static final SimpleAttributeModifier hugeSimpleAttributeModifier = new SimpleAttributeModifier(
	    "maxlength", "2048");
    public static final SimpleAttributeModifier amountSimpleAttributeModifier = new SimpleAttributeModifier(
	    "maxlength", "30");
    public static final SimpleAttributeModifier dAmountSimpleAttributeModifier = new SimpleAttributeModifier(
	    "maxlength", "8");

    public static final SimpleAttributeModifier isShortAttributeModifier = new SimpleAttributeModifier(
	    "maxlength", "5");

    public static final SimpleAttributeModifier idLongAttributeModifier = new SimpleAttributeModifier(
	    "maxlength", "18");

    public static final SimpleAttributeModifier linkLastAttributeModifier = new SimpleAttributeModifier(
	    "class", "additionalLink last");

    public static final SimpleAttributeModifier linkAttributeModifier = new SimpleAttributeModifier(
	    "class", "additionalLink");

    public static String DROP_DOWN_ALL_OPTION = "All";
    public static Integer DROP_DOWN_ALL_OPTION_INT = 9999;
    public static final int STANDARD_INV_GRP_TYPE = 0;
    public static final int TOPUP_INV_GRP_TYPE = 2;
    public static final int DEMAND_FORPAYMENT_INV_GRP_TYPE = 3;

    // use date entry formats that are not middle-endian or low-endian specific:
    // - this is the pattern used to parse the entry into a java Date
    public static String DATE_FORMAT_PATTERN_PARSE = "MMM-dd-yyyy";
    // - this is the pattern used by the jquery datepicker corresponding
    // to the format above
    public static String DATE_FORMAT_PATTERN_PICKER = "M-dd-yy";

    // origin for all mobiliser service requests
    public static String MOBILISER_REQUEST_ORIGIN = "mobiliser-web";

    public static String SEARCH_TYPE_CUSTOMER = "customer";
    public static String SEARCH_TYPE_AGENT = "agent";
    public static String SEARCH_TYPE_MERCHANT_AGENT = "magent";
    public static String SEARCH_TYPE_MERCHANT_DEALER = "dealer";

    public static String DISABLE_ALERTS_DURING_WEEKENDS = "6,7";

    public static String ALERT_FREQUENCY_EVERYTIME = "0";
    public static String ALERT_FREQUENCY_FIRSTTIME = "1";

    public static String ALL_ACCOUNT = "-1";
    public static String TRANSACTION_AMOUNT_TYPE_ANY = "any";

    public static long ACCOUNT_BALANCE_SUMMARY_ALERT = 1;
    public static long ACCOUNT_BALANCE_THRESHOLD_ALERT = 2;
    public static long BANK_INQUIRY_RESPONSE_ALERT = 3;
    public static long FAILED_ATTEMPT_LOCKOUT_ALERT = 4;
    public static long INSUFFICIENT_FUNDS_ALERT = 5;
    public static long INVALID_ACCOUNT_ACCESS_ALERT = 6;
    public static long PANIC_PAY_ALERT = 7;
    public static long PASSWORD_CHANGED_ALERT = 8;
    public static long TRANSACTION_NOTIFICATION_ALERT = 9;
    public static long UNREAD_BANK_MESSAGE_ALERT = 10;

    // TODO - delete these once references in cst are removed
    public static String ALERT_NOTIFICATION_MSG_TYPE_TEXT = "1";
    public static String ALERT_NOTIFICATION_MSG_TYPE_CONV = "2";
    public static long ALERT_NOTIF_MSG_TYPE_TEXT = 1L;
    public static long ALERT_NOTIF_MSG_TYPE_CONV = 2L;

    public static long ACCOUNT_BALANCE_SUMMARY_ALERT_NOTIFICATION_MSG_ID = 1;
    public static long ACCOUNT_BALANCE_THRESHOLD_ALERT_NOTIFICATION_MSG_ID = 2;
    public static long BANK_INQUIRY_RESPONSE_ALERT_NOTIFICATION_MSG_ID = 3;
    public static long FAILED_ATTEMPT_LOCKOUT_ALERT_NOTIFICATION_MSG_ID = 4;
    public static long INSUFFICIENT_FUNDS_ALERT_NOTIFICATION_MSG_ID_TEXT = 5;
    public static long INSUFFICIENT_FUNDS_ALERT_NOTIFICATION_MSG_ID_CONV = 6;
    public static long INVALID_ACCOUNT_ACCESS_ALERT_NOTIFICATION_MSG_ID_TEXT = 7;
    public static long INVALID_ACCOUNT_ACCESS_ALERT_NOTIFICATION_MSG_ID_CONV = 8;
    public static long PANIC_PAY_ALERT_NOTIFICATION_MSG_ID_TEXT = 9;
    public static long PANIC_PAY_ALERT_NOTIFICATION_MSG_ID_CONV = 10;
    public static long PASSWORD_CHANGED_ALERT_NOTIFICATION_MSG_ID = 11;
    public static long TRANSACTION_ALERT_NOTIFICATION_MSG_ID = 12;
    public static long UNREAD_BANK_MESSAGE_ALERT_NOTIFICATION_MSG_ID = 13;

    public static String ALERT_DATA_KEY_PI_ID = "customerPiId";
    public static String ALERT_DATA_KEY_MIN_AMOUNT = "customerMinAmount";
    public static String ALERT_DATA_KEY_MAX_AMOUNT = "customerMaxAmount";
    public static String ALERT_DATA_KEY_TXN_TYPE = "customerUseCase";
    public static String ALERT_DATA_KEY_TXN_AMOUNT = "customerAmount";
    public static String ALERT_DATA_KEY_LOGIC_OPERATOR = "customerLogicOperator";

    public static String ROLE_ASSIGN_PRIVILEGE = "role";
    public static String PRIVILEGE_ASSIGN_ROLE = "privilege";

    public static final int CPN_ACTIVATION_PENDING = 5;

    public static int PENDING_APPROVAL_ERROR = 250;
    public static int PENDING_APPROVAL_INSUFFICIENT_PRIV_ERROR = 205;
    public static int NO_APPROVAL_CONFIG_FOUND = 204;

    public static Integer[] CST_ALLOWED_BULKFILE_TYPES = { 1, 2 };
    public static Integer[] DPP_ALLOWED_BULKFILE_TYPES = { 11, 12 };
}
