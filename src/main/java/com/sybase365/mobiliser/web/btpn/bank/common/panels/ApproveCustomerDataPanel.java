package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Address;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Customer;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerAttributes;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.CustomerIdentification;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.customer.Identity;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.editprofile.PendingApprovalEditProfile;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.editprofile.PendingApprovalEditProfileDetail;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.editprofile.FindPendingApprovalEditProfileRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.editprofile.FindPendingApprovalEditProfileResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.editprofile.GetPendingEditProfileDetailsRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.editprofile.GetPendingEditProfileDetailsResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveCustomerDataBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerDataBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ApproveCustomerDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.approval.ApproveCustomerDataView;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class ApproveCustomerDataPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ApproveCustomerDataPanel.class);

	protected BtpnMobiliserBasePage basePage;

	private FeedbackPanel feedBack;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK_NAME = "detailsLinkName";

	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";

	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";

	private int approvalStartIndex = 0;

	private int approvalEndIndex = 0;

	private String approvalTotalItemString;

	private Label approvalHeader;

	private Label noRecordsLabel;

	private BtpnCustomPagingNavigator navigator;

	public ApproveCustomerDataPanel(String id) {
		super(id);
	}

	public ApproveCustomerDataPanel(String id, CustomerDataBean customerDataBean, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	public void constructPanel() {
		Form<ApproveCustomerDataPanel> form = new Form<ApproveCustomerDataPanel>("customerDataPanel",
			new CompoundPropertyModel<ApproveCustomerDataPanel>(ApproveCustomerDataPanel.class));

		feedBack = new FeedbackPanel("errorMessages");
		feedBack.setOutputMarkupId(true);
		feedBack.setOutputMarkupPlaceholderTag(true);
		form.add(feedBack);

		String message = getLocalizer().getString("label.noDataFound", ApproveCustomerDataPanel.this);
		noRecordsLabel = new Label("emptyRecordsMessage", message);
		noRecordsLabel.setOutputMarkupId(true);
		noRecordsLabel.setOutputMarkupPlaceholderTag(true);
		noRecordsLabel.setVisible(false);
		form.add(noRecordsLabel);

		List<CustomerDataBean> customerDataList = getAprroveCustomerDataList();
		if (!PortalUtils.exists(customerDataList)) {
			noRecordsLabel.setVisible(true);
		} else {
			noRecordsLabel.setVisible(false);
		}

		// Add the approval container
		final WebMarkupContainer customerListConainer = new WebMarkupContainer("customerListContainer");
		createCustomerList(customerListConainer);
		customerListConainer.setOutputMarkupId(true);
		form.add(customerListConainer);
		add(form);
	}

	protected void createCustomerList(final WebMarkupContainer dataViewContainer) {
		// Create the approval View
		final ApproveCustomerDataProvider approveCustomerDataProvider = new ApproveCustomerDataProvider("fromDate",
			getAprroveCustomerDataList());

		final DataView<CustomerDataBean> dataView = new ApproveCustomerViewData(WICKET_ID_PAGEABLE,
			approveCustomerDataProvider);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approveCustomerDataProvider.size() != 0;
			}

		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				final String displayTotalItemsText = ApproveCustomerDataPanel.this.getLocalizer().getString(
					"approval.totalitems.header", ApproveCustomerDataPanel.this);
				return String.format(displayTotalItemsText, approvalTotalItemString, approvalStartIndex,
					approvalEndIndex);
			}

		};
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approveCustomerDataProvider.size() != 0;

			}
		};
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
		dataViewContainer.add(new OrderByBorder("orderByCreatedBy", "createdBy", approveCustomerDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});
		dataViewContainer.add(new OrderByBorder("orderByCustomer", "customer", approveCustomerDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.add(new OrderByBorder("orderByStatus", "status", approveCustomerDataProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSortChanged() {
				dataView.setCurrentPage(0);
			}
		});

		dataViewContainer.addOrReplace(dataView);
	}

	private class ApproveCustomerViewData extends DataView<CustomerDataBean> {
		private static final long serialVersionUID = 1L;

		protected ApproveCustomerViewData(String id, IDataProvider<CustomerDataBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);

		}

		@Override
		protected void onBeforeRender() {
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<CustomerDataBean> item) {

			final CustomerDataBean entry = item.getModelObject();

			item.add(new Label("createdBy", entry.getCreatedBy()));
			item.add(new Label("customer", entry.getCustomer()));
			item.add(new Label("status", entry.getStatus()));

			Link<CustomerDataBean> detailsLink = new Link<CustomerDataBean>("detailsLink", item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					CustomerDataBean customerBean = (CustomerDataBean) item.getModelObject();
					ApproveCustomerDataBean approveBean = getCustomerDetails(customerBean);
					setResponsePage(new ApproveCustomerDataView(approveBean, basePage));
				}
			};
			detailsLink.add(new Label(WICKET_ID_LINK_NAME, getLocalizer().getString("Details", this)));
			item.add(detailsLink);
			// Add the UseCase

			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((ApproveCustomerDataProvider) internalGetDataProvider()).size() != 0;

		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			approvalTotalItemString = new Integer(size).toString();
			if (size > 0) {
				approvalStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				approvalEndIndex = approvalStartIndex + getItemsPerPage() - 1;
				if (approvalEndIndex > size) {
					approvalEndIndex = size;
				}
			} else {
				approvalStartIndex = 0;
				approvalEndIndex = 0;
			}
		}
	}

	/**
	 * This is used to fetch the pending approval edit customer details.
	 * 
	 * @return CustomerDataBean used for approve customer data.
	 */
	public List<CustomerDataBean> getAprroveCustomerDataList() {
		List<CustomerDataBean> customerDataList = new ArrayList<CustomerDataBean>();
		try {
			FindPendingApprovalEditProfileRequest request = basePage
				.getNewMobiliserRequest(FindPendingApprovalEditProfileRequest.class);
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCheckerId(customerId);
			FindPendingApprovalEditProfileResponse response = basePage.getEditProfileClient()
				.findPendingApprovalEditProfile(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
				List<PendingApprovalEditProfile> customers = response.getCustomers();
				for (PendingApprovalEditProfile bean : customers) {
					final CustomerDataBean customerBean = new CustomerDataBean();
					customerBean.setCreatedBy(bean.getCreatedBy());
					customerBean.setCustomer(bean.getUsername());
					customerBean.setStatus(bean.getStatus());
					customerBean.setTaskId(bean.getTaskId());
					customerDataList.add(customerBean);
				}
			}
		} catch (Exception ex) {
			LOG.error("Error occured while calling findPendingApprovalEditProfile service from Edit Profile Endpoint");
		}
		return customerDataList;
	}

	public ApproveCustomerDataBean getCustomerDetails(CustomerDataBean customerBean) {
		ApproveCustomerDataBean bean = new ApproveCustomerDataBean();
		try {
			GetPendingEditProfileDetailsRequest request = basePage
				.getNewMobiliserRequest(GetPendingEditProfileDetailsRequest.class);
			long checkerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			request.setCheckerId(checkerId);
			request.setTaskId(customerBean.getTaskId());
			GetPendingEditProfileDetailsResponse response = basePage.getEditProfileClient()
				.getPendingEditProfileDetails(request);
			if (basePage.evaluateBankPortalMobiliserResponse(response)
					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {

				// Populate Current Profile Values
				ApproveCustomerDataBean currentValue = populateCurrentProfile(response.getCustomer());
				bean.setCurrentValue(currentValue);
				bean.setTaskId(response.getCustomer().getTaskId());

				// Populate New Profile Values
				ApproveCustomerDataBean newValue = populateNewProfileValues(response.getCustomer());
				bean.setNewValue(newValue);
			}
		} catch (Exception ex) {
			LOG.error("Error occured while calling findPendingApprovalEditProfile service from Edit Profile Endpoint", ex);
			error(getLocalizer().getString("error.exception", this));
		}
		return bean;
	}

	private ApproveCustomerDataBean populateCurrentProfile(PendingApprovalEditProfileDetail customer) {

		ApproveCustomerDataBean currentValue = new ApproveCustomerDataBean();

		Customer currentProfile = customer.getCurrentProfile();
		Address currentAddress = currentProfile.getAddress();

		String currentBlackListReason = basePage.getDisplayValue(String.valueOf(currentProfile.getBlackListReason()),
			BtpnConstants.RESOURCE_BUNDLE_ID_BALCK_LIST);

		String currentDateOfBirth = PortalUtils.getMMDDYYYYDate(currentProfile.getDateOfBirth(), null);

		String currentGender = basePage.getDisplayValue(String.valueOf(currentAddress.getGender()),
			BtpnConstants.RESOURCE_BUNDLE_GENDERS);
		List<CustomerIdentification> identifications = currentProfile.getIdentifications();
		for (CustomerIdentification identification : identifications) {
			if (identification.getType() == BtpnConstants.IDENTIFICATION_TYPE_ATM_CARD_NO) {
				currentValue.setAtmCardNo(identification.getIdentification());
			}
		}
		currentValue.setBalckListReason(currentBlackListReason);
		currentValue.setDateOfBirth(currentDateOfBirth);
		currentValue.setEmailId(currentAddress.getEmail());
		currentValue.setEmployeeId(currentAddress.getEmployeeID());
		currentValue.setGender(currentGender);
		if (currentProfile.isHighRiskBusiness()) {
			currentValue.setHighRiskBusiness(BtpnConstants.YES_ID);
		} else {
			currentValue.setHighRiskBusiness(BtpnConstants.NO_ID);
		}
		if (currentProfile.isHighRiskCustomer()) {
			currentValue.setHighRiskCustomer(BtpnConstants.YES_ID);
		} else {
			currentValue.setHighRiskCustomer(BtpnConstants.NO_ID);
		}
		if (currentProfile.isIsOptimaActivated()) {
			currentValue.setTaxExempted(BtpnConstants.YES_ID);
		} else {
			currentValue.setTaxExempted(BtpnConstants.NO_ID);
		}
		Identity identity = currentProfile.getIdentities().get(0);
		currentValue.setIdCardNo(identity.getIdentity());
		currentValue.setIdType(basePage.getDisplayValue(String.valueOf(identity.getIdentityType()),
			BtpnConstants.RESOURCE_BUBDLE_ID_TYPE));
		String currentExpirationDate = PortalUtils.getMMDDYYYYDate(identity.getDateExpires(), null);
		currentValue.setIdExpirationDate(currentExpirationDate);
		currentValue.setIncome(basePage.getDisplayValue(String.valueOf(currentAddress.getIncome()),
			BtpnConstants.RESOURCE_BUBDLE_INCOME));
		currentValue.setIndustryOfEmployer(basePage.getDisplayValue(
			String.valueOf(currentAddress.getIndustrySectorOfEmployer()),
			BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE));
		currentValue.setJob(basePage.getDisplayValue(String.valueOf(currentAddress.getJob()),
			BtpnConstants.RESOURCE_BUBDLE_JOB));
		currentValue.setLanguage(currentProfile.getLanguage());
		currentValue.setMaritalStatus(basePage.getDisplayValue(String.valueOf(currentProfile.getMaritalStatus()),
			BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS));
		currentValue.setMothersMaidenName(currentAddress.getMaidenName());
		currentValue.setName(currentProfile.getName());
		currentValue.setNationality(currentProfile.getNationality());
		currentValue.setOccupation(basePage.getDisplayValue(String.valueOf(currentAddress.getOccupation()),
			BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT));
		currentValue.setPlaceOfBirth(currentProfile.getPlaceOfBirth());
		currentValue.setPurposeOfAccount(basePage.getDisplayValue(String.valueOf(currentAddress.getPurposeOfAccount()),
			BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT));
		currentValue.setReceiptMode(basePage.getDisplayValue(String.valueOf(currentProfile.getNotificationMode()),
			BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE));
		currentValue.setShortName(currentAddress.getShortName());
		currentValue.setSourceofFound(basePage.getDisplayValue(String.valueOf(currentAddress.getSourceOfFund()),
			BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND));
		currentValue.setStreet1(currentAddress.getStreet1());
		currentValue.setStreet2(currentAddress.getStreet2());
		currentValue.setGlCode(currentProfile.getGlCode());
		int blackListReason = currentProfile.getBlackListReason();
		// BOL_IS_ACTIVE Flag from DB
		boolean bolIsActive = currentProfile.isActive();
		// Customer status attribute 19
		String customerStatusAttribute = currentProfile.getCustomerStatus();
		if (bolIsActive) {// Check for BOL_IS_ACTIVE if its Y then					
			if (blackListReason == BtpnConstants.BLACKLISTREASON_ZERO
					&& customerStatusAttribute.equalsIgnoreCase(BtpnConstants.CUSTOMER_STATUS_APPROVED)) {
				// If blacklist reason is zero, customer status attribute is approved set customer status to active.
				currentValue.setStatus(
					basePage.getDisplayValue(BtpnConstants.CUSTOMER_STATUS_ACTIVE,
						BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS));
			} else if (customerStatusAttribute.equalsIgnoreCase(BtpnConstants.CUSTOMER_STATUS_PENDING)) {
				//If customer status attribute is Pending Approval
				currentValue.setStatus(
					basePage.getDisplayValue(BtpnConstants.CUSTOMER_STATUS_PENDING_APPROVAL,
						BtpnConstants.RESOURCE_BUNDLE_PENDING_CUST_STATUS));
			} else if (blackListReason == BtpnConstants.BLACKLISTREASON_INACTIVE) {
				// If blacklist reason is 9, set customer status to InActive.
				currentValue.setStatus(basePage.getDisplayValue(BtpnConstants.CUSTOMER_STATUS_INACTIVE,
						BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS));
			}  else {
				// If blacklist reason is other than 0 or 9, set customer status to Suspend.
				currentValue.setStatus(basePage.getDisplayValue(BtpnConstants.CUSTOMER_STATUS_SUSPEND,
						BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS));
			}

		} else {// Check for BOL_IS_ACTIVE if its N then Customer status is closed.
			currentValue.setStatus(basePage.getDisplayValue(
				BtpnConstants.CUSTOMER_STATUS_CLOSED, BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS));
		}
		final CustomerAttributes attributes = currentProfile.getAttributes();
		if (attributes != null) {
			currentValue.setAgentCode(attributes.getAgentId());
			// Set Province
			final String province = attributes.getProvince();
			if (PortalUtils.exists(province)) {
				currentValue.setProvince(new CodeValue(province, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, this),
					BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE + "." + province)));
			}
			// Set City Attribute
			final String city = attributes.getCity();
			if (PortalUtils.exists(city)) {
				currentValue.setCity(new CodeValue(city, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(province, this), province + "." + city)));
			}
			// set Forecast
			final String forecast = attributes.getForecastTransaction();
			if (PortalUtils.exists(forecast)) {
				currentValue.setForeCastTransaction(new CodeValue(forecast, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS,
						this), BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS + "." + forecast)));
			}
			// Last Education
			final String lastEducation = attributes.getLastEducation();
			if (PortalUtils.exists(lastEducation)) {
				currentValue.setLastEducation(new CodeValue(lastEducation, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, this),
					BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS + "." + lastEducation)));
			}
			// Religion
			final String religion = attributes.getReligion();
			if (PortalUtils.exists(religion)) {
				currentValue.setReligion(new CodeValue(religion, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_RELIGION, this),
					BtpnConstants.RESOURCE_BUNDLE_RELIGION + "." + religion)));
			}
			currentValue.setMarketingSourceCode(attributes.getMarketingsourceCode());
			currentValue.setReferralNumber(attributes.getReferralNumber());
			currentValue.setTaxCardNumber(attributes.getTaxCardNumber());
			currentValue.setZipCode(attributes.getZipCode());
		}
		return currentValue;
	}

	private ApproveCustomerDataBean populateNewProfileValues(PendingApprovalEditProfileDetail customer) {

		ApproveCustomerDataBean newValue = new ApproveCustomerDataBean();

		Customer newProfile = customer.getNewProfile();
		Address newAddress = newProfile.getAddress();

		String newBlackListReason = basePage.getDisplayValue(String.valueOf(newProfile.getBlackListReason()),
			BtpnConstants.RESOURCE_BUNDLE_ID_BALCK_LIST);
		String newDateOfBirth = PortalUtils.getMMDDYYYYDate(newProfile.getDateOfBirth(), null);
		String newGender = basePage.getDisplayValue(String.valueOf(newAddress.getGender()),
			BtpnConstants.RESOURCE_BUNDLE_GENDERS);
		List<CustomerIdentification> identifications = newProfile.getIdentifications();
		for (CustomerIdentification identification : identifications) {
			if (identification.getType() == BtpnConstants.IDENTIFICATION_TYPE_ATM_CARD_NO) {
				newValue.setAtmCardNo(identification.getIdentification());
			}
		}
		newValue.setBalckListReason(newBlackListReason);
		newValue.setDateOfBirth(newDateOfBirth);
		newValue.setEmailId(newAddress.getEmail());
		newValue.setEmployeeId(newAddress.getEmployeeID());
		newValue.setGender(newGender);
		if (newProfile.isHighRiskBusiness()) {
			newValue.setHighRiskBusiness(BtpnConstants.YES_ID);
		} else {
			newValue.setHighRiskBusiness(BtpnConstants.NO_ID);
		}
		if (newProfile.isHighRiskCustomer()) {
			newValue.setHighRiskCustomer(BtpnConstants.YES_ID);
		} else {
			newValue.setHighRiskCustomer(BtpnConstants.NO_ID);
		}
		if (newProfile.isIsOptimaActivated()) {
			newValue.setTaxExempted(BtpnConstants.YES_ID);
		} else {
			newValue.setTaxExempted(BtpnConstants.NO_ID);
		}
		newValue.setIncome(basePage.getDisplayValue(String.valueOf(newAddress.getIncome()),
			BtpnConstants.RESOURCE_BUBDLE_INCOME));
		newValue.setIndustryOfEmployer(basePage.getDisplayValue(
			String.valueOf(newAddress.getIndustrySectorOfEmployer()),
			BtpnConstants.RESOURCE_BUNDLE_INDUSTRY_OF_EMPLOYEE));
		Identity identity = newProfile.getIdentities().get(0);
		newValue.setIdCardNo(identity.getIdentity());
		newValue.setIdType(basePage.getDisplayValue(String.valueOf(identity.getIdentityType()),
			BtpnConstants.RESOURCE_BUBDLE_ID_TYPE));
		String newExpirationDate = PortalUtils.getMMDDYYYYDate(identity.getDateExpires(), null);
		newValue.setIdExpirationDate(newExpirationDate);

		newValue
			.setJob(basePage.getDisplayValue(String.valueOf(newAddress.getJob()), BtpnConstants.RESOURCE_BUBDLE_JOB));
		newValue.setLanguage(newProfile.getLanguage());
		newValue.setMaritalStatus(basePage.getDisplayValue(String.valueOf(newProfile.getMaritalStatus()),
			BtpnConstants.RESOURCE_BUNDLE_MARITAL_STATUS));
		newValue.setMothersMaidenName(newAddress.getMaidenName());
		newValue.setName(newProfile.getName());
		newValue.setNationality(newProfile.getNationality());
		newValue.setOccupation(basePage.getDisplayValue(String.valueOf(newAddress.getOccupation()),
			BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT));
		newValue.setPlaceOfBirth(newProfile.getPlaceOfBirth());
		newValue.setPurposeOfAccount(basePage.getDisplayValue(String.valueOf(newAddress.getPurposeOfAccount()),
			BtpnConstants.RESOURCE_BUBDLE_PURPOSE_OF_ACCOUNT));
		newValue.setReceiptMode(basePage.getDisplayValue(String.valueOf(newProfile.getNotificationMode()),
			BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE));
		newValue.setShortName(newAddress.getShortName());
		newValue.setSourceofFound(basePage.getDisplayValue(String.valueOf(newAddress.getSourceOfFund()),
			BtpnConstants.RESOURCE_BUNDLE_SOURCE_OF_FOUND));
		newValue.setStreet1(newAddress.getStreet1());
		newValue.setStreet2(newAddress.getStreet2());
		newValue.setGlCode(newProfile.getGlCode());
		int blackListReason = newProfile.getBlackListReason();
		// BOL_IS_ACTIVE Flag from DB
		boolean bolIsActive = newProfile.isActive();
		// Customer status attribute 19
		String customerStatusAttribute = customer.getCurrentProfile().getCustomerStatus();
		if (bolIsActive) {// Check for BOL_IS_ACTIVE if its Y then					
			if (blackListReason == BtpnConstants.BLACKLISTREASON_ZERO
					&& customerStatusAttribute.equalsIgnoreCase(BtpnConstants.CUSTOMER_STATUS_APPROVED)) {
				// If blacklist reason is zero, customer status attribute is approved set customer status to active.
				newValue.setStatus(
					basePage.getDisplayValue(BtpnConstants.CUSTOMER_STATUS_ACTIVE,
						BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS));
			} else if (customerStatusAttribute.equalsIgnoreCase(BtpnConstants.CUSTOMER_STATUS_PENDING)) {
				//If customer status attribute is Pending Approval
				newValue.setStatus(
					basePage.getDisplayValue(BtpnConstants.CUSTOMER_STATUS_PENDING_APPROVAL,
						BtpnConstants.RESOURCE_BUNDLE_PENDING_CUST_STATUS));
			} else if (blackListReason == BtpnConstants.BLACKLISTREASON_INACTIVE) {
				// If blacklist reason is 9, set customer status to InActive.
				newValue.setStatus(basePage.getDisplayValue(BtpnConstants.CUSTOMER_STATUS_INACTIVE,
						BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS));
			}  else {
				// If blacklist reason is other than 0 or 9, set customer status to Suspend.
				newValue.setStatus(basePage.getDisplayValue(BtpnConstants.CUSTOMER_STATUS_SUSPEND,
						BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS));
			}

		} else {// Check for BOL_IS_ACTIVE if its N then Customer status is closed.
			newValue.setStatus(basePage.getDisplayValue(
				BtpnConstants.CUSTOMER_STATUS_CLOSED, BtpnConstants.RESOURCE_BUNDLE_ACTIVE_CUST_STATUS));
		}
		// Set Customer Attributes
		final CustomerAttributes attributes = newProfile.getAttributes();
		if (attributes != null) {
			newValue.setAgentCode(attributes.getAgentId());
			// Set Province
			final String province = attributes.getProvince();
			if (PortalUtils.exists(province)) {
				newValue.setProvince(new CodeValue(province, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE, this),
					BtpnConstants.RESOURCE_BUNDLE_FORECAST_PROVINCE + "." + province)));
			}
			// Set City Attribute
			final String city = attributes.getCity();
			if (PortalUtils.exists(city)) {
				newValue.setCity(new CodeValue(city, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(province, this), province + "." + city)));
			}
			// set Forecast
			final String forecast = attributes.getForecastTransaction();
			if (PortalUtils.exists(forecast)) {
				newValue.setForeCastTransaction(new CodeValue(forecast, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS,
						this), BtpnConstants.RESOURCE_BUNDLE_FORECAST_TRANSACTIONS + "." + forecast)));
			}
			// Last Education
			final String lastEducation = attributes.getLastEducation();
			if (PortalUtils.exists(lastEducation)) {
				newValue.setLastEducation(new CodeValue(lastEducation, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS, this),
					BtpnConstants.RESOURCE_BUNDLE_LAST_EDUCATIONS + "." + lastEducation)));
			}
			// Religion
			final String religion = attributes.getReligion();
			if (PortalUtils.exists(religion)) {
				newValue.setReligion(new CodeValue(religion, BtpnUtils.getDropdownValueFromId(
					basePage.lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUNDLE_RELIGION, this),
					BtpnConstants.RESOURCE_BUNDLE_RELIGION + "." + religion)));
			}
			newValue.setMarketingSourceCode(attributes.getMarketingsourceCode());
			newValue.setReferralNumber(attributes.getReferralNumber());
			newValue.setTaxCardNumber(attributes.getTaxCardNumber());
			newValue.setZipCode(attributes.getZipCode());
		}
		return newValue;
	}
}
