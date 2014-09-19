package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.math.BigDecimal;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.beans.fee.FeeConfig;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.UpdateFeeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.UpdateFeeResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageFeeAddConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageFeePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.btpn.util.ProductValueValidator;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Manage Fee Add Details Panel for fixed, slab, sharing fee.
 * 
 * @author Vikram Gunda
 */
public class ManageFeeAddDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ManageFeeAddDetailsPanel.class);

	private BtpnMobiliserBasePage mobBasePage;

	private ManageFeeBean feeBean;

	private ManageFeeDetailsBean feeDetailsBean;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private static final String WICKET_ID_FEESNAVIGATOR = "feeNavigator";

	private static final String WICKET_ID_FEESTOTALITEMS = "feeHeader";

	private String feesTotalItemString;

	private int feeStartIndex = 0;

	private int feeEndIndex = 0;

	private Label feeHeader;

	private BtpnCustomPagingNavigator navigator;

	private Button createButton;

	private FeedbackPanel feedbackPanel;

	private final boolean isAdd;

	private Component maxValueComp;
	/**
	 * Constructor for this page.
	 * 
	 * @param id id of the panel.
	 * @param mobBasePage base Page of the mobiliser.
	 * @param feeBean fee bean for the fees.
	 * @param feeDetailsBean fee details bean for the fee.
	 */
	public ManageFeeAddDetailsPanel(final String id, final BtpnMobiliserBasePage mobBasePage,
		final ManageFeeBean feeBean, final ManageFeeDetailsBean feeDetailsBean, final boolean isAdd) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.feeBean = feeBean;
		this.feeDetailsBean = feeDetailsBean;
		this.isAdd = isAdd;
		constructPanel();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPanel() {
		final Form<ManageFeeDetailsPanel> form = new Form<ManageFeeDetailsPanel>("feeForm",
			new CompoundPropertyModel<ManageFeeDetailsPanel>(this));

		// Error Messages
		feedbackPanel = new FeedbackPanel("errorMessages");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);

		// Create the choice renderer for dropdowns
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		// Add fee management fields
		form.add(new Label("feeBean.useCaseName.value"));
		form.add(new Label("feeBean.productName.value"));
		form.add(new Label("feeBean.feeType"));
		form.add(new Label("feeBean.applyToPayee",
			feeBean.getApplyToPayee() ? BtpnConstants.YES_VALUE : BtpnConstants.NO_VALUE));

		// Add Transaction amount container for only sharing fee
		final WebMarkupContainer transfeeContainer = addTransactionAmountContainer();
		transfeeContainer.setOutputMarkupId(true);
		form.add(transfeeContainer);

		// Add fixed fee conatiner
		final WebMarkupContainer fixedFeeContainer = addFixedFeeContainer(codeValueChoiceRender);
		fixedFeeContainer.setOutputMarkupId(true);
		fixedFeeContainer.setVisible(feeBean.getFeeType().equals(BtpnConstants.USECASE_FIXED_RADIO));
		form.add(fixedFeeContainer);

		// Add slab fee or Sharing fee container
		final WebMarkupContainer slabFeeContainer = addSlabFeeContainer(codeValueChoiceRender);
		slabFeeContainer.setVisible(feeBean.getFeeType().equals(BtpnConstants.USECASE_SLAB_RADIO));
		slabFeeContainer.setOutputMarkupId(true);
		form.add(slabFeeContainer);

		// Add Sharing fee container
		final WebMarkupContainer sharingFeeContainer = addSharingFeeContainer(codeValueChoiceRender, transfeeContainer);
		sharingFeeContainer.setVisible(feeBean.getFeeType().equals(BtpnConstants.USECASE_SHARING_RADIO));
		sharingFeeContainer.setOutputMarkupId(true);
		form.add(sharingFeeContainer);

		form.add(addCreateButton());
		form.add(addUpdateButton());
		form.add(addCancelButton());

		// Add add Button
		add(form);
	}

	/**
	 * This method adds the Transaction Amount container.
	 */
	private WebMarkupContainer addTransactionAmountContainer() {
		final WebMarkupContainer transactionAmountContainer = new WebMarkupContainer("transactionAmountContainer");
		transactionAmountContainer.add(new AmountTextField<Long>("feeBean.transactionAmount", Long.class).setRequired(
			true).add(new ErrorIndicator()));
		transactionAmountContainer.setVisible(feeBean.getFeeType().equals(BtpnConstants.USECASE_SHARING_RADIO));
		return transactionAmountContainer;
	}

	/**
	 * This method adds the fixed fee container.
	 * 
	 * @param codeValueChoiceRender codeValueChoiceRender for Dropdowns.
	 */
	private WebMarkupContainer addFixedFeeContainer(final IChoiceRenderer<CodeValue> codeValueChoiceRender) {
		final WebMarkupContainer fixedFeeContainer = new WebMarkupContainer("fixedFeeContainer");
		fixedFeeContainer.add(new RequiredTextField<String>("feeDetailsBean.feeName").add(new ErrorIndicator()));
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		fixedFeeContainer.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeDetailsBean.glCode",
			CodeValue.class, BtpnConstants.PRODUCT_GL_CODES, this, Boolean.TRUE, false).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		final RadioGroup<String> rg = addAmountRadioGroupContainer();
		fixedFeeContainer.add(rg);
		fixedFeeContainer.add(new RequiredTextField<String>("feeDetailsBean.amount").setRequired(true)
			.add(new ProductValueValidator(rg)).add(new ErrorIndicator()));
		fixedFeeContainer.add(new AmountTextField<Long>("feeBean.transactionAmount", Long.class).setRequired(true).add(
			new ErrorIndicator()));
		return fixedFeeContainer;

	}

	/**
	 * This method adds the Slab fee container.
	 * 
	 * @param codeValueChoiceRender codeValueChoiceRender for Dropdowns.
	 */
	private WebMarkupContainer addSlabFeeContainer(final IChoiceRenderer<CodeValue> codeValueChoiceRender) {
		final WebMarkupContainer slabFeeContainer = new WebMarkupContainer("slabeFeeContainer");
		slabFeeContainer.add(new Label("feeDetailsBean.feeName"));
		slabFeeContainer.add(new Label("feeDetailsBean.glCode.value"));
		slabFeeContainer.add(new AmountTextField<Long>("feeDetailsBean.minValue").setRequired(true).add(
			new ErrorIndicator()));
		slabFeeContainer.add(maxValueComp = new AmountTextField<Long>("feeDetailsBean.maxValue").setRequired(true).add(
			new ErrorIndicator()));
		maxValueComp.setOutputMarkupId(true);
		final RadioGroup<String> rg = addAmountRadioGroupContainer();
		slabFeeContainer.add(rg);
		slabFeeContainer.add(new RequiredTextField<String>("feeDetailsBean.amount").add(new ProductValueValidator(rg))
			.add(new ErrorIndicator()));
		notificationManageFeesDataView(slabFeeContainer, true);
		slabFeeContainer.add(addSlabFeeButton(slabFeeContainer));
		return slabFeeContainer;
	}

	/**
	 * This method adds the Sharing fee container.
	 * 
	 * @param codeValueChoiceRender codeValueChoiceRender for Dropdowns.
	 */
	private WebMarkupContainer addSharingFeeContainer(final IChoiceRenderer<CodeValue> codeValueChoiceRender,
		final WebMarkupContainer transfeeContainer) {
		final WebMarkupContainer sharingFeeContainer = new WebMarkupContainer("sharingFeeContainer");
		sharingFeeContainer.add(new RequiredTextField<String>("feeDetailsBean.feeName").add(new ErrorIndicator()));
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
				BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		sharingFeeContainer.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeDetailsBean.glCode",
			CodeValue.class, BtpnConstants.PRODUCT_GL_CODES, this, Boolean.TRUE, false).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		final RadioGroup<String> rg = addAmountRadioGroupContainer();
		sharingFeeContainer.add(rg);
		sharingFeeContainer.add(new RequiredTextField<String>("feeDetailsBean.amount").add(
			new ProductValueValidator(rg)).add(new ErrorIndicator()));
		notificationManageFeesDataView(sharingFeeContainer, false);
		sharingFeeContainer.add(addSharingFeeButton(sharingFeeContainer, transfeeContainer));
		return sharingFeeContainer;
	}

	/**
	 * This method adds the Slab Fee Button
	 * 
	 * @param slabFeeContainer slabFeeContainer that needs to refreshed
	 */
	private Button addSlabFeeButton(final WebMarkupContainer slabFeeContainer) {
		AjaxButton slabButton = new AjaxButton("slabFeeButton") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					if (validateMinAndMaxValueRanges()) {
						feeBean.getManageDetailsList().add((ManageFeeDetailsBean) BeanUtils.cloneBean(feeDetailsBean));
					}
					target.addComponent(feedbackPanel);
					target.addComponent(feeHeader);
					target.addComponent(slabFeeContainer);
					target.addComponent(navigator);
					target.addComponent(maxValueComp);
				} catch (Exception e) {
					LOG.debug("Exception occured while adding the fee ===> ", e);
					ManageFeeAddDetailsPanel.this.error(getLocalizer().getString("error.add",
						ManageFeeAddDetailsPanel.this));
				}
			};

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedbackPanel);
				target.addComponent(feeHeader);
				target.addComponent(slabFeeContainer);
				target.addComponent(navigator);
			};
		};
		slabButton.setVisible(isAdd);
		return slabButton;
	}

	/**
	 * This method adds the Sharing Fee Button
	 * 
	 * @param sharingFeeContainer sharingFeeContainer that needs to refreshed
	 */
	private Button addSharingFeeButton(final WebMarkupContainer sharingFeeContainer,
		final WebMarkupContainer transfeeContainer) {
		AjaxButton shareButton = new AjaxButton("btnSharingFeeAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					feeBean.getManageDetailsList().add((ManageFeeDetailsBean) BeanUtils.cloneBean(feeDetailsBean));
					target.addComponent(feeHeader);
					target.addComponent(feedbackPanel);
					target.addComponent(transfeeContainer);
					target.addComponent(sharingFeeContainer);
					target.addComponent(navigator);
					if (feeBean.getManageDetailsList().size() >= 2) {
						createButton.setVisible(true);
						target.addComponent(createButton);
					}
				} catch (Exception e) {
					LOG.debug("Exception occured while adding the fee ===> ", e);
					ManageFeeAddDetailsPanel.this.error(getLocalizer().getString("error.add",
						ManageFeeAddDetailsPanel.this));
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedbackPanel);
				target.addComponent(feeHeader);
				target.addComponent(transfeeContainer);
				target.addComponent(sharingFeeContainer);
				target.addComponent(navigator);
			};
		};
		shareButton.setVisible(isAdd);
		return shareButton;
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addUpdateButton() {
		Button updateButton = new Button("btnUpdate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleUpdateFeeBean();
			};
		};
		updateButton.setVisible(!isAdd);
		return updateButton;
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addCreateButton() {
		createButton = new Button("btnCreate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				final String feeType = feeBean.getFeeType();
				int size = feeBean.getManageDetailsList().size();
				if (feeType.equals(BtpnConstants.USECASE_SLAB_RADIO) && size == 0) {
					error(getLocalizer().getString("slabfee.required", this));
					return;
				}
				if (feeType.equals(BtpnConstants.USECASE_FIXED_RADIO)) {
					final String amountType = feeDetailsBean.getAmountType();
					// Radio intererst fixed and percentage
					if (PortalUtils.exists(amountType) && amountType.equals(BtpnConstants.FIXED_INTEREST_RADIO)) {
						feeDetailsBean.setFixedFee(Long.valueOf(feeDetailsBean.getAmount()) * 100);
						feeDetailsBean.setPercentageFee(new BigDecimal(0.0));
					} else {
						feeDetailsBean.setPercentageFee(new BigDecimal(feeDetailsBean.getAmount()));
						feeDetailsBean.setFixedFee(0L);
					}
					feeBean.getManageDetailsList().add(feeDetailsBean);
				}
				setResponsePage(new ManageFeeAddConfirmPage(feeBean));
			};
		};
		final String feeType = feeBean.getFeeType();
		createButton.setDefaultFormProcessing(feeType.equals(BtpnConstants.USECASE_FIXED_RADIO));
		createButton.setVisible(!feeType.equals(BtpnConstants.USECASE_SHARING_RADIO) && isAdd);
		createButton.setOutputMarkupId(true);
		createButton.setOutputMarkupPlaceholderTag(true);
		return createButton;
	}

	private Button addCancelButton() {
		Button cancelButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageFeePage.class);
			};
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method adds the radio group container.
	 */
	private RadioGroup<String> addAmountRadioGroupContainer() {
		final RadioGroup<String> rg = new RadioGroup<String>("feeDetailsBean.amountType");
		rg.add(new Radio<String>("radio.fixed", Model.of(BtpnConstants.FIXED_INTEREST_RADIO)));
		rg.add(new Radio<String>("radio.percentage", Model.of(BtpnConstants.PERCENT_INTEREST_RADIO)));
		return rg;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer dataViewContainer for the fee.
	 * @param isSlabFee isSlabFee for the container.
	 */
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer, boolean isSlabFee) {

		final DataView<ManageFeeDetailsBean> dataView = new ManageFeeDetailsView(WICKET_ID_PAGEABLE,
			new ListDataProvider<ManageFeeDetailsBean>(feeBean.getManageDetailsList()), isSlabFee);
		dataView.setItemsPerPage(20);

		// Add the navigation
		navigator = new BtpnCustomPagingNavigator(WICKET_ID_FEESNAVIGATOR, dataView) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsList().size() != 0 && isAdd;

			}

		};
		navigator.setOutputMarkupId(true);
		navigator.setOutputMarkupPlaceholderTag(true);
		dataViewContainer.add(navigator);

		dataViewContainer.add(new Label("no.items", getLocalizer().getString("no.items", this)) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return feeBean.getManageDetailsList().size() == 0 && isAdd;

			}
		}.setRenderBodyOnly(true));

		final String displayTotalItemsText = ManageFeeAddDetailsPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageFeeAddDetailsPanel.this);

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
				return feeBean.getManageDetailsList().size() != 0 && isAdd;

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
	private class ManageFeeDetailsView extends DataView<ManageFeeDetailsBean> {

		private static final long serialVersionUID = 1L;

		private boolean isSlabFee;

		protected ManageFeeDetailsView(String id, IDataProvider<ManageFeeDetailsBean> dataProvider, boolean isSlabFee) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
			this.isSlabFee = isSlabFee;
		}

		@Override
		protected void onBeforeRender() {
			refreshTotalItemCount();
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<ManageFeeDetailsBean> item) {
			final ManageFeeDetailsBean entry = item.getModelObject();
			if (isSlabFee) {
				addSlabeFeeItems(item, entry);
			} else {
				addSharingFeeItems(item, entry);
			}
			final String cssStyle = item.getIndex() % 2 == 0 ? BtpnConstants.DATA_VIEW_EVEN_ROW_CSS : BtpnConstants.DATA_VIEW_ODD_ROW_CSS;
			item.add(new SimpleAttributeModifier("class", cssStyle));
		}

		@Override
		public boolean isVisible() {
			return feeBean.getManageDetailsList().size() != 0 && isAdd;

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

		private void addSlabeFeeItems(final Item<ManageFeeDetailsBean> item, final ManageFeeDetailsBean entry) {
			item.setModel(new CompoundPropertyModel<ManageFeeDetailsBean>(entry));
			// Add the File name
			item.add(new AmountLabel("minValue"));
			item.add(new AmountLabel("maxValue"));
			final String amountType = entry.getAmountType();
			// Radio intererst fixed and percentage
			if (PortalUtils.exists(amountType) && amountType.equals(BtpnConstants.FIXED_INTEREST_RADIO)) {
				entry.setFixedFee(Long.valueOf(entry.getAmount()) * 100);
				entry.setPercentageFee(new BigDecimal("0.0"));
			} else {
				entry.setPercentageFee(new BigDecimal(entry.getAmount()));
				entry.setFixedFee(0L);
			}
			item.add(new AmountLabel("fixedFee"));
			item.add(new Label("percentageFee",
				(entry.getPercentageFee() != null ? entry.getPercentageFee().toString() : "0.0") + " %"));
		}

		private void addSharingFeeItems(final Item<ManageFeeDetailsBean> item, final ManageFeeDetailsBean entry) {
			item.setModel(new CompoundPropertyModel<ManageFeeDetailsBean>(entry));
			// Add the File name
			item.add(new Label("feeName", entry.getFeeName()));
			item.add(new Label("glCode", entry.getGlCode().getValue()));
			final String amountType = entry.getAmountType();
			// Radio intererst fixed and percentage
			if (PortalUtils.exists(amountType) && amountType.equals(BtpnConstants.FIXED_INTEREST_RADIO)) {
				entry.setFixedFee(Long.valueOf(entry.getAmount()) * 100);
				entry.setPercentageFee(new BigDecimal("0.0"));
			} else {
				entry.setPercentageFee(new BigDecimal(entry.getAmount()));
				entry.setFixedFee(0L);
			}
			item.add(new AmountLabel("fixedFee"));
			item.add(new Label("percentageFee",
				(entry.getPercentageFee() != null ? entry.getPercentageFee().toString() : "0.0") + " %"));
		}
	}

	/**
	 * This method handles the updation of Fee Bean.
	 */
	private void handleUpdateFeeBean() {
		try {
			if (feeBean.getFeeType().equals(BtpnConstants.USECASE_SLAB_RADIO) && !validateMinAndMaxValueRanges()) {
				return;
			}
			// Update Fixed Fee and Percentage Fee.
			for (ManageFeeDetailsBean feeDetails : feeBean.getManageDetailsList()) {
				boolean isUpdate = feeDetails.getFeeScalePeriodId().equals(feeDetailsBean.getFeeScalePeriodId());
				if (isUpdate) {
					updateFixedAndPercentageFee(feeDetails);
				}
			}
			final UpdateFeeRequest request = this.mobBasePage.getNewMobiliserRequest(UpdateFeeRequest.class);
			final FeeConfig feeConfig = new FeeConfig();
			feeConfig.setUseCaseFee(ConverterUtils.convertToUseCaseFee(feeBean));
			feeConfig.getFees().addAll(ConverterUtils.convertToAddFeeConfig(feeBean));
			request.setFee(feeConfig);
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final UpdateFeeResponse response = this.mobBasePage.feeClient.updateFee(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				getSession().info(getLocalizer().getString("update.success", ManageFeeAddDetailsPanel.this));
				setResponsePage(ManageFeePage.class);
			} else {
				error(getLocalizer().getString("error.update.fees", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
	}

	private void updateFixedAndPercentageFee(ManageFeeDetailsBean feeDetails) {
		final String amountType = feeDetailsBean.getAmountType();
		// Radio intererst fixed and percentage
		if (PortalUtils.exists(amountType) && amountType.equals(BtpnConstants.FIXED_INTEREST_RADIO)) {
			feeDetails.setFixedFee(Long.valueOf(feeDetailsBean.getAmount()) * 100);
			feeDetails.setPercentageFee(new BigDecimal(0.0));
		} else {
			feeDetails.setPercentageFee(new BigDecimal(feeDetailsBean.getAmount()));
			feeDetails.setFixedFee(0L);
		}
	}

	/**
	 * Check for existing ranges.
	 */
	private boolean validateMinAndMaxValueRanges() {
		final Long minValueEntered = feeDetailsBean.getMinValue();
		final Long maxValueEntered = feeDetailsBean.getMaxValue();
		if(maxValueEntered <= minValueEntered){
			maxValueComp.error(getLocalizer().getString("error.maxvalue", this));
			return false;
		}
		for (ManageFeeDetailsBean feeDetails : feeBean.getManageDetailsList()) {
			final Long minvaluePresent = feeDetails.getMinValue();
			final Long maxvaluePresent = feeDetails.getMaxValue();
			if (PortalUtils.exists(minvaluePresent) && (minValueEntered >= minvaluePresent)
					&& (minValueEntered <= maxvaluePresent)) {
				error(getLocalizer().getString("error.invalid.range", this));
				return false;
			}
			if (PortalUtils.exists(maxvaluePresent) && (maxValueEntered >= minvaluePresent)
					&& (maxValueEntered <= maxvaluePresent)) {
				error(getLocalizer().getString("error.invalid.range", this));
				return false;
			}

		}
		return true;
	}

}
