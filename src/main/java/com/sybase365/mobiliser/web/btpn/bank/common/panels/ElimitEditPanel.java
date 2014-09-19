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
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
//import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ElimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitEditConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.limit.ElimitPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
//import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the CashoutDetailsPanel page for bank portals.
 * 
 * @author Febrie Subhan
 */
public class ElimitEditPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnBaseBankPortalSelfCarePage basePage;

	protected ElimitBean limitBean; 

	public ElimitEditPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ElimitBean limitBean) {
		super(id);
		this.basePage = basePage;
		this.limitBean = limitBean;
		addDateHeaderContributor();
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BankPortalCashinDetailsPanel> 
		form = new Form<BankPortalCashinDetailsPanel>("limitEditForm",
			new CompoundPropertyModel<BankPortalCashinDetailsPanel>(this));
		// Add feedback panel for Error Messages
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);

		// Choice Renderer
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);

		form.add(new TextField<String>("limitBean.description"));
		
		form.add(new TextField<String>("limitBean.customer").setEnabled(false));
		
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("limitBean.selectedPiType",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, this, true, false).setNullValid(false)
				.setChoiceRenderer(codeValueChoiceRender).setRequired(false).setEnabled(false).add(new ErrorIndicator()));
		
//		form.add(new TextField<String>("limitBean.piType").setEnabled(false));
		
		form.add(new TextField<String>("limitBean.pi").setEnabled(false));
		
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("limitBean.selectedCustomerType",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, this, true, false).setNullValid(false)
				.setChoiceRenderer(codeValueChoiceRender).setRequired(false).setEnabled(false).add(new ErrorIndicator()));
		
//		form.add(new TextField<String>("limitBean.customerType").setEnabled(false));
		
		
//		form.add(new TextField<String>("limitBean.useCase").setEnabled(false));
		
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("limitBean.selectedUseCases",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_USECASE, this, true, false).setNullValid(false)
				.setChoiceRenderer(codeValueChoiceRender).setRequired(false).setEnabled(false).add(new ErrorIndicator()));
		/*
		
		form.add(new AmountTextField<Long>("limitBean.singleDebitMinAmount",Long.class));
		form.add(new AmountTextField<Long>("limitBean.singleDebitMaxAmount",Long.class, false).setRequired(true));
		form.add(new AmountTextField<Long>("limitBean.singleCreditMinAmount",Long.class, false).setRequired(true));
		form.add(new AmountTextField<Long>("limitBean.singleCreditMaxAmount",Long.class, false).setRequired(true));
		*/
		form.add(new TextField<String>("limitBean.singleDebitMinAmount"));
		form.add(new TextField<String>("limitBean.singleDebitMaxAmount"));
		form.add(new TextField<String>("limitBean.singleCreditMinAmount"));
		form.add(new TextField<String>("limitBean.singleCreditMaxAmount"));
		
		/*
		form.add(new AmountTextField<Long>("limitBean.dailyDebitMaxAmount",Long.class, true).setRequired(true));
		form.add(new AmountTextField<Long>("limitBean.weeklyDebitMaxAmount",Long.class, true).setRequired(true));
		form.add(new AmountTextField<Long>("limitBean.monthlyDebitMaxAmount",Long.class, false).setRequired(true));
		form.add(new AmountTextField<Long>("limitBean.dailyCreditMaxAmount",Long.class, false).setRequired(true));
		form.add(new AmountTextField<Long>("limitBean.weeklyCreditMaxAmount",Long.class, false).setRequired(true));
		form.add(new AmountTextField<Long>("limitBean.monthlyCreditMaxAmount",Long.class, false).setRequired(true));
		*/
		
		form.add(new TextField<String>("limitBean.dailyDebitMaxAmount"));
		form.add(new TextField<String>("limitBean.weeklyDebitMaxAmount"));
		form.add(new TextField<String>("limitBean.monthlyDebitMaxAmount"));
		form.add(new TextField<String>("limitBean.dailyCreditMaxAmount"));
		form.add(new TextField<String>("limitBean.weeklyCreditMaxAmount"));
		form.add(new TextField<String>("limitBean.monthlyCreditMaxAmount"));

		
		
		form.add(new TextField<String>("limitBean.dailyDebitMaxCount"));
		form.add(new TextField<String>("limitBean.weeklyDebitMaxCount"));
		form.add(new TextField<String>("limitBean.monthlyDebitMaxCount"));
		form.add(new TextField<String>("limitBean.dailyCreditMaxCount"));
		form.add(new TextField<String>("limitBean.weeklyCreditMaxCount"));
		form.add(new TextField<String>("limitBean.monthlyCreditMaxCount"));
		form.add(new TextField<String>("limitBean.maximumBalance"));
		form.add(new TextField<String>("limitBean.minimumBalance"));
		
		form.add(new Label("limitBean.status", "INITIAL").setEnabled(true));	
		form.add(new Label("limitBean.creationDate").setEnabled(false));
		form.add(new Label("limitBean.creator").setEnabled(false));
		
		/*
		form.add(new TextField<String>("limitBean.creator").setEnabled(true));

		DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("limitBean.creationDate",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.add(DateValidator.minimum(new Date(),
						BtpnConstants.ID_EXPIRY_DATE_PATTERN))
				.setRequired(false).add(new ErrorIndicator());
		form.add(fromDate);
		*/

		Button submitEdit = new Button("submitEdit"){
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(){
				setResponsePage(new ElimitEditConfirmPage(limitBean));
				
			}
		};
		
		form.add(submitEdit);
		
		Button cancelEdit = new Button("cancelEdit"){
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(){
				setResponsePage(ElimitPage.class);
				
			}
		}.setDefaultFormProcessing(false);
		
		form.add(cancelEdit);
		
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
