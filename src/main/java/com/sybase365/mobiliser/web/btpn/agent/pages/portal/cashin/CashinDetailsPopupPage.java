package com.sybase365.mobiliser.web.btpn.agent.pages.portal.cashin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentCashinBean;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.common.components.AmountLabel;

/**
 * This is the cashin Details popup and print page for agent portals.
 * 
 * @author Narasa Reddy
 */
public class CashinDetailsPopupPage extends BtpnMobiliserBasePage {

	AgentCashinBean cashInBean;

	public CashinDetailsPopupPage(final AgentCashinBean cashInBean) {
		this.cashInBean = cashInBean;
		final Form<CashinDetailsPopupPage> form = new Form<CashinDetailsPopupPage>("cashinReceiptForm",
			new CompoundPropertyModel<CashinDetailsPopupPage>(this));
		form.add(new Label("cashInBean.payeeMsisdn"));
		form.add(new AmountLabel("cashInBean.cashinAmount"));
		form.add(new AmountLabel("cashInBean.feeAmount"));
		form.add(new AmountLabel("cashInBean.totalAmount"));
		form.add(new AmountLabel("cashInBean.accountBalance"));
		add(form);
	}

	@Override
	protected void initOwnPageComponents() {
	}

}
