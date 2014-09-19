package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.BillerCodeDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.BillPaymentDetailsPage;

/**
 * This is the BillPaymentPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class BillPaymentPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	BillPaymentBean billPaymentBean;

	public BillPaymentPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage, BillPaymentBean billPaymentBean) {
		super(id);
		this.basePage = basePage;
		this.billPaymentBean = billPaymentBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<BillPaymentPanel> form = new Form<BillPaymentPanel>("billPaymentForm",
			new CompoundPropertyModel<BillPaymentPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		// Add the Biller Type Drop down only for consumer Portal
		form.add(new BillerCodeDropdownChoice("billPaymentBean.billerType", false, false).setRequired(true).add(
			new ErrorIndicator()));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
//				setResponsePage(new BillPaymentDetailsPage(billPaymentBean));
			};
		});
		add(form);
	}

}
