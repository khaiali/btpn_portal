package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCustomCashInPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;


public class BankPortalCustomCashInSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected BankCustomCashInBean cashInBean;

	public BankPortalCustomCashInSuccessPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashInBean cashInBean) {
		super(id);
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCustomCashInSuccessPanel> form = new Form<BankPortalCustomCashInSuccessPanel>("customCashInSuccessForm",
			new CompoundPropertyModel<BankPortalCustomCashInSuccessPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("cashInBean.accountNumber"));
		form.add(new Label("cashInBean.msisdn"));
		form.add(new Label("cashInBean.displayName"));
		form.add(new AmountLabel("cashInBean.cashinAmount", true, true));

		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(BankPortalCustomCashInPage.class);
			};
		});

		add(form);
	}
}
