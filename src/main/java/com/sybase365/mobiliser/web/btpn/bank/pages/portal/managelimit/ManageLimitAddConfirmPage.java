package com.sybase365.mobiliser.web.btpn.bank.pages.portal.managelimit;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.CreateLimitRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.limit.CreateLimitResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageLimitBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ManageLimitAddConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private ManageLimitBean bean;

	private static final Logger LOG = LoggerFactory.getLogger(ManageLimitAddConfirmPage.class);

	/**
	 * Constructor for this page.
	 */
	public ManageLimitAddConfirmPage(final ManageLimitBean bean) {
		super();
		this.bean = bean;
		initThisPageComponents();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
	}

	/**
	 * This is used for this page components
	 */
	protected void initThisPageComponents() {
		constructPage();
	}

	protected void constructPage() {
		Form<ManageLimitAddConfirmPage> form = new Form<ManageLimitAddConfirmPage>("confirmForm",
			new CompoundPropertyModel<ManageLimitAddConfirmPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("bean.productId.value"));
		form.add(new Label("bean.useCaseId.value"));
		form.add(new Label("bean.isPerCustomer",
			bean.getIsPerCustomer() ? BtpnConstants.YES_VALUE : BtpnConstants.NO_VALUE));
		form.add(new Label("bean.isApplyToPayee",
			bean.getIsApplyToPayee() ? BtpnConstants.YES_VALUE : BtpnConstants.NO_VALUE));
		form.add(new AmountLabel("bean.dailyLimit"));
		form.add(new AmountLabel("bean.weeklyLimit"));
		form.add(new AmountLabel("bean.monthlyLimit"));
		form.add(addConfirmButton());
		form.add(addCancelButton());
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addConfirmButton() {
		Button addButton = new Button("btnCreate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleAddManageLimit();
				setResponsePage(ManageLimitPage.class);
			}
		};
		return addButton;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageLimitPage.class);
			}
		}.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method fetches the list of Manage Limit details.
	 * 
	 * @param ManageLimitBean returns the ManageLimitBean.
	 */
	private void handleAddManageLimit() {
		CreateLimitRequest request;
		try {
			request = this.getNewMobiliserRequest(CreateLimitRequest.class);
			request.setLimit(ConverterUtils.convertToLimit(bean));
			request.setMakerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final CreateLimitResponse response = this.limitClient.createLimit(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				getWebSession().info(getLocalizer().getString("confirm.success", ManageLimitAddConfirmPage.this));
			} else {
				getWebSession().error(handleSpecificErrorMessage(response.getStatus().getCode()));
			}
		} catch (Exception e) {
			getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while adding manage limit Details  ===> ", e);
		}
	}

	/**
	 * This method handles the specific error message.
	 */
	private String handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("limit.details.error", this);
		}
		return message;
	}
}
