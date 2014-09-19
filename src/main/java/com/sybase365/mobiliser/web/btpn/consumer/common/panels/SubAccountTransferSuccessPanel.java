package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.SubAccountTransferPage;

/**
 * This is the SubAccountTransferSuccessPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class SubAccountTransferSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected SubAccountsBean subAccountBean;

	protected String subAccountPin;

	public SubAccountTransferSuccessPanel(String id, BtpnMobiliserBasePage basePage, SubAccountsBean subAccountBean) {
		super(id);
		this.basePage = basePage;
		this.subAccountBean = subAccountBean;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<SubAccountTransferSuccessPanel> form = new Form<SubAccountTransferSuccessPanel>("subAccountPinForm",
			new CompoundPropertyModel<SubAccountTransferSuccessPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));
		// SubAccount Fund Transfer Success Message
		String successMessage = SubAccountTransferSuccessPanel.this.getLocalizer().getString("label.successMessage",
			SubAccountTransferSuccessPanel.this);
		if (successMessage != null) {
			basePage.getWebSession().info(successMessage);
		}
		form.add(new Label("subAccountBean.name"));
		form.add(new Label("subAccountBean.description"));
		form.add(new Label("subAccountBean.accountId"));
		form.add(new AmountLabel("subAccountBean.amount"));
		form.add(new Label("subAccountBean.remarks"));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new SubAccountTransferPage());
			};
		});

		add(form);
	}

	public String getSubAccountPin() {
		return subAccountPin;
	}

	public void setSubAccountPin(String subAccountPin) {
		this.subAccountPin = subAccountPin;
	}

}
