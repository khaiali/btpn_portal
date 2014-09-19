package com.sybase365.mobiliser.web.btpn.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * This class delcares the constants required for the btpn applications.
 * 
 * @author Vikram Gunda
 */
public class BtpnConstants {

	/**
	 * Default Private Constructor
	 */
	private BtpnConstants() {

	}

	public static final Map<Integer, String> AGENT_TYPES = new HashMap<Integer, String>();

	public static final int PRODUCT_CATEGORY_TOP_AGENT = 2;

	public static final int PRODUCT_CATEGORY_AGENT = 3;

	public static final int PRODUCT_TYPE_SUB_AGENT = 4;

	static {
		AGENT_TYPES.put(PRODUCT_CATEGORY_TOP_AGENT, "Top Agent");
		AGENT_TYPES.put(PRODUCT_CATEGORY_AGENT, "Child Agent");
		AGENT_TYPES.put(PRODUCT_TYPE_SUB_AGENT, "Sub Agent");
	}

	/* Start common constants across the panels */

	/** footer version seperator. */
	public static final String FOOTER_VERSION_SEPERATOR = " | ";

	/** Required Symbol if escapeMarkupStrings is used. */
	public static final String REQUIRED_SYMBOL = "<span class=\"required\">*</span>";

	/** Id Expression for the dropdown. */
	public static final String ID_EXPRESSION = "id";

	/** Display Expression for the dropdown. */
	public static final String DISPLAY_EXPRESSION = "value";

	public static final String DISPLAY_EXPRESSION_ID_VALUE = "idAndValue";

	/** HTML Max Length Attribute. */
	public static final String MAXLENGTH_TAG_LABEL = "maxlength";

	/** Max Length Attribute for Email Id. */
	public static final SimpleAttributeModifier EMAIL_ID_MAX_LENGTH = new SimpleAttributeModifier(MAXLENGTH_TAG_LABEL,
		"40");

	/** Regex for Email Id. */
	public static final String EMAIL_ID_REGEX = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

	public static final int IDENTIFICATION_TYPE_USER_NAME = 5;

	public static final int IDENTIFICATION_TYPE_MOBILE_NO = 0;

	public static final int IDENTIFICATION_TYPE_ATM_CARD_NO = 6;

	public static final int IDENTIFICATION_TYPE_ID_CARD_NO = 4;

	public static final int IDENTIFICATION_TYPE_SVA_NO = 8;

	public static final int CREDENTIAL_TYPE_PIN = 0;

	public static final String PIN_REGEX = "\\d{6}";

	public static final int CREDENTIAL_TYPE_PASSWORD = 1;

	public static final String MALE_CODE_ID = "M";

	public static final String YES_ID = "Y";

	public static final String YES_VALUE = "Yes";

	public static final String NO_ID = "N";

	public static final String NO_VALUE = "No";

	/* End common constants across the panels */

	/* Start Privileges associated with BTPN */

	/** BTPN Bank Portal Login Privilege. */
	public static final String PRIV_BTPN_BANK_PORTAL_LOGIN = "BANK_PORTAL_LOGIN";

	public static final String PRIV_UI_HOME_BANK_PORTAL = "UI_HOME_BANK_PORTAL";
	
	public static final String PRIV_UI_BANK_LIMITEX = "UI_BANK_LIMITEX";
	
	public static final String PRIV_UI_BANK_LIMITEX_APPROVE = "UI_BANK_LIMITEX_APPROVE";
	
	public static final String PRIV_UI_BANK_MANAGE_PROFILE = "UI_BANK_MANAGE_PROFILE";

	public static final String PRIV_UI_BANK_ADMIN_REGISTRATION = "UI_BANK_ADMIN_REGISTRATION";

	public static final String PRIV_UI_BANK_USER_REGISTRATION = "UI_BANK_USER_REGISTRATION";

	public static final String PRIV_UI_CUSTOMER_REGISTRATION_APPROVAL = "UI_CUSTOMER_REGISTRATION_APPROVAL";

	public static final String PRIV_UI_CHANGE_LANGUAGE = "UI_CHANGE_LANGUAGE";

	public static final String PRIV_UI_CUSTOMER_REGISTRATION = "UI_CUSTOMER_REGISTRATION";

	public static final String PRIV_UI_OFFICER_AGENT_APPROVAL = "UI_OFFICER_AGENT_APPROVAL";

	public static final String PRIV_UI_TOP_AGENT_CASH_IN = "UI_TOP_AGENT_CASH_IN";

	public static final String PRIV_UI_TOP_AGENT_CASH_OUT = "UI_TOP_AGENT_CASH_OUT";

	public static final String PRIV_UI_MANAGE_PRODUCTS = "UI_MANAGE_PRODUCTS";

	public static final String PRIV_UI_MANAGE_LIMIT = "UI_MANAGE_LIMIT";

	public static final String PRIV_UI_MANAGE_FEE = "UI_MANAGE_FEE";
	
	public static final String PRIV_UI_MANAGE_FEE_APPROVAL = "UI_MANAGE_FEE_APPROVAL";

	public static final String PRIV_UI_MANAGE_AIRTIME_TOPUP_FEE = "UI_MANAGE_AIRTIME_TOPUP_FEE";

	public static final String PRIV_UI_MANAGE_BILL_PAYMENT_FEE = "UI_MANAGE_BILL_PAYMENT_FEE";

