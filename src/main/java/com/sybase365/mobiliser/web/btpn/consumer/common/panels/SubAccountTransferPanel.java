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
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SelectSubAccountPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.util.BtpnConstants;
import com.sybase365.mobiliser.web.btpn.util.BtpnLocalizableLookupDropDownChoice;

/**
 * This is the SubAccountTransferPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class SubAccountTransferPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected SubAccountsBean subAccountsBean;

	public SubAccountTransferPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage, SubAccountsBean subAccount) {
		super(id);
		this.basePage = basePage;
		this.subAccountsBean = subAccount;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<SubAccountTransferPanel> form = new Form<SubAccountTransferPanel>("subAccountTransferForm",
			new CompoundPropertyModel<SubAccountTransferPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		final IChoiceRenderer<CodeValue> codeValueChoiceRender = new ChoiceRenderer<CodeValue>(
			BtpnConstants.DISPLAY_EXPRESSION, BtpnConstants.ID_EXPRESSION);

		form.add(new BtpnLocalizableLookupDropDownChoice<CodeValue>("subAccountsBean.transferType", CodeValue.class,
			BtpnConstants.RESOURCE_BUBDLE_SUB_ACCOUNT_TYPE, this, Boolean.TRUE, Boolean.FALSE).setNullValid(false)
			.setChoiceRenderer(codeValueChoiceRender).setRequired(true).add(new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new SelectSubAccountPage(subAccountsBean));
			};
		});
		add(form);
	}
}
