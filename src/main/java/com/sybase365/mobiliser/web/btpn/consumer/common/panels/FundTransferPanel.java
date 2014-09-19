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
import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.FundTransferToMobilePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.FundTransferToOtherAccountsPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the FundTransferPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class FundTransferPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected FundTransferBean fundTransfer;

	public FundTransferPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage, FundTransferBean fundTransferBean) {
		super(id);
		this.basePage = basePage;
		this.fundTransfer = fundTransferBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<FundTransferPanel> form = new Form<FundTransferPanel>("fundTransferForm",
			new CompoundPropertyModel<FundTransferPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		// Add the Account Type Drop down only for consumer Portal
		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("fundTransfer.accountType", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_FT_ACCOUNT_TYPE, this, Boolean.FALSE, true).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				String selectedFTType = fundTransfer.getAccountType().getId();
				fundTransfer.setSelectedFundTransferType(selectedFTType);
				if (selectedFTType.equals(BtpnConstants.FT_ACCOUNT_TYPE_MOBILE)) {
					fundTransfer.setMobileSelected(true);
					setResponsePage(new FundTransferToMobilePage(fundTransfer));
				} else {
					setResponsePage(new FundTransferToOtherAccountsPage(fundTransfer));
				}
			};
		});

		add(form);
	}

}