	public static final String PRIV_UI_MANAGE_BILL_PAYMENT_FEE_APPROVAL = "UI_MANAGE_BILL_PAYMENT_FEE_APPROVAL";
	
	public static final String PRIV_UI_APPROVE_PRODUCTS = "UI_APPROVE_PRODUCTS";

	public static final String PRIV_UI_TOP_AGENT_REGISTRATION = "UI_TOP_AGENT_REGISTRATION";

	public static final String PRIV_UI_MSISDN_CHANGE_REQUESTS = "UI_CHANGE_MOBILE_NUMBER";

	public static final String PRIV_UI_TRANSACTION_REVERSAL = "UI_TRANSACTION_REVERSAL";

	public static final String PRIV_UI_APPROVE_TRANSACTION_REVERSAL = "UI_APPROVE_TRANSACTION_REVERSAL";

	public static final String PRIV_UI_APPROVE_FEE_LIMIT = "UI_APPROVE_FEE_LIMIT";
	
	public static final String PRIV_UI_CHILD_SUB_AGENT_APPROVAL = "UI_APPROVE_ALL_AGENTS_EDIT_PROFILE_DATA";

	public static final String PRIV_UI_MANAGE_GL = "UI_MANAGE_GL";

	public static final String PRIV_UI_APPROVE_GL = "UI_APPROVE_GL";
	
	public static final String PRIV_INTEREST_MAKER = "INTEREST_MAKER_PRIVILEGE";
	
	public static final String PRIV_INTEREST_CHECKER = "INTEREST_CHECKER_PRIVILEGE";
	
	public static final String PRIV_INTEREST_TAX_MAKER = "INTEREST_TAX_MAKER_PRIVILEGE";
	
	public static final String PRIV_INTEREST_TAX_CHECKER = "INTEREST_TAX_CHECKER_PRIVILEGE";

	public static final String PRIV_UI_BATCH_TRANSACTION_UPLOAD = "UI_BATCH_TRANSACTION_UPLOAD";

	public static final String PRIV_UI_BATCH_TRANSACTION_UPLOAD_APPROVE = "UI_BATCH_TRANSACTION_UPLOAD_APPROVE";

	public static final String PRIV_UI_SEARCH_TRANSACTION_DATA = "UI_SEARCH_TRANSACTION_DATA";

	public static final String PRIV_UI_CUSTOMER_UPLOAD = "UI_CUSTOMER_UPLOAD";

	public static final String PRIV_UI_SEARCH_CUSTOMER_DATA = "UI_SEARCH_CUSTOMER_DATA";

	public static final String PRIV_UI_HOLIDAY_CALENDAR = "UI_HOLIDAY_CALENDAR";

	public static final String PRIV_UI_HOLIDAY_CALENDAR_APPROVAL = "UI_HOLIDAY_CALENDAR_APPROVAL";

	public static final String PRIV_UI_SALARY_UPLOAD = "UI_SALARY_UPLOAD";

	public static final String PRIV_UI_SEARCH_SALARY_DATA = "UI_SEARCH_SALARY_DATA";

	public static final String PRIV_UI_SALARY_UPLOAD_APPROVE = "UI_SALARY_UPLOAD_APPROVE";

	public static final String PRIV_UI_MANAGE_TRANSACTION_GL = "UI_MANAGE_TRANSACTION_GL";

	public static final String PRIV_UI_APPROVE_TRANSACTION_GL = "UI_APPROVE_TRANSACTION_GL";

	public static final String PRIV_UI_APPROVE_CUSTOMER_DATA = "UI_APPROVE_CUSTOMER_DATA";
	
	public static final String PRIV_UI_APPROVE_ALL_AGENTS_DATA = "UI_CHILD_SUB_AGENT_REG_APPROVAL";

	public static final String PRIV_UI_AGENT_CARE = "UI_AGENT_CARE";

	public static final String PRIV_UI_CUSTOMER_CARE = "UI_CUSTOMER_CARE";

	public static final String PRIV_UI_BANK_PORTAL_TRANSACTIONS = "UI_BANK_PORTAL_TRANSACTIONS";

	public static final String PRIV_UI_BANK_PORTAL_MAIN_DATA = "UI_BANK_PORTAL_MAIN_DATA";

	public static final String PRIV_UI_BANK_PORTAL_ACTIVATE_DEACTIVATE_MENU = "UI_BANK_PORTAL_ACTIVATE_DEACTIVATE_MENU";

	public static final String PRIV_UI_ADD_HELP = "UI_BANK_PORTAL_ADD_HELP";

	public static final String PRIV_UI_TRANSACTION_DETAILS_REPORT = "UI_BANK_PORTAL_TRANSACTION_DETAILS_REPORT";

	/** BTPN Consumer Portal Login Privilege. */
	public static final String PRIV_BTPN_CONSUMER_LOGIN = "CONSUMER_PORTAL_LOGIN";

	/** BTPN Agent Portal Login Privilege. */
	public static final String PRIV_BTPN_AGENT_LOGIN = "AGENT_PORTAL_LOGIN";

	public static final String BANK_ADMIN_REGISTER_MAKER_PRIVILEGE = "BANK_ADMIN_REGISTER_MAKER_PRIVILEGE";

	public static final String BANK_ADMIN_REGISTER_EXECUTE_PRIVILEGE = "BANK_ADMIN_REGISTER_EXECUTE_PRIVILEGE";

