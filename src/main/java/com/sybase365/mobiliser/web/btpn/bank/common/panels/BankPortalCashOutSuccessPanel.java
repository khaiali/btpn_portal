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
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashOutBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the CashOutSuccessPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashOutSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BankPortalCashOutSuccessPanel.class);

	protected BtpnMobiliserBasePage basePage;

	protected BankCashOutBean cashOutBean;

	public BankPortalCashOutSuccessPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		LOG.debug("BankPortalcashOutDetailsPanel Started.");
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashOutSuccessPanel> form = new Form<BankPortalCashOutSuccessPanel>("cashOutSuccessForm",
			new CompoundPropertyModel<BankPortalCashOutSuccessPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new Label("cashOutBean.accountNumber"));
		form.add(new Label("cashOutBean.mobileNumber"));
		form.add(new Label("cashOutBean.displayName"));
		form.add(new Label("cashOutBean.accountType"));
		cashOutBean.setTotalSVABalance(basePage.getSvaBalance(cashOutBean.getMobileNumber()));
		form.add(new AmountLabel("cashOutBean.totalSVABalance"));
		form.add(new AmountLabel("cashOutBean.debitAmount"));
		form.add(new AmountLabel("cashOutBean.cashOutFeeAmount"));
		form.add(new AmountLabel("cashOutBean.totalCashOutAmount"));

		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(BankPortalCashOutPage.class);
			};
		});

		add(form);
	}

	public BankCashOutBean getCashOutBean() {
		return cashOutBean;
	}

	public void setCashOutBean(BankCashOutBean cashOutBean) {
		this.cashOutBean = cashOutBean;
	}

}
