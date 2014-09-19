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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.fee.facade.contract.wrk.FeeEntryWrkType;
import com.btpnwow.portal.common.util.BillerProductLookup;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeeConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee Add Details Panel for fixed, slab, sharing fee.
 * 
 * @author Vikram Gunda
 * @modified Feny Yanti
 */
public class ManageBillPaymentFeeEditPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageBillPaymentFeeEditPanel.class);
	private ManageBillPaymentFeeBean feeBean;
	
	private ManageBillPaymentFeeDetailsBean feeDetailsBean;
	
	private static final String WICKET_ID_LINK = "detailsLink";
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private String feesTotalItemString;
	
	private BtpnCustomPagingNavigator navigator;
	
	private final BtpnMobiliserBasePage basePage;
	
	private FeedbackPanel feedback;
	private WebMarkupContainer feeDetailContainer;
	
	private String percentageFee;
	private Component debitOrgUnitComp;
	private Component debitPiTypeComp;
	private Component glCodeComp;
	private Component customerTypeComp;
	private Component validFrom;
	private Component fixedFeeComp;
	private Component percentageFeeComp;
	private Component theresholdAmountComp;
	private Component maxFeeComp;
	private Component minFeeComp;
	private Component currencyCodeComp;
	private Component noteComp;
	private Component descComp;

	@SpringBean(name = "billerProductLookup")
	private BillerProductLookup billerProductLookup;

	
	public ManageBillPaymentFeeEditPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageBillPaymentFeeBean feeBean) {
		super(id);
		this.basePage = basePage;
		this.feeBean = feeBean == null ? new ManageBillPaymentFeeBean() : feeBean;
		addDateHeaderContributor();
		constructPanel();
		
	}
	

	private void constructPanel() {
		final Form<ManageBillPaymentFeeEditPanel> form = new Form<ManageBillPaymentFeeEditPanel>(
				"feeAddForm", new CompoundPropertyModel<ManageBillPaymentFeeEditPanel>(this));
		
		// Error Messages
		feedback = new FeedbackPanel("errorMessages");
		feedback.setOutputMarkupId(true);
		form.add(feedback);
		
		// Add the Choice Renderer
		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		
		//DESCRIPTION
		form.add(descComp = new TextField<String>("feeBean.description").setRequired(true).add(new ErrorIndicator()));
				descComp.setOutputMarkupId(true);
				
		// use case
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"feeBean.useCase", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_USE_CASES,
				this, Boolean.FALSE, true)
				.setChoiceRenderer(choiceRenderer)
				.setEnabled(false)
				.add(new ErrorIndicator()) );
		
		//product
		final WebMarkupContainer product = new WebMarkupContainer("productContainer");	
		product.add(new Label("feeBean.productLabel", feeBean.getProductLabel() == null ? "-" : feeBean.getProductLabel()));
		product.setVisible( "223".equals(feeBean.getUseCase().getId()) || "224".equals(feeBean.getUseCase().getId()) );
		form.add(product);
		
		// Add check box apply to payee
		form.add(new CheckBox("feeBean.applyToPayee").setEnabled(false));
		
		//DEBIT ORG UNIT
		final IChoiceRenderer<CodeValue> debitOrgUnit = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(debitOrgUnitComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.orgUnit",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, this, true, false)
				.setChoiceRenderer(debitOrgUnit).setRequired(true).add(new ErrorIndicator()));
		debitOrgUnitComp.setOutputMarkupId(true);
		debitOrgUnitComp.setEnabled(false);
	
		//GL CODE
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(glCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.glCode",
				CodeValue.class, BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
			glCodeComp.setOutputMarkupId(true);
		
		//DEBIT PI TYPE
		final IChoiceRenderer<CodeValue> debitPiType = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(debitPiTypeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.piType",
				CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PI_TYPE, this, true, false)
				.setChoiceRenderer(debitPiType).add(new ErrorIndicator()));
			debitPiTypeComp.setOutputMarkupId(true);
			debitPiTypeComp.setEnabled(false);
			
		//CUSTOMER TYPE
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderCT = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(customerTypeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.customerType",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderCT).add(new ErrorIndicator()));
			customerTypeComp.setOutputMarkupId(true);
			customerTypeComp.setEnabled(false);
			
		//VALID FROM
		DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("feeBean.validFrom",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.add(new ErrorIndicator());
		validFrom = fromDate;
		validFrom.setEnabled(false);
		form.add(validFrom);
		
		//CURRENCY CODE
		final IChoiceRenderer<CodeValue> codeValueChoiceCurCode = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		form.add(currencyCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.currency",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CURRENCY, this, true, false)
				.setChoiceRenderer(codeValueChoiceCurCode)
				.add(new ErrorIndicator()));
			currencyCodeComp.setOutputMarkupId(true);
			currencyCodeComp.setEnabled(false);
			
		/* NOTE */
		form.add(noteComp = new TextField<String>("feeBean.note").add(new ErrorIndicator()));
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
				
		
		
		form.add(new Button("btnEdit") {	
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {		
				feeBean.getManageDetailsWrkList().addAll(convertToFeeEntryTypeWrk(feeBean.getManageFeeDetailsList()));
				
				setResponsePage(new ManageBillPaymentFeeConfirmPage(feeBean, BtpnConstants.UPDATE));
			}
		}.setDefaultFormProcessing(true));
		
		form.add(new AjaxButton("btnCancel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(ManageBillPaymentFeePage.class);
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
	
	
	private Button addSlabFeeButton(final WebMarkupContainer slabFeeContainer) {
		AjaxButton slabButton = new AjaxButton("slabFeeButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					feeDetailsBean.setPercentageFee(new BigDecimal(percentageFee).movePointRight(2).setScale(2, RoundingMode.DOWN).longValue());
					feeBean.getManageFeeDetailsList().add(feeDetailsBean);
					
					feeDetailsBean = null;
					percentageFee  = null;
					target.addComponent(feedback);
					target.addComponent(feeHeader);
					feeDetailContainer.setVisible(false);
					target.addComponent(feeDetailContainer);
					target.addComponent(slabFeeContainer);
					target.addComponent(navigator);
					
				
				} catch (Exception e) {
					log.debug("Exception occured while adding the fee ===> ", e);
					error(getLocalizer().getString("error.add",ManageBillPaymentFeeEditPanel.this));
				}
			};
		};
		slabButton.setVisible(true);
		return slabButton;
	}
	
	
	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer dataViewContainer for the fee.
	 */
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final DataView<ManageBillPaymentFeeDetailsBean> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<ManageBillPaymentFeeDetailsBean>(feeBean.getManageFeeDetailsList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageFeeDetailsList().size() != 0 ;
			}
			
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageFeeDetailsList().size() == 0;

			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageBillPaymentFeeEditPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageBillPaymentFeeEditPanel.this);

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
				return feeBean.getManageFeeDetailsList().size() != 0;
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
			item.add(new Label("percentageFee", getDiv100(entry.getPercentageFee()) ) );
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
					feeBean.getManageFeeDetailsList().remove(feeDetailsBean.getId());
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
			return feeBean.getManageDetailsList().size() != 0;
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
