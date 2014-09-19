package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.consumer.beans.FundTransferBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.FundTransferPage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;

/**
 * This is the FundTransferSuccessPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class FundTransferSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected FundTransferBean fundTransfer;

	String selectedFundTransferType;

	public FundTransferSuccessPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage,
		FundTransferBean fundTransferBean) {
		super(id);
		this.basePage = basePage;
		this.fundTransfer = fundTransferBean;
		selectedFundTransferType = fundTransfer.getSelectedFundTransferType();
		constructPanel();
	}

	protected void constructPanel() {

		final Form<FundTransferSuccessPanel> form = new Form<FundTransferSuccessPanel>("fundTransferMobileSuccessForm",
			new CompoundPropertyModel<FundTransferSuccessPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		final String messageKey = "header." + selectedFundTransferType;
		String headerMessage = getLocalizer().getString(messageKey, this);

		form.add(new Label("fundTransferHeaderMessage", headerMessage));

		form.add(new AmountLabel("fundTransfer.amount"));
		form.add(new Label("fundTransfer.payeeMsisdn"));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(FundTransferPage.class);
			};
		});

		add(form);
	}

}
