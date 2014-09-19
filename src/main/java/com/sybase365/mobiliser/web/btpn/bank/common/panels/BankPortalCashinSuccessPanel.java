package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.BankCashinBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalCashinPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the CashinSuccessPanel page for bank portals.
 * 
 * @author Narasa Reddy
 */
public class BankPortalCashinSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected BankCashinBean cashInBean;

	public BankPortalCashinSuccessPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCashinBean cashInBean) {
		super(id);
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashinSuccessPanel> form = new Form<BankPortalCashinSuccessPanel>("cashInSuccessForm",
			new CompoundPropertyModel<BankPortalCashinSuccessPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("cashInBean.accountNumber"));
		form.add(new Label("cashInBean.msisdn"));
		form.add(new Label("cashInBean.displayName"));
		form.add(new Label("cashInBean.accountType"));
		cashInBean.setTotalSVABalance(basePage.getSvaBalance(cashInBean.getMsisdn()));
		form.add(new AmountLabel("cashInBean.totalSVABalance"));
		form.add(new AmountLabel("cashInBean.creditAmount"));
		form.add(new AmountLabel("cashInBean.cashInFeeAmount"));
		form.add(new AmountLabel("cashInBean.totalCashinAmount"));

		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(BankPortalCashinPage.class);
			};
		});

		add(form);
	}
}
