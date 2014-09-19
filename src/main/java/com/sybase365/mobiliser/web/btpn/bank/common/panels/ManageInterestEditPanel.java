package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinterest.ManageInterestPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Feny Yanti 
 */
public class ManageInterestEditPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageInterestEditPanel.class);
	
	protected BtpnMobiliserBasePage basePage;
	private FeedbackPanel feedBackPanel;
	private ManageInterestBean interestBean;
	WebMarkupContainer interestContainer;
	
	private Component descComp;
	private Component noteComp;
	private Component custIdentComp;
	private Component custIdentTypeComp;
	private Component custTypeIdComp;
	private Component piIdComp;
	private Component piTypeIdComp;
	private Component accGLCodeComp;
	private Component expGLCodeComp;
	private Component validFromComp;
	private Component thresholdAmountComp;
	private Component fixedFeeComp;
	private Component percentageFeeComp;
	private Component maxFeeComp;
	private Component minFeeComp;

	/**
	 * Constructor for this page.
	 */
	public ManageInterestEditPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageInterestBean interestBean) {
		super(id);
		this.basePage = basePage;
		this.interestBean = interestBean;
		addDateHeaderContributor();
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		
		Form<ManageInterestEditPanel> form = new Form<ManageInterestEditPanel>("interestEditForm",
			new CompoundPropertyModel<ManageInterestEditPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedBackPanel);
		
		/* DESCRIPTION */
		form.add(descComp = new TextField<String>("interestBean.description").add(new ErrorIndicator()));
		descComp.setOutputMarkupId(true);
		
		//CUST IDENTIFIER TYPE & CUST IDENTIFIERID
		form.add(custIdentTypeComp = new TextField<String>("interestBean.customerIdentifierType", Model.of(String.valueOf(BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO)) ));
		custIdentTypeComp.setOutputMarkupId(true);
		custIdentTypeComp.setEnabled(false);
		
		final String mobileRegex = basePage.getAgentPortalPrefsConfig().getMobileRegex();
		form.add(custIdentComp = new TextField<String>("interestBean.customerIdentifier").setRequired(true)
				.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(new PatternValidator(mobileRegex))
				.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH).add(new ErrorIndicator()));
		custIdentComp.setOutputMarkupId(true);
		custIdentComp.setEnabled(false);
		
		//CUST TYPEID
		final IChoiceRenderer<CodeValue> custType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(custTypeIdComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("interestBean.customerTypeId",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, this, true, false)
				.setChoiceRenderer(custType).setRequired(true).add(new ErrorIndicator()));
		custTypeIdComp.setOutputMarkupId(true);
		custTypeIdComp.setEnabled(false);
	
		// PI Id & PI TypeId */
		form.add(piIdComp = new TextField<String>("interestBean.paymentInstrumentId").add(new ErrorIndicator()));
		piIdComp.setOutputMarkupId(true);
		piIdComp.setEnabled(false);
		
		final IChoiceRenderer<CodeValue> PiType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(piTypeIdComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("interestBean.paymentInstrumentTypeId",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, this, true, false)
				.setChoiceRenderer(PiType).setRequired(true).add(new ErrorIndicator()));
		piTypeIdComp.setOutputMarkupId(true);
		piTypeIdComp.setEnabled(false);

		//ACC GL CODE & EXP GL CODE
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		
		form.add(accGLCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("interestBean.accrueGLCode",
				CodeValue.class, BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		accGLCodeComp.setOutputMarkupId(true);
		
		form.add(expGLCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("interestBean.expenseGLCode",
				CodeValue.class, BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		expGLCodeComp.setOutputMarkupId(true);
				
		//VALID FROM 
		form.add(validFromComp = new TextField<String>("interestBean.validFrom").add(new ErrorIndicator()));
		validFromComp.setOutputMarkupId(true);
		validFromComp.setEnabled(false);
		
		//THRESHOLD AMOUNT
		form.add(thresholdAmountComp = new AmountTextField<Long>("interestBean.thresholdAmount", Long.class, true).setRequired(true)
				.add(new ErrorIndicator()));
		thresholdAmountComp.setOutputMarkupId(true);
	
		//FIXED FEE
		form.add(fixedFeeComp = new AmountTextField<Long>("interestBean.fixedFee", Long.class,true).add(new ErrorIndicator()));
		fixedFeeComp.setOutputMarkupId(true);
		
		//PERCENTAGE FEE
		form.add(percentageFeeComp = new TextField<String>("interestBean.percentageFee").setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGEX_PERCENT))
				.add(new ErrorIndicator()));
		percentageFeeComp.setOutputMarkupId(true);
		
		//MAXIMUM FEE
		form.add(maxFeeComp = new AmountTextField<Long>("interestBean.maximumFee", Long.class,false).add(new ErrorIndicator()));
		maxFeeComp.setOutputMarkupId(true);
		
		//MINIMUM FEE
		form.add(minFeeComp = new AmountTextField<Long>("interestBean.minimumFee", Long.class,true).add(new ErrorIndicator()));
		minFeeComp.setOutputMarkupId(true);
		
		//NOTE
		form.add(noteComp = new TextField<String>("interestBean.note").add(new ErrorIndicator()));
		noteComp.setOutputMarkupId(true);

		
		form.add(new Button("btnSubmit") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				if (!PortalUtils.exists(interestBean)) {
					interestBean = new ManageInterestBean();
				}
				
				setResponsePage(new ManageInterestConfirmPage(interestBean, BtpnConstants.UPDATE));
			}
		});
		
		form.add(new AjaxButton("btnCancel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageInterestPage.class);
			}
		}.setDefaultFormProcessing(false));
		
		// Add add Button
		add(form);
	}
	
	/**
	 * Adds the date header contributor
	 */
	protected void addDateHeaderContributor() {
		final String chooseDtTxt = this.basePage.getLocalizer().getString(
				"datepicker.chooseDate", basePage);
		final String locale = this.basePage.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale,
				BtpnConstants.DATE_FORMAT_PATTERN_PICKER, chooseDtTxt)));
	}
	
}
