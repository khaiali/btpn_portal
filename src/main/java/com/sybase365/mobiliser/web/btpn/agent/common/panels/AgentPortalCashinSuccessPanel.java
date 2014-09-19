package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashinBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin.CashinDetailsPopupPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the CashinSuccessPanel page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashinSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected AgentCashinBean cashInBean;

	/**
	 * Constructor for the panel.
	 * 
	 * @param id id for the page.
	 * @param basePage base page for the panel
	 * @param cashInBean Cashin Bean.
	 */
	public AgentPortalCashinSuccessPanel(final String id, final BtpnBaseAgentPortalSelfCarePage basePage,
		final AgentCashinBean cashInBean) {
		super(id);
		this.basePage = basePage;
		this.cashInBean = cashInBean;
		constructPanel();
	}

	/**
	 * Constructs a panel.
	 */
	protected void constructPanel() {
		final Form<AgentPortalCashinSuccessPanel> form = new Form<AgentPortalCashinSuccessPanel>("cashInSuccessForm",
			new CompoundPropertyModel<AgentPortalCashinSuccessPanel>(this));
		// Add FeedBack panel
		final FeedbackPanel feedBack = new FeedbackPanel("errorMessages");
		form.add(feedBack);
		form.add(new AmountLabel("cashInBean.cashinAmount"));
		form.add(new AmountLabel("cashInBean.feeAmount"));
		form.add(new AmountLabel("cashInBean.totalAmount"));
		form.add(new AmountLabel("cashInBean.creditAmount"));
		form.add(new Label("cashInBean.payeeMsisdn"));
		form.add(new AmountLabel("cashInBean.debitAmount"));
		cashInBean.setAccountBalance(basePage.getSvaBalance(cashInBean.getPayeeMsisdn()));
		form.add(new AmountLabel("cashInBean.accountBalance"));

		final PopupSettings popupSettings = new PopupSettings("popuppagemap").setHeight(450).setWidth(400).setLeft(400)
			.setTop(200);
		final Link<AgentCashinBean> link = new Link<AgentCashinBean>("printReceiptLink", Model.of(cashInBean)) {
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(new CashinDetailsPopupPage(cashInBean));
			}
		};
		link.setPopupSettings(popupSettings);
		link.add(new SimpleAttributeModifier("class", "submit btnstyle"));
		link.add(new Label("printLinkName", getLocalizer().getString("label.print", this)).setRenderBodyOnly(true));
		form.add(link);
		add(form);
	}

}
