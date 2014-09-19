package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.contract.CustomerFindViewType;
import com.btpnwow.core.customer.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.customer.facade.contract.FindCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.FindCustomerExResponse;
import com.btpnwow.core.customer.facade.contract.GetCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.GetCustomerExResponse;
import com.btpnwow.portal.bank.converter.CustomerRegistrationBeanConverter;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.util.tools.wicketutils.resourceloader.ILookupMapUtility;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.CustomerDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.SearchCustomerCareMenu;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.util.PortalUtils;

public class SearchCustomerPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SearchCustomerPanel.class);
	
	private static final String TYPE_AGENT = "agent";
	
	private static final String TYPE_CUSTOMER = "customer";

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_LINK_NAME = "msisdnLinkName";

	protected BtpnMobiliserBasePage basePage;

	@SpringBean(name = "customerFacade")
	protected CustomerFacade customerFacade;
	
	@SpringBean(name = "btpnLookupMapUtilitiesImpl")
	public ILookupMapUtility lookupMapUtility;

	private String searchFor;
	
	private String type;

//	CustomerSearchBean searchBean;
	
	private String totalRecordsMessage;

	private int totalCount = 0;

	private int rowNum = 0;

	private int maxResult;

	private List<CustomerRegistrationBean> customerList;
	
	private WebMarkupContainer listContainer;
	
	private Label numberOfRecords;
	
	private Button firstButton;
	
	private Button previousButton;
	
	private Button nextButton;
	
	private Button lastButton;

	public SearchCustomerPanel(String id, BtpnMobiliserBasePage basePage, String searchFor, String type) {
		super(id);
		
		this.basePage = basePage;
		
//		this.searchBean = bean;
		
		this.searchFor = searchFor;
		this.type = type;
		
		maxResult = basePage.getBankPortalPrefsConfig().getDefaultMaxResult();
		
		customerList = getCustomerList();
		totalRecordsMessage = getNorMessage();
		
		constructPanel();
	}
	
	protected String getNorMessage() {
		if ((rowNum + maxResult) >= totalCount) {
			return String.format(getLocalizer().getString("approval.totalitems.header", this),
					Integer.toString(totalCount),
					Integer.toString(rowNum + 1),
					Integer.toString(totalCount));
		}
		
		return String.format(getLocalizer().getString("approval.totalitems.header", this),
				Integer.toString(totalCount),
				Integer.toString(rowNum + 1),
				Integer.toString(rowNum + maxResult));
	}

	protected void constructPanel() {
		final Form<SearchCustomerPanel> form = new Form<SearchCustomerPanel>(
				"searchCustomerPanel", new CompoundPropertyModel<SearchCustomerPanel>(this));
		
		form.add(new FeedbackPanel("errorMessages"));

		numberOfRecords = new Label("totalRecordsMessage");
		numberOfRecords.setOutputMarkupId(true);
		numberOfRecords.setVisible(false);
		
		form.add(numberOfRecords);

		if (PortalUtils.exists(customerList)) {
			numberOfRecords.setVisible(true);
		} else {
			numberOfRecords.setVisible(false);
			
			error(getLocalizer().getString("label.noDataFound", this));
		}

		// Add the list container
		listContainer = new WebMarkupContainer("listContainer");
		approveTxnReversalDataView(listContainer);
		
		listContainer.setOutputMarkupId(true);
		form.add(listContainer);

		// First Button
		form.add((firstButton = new AjaxButton("firstButton") {
			
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				rowNum = 0;
				reload(target);
			}

			@Override
			public boolean isVisible() {
				return rowNum > 0;
			}
		})
		.setDefaultFormProcessing(false)
		.setOutputMarkupPlaceholderTag(true));

		// Previous Button
		form.add((previousButton = new AjaxButton("previousButton") {
			
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (rowNum - maxResult >= 0) {
					rowNum = rowNum - maxResult;
					reload(target);
				}
			}

			@Override
			public boolean isVisible() {
				return rowNum >= maxResult;
			}
		})
		.setDefaultFormProcessing(false)
		.setOutputMarkupPlaceholderTag(true));

		// Next Button
		form.add((nextButton = new AjaxButton("nextButton") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (rowNum + maxResult <= totalCount) {
					rowNum = rowNum + maxResult;
					reload(target);
				}
			}

			@Override
			public boolean isVisible() {
				return rowNum + maxResult <= totalCount;
			}
		})
		.setDefaultFormProcessing(false)
		.setOutputMarkupPlaceholderTag(true));

		// Last Button
		form.add((lastButton = new AjaxButton("lastButton") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (rowNum + maxResult <= totalCount) {
					rowNum = totalCount - maxResult;
					reload(target);
				}
			}

			@Override
			public boolean isVisible() {
				return rowNum + maxResult <= totalCount;
			}
		})
		.setDefaultFormProcessing(false)
		.setOutputMarkupPlaceholderTag(true));
		
		add(form);
	}

	/**
	 * This method adds the approveTxnReversalDataView for the transaction reversal, and also adds the sorting logic for
	 * data view
	 */
	protected void approveTxnReversalDataView(final WebMarkupContainer dataViewContainer) {
		// Create the approval View
		final CustomerDataProvider approvalDataProvider = new CustomerDataProvider("msisdn", customerList);

		final DataView<CustomerRegistrationBean> dataView = new ApprovalDataView(WICKET_ID_PAGEABLE, approvalDataProvider);
		
		dataView.setItemsPerPage(20);

		// Add the sort providers
//		dataViewContainer.add(new BtpnOrderByOrder("orderByMsisdn", "msisdn", approvalDataProvider, dataView));
//		dataViewContainer.add(new BtpnOrderByOrder("orderByName", "displayName", approvalDataProvider, dataView));
//		dataViewContainer.add(new BtpnOrderByOrder("orderByType", "customerType", approvalDataProvider, dataView));
		
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the approvalsDataView for approval MSISDN.
	 * 
	 * @author Narasa Reddy
	 */
	private class ApprovalDataView extends DataView<CustomerRegistrationBean> {
		private static final long serialVersionUID = 1L;

		protected ApprovalDataView(String id, IDataProvider<CustomerRegistrationBean> dataProvider) {
			super(id, dataProvider);
			
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
		}

		@Override
		protected void onBeforeRender() {
			CustomerDataProvider dataProvider = (CustomerDataProvider) internalGetDataProvider();
			
			dataProvider.setcustomerList(customerList);
			
			super.onBeforeRender();
		}

		protected void populateItem(final Item<CustomerRegistrationBean> item) {
			final CustomerRegistrationBean entry = item.getModelObject();

			Link<CustomerRegistrationBean> msisdnLink = new Link<CustomerRegistrationBean>("msisdnLink", item.getModel()) {
				
				private static final long serialVersionUID = 1L;

				public void onClick() {
					SearchCustomerPanel.this.gotoDetailsPage(Long.parseLong(((CustomerRegistrationBean) item.getModelObject()).getCustomerId()));
				}
			};
			
			msisdnLink.add(new Label(WICKET_ID_LINK_NAME, entry.getMobileNumber()));
			item.add(msisdnLink);

			item.add(new Label("displayName", entry.getName()));
			item.add(new Label("customerType", entry.getCustomerType()));

			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}
	}
	
	private void reload(AjaxRequestTarget target) {
		customerList = getCustomerList();
		totalRecordsMessage = getNorMessage();
		
		target.addComponent(listContainer);
		target.addComponent(numberOfRecords);
		
		target.addComponent(firstButton);
		target.addComponent(previousButton);
		target.addComponent(nextButton);
		target.addComponent(lastButton);
	}

	private List<CustomerRegistrationBean> getCustomerList() {
		List<CustomerRegistrationBean> customerList = new ArrayList<CustomerRegistrationBean>();
		
		BtpnCustomer loggedIn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		try {
			FindCustomerExRequest request = MobiliserUtils.fill(new FindCustomerExRequest(), basePage);
			request.setSearchFor(searchFor);
			request.setStart(rowNum);
			request.setLength(maxResult);
			
			if (TYPE_CUSTOMER.equals(type)) {
				request.getCustomerTypeCategory().add(Integer.valueOf(0));
				request.getCustomerTypeCategory().add(Integer.valueOf(1));
			} else if (TYPE_AGENT.equals(type)) {
				request.getCustomerTypeCategory().add(Integer.valueOf(2));
				request.getCustomerTypeCategory().add(Integer.valueOf(3));
				request.getCustomerTypeCategory().add(Integer.valueOf(4));
			}
			
			request.setOrgUnitId(loggedIn.getOrgUnitId());
			request.setTerritoryCode(loggedIn.getTerritoryCode());
			
			request.setCallerId(Long.valueOf(loggedIn.getCustomerId()));
			
			FindCustomerExResponse response = customerFacade.find(request);
			
			if (MobiliserUtils.success(response)) {
				rowNum = response.getStart().intValue();
				
				totalCount = response.getTotal().intValue();
				
				List<CustomerFindViewType> clist = response.getItem();
				
				if ((clist != null) && !clist.isEmpty()) {
					for (CustomerFindViewType ce : clist) {
						customerList.add(CustomerRegistrationBeanConverter.fromContract(ce, lookupMapUtility, this));
					}
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Exception ex) {
			LOG.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("error.exception", this));
		}
		
		return customerList;
	}
	
	protected void gotoDetailsPage(long customerId) {
		try {
			CustomerIdentificationType cid = new CustomerIdentificationType();
			cid.setValue(Long.toString(customerId));
			cid.setType(1);
			
			GetCustomerExRequest request = MobiliserUtils.fill(new GetCustomerExRequest(), basePage);
			request.setIdentification(cid);
			request.setCallerId(Long.valueOf(basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
			
			GetCustomerExResponse response = customerFacade.get(request);
			
			if (MobiliserUtils.success(response)) {
				CustomerRegistrationBean bean = CustomerRegistrationBeanConverter.fromContract(response.getInformation(), lookupMapUtility, this);
				
				basePage.getMobiliserWebSession().setCustomerRegistrationBean(bean);
				
				setResponsePage(new SearchCustomerCareMenu(true, searchFor, type));
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
		} catch (Exception ex) {
			LOG.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("error.exception", this));
		}
	}
}
