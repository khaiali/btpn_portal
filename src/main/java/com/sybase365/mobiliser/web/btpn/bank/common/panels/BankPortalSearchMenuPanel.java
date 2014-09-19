package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.contract.CustomerFindViewType;
import com.btpnwow.core.customer.facade.contract.FindCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.FindCustomerExResponse;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.AgentListPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.CustomerListPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBankPortalAgentCareMenuPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBankPortalCustomerCareMenuPage;

public class BankPortalSearchMenuPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalSearchMenuPanel.class);
	
	private static final String TYPE_AGENT = "agent";
	
	private static final String TYPE_CUSTOMER = "customer";

	@SpringBean(name = "customerFacade")
	private CustomerFacade customerFacade;
	
	// private WebMarkupContainer emailDiv;

	// private WebMarkupContainer empIdDiv;

	// private CustomerSearchBean customerSearchBean;

	private BtpnMobiliserBasePage basePage;
	
	private String searchFor;

	private String type;

	private int rowNum = 0;

	private int maxResult;

	public BankPortalSearchMenuPanel(String id, String type, BtpnMobiliserBasePage basePage) {
		super(id);
		
		this.type = type;
		this.basePage = basePage;
		
		maxResult = basePage.getBankPortalPrefsConfig().getDefaultMaxResult();
		
		// set the Default Customer Status
//		customerSearchBean = basePage.getMobiliserWebSession().getCustomerSearchBean();
//		if (customerSearchBean == null) {
//			customerSearchBean = new CustomerSearchBean();
//			customerSearchBean.setCustStatus(new CodeValue("ACTIVE", "ACTIVE"));
//		}
		
		initOwnComponents();
	}

	protected void initOwnComponents() {
		final Form<BankPortalSearchMenuPanel> form = new Form<BankPortalSearchMenuPanel>(
				"confirmRegistrationForm", new CompoundPropertyModel<BankPortalSearchMenuPanel>(this));

//		final TextField<String> msisdn = new TextField<String>("customerSearchBean.msisdn");
//		final TextField<String> displayName = new TextField<String>("customerSearchBean.displayName");
//		final TextField<String> employeeId = new TextField<String>("customerSearchBean.employeeId");
//		final TextField<String> email = new TextField<String>("customerSearchBean.email");
//
//		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
//			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
//
//		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("customerSearchBean.custStatus", CodeValue.class,
//			BtpnConstants.RESOURCE_BUNDLE_CUSTOMER_STATUS, this, Boolean.FALSE, true).setNullValid(false)
//			.setChoiceRenderer(codeValueChoiceRender).add(new ErrorIndicator()));
//
//		empIdDiv = new WebMarkupContainer("employeeDiv");
//		empIdDiv.add(employeeId);
//		emailDiv = new WebMarkupContainer("emailDiv");
//		emailDiv.add(email);

		form.add(new TextField<String>("searchFor"));
		
		form.add(new AjaxButton("findButton") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//				basePage.getMobiliserWebSession().setCustomerSearchBean(customerSearchBean);
//				
//				if (PortalUtils.exists(customerSearchBean) && PortalUtils.exists(customerSearchBean.getMsisdn())) {
//					final PhoneNumber phoneNumber = new PhoneNumber(customerSearchBean.getMsisdn(), basePage
//						.getAgentPortalPrefsConfig().getDefaultCountryCode());
//					
//					customerSearchBean.setMsisdn(phoneNumber.getInternationalFormat());
//				}
				
				if (TYPE_CUSTOMER.equals(type)) {
					setResponsePage(new CustomerListPage(searchFor, type));
				} else if (TYPE_AGENT.equals(type)) {
					setResponsePage(new AgentListPage(searchFor, type));
				}
				
//				List<CustomerFindViewType> list = find();
//				if (list == null) {
//					return; // error!
//				}
//				
//				if (!list.isEmpty()) {
//					if (TYPE_CUSTOMER.equals(type)) {
//						setResponsePage(new CustomerListPage(searchFor, type));
//					} else if (TYPE_AGENT.equals(type)) {
//						setResponsePage(new AgentListPage(searchFor, type));
//					}
					
//					if (list.size() == BtpnConstants.SEARCH_CUSTOMERS_COUNT) {
//						SearchCustomerResult customer = list.get(0);
//						CustomerRegistrationBean customerRegBean = basePage.getCustomerDetailsByCustomerId(Long
//							.valueOf(customer.getCustomerId()));
//						basePage.getMobiliserWebSession().setCustomerRegistrationBean(customerRegBean);
//						setResponsePage(new SearchCustomerCareMenu(true, type, customerSearchBean));
//					} else if (type.equals("customer")) {
//						setResponsePage(new CustomerListPage(customerSearchBean, type));
//					} else if (type.equals("agent")) {
//						setResponsePage(new AgentListPage(customerSearchBean, type));
//					}
//				} else {
//					gotoErrorPage(getLocalizer().getString("search.empty", this));
//				}
			}
		});
		
		form.add(new AjaxButton("backButton") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			};
		});
		
