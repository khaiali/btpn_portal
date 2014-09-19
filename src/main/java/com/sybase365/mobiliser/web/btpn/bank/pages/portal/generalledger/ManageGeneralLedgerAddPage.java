package com.sybase365.mobiliser.web.btpn.bank.pages.portal.generalledger;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.SetGeneralLedgerRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.generalledger.SetGeneralLedgerResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.ManageGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;
import com.sybase365.mobiliser.web.util.PortalUtils;

/**
 * This is the Add Page for Manage General Ledger List. This page adds the General Ledger screen.
 * 
 * @author Vikram Gunda
 */
public class ManageGeneralLedgerAddPage extends BtpnBaseBankPortalSelfCarePage {

	/** Ledger Bean */
	private ManageGeneralLedgerBean ledgerBean;

	private static final Logger LOG = LoggerFactory.getLogger(ManageGeneralLedgerAddPage.class);

	private BtpnLocalizableLookupDropDownChoice<CodeValue> parentGLCode;

	/**
	 * Constructor for this page.
	 */
	public ManageGeneralLedgerAddPage() {
		super();
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@SuppressWarnings("unchecked")
	private void constructPage() {
		Form<ManageGeneralLedgerEditPage> form = new Form<ManageGeneralLedgerEditPage>("glAddForm",
			new CompoundPropertyModel<ManageGeneralLedgerEditPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new TextField<String>("ledgerBean.glCode").setRequired(true)
			.add(new PatternValidator(BtpnConstants.GL_CODE_REGEX)).add(BtpnConstants.GL_MINIMUM_LENGTH)
			.add(new ErrorIndicator()));
		form.add(new TextField<String>("ledgerBean.glDescription").setRequired(true).add(new ErrorIndicator()));
		form.add(new CheckBox("ledgerBean.isRoot").add(new ErrorIndicator()));
		form.add(new CheckBox("ledgerBean.isLeaf").add(new ErrorIndicator()));
		form.add(parentGLCode = (BtpnLocalizableLookupDropDownChoice<CodeValue>) new BtpnLocalizableLookupDropDownChoice<CodeValue>(
			"ledgerBean.parentGlCode", CodeValue.class, BtpnConstants.RESOURCE_BUNDLE_PARENT_GENERAL_LEDGER_CODES, this)
			.setNullValid(true).add(new ErrorIndicator()));
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("ledgerBean.type", CodeValue.class,
			BtpnConstants.RESOURCE_BUNDLE_GENERAL_LEDGER_TYPES, this).setRequired(true).add(new ErrorIndicator()));
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
				if (!performClientSideValidations()) {
					return;
				}
				// handles the add General Ledger
				handleAddGeneralLedger();
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
	 * This method peforms the client side validations
	 */
	private boolean performClientSideValidations() {
		final boolean isLeaf = ledgerBean.getIsLeaf();
		final boolean isRoot = ledgerBean.getIsRoot();
		if (isLeaf && isRoot) {
			error(getLocalizer().getString("root.or.leaf.select", this));
			return false;
		}
		if (!(isLeaf || isRoot)) {
			error(getLocalizer().getString("root.or.leaf.select", this));
			return false;
		}
		if (isLeaf && !PortalUtils.exists(ledgerBean.getParentGlCode())) {
			parentGLCode.error(getLocalizer().getString("ledgerBean.parentGlCode.Required", this));
			return false;
		}
		if (isRoot && PortalUtils.exists(ledgerBean.getParentGlCode())) {
			parentGLCode.error(getLocalizer().getString("ledgerBean.parentGlCode.Not.Required", this));
			return false;
		}
		return true;
	}

	/**
	 * This method adds the new general ledger details.
	 * 
	 * @return ManageGeneralLedgerBean returns the list of ManageGeneralLedgerBean details
	 */
	private void handleAddGeneralLedger() {
		SetGeneralLedgerRequest request;
		try {
			request = this.getNewMobiliserRequest(SetGeneralLedgerRequest.class);
			request.setGeneralledger(ConverterUtils.convertToGeneralLedger(ledgerBean));
			request.setMakerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setIsUpdate(false);
			final SetGeneralLedgerResponse response = this.generalLedgerClient.setGeneralLedger(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				getWebSession().info(getLocalizer().getString("gl.submit.success", ManageGeneralLedgerAddPage.this));
				setResponsePage(ManageGeneralLedgerPage.class);
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
