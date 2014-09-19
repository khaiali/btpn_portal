package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.AddAccountConfirmPage;

/**
 * This is the AddAccountPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class AddAccountPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	SubAccountsBean subAccountBean;

	public AddAccountPanel(String id, BtpnMobiliserBasePage basePage) {
		super(id);
		this.basePage = basePage;
		constructPanel();
	}

	protected void constructPanel() {
		final Form<AddAccountPanel> form = new Form<AddAccountPanel>("addAccountForm",
			new CompoundPropertyModel<AddAccountPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		// Add name field to form
		form.add(new TextField<String>("subAccountBean.name").setRequired(true).add(new ErrorIndicator()));

		// create a Text area field for subAccntDescription
		form.add(new TextArea<String>("subAccountBean.description"));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(new AddAccountConfirmPage(subAccountBean));
			};
		});
		add(form);
	}

}
