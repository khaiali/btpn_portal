package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.SetReceiptModeRequest;
import com.sybase365.mobiliser.custom.btpn.services.contract.v1_0.support.SetReceiptModeResponse;
import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnCustomer;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;
import com.sybase365.mobiliser.web.btpn.util.BtpnUtils;

/**
 * This is the Manage Products page for bank portals.
 * 
 * @author Vikram Gunda
 */
public class ChangeRecieptModePage extends BtpnBaseConsumerPortalSelfCarePage {

	private CodeValue recieptMode;

	private static final Logger LOG = LoggerFactory.getLogger(ChangeRecieptModePage.class);

	/**
	 * Constructor for this page.
	 */
	public ChangeRecieptModePage() {
		super();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	@Override
	protected void initOwnPageComponents() {
		super.initOwnPageComponents();
		final String recieptModeId = String.valueOf(this.getMobiliserWebSession().getBtpnLoggedInCustomer()
			.getTxnReceiptModeId());
		recieptMode = new CodeValue(recieptModeId, BtpnUtils.getDropdownValueFromId(
			lookupMapUtility.getLookupNamesMap(BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, this),
			BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE + "." + recieptModeId));
		constructPage();
	}

	/**
	 * This method should be used to initialise all components of the page which have to be available each time a fresh
	 * instance of the page has to be created.
	 */
	private void constructPage() {
		final Form<ChangeRecieptModePage> recieptModeForm = new Form<ChangeRecieptModePage>("recieptModeForm",
			new CompoundPropertyModel<ChangeRecieptModePage>(this));
		recieptModeForm.add(new FeedbackPanel("errorMessages"));
		recieptModeForm.add(new Label("recieptMode.value"));
		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);
		recieptModeForm.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("recieptMode", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_RECEIPT_MODE, this, Boolean.FALSE, true)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));
		recieptModeForm.add(addRecieptButton());
		add(recieptModeForm);
	}

	/**
	 * This method is used to add the reciept mode.
	 */
	private Button addRecieptButton() {
		Button cancelButton = new Button("btnAdd") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				handleUpdateReceiptMode();

			};
		};
		return cancelButton;
	}

	/**
	 * This method is used to handle the reciept Mode
	 */
	private void handleUpdateReceiptMode() {
		try {
			// Set Reciept Mode request
			final SetReceiptModeRequest request = this.getNewMobiliserRequest(SetReceiptModeRequest.class);
			final BtpnCustomer btpnCustomer = this.getMobiliserWebSession().getBtpnLoggedInCustomer();
			request.setCustomerId(btpnCustomer.getCustomerId());
			request.setReceiptModeId(Integer.valueOf(recieptMode.getId()));
			final SetReceiptModeResponse response = this.getSupportClient().setReceiptMode(request);
			if (evaluateBankPortalMobiliserResponse(response)) {
				info(getLocalizer().getString("reciept.success", this));
				btpnCustomer.setTxnReceiptModeId(Integer.valueOf(recieptMode.getId()));
			}else{
				error(getLocalizer().getString("reciept.fail", this));
			}
		} catch (Exception e) {
			error(getLocalizer().getString("error.exception", this));
			LOG.error("Exception occured while ChangeRecieptModePage  ===> ", e);
		}
	}
}
