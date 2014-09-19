package com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.bank.beans.CustomerRegistrationBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalHomePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ViewCustomerProfile;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;

/**
 * This class is used for showing Agent profile information
 * 
 * @author Vikram Gunda
 */
public class ViewAgentProfile extends BtpnBaseAgentPortalSelfCarePage {

	private CustomerRegistrationBean customer;

	public ViewAgentProfile() {
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
		form.add(new Label("customer.glCodeId.value"));
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
		form.add(new Label("customer.expireDateString", sdf.format(customer.getExpireDateString())));
		form.add(new Label("customer.customerNumber"));
		form.add(new Label("customer.taxExempted.value"));
		form.add(new Label("customer.optimaActivated.value"));
		form.add(new Label("customer.mobileNumber"));
		form.add(new Label("customer.street1"));
		form.add(new Label("customer.street2"));
		form.add(new Label("customer.idCardNo"));
		form.add(new Label("customer.idType.value"));
		Button button = new Button("btnOk") {
			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				ViewAgentProfile.this.handleCancelButtonRedirectToHomePage(ConsumerPortalHomePage.class);
			};
		};
		form.add(button);
		add(form);
	}
}
