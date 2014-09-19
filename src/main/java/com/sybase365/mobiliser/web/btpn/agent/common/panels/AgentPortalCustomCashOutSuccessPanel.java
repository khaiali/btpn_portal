package com.sybase365.mobiliser.web.btpn.agent.common.panels;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCustomCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.AgentPortalCustomCashInPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;


public class AgentPortalCustomCashOutSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected AgentCustomCashOutBean cashOutBean;

	protected ModalWindow cashOutWindow;

	public AgentPortalCustomCashOutSuccessPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage,
		AgentCustomCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<AgentPortalCustomCashOutSuccessPanel> form = new Form<AgentPortalCustomCashOutSuccessPanel>(
			"cashOutSuccessForm", new CompoundPropertyModel<AgentPortalCustomCashOutSuccessPanel>(this));
		
		// Add FeedBack panel
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new Label("cashOutBean.accountNumber"));
		form.add(new Label("cashOutBean.payerMsisdn"));
		form.add(new Label("cashOutBean.displayName"));
		form.add(new AmountLabel("cashOutBean.cashOutAmount"));
		
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
