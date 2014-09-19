package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCustomCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the CashOutSuccessPanel page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalCustomCashOutSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCustomCashOutSuccessPanel.class);

	protected BtpnMobiliserBasePage basePage;

	protected BankCustomCashOutBean cashOutBean;

	public BankPortalCustomCashOutSuccessPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		LOG.debug("BankPortalcashOutDetailsPanel Started.");
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCustomCashOutSuccessPanel> form = new Form<BankPortalCustomCashOutSuccessPanel>("customCashOutSuccessForm",
			new CompoundPropertyModel<BankPortalCustomCashOutSuccessPanel>(this));
		
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("cashOutBean.accountNumber"));
		form.add(new Label("cashOutBean.mobileNumber"));
		form.add(new Label("cashOutBean.displayName"));
		form.add(new AmountLabel("cashOutBean.cashOutAmount", true, true));

		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(BankPortalCustomCashOutPage.class);
			};
		});

		add(form);
	}

	public BankCustomCashOutBean getCashOutBean() {
		return cashOutBean;
	}

	public void setCashOutBean(BankCustomCashOutBean cashOutBean) {
		this.cashOutBean = cashOutBean;
	}

}
