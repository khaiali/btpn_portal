package com.sybase365.mobiliser.web.btpn.bank.common.panels;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.DeleteLimitRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.DeleteLimitResponse;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.UpdateLimitRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.UpdateLimitResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managelimit.ManageLimitAddConfirmPage;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.managelimit.ManageLimitPage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountTextField;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the ManageLimitDetailsPanel for bank portal.
 * 
 * @author Vikram Gunda
 */
public class ManageLimitDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private BtpnMobiliserBasePage mobBasePage;

	private ManageLimitBean limitBean;

	private boolean isAdd;

	private Component dailyLimitField;

	private Component weeklyLimitField;

	private Component monthlyLimitField;

	private static final Logger LOG = LoggerFactory.getLogger(ManageLimitDetailsPanel.class);

	public ManageLimitDetailsPanel(final String id, final BtpnMobiliserBasePage mobBasePage,
		final ManageLimitBean limitBean, final boolean isAdd) {
		super(id);
		this.mobBasePage = mobBasePage;
		this.limitBean = limitBean;
		this.isAdd = isAdd;
		constructPanel();
	}

	protected void constructPanel() {
		Form<ManageLimitDetailsPanel> form = new Form<ManageLimitDetailsPanel>("limitDetailsForm",
			new CompoundPropertyModel<ManageLimitDetailsPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("limitBean.productId.value").setVisible(!isAdd));
		// Choice Renderer
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		// Use Case Id
		form.add(new Label("limitBean.useCaseId.value").setVisible(!isAdd));
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("limitBean.useCaseId", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_USECASE, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()).setVisible(isAdd));
		// Product
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("limitBean.productId", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_PRODUCT, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).add(new ErrorIndicator()).setVisible(isAdd));

		form.add(new CheckBox("limitBean.isPerCustomer").setEnabled(false));
		form.add(new CheckBox("limitBean.isApplyToPayee"));
		form.add(dailyLimitField = new AmountTextField<Long>("limitBean.dailyLimit", Long.class, false).add(
			BtpnConstants.LIMIT_MAX_LENGTH).add(new ErrorIndicator()));
		form.add(weeklyLimitField = new AmountTextField<Long>("limitBean.weeklyLimit", Long.class, false).add(
			BtpnConstants.LIMIT_MAX_LENGTH).add(new ErrorIndicator()));
		form.add(monthlyLimitField = new AmountTextField<Long>("limitBean.monthlyLimit", Long.class, false).add(
			BtpnConstants.LIMIT_MAX_LENGTH).add(new ErrorIndicator()));
		// Add add Button
		form.add(addAddButton());
		// Add add Button
		form.add(addUpdateButton());
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
				boolean isValid = ManageLimitDetailsPanel.this.performValidations();
				if (isValid) {
					setResponsePage(new ManageLimitAddConfirmPage(limitBean));
				}
			}
		};
		addButton.setVisible(isAdd);
		return addButton;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addUpdateButton() {
		Button editButton = new Button("updateBtn") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				boolean isValid = ManageLimitDetailsPanel.this.performValidations();
				if (isValid) {
					handleUpdateManageLimit();
					setResponsePage(ManageLimitPage.class);
				}
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
				boolean isValid = ManageLimitDetailsPanel.this.performValidations();
				if (isValid) {
					setResponsePage(ManageLimitPage.class);
					handleDeleteManageLimit();
				}
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
				setResponsePage(ManageLimitPage.class);
			}
		}.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method performs the validations.
	 */
	private boolean performValidations() {
		boolean isValid = true;
		if (!(limitBean.getIsPerCustomer() || limitBean.getIsApplyToPayee())) {
			error(getLocalizer().getString("checkbox.shouldbe.checked", this));
			isValid = false;
		}
		final Long dailyLimit = limitBean.getDailyLimit();
		final Long weeklyLimit = limitBean.getWeeklyLimit();
		final Long monthlyLimit = limitBean.getMonthlyLimit();
		// Atleast one limit is required.
		if (!(PortalUtils.exists(dailyLimit) || PortalUtils.exists(weeklyLimit) || PortalUtils.exists(monthlyLimit))) {
			error(getLocalizer().getString("limit.required", this));
			isValid = false;
		}
		//Weekly limit should be greater than monthly limit.
		if (PortalUtils.exists(dailyLimit) && PortalUtils.exists(weeklyLimit) && weeklyLimit < dailyLimit) {
			weeklyLimitField.error(getLocalizer().getString("limitBean.daily.weekly.limit", this));
			isValid = false;
		}
		if (PortalUtils.exists(weeklyLimit) && PortalUtils.exists(monthlyLimit) && monthlyLimit < weeklyLimit) {
			monthlyLimitField.error(getLocalizer().getString("limitBean.weekly.monthly.limit", this));
			isValid = false;
		}
		if (PortalUtils.exists(dailyLimit) && PortalUtils.exists(monthlyLimit) && monthlyLimit < dailyLimit) {
			monthlyLimitField.error(getLocalizer().getString("limitBean.monthly.daily.limit", this));
			isValid = false;
		}
		return isValid;
	}

	/**
	 * This method updates the Manage Limit details.
	 */
	private void handleUpdateManageLimit() {
		UpdateLimitRequest request;
		try {
			request = this.mobBasePage.getNewMobiliserRequest(UpdateLimitRequest.class);
			request.setLimit(ConverterUtils.convertToLimit(limitBean));
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final UpdateLimitResponse response = this.mobBasePage.limitClient.updateLimit(request);
			if (mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				mobBasePage.getWebSession()
					.info(getLocalizer().getString("edit.success", ManageLimitDetailsPanel.this));
			} else {
				mobBasePage.getWebSession().error(getLocalizer().getString("limit.update.error", this));
			}
		} catch (Exception e) {
			mobBasePage.getWebSession().error(mobBasePage.getLocalizer().getString("error.exception", mobBasePage));
			LOG.error("Exception occured while updating manage limits ===> ", e);
		}
	}

	/**
	 * This method deletes the ManageLimit Details.
	 */
	private void handleDeleteManageLimit() {
		DeleteLimitRequest request;
		try {
			request = this.mobBasePage.getNewMobiliserRequest(DeleteLimitRequest.class);
			request.setLimit(ConverterUtils.convertToLimit(limitBean));
			request.setMakerId(this.mobBasePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final DeleteLimitResponse response = this.mobBasePage.limitClient.deleteLimit(request);
			if (mobBasePage.evaluateBankPortalMobiliserResponse(response)) {
				mobBasePage.getWebSession().info(
					getLocalizer().getString("removed.success", ManageLimitDetailsPanel.this));
			} else {
				mobBasePage.getWebSession().error(getLocalizer().getString("limit.delete.error", this));
			}
		} catch (Exception e) {
			mobBasePage.getWebSession().error(mobBasePage.getLocalizer().getString("error.exception", mobBasePage));
			LOG.error("Exception occured while deleting manage limits  ===> ", e);
		}
	}

}
