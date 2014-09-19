package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import java.text.SimpleDateFormat;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.CustomerFacade;
import com.btpnwow.core.customer.facade.contract.CustomerIdentificationType;
import com.btpnwow.core.customer.facade.contract.GetCustomerExRequest;
import com.btpnwow.core.customer.facade.contract.GetCustomerExResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.consumer.common.panels.ManageFavoritesPanel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.util.ConsumerPortalConverterUtils;


public class ViewCustomerProfile extends BtpnBaseConsumerPortalSelfCarePage {

	private static final Logger log = LoggerFactory.getLogger(ManageFavoritesPanel.class);
	
	private CustomerRegistrationBean customer;
	
	@SpringBean(name = "customerClient")
	private CustomerFacade customerClient;

	public ViewCustomerProfile() {
		this.customer = getCustomerDetailsByCustomerId();
		initPageComponents();
	}

	/**
	 * Initialize page components.
	 */
	protected void initPageComponents() {

		final Form<ViewCustomerProfile> form = new Form<ViewCustomerProfile>("viewProfilePage",
			new CompoundPropertyModel<ViewCustomerProfile>(this));
		
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("customer.name"));
		form.add(new Label("customer.mothersMaidenName"));
		form.add(new Label("customer.nationality.value"));
		form.add(new Label("customer.job.value"));
		form.add(new Label("customer.purposeOfAccount.value"));
		form.add(new Label("customer.income.value"));
		form.add(new Label("customer.highRiskCustomer.value"));
		form.add(new Label("customer.atmCardNo"));
		form.add(new Label("customer.emailId"));
		form.add(new Label("customer.employeeId"));
		form.add(new Label("customer.maritalStatus.value"));
		form.add(new Label("customer.placeOfBirth"));
		form.add(new Label("customer.occupation.value"));
		form.add(new Label("customer.industryOfEmployee.value"));
		form.add(new Label("customer.sourceofFound.value"));
		form.add(new Label("customer.highRiskBusiness.value"));
		form.add(new Label("customer.gender.value"));
		final SimpleDateFormat sdf = new SimpleDateFormat(BtpnConstants.ID_EXPIRY_DATE_PATTERN);
		form.add(new Label("customer.birthDateString", sdf.format(customer.getBirthDateString())));
//		form.add(new Label("customer.expireDateString", sdf.format(customer.getExpireDateString())));
		form.add(new Label("customer.customerNumber"));
		form.add(new Label("customer.taxExempted.value"));
		form.add(new Label("customer.optimaActivated.value"));
		form.add(new Label("customer.religion.value"));
		form.add(new Label("customer.lastEducation.value"));
		form.add(new Label("customer.foreCastTransaction.value"));
		form.add(new Label("customer.taxCardNumber"));
		form.add(new Label("customer.mobileNumber"));
		form.add(new Label("customer.street1"));
		form.add(new Label("customer.street2"));
		form.add(new Label("customer.province.value"));
		form.add(new Label("customer.city.value"));
		form.add(new Label("customer.zipCode"));
		form.add(new Label("customer.idCardNo"));
		form.add(new Label("customer.idType.value"));
		form.add(new Label("customer.marketingSourceCode"));
		form.add(new Label("customer.referralNumber"));
		form.add(new Label("customer.agentCode"));
		
		Button button = new Button("btnOk") {

			private static final long serialVersionUID = 1L;
			public void onSubmit() {
				ViewCustomerProfile.this.handleCancelButtonRedirectToHomePage(ConsumerPortalHomePage.class);
			};
		};
		
		form.add(button);
		
		add(form);
	}
	
	
	

	public CustomerRegistrationBean getCustomerDetailsByCustomerId() {
		
		CustomerRegistrationBean custRegBean = new CustomerRegistrationBean();
		
		try {
			
			log.info(" ### (ViewCustomerProfile::getCustomerDetailsByCustomerId) ### ");
			
			String userName = getMobiliserWebSession().getBtpnLoggedInCustomer().getUsername();
			log.info(" ### (ViewCustomerProfile::getCustomerDetailsByCustomerId) USER NAME ### "+userName);
			
			final GetCustomerExRequest request = getNewMobiliserRequest(GetCustomerExRequest.class);
			CustomerIdentificationType obj = new CustomerIdentificationType();
			obj.setType(0);
			obj.setValue(userName);
			
			request.setFlags(0);
			request.setIdentification(obj);
			
			final GetCustomerExResponse response = customerClient.get(request);
			log.info(" ### (ViewCustomerProfile::getCustomerDetailsByCustomerId) RESPONSE ### " +response.getStatus().getCode());
			
			if (evaluateConsumerPortalMobiliserResponse(response)) {
				custRegBean = ConsumerPortalConverterUtils.convertViewCustomertoCustomer(response,
					this.getLookupMapUtility(), this);
				custRegBean.setCustomerId(String.valueOf(getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
				
			} else {
				error(getLocalizer().getString("error.customer.details", this));
			}

		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			log.error("Error occured while calling viewCustomerProfile Service", e);
		}
		
		return custRegBean;
	}

}
