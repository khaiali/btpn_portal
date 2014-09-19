package com.sybase365.mobiliser.web.btpn.consumer.pages.portal.airtimetopup;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirtimePerformBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.ConsumerPortalHomePage;

public class AirtimeTopupStatusPage extends BtpnBaseConsumerPortalSelfCarePage {

	private static final long serialVersionUID = 1L;

	private AirtimePerformBean airtimeBean;

	public AirtimeTopupStatusPage(final AirtimePerformBean airtimeBean) {
		super();
		this.airtimeBean = airtimeBean;
		initPageComponents();
	}

	/**
	 * This is the init page components for Bill Pay Perform page.
	 */
	public void initPageComponents() {

		final Form<AirtimeTopupStatusPage> form = new Form<AirtimeTopupStatusPage>(
				"airtimeStatusPage",
				new CompoundPropertyModel<AirtimeTopupStatusPage>(this));

		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("airtimeBean.productId", airtimeBean.getBillerDescription()));
		form.add(new Label("airtimeBean.label"));
		form.add(new AmountLabel("airtimeBean.feeAmount"));
		form.add(new Label("airtimeBean.selectedMsisdn.id"));

		addSubmitButton(form);

		add(form);
	}

	/**
	 * This adds the submit button.
	 */
	private void addSubmitButton(final Form<AirtimeTopupStatusPage> form) {

		Button confirmBtn = new Button("btnOk") {

			private static final long serialVersionUID = 1L;

			public void onSubmit() {
				AirtimeTopupStatusPage.this
						.handleCancelButtonRedirectToHomePage(ConsumerPortalHomePage.class);
			};
		};
		form.add(confirmBtn);
	}
}
