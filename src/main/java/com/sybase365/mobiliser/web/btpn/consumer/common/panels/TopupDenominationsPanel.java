package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AirtimeDenominationDropdownChoice;
import com.sybase365.mobiliser.web.btpn.consumer.beans.AirTimeTopupBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.selfcare.BtpnBaseConsumerPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.standing.ConfirmTopupPage;

/**
 * This is the TopupDenominationsPanel page for consumer portals.
 * 
 * @author Narasa Reddy
 */
public class TopupDenominationsPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected AirTimeTopupBean topupBean;

	public TopupDenominationsPanel(String id, BtpnBaseConsumerPortalSelfCarePage basePage, AirTimeTopupBean topupBean) {
		super(id);
		this.basePage = basePage;
		this.topupBean = topupBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<TopupDenominationsPanel> form = new Form<TopupDenominationsPanel>("topupDenominationsForm",
			new CompoundPropertyModel<TopupDenominationsPanel>(this));

		// Add feedback panel for Error Messages
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new AirtimeDenominationDropdownChoice("topupBean.denomination", false, false, topupBean
			.getSelectedTelco().getId()));

		form.add(new AjaxButton("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(new ConfirmTopupPage(topupBean));
			};
		});

		add(form);
	}
}
