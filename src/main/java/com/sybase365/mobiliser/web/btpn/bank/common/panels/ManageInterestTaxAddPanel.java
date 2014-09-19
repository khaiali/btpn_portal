package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageInterestTaxBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageinteresttax.ManageInterestTaxPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Interest page for bank portals.
 * 
 * @author Feny Yanti
 */
public class ManageInterestTaxAddPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageInterestTaxAddPanel.class);
	
	protected BtpnMobiliserBasePage basePage;
	private FeedbackPanel feedBackPanel;
	private ManageInterestTaxBean interestTaxBean;

	private String percentageFee;
	private Component descComp;
	private Component noteComp;
	private Component custIdentComp;
	private Component custIdentTypeComp;
	private Component custTypeIdComp;
	private Component piIdComp;
	private Component piTypeIdComp;
	private Component accGLCodeComp;
	private Component taxGLCodeComp;
	private Component validFromComp;
	private Component thresholdAmountComp;
	private Component fixedFeeComp;
	private Component percentageFeeComp;
	private Component maxFeeComp;
	private Component minFeeComp;
	
	
	/**
	 * Constructor for this page.
	 */
	public ManageInterestTaxAddPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		addDateHeaderContributor();
		constructPanel();
	}
	
	/**
	 * Constructor for this page.
	 */
	public ManageInterestTaxAddPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageInterestTaxBean interestTaxBean) {
		super(id);
		this.basePage = basePage;
		this.interestTaxBean = interestTaxBean;
		addDateHeaderContributor();
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		
		Form<ManageInterestTaxAddPanel> form = new Form<ManageInterestTaxAddPanel>("interestTaxAddForm",
			new CompoundPropertyModel<ManageInterestTaxAddPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedBackPanel);
		
		/* DESCRIPTION */
		form.add(descComp = new TextField<String>("interestTaxBean.description").setRequired(true).
				add(new ErrorIndicator()));
		descComp.setOutputMarkupId(true);
		
		//CUST IDENTIFIER TYPE & CUST IDENTIFIER
		form.add(custIdentTypeComp = new TextField<String>("interestTaxBean.customerIdentifierType", Model.of(String.valueOf(BtpnConstants.IDENTIFICATION_TYPE_MOBILE_NO)) ));
		custIdentTypeComp.setOutputMarkupId(true);
	
		final String mobileRegex = basePage.getAgentPortalPrefsConfig().getMobileRegex();
		form.add(custIdentComp = new TextField<String>("interestTaxBean.customerIdentifier")
				.add(BtpnConstants.PHONE_NUMBER_VALIDATOR).add(new PatternValidator(mobileRegex))
				.add(BtpnConstants.PHONE_NUMBER_MAX_LENGTH).add(new ErrorIndicator()));
		custIdentComp.setOutputMarkupId(true);
	
		
		//CUST TYPEID
		final IChoiceRenderer<CodeValue> custType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(custTypeIdComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("interestTaxBean.customerTypeId",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, this, true, false)
				.setChoiceRenderer(custType).add(new ErrorIndicator()));
		custTypeIdComp.setOutputMarkupId(true);
		
	
		// PI Id & PI TypeId */
		form.add(piIdComp = new TextField<String>("interestTaxBean.paymentInstrumentId").add(new ErrorIndicator()));
		piIdComp.setOutputMarkupId(true);
		
		final IChoiceRenderer<CodeValue> PiType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(piTypeIdComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("interestTaxBean.paymentInstrumentTypeId",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, this, true, false)
				.setChoiceRenderer(PiType).add(new ErrorIndicator()));
		piTypeIdComp.setOutputMarkupId(true);
		

		//ACC GL CODE & EXP GL CODE
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		
		form.add(accGLCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("interestTaxBean.accrueGLCode",
				CodeValue.class, BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		accGLCodeComp.setOutputMarkupId(true);
	
		form.add(taxGLCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("interestTaxBean.taxGLCode",
				CodeValue.class, BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		taxGLCodeComp.setOutputMarkupId(true);
	
		//VALID FROM 
		DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("interestTaxBean.validFrom",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true).add(new ErrorIndicator());
		validFromComp = fromDate;
		form.add(validFromComp);
		
		/* THRESHOLD AMOUNT */
		form.add(thresholdAmountComp = new AmountTextField<Long>("interestTaxBean.thresholdAmount", Long.class, true).setRequired(true)
				.add(new ErrorIndicator()));
		thresholdAmountComp.setOutputMarkupId(true);
	
		/* FIXED FEE */
		form.add(fixedFeeComp = new AmountTextField<Long>("interestTaxBean.fixedFee", Long.class,true).setRequired(true).add(new ErrorIndicator()));
		fixedFeeComp.setOutputMarkupId(true);
		
		/* PERCENTAGE FEE */
		form.add(percentageFeeComp = new TextField<String>("interestTaxBean.percentageFee").setRequired(true)
				.add(new PatternValidator(BtpnConstants.REGEX_PERCENT))
				.add(new ErrorIndicator()));
		percentageFeeComp.setOutputMarkupId(true);
		
		/* MAXIMUM FEE */
		form.add(maxFeeComp = new AmountTextField<Long>("interestTaxBean.maximumFee", Long.class,false).add(new ErrorIndicator()));
		maxFeeComp.setOutputMarkupId(true);
		
		/* MINIMUM FEE */
		form.add(minFeeComp = new AmountTextField<Long>("interestTaxBean.minimumFee", Long.class,true).setRequired(true).add(new ErrorIndicator()));
		minFeeComp.setOutputMarkupId(true);
		
		/* NOTE */
		form.add(noteComp = new TextField<String>("interestTaxBean.note").add(new ErrorIndicator()));
		noteComp.setOutputMarkupId(true);
		log.info(" ### (ManageInterestAddPanel)  ### Before btnSubmit ");
		

		form.add(new Button("btnSubmit") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				if (!PortalUtils.exists(interestTaxBean)) {
					interestTaxBean = new ManageInterestTaxBean();
				}
				setResponsePage(new ManageInterestTaxConfirmPage(interestTaxBean, BtpnConstants.ADD));
			}
		});
		
		form.add(new AjaxButton("btnCancel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageInterestTaxPage.class);
			}
		}.setDefaultFormProcessing(false));
		
		// Add add Button
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
