package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.DeleteProductRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.DeleteProductResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.UpdateProductRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.product.UpdateProductResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsBean;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageProductsRangeBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsAddConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.manageproducts.ManageProductsPage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.btpn.util.ProductValueValidator;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ManageProductsDetailsPanel for Manage Products Page.
 * 
 * @author Vikram Gunda
 */
public class ManageProductsDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private BtpnMobiliserBasePage mobBasePage;

	private ManageProductsBean productsBean;

	private ListDataProvider<ManageProductsRangeBean> manageProductsDataProvider;

	private static final String WICKET_ID_PAGEABLE = "pageable";

	private boolean isAdd;

	private WebMarkupContainer productsRangeContainer;

	private static final Logger LOG = LoggerFactory.getLogger(ManageProductsDetailsPanel.class);

	public ManageProductsDetailsPanel(String id, BtpnMobiliserBasePage mobBasePage,
		final ManageProductsBean productsBean, final boolean isAdd) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.productsBean = productsBean;
		this.isAdd = isAdd;
		constructPanel();
	}

	protected void constructPanel() {
		Form<ManageProductsDetailsPanel> form = new Form<ManageProductsDetailsPanel>("productDetailsForm",
			new CompoundPropertyModel<ManageProductsDetailsPanel>(this)) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onError() {
				// Need to update the models on error as well. Wicket doesn't update on error
				FormComponent.visitComponentsPostOrder(this,
					new com.sybase365.mobiliser.web.btpn.util.FormModelUpdateVisitor(this));
			}
		};
		form.add(new FeedbackPanel("errorMessages"));
		final WebMarkupContainer productIdConatiner = new WebMarkupContainer("productIdRequired");
		productIdConatiner.add(new TextField<String>("productsBean.productId").setRequired(true)
			.add(new ErrorIndicator()).setEnabled(false));
		productIdConatiner.setVisible(!isAdd);
		form.add(productIdConatiner);
		form.add(new TextField<String>("productsBean.productName").setRequired(true).add(new ErrorIndicator()));

		// product Label
		final String productLabel = isAdd == false ? getLocalizer().getString("header.editProduct", this) : getLocalizer()
			.getString("header.addProduct", this);
		// Product Label
		form.add(new Label("product.label", productLabel));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		// Product Types for Manage Products
		form.add(new TextField<String>("productsBean.productType.id").setRequired(true).add(new ErrorIndicator())
			.setVisible(!isAdd).setEnabled(false));
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("productsBean.productType", CodeValue.class,
			BtpnConstants.PRODUCT_TYPES, this, Boolean.FALSE, true).setChoiceRenderer(codeValueChoiceRender)
			.setRequired(true).setVisible(isAdd).add(new ErrorIndicator()));

		final IChoiceRenderer<CodeValue> codeValueChoiceRenderGL = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);

		// Product GL Codes and Fee GL Codes
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("productsBean.productGLCode", CodeValue.class,
			BtpnConstants.PRODUCT_GL_CODES, this, Boolean.TRUE, false).setChoiceRenderer(codeValueChoiceRenderGL)
			.setRequired(true).add(new ErrorIndicator()));
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("productsBean.feeGLCode", CodeValue.class,
			BtpnConstants.PRODUCT_GL_CODES, this, Boolean.TRUE, false).setChoiceRenderer(codeValueChoiceRenderGL)
			.setRequired(true).add(new ErrorIndicator()));

		form.add(new TextField<String>("productsBean.roleName").setRequired(true).add(new ErrorIndicator())
			.setEnabled(isAdd));

		form.add(new TextField<String>("productsBean.roleDescription").setRequired(true).add(new ErrorIndicator()));

		form.add(new AmountTextField<Long>("productsBean.minBalance", Long.class).add(
			BtpnConstants.PRODUCT_AMOUNTS_LENGTH).add(new ErrorIndicator()));
		form.add(new AmountTextField<Long>("productsBean.initialDeposit", Long.class).add(
			BtpnConstants.PRODUCT_AMOUNTS_LENGTH).add(new ErrorIndicator()));
		form.add(new AmountTextField<Long>("productsBean.adminFee", Long.class).add(
			BtpnConstants.PRODUCT_AMOUNTS_LENGTH).add(new ErrorIndicator()));

		// Add the Manage Products container
		productsRangeContainer = new WebMarkupContainer("productsRangeContainer");
		manageProductsRangeDataView(productsRangeContainer);
		productsRangeContainer.setOutputMarkupId(true);
		productsRangeContainer.setVisible(productsBean.getRangeList().size() != 0);
		form.add(productsRangeContainer);
		// Add add Button
		form.add(addAddButton());
		// Add add Button
		form.add(addEditButton());
		// Add add Button
		form.add(addRemoveButton());
		// Add add Button
		form.add(addCancelButton());

		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addAddButton() {
		Button addButton = new Button("addBtn") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				checkInitialAmounts();
				setResponsePage(new ManageProductsAddConfirmPage(productsBean));
			}
		};
		addButton.setVisible(isAdd);
		return addButton;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addEditButton() {
		Button editButton = new Button("editBtn") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleEditProduct();
				setResponsePage(ManageProductsPage.class);
			}
		};
		editButton.setVisible(!isAdd);
		return editButton;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addRemoveButton() {
		Button removeButton = new Button("removeBtn") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleRemoveProduct();
				setResponsePage(ManageProductsPage.class);
			}
		};
		removeButton.setDefaultFormProcessing(false);
		removeButton.setVisible(!isAdd);
		return removeButton;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("cancelBtn") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageProductsPage.class);
			}
		}.setDefaultFormProcessing(false);
		cancelButton.setVisible(!isAdd);
		return cancelButton;
	}

	/**
	 * This method adds the AttachmentsDataView for the file upload and also adds the sorting logic for data view
	 */
	protected void manageProductsRangeDataView(final WebMarkupContainer dataViewContainer) {

		final List<ManageProductsRangeBean> manageProductsRangeList = productsBean.getRangeList();
		// Create the Attachment View
		manageProductsDataProvider = new ListDataProvider<ManageProductsRangeBean>(manageProductsRangeList);
		final DataView<ManageProductsRangeBean> dataView = new ManageProductsRangeDataView(WICKET_ID_PAGEABLE,
			manageProductsDataProvider);
		dataViewContainer.addOrReplace(dataView);
	}

	/**
	 * This is the ManageProductsDataView for Managing Products Range Details.
	 * 
	 * @author Vikram Gunda
	 */
	private class ManageProductsRangeDataView extends DataView<ManageProductsRangeBean> {

		private static final long serialVersionUID = 1L;

		protected ManageProductsRangeDataView(String id, IDataProvider<ManageProductsRangeBean> dataProvider) {
			super(id, dataProvider);
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);

		}

		@Override
		protected void onBeforeRender() {
			super.onBeforeRender();
		}

		@Override
		protected void populateItem(final Item<ManageProductsRangeBean> item) {

			final ManageProductsRangeBean entry = item.getModelObject();

			item.setModel(new CompoundPropertyModel<ManageProductsRangeBean>(entry));
			// Add the Min Range
			item.add(new AmountTextField<Long>("minRange", Long.class).setRequired(true)
				.add(BtpnConstants.PRODUCT_AMOUNTS_LENGTH).add(new ErrorIndicator()));

			// Add the Max Range
			item.add(new AmountTextField<Long>("maxRange", Long.class).add(BtpnConstants.PRODUCT_AMOUNTS_LENGTH).add(
				new ErrorIndicator()));

			// Add Radio Buttons
			final RadioGroup<String> rg = new RadioGroup<String>("interest");
			rg.setRequired(true).add(new ErrorIndicator());
			rg.add(new Radio<String>("radio1", Model.of(BtpnConstants.FIXED_INTEREST_RADIO), rg)
				.add(new ErrorIndicator()));
			rg.add(new Radio<String>("radio2", Model.of(BtpnConstants.PERCENT_INTEREST_RADIO), rg)
				.add(new ErrorIndicator()));
			item.add(rg);

			// Add the Value
			item.add(new TextField<String>("value").add(new ProductValueValidator(rg)).setRequired(true)
				.add(new ErrorIndicator()));

			item.add(new SimpleAttributeModifier("class", BtpnConstants.DATA_VIEW_EVEN_ROW_CSS));

			final int size = internalGetDataProvider().size();

			final WebMarkupContainer addContainer = new WebMarkupContainer("addContainer");
			// Add and Delete links
			AjaxSubmitLink addLink = new AjaxSubmitLink("addLink") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					handleAddLinkClick(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					handleAddLinkClick(target);
				}
			};
			addContainer.add(addLink);
			addLink.setVisible(item.getIndex() == size - 1);
			addContainer.setVisible(isAdd);
			// Add and Delete links
			final WebMarkupContainer removeContainer = new WebMarkupContainer("removeContainer");
			AjaxSubmitLink deleteLink = new AjaxSubmitLink("deleteLink") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onSubmit(AjaxRequestTarget target, Form<?> form) {
					handleDeleteLinkClick(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					handleDeleteLinkClick(target);
				}
			};
			removeContainer.add(deleteLink);
			deleteLink.setVisible(item.getIndex() == size - 1);
			removeContainer.setVisible(isAdd);
			item.add(addContainer);
			item.add(removeContainer);
		}

		@Override
		public boolean isVisible() {
			return internalGetDataProvider().size() != 0;

		}

		private void handleAddLinkClick(AjaxRequestTarget target) {
			final ManageProductsRangeBean bean = new ManageProductsRangeBean();
			productsBean.addRangeBeanToList(bean);
			target.addComponent(productsRangeContainer);
			target.appendJavascript("display()");
		}

		private void handleDeleteLinkClick(AjaxRequestTarget target) {
			final int size = productsBean.getRangeList().size();
			if (productsBean.getRangeList().size() != 1) {
				productsBean.removeRangeBeanFromList(size);
				target.addComponent(productsRangeContainer);
				target.appendJavascript("display()");
			}
		}
	}

	/**
	 * This method handles the edit product
	 * 
	 * @author Vikram Gunda
	 */
	private void handleEditProduct() {
		try {
			final UpdateProductRequest request = this.mobBasePage.getNewMobiliserRequest(UpdateProductRequest.class);
			checkInitialAmounts();
			request.setProduct(ConverterUtils.convertToProduct(productsBean));
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			UpdateProductResponse response = this.mobBasePage.getProductClient().updateProduct(request);
			if (mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				mobBasePage.getWebSession().info(
					getLocalizer().getString("edit.success", ManageProductsDetailsPanel.this));
			} else {
				handleEditSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			mobBasePage.getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Error Occured while updating Product ", e);
		}
	}

	/**
	 * This method handles the edit product
	 * 
	 * @author Vikram Gunda
	 */
	private void handleRemoveProduct() {
		try {
			final DeleteProductRequest request = this.mobBasePage.getNewMobiliserRequest(DeleteProductRequest.class);
			request.setCustomerTypeId(productsBean.getProductId());
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			DeleteProductResponse response = this.mobBasePage.getProductClient().deleteProduct(request);
			if (mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				mobBasePage.getWebSession().info(
					getLocalizer().getString("removed.success", ManageProductsDetailsPanel.this));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			mobBasePage.getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Error Occured while updating Product ", e);
		}
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private void handleEditSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.update.products", this);
		}
		this.mobBasePage.getWebSession().error(message);
	}

	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.remove.products." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("error.remove.products", this);
		}
		this.mobBasePage.getWebSession().error(message);
	}

	private void checkInitialAmounts() {
		if (!PortalUtils.exists(productsBean.getAdminFee())) {
			productsBean.setAdminFee(0L);
		}
		if (!PortalUtils.exists(productsBean.getInitialDeposit())) {
			productsBean.setInitialDeposit(0L);
		}
		if (!PortalUtils.exists(productsBean.getMinBalance())) {
			productsBean.setMinBalance(0L);
		}
	}
}
