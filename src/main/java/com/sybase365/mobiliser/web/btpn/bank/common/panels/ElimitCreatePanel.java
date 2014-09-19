package com.sybase365.mobiliser.web.btpn.bank.common.panels;


import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
//import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
//import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
//import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
//import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitCreateConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
//import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the ElimitCreatePanel page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitCreatePanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnBaseBankPortalSelfCarePage basePage;

	protected BtpnMobiliserBasePage mobBasePage;
	
	protected ElimitBean limitBean;

	public ElimitCreatePanel(String id,
			BtpnBaseBankPortalSelfCarePage basePage, ElimitBean limitBean) {
		super(id);
		this.basePage = basePage;
		this.mobBasePage = basePage;
		this.limitBean = limitBean;
		constructPanel();
		addDateHeaderContributor();
	}

	protected void constructPanel() {

		final Form<ElimitCreatePanel> form = new Form<ElimitCreatePanel>(
				"limitCreateForm",
				new CompoundPropertyModel<ElimitCreatePanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		
		// Choice Renderer
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		
		form.add(new TextField<String>("limitBean.description").setRequired(true).setEnabled(true));
		
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("limitBean.selectedPiType",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, this, true, false).setNullValid(false)
				.setChoiceRenderer(codeValueChoiceRender).setRequired(false).add(new ErrorIndicator()));
		
//		form.add(new TextField<String>("limitBean.piType").setEnabled(true));
		
		form.add(new TextField<String>("limitBean.pi").setEnabled(true));
		
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("limitBean.selectedCustomerType",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, this, true, false).setNullValid(false)
				.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		
//		form.add(new TextField<String>("limitBean.customerType").setRequired(true) .setEnabled(true));
		
		form.add(new TextField<String>("limitBean.customer").setEnabled(true));
		
		
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("limitBean.selectedUseCases",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_USECASE, this, true, false).setNullValid(false)
				.setChoiceRenderer(codeValueChoiceRender).setRequired(false).add(new ErrorIndicator()));
		
//		form.add(new TextField<String>("limitBean.useCase").setEnabled(true));
		
		/*
		form.add(new AmountTextField<Long>("limitBean.singleDebitMinAmount",Long.class));
		form.add(new AmountTextField<Long>("limitBean.singleDebitMaxAmount",Long.class, false).setRequired(false));
		
		form.add(new AmountTextField<Long>("limitBean.singleCreditMinAmount",Long.class, false).setRequired(false));
		form.add(new AmountTextField<Long>("limitBean.singleCreditMaxAmount",Long.class, false).setRequired(false));
		form.add(new AmountTextField<Long>("limitBean.dailyDebitMaxAmount",Long.class, false).setRequired(false));
		form.add(new AmountTextField<Long>("limitBean.weeklyDebitMaxAmount",Long.class, false).setRequired(false));
		form.add(new AmountTextField<Long>("limitBean.monthlyDebitMaxAmount",Long.class, false).setRequired(false));
		form.add(new AmountTextField<Long>("limitBean.dailyCreditMaxAmount",Long.class, false).setRequired(false));
		form.add(new AmountTextField<Long>("limitBean.weeklyCreditMaxAmount",Long.class, false).setRequired(false));
		form.add(new AmountTextField<Long>("limitBean.monthlyCreditMaxAmount",Long.class, false).setRequired(false));
			*/
		form.add(new TextField<String>("limitBean.singleDebitMinAmount"));
		form.add(new TextField<String>("limitBean.singleDebitMaxAmount").setRequired(false));
		form.add(new TextField<String>("limitBean.singleCreditMinAmount").setRequired(false));
		form.add(new TextField<String>("limitBean.singleCreditMaxAmount").setRequired(false));
		form.add(new TextField<String>("limitBean.dailyDebitMaxAmount").setRequired(false));
		form.add(new TextField<String>("limitBean.weeklyDebitMaxAmount").setRequired(false));
		form.add(new TextField<String>("limitBean.monthlyDebitMaxAmount").setRequired(false));
		form.add(new TextField<String>("limitBean.dailyCreditMaxAmount").setRequired(false));
		form.add(new TextField<String>("limitBean.weeklyCreditMaxAmount").setRequired(false));
		form.add(new TextField<String>("limitBean.monthlyCreditMaxAmount").setRequired(false));
		
		form.add(new TextField<String>("limitBean.dailyDebitMaxCount"));
		form.add(new TextField<String>("limitBean.weeklyDebitMaxCount"));
		form.add(new TextField<String>("limitBean.monthlyDebitMaxCount"));
		form.add(new TextField<String>("limitBean.dailyCreditMaxCount"));
		form.add(new TextField<String>("limitBean.weeklyCreditMaxCount"));
		form.add(new TextField<String>("limitBean.monthlyCreditMaxCount"));
		form.add(new TextField<String>("limitBean.maximumBalance"));
		form.add(new TextField<String>("limitBean.minimumBalance"));
		
		form.add(new Label("limitBean.status", "INITIAL").setEnabled(true));	

		Button editButton = new Button("submitCreateButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
					setResponsePage(new ElimitCreateConfirmPage(limitBean));
			}

		};

		form.add(editButton);

		Button cancelButton = new Button("cancelCreateButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				// / back to Home
				setResponsePage(ElimitPage.class);
			};
		}.setDefaultFormProcessing(false);
		form.add(cancelButton);

		add(form);
	}

	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.getLocalizer().getString(
				"datepicker.chooseDate", basePage);
		final String locale = this.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale,
				BtpnConstants.DATE_FORMAT_PATTERN_PICKER, chooseDtTxt)));
	}
	
}
