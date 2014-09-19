package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCustomCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCustomCashOutSuccessPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BankPortalHomePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.AttributePrepender;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the CashOutConfirmPanel page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class BankPortalCustomCashOutConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCustomCashOutConfirmPanel.class);

	protected BtpnMobiliserBasePage basePage;

	protected BankCustomCashOutBean cashOutBean;

	public BankPortalCustomCashOutConfirmPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		LOG.debug("BankPortalcashOutDetailsPanel Started.");
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCustomCashOutConfirmPanel> form = new Form<BankPortalCustomCashOutConfirmPanel>("customCashOutConfirmForm",
			new CompoundPropertyModel<BankPortalCustomCashOutConfirmPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		form.add(new Label("cashOutBean.accountNumber"));
		form.add(new Label("cashOutBean.mobileNumber"));
		form.add(new Label("cashOutBean.displayName"));
		form.add(new AmountLabel("cashOutBean.cashOutAmount", true, true));

		// Add Confirm button
		Button confirmButton = new Button("submitConfirm") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new BankPortalCustomCashOutSuccessPage(cashOutBean));
			}
		};
		confirmButton.add(new AttributePrepender("onclick", Model.of("loading(submitConfirm)"), ";"));
		form.add(confirmButton);

		// Add Back button
		form.add(new Button("submitBack") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new BankPortalCustomCashOutPage(cashOutBean));
			};
		}.setDefaultFormProcessing(false));

		// Add Cancel button
		form.add(new Button("submitCancle") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				basePage.handleCancelButtonRedirectToHomePage(BankPortalHomePage.class);
			};
		}.setDefaultFormProcessing(false));

		add(form);
	}
}
