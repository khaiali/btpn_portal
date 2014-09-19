package com.sybase365.mobiliser.web.btpn.agent.common.panels;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashOutBean;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout.CashOutDetailsPopupPage;
import com.sybase365.mobiliser.web.btpn.agent.pages.portal.selfcare.BtpnBaseAgentPortalSelfCarePage;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the CashOutSuccessPanel page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class AgentPortalCashOutSuccessPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected BtpnMobiliserBasePage basePage;

	protected AgentCashOutBean cashOutBean;

	protected ModalWindow cashOutWindow;

	public AgentPortalCashOutSuccessPanel(String id, BtpnBaseAgentPortalSelfCarePage basePage,
		AgentCashOutBean cashOutBean) {
		super(id);
		this.basePage = basePage;
		this.cashOutBean = cashOutBean;
		constructPanel();
	}

	protected void constructPanel() {

		final Form<AgentPortalCashOutSuccessPanel> form = new Form<AgentPortalCashOutSuccessPanel>(
			"cashOutSuccessForm", new CompoundPropertyModel<AgentPortalCashOutSuccessPanel>(this));
		// Add FeedBack panel
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new AmountLabel("cashOutBean.cashOutAmount"));
		form.add(new AmountLabel("cashOutBean.feeAmount"));
		form.add(new AmountLabel("cashOutBean.totalAmount"));
		form.add(new AmountLabel("cashOutBean.creditAmount"));
		form.add(new Label("cashOutBean.payeeMsisdn"));
		form.add(new AmountLabel("cashOutBean.debitAmount"));
		cashOutBean.setAccountBalance(basePage.getSvaBalance(cashOutBean.getPayeeMsisdn()));
		form.add(new AmountLabel("cashOutBean.accountBalance"));

		final PopupSettings popupSettings = new PopupSettings("popuppagemap").setHeight(450).setWidth(400).setLeft(400)
			.setTop(200);
		final Link<AgentCashOutBean> link = new Link<AgentCashOutBean>("printReceiptLink", Model.of(cashOutBean)) {
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(new CashOutDetailsPopupPage(cashOutBean));
			}
		};
		link.setPopupSettings(popupSettings);
		link.add(new SimpleAttributeModifier("class", "submit btnstyle"));
		link.add(new Label("printLinkName", getLocalizer().getString("label.print", this)).setRenderBodyOnly(true));
		form.add(link);

		add(form);
	}

}
