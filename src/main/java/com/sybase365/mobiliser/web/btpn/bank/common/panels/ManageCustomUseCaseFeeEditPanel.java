package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
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
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageCustomUseCaseFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeeConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageCustomUseCaseFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee page for bank portals.
 */
public class ManageCustomUseCaseFeeEditPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(ManageCustomUseCaseFeeEditPanel.class);
	
	protected BtpnMobiliserBasePage basePage;
	private FeedbackPanel feedBackPanel;
	private ManageCustomUseCaseFeeBean ucFeeBean;
	private ManageBillPaymentFeeDetailsBean feeDetailsBean;
	
	private static final String WICKET_ID_LINK = "detailsLink";
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private String feesTotalItemString;
	
	private String percentageFee;
	
	private BtpnCustomPagingNavigator navigator;
	
	private WebMarkupContainer feeDetailContainer;
	
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
	public ManageCustomUseCaseFeeEditPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageCustomUseCaseFeeBean ucFeeBean) {
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
		
		Form<ManageCustomUseCaseFeeEditPanel> form = new Form<ManageCustomUseCaseFeeEditPanel>("ucFeeEditForm",
			new CompoundPropertyModel<ManageCustomUseCaseFeeEditPanel>(this));
		
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
		useCaseComp.setEnabled(false);
		
		/* DEBIT ORG UNIT */
		final IChoiceRenderer<CodeValue> debitOrgUnit = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(debitOrgUnitComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.debitOrgUnit",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, this, true, false)
				.setChoiceRenderer(debitOrgUnit).add(new ErrorIndicator()));
		debitOrgUnitComp.setOutputMarkupId(true);
		debitOrgUnitComp.setEnabled(false);
		
		/* CREDIT ORG UNIT */
		final IChoiceRenderer<CodeValue> creditOrgUnit = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(creditOrgUnitComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.creditOrgUnit",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, this, true, false)
				.setChoiceRenderer(creditOrgUnit).add(new ErrorIndicator()));
		creditOrgUnitComp.setOutputMarkupId(true);
		creditOrgUnitComp.setEnabled(false);
		
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
		debitPiTypeComp.setEnabled(false);
		
		/* CREDIT PI TYPE */
		final IChoiceRenderer<CodeValue> creditPiType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(creditPiTypeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.creditPiType",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, this, true, false)
				.setChoiceRenderer(creditPiType).add(new ErrorIndicator()));
		creditPiTypeComp.setOutputMarkupId(true);
		creditPiTypeComp.setEnabled(false);
		
		/* CUSTOMER TYPE */
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderCT = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(customerTypeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.customerType",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderCT).setRequired(true).add(new ErrorIndicator()));
		customerTypeComp.setOutputMarkupId(true);
		customerTypeComp.setEnabled(false);
		
		/*  VALID FROM */
//		DateTextField fromDate = (DateTextField) DateTextField
//				.forDatePattern("ucFeeBean.validFrom",
//						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
//				.setRequired(false).add(new ErrorIndicator());
//		validFrom = fromDate;
//		form.add(validFrom);
		
		form.add(validFrom = new TextField<String>("ucFeeBean.validFrom").add(new ErrorIndicator()));
		validFrom.setOutputMarkupId(true);
		validFrom.setEnabled(false);
		
		
		/* PAYEE FEE */
		form.add(payeeFeeComp = new CheckBox("ucFeeBean.payeeFee").add(new ErrorIndicator()));
		payeeFeeComp.setOutputMarkupId(true);
		
		/* CURRENCY CODE */
		final IChoiceRenderer<CodeValue> codeValueChoiceCurCode = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		form.add(currencyCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("ucFeeBean.currencyCode",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CURRENCY, this, true, false)
				.setChoiceRenderer(codeValueChoiceCurCode).setRequired(true).add(new ErrorIndicator()));
		currencyCodeComp.setOutputMarkupId(true);
		currencyCodeComp.setEnabled(false);
		
		/* DESCRIPTION */
		form.add(descComp = new TextField<String>("ucFeeBean.description").add(new ErrorIndicator()));
		descComp.setOutputMarkupId(true);
		
		/* NOTE */
		form.add(noteComp = new TextField<String>("ucFeeBean.note").add(new ErrorIndicator()));
		noteComp.setOutputMarkupId(true);
		
		// add Slab Fee Container;
		final WebMarkupContainer slabFeeContainer = new WebMarkupContainer("slabFeeContainer");	
				notificationManageFeesDataView(slabFeeContainer);
				slabFeeContainer.setOutputMarkupId(true);
				slabFeeContainer.setOutputMarkupPlaceholderTag(true);
				slabFeeContainer.setVisibilityAllowed(true);
				slabFeeContainer.setVisible(true);
		form.add(slabFeeContainer);	

		//detailForm container
		feeDetailContainer  = new WebMarkupContainer("feeDetailContainer");
			//FIXED FEE
			feeDetailContainer.add(fixedFeeComp = new TextField<Long>("feeDetailsBean.fixedFee").setRequired(true).add(new ErrorIndicator()));
			fixedFeeComp.setOutputMarkupId(true);
			
			//PERCENTAGE FEE
			feeDetailContainer.add(percentageFeeComp = new TextField<String>("percentageFee")
					.setRequired(true)
					//.add( new RangeValidator<String>( String.valueOf("0"), String.valueOf("100")) )
					.add(new PatternValidator(BtpnConstants.REGEX_PERCENT))
					.add(new ErrorIndicator()));
			percentageFeeComp.setOutputMarkupId(true);
			
			//MAXIMUM FEE
			feeDetailContainer.add(maxFeeComp = new TextField<Long>("feeDetailsBean.maximumFee").add(new ErrorIndicator()));
			maxFeeComp.setOutputMarkupId(true);	
			
			//MINIMUM FEE
			feeDetailContainer.add(minFeeComp = new TextField<Long>("feeDetailsBean.minimumFee").setRequired(true).add(new ErrorIndicator()));
			minFeeComp.setOutputMarkupId(true);
			
			//THERESHOLD AMOUNT
			feeDetailContainer.add(theresholdAmountComp = new TextField<Long>("feeDetailsBean.thresholdAmount").setRequired(true)
					.add(new ErrorIndicator()));
			theresholdAmountComp.setOutputMarkupId(true);
			feeDetailContainer.add(addSlabFeeButton(slabFeeContainer));
			feeDetailContainer.setOutputMarkupId(true);
			feeDetailContainer.setOutputMarkupPlaceholderTag(true);
			feeDetailContainer.setVisible(false);
		form.add(feeDetailContainer);	
		
		
		form.add(new AjaxButton("btnSubmit") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				
				ucFeeBean.getManageDetailsWrkList().addAll(convertToFeeEntryTypeWrk(ucFeeBean.getManageFeeDetailsList()));
			
				setResponsePage(new ManageCustomUseCaseFeeConfirmPage(ucFeeBean, BtpnConstants.UPDATE));
			}
		});
		
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
		final String chooseDtTxt = this.basePage.getLocalizer().getString(
				"datepicker.chooseDate", basePage);
		final String locale = this.basePage.getLocale().getLanguage();
		add(new HeaderContributor(new DateHeaderContributor(locale,
				BtpnConstants.DATE_FORMAT_PATTERN_PICKER, chooseDtTxt)));
	}
	
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final DataView<ManageBillPaymentFeeDetailsBean> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<ManageBillPaymentFeeDetailsBean>(ucFeeBean.getManageFeeDetailsList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageFeeDetailsList().size() != 0 ;
			}
			
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return ucFeeBean.getManageFeeDetailsList().size() == 0;
			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageCustomUseCaseFeeEditPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageCustomUseCaseFeeEditPanel.this);

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
				return ucFeeBean.getManageFeeDetailsList().size() != 0;
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
	private class ManageBillPaymentDetailsView extends DataView<ManageBillPaymentFeeDetailsBean> {

		private static final long serialVersionUID = 1L;
		
		protected ManageBillPaymentDetailsView(String id, IDataProvider<ManageBillPaymentFeeDetailsBean> dataProvider) {
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
		protected void populateItem(final Item<ManageBillPaymentFeeDetailsBean> item) {
			final ManageBillPaymentFeeDetailsBean entry = item.getModelObject();
			
			item.setModel(new CompoundPropertyModel<ManageBillPaymentFeeDetailsBean>(entry));
			
			item.add(new Label("minFee", String.valueOf(entry.getMinimumFee()) ));
			item.add(new Label("maxFee", (entry.getMaximumFee()!=null) ? String.valueOf(entry.getMaximumFee()) : "" ));
			item.add(new Label("fixedFee", Long.toString(entry.getFixedFee()/100)));
			item.add(new Label("percentageFee", getDiv100(entry.getPercentageFee()/100) ) );
			item.add(new Label("thresholdAmount", Long.valueOf(entry.getThresholdAmount()/100).toString() ));
			// Add the details Link
				final AjaxLink<ManageBillPaymentFeeDetailsBean> detailsLink = new AjaxLink<ManageBillPaymentFeeDetailsBean>(WICKET_ID_LINK, item.getModel()) {
	
					private static final long serialVersionUID = 1L;
	
					@Override
					public void onClick(AjaxRequestTarget target) {
						if (!PortalUtils.exists(feeDetailsBean)){
							feeDetailsBean = entry;
						}
						percentageFee = getDiv100(entry.getPercentageFee());
						ucFeeBean.getManageFeeDetailsList().remove(feeDetailsBean.getId());
						feeDetailContainer.setVisible(true);
						target.addComponent(feeDetailContainer);
					}
				};
				item.add(detailsLink);
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return ucFeeBean.getManageFeeDetailsList().size() != 0;
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
	
	private Button addSlabFeeButton(final WebMarkupContainer slabFeeContainer) {
		AjaxButton slabButton = new AjaxButton("slabFeeButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					
					// feeDetailsBean to FeeEntryWrkType
					feeDetailsBean.setPercentageFee(new BigDecimal(percentageFee).movePointRight(2).setScale(2, RoundingMode.DOWN).longValue());
					ucFeeBean.getManageFeeDetailsList().add(feeDetailsBean);
					
					feeDetailsBean = null;
					percentageFee = null;
					target.addComponent(feedBackPanel);
					target.addComponent(feeHeader);
					feeDetailContainer.setVisible(false);
					target.addComponent(feeDetailContainer);
					target.addComponent(slabFeeContainer);
					target.addComponent(navigator);
					
				} catch (Exception e) {
					log.debug("Exception occured while adding the fee ===> ", e);
					error(getLocalizer().getString("error.add",ManageCustomUseCaseFeeEditPanel.this));
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
	
	private static String getDiv100(long amount) {
		return new BigDecimal(amount).movePointLeft(2).setScale(2, RoundingMode.DOWN).toString();
	}
	
	//FeeEntryWrkType
	public static List<FeeEntryWrkType> convertToFeeEntryTypeWrk(
			final List<ManageBillPaymentFeeDetailsBean> txnList) {
		
		final List<FeeEntryWrkType> ucFeeList = new ArrayList<FeeEntryWrkType>();
		
		for (final ManageBillPaymentFeeDetailsBean uc : txnList) {
			FeeEntryWrkType feeWrk = new FeeEntryWrkType();
			feeWrk.setFixedFee(uc.getFixedFee()*100);
			feeWrk.setPercentageFee(uc.getPercentageFee());
			feeWrk.setThresholdAmount(uc.getThresholdAmount()*100);
			feeWrk.setMinimumFee(uc.getMinimumFee());
			if(uc.getMaximumFee()!=null){
				feeWrk.setMaximumFee(uc.getMaximumFee());
			}	
			ucFeeList.add(feeWrk);
		
		}	
		return ucFeeList;
	}
	
}
