package com.sybase365.mobiliser.web.btpn.bank.pages.portal.transledger;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactiongl.UpdateTransactionGLRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.transactiongl.UpdateTransactionGLResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.bank.beans.TransactionGeneralLedgerBean;
import com.sybase365.mobiliser.web.btpn.bank.pages.portal.selfcare.BtpnBaseBankPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.ConverterUtils;

/**
 * This is the Edit Page for Manage General Ledger List. This page only updates the description of the General Ledger
 * screen.
 * 
 * @author Vikram Gunda
 */
public class TransactionGeneralLedgerEditPage extends BtpnBaseBankPortalSelfCarePage {

	private TransactionGeneralLedgerBean ledgerBean;

	private static final Logger LOG = LoggerFactory.getLogger(TransactionGeneralLedgerEditPage.class);

	/**
	 * Constructor for this page.
	 */
	public TransactionGeneralLedgerEditPage(final TransactionGeneralLedgerBean ledgerBean) {
		super();
		this.ledgerBean = ledgerBean;
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		Form<TransactionGeneralLedgerEditPage> form = new Form<TransactionGeneralLedgerEditPage>("glEditForm",
			new CompoundPropertyModel<TransactionGeneralLedgerEditPage>(this));
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new TextField<String>("ledgerBean.useCaseName").setEnabled(false));
		form.add(new TextField<String>("ledgerBean.currentGL.id").setEnabled(false));
		form.add(new TextField<String>("ledgerBean.currentGL.value").setEnabled(false));
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION_ID_VALUE, BtpnConstants.ID_EXPRESSION);
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("ledgerBean.newGL", CodeValue.class,
			BtpnConstants.PRODUCT_GL_CODES, this, Boolean.TRUE, false).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
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
				setResponsePage(TransactionGeneralLedgerPage.class);
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
		try {
			final UpdateTransactionGLRequest request = this.getNewMobiliserRequest(UpdateTransactionGLRequest.class);
			request.setTransactionGl(ConverterUtils.convertToTransactionGeneralLedgerBean(ledgerBean));
			request.setMakerId(this.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId());
			request.setIsUpdate(true);
			final UpdateTransactionGLResponse response = this.transactionGLEndPoint.updateTransactionGL(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				this.getWebSession().info(getLocalizer().getString("generalledger.success", this));
				setResponsePage(TransactionGeneralLedgerPage.class);
			} else {
				this.getWebSession().error(getLocalizer().getString("error.generalledger", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while fetching GL Code Details  ===> ", e);
		}
	}
}