	public static final String BANK_STAFF_REGISTER_MAKER_PRIVILEGE = "BANK_STAFF_REGISTER_MAKER_PRIVILEGE";

	public static final String BANK_STAFF_REGISTER_CHECKER_PRIVILEGE = "BANK_STAFF_REGISTER_CHECKER_PRIVILEGE";

	public static final String CUSTOMER_REGISTER_MAKER_PRIVILEGE = "CUSTOMER_REGISTER_MAKER_PRIVILEGE";

	public static final String CUSTOMER_REGISTER_CHECKER_PRIVILEGE = "CUSTOMER_REGISTER_CHECKER_PRIVILEGE";

	public static final String TOP_AGENT_REGISTER_MAKER_PRIVILEGE = "TOP_AGENT_REGISTER_MAKER_PRIVILEGE";

	public static final String PRIV_BTPN_CONSUMER_FUND_TRANSFER = "UI_CONSUMER_SELF_FUND_TRANSFERS";

	public static final String PRIV_BTPN_CONSUMER_MANAGE_SUB_ACCOUNTS = "UI_CONSUMER_SELF_MANAGE_SUB_ACCOUNTS";

	public static final String PRIV_BTPN_CONSUMER_SELF_SUB_ACCOUNTS_TRANSFERS = "UI_CONSUMER_SELF_SUB_ACCOUNTS_TRANSFERS";

	public static final String PRIV_BTPN_CONSUMER_CHANGE_PIN = "UI_CONSUMER_SELF_CHANGE_PIN";
	
	public static final String PRIV_BTPN_CASH_IN = "UI_CASH_IN";

	/* End Privileges associated with BTPN */

	// Start Preferences default values
	public static final String LOGIN_DOMIANS_DEFAULT_VALUE = "@Development,@Production";

	/* Start Bank Admin/Staff Registration constants */

