package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.api.CustomerWrkFacade;
import com.btpnwow.core.customer.facade.api.UserWrkFacade;
import com.btpnwow.core.customer.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.customer.facade.contract.CustomerWrkFindViewType;
import com.btpnwow.core.customer.facade.contract.FindCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.FindCustomerExWrkResponse;
import com.btpnwow.core.customer.facade.contract.FindUserWrkRequest;
import com.btpnwow.core.customer.facade.contract.FindUserWrkResponse;
import com.btpnwow.core.customer.facade.contract.GetCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.GetCustomerExResponse;
import com.btpnwow.core.customer.facade.contract.GetCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.GetCustomerExWrkResponse;
import com.btpnwow.core.customer.facade.contract.GetUserWrkRequest;
import com.btpnwow.core.customer.facade.contract.GetUserWrkResponse;
import com.btpnwow.core.customer.facade.contract.UserWrkFindViewType;
import com.btpnwow.portal.bank.converter.BankStaffBeanConverter;
import com.btpnwow.portal.bank.converter.CustomerRegistrationBeanConverter;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.ApproveCustomerBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ApprovalDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.approval.ApproveCustomerDataView;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankStaffConfirmApprovalPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.ConsumerTopAgentConfirmApprovalPage;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ConsumerApprovalPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankCheckerApprovalPanel extends Panel { 

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankCheckerApprovalPanel.class);

	protected BtpnMobiliserBasePage basePage;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK = "detailsLink";

	private static final String WICKET_ID_LINK_NAME = "linkName";

	private static final String WICKET_ID_approvalNavigator = "approvalNavigator";

	private static final String WICKET_ID_approvalTotalItems = "approvalHeader";
	
	@SpringBean(name = "btpnLookupMapUtilitiesImpl")
	public ILookupMapUtility lookupMapUtility;

	@SpringBean(name = "customerFacade")
	private CustomerFacade customerFacade;

	@SpringBean(name = "customerWrkFacade")
	private CustomerWrkFacade customerWrkFacade;

	@SpringBean(name = "userWrkFacade")
	private UserWrkFacade userWrkFacade;

	private String approvalTotalItemString;

	private int approvalStartIndex = 0;

	private int approvalEndIndex = 0;

	private Label approvalHeader;

	private BtpnCustomPagingNavigator navigator;

