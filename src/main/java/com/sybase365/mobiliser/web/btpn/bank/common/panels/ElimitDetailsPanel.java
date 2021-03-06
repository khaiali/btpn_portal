package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitDeleteConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitEditPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the CashoutDetailsPanel page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnBaseBankPortalSelfCarePage basePage;

	protected ElimitBean limitBean;

	public ElimitDetailsPanel(String id,
			BtpnBaseBankPortalSelfCarePage basePage, ElimitBean limitBean) {
		super(id);
		this.basePage = basePage;
		this.limitBean = limitBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashinDetailsPanel> form = new Form<BankPortalCashinDetailsPanel>(
				"limitDetailsForm",
				new CompoundPropertyModel<BankPortalCashinDetailsPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);

		form.add(new Label("limitBean.description").setEnabled(false));
		form.add(new Label("limitBean.selectedPiType", limitBean.getSelectedPiType() == null ? "-|-": limitBean.getSelectedPiType().getIdAndValue()).setEnabled(false));
		form.add(new Label("limitBean.pi").setEnabled(false));
		form.add(new Label("limitBean.selectedCustomerType", limitBean.getSelectedCustomerType()==null ?"-|-": limitBean.getSelectedCustomerType().getIdAndValue()).setEnabled(false));
		form.add(new Label("limitBean.customer").setEnabled(false));
		form.add(new Label("limitBean.selectedUseCases", limitBean.getSelectedUseCases()==null ?"-|-" : limitBean.getSelectedUseCases().getIdAndValue()).setEnabled(false));
		
		/*
		form.add(new AmountLabel("limitBean.singleDebitMinAmount"));
		form.add(new AmountLabel("limitBean.singleDebitMaxAmount"));
		form.add(new AmountLabel("limitBean.singleCreditMinAmount"));
		form.add(new AmountLabel("limitBean.singleCreditMaxAmount"));
		
		form.add(new AmountLabel("limitBean.dailyDebitMaxAmount"));
		form.add(new AmountLabel("limitBean.weeklyDebitMaxAmount"));
		form.add(new AmountLabel("limitBean.monthlyDebitMaxAmount"));
		form.add(new AmountLabel("limitBean.dailyCreditMaxAmount"));
		form.add(new AmountLabel("limitBean.weeklyCreditMaxAmount"));
		form.add(new AmountLabel("limitBean.monthlyCreditMaxAmount"));
		*/
		
		form.add(new Label("limitBean.singleDebitMinAmount"));
		form.add(new Label("limitBean.singleDebitMaxAmount"));
		form.add(new Label("limitBean.singleCreditMinAmount"));
		form.add(new Label("limitBean.singleCreditMaxAmount"));
		
		form.add(new Label("limitBean.dailyDebitMaxAmount"));
		form.add(new Label("limitBean.weeklyDebitMaxAmount"));
		form.add(new Label("limitBean.monthlyDebitMaxAmount"));
		form.add(new Label("limitBean.dailyCreditMaxAmount"));
		form.add(new Label("limitBean.weeklyCreditMaxAmount"));
		form.add(new Label("limitBean.monthlyCreditMaxAmount"));
		
		form.add(new Label("limitBean.dailyDebitMaxCount"));
		form.add(new Label("limitBean.weeklyDebitMaxCount"));
		form.add(new Label("limitBean.monthlyDebitMaxCount"));
		form.add(new Label("limitBean.dailyCreditMaxCount"));
		form.add(new Label("limitBean.weeklyCreditMaxCount"));
		form.add(new Label("limitBean.monthlyCreditMaxCount"));
		
		form.add(new Label("limitBean.maximumBalance").setEnabled(false));
		form.add(new Label("limitBean.minimumBalance").setEnabled(false));
		form.add(new Label("limitBean.creationDate").setEnabled(false));
		form.add(new Label("limitBean.creator").setEnabled(false));
		form.add(new Label("limitBean.status", "INITIAL").setEnabled(false));

		Button editButton = new Button("editButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new ElimitEditPage(limitBean));

			};
		};
		form.add(editButton);

		Button deleteButton = new Button("deleteButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new ElimitDeleteConfirmPage(limitBean));

			};
		};

		form.add(deleteButton);

		add(form);
	}

}
