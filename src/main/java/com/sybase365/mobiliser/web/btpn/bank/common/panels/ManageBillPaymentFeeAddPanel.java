package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
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
import com.btpnwow.portal.common.util.BillerProductLookup.BillerProduct;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageBillPaymentFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeeConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageBillPaymentFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.behaviours.DateHeaderContributor;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the Manage Fee Add Details Panel for fixed, slab, sharing fee.
 * 
 * @author Vikram Gunda
 * @modified Feny Yanti
 */
public class ManageBillPaymentFeeAddPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ManageBillPaymentFeeAddPanel.class);
	private ManageBillPaymentFeeBean feeBean;
	private FeeEntryWrkType feeDetailsBean;
	
	private static final String WICKET_ID_PAGEABLE = "pageable";
	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";
	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private int feeStartIndex = 0;
	private int feeEndIndex = 0;
	private Label feeHeader;
	private String feesTotalItemString;
	
	private BtpnCustomPagingNavigator navigator;
	
	private final BtpnMobiliserBasePage basePage;
	private static final int N = 6;
	
	private String[] labels;
	private CodeValue[] opts;
	
	private FeedbackPanel feedback;
	
	private WebMarkupContainer[] containers;
	private DropDownChoice<CodeValue>[] choices;
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

	
	public ManageBillPaymentFeeAddPanel(String id, BtpnBaseBankPortalSelfCarePage basePage, ManageBillPaymentFeeBean feeBean) {
		super(id);
		this.basePage = basePage;
		this.feeBean = feeBean == null ? new ManageBillPaymentFeeBean() : feeBean;
		this.labels = new String[N];
		this.opts = new CodeValue[N];
		addDateHeaderContributor();
		this.containers = new WebMarkupContainer[N];
		this.choices = new DropDownChoice[N];
		constructPanel();
	}
	

	private void constructPanel() {
		final Form<ManageBillPaymentFeeAddPanel> form = new Form<ManageBillPaymentFeeAddPanel>(
				"feeAddForm", new CompoundPropertyModel<ManageBillPaymentFeeAddPanel>(this));
		
		// Error Messages
		feedback = new FeedbackPanel("errorMessages");
		feedback.setOutputMarkupId(true);
		feedback.setOutputMarkupPlaceholderTag(true);
		form.add(feedback);
		
		// Add the Choice Renderer
		final IChoiceRenderer<CodeValue> choiceRenderer = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		
		final IChoiceRenderer<CodeValue> choiceRendererValue = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		
		
		//DESCRIPTION
		form.add(descComp = new TextField<String>("feeBean.description").setRequired(true).add(new ErrorIndicator()));
				descComp.setOutputMarkupId(true);
			
		// product
			for (int i = 0; i < N; ++i) {
				constructOneLevel(form, i, choiceRendererValue);
			}	
			
		// use case
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>(
				"feeBean.useCase", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_USE_CASES,
				this, Boolean.FALSE, true)
				.setChoiceRenderer(choiceRenderer)
				.setRequired(true)
				.add(new ErrorIndicator())
				.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					private static final long serialVersionUID = 1L;
					
					protected void onUpdate(AjaxRequestTarget target) {
						ManageBillPaymentFeeAddPanel.this.useCaseOnChange(target);
					}
				}));
		
		
		// Add check box apply to payee
		form.add(new CheckBox("feeBean.applyToPayee").setEnabled(true));
		
		//DEBIT ORG UNIT
		final IChoiceRenderer<CodeValue> debitOrgUnit = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(debitOrgUnitComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.orgUnit",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_ORG_UNIT, this, true, false)
				.setChoiceRenderer(debitOrgUnit).setRequired(true).add(new ErrorIndicator()));
		debitOrgUnitComp.setOutputMarkupId(true);
	
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
			
		//CUSTOMER TYPE
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderCT = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(customerTypeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.customerType",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CUSTOMER_TYPE, this, true, false)
				.setChoiceRenderer(codeValueChoiceRenderCT).add(new ErrorIndicator()));
			customerTypeComp.setOutputMarkupId(true);
		
		//VALID FROM
		DateTextField fromDate = (DateTextField) DateTextField
				.forDatePattern("feeBean.validFrom",
						BtpnConstants.ID_EXPIRY_DATE_PATTERN)
				.setRequired(true).add(new ErrorIndicator());
		validFrom = fromDate;
		form.add(validFrom);
		
		//CURRENCY CODE
		final IChoiceRenderer<CodeValue> codeValueChoiceCurCode = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		form.add(currencyCodeComp = new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.currency",
				CodeValue.class, BtpnConstants.RESOURCE_USE_CASE_FEE_CURRENCY, this, true, false)
				.setChoiceRenderer(codeValueChoiceCurCode).setRequired(true)
				.add(new ErrorIndicator()));
			currencyCodeComp.setOutputMarkupId(true);
		
		/* NOTE */
		form.add(noteComp = new TextField<String>("feeBean.note").add(new ErrorIndicator()));
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
		
		form.add(new Button("btnAdd") {	
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {		
				
				String finalId = null;
				
				for (int i = 0; i < N; ++i) {
					if (opts[i] != null) {
						List<CodeValue> children = billerProductLookup.getChildrenAsCodeValue(opts[i].getId());
	
						if ((children == null) || children.isEmpty()) {
							finalId = opts[i].getId();
						}
					}
				}
				
				if (finalId == null) {
					error(ManageBillPaymentFeeAddPanel.this.getLocalizer().getString("product.required", ManageBillPaymentFeeAddPanel.this));
				} 
				else {

					BillerProduct billerProduct = billerProductLookup.get(finalId);
					feeBean.setBillerId(billerProduct.getBillerId());
					feeBean.setProductId(billerProduct.getProductId());
					feeBean.setProductLabel(billerProduct.getDescription());
	
					setResponsePage(new ManageBillPaymentFeeConfirmPage(feeBean, BtpnConstants.ADD));
				}
				
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

					feeDetailsBean.setFixedFee(feeDetailsBean.getFixedFee()*100);
					feeDetailsBean.setPercentageFee(new BigDecimal(percentageFee).movePointRight(2).setScale(0, RoundingMode.DOWN).longValue());
					feeDetailsBean.setThresholdAmount( feeDetailsBean.getThresholdAmount()*100 );
					
					feeBean.getManageDetailsWrkList().add((FeeEntryWrkType) BeanUtils.cloneBean(feeDetailsBean));
					
					feeDetailsBean = null;
					percentageFee = null;
					target.addComponent(feedback);
					target.addComponent(feeHeader);
					target.addComponent(slabFeeContainer);
					target.addComponent(navigator);
					
				
				} catch (Exception e) {
					log.debug("Exception occured while adding the fee ===> ", e);
					error(getLocalizer().getString("error.add",ManageBillPaymentFeeAddPanel.this));
				}
			};
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedback);
				target.addComponent(feeHeader);
				target.addComponent(slabFeeContainer);
				target.addComponent(navigator);
			};
		};
		slabButton.setVisible(true);
		return slabButton;
	}
	
	
	private void useCaseOnChange(AjaxRequestTarget target) {
		String rootId;
		
		if (feeBean.getUseCase() == null) {
			rootId = null;
		} else {
			String useCaseId = feeBean.getUseCase().getId();
			String lang = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getLanguage();
			log.info("###USECASE ID:"+useCaseId);
			if ("223".equals(useCaseId)) { // Airtime Topup
				rootId = "in".equalsIgnoreCase(lang) ? "in.201" : "en.251";
			} else if ("224".equals(useCaseId)) { // Bill Payment
				rootId = "in".equalsIgnoreCase(lang) ? "in.100" : "en.150";
			} else {
				rootId = null;
			}
		}
		
		BillerProduct root = null;
		List<CodeValue> children = null;
		
		if (rootId != null) {
			root = billerProductLookup.get(rootId);
			if (root != null) {
				children = billerProductLookup.getChildrenAsCodeValue(rootId);
			}
		}
		
		if (root == null) {
			labels[0] = "";
			opts[0] = null;
		} else {
			labels[0] = root.getLabel();
			opts[0] = null;
		}
		
		if ((children == null) || children.isEmpty()) {
			choices[0].setChoices(Collections.<CodeValue> emptyList());
			containers[0].setVisible(false);
		} else {
			choices[0].setChoices(children);
			containers[0].setVisible(true);	
		}
		
		target.addComponent(choices[0]);
		target.addComponent(containers[0]);
		
		for (int i = 1; i < N; ++i) {
			labels[i] = "";
			opts[i] = null;
			
			choices[i].setChoices(Collections.<CodeValue> emptyList());
			containers[i].setVisible(false);
			
			target.addComponent(choices[i]);
			target.addComponent(containers[i]);
		}
		
	}
	
	private void constructOneLevel(Form<?> frm, final int i, IChoiceRenderer<CodeValue> renderer) {
		String si = Integer.toString(i);
		
		WebMarkupContainer container = new WebMarkupContainer("containers.".concat(si));
		container.setOutputMarkupPlaceholderTag(true);
		container.setVisible(false);
		
		Label label = new Label("labels.".concat(si));
		label.setOutputMarkupPlaceholderTag(true);
		
		DropDownChoice<CodeValue> choice = new DropDownChoice<CodeValue>("opts.".concat(si));
		choice.setChoices(Collections.<CodeValue> emptyList());
			
		choice.setChoiceRenderer(renderer);
		choice.setOutputMarkupPlaceholderTag(true);
		
		choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			
			private static final long serialVersionUID = 1L;

			protected void onUpdate(AjaxRequestTarget target) {
				if (i + 1 >= N) {
					return;
				}
				
				String rootId;
				
				BillerProduct root;
				List<CodeValue> children;
				
				if (opts[i] == null) {
					rootId = null;
					
					root = null;
					children = null;
				} else {
					rootId = opts[i].getId();
					
					root = billerProductLookup.get(rootId);
					children = billerProductLookup.getChildrenAsCodeValue(rootId);
				}
				
				if ((root == null) || (children == null) || children.isEmpty()) {
					opts[i + 1] = null;
					labels[i + 1] = "";
					
					choices[i + 1].setChoices(Collections.<CodeValue> emptyList());
					containers[i + 1].setVisible(false);
				} else {
					opts[i + 1] = null;
					labels[i + 1] = root.getPromptLabel();
					
					choices[i + 1].setChoices(children);
					containers[i + 1].setVisible(true);
				}
				
				target.addComponent(choices[i + 1]);
				target.addComponent(containers[i + 1]);
			
				for (int j = i + 2; j < N; ++j) {
					opts[j] = null;
					labels[j] = "";
					
					choices[j].setChoices(Collections.<CodeValue> emptyList());
					containers[j].setVisible(false);
					
					target.addComponent(choices[j]);
					target.addComponent(containers[j]);
				}
			}
		});
		
		labels[i] = ""; 
		opts[i] = null;
		
		container.add(label);
		container.add(choice);
		
		containers[i] = container;
		choices[i] = choice;
		
		frm.add(container);
	}
	
	
	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer dataViewContainer for the fee.
	 */
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final DataView<FeeEntryWrkType> dataView = new ManageBillPaymentDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<FeeEntryWrkType>(feeBean.getManageDetailsWrkList()));
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsWrkList().size() != 0 ;
			}
			
		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsWrkList().size() == 0;

			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageBillPaymentFeeAddPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageBillPaymentFeeAddPanel.this);

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
				return feeBean.getManageDetailsWrkList().size() != 0;
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
			return feeBean.getManageDetailsWrkList().size() != 0;
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
	
}