//	private String customerType;
	
	private boolean officerApproval;

	private Component feedbackPanel;

	private Long callerId;

	public BankCheckerApprovalPanel(String id, BtpnMobiliserBasePage basePage, final String customerType) {
		super(id);
		
		this.basePage = basePage;
		
		this.officerApproval = BtpnConstants.REG_TOPUP_AGENT.equals(customerType);
		
//		this.customerType = customerType;
//		LOG.debug("ConsumerRegistrationPanel Started.");
//		Customer customer = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
//		customerId = customer.getCustomerId();
		
		callerId = Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
		
		constructPanel();
	}

	protected void constructPanel() {
		final Form<BankCheckerApprovalPanel> form = new Form<BankCheckerApprovalPanel>(
				"approvalConsumerForm", new CompoundPropertyModel<BankCheckerApprovalPanel>(this));
		
		form.add(feedbackPanel = new FeedbackPanel("errorMessages").setOutputMarkupId(true));
		
		// Add the approval attachemnts container
		final WebMarkupContainer approvalContainer = new WebMarkupContainer("pendingApprovalContainer");
		notificationapprovalsDataView(approvalContainer);
		approvalContainer.setOutputMarkupId(true);
		
		form.add(approvalContainer);
		add(form);
	}

	/**
	 * This method adds the approvalsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void notificationapprovalsDataView(final WebMarkupContainer dataViewContainer) {
		// Fetch the approval data from the service
		
		List<ApproveCustomerBean> list = new ArrayList<ApproveCustomerBean>();
		if (officerApproval) {
			list.addAll(fetchPendingUserApprovalList());
		}
		list.addAll(fetchPendingConsumerApprovalList());
		
		// Create the approval View
		final ApprovalDataProvider approvalDataProvider = new ApprovalDataProvider("date", list);

		final DataView<ApproveCustomerBean> dataView = new ApprovalDataView(WICKET_ID_PAGEABLE, approvalDataProvider);
		dataView.setItemsPerPage(20);

		dataViewContainer.add(new Label("no.items",
				getLocalizer().getString("no.items", this))
					.setRenderBodyOnly(true)
					.setVisible(approvalDataProvider.size() == 0));

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_approvalNavigator, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approvalDataProvider.size() != 0;
			}
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		
		dataViewContainer.add(navigator);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			public String getObject() {
				final String displayTotalItemsText = BankCheckerApprovalPanel.this.getLocalizer().getString(
					"approval.totalitems.header", BankCheckerApprovalPanel.this);
				
				return String.format(
						displayTotalItemsText,
						approvalTotalItemString,
						Integer.valueOf(approvalStartIndex), Integer.valueOf(approvalEndIndex));
			}
		};
		
		approvalHeader = new Label(WICKET_ID_approvalTotalItems, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return approvalDataProvider.size() != 0;
			}
		};
		
		dataViewContainer.add(approvalHeader);
		approvalHeader.setOutputMarkupId(true);
		approvalHeader.setOutputMarkupPlaceholderTag(true);

		// Add the sort providers
//		dataViewContainer.add(new OrderByBorder("orderByDate", "date", approvalDataProvider) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void onSortChanged() {
//				dataView.setCurrentPage(0);
//			}
//		});
//		dataViewContainer.add(new OrderByBorder("orderByRequestType", "requestType", approvalDataProvider) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void onSortChanged() {
//				dataView.setCurrentPage(0);
//			}
//		});
//		dataViewContainer.add(new OrderByBorder("orderByConsumerType", "consumerType", approvalDataProvider) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void onSortChanged() {
//				dataView.setCurrentPage(0);
//			}
//		});
//		dataViewContainer.add(new OrderByBorder("orderByMobileNumber", "mobileNumber", approvalDataProvider) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void onSortChanged() {
//				dataView.setCurrentPage(0);
//			}
//		});
//		dataViewContainer.add(new OrderByBorder("orderByStatus", "status", approvalDataProvider) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void onSortChanged() {
//				dataView.setCurrentPage(0);
//			}
//		});
//		dataViewContainer.add(new OrderByBorder("orderByCreatedBy", "createdBy", approvalDataProvider) {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void onSortChanged() {
//				dataView.setCurrentPage(0);
//			}
//		});

		dataViewContainer.addOrReplace(dataView);
	}

	private List<ApproveCustomerBean> fetchPendingUserApprovalList() {
		List<ApproveCustomerBean> list = new ArrayList<ApproveCustomerBean>();
		
		try {
			// Prepare the pending request
			FindUserWrkRequest request = MobiliserUtils.fill(new FindUserWrkRequest(), basePage);
			request.setCallerId(callerId);
			request.setDateRangeBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(new Date(Long.MIN_VALUE), TimeZone.getDefault()));
			request.setDateRangeEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(new Date(Long.MAX_VALUE), TimeZone.getDefault()));
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			
			FindUserWrkResponse response = userWrkFacade.findWrk(request);
			
			if (MobiliserUtils.success(response)) {
				List<UserWrkFindViewType> items = response.getItem();
				
				if ((items != null) && !items.isEmpty()) {
					for (UserWrkFindViewType e : items) {
						ApproveCustomerBean bean = new ApproveCustomerBean();
						bean.setConsumerType(MobiliserUtils.getValue("customertypes", Integer.toString(e.getCustomerType()), lookupMapUtility, this));
						bean.setCustomerTypeCategory(e.getCustomerTypeCategory());
						bean.setCreatedBy(e.getLastModifiedById().toString().concat("|").concat(e.getLastModifiedByName()));
						bean.setDate(PortalUtils.getSaveDate(e.getLastModifiedDate()));
						bean.setMobileNumber(e.getUserId().concat("|").concat(e.getName()));
						bean.setRequestType("");
						bean.setStatus(BtpnConstants.STATUS_PENDING_APPROVAL);
						bean.setTaskId(e.getWorkflowId());
						
						list.add(bean);
					}
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Throwable e) {
			LOG.error("An exception was thrown.", e);
			
			error(getLocalizer().getString("pending.failure.exception", this));
			
			list.clear();
		}
		
		return list;
	}

	/**
	 * This fetches the pending approval details
	 */
	private List<ApproveCustomerBean> fetchPendingConsumerApprovalList() {
		List<ApproveCustomerBean> list = new ArrayList<ApproveCustomerBean>();
		
		try {
			// Prepare the pending request
			FindCustomerExWrkRequest request = MobiliserUtils.fill(new FindCustomerExWrkRequest(), basePage);
			request.setCallerId(callerId);
			request.setDateRangeBegin(PortalUtils.getSaveXMLGregorianCalendarFromDate(new Date(Long.MIN_VALUE), TimeZone.getDefault()));
			request.setDateRangeEnd(PortalUtils.getSaveXMLGregorianCalendarToDate(new Date(Long.MAX_VALUE), TimeZone.getDefault()));
			request.setStart(0);
			request.setLength(Integer.MAX_VALUE);
			
			FindCustomerExWrkResponse response = customerWrkFacade.findWrk(request);
			
			if (MobiliserUtils.success(response)) {
				List<CustomerWrkFindViewType> items = response.getItem();
				
				if ((items != null) && !items.isEmpty()) {
					for (CustomerWrkFindViewType e : items) {
						if (officerApproval) {
							switch (e.getCustomerTypeCategory().intValue()) {
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
								break;
							default:
								continue;
							}
						}
						
						ApproveCustomerBean bean = new ApproveCustomerBean();
						bean.setConsumerType(MobiliserUtils.getValue("customertypes", e.getCustomerType().toString(), lookupMapUtility, this));
						bean.setCustomerTypeCategory(e.getCustomerTypeCategory().intValue());
						bean.setCreatedBy(e.getLastModifiedById().toString().concat("|").concat(e.getLastModifiedByName()));
						bean.setDate(PortalUtils.getSaveDate(e.getLastModifiedDate()));
						bean.setMobileNumber(e.getMobileNumber().concat("|").concat(e.getName()));
						bean.setRequestType(MobiliserUtils.getValue("opcode", Integer.toString(e.getOpcode()), lookupMapUtility, basePage));
						bean.setStatus(BtpnConstants.STATUS_PENDING_APPROVAL);
						bean.setTaskId(e.getWorkflowId());
						
						list.add(bean);
					}
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Throwable e) {
			LOG.error("An exception was thrown.", e);
			
			error(getLocalizer().getString("pending.failure.exception", this));
			
			list.clear();
		}
		
		return list;
	}
	
	private void gotoUserConfirmPage(String workflowId) {
		try {
			GetUserWrkRequest request = MobiliserUtils.fill(new GetUserWrkRequest(), basePage);
			request.setCallerId(callerId);
			request.setWorkflowId(workflowId);
			
			GetUserWrkResponse response = userWrkFacade.getWrk(request);
			
			if (MobiliserUtils.success(response)) {
				BankStaffBean bean = BankStaffBeanConverter.fromContractWrk(response.getInformation(), lookupMapUtility, this);
				
				bean.setCreatedBy(response.getCreatedById().toString().concat("|").concat(response.getCreatedByName()));
				bean.setCreatedDate(PortalUtils.getFormattedDateTime(response.getCreatedDate(), getLocale()));
				
				bean.setWorkflowId(workflowId);
				bean.setRequestType(MobiliserUtils.getValue("opcode", response.getOpcode().toString(), lookupMapUtility, basePage));
				
				switch (response.getOpcode().intValue()) {
				case 1: // create
					setResponsePage(new BankStaffConfirmApprovalPage(bean));
					break;
				
				case 2: // update
				case 4: // remove
					// FIXME
					setResponsePage(new BankStaffConfirmApprovalPage(bean));
					break;
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Throwable e) {
			LOG.error("An exception was thrown.", e);
			
			error(getLocalizer().getString("selected.details.failure.error", this));
		}
	}

	private void gotoCustomerConfirmPage(String workflowId) {
		try {
			GetCustomerExWrkRequest request = MobiliserUtils.fill(new GetCustomerExWrkRequest(), basePage);
			request.setCallerId(callerId);
			request.setWorkflowId(workflowId);
			
			GetCustomerExWrkResponse response = customerWrkFacade.getWrk(request);
			
			if (MobiliserUtils.success(response)) {
				CustomerRegistrationBean newValue = CustomerRegistrationBeanConverter.fromContractWrk(response.getInformation(), lookupMapUtility, this);
				newValue.setCreatedBy(response.getCreatedById().toString().concat("|").concat(response.getCreatedByName()));
				newValue.setCreatedDate(PortalUtils.getFormattedDateTime(response.getCreatedDate(), getLocale()));
				
				newValue.setTaskId(workflowId);
				newValue.setRequestType(MobiliserUtils.getValue("opcode", response.getOpcode().toString(), lookupMapUtility, basePage));
				
				switch (response.getOpcode().intValue()) {
				case 1: // create
					setResponsePage(getRegistrationConfirmationPage(newValue));
					break;
				
				case 4: // remove
					newValue.setBlackListReason(MobiliserUtils.getCodeValue("blackListReasons", "9", lookupMapUtility, this));
					newValue.setBlackListReson(9);
					newValue.setStatus(MobiliserUtils.getValue("blackListReasons", "9", lookupMapUtility, this));
					
				case 2: // update
					CustomerIdentificationType ident = new CustomerIdentificationType();
					ident.setValue(newValue.getCustomerId());
					ident.setType(1);
					
					GetCustomerExRequest request2 = MobiliserUtils.fill(new GetCustomerExRequest(), basePage);
					request2.setCallerId(callerId);
					request2.setIdentification(ident);
					
					GetCustomerExResponse response2 = customerFacade.get(request2);
					
					if (MobiliserUtils.success(response2)) {
						CustomerRegistrationBean oldValue = CustomerRegistrationBeanConverter.fromContract(response2.getInformation(), lookupMapUtility, this);
						
						setResponsePage(getUpdateOrRemoveConfirmationPage(oldValue, newValue));
					} else {
						error(MobiliserUtils.errorMessage(response2, this));
					}
					
					break;
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Throwable e) {
			LOG.error("An exception was thrown.", e);
			
			error(getLocalizer().getString("selected.details.failure.error", this));
		}
	}

	private Page getUpdateOrRemoveConfirmationPage(CustomerRegistrationBean oldValue, CustomerRegistrationBean newValue) {
		String id = newValue.getProductType().getId();
		
		if (BtpnUtils.checkConsumerProductType(id, basePage.getLookupMapUtility(), this, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_CUSTOMERS)) {
			newValue.setCustomerType(BtpnConstants.REG_CONSUMER);
			
			return new ApproveCustomerDataView(oldValue, newValue);
		}
		
		if (BtpnUtils.checkConsumerProductType(id, basePage.getLookupMapUtility(), this, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_TOP_AGENT)) {
			newValue.setCustomerType(BtpnConstants.REG_TOPUP_AGENT);
			
			return new ApproveCustomerDataView(oldValue, newValue);
		}
		
		if (BtpnUtils.checkConsumerProductType(id, basePage.getLookupMapUtility(), this, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_AGENTS)) {
			newValue.setCustomerType(BtpnConstants.REG_CHILD_AGENT);
			
			return new ApproveCustomerDataView(oldValue, newValue);
		}
		
		newValue.setCustomerType(BtpnConstants.REG_SUB_AGENT);
		
		return new ApproveCustomerDataView(oldValue, newValue);
	}


	private Page getRegistrationConfirmationPage(CustomerRegistrationBean customer) {
		String id = customer.getProductType().getId();
		
		if (BtpnUtils.checkConsumerProductType(id, basePage.getLookupMapUtility(), this, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_CUSTOMERS)) {
			customer.setCustomerType(BtpnConstants.REG_CONSUMER);
			
			return new ConsumerTopAgentConfirmApprovalPage(customer);
		}
		
		if (BtpnUtils.checkConsumerProductType(id, basePage.getLookupMapUtility(), this, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_TOP_AGENT)) {
			customer.setCustomerType(BtpnConstants.REG_TOPUP_AGENT);
			
			return new ConsumerTopAgentConfirmApprovalPage(customer);
		}
		
		if (BtpnUtils.checkConsumerProductType(id, basePage.getLookupMapUtility(), this, BtpnConstants.RESOURCE_BUNDLE_PRODUCT_AGENTS)) {
			customer.setCustomerType(BtpnConstants.REG_CHILD_AGENT);
			
			return new ConsumerTopAgentConfirmApprovalPage(customer);
		}
		
		customer.setCustomerType(BtpnConstants.REG_SUB_AGENT);
		
		return new ConsumerTopAgentConfirmApprovalPage(customer);
	}
	/**
	 * This is the approvalsDataView for registering Bank consumer.
	 * 
	 * @author Vikram Gunda
	 */
	private class ApprovalDataView extends DataView<ApproveCustomerBean> {

		private static final long serialVersionUID = 1L;

		protected ApprovalDataView(final String id, IDataProvider<ApproveCustomerBean> dataProvider) {
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
		protected void populateItem(final Item<ApproveCustomerBean> item) {
			final ApproveCustomerBean entry = item.getModelObject();

			// Add the created date
			final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			final String createdDate = dateFormat.format(entry.getDate());
			item.add(new Label("date", createdDate));
			// Add the request Type
			item.add(new Label("requestType", entry.getRequestType()));
			// Add the customer Type
			item.add(new Label("consumerType", entry.getConsumerType()));
			// Add the Mobile Number
			item.add(new Label("mobileNumber", entry.getMobileNumber()));
			// Add the created By
			item.add(new Label("createdBy", entry.getCreatedBy()));

			// Add the delete Link
			final AjaxLink<ApproveCustomerBean> detailsLink = new AjaxLink<ApproveCustomerBean>(
					WICKET_ID_LINK, item.getModel()) {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					final ApproveCustomerBean bean = (ApproveCustomerBean) item.getModelObject();
					
					switch (bean.getCustomerTypeCategory()) {
					case 0: // customer employee
					case 1: // customer non employee
					case 2: // top agent
					case 3: // agent
					case 4: // sub agent
						BankCheckerApprovalPanel.this.gotoCustomerConfirmPage(bean.getTaskId());
						break;
					case 5: // bank admin
					case 6: // bank staff
						BankCheckerApprovalPanel.this.gotoUserConfirmPage(bean.getTaskId());
						break;
					}
					
					target.addComponent(feedbackPanel);
				}
			};
			detailsLink.add(new Label(WICKET_ID_LINK_NAME, "Details"));
			
			item.add(detailsLink);
			
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ((ApprovalDataProvider) internalGetDataProvider()).size() != 0;

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
}
