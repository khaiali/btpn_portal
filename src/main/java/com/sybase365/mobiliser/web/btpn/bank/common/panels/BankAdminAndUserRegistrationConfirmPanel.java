package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.customer.facade.api.UserWrkFacade;
import com.btpnwow.core.customer.facade.contract.CreateUserWrkRequest;
import com.btpnwow.core.customer.facade.contract.CreateUserWrkResponse;
import com.btpnwow.portal.bank.converter.BankStaffBeanConverter;
import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankAdminRegistrationConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankAdminRegistrationPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankAdminRegistrationSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankUserRegistrationPage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;

/**
 * This is the Registration Panel for registering Bank Admins and Users.
 * 
 * @author Andi Samallangi W
 */
public class BankAdminAndUserRegistrationConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(BankAdminAndUserRegistrationConfirmPanel.class);

	@SpringBean(name = "userWrkFacade")
	private UserWrkFacade userWrkFacade;

	private BankAdminRegistrationConfirmPage page;
	
	private BankStaffBean bankStaffBean;
	
	public BankAdminAndUserRegistrationConfirmPanel(String id, BankAdminRegistrationConfirmPage page) {
		super(id);
		
		this.page = page;
		
		this.bankStaffBean = page.getBankStaffBean();
		
		constructPanel();
	}

	private void constructPanel() {
		// Add the wicket form
		final Form<BankAdminAndUserRegistrationConfirmPanel> form = new Form<BankAdminAndUserRegistrationConfirmPanel>(
				"regConfirmForm",
				new CompoundPropertyModel<BankAdminAndUserRegistrationConfirmPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));
		
		if (page.isBankAdmin()) {
			form.add(new Label("headline.registration.legend.label", getLocalizer().getString("headline.registration.bankAdmin.legend.label", this)));
		} else {
			form.add(new Label("headline.registration.legend.label", getLocalizer().getString("headline.registration.bankStaff.legend.label", this)));
		}
		
		form.add(new Label("bankStaffBean.customerType.idAndValue"));
		form.add(new Label("bankStaffBean.userId"));
		form.add(new Label("bankStaffBean.name"));
		form.add(new Label("bankStaffBean.designation"));
		form.add(new Label("bankStaffBean.email"));
		form.add(new Label("bankStaffBean.language.idAndValue"));
		form.add(new Label("bankStaffBean.glCode.idAndValue"));
		form.add(new Label("bankStaffBean.territoryCode"));
		form.add(new Label("bankStaffBean.orgUnit.idAndValue"));
		
		// confirm
		form.add(new Button("submitConfirm") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (page.isBankAdmin()) {
					createBankAdmin();
				} else {
					createBankStaff();
				}
			}
		}.add(new AttributePrepender("onclick", Model.of("loading(submitConfirm)"), ";")));

		// cancel
		form.add(new Button("submitCancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (page.isBankAdmin()) {
					setResponsePage(new BankAdminRegistrationPage());
				} else {
					setResponsePage(new BankUserRegistrationPage());
				}
			};
		}.setDefaultFormProcessing(false));

		add(form);
	}
	
	protected void createBankAdmin() {
		CreateUserWrkRequest request = MobiliserUtils.fill(new CreateUserWrkRequest(), page);
		request.setInformation(BankStaffBeanConverter.toContractWrk(bankStaffBean, true));
		request.setNote(null);
		request.setCallerId(Long.valueOf(page.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
		
		CreateUserWrkResponse response = userWrkFacade.createWrk(request);
		
		if (MobiliserUtils.success(response)) {
			setResponsePage(new BankAdminRegistrationSuccessPage(bankStaffBean, page.isBankAdmin(), false));
		} else {
			page.getWebSession().error(MobiliserUtils.errorMessage(response, this));
		}
	}
	
	protected void createBankStaff() {
		CreateUserWrkRequest request = MobiliserUtils.fill(new CreateUserWrkRequest(), page);
		request.setInformation(BankStaffBeanConverter.toContractWrk(bankStaffBean, true));
		request.setNote(null);
		request.setCallerId(Long.valueOf(page.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId()));
		
		CreateUserWrkResponse response;
		
		try {
			response = userWrkFacade.createWrk(request);
		} catch (Throwable ex) {
			LOG.error("An exception was thrown.", ex);
			
			error(getLocalizer().getString("error.exception", this));
			return;
		}
		
		if (MobiliserUtils.success(response)) {
			setResponsePage(new BankAdminRegistrationSuccessPage(bankStaffBean, page.isBankAdmin(), true));
		} else {
			error(MobiliserUtils.errorMessage(response, this));
		}
	}
}
