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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.DeleteFeeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.DeleteFeeResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.UpdateFeeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.fee.UpdateFeeResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageAirtimeTopupFeeBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageFeeDetailsBean;
import com.sybase365.mobiliser.web.btpn.bank.common.dataproviders.ManageFeeDetailsDataProvider;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageAirtimeTopupAddFeeConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageAirtimeTopupFeePage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managefee.ManageFeePage;
import com.sybase365.mobiliser.web.btpn.common.components.AirtimeDenominationDropdownChoice;
import com.sybase365.mobiliser.web.btpn.common.components.AirtimeTelcoDropdownChoice;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.common.components.BtpnCustomPagingNavigator;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.btpn.util.ProductValueValidator;

/**
 * This is the Manage Fee Add Details Panel for fixed, slab, sharing fee.
 * 
 * @author Vikram Gunda
 */
public class ManageAirtimeTopupFeeAddPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ManageAirtimeTopupFeeAddPanel.class);

	private BtpnMobiliserBasePage mobBasePage;

	private ManageAirtimeTopupFeeBean feeBean;

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

	private Button cancelButton;

	private final boolean isAdd;

	private WebMarkupContainer airtimeTopupFeeContainer;

	private DataView<ManageFeeDetailsBean> dataView;

	private Component denomination;

	/**
	 * Constructor for this page.
	 * 
	 * @param id id of the panel.
	 * @param mobBasePage base Page of the mobiliser.
	 * @param feeBean fee bean for the fees.
	 * @param feeDetailsBean fee details bean for the fee.
	 */
	public ManageAirtimeTopupFeeAddPanel(final String id, final BtpnMobiliserBasePage mobBasePage, final boolean isAdd,
		final ManageAirtimeTopupFeeBean feeBean, final ManageFeeDetailsBean feeDetailsBean) {
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
		final Form<ManageAirtimeTopupFeeAddPanel> form = new Form<ManageAirtimeTopupFeeAddPanel>("feeForm",
			new CompoundPropertyModel<ManageAirtimeTopupFeeAddPanel>(this));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		final WebMarkupContainer operatorContainer = new WebMarkupContainer("airtimeOperatorContainer");
		final WebMarkupContainer denominationContainer = new WebMarkupContainer("denominationContainer");
		airtimeTopupFeeContainer = new WebMarkupContainer("airTimeFeeConatiner");

		// Add the biller category container
		form.add(operatorContainer);
		form.add(denominationContainer);
		form.add(airtimeTopupFeeContainer);

		addOperatorContainerContainer(codeValueChoiceRender, operatorContainer, denominationContainer);
		addDenominationContainerContainer(codeValueChoiceRender, denominationContainer, airtimeTopupFeeContainer);
		addBillPaymentFeeContainer(codeValueChoiceRender, airtimeTopupFeeContainer);
		operatorContainer.setVisible(isAdd);
		denominationContainer.setVisible(false);
		airtimeTopupFeeContainer.setVisible(!isAdd);

		form.add(addCreateButton());
		form.add(addCancelButton());
		form.add(addUpdateButton());
		form.add(addDeleteButton());
		// Add add Button
		add(form);
	}

	/**
	 * This method addBillerCategoryContainer for creating the fee.
	 */
	private WebMarkupContainer addOperatorContainerContainer(final IChoiceRenderer<CodeValue> codeValueChoiceRender,
		final WebMarkupContainer operatorContainer, final WebMarkupContainer airtimeTopupFeeContainer) {
		// Add biller category container.

		operatorContainer.add(new FeedbackPanel("errorMessages"));
		// Add biller code dropdown choice
		operatorContainer.add(new AirtimeTelcoDropdownChoice("feeBean.telco", false, true).setRequired(true).add(
			new ErrorIndicator()));
		operatorContainer.add(addOperatorAddButton(operatorContainer, airtimeTopupFeeContainer));
		operatorContainer.setOutputMarkupId(true);
		operatorContainer.setOutputMarkupPlaceholderTag(true);
		return operatorContainer;
	}

	/**
	 * This method addBillerCategoryContainer for creating the fee.
	 */
	private WebMarkupContainer addDenominationContainerContainer(
		final IChoiceRenderer<CodeValue> codeValueChoiceRender, final WebMarkupContainer denominationContainer,
		final WebMarkupContainer airtimeTopupFeeContainer) {
		final FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		denominationContainer.add(feedBackPanel);
		// Add biller code dropdown choice
		denominationContainer.add(denomination = new AirtimeDenominationDropdownChoice("feeBean.denomination", false,
			true, feeBean.getTelco() != null ? feeBean.getTelco().getId() : null).setRequired(true));
		// Add the Product Type Dropdown choice
		denominationContainer.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeBean.productName",
			CodeValue.class, BtpnUtils.fetchProductType("consumer"), this).setChoiceRenderer(codeValueChoiceRender)
			.add(new ErrorIndicator()));
		denominationContainer.add(addOperatorAddButton(denominationContainer, airtimeTopupFeeContainer));
		denominationContainer.setOutputMarkupId(true);
		denominationContainer.setOutputMarkupPlaceholderTag(true);
		return denominationContainer;
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addOperatorAddButton(final WebMarkupContainer airtimeContainer,
		final WebMarkupContainer airtimeDenomContainer) {

		AjaxButton addButton = new AjaxButton("btnAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				((AirtimeDenominationDropdownChoice) denomination).setTelco(feeBean.getTelco().getId());
				airtimeContainer.setVisible(false);
				airtimeDenomContainer.setVisible(true);
				target.addComponent(airtimeContainer);
				target.addComponent(airtimeDenomContainer);
				target.addComponent(createButton);
				target.addComponent(cancelButton);
			};

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(airtimeContainer);
				target.addComponent(airtimeDenomContainer);
			};
		};
		return addButton;
	}

	/**
	 * This method addBillerCategoryContainer for creating the fee.
	 */
	private void addBillPaymentFeeContainer(final IChoiceRenderer<CodeValue> codeValueChoiceRender,
		final WebMarkupContainer billPayFeeContainer) {
		billPayFeeContainer.add(new Label("feeBean.telco.id"), new Label("feeBean.denomination.value"), new Label(
			"feeBean.productName.value"));
		billPayFeeContainer.add(new Label("label.productId", getLocalizer().getString("label.productId", this))
			.setVisible(!isAdd));
		billPayFeeContainer.add(new Label("feeBean.productName.id").setVisible(!isAdd));
		final String billPayHeader = isAdd ? getLocalizer().getString("label.airtimeTopupFee", this) : getLocalizer()
			.getString("label.airtimeTopupFee.edit", this);
		billPayFeeContainer.add(new Label("airtimeTopupHeader", billPayHeader));
		FeedbackPanel feedBackPanel = new FeedbackPanel("errorMessages");
		billPayFeeContainer.add(feedBackPanel);
		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		billPayFeeContainer.add(new AmountTextField<String>("feeBean.transactionAmount").setRequired(true).add(
			new ErrorIndicator()));
		final WebMarkupContainer editFeeLabel = new WebMarkupContainer("fieldsetClass");
		editFeeLabel.add(new Label("label.edit.fee", getLocalizer().getString("label.edit.fee", this))
			.setVisible(!isAdd));
		editFeeLabel.setVisible(!isAdd);
		billPayFeeContainer.add(editFeeLabel);
		billPayFeeContainer.add(new Label("feeDetailsBean.feeName.label", feeDetailsBean.getFeeName()).setVisible(
			!isAdd));
		billPayFeeContainer.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("feeDetailsBean.glCode",
			CodeValue.class, BtpnConstants.RESOURCE_BILL_PAYMENT_FEE_GL_CODES, this, true, false)
			.setChoiceRenderer(codeValueChoiceRenderGL).setRequired(true).add(new ErrorIndicator()));
		billPayFeeContainer.add(new RequiredTextField<String>("feeDetailsBean.feeName").add(new ErrorIndicator()).setVisible(isAdd));
		final RadioGroup<String> rg = addAmountRadioGroupContainer();
		billPayFeeContainer.add(rg);
		billPayFeeContainer.add(new RequiredTextField<String>("feeDetailsBean.amount").add(
			new ProductValueValidator(rg)).add(new ErrorIndicator()));
		notificationManageFeesDataView(billPayFeeContainer);
		billPayFeeContainer.setOutputMarkupId(true);
		billPayFeeContainer.setOutputMarkupPlaceholderTag(true);
		billPayFeeContainer.add(addAddBillDetailsButton(billPayFeeContainer));
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
	 * This method adds the Create button for creating the fee.
	 */
	private Button addAddBillDetailsButton(final WebMarkupContainer billPayFeeDetailsContainer) {

		AjaxButton addButton = new AjaxButton("btnAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					final String amountType = feeDetailsBean.getAmountType();
					if (amountType.equals(BtpnConstants.FIXED_INTEREST_RADIO)) {
						feeDetailsBean.setFixedFee(Long.valueOf(feeDetailsBean.getAmount()) * 100);
						feeDetailsBean.setPercentageFee(new BigDecimal("0.0"));
					} else {
						feeDetailsBean.setFixedFee(0L);
						feeDetailsBean.setPercentageFee(new BigDecimal(feeDetailsBean.getAmount()));
					}
					feeBean.getManageDetailsList().add((ManageFeeDetailsBean) BeanUtils.cloneBean(feeDetailsBean));
					target.addComponent(billPayFeeDetailsContainer);
				} catch (Exception e) {
					LOG.error("An error occured while adding Bill Payment Fee");
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				try {
					target.addComponent(billPayFeeDetailsContainer);
				} catch (Exception e) {
					LOG.error("An error occured while adding Bill Payment Fee");
				}
			};
		};
		addButton.setVisible(isAdd);
		return addButton;
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addCreateButton() {
		createButton = new Button("btnCreate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				final int size = feeBean.getManageDetailsList().size();
				if (size == 0) {
					error(getLocalizer().getString("fee.required", ManageAirtimeTopupFeeAddPanel.this));
					return;
				}
				setResponsePage(new ManageAirtimeTopupAddFeeConfirmPage(feeBean));
			}

			@Override
			public boolean isVisible() {
				return airtimeTopupFeeContainer.isVisible() && isAdd;
			};
		};
		createButton.setDefaultFormProcessing(false);
		createButton.setOutputMarkupId(true);
		createButton.setOutputMarkupPlaceholderTag(true);
		return createButton;
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addUpdateButton() {
		Button updateButton = new Button("btnUpdate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleUpdateAirtimeFeeBean();
			}
		};
		updateButton.setOutputMarkupId(true);
		updateButton.setOutputMarkupPlaceholderTag(true);
		updateButton.setVisible(!isAdd);
		return updateButton;
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addDeleteButton() {
		Button deleteButton = new Button("btnDelete") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleDeleteAirtimeFees();
			}
		};
		deleteButton.setDefaultFormProcessing(false);
		deleteButton.setOutputMarkupId(true);
		deleteButton.setOutputMarkupPlaceholderTag(true);
		deleteButton.setVisible(!isAdd);
		return deleteButton;
	}

	/**
	 * This method adds the Create button for creating the fee.
	 */
	private Button addCancelButton() {
		cancelButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageAirtimeTopupFeePage.class);
			}

			@Override
			public boolean isVisible() {
				return airtimeTopupFeeContainer.isVisible();
			};
		};
		cancelButton.setDefaultFormProcessing(false);
		cancelButton.setOutputMarkupId(true);
		cancelButton.setOutputMarkupPlaceholderTag(true);
		return cancelButton;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view.
	 * 
	 * @param dataViewContainer dataViewContainer for the fee.
	 * @param isSlabFee isSlabFee for the container.
	 */
	protected void notificationManageFeesDataView(final WebMarkupContainer dataViewContainer) {

		final ManageFeeDetailsDataProvider dataProvider = new ManageFeeDetailsDataProvider("feeName",
			feeBean.getManageDetailsList());
		dataView = new ManageFeeDetailsView(WICKET_ID_PAGEABLE, dataProvider);
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

		final String displayTotalItemsText = ManageAirtimeTopupFeeAddPanel.this.getLocalizer().getString(
			"fees.totalitems.header", ManageAirtimeTopupFeeAddPanel.this);

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

		protected ManageFeeDetailsView(String id, IDataProvider<ManageFeeDetailsBean> dataProvider) {
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
		protected void populateItem(final Item<ManageFeeDetailsBean> item) {
			final ManageFeeDetailsBean entry = (ManageFeeDetailsBean) item.getModelObject();
			item.setModel(new CompoundPropertyModel<ManageFeeDetailsBean>(entry));
			// Add the File name
			item.add(new Label("feeName"));
			item.add(new Label("glCode.id"));
			item.add(new AmountLabel("fixedFee"));
			item.add(new Label("percentageFee", entry.getPercentageFee().toString() + " %"));
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
	}

	/**
	 * This method handles the updation of Fee Bean.
	 */
	private void handleUpdateAirtimeFeeBean() {
		try {
			final String amountType = feeDetailsBean.getAmountType();
			if (amountType.equals(BtpnConstants.FIXED_INTEREST_RADIO)) {
				feeDetailsBean.setFixedFee(Long.valueOf(feeDetailsBean.getAmount()) * 100);
				feeDetailsBean.setPercentageFee(new BigDecimal("0.0"));
			} else {
				feeDetailsBean.setFixedFee(0L);
				feeDetailsBean.setPercentageFee(new BigDecimal(feeDetailsBean.getAmount()));
			}
			// Transaction GL Request
			final UpdateFeeRequest request = this.mobBasePage.getNewMobiliserRequest(UpdateFeeRequest.class);
			request.setVendorFeeSharing(ConverterUtils.convertToAirtimeVendorFeeSharing(feeBean));
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final UpdateFeeResponse response = this.mobBasePage.feeClient.updateFee(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				getSession().info(getLocalizer().getString("update.success", this));
				setResponsePage(ManageFeePage.class);
			} else {
				error(getLocalizer().getString("error.update.fees", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching Transaction GL Code List  ===> ", e);
		}
		setResponsePage(ManageAirtimeTopupFeePage.class);
	}

	/**
	 * Fetch the Manage Fee Details List from service.
	 * 
	 * @return List<ManageFeeDetailsBean> Manage Fee Details bean for ManageFeeBean
	 */
	private void handleDeleteAirtimeFees() {
		try {
			// Delete Fee Request.
			final DeleteFeeRequest request = this.mobBasePage.getNewMobiliserRequest(DeleteFeeRequest.class);
			request.setVendorFeeSharing(ConverterUtils.convertToAirtimeVendorFeeSharing(feeBean));
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final DeleteFeeResponse response = this.mobBasePage.feeClient.deleteFee(request);
			if (this.mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				getSession().info(getLocalizer().getString("delete.success", this));
			} else {
				getSession().error(getLocalizer().getString("error.delete.fees", this));
			}
		} catch (Exception e) {
			getSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while handleDeleteFees  ===> ", e);
		}
		setResponsePage(ManageAirtimeTopupFeePage.class);
	}
}
