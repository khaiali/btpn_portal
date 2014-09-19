package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashout;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashOutBean;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the cashout Details popup and print page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class CashOutDetailsPopupPage extends BtpnMobiliserBasePage {

	AgentCashOutBean cashOutBean;

	public CashOutDetailsPopupPage(final AgentCashOutBean cashOutBean) {
		this.cashOutBean = cashOutBean;
		final Form<CashOutDetailsPopupPage> form = new Form<CashOutDetailsPopupPage>("cashOutReceiptForm",
			new CompoundPropertyModel<CashOutDetailsPopupPage>(this));
		form.add(new Label("cashOutBean.payeeMsisdn"));
		form.add(new AmountLabel("cashOutBean.cashOutAmount"));
		form.add(new AmountLabel("cashOutBean.feeAmount"));
		form.add(new AmountLabel("cashOutBean.totalAmount"));
		form.add(new AmountLabel("cashOutBean.accountBalance"));
		add(form);
	}

	@Override
	protected void initOwnPageComponents() {
	}

}
