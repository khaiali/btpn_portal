package com.sybase365.mobiliser.web.btpn.bank.common.panels;


import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerWrkFacade;
import com.btpnwow.core.customer.facade.contract.CreateCustomerExWrkRequest;
import com.btpnwow.core.customer.facade.contract.CreateCustomerExWrkResponse;
import com.btpnwow.portal.bank.converter.CustomerRegistrationBeanConverter;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration.AgentRegistrationPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.registration.AgentRegistrationSuccessPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.AgentPortalHomePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.NotificationAttachmentsBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.ConsumerTopAgentRegistrationPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.ConsumerTopAgentRegistrationSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;


public class ConfirmRegistrationPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ConfirmRegistrationPanel.class);
	
	@SpringBean(name = "customerWrkFacade")
	protected CustomerWrkFacade customerWrkFacade;

	protected CustomerRegistrationBean customer;
	protected BtpnMobiliserBasePage basePage;

	/**
	 * Constructor for this page.
	 * 
	 * @param id id Of the Panel
	 * @param basePage basePage of the Panel
	 * @param customer customer Object
	 */
	public ConfirmRegistrationPanel(final String id, final CustomerRegistrationBean customer, final BtpnMobiliserBasePage basePage) {
		super(id);
		
		// Initialize the bean
		this.customer = customer;
		
		// mobiliser page
		this.basePage = basePage;
		
		// constructs the panel
		constructPanel();
	}

	/**
	 * Constructs for the panel.
	 */
	protected void constructPanel() {
		
		final String customerType = customer.getCustomerType();
		
		final boolean agentPortal = customerType.equals(BtpnConstants.REG_SUB_AGENT) || customerType.equals(BtpnConstants.REG_CHILD_AGENT);
		
		final Form<ConfirmRegistrationPanel> form = new Form<ConfirmRegistrationPanel>(
				"confirmRegistrationForm", new CompoundPropertyModel<ConfirmRegistrationPanel>(this));
		
		form.add(new FeedbackPanel("errorMessages"));
		
		form.add(new Label("customerInfo.headLine", getLocalizer().getString(customerType + ".customerInfo.headLine", this)));
		form.add(new Label("customer.name"));
		form.add(new Label("customer.mothersMaidenName"));
		form.add(new Label("customer.nationality.value"));
		form.add(new Label("customer.language.value"));
		form.add(new Label("customer.receiptMode.value"));
		form.add(addGLCodeContainer(customerType));
		form.add(new Label("customer.job.value"));
		form.add(new Label("customer.purposeOfAccount.value"));
		form.add(new Label("customer.income.value"));
		form.add(new Label("customer.highRiskCustomer.value"));
		form.add(new Label("customer.atmCardNo"));
		form.add(new Label("customer.emailId"));
		form.add(new AttachmentListView("attachmentListView", customer.getAttachmentsList()));
		form.add(new Label("customer.shortName"));
		form.add(new Label("customer.employeeId"));
		form.add(new Label("customer.maritalStatus.value"));
		form.add(new Label("customer.placeOfBirth"));
		form.add(new Label("customer.occupation.value"));
		form.add(new Label("customer.industryOfEmployee.value"));
		form.add(new Label("confirmData.sourceofFound", getLocalizer().getString(customerType + ".confirmData.sourceofFound", this)));
		form.add(new Label("customer.sourceofFound.value"));
		form.add(new Label("customer.highRiskBusiness.value"));
		form.add(new Label("customer.gender.value"));
		final SimpleDateFormat sdf = new SimpleDateFormat(BtpnConstants.ID_EXPIRY_DATE_PATTERN);
		form.add(new Label("customer.birthDateString", sdf.format(customer.getBirthDateString())));
		form.add(new Label("customer.expireDateString", sdf.format(customer.getExpireDateString())));
		form.add(new Label("customer.customerNumber"));
		form.add(new Label("customer.taxExempted.value"));
		form.add(new Label("customer.optimaActivated.value"));
		form.add(new Label("customer.religion.value"));
		form.add(new Label("customer.lastEducation.value"));
		form.add(new Label("customer.foreCastTransaction.value"));
		form.add(new Label("customer.taxCardNumber"));
		form.add(new Label("contactDetails.headLine", getLocalizer().getString(customerType + ".contactDetails.headLine", this)));
		form.add(new Label("customer.mobileNumber"));
		form.add(new Label("customer.street1"));
		form.add(new Label("customer.street2"));
		form.add(new Label("customer.province.value"));
		form.add(new Label("customer.city.value"));
		form.add(new Label("customer.zipCode"));
		
		addRegistrationTypeInfo(form, customerType);
		
		form.add(addBackButton(agentPortal));

		form.add(addConfirmButton(agentPortal));

		form.add(addCancelButton(agentPortal));

		add(form);
	}

	/**
	 * This method is used for add RegistrationType Info.
	 */
	protected void addRegistrationTypeInfo(final Form<ConfirmRegistrationPanel> form, final String customerType) {
		form.add(new WebMarkupContainer("createdByContainer").add(new Label("customer.createdBy")).setVisible(false));
		form.add(new WebMarkupContainer("createdDateContainer").add(new Label("customer.createdDate")).setVisible(false));
		form.add(new Label("registrationType.headLine", getLocalizer().getString(customerType + ".registrationType.headLine", this)));
		// form.add(new Label("confirmData.registration.type", getLocalizer().getString(customerType + ".confirmData.registration.type", this)));
		// form.add(new Label("customer.registrationType.value"));
		form.add(new Label("customer.requestType"));
		form.add(new Label("customer.productType.value"));
		form.add(new Label("customer.marketingSourceCode"));
		form.add(new Label("customer.referralNumber"));
		form.add(new Label("customer.agentCode"));
		form.add(new Label("customer.territoryCode"));
		form.add(new WebMarkupContainer("statusContainer").add(new Label("customer.status")).setVisible(false));
	}

	/**
	 * ) This method is used for adding the GL container.
	 */
	private WebMarkupContainer addGLCodeContainer(final String customerType) {
		final WebMarkupContainer container = new WebMarkupContainer("idglCodeBlock");
		container.add(new Label("customer.glCodeId.value"));
		container.setVisible(!customerType.equals(BtpnConstants.REG_CONSUMER));
		
		return container;
	}

	/**
	 * This method adds the submit button for the Registration Panel.
	 */
	protected Button addBackButton(final boolean agentPortal) {
		Button backButton = new Button("back") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				if (agentPortal) {
					setResponsePage(new AgentRegistrationPage(customer));
				} else {
					setResponsePage(new ConsumerTopAgentRegistrationPage(customer));
				}
			}
		};
		backButton.setDefaultFormProcessing(true);
		
		return backButton;
	}

	/**
	 * This method adds the submit button for the Registration Panel.
	 */
	protected Button addConfirmButton(final boolean agentPortal) {
		Button submitButton = new Button("confirm") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				ConfirmRegistrationPanel.this.handleRegistration(agentPortal);
			}
		};
		
		return submitButton;
	}

	/**
	 * This method adds the cancel button for the Registration Panel.
	 */
	protected Button addCancelButton(final boolean agentPortal) {
		Button cancelButton = new Button("cancel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				if (agentPortal) {
					basePage.handleCancelButtonRedirectToHomePage(AgentPortalHomePage.class);
				} else {
					basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
				}
			};
		};
		
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * Attachment List view shows list of items
	 */
	private class AttachmentListView extends ListView<NotificationAttachmentsBean> {

		private static final long serialVersionUID = 1L;
		public AttachmentListView(final String id, final List<NotificationAttachmentsBean> list) {
			super(id, list);
		}

		protected void populateItem(ListItem<NotificationAttachmentsBean> item) {
			final NotificationAttachmentsBean object = item.getModelObject();
			item.add(new Label("customer.identificationAttachment", object.getFileName()));
			item.add(new Label("customer.idType.value", customer.getIdType().getValue()));
			item.add(new Label("customer.idCardNo", customer.getIdCardNo()));
		}
	}

	/**
	 * This method handles the registration
	 */
	private void handleRegistration(final boolean agentPortal) {
		
		BtpnCustomer loggedIn = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer();
		
		try {
			
			final String countryCode;
			
			if (agentPortal) {
				countryCode = basePage.getAgentPortalPrefsConfig().getDefaultCountryCode();
			} else {
				countryCode = basePage.getBankPortalPrefsConfig().getDefaultCountryCode();
			}
			
			CreateCustomerExWrkRequest request = MobiliserUtils.fill(new CreateCustomerExWrkRequest(), basePage);
			request.setCallerId(Long.valueOf(loggedIn.getCustomerId()));
			request.setInformation(CustomerRegistrationBeanConverter.toContractWrk(
					customer,
					countryCode,
					loggedIn.getOrgUnitId(),
					agentPortal ? Long.valueOf(loggedIn.getCustomerId()) : null));
			
			CreateCustomerExWrkResponse response = customerWrkFacade.createWrk(request);
			log.info(" ### (ConfirmRegistrationPanel:handleRegistration) RESPONSE CODE ### " +response.getStatus().getCode()  +  response.getStatus().getValue());
			if (MobiliserUtils.success(response)) {
				customer.setMessage(String.format(
						getLocalizer().getString("registration.successMessage", ConfirmRegistrationPanel.this),
						request.getInformation().getMobileNumber()));
				
				if (agentPortal) {
					setResponsePage(new AgentRegistrationSuccessPage(customer));
				} else {
					setResponsePage(new ConsumerTopAgentRegistrationSuccessPage(customer));
				}
			} else {
				error(MobiliserUtils.errorMessage(response, this));
			}
			
		} catch (Throwable ex) {
			log.error("An exception was thrown", ex);
			
			error(getLocalizer().getString("registration.failure.exception", ConfirmRegistrationPanel.this));
		}
	}
}