//		if (type.equals("agent")) {
//			empIdDiv.setVisible(false);
//			emailDiv.setVisible(false);
//		} else {
//			empIdDiv.setVisible(true);
//			emailDiv.setVisible(true);
//		}
//		form.add(msisdn);
//		form.add(displayName);
//		form.add(empIdDiv);
//		form.add(emailDiv);
		
		add(form);
	}

	public List<CustomerFindViewType> find() {
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
			
			FindCustomerExResponse response = customerFacade.find(request);
			
			if (MobiliserUtils.success(response)) {
				return response.getItem();
			}
			
			gotoErrorPage(MobiliserUtils.errorMessage(response, this));
			
//			SearchCustomerRequest request = basePage.getNewMobiliserRequest(SearchCustomerRequest.class);
//			SearchCustomer searchCustomer = new SearchCustomer();
//			searchCustomer.setMsisdn(bean.getMsisdn());
//			searchCustomer.setName(bean.getDisplayName());
//			searchCustomer.setEmail(bean.getEmail());
//			searchCustomer.setEmployeeId(bean.getEmployeeId());
//			if (PortalUtils.exists(bean.getCustStatus())) {
//				searchCustomer.setStatus(bean.getCustStatus().getId());
//			}
//			if (type.equals("customer")) {
//				searchCustomer.getListProductCategory().add(0);
//				searchCustomer.getListProductCategory().add(1);
//			} else if (type.equals("agent")) {
//				searchCustomer.getListProductCategory().add(2);
//				searchCustomer.getListProductCategory().add(3);
//				searchCustomer.getListProductCategory().add(4);
//			}
//			searchCustomer.setRowNum(rowNum);
//			searchCustomer.setMaxResults(maxResult);
//			request.setSearchCustomer(searchCustomer);
//			SearchCustomerResponse response = basePage.getSupportClient().searchCustomer(request);
//			if (basePage.evaluateBankPortalMobiliserResponse(response)
//					&& response.getStatus().getCode() == BtpnConstants.STATUS_CODE) {
//				customers = response.getCustomers();
//			}
		} catch (Throwable ex) {
			LOG.error("An exception was thrown.", ex);
			
			gotoErrorPage(getLocalizer().getString("search.failure.exception", this));
		}
		
		return null;
	}
	
	private void gotoErrorPage(String message) {
		basePage.getMobiliserWebSession().error(message);
		
		if (TYPE_CUSTOMER.equals(type)) {
			setResponsePage(BtpnBankPortalCustomerCareMenuPage.class);
		} else if (TYPE_AGENT.equals(type)) {
			setResponsePage(BtpnBankPortalAgentCareMenuPage.class);
		}
	}

//	public CustomerSearchBean getCustomerSearchBean() {
//		return customerSearchBean;
//	}

//	public void setCustomerSearchBean(CustomerSearchBean customerSearchBean) {
//		this.customerSearchBean = customerSearchBean;
//	}
}
