package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.SetGeneralLedgerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.SetGeneralLedgerResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Edit Page for Manage General Ledger List. This page only updates the description of the General Ledger
 * screen.
 * 
 * @author Vikram Gunda
 */
public class ManageGeneralLedgerEditPage extends BtpnBaseBankPortalSelfCarePage {

	private ManageGeneralLedgerBean ledgerBean;

	private static final Logger LOG = LoggerFactory.getLogger(ManageGeneralLedgerEditPage.class);

	/**
	 * Constructor for this page.
	 */
	public ManageGeneralLedgerEditPage(final ManageGeneralLedgerBean ledgerBean) {
		super();
		this.ledgerBean = ledgerBean;
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		Form<ManageGeneralLedgerEditPage> form = new Form<ManageGeneralLedgerEditPage>("glEditForm",
			new CompoundPropertyModel<ManageGeneralLedgerEditPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new TextField<String>("ledgerBean.glCode"));
		form.add(new RequiredTextField<String>("ledgerBean.glDescription").add(new ErrorIndicator()));

		final WebMarkupContainer isLeafContainer = new WebMarkupContainer("isLeafContainer");
		isLeafContainer.add(new TextField<String>("ledgerBean.isLeaf"));
		isLeafContainer.setVisible(ledgerBean.getIsLeaf());
		form.add(isLeafContainer);

		final WebMarkupContainer isParentGLCodeContainer = new WebMarkupContainer("parentGLCodeContainer");
		isParentGLCodeContainer.add(new TextField<String>("ledgerBean.parentGlCode.id"));
		isParentGLCodeContainer.setVisible(ledgerBean.getParentGlCode() != null);
		form.add(isParentGLCodeContainer);

		final WebMarkupContainer isRootContainer = new WebMarkupContainer("isRootContainer");
		isRootContainer.add(new TextField<String>("ledgerBean.isRoot"));
		isRootContainer.setVisible(ledgerBean.getIsRoot());
		form.add(isRootContainer);

		form.add(new TextField<String>("ledgerBean.type.value"));

		form.add(addSubmitButton());
		form.add(addCancelButton());

		// Add add Button
		add(form);

	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addSubmitButton() {
		Button submitButton = new Button("btnSubmit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleUpdateGeneralLedger();
				setResponsePage(ManageGeneralLedgerPage.class);
			}
		};
		return submitButton;
	}

	/**
	 * This method adds the Add button for the Manage Products
	 */
	protected Button addCancelButton() {
		Button cancelButton = new Button("btnCancel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(ManageGeneralLedgerPage.class);
			}
		};
		cancelButton.setDefaultFormProcessing(false);
		return cancelButton;
	}

	/**
	 * This method updates the general ledger details.
	 * 
	 * @return ManageGeneralLedgerBean returns the list of ManageGeneralLedgerBean details
	 */
	private void handleUpdateGeneralLedger() {
		SetGeneralLedgerRequest request;
		try {
			request = this.getNewMobiliserRequest(SetGeneralLedgerRequest.class);
			request.setGeneralledger(ConverterUtils.convertToGeneralLedger(ledgerBean));
			request.setMakerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setIsUpdate(true);
			final SetGeneralLedgerResponse response = this.generalLedgerClient.setGeneralLedger(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				getWebSession().info(getLocalizer().getString("gl.submit.success", ManageGeneralLedgerEditPage.this));
			} else {
				handleSpecificErrorMessage(response.getStatus().getCode());
			}
		} catch (Exception e) {
			getWebSession().error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching updating GL Code Details  ===> ", e);
		}
	}
	
	/**
	 * This method handles the specific error message.
	 */
	private void handleSpecificErrorMessage(final int errorCode) {
		// Specific error message handling
		final String messageKey = "error.gl." + errorCode;
		String message = getLocalizer().getString(messageKey, this);
		// Generice error messages
		if (messageKey.equals(message)) {
			message = getLocalizer().getString("gl.error", this);
		}
		this.getWebSession().error(message);
	}
}
