package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.bank.beans.CodeValue;
import com.sybase365.mobiliser.web.btpn.consumer.beans.StandingInstructionsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.BillPaymentPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.StandingInstructionFundTransferPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.StandingInstructionTopupPage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the StandingInstructionsPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class AddStandingInstructionsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	private StandingInstructionsBean instructionsBean;

	public AddStandingInstructionsPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		StandingInstructionsBean instructionsBean) {
		super(id);
		this.basePage = basePage;
		this.instructionsBean = instructionsBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<AddStandingInstructionsPanel> form = new Form<AddStandingInstructionsPanel>(
			"standingInstructionsForm", new CompoundPropertyModel<AddStandingInstructionsPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		// Add the Standing Instruction Type Drop down only for consumer Portal
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("instructionsBean.instructionType",
			CodeValue.class, BtpnConstants.RESOURCE_BUBDLE_SI_TYPE, this, Boolean.TRUE, Boolean.FALSE)
			.setNullValid(false).setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new Button("addButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				String selectedType = instructionsBean.getInstructionType().getId();
				if (selectedType.equals(BtpnConstants.INSTRUCTION_TYPE_BILL_PAYMENT)) {
					setResponsePage(new BillPaymentPage());
				} else if (selectedType.equals(BtpnConstants.INSTRUCTION_TYPE_FUND_TRANSFER)) {
					setResponsePage(new StandingInstructionFundTransferPage());
				} else if (selectedType.equals(BtpnConstants.INSTRUCTION_TYPE_TOPUP)) {
					setResponsePage(new StandingInstructionTopupPage());
				}
			};
		});

		add(form);
	}

}
