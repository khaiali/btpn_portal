package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.bank.beans.BankStaffBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankAdminRegistrationPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankAdminRegistrationSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.registration.BankUserRegistrationPage;

/**
 * This is the BankAdminAndUserRegistrationSuccessPanel Panel for registering Bank Admins and Users.
 * 
 * @author Andi Samallangi W
 */
public class BankAdminAndUserRegistrationSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private BankAdminRegistrationSuccessPage page;
	
	@SuppressWarnings("unused")
	private BankStaffBean bankStaffBean;

	public BankAdminAndUserRegistrationSuccessPanel(String id, BankAdminRegistrationSuccessPage page) {
		super(id);
		
		this.page = page;
		
		this.bankStaffBean = page.getBankStaffBean();
		
		constructPanel();
	}

	private void constructPanel() {
		final Form<BankAdminAndUserRegistrationSuccessPanel> form = new Form<BankAdminAndUserRegistrationSuccessPanel>(
				"regSuccessForm",
				new CompoundPropertyModel<BankAdminAndUserRegistrationSuccessPanel>(this));
		
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
		
		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (page.isBankAdmin()) {
					setResponsePage(BankAdminRegistrationPage.class);
				} else {
					setResponsePage(BankUserRegistrationPage.class);
				}
			}
		});

		add(form);
	}
}
