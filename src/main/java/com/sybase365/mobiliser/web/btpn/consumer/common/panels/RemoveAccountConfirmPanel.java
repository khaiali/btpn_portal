package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.core.account.facade.contract.AccountIdentificationType;
import com.btpnwow.core.account.facade.contract.RemoveAccountRequest;
import com.btpnwow.core.account.facade.contract.RemoveAccountResponse;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.ManageSubAccountsPage;


public class RemoveAccountConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(RemoveAccountConfirmPanel.class);

	protected BtpnMobiliserBasePage basePage;

	SubAccountsBean subAccountBean;

	public RemoveAccountConfirmPanel(String id, BtpnMobiliserBasePage basePage, SubAccountsBean subAccountBean) {
		super(id);
		this.basePage = basePage;
		this.subAccountBean = subAccountBean;
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<RemoveAccountConfirmPanel> form = new Form<RemoveAccountConfirmPanel>("removeAccountConfirmForm",
			new CompoundPropertyModel<RemoveAccountConfirmPanel>(this));
		
		form.add(new FeedbackPanel("errorMessages"));
		form.add(new Label("subAccountBean.name"));
		form.add(new Label("subAccountBean.accountId"));
		form.add(new Label("subAccountBean.description"));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				removeSubAccount();
			};
		});

		add(form);
	}
	
	/**
	 * calling removeSubAccount service from support end point
	 */
	private void removeSubAccount() {
		
		RemoveAccountResponse removeSubAccountResponse = null;
		
		try {
			
			final RemoveAccountRequest request = basePage.getNewMobiliserRequest(RemoveAccountRequest.class);
			
			AccountIdentificationType obj = new AccountIdentificationType();
			obj.setType("WALLET");
			obj.setFlags(1);
			log.info(" ### (RemoveAccountConfirmPanel::removeSubAccount) ACCOUNT ID ### " +subAccountBean.getAccountId());
			obj.setValue(subAccountBean.getAccountId());
			
			request.setIdentification(obj);
			
			removeSubAccountResponse = basePage.getAccountClient().remove(request);
			if (basePage.evaluateConsumerPortalMobiliserResponse(removeSubAccountResponse)) {
				basePage.getWebSession().info(
					getLocalizer().getString("remove.successMessage", RemoveAccountConfirmPanel.this));
				setResponsePage(new ManageSubAccountsPage());
			} else {
				error(removeSubAccountResponse.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling removeSubAccount service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

}