	// Bank Staff Registration Panel
	public static final SimpleAttributeModifier BANK_STAFF_USER_ID_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "40");

	public static final String BANK_STAFF_USER_ID_REGEX = "[A-Za-z0-9 \\.\\-\\_\\@]*";

	public static final SimpleAttributeModifier BANK_STAFF_NAME_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "40");
	
	/* Add By Andi */
	public static final SimpleAttributeModifier BANK_STAFF_GL_CODE_MAX_LENGTH = new SimpleAttributeModifier(
			MAXLENGTH_TAG_LABEL, "6");

	public static final SimpleAttributeModifier BANK_STAFF_DESIGNATION_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "30");

	public static final SimpleAttributeModifier BANK_STAFF_TERRITORY_CODE_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "9");

	public static final String BANK_STAFF_TERRITORY_CODE_REGEX = "[A-Za-z0-9]*";

	public static final String RESOURCE_BUNDLE_BTPN_LANGUAGES = "filteredlanguages";

	public static final String RESOURCE_BUNDLE_BANK_ADMIN = "bankAdminRole";

	public static final String RESOURCE_BUNDLE_BANK_USER = "bankOfficerRole";

	public static final String RESOURCE_BUNDLE_BANK_USER_ALL_ROLES = "allRoles";

	public static final int BLACKLISTREASON_ZERO = 0;

	public static final int BLACKLISTREASON_STAFF_STATUS = 6;

	public static final String BANK_ADMIN_REGISTRATION_TYPE = "Bank Admin";

	public static final String BANK_OFFICER_REGISTRATION_TYPE = "Bank Officer";

	public static final int NOTIFICATION_MODE_EMAIL = 2;

	/* End Bank Admin/Staff Registration constants */

	// Login Constants
	public static final SimpleAttributeModifier LOGIN_USER_NAME_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "40");

	public static final SimpleAttributeModifier PASSWORD_MAX_LENGTH = new SimpleAttributeModifier(MAXLENGTH_TAG_LABEL,
		"32");

	public static final String ATTEMPTS_LEFT_KEY = "ATTEMPTS_LEFT";

	public static final String RESOURCE_BUNDLE_PORTALS_ADD_HELP = "portalsAddHelp";

	public static final String PORTAL_CONSUMER = "CONSUMER_PORTAL";

	public static final String PORTAL_AGENT = "AGENT_PORTAL";

	// Start Consumer Registration Panel Constants

	/** Date Format Picker for registration. */
	public static String DATE_FORMAT_PATTERN_PICKER = "dd/mm/yy";

	public static String ID_EXPIRY_DATE_PATTERN = "dd/MM/yyyy";

	public static final String REG_CONSUMER = "consumer";

	public static final String REG_TOPUP_AGENT = "topupagent";

	public static final String REG_CHILD_AGENT = "childagent";

	public static final String REG_SUB_AGENT = "subagent";

	public static final String REGEX_PHONE_NUMBER = "^((\\+628)|(08))(\\d{1,10})$";

	public static final String DEFAULT_TIME_ZONE = "Asia/Jakarta";

	public static final String DEFAULT_COUNTRY_CODE = "62";

	public static final String DEFAULT_LANGUAGE = "in";

	public static final String DEFAULT_COUNTRY = "ID";

	public static final int DEFAULT_TRANSACTION_START_MONTH = 1;

	public static final int DEFAULT_TRANSACTION_START_YEAR = 2009;

	public static final String DEFAULT_SUPER_ADMIN = "SuperAdmin";

	public static final String BANK_PORTAL_LOGIN_URL_SUFFIX = "/login";

	public static final String AGENT_PORTAL_LOGIN_URL_SUFFIX = "/login";

	public static final String CONSUMER_PORTAL_LOGIN_URL_SUFFIX = "/login";

	public static final StringValidator PHONE_NUMBER_VALIDATOR = StringValidator.lengthBetween(6, 14);

	public static final SimpleAttributeModifier PHONE_NUMBER_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "14");

	public static final SimpleAttributeModifier OTP_MAX_LENGTH = new SimpleAttributeModifier(MAXLENGTH_TAG_LABEL, "6");

	public static final String RESOURCE_BUNDLE_CONSUMER_REGISTRATION_TYPE = "registrationType";

	public static final String RESOURCE_BUNDLE_MARITAL_STATUS = "martialStatus";

	public static final String RESOURCE_BUNDLE_PRODUCT_CUSTOMERS = "customers";

	public static final String RESOURCE_BUNDLE_PRODUCT_TOP_AGENT = "topagents";

	public static final String RESOURCE_BUNDLE_PRODUCT_AGENTS = "agents";

	public static final String RESOURCE_BUNDLE_PRODUCT_SUB_AGENTS = "subagents";

	public static final String RESOURCE_BUNDLE_OCCUPATION = "occupation";

	public static final String RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE = "industrySectorofEmployer";

	public static final String RESOURCE_BUNDLE_SOURCE_OF_FOUND = "sourceofFund";

	public static final String RESOURCE_BUNDLE_HIGH_RISK_BUSINESS = "highRiskBusiness";

	public static final String RESOURCE_BUNDLE_IS_OPTIMA_ACTIVATED = "isOptimaActivated";
	
	public static final String RESOURCE_BUNDLE_RELIGION = "allReligions";
	
	public static final String RESOURCE_BUNDLE_LAST_EDUCATIONS = "allLastEducations";
	
	public static final String RESOURCE_BUNDLE_FORECAST_TRANSACTIONS = "allForecastTransactions";
	
	public static final String RESOURCE_BUNDLE_FORECAST_PROVINCE = "allProvinces";
	
	public static final String RESOURCE_BUNDLE_CITIES= "allCities";

	public static final String RESOURCE_BUNDLE_GENDERS = "genders";

	public static final String RESOURCE_BUBDLE_TAX_EXEMPTED = "isTaxExempted";

	public static final String RESOURCE_BUBDLE_HIGH_RISK_CUSTOMER = "highRiskCustomer";

	public static final String RESOURCE_BUBDLE_ID_TYPE = "idType";

	public static final String RESOURCE_BUBDLE_INCOME = "income";

	public static final String RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT = "purposeofAccount";

	public static final String RESOURCE_BUBDLE_JOB = "job";

	public static final String RESOURCE_BUBDLE_RECEIPT_MODE = "notificationmodes";

	public static final String RESOURCE_BUBDLE_NATIONALITY = "btpn_countries";

	public static final String RESOURCE_BUNDLE_BTPN_REG_LANG = "filteredlanguages";

	public static final String RESOURCE_BUNDLE_BTPN_GL_CODES = "agentGlCodes";

	public static final SimpleAttributeModifier REGISTRATION_DISPLAY_NAME_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "50");

	public static final String REGISTRATION_DISPLAY_NAME_REGEX = "[A-Za-z0-9 ]*";

	public static final String REGISTRATION_NAME_WILDCARD_REGEX = "[A-Za-z0-9-* ]*";

	public static final SimpleAttributeModifier REGISTRATION_SHORT_NAME_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "24");

	public static final String REGISTRATION_EMPLOYEE_ID_REGEX = "[A-Za-z0-9]*";

	public static final SimpleAttributeModifier REGISTRATION_EMPLOYEE_ID_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "20");

	public static final String REGISTRATION_SHORT_NAME_REGEX = "[A-Za-z0-9 ]*";

	public static final SimpleAttributeModifier REGISTRATION_MOTHER_MAIDEN_NAME_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "30");

	public static final String REGISTRATION_MOTHER_MAIDEN_NAME_REGEX = "[A-Za-z0-9 ]*";

	public static final String REGISTRATION_PLACE_OF_BIRTH_REGEX = "[A-Za-z0-9 ]*";

	public static final String ATTACHMENT_FILE_LIST_SEPERATOR = ",";

	public static final SimpleAttributeModifier REGISTRATION_PLACE_OF_BIRTH_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "35");

	public static final String REGISTRATION_ATM_CARD_NUMBER_REGEX = "[0-9]*";

	public static final SimpleAttributeModifier REGISTRATION_ATM_CARD_NUMBER_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "16");

	public static final SimpleAttributeModifier REGISTRATION_ID_CARD_NUMBER_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "30");

	public static final StringValidator REGISTRATION_ATM_CARD_NUMBER_VALIDATOR = StringValidator.exactLength(16);

	public static final String REGISTRATION_STREET1_REGEX = "[A-Za-z0-9 \\(\\)\\.\\&\\,\\-\\/]*";

	public static final SimpleAttributeModifier REGISTRATION_STREET1_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "40");

	public static final String REGISTRATION_STREET2_REGEX = "[A-Za-z0-9 \\(\\)\\.\\&\\,\\-\\/]*";

	public static final SimpleAttributeModifier REGISTRATION_STREET2_MAX_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "40");

	public static final SimpleAttributeModifier REGISTRATION_PURPOSE_OF_TRANSACTION_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "40");

	public static final Long REGISTRATION_FILE_MAX_SIZE = 100000L;

	public static final int REGISTRATION_CUSTOMER_INITIAL_BLACKLISTREASON = 6;

	public static final int REGISTRATION_INITIAL_STATUS = 0;

	public static final String REGISTRATION_PENDING_APPROVAL_STATUS = "Pending Approval";

	public static final int CUSTOMER_ATTRIBUTE_GLCODE = 65;

	// Css Styles for Data View
	public static final String DATA_VIEW_EVEN_ROW_CSS = "even";

	public static final String DATA_VIEW_ODD_ROW_CSS = "odd";

	public static final String REGEX_PERCENT = "^([0-9][1-9]*([\\.][0-9]{0,2})?)|(0[\\.,][0-9][1-9])||(0[\\.,][1-9]0?)$";
	
	public static final String REGEX_AMOUNT = "^([1-9][0-9]*([\\.][0-9]{0,2})?)|(0[\\.,][0-9][1-9])||(0[\\.,][1-9]0?)$";

	public static final String REGEX_ALPHBETES_SPACE = "[a-zA-Z\\s']+";

	public static final String RESOURCE_BUBDLE_FT_ACCOUNT_TYPE = "fundTransferAccountType";

	public static final String RESOURCE_BUBDLE_FT_FAVORITE_LIST = "favoriteList";

	public static final String RESOURCE_BUBDLE_FT_BANK_CODES = "allBankCode";
	
	public static final String RESOURCE_BUBDLE_FT_TO_OTHER_BANK_CODES = "allOnlineBanks";

	public static final String FILTERTYPE_MANUAL = "manual";

	public static final String FILTERTYPE_FAVORITE = "favorite";

	public static final String FT_ACCOUNT_TYPE_MOBILE = "Mobile";

	public static final String FT_ACCOUNT_TYPE_BTPN_ACCOUNT = "BtpnBankAccount";

	public static final String FT_ACCOUNT_TYPE_OTHER_BANK_ACCOUNT = "OtherBankAccount";

	public static final String FT_ACCOUNT_TYPE_SKN = "skn";

	public static final String FT_ACCOUNT_TYPE_RTGS = "rtgs";

	public static final String RESOURCE_BUBDLE_SUB_ACCOUNT_TYPE = "subAccountType";

	public static final String RESOURCE_BUBDLE_TRANSFER_TYPE_PTOS = "primaryToSubAccount";

	public static final String RESOURCE_BUBDLE_TRANSFER_TYPE_STOP = "subAccountToPrimary";

	// Constants for Manage Products
	public static final String FIXED_INTEREST_RADIO = "Fixed Interest";

	public static final String PERCENT_INTEREST_RADIO = "Percent Interest";

	public static final String MIN_BALANCE_REGEX = "^[0-9]{1,12}$";

	public static final String INITIAL_AMOUNT_REGEX = "^[0-9]{1,12}$";

	public static final String ADMIN_FEE_REGEX = "^[0-9]{1,12}$";

	public static final String RANGE_REGEX = "^[0-9]{1,12}$";

	public static final String PRODUCT_GL_CODES = "allGlCodes";

	public static final String PRODUCT_TYPES = "allProductCategories";

	public static final SimpleAttributeModifier PRODUCT_AMOUNTS_LENGTH = new SimpleAttributeModifier(
		MAXLENGTH_TAG_LABEL, "12");

	// Constants for Manage Limits
	public static final String LIMIT_REGEX = "^([0-9]{1,12})&(^0)$";

	public static final SimpleAttributeModifier LIMIT_MAX_LENGTH = new SimpleAttributeModifier(MAXLENGTH_TAG_LABEL,
		"12");

	public static final String RESOURCE_BUNDLE_PRODUCT = "allActiveProducts";

	public static final String RESOURCE_BUNDLE_USECASE = "allUseCases";

	public static final int CONFIRM_NEW_PIN_LENGTH = 6;

	public static final int STATUS_CODE = 0;

	public static final int CREDENTIAL_TYPE = 0;

	public static final String RESOURCE_BUBDLE_MANAGE_FAVORITE_TYPES = "favoriteTypes";

	public static final int OTP_TYPE = 11;

	public static final int OTP_REFERENCE_ID = 100;

	// Constants for Manage Fees
	public static final String RESOURCE_BUNDLE_USE_CASES = "allUseCases";

	public static final String USECASE_FIXED_RADIO = "Fixed Fee";

	public static final String USECASE_SLAB_RADIO = "Slab Fee";

	public static final String USECASE_SHARING_RADIO = "Sharing Fee";

	public static final String RESOURCE_BUNDLE_PRODUCTS_TYPES = "allActiveProducts";

	public static final String RESOURCE_BUNDLE_GL_CODES = "allGlCodes";

	public static final String AMOUNT_REGEX = "^[0-9]{0,19}$";

	public static final String TRANSACTION_AMOUNT_REGEX = "^[0-9]*$";

	public static final String SLAB_AMOUNT_REGEX = "^[0-9]+$";

	public static final int ORDER_CHANNEL = 0;

	// Constants for Manage Bill Payment Fee

	public static final String RESOURCE_BILL_PAYMENT_FEE_GL_CODES = "allGlCodes";

	public static final String USECASE_BILLPAYMENT_FEE = "Bill Payment Fee";

	public static final String USECASE_AIRTIME_FEE = "Airtime Topup Fee";

	public static final String USECASE_LIMIT = "Limit";

	public static final int USECASE_USSD_CASHIN = 210;

	public static final int USECASE_USSD_CASHOUT = 211;

	public static final int USECASE_TOPAGENT_CASH_IN_AT_BANK = 212;

	public static final int USECASE_TOPAGENT_CASH_OUT_AT_BANK = 213;

	public static final int USECASE_AGENT_CASH_IN_AT_TOP_AGENT = 214;

	public static final int USECASE_AGENT_CASH_OUT_AT_TOP_AGENT = 215;

	public static final String PRIV_CUSTOMER_CASHIN_AT_AGENT = "UC_210_PAYER_PRIVILEGE";

	public static final String PRIV_CUSTOMER_CASHOUT_AT_AGENT = "UC_211_PAYEE_PRIVILEGE";

	public static final String PRIV_TOPAGENT_CASHIN_AT_BANK = "UC_212_PAYER_PRIVILEGE";

	public static final String PRIV_TOPAGENT_CASHOUT_AT_BANK = "UC_213_PAYEE_PRIVILEGE";

	public static final String PRIV_AGENT_CASHIN_AT_TOP_AGENT = "UC_214_PAYER_PRIVILEGE";

	public static final String PRIV_AGENT_CASHOUT_AT_TOP_AGENT = "UC_215_PAYEE_PRIVILEGE";

	public static final String RESOURCE_BUBDLE_SI_TYPE = "standingInstructionsType";

	public static final String INSTRUCTION_TYPE_BILL_PAYMENT = "billpayment";

	public static final String INSTRUCTION_TYPE_FUND_TRANSFER = "fundtransfer";

	public static final String INSTRUCTION_TYPE_TOPUP = "topup";

	// Constants for Manage General Ledger
	public static final String RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES = "allParentGlCodes";

	public static final String GL_CODE_REGEX = "^[0-9]*$";

	public static final String RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES = "generalLedgerTypes";

	public static final StringValidator GL_MINIMUM_LENGTH = StringValidator.minimumLength(7);

	// Constants for Salary Upload
	public static final String CSV_CONTENT_TYPE = "application/vnd.ms-excel";

	public static String SALARY_SEARCH_FROM_DATE_PATTERN = "dd/MM/yyyy";

	public static String SALARY_SEARCH_TO_DATE_PATTERN = "dd/MM/yyyy";

	public static final String RESOURCE_BUNDLE_SEARCH_STATUS = "bulkFileStatus";

	public static final String RESOURCE_BUNDLE_BULK_FILE_PROCESSING_TYPE = "bulkFileProcessingType";
	
	public static final String RESOURCE_BUNDLE_BULK_FILE_PROCESSING_STATUS = "bulkFileProcessingStatus";
	
	public static final int FILE_TYPE_REG_UPLOAD = 2;

	public static final int FILE_TYPE_SALARY_UPLOAD = 3;

	public static final String BATCH_FUND_TRANSFER_CHECKER_PRIVILEGE = "BATCH_FUND_TRANSFER_CHECKER_PRIVILEGE";

	public static final String BATCH_CUSTOMER_CHECKER_PRIVILEGE = "BATCH_CUSTOMER_CHECKER_PRIVILEGE";

	public static final String PRIV_CHANGE_MSISDN_CHECKER = "CHANGE_MSISDN_CHECKER_PRIVILEGE";

	public static final String RESOURCE_BUBDLE_BILLER_TYPE = "billerType";

	public static final String RESOURCE_BUBDLE_SUB_BILLER = "subBiller";

	public static final String PRIV_BTPN_AGENT_CHANGE_PIN = "UI_CONSUMER_SELF_CHANGE_PIN";

	public static final String RESOURCE_BUBDLE_SELECTED_DAY = "selectedDay";

	public static final String FILTERTYPE_CHARGED = "charged";

	public static final String FILTERTYPE_FIXED = "fixed";

	public static final String FREQUENCY_TYPE_MONTH = "MONTHLY";

	public static final String FREQUENCY_TYPE_QUARTER = "QUARTERLY";

	public static final String FREQUENCY_TYPE_WEEK = "WEEKLY";

	public static final String FREQUENCY_TYPE_DAILY = "DAILY";

	public static final String RESOURCE_BUBDLE_SI_FT_TYPE = "siFundTransferType";

	public static final String BILLPAYMENT_MANUALLY = "Manual";

	public static final String BILLPAYMENT_FAVLIST = "Favourite";

	public static final String BILLPAYMENT_CURRENCY = "IDR";

	public static final String RESOURCE_BUBDLE_SI_TOPUP_TYPE = "siTopupType";

	public static final String RESOURCE_BUBDLE_SI_TOPUP_DENOMINATIONS = "denominations";

	public static final int USECASE_AIR_TIME_TOPUP = 223;

	public static final int USECASE_BILL_PAYMENT = 224;

	public static final int USECASE_FT_MOBILE_TO_MOBILE = 219;
	
	public static final int USECASE_FT_BTPN_BANK = 216;

	public static final int USECASE_FT_OTHER_BANK = 217;

	// Consumer Portal Privileges
	public static final String PRIV_HOME_CONSUMER_PORTAL = "UI_HOME_CONSUMER_PORTAL";

	public static final String PRIV_CONSUMER_PORTAL_VIEW_TXN_HISTORY = "UI_CONSUMER_PORTAL_VIEW_TXN_HISTORY";

	public static final String PRIV_CONSUMER_PORTAL_VIEW_PROFILE = "UI_CONSUMER_PORTAL_VIEW_PROFILE";

	public static final String PRIV_CONSUMER_PORTAL_CHANGE_LANGUAGE = "UI_CONSUMER_PORTAL_CHANGE_LANGUAGE";

	public static final String PRIV_CUSTOMER_CHANGE_PIN = "UI_CUSTOMER_CHANGE_PIN";

	public static final String PRIV_CONSUMER_PORTAL_FUND_TRANSFER = "UI_CONSUMER_PORTAL_FUND_TRANSFER";

	public static final String PRIV_CONSUMER_PORTAL_BILL_PAYMENT = "UI_CONSUMER_PORTAL_BILL_PAYMENT";

	public static final String PRIV_CONSUMER_PORTAL_AIRTIME_TOPUP = "UI_CONSUMER_PORTAL_AIRTIME_TOPUP";

	public static final String PRIV_CONSUMER_PORTAL_MANAGE_FAVORITES = "UI_CONSUMER_PORTAL_MANAGE_FAVORITES";

	public static final String PRIV_CONSUMER_PORTAL_MANAGE_SUB_ACCOUNTS = "UI_CONSUMER_PORTAL_MANAGE_SUB_ACCOUNTS";

	public static final String PRIV_CONSUMER_PORTAL_SUB_ACCOUNT_TRANSFER = "UI_CONSUMER_PORTAL_SUB_ACCOUNT_TRANSFER";

	public static final String PRIV_CONSUMER_PORTAL_MANAGE_STANDING_INSTRUCTIONS = "UI_CONSUMER_PORTAL_MANAGE_STANDING_INSTRUCTIONS";

	public static final String PRIV_CONSUMER_PORTAL_VIEW_HELP = "UI_CONSUMER_PORTAL_VIEW_HELP";

	public static final String PRIV_CONSUMER_PORTAL_CHANGE_RECIEPT_MODE = "UI_CONSUMER_PORTAL_CHANGE_RECIEPT_MODE";
	
	public static final String PRIV_CONSUMER_PORTAL_MANUAL_ADVICES = "UI_CONSUMER_PORTAL_MANUAL_ADVICES";

	public static final String PRIV_GENERATE_OTP_PRIVILEGE_MODE = "GENERATE_OTP_PRIVILEGE";

	public static final String PRIV_CHANGE_PASSWORD_EXPIRED = "PRIV_CHANGE_PASSWORD_EXPIRED";

	// Agent Portal Privileges
	public static final String PRIV_HOME_AGENT_PORTAL = "UI_HOME_AGENT_PORTAL";

	public static final String PRIV_AGENT_REGISTRATION = "UI_AGENT_REGISTRATION";

	public static final String PRIV_SUB_AGENT_REGISTRATION = "UI_SUB_AGENT_REGISTRATION";

	public static final String PRIV_AGENT_APPROVAL = "UI_AGENT_APPROVAL";

	public static final String PRIV_AGENT_MANAGE_PROFILE = "UI_AGENT_MANAGE_PROFILE";

	public static final String PRI_AGENT_PORTAL_CASH_IN = "UI_AGENT_PORTAL_CASH_IN";

	public static final String PRIV_AGENT_PORTAL_CASH_OUT = "UI_AGENT_PORTAL_CASH_OUT";

	public static final String PRIV_VIEW_AGENT_PROFILE = "UI_VIEW_AGENT_PROFILE";

	public static final String PRIV_AGENT_CHANGE_PIN = "UI_AGENT_CHANGE_PIN";

	public static final String PRIV_VIEW_AGENT_SUB_AGENT_TXN_HISTORY = "UI_VIEW_AGENT_SUB_AGENT_TXN_HISTORY";

	public static final String PRIV_VIEW_AGENT_TXN_HISTORY = "UI_VIEW_AGENT_TXN_HISTORY";

	public static final String PRIV_AGENT_PORTAL_VIEW_HELP = "UI_AGENT_PORTAL_VIEW_HELP";

	// Privileges End

	public static final String SELECTED_AMOUNT_TYPE_Y = "Y";

	public static final String SELECTED_AMOUNT_TYPE_N = "N";

	// Constants for Bill Payment consumer.
	public static final int FAVOURITE_BILLPAYMENT_ID = 1;

	public static final int FAVOURITE_AIRTIME_ID = 4;

	public static final String RESOURCE_BUNDLE_MSISDN_FAVOURITE_LIST = "favoriteListMsisdn";

	public static final int MULTIPLE_AMOUNT = 100;

	public static final String RESOURCE_BUNDLE_CUSTOMER_STATUS = "customerStatus";

	public static final String RESOURCE_BUNDLE_STATUS = "status";

	public static final String RESOURCE_BUNDLE_BALCK_LIST = "blackListReason";

	public static final String RESOURCE_BUNDLE_ID_BALCK_LIST = "idBlackList";

	public static final String RESOURCE_BUNDLE_ACTIVE_CUST_STATUS = "activeCustomerStatus";

	public static final String RESOURCE_BUNDLE_PENDING_CUST_STATUS = "pendingCustomerStatus";

	public static final String RESOURCE_BUNDLE_TXN_TYPE = "customUseCases";

	public static final String RESOURCE_BUNDLE_TXN_STATUS = "transactionStatus";

	public static final int DEFAULT_MAX_RESULT = 20;

	public static final String FUND_TRANSFER_ISSUER_BANK = "213";

	public static final String FUND_TRANSFER_DESTINATION_BANK = "213";

	public static final int SEARCH_CUSTOMERS_COUNT = 1;

	// Validate MSISDN
	public static final int MSISDN_UNREGISTERED = 89;

	public static final int MSISDN_NOT_APPROVED = 90;

	public static final int MSISDN_INACTIVE = 91;

	public static final int MSISDN_SUSPENDED = 92;

	public static final int MSISDN_CLOSED = 93;

	public static final int MSISDN_NOT_APPROVED_BY_OPTIMA = 94;

	public static final int INVALID_MSISDN_RESPONSE_CODE = 99;

	public static final int VALID_CUSTOMER_MSISDN_RESPONSE_CODE = 0;

	public static final int VALID_TOP_AGENT_MSISDN_RESPONSE_CODE = 2;

	public static final int VALID_CHILD_AGENT_MSISDN_RESPONSE_CODE = 3;

	public static final int VALID_SUB_AGENT_MSISDN_RESPONSE_CODE = 4;

	public static final int VALID_OTW_MSISDN_RESPONSE_CODE = 4;

	// Favorite Types
	public static final int FAVOURITE_TYPE_BILLER_ACCOUNT = 1;

	public static final int FAVOURITE_TYPE_FF_ACCOUNT = 2;

	public static final int FAVOURITE_TYPE_BTPN_ACCOUNT = 3;

	public static final int FAVOURITE_TYPE_TOPUP_ACCOUNT = 4;

	public static final int FAVOURITE_TYPE_OTHER_BANK_ACCOUNT = 5;

	public static final int PRODUCT_CATEGORY = 2;

	public static final int IDENTIFICATION_TYPE = 0;

	public static final int IDENTIFICATION_STATUS = 0;

	public static final String TRANSACTION_FILE_NAME = "transactions.csv";

	public static final String TRANSACTION_PDF_FILE_NAME = "Transactions.pdf";

	public static final String PDF_CONTENT_TYPE = "application/x-pdf";

	public static final String STATUS_PENDING_APPROVAL = "PENDING APPROVAL";

	public static final String FILE_CSV_EXTENSION = "csv";

	public static final String CUSTOMER_STATUS_ACTIVE = "ACTIVE";

	public static final String CUSTOMER_STATUS_INACTIVE = "INACTIVE";

	public static final String CUSTOMER_STATUS_SUSPEND = "SUSPEND";

	public static final String CUSTOMER_STATUS_PENDING_APPROVAL = "PENDINGAPPROVAL";

	public static final String CUSTOMER_STATUS_CLOSED = "CLOSED";

	public static final int BLACKLISTREASON_FRAUD_SUSPICION = 4;

	public static final int BLACKLISTREASON_PENDING_APPROVAL = 6;

	public static final int BLACKLISTREASON_INACTIVE = 9;

	public static final Long SUB_ACCOUNT_BALANCE = 0L;

	public static final String CUSTOMER_STATUS_PENDING = "Pending Approval";

	public static final String CUSTOMER_STATUS_APPROVED = "Approved";

	public static final String PRIV_EMPLOYEE_TXN_HISTORY_VIEW = "UI_EMPLOYEE_TXN_HISTORY_FULL_VIEW";

	public static final int PRODUCT_CATEGORY_ZERO = 0;

	public static final String SI_BILLER_TYPE_TELCO = "TELCO";

	public static final String CHANGE_MSISDN_STATUS = "PENDING";

	// Constants for Transaction Details report link.
	public static final String RESOURCE_BUNDLE_TRANSACTION_DETAILS_REPORT_TYPE = "transDetailsReporttype";

	public static final String RESOURCE_BUNDLE_TRANSACTION_DETAILS_TRANSACTION_TYPE = "agentUseCases";

	public static final int PRODUCT_TYPE_ZERO = 0;

	public static final int PRODUCT_TYPE_ONE = 1;

	public static final int PRODUCT_TYPE_TWO = 2;

	public static final int PRODUCT_TYPE_THREE = 3;

	public static final int PRODUCT_TYPE_FOUR = 4;

	public static final List<Integer> CUSTOMER_TYPES_TRANSACTION_LINK = Arrays.asList(20, 97, 98);

	public static final String RESET_PIN_PRIV = "UI_BANK_PORTAL_RESET_PIN";
	
	public static final String DEFAULT_PLN_PREPAID = "91901"; 
	
	public static final String DEFAULT_PLN_NOS_FOR_CUSTOMER_NAME = "91901,91951,91999";
	
	public static final String RESOURCE_BUNDLE_CUSTOMER_TYPE = "customertypes";
	public static final String RESOURCE_BUNDLE_PI_TYPE = "pitypes";
	public static final String RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE = "customerCustomerTypes";
	public static final String RESOURCE_USE_CASE_FEE_ORG_UNIT = "orgunits";
	public static final String RESOURCE_USE_CASE_FEE_CURRENCY = "currencies";
	
	public static final String APPROVED = "approve";
	public static final String REJECT = "reject";
	public static final String ADD = "add";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";
	


}
