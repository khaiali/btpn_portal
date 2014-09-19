package com.sybase365.mobiliser.web.btpn.bank.pages.portal.addhelp;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.SaveHelpPageContentRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.SaveHelpPageContentResponse;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;

/**
 * This is the Add Help Confirm page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class AddHelpConfirmPage extends BtpnBaseBankPortalSelfCarePage {

	private CodeValue portalType;

	private String helpText;

	private static final Logger LOG = LoggerFactory.getLogger(AddHelpConfirmPage.class);

	/**
	 * Constructor for this page.
	 */
	public AddHelpConfirmPage(final CodeValue portalType, final String helpText) {
		super();
		this.portalType = portalType;
		this.helpText = helpText;
		initThisPageComponents();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	protected void initThisPageComponents() {
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<AddHelpConfirmPage> form = new Form<AddHelpConfirmPage>("confirmAddHelpForm",
			new CompoundPropertyModel<AddHelpConfirmPage>(this));
		form.add(new Label("portalType.value"));
		form.add(new Label("helpText"));
		form.add(addCancelButton());
		form.add(addUpdateButton());
		add(form);
	}

	/**
	 * This method adds the Add button for the Manage Profile Page
	 */
	protected Button addUpdateButton() {
		Button submitButton = new Button("btnUpdate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleUpdateButton();
			}

		};
		return submitButton;
	}

	/**
	 * This method adds the update button for Add Help Page
	 */
	protected void handleUpdateButton() {
		try {
			final SaveHelpPageContentRequest request = new SaveHelpPageContentRequest();
			request.setPortalType(portalType.getId());
			request.setPageContent(helpText);
			request.setMakerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			final SaveHelpPageContentResponse response = this.getSupportClient().saveHelpPageContent(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				this.getMobiliserWebSession().info(getLocalizer().getString("confirm.success", this));
			} else {
				this.getMobiliserWebSession().error(getLocalizer().getString("confirm.error", this));
			}
		} catch (Exception e) {
			LOG.error("Exception occured while Confirming help ==> ", e);
			this.error(getLocalizer().getString("error.exception", this));
		}
		setResponsePage(AddHelpPage.class);
	}

	/**
	 * This method adds the Add button for the Manage Profile Page
	 */
	protected Button addCancelButton() {
		Button submitButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(AddHelpPage.class);
			}
		};
		submitButton.setDefaultFormProcessing(false);
		return submitButton;
	}
}
