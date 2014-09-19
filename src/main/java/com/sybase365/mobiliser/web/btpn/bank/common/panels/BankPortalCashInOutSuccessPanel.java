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
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashin.BankPortalTopAgentCashInPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalCustomCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.cashout.BankPortalTopAgentCashOutPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the BankPortalCustomCashInSuccessPanel page for bank portals.
 * 
 * @author Andi Samallangi W
 * @modified Feny Yanti
 */
public class BankPortalCashInOutSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected BankCustomCashInBean cashInBean;

	public BankPortalCashInOutSuccessPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, BankCustomCashInBean cashInBean) {
		super(id);
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashInOutSuccessPanel> form = new Form<BankPortalCashInOutSuccessPanel>("customCashInSuccessForm",
			new CompoundPropertyModel<BankPortalCashInOutSuccessPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new Label("headLine.cashinSuccess", cashInBean.getIsFinal() ? "CASH-OUT Success" : "CASH-IN Success"));
		form.add(new Label("cashInBean.glAccount", cashInBean.getGlAccount().getIdAndValue()));
		form.add(new Label("cashInBean.msisdn"));
		form.add(new Label("cashInBean.accountName"));
		form.add(new AmountLabel("cashInBean.amount", true, true));
		form.add(new AmountLabel("cashInBean.fee"));
		
		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if(cashInBean.getProcessingCode().equals("100101"))
				{
					setResponsePage(BankPortalTopAgentCashInPage.class);
				}
				else if(cashInBean.getProcessingCode().equals("100201"))
				{
					setResponsePage(BankPortalTopAgentCashOutPage.class);
				}
				else if(cashInBean.getProcessingCode().equals("102401"))
				{
					setResponsePage(BankPortalCustomCashInPage.class);
				}
				else if(cashInBean.getProcessingCode().equals("102501"))
				{
					setResponsePage(BankPortalCustomCashOutPage.class);
				}
					
			};
		});

		add(form);
	}
}
