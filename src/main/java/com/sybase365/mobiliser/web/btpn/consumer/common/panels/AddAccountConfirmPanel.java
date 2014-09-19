package com.sybase365.mobiliser.web.btpn.consumer.common.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btpnwow.portal.common.util.MobiliserUtils;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateWalletEntryRequest;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.CreateWalletEntryResponse;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.SVA;
import com.sybase365.mobiliser.money.contract.v5_0.wallet.beans.WalletEntry;
import com.sybase365.mobiliser.money.services.api.ISystemEndpoint;
import com.sybase365.mobiliser.web.btpn.application.pages.BtpnMobiliserBasePage;
import com.sybase365.mobiliser.web.btpn.consumer.beans.SubAccountsBean;
import com.sybase365.mobiliser.web.btpn.consumer.pages.portal.fundtransfer.ManageSubAccountsPage;


public class AddAccountConfirmPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(AddAccountConfirmPanel.class);

	BtpnMobiliserBasePage basePage;

	SubAccountsBean subAccountBean;
	
	@SpringBean(name = "systemAuthSystemClient")
	private ISystemEndpoint systemEndpoint;

	public AddAccountConfirmPanel(String id, BtpnMobiliserBasePage basePage, SubAccountsBean subAccountBean) {
		super(id);
		this.basePage = basePage;
		this.subAccountBean = subAccountBean;
		constructPanel();
	}

	protected void constructPanel() {
		
		final Form<AddAccountConfirmPanel> form = new Form<AddAccountConfirmPanel>("addAccountConfirmForm",
			new CompoundPropertyModel<AddAccountConfirmPanel>(this));
		form.add(new FeedbackPanel("errorMessages"));

		form.add(new Label("subAccountBean.name"));
		form.add(new Label("subAccountBean.description"));

		form.add(new Button("submitButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				createaddSubAccountRequest(subAccountBean);
			};
		});

		add(form);
	}

	
	private void createaddSubAccountRequest(SubAccountsBean bean) {
		
		CreateWalletEntryResponse response = null;
		
		try {
			
			final CreateWalletEntryRequest request = basePage.getNewMobiliserRequest(CreateWalletEntryRequest.class);
			WalletEntry wallet = new WalletEntry();
			long customerId = basePage.getMobiliserWebSession().getBtpnLoggedInCustomer().getCustomerId();
			log.info(" ### (AddAccountConfirmPanel::createAddSubAccountRequest) CUST ID ### " +customerId);
			String alias = subAccountBean.getName();
			log.info(" ### (AddAccountConfirmPanel::createAddSubAccountRequest) ALIAS ### " +alias);
			wallet.setCustomerId(customerId);
			wallet.setAlias(alias);
			
			SVA sva = new SVA();
			sva.setCustomerId(customerId);
			sva.setActive(true);
			sva.setStatus(0);
			sva.setCurrency("IDR");
			sva.setMultiCurrency(false);
			sva.setType(1);
			sva.setCreditBalance(0L);
			sva.setCreditReserved(0L);
			sva.setDebitBalance(0L);
			sva.setDebitReserved(0L);
			sva.setMinBalance(0L);
			
			wallet.setSva(sva);
			
			String refNo = MobiliserUtils.getExternalReferenceNo(systemEndpoint);
			log.info(" ### (AddAccountConfirmPanel::createAddSubAccountRequest) REF NO ### " +refNo);
			
			request.setOrigin(refNo);
			request.setTraceNo(refNo);
			
			request.setWalletEntry(wallet);
			log.info(" ### (AddAccountConfirmPanel::createAddSubAccountRequest) BEFORE SERVICE ### ");
			response = basePage.getWalletClient().createWalletEntry(request);
			log.info(" ### (AddAccountConfirmPanel::createAddSubAccountRequest) RESPONSE ### " +response.getStatus().getCode());
			if (basePage.evaluateConsumerPortalMobiliserResponse(response)) {
				basePage.getWebSession().info(
					getLocalizer().getString("add.successMessage", AddAccountConfirmPanel.this));
				setResponsePage(new ManageSubAccountsPage());
			} else {
				error(response.getStatus().getValue());
			}
			
		} catch (Exception ex) {
			log.error("#An error occurred while calling addSubAccount service.", ex);
			error(getLocalizer().getString("error.exception", this));
		}
	}

}
