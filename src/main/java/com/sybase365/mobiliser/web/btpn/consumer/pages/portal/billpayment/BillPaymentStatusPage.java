package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.billpayment;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.consumer.beans.BillPaymentPerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalHomePage;

/**
 * This is the page for BillPayment Confirmation.
 * 
 * @author Vikram Gunda
 */
public class BillPaymentStatusPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private BillPaymentPerformBean billPayBean;

	public BillPaymentStatusPage(final BillPaymentPerformBean billPayBean) {
		super();
		this.billPayBean = billPayBean;
		initPageComponents();
	}
	
	/**
	 * This is the init page components for Bill Pay Perform page.
	 */
	public void initPageComponents() {
		final Form<BillPaymentStatusPage> form = new Form<BillPaymentStatusPage>("confirmBillPay",
			new CompoundPropertyModel<BillPaymentStatusPage>(this));
		form.add(new Label("billPayBean.statusMessage").setRenderBodyOnly(true));
		
		addSubmitButton(form);
		add(form);
	}

	/**
	 * This adds the submit button.
	 */
	private void addSubmitButton(final Form<BillPaymentStatusPage> form) {

		Button confirmBtn = new Button("btnOk") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				BillPaymentStatusPage.this.handleCancelButtonRedirectToHomePage(ConsumerPortalHomePage.class);
			};
		};
		form.add(confirmBtn);
	}
}
