package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the Manage Fee page for bank portals.
 * 
 * @author Andi Samallangi W
 */
public class ManageCustomUseCaseFeeAddPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageCustomUseCaseFeeAddPanel.class);
	
	protected BtpnMobiliserBasePage basePage;
	private FeedbackPanel feedBackPanel;
	
	private ManageCustomUseCaseFeeBean ucFeeBean;
	private FeeEntryWrkType feeDetailsBean;
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private String percentageFee;
	
	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private String feesTotalItemString;
	
	private BtpnCustomPagingNavigator navigator;

	private Component useCaseComp;
	private Component debitOrgUnitComp;
	private Component creditOrgUnitComp;
	private Component debitPiTypeComp;
	private Component creditPiTypeComp;
	private Component glCodeComp;
	private Component customerTypeComp;
	private Component validFrom;
	private Component payeeFeeComp;
	private Component fixedFeeComp;
	private Component percentageFeeComp;
	private Component theresholdAmountComp;
	private Component maxFeeComp;
	private Component minFeeComp;
	private Component currencyCodeComp;
	private Component noteComp;
	private Component descComp;

	/**
	 * Constructor for this page.
	 */
	public ManageCustomUseCaseFeeAddPanel(String id, BtpnBaseBankPortalSelfCarePage basePage) {
		super(id);
		this.basePage = basePage;
		addDateHeaderContributor();
		constructPanel();
	}
	
	/**
	 * Constructor for this page.
	 */
	public ManageCustomUseCaseFeeAddPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomUseCaseFeeBean ucFeeBean) {
		super(id);
		this.basePage = basePage;
		this.ucFeeBean = ucFeeBean == null ? new ManageCustomUseCaseFeeBean() : ucFeeBean;
		addDateHeaderContributor();
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		
		Form<ManageCustomUseCaseFeeAddPanel> form = new Form<ManageCustomUseCaseFeeAddPanel>("ucFeeAddForm",
			new CompoundPropertyModel<ManageCustomUseCaseFeeAddPanel>(this));
		
		// Add feedback panel for Error Messages
		feedBackPanel = new FeedbackPanel("errorMessages");
		feedBackPanel.setOutputMarkupId(true);
		feedBackPanel.setOutputMarkupPlaceholderTag(true);
		form.add(feedBackPanel);
		
		/* USE CASE */
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderUC = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(useCaseComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.useCase",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_USE_CASES, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderUC).setRequired(true).add(new ErrorIndicator()));
		useCaseComp.setOutputMarkupId(true);
		
		/* DEBIT ORG UNIT */
		final IChoiceRenderer<CodeValue> debitOrgUnit = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(debitOrgUnitComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.debitOrgUnit",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, this, true, false)
				.setChoiceRenderer(debitOrgUnit).setRequired(true).add(new ErrorIndicator()));
		debitOrgUnitComp.setOutputMarkupId(true);
		
		/* CREDIT ORG UNIT */
		final IChoiceRenderer<CodeValue> creditOrgUnit = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(creditOrgUnitComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.creditOrgUnit",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, this, true, false)
				.setChoiceRenderer(creditOrgUnit).setRequired(true).add(new ErrorIndicator()));
		creditOrgUnitComp.setOutputMarkupId(true);
		
		/* GL CODE */
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(glCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.glCode",
				CodeValue.class, BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		glCodeComp.setOutputMarkupId(true);
		
		
		/* DEBIT PI TYPE */
		final IChoiceRenderer<CodeValue> debitPiType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(debitPiTypeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.debitPiType",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, this, true, false)
				.setChoiceRenderer(debitPiType).add(new ErrorIndicator()));
		debitPiTypeComp.setOutputMarkupId(true);
		
		/* CREDIT PI TYPE */
		final IChoiceRenderer<CodeValue> creditPiType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(creditPiTypeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.creditPiType",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, this, true, false)
				.setChoiceRenderer(creditPiType).add(new ErrorIndicator()));
		creditPiTypeComp.setOutputMarkupId(true);
		
		/* CUSTOMER TYPE */
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderCT = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(customerTypeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.customerType",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderCT).add(new ErrorIndicator()));
		customerTypeComp.setOutputMarkupId(true);
		
		/*  VALID FROM */
		DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("ucFeeBean.validFrom",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(false).add(new ErrorIndicator());
		validFrom = fromDate;
		form.add(validFrom);
		
		// Add check box apply to payee
		form.add(payeeFeeComp = new CheckBox("ucFeeBean.payeeFee").add(new ErrorIndicator()));
		payeeFeeComp.setOutputMarkupId(true);
		
		/* CURRENCY CODE */
		final IChoiceRenderer<CodeValue> codeValueChoiceCurCode = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		form.add(currencyCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.currencyCode",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CURRENCY, this, true, false)
				.setChoiceRenderer(codeValueChoiceCurCode).setRequired(true).add(new ErrorIndicator()));
		currencyCodeComp.setOutputMarkupId(true);
		
		/* DESCRIPTION */
		form.add(descComp = new TextField<String>("ucFeeBean.description").setRequired(true).add(new ErrorIndicator()));
		descComp.setOutputMarkupId(true);
		
		/* NOTE */
		form.add(noteComp = new TextField<String>("ucFeeBean.note").add(new ErrorIndicator()));
		noteComp.setOutputMarkupId(true);

		
		// add Slab Fee Container;
		final WebMarkupContainer slabFeeContainer = new WebMarkupContainer("slabFeeContainer");
		
			//FIXED FEE
			slabFeeContainer.add(fixedFeeComp = new TextField<Long>("feeDetailsBean.fixedFee").setRequired(true).add(new ErrorIndicator()));
			fixedFeeComp.setOutputMarkupId(true);
			
			//PERCENTAGE FEE
			slabFeeContainer.add(percentageFeeComp = new TextField<String>("percentageFee")
					.setRequired(true)
					//.add( new RangeValidator<String>( String.valueOf("0"), String.valueOf("100")) )
					.add(new PatternValidator(BtpnConstants.REGEX_PERCENT))
					.add(new ErrorIndicator()));
			percentageFeeComp.setOutputMarkupId(true);
			
			//MAXIMUM FEE
			slabFeeContainer.add(maxFeeComp = new TextField<Long>("feeDetailsBean.maximumFee").add(new ErrorIndicator()));
			maxFeeComp.setOutputMarkupId(true);
			
			//MINIMUM FEE
			slabFeeContainer.add(minFeeComp = new TextField<Long>("feeDetailsBean.minimumFee").setRequired(true).add(new ErrorIndicator()));
			minFeeComp.setOutputMarkupId(true);
			
			//THERESHOLD AMOUNT
			slabFeeContainer.add(theresholdAmountComp = new TextField<Long>("feeDetailsBean.thresholdAmount").setRequired(true)
					.add(new ErrorIndicator()));
			theresholdAmountComp.setOutputMarkupId(true);
	
			notificationManageFeesDataView(slabFeeContainer);
			slabFeeContainer.add(addSlabFeeButton(slabFeeContainer));
			slabFeeContainer.setOutputMarkupId(true);
			slabFeeContainer.setOutputMarkupPlaceholderTag(true);
			slabFeeContainer.setVisibilityAllowed(true);
			slabFeeContainer.setVisible(true);
		form.add(slabFeeContainer);
				

		form.add(new AjaxButton("btnSubmit") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(new ManageCustomUseCaseFeeConfirmPage(ucFeeBean, BtpnConstants.ADD));
			}
		}.setDefaultFormProcessing(true));
		
		form.add(new AjaxButton("btnCancel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageCustomUseCaseFeePage.class);
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
	
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final DataView<FeeEntryWrkType> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<FeeEntryWrkType>(ucFeeBean.getManageDetailsWrkList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageDetailsWrkList().size() != 0 ;
			}
			
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageDetailsWrkList().size() == 0;

			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageCustomUseCaseFeeAddPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageCustomUseCaseFeeAddPanel.this);

		// Add the header
		IModel<String> headerDisplayModel = new AbstractReadOnlyModel<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return String.format(displayTotalItemsText, feesTotalItemString, feeStartIndex, feeEndIndex);
			}
		};

		// Add the fee header
		feeHeader = new Label(WICKET_ID_FEESTOTALITEMS, headerDisplayModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageDetailsWrkList().size() != 0;
			}
		};
		dataViewContainer.add(feeHeader);
		feeHeader.setOutputMarkupId(true);
		feeHeader.setOutputMarkupPlaceholderTag(true);

		dataViewContainer.addOrReplace(dataView);
	}
	
	
	/**
	 * This is the ManageFeeDetailsView for Manage Fees of Sharing and Slab Fee
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageBillPaymentDetailsView extends DataView<FeeEntryWrkType> {

		private static final long serialVersionUID = 1L;

		protected ManageBillPaymentDetailsView(String id, IDataProvider<FeeEntryWrkType> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
		}

		@Override
		protected void onBeforeRender() {
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<FeeEntryWrkType> item) {
			final FeeEntryWrkType entry = item.getModelObject();
			
			item.setModel(new CompoundPropertyModel<FeeEntryWrkType>(entry));
			
			item.add(new Label("minFee", String.valueOf(entry.getMinimumFee()) ));
			item.add(new Label("maxFee", (entry.getMaximumFee()!=null) ? String.valueOf(entry.getMaximumFee()) : "" ));
			item.add(new Label("fixedFee", Long.toString(entry.getFixedFee()/100)));
			item.add(new Label("percentageFee", getDiv100(entry.getPercentageFee())));
			item.add(new Label("thresholdAmount", Long.valueOf(entry.getThresholdAmount()/100).toString() ));
			
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ucFeeBean.getManageDetailsWrkList().size() != 0;
		}

		private void refreshTotalItemCount() {
			final int size = internalGetDataProvider().size();
			feesTotalItemString = new Integer(size).toString();
			if (size > 0) {
				feeStartIndex = getCurrentPage() * getItemsPerPage() + 1;
				feeEndIndex = feeStartIndex + getItemsPerPage() - 1;
				if (feeEndIndex > size) {
					feeEndIndex = size;
				}
			} else {
				feeStartIndex = 0;
				feeEndIndex = 0;
			}
		}	
	}
	
	private static String getDiv100(long amount) {
		return new BigDecimal(amount).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString();
	}
	
	private Button addSlabFeeButton(final WebMarkupContainer slabFeeContainer) {
		AjaxButton slabButton = new AjaxButton("slabFeeButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					feeDetailsBean.setFixedFee(feeDetailsBean.getFixedFee()*100);
					feeDetailsBean.setPercentageFee(new BigDecimal(percentageFee).movePointRight(2).setScale(0, RoundingMode.DOWN).longValue());
					feeDetailsBean.setThresholdAmount( feeDetailsBean.getThresholdAmount()*100 );
					
					ucFeeBean.getManageDetailsWrkList().add((FeeEntryWrkType) BeanUtils.cloneBean(feeDetailsBean));
					feeDetailsBean = null;
					percentageFee = null;
					target.addComponent(feedBackPanel);
					target.addComponent(feeHeader);
					target.addComponent(slabFeeContainer);
					target.addComponent(navigator);
					
				
				} catch (Exception e) {
					log.debug("Exception occured while adding the fee ===> ", e);
					error(getLocalizer().getString("error.add",ManageCustomUseCaseFeeAddPanel.this));
				}
			};
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedBackPanel);
				target.addComponent(feeHeader);
				target.addComponent(slabFeeContainer);
				target.addComponent(navigator);
			};
		};
		slabButton.setVisible(true);
		return slabButton;
	}
	
}
