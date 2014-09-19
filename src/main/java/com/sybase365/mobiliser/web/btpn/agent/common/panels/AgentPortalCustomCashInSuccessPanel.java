package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashInBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.AgentPortalCustomCashInPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;


public class AgentPortalCustomCashInSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;
	protected AgentCustomCashInBean cashInBean;

	/**
	 * Constructor for the panel.
	 * 
	 * @param id id for the page.
	 * @param basePage base page for the panel
	 * @param cashInBean Cashin Bean.
	 */
	public AgentPortalCustomCashInSuccessPanel(final String id, final BtpnBaseAgentPortalSelfCarePage basePage,
		final AgentCustomCashInBean cashInBean) {
		
		super(id);
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		constructPanel();
	}

	/**
	 * Constructs a panel.
	 */
	protected void constructPanel() {
		
		final Form<AgentPortalCustomCashInSuccessPanel> form = new Form<AgentPortalCustomCashInSuccessPanel>("cashInSuccessForm",
			new CompoundPropertyModel<AgentPortalCustomCashInSuccessPanel>(this));
		
		// Add FeedBack panel
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("cashInBean.accountNumber"));
		form.add(new Label("cashInBean.msisdn"));
		form.add(new Label("cashInBean.displayName"));
		form.add(new AmountLabel("cashInBean.cashinAmount", true, true));

		form.add(new AjaxButton("finishButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(AgentPortalCustomCashInPage.class);
			};
		});

		add(form);
	}

}
